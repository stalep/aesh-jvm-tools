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

import org.aesh.command.Command;
import org.aesh.command.CommandDefinition;
import org.aesh.command.CommandException;
import org.aesh.command.CommandResult;
import org.aesh.command.impl.registry.AeshCommandRegistryBuilder;
import org.aesh.command.invocation.CommandInvocation;
import org.aesh.command.parser.CommandLineParserException;
import org.aesh.command.registry.CommandRegistry;
import org.aesh.console.settings.Settings;
import org.aesh.console.settings.SettingsBuilder;
import org.aesh.jvm.commands.JStack;
import org.aesh.jvm.commands.Jps;
import org.aesh.readline.Prompt;
import org.aesh.readline.ReadlineConsole;

import java.io.IOException;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class JvmCli {

    public static void main(String[] args) throws IOException, CommandLineParserException {

        CommandRegistry registry = new AeshCommandRegistryBuilder()
                .command(Exit.class)
                .command(Jps.class)
                .command(JStack.class)
                .create();

        Settings settings = SettingsBuilder.builder()
                .readInputrc(false)
                .logging(true)
                .commandRegistry(registry)
                .build();


        ReadlineConsole console = new ReadlineConsole(settings);
        console.setPrompt(new Prompt("[aesh@jvm]$ "));

        console.start();
    }



    @CommandDefinition(name = "exit", description = "")
    public static class Exit implements Command {

        @Override
        public CommandResult execute(CommandInvocation commandInvocation) throws CommandException, InterruptedException {
            commandInvocation.stop();
            return CommandResult.SUCCESS;
        }
    }



}
