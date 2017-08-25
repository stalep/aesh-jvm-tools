/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aesh.jvm.utils;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.aesh.jvm.JVM;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.management.ConnectorAddressLink;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class JVMProcesses {

    private static final boolean canAttachToLocalJVMs;

    static {
        boolean supported;
        try {
            Class.forName("com.sun.tools.attach.VirtualMachine");
            Class.forName("sun.management.ConnectorAddressLink");
            supported = true;
        } catch (NoClassDefFoundError x) {
            supported = false;
        } catch (ClassNotFoundException x) {
            supported = false;
        }
        canAttachToLocalJVMs = supported;
    }

    public static boolean canAttachToLocalJVMs() {
        return canAttachToLocalJVMs;
    }

    // This method returns the list of all virtual machines currently
    // running on the machine
    public static Map<Integer, JVM> getAllVirtualMachines() {
        Map<Integer, JVM> map =
                new HashMap<>();
        getMonitoredVMs(map);
        getAttachableVMs(map);
        return map;
    }

    private static void getMonitoredVMs(Map<Integer, JVM> map) {
        MonitoredHost host;
        Set<Integer> vms;
        try {
            host = MonitoredHost.getMonitoredHost(new HostIdentifier((String)null));
            vms = host.activeVms();
        } catch (java.net.URISyntaxException | MonitorException x) {
            throw new InternalError(x.getMessage(), x);
        }
        for (Object vmid: vms) {
            if (vmid instanceof Integer) {
                int pid = (Integer) vmid;
                String name = vmid.toString(); // default to pid if name not available
                boolean attachable = false;
                String address = null;
                try {
                    MonitoredVm mvm = host.getMonitoredVm(new VmIdentifier(name));
                    // use the command line as the display name
                    name =  MonitoredVmUtil.commandLine(mvm);
                    attachable = MonitoredVmUtil.isAttachable(mvm);
                    address = ConnectorAddressLink.importFrom(pid);
                    mvm.detach();
                } catch (Exception x) {
                    // ignore
                }
                map.put((Integer) vmid,
                        new JVM(pid, name, attachable, address));
            }
        }
    }

    private static final String LOCAL_CONNECTOR_ADDRESS_PROP =
            "com.sun.management.jmxremote.localConnectorAddress";

    private static void getAttachableVMs(Map<Integer, JVM> map) {
        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        for (VirtualMachineDescriptor vmd : vms) {
            try {
                Integer vmId = Integer.valueOf(vmd.id());
                if (!map.containsKey(vmId)) {
                    boolean attachable = false;
                    String address = null;
                    try {
                        VirtualMachine vm = VirtualMachine.attach(vmd);
                        attachable = true;
                        Properties agentProps = vm.getAgentProperties();
                        address = (String) agentProps.get(LOCAL_CONNECTOR_ADDRESS_PROP);
                        vm.detach();
                    } catch (AttachNotSupportedException | IOException x) {
                        // not attachable
                    }
                    map.put(vmId, new JVM(vmId, vmd.displayName(), attachable, address));
                }
            } catch (NumberFormatException e) {
                // do not support vmId different than pid
            }
        }
    }
}
