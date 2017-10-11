/**
 * Copyright (C) 2017 drrb
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.drrb.rust.netbeans.commandrunner;

public class CommandRunner {

    public static CommandRunner get(String name) {
        return new CommandRunner(name);
    }

    private final String name;
    private final CommandRunnerUi.Factory uiFactory;

    public CommandRunner(String name) {
        this(name, new CommandRunnerUi.Factory());
    }

    CommandRunner(String name, CommandRunnerUi.Factory uiFactory) {
        this.name = name;
        this.uiFactory = uiFactory;
    }

    public CommandFuture run(ProcessBuilder processBuilder) {
        CommandRunnerUi ui = uiFactory.get(name);
        ui.printText(printableCommand(processBuilder));
        return ui.runAndWatch(processBuilder);
    }

    private String printableCommand(ProcessBuilder processBuilder) {
        StringBuilder commandString = new StringBuilder();
        for (String commandPart : processBuilder.command()) {
            if (commandPart.contains(" ")) {
                commandString.append("'").append(commandPart).append("'");
            } else {
                commandString.append(commandPart);
            }
            commandString.append(" ");
        }
        return commandString.toString().replaceAll(" $", "");
    }
}
