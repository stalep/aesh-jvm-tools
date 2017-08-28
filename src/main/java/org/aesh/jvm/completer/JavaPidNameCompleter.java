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
package org.aesh.jvm.completer;

import org.aesh.command.completer.CompleterInvocation;
import org.aesh.command.completer.OptionCompleter;
import org.aesh.jvm.utils.JVMProcesses;

import java.util.Map;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class JavaPidNameCompleter implements OptionCompleter {

    @Override
    public void complete(CompleterInvocation completerInvocation) {
        Map<Integer, String> jvms = JVMProcesses.getMonitoredVMIdsAndNames();
        if(completerInvocation.getGivenCompleteValue() == null ||
                completerInvocation.getGivenCompleteValue().length() == 0)
            for(Integer id : jvms.keySet())
                completerInvocation.addCompleterValue(id+"("+jvms.get(id)+")");
        else {
            for(Integer id : jvms.keySet())
                if( String.valueOf(id).startsWith(completerInvocation.getGivenCompleteValue()))
                    completerInvocation.addCompleterValue(String.valueOf(id));
        }
    }

}
