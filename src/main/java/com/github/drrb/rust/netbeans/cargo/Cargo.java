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
package com.github.drrb.rust.netbeans.cargo;

import com.github.drrb.rust.netbeans.commandrunner.CommandFuture;
import com.github.drrb.rust.netbeans.commandrunner.CommandRunner;
import com.github.drrb.rust.netbeans.configuration.RustConfiguration;
import com.github.drrb.rust.netbeans.project.RustProject;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Cargo {

    private final CommandRunner commandRunner;
    private final RustProject project;
    private final RustConfiguration configuration;

    public Cargo(RustProject project) {
        this(project, new CommandRunner("Cargo"), RustConfiguration.get());
    }

    protected Cargo(RustProject project, CommandRunner commandRunner, RustConfiguration configuration) {
        this.project = project;
        this.commandRunner = commandRunner;
        this.configuration = configuration;
    }

    public CommandFuture run(String... commands) {
        List<String> cargoCommands = new ArrayList<>(commands.length);
        for (String command : commands) {
            cargoCommands.add(String.format("%s %s --verbose", configuration.getCargoPath(), command));
        }
        String commandLine = Joiner.on(" && ").join(cargoCommands);
        return commandRunner.run(commandLine, project.dir());
    }
}
