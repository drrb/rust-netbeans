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
import com.github.drrb.rust.netbeans.commandrunner.Shell;
import com.github.drrb.rust.netbeans.configuration.Os;
import com.github.drrb.rust.netbeans.configuration.RustConfiguration;
import com.github.drrb.rust.netbeans.project.RustProject;
import com.github.drrb.rust.netbeans.util.Template;
import static com.github.drrb.rust.netbeans.util.Template.template;
import static com.google.common.collect.Lists.transform;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Cargo {

    private final RustProject project;
    private final Shell shell;
    private final CommandRunner commandRunner;
    private final RustConfiguration configuration;

    public Cargo(RustProject project) {
        this(project, Os.getCurrent().shell(), new CommandRunner("Cargo"), RustConfiguration.get());
    }

    protected Cargo(RustProject project, Shell shell, CommandRunner commandRunner, RustConfiguration configuration) {
        this.project = project;
        this.shell = shell;
        this.commandRunner = commandRunner;
        this.configuration = configuration;
    }

    public CommandFuture run(String... commands) {
        return run(new CommandSet(commands));
    }

    public CommandFuture run(CommandSet commandSet) {
        Template commandTemplate = template("{cargo} {command} --verbose");
        commandTemplate.interpolate("cargo", configuration.getCargoPath());
        List<String> commandLines = transform(commandSet.commands, commandTemplate.interpolateFromInput("command"));
        ProcessBuilder process = shell.createProcess(commandLines).directory(project.dir());
        process.environment().putAll(commandSet.envVars);
        return commandRunner.run(process);
    }

    public static class CommandSet {
        public static CommandSet commands(String... commands) {
            return new CommandSet(commands);
        }

        final List<String> commands;
        final Map<String, String> envVars = new HashMap<>();

        public CommandSet(String... commands) {
            this.commands = asList(commands);
        }

        public CommandSet withEnvVar(String key, String value) {
            envVars.put(key, value);
            return this;
        }
    }
}
