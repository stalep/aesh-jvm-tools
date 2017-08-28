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
package org.aesh.jvm;

import java.io.File;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class JVM {

    private final String args;
    private final String flags;
    private String name;
    private String address;
    private String commandLine;
    private int vmId;
    private boolean attachable;

    public JVM(int vmId, String commandLine, String name, boolean attachable, String address,
               String flags, String args) {
        this.commandLine = commandLine;
        this.name = name;
        this.address = address;
        this.vmId = vmId;
        this.attachable = attachable;
        this.flags = flags;
        this.args = args;
    }

    public String name() {
        return name;
    }

    public String commandLine() {
        return commandLine;
    }

    public String address() {
        return address;
    }

    public int vmId() {
        return vmId;
    }

    public boolean isAttachable() {
        return attachable;
    }

    public String args() {
        return args;
    }

    public String flags() {
        return flags;
    }

    @Override
    public String toString() {
        return "JVM{" +
                "args='" + args + '\'' +
                ", flags='" + flags + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", commandLine='" + commandLine + '\'' +
                ", vmId=" + vmId +
                ", attachable=" + attachable +
                '}';
    }
}
