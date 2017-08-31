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
package org.aesh.jvm.commands;

import org.aesh.command.Command;
import org.aesh.command.CommandDefinition;
import org.aesh.command.CommandException;
import org.aesh.command.CommandResult;
import org.aesh.command.invocation.CommandInvocation;
import org.aesh.command.option.Argument;
import org.aesh.command.option.Option;
import org.aesh.jvm.completer.JavaPidNameCompleter;
import org.aesh.jvm.utils.JVMProcesses;

import java.util.ArrayList;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
@CommandDefinition(name = "jstack", description = "Prints Java thread stack traces for a Java process or core file.")
public class JStack implements Command {

    @Option(name = "force", shortName = 'F', hasValue = false)
    private boolean force;

    @Option(name = "list", shortName = 'l', hasValue = false)
    private boolean list;

    @Option(name = "mixed", shortName = 'm', hasValue = false)
    private boolean mixed;

    @Argument(required = true, completer = JavaPidNameCompleter.class)
    private String pid;

    @Override
    public CommandResult execute(CommandInvocation commandInvocation) throws CommandException, InterruptedException {
        if(JVMProcesses.canAttachToLocalJVMs()) {

            try {
                JVMProcesses.dumpThread(pid, args());
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return CommandResult.SUCCESS;
        }

        else {
            commandInvocation.println("JStack cannot list any running JVMs are you running with OpenJDK/OracleJDK "+
                    "and have tools.jar in your classpath?");
            return CommandResult.FAILURE;
        }
    }



    private String[] args() {
        ArrayList<String> options = new ArrayList<>();
        if (list)
            options.add("-l");
        if (mixed)
            options.add("-m");
        if (force)
            options.add("-F");

        return options.toArray(new String[options.size()]);
    }

}
