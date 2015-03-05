/*
 * Copyright (C) 2015 drrb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.drrb.rust.netbeans.commandrunner;

import com.github.drrb.rust.netbeans.configuration.Os;
import java.io.File;
import java.util.Deque;
import java.util.LinkedList;

public class CommandRunner {

    public static CommandRunner get(String name) {
        return new CommandRunner(name);
    }

    private final String name;

    private final CommandRunnerUi.Factory uiFactory;
    private final Shell shell;

    public CommandRunner(String name) {
        this(name, new CommandRunnerUi.Factory(), Os.getCurrent().shell());
    }

    CommandRunner(String name, CommandRunnerUi.Factory uiFactory, Shell shell) {
        this.name = name;
        this.uiFactory = uiFactory;
        this.shell = shell;
    }

    public InvocationBuilder run(String commandLine) {
        return new InvocationBuilder(shell.createProcess(commandLine));
    }

    public CommandFuture run(String commandLine, File workingDir) {
        return run(commandLine).inDir(workingDir).start();
    }

    public CommandFuture run(ProcessBuilder processBuilder) {
        CommandRunnerUi ui = uiFactory.get(name);
        ui.printText(printableCommand(processBuilder));
        return ui.runAndWatch(processBuilder);
    }

    private String printableCommand(ProcessBuilder processBuilder) {
        StringBuilder commandString = new StringBuilder();
        Deque<String> commandParts = new LinkedList<>(processBuilder.command());
        commandString.append(commandParts.pop()).append(" ");
        commandString.append(commandParts.pop()).append(" ");
        commandString.append("'").append(commandParts.pop()).append("'");
        return commandString.toString();
    }

    public class InvocationBuilder {

        private final ProcessBuilder processBuilder;

        private InvocationBuilder(ProcessBuilder processBuilder) {
            this.processBuilder = processBuilder;
        }

        public InvocationBuilder inDir(File workingDir) {
            processBuilder.directory(workingDir);
            return this;
        }

        public InvocationBuilder withEnvVar(String key, String value) {
            processBuilder.environment().put(key, value);
            return this;
        }

        public CommandFuture start() {
            return run(processBuilder);
        }
    }

}
