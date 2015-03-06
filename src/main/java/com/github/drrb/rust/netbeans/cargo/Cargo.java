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
import com.google.common.base.Joiner;
import org.apache.commons.lang3.builder.EqualsBuilder;

import static com.github.drrb.rust.netbeans.cargo.Cargo.Command.command;
import static com.github.drrb.rust.netbeans.util.Template.template;
import static com.google.common.collect.Lists.transform;
import static java.util.Arrays.asList;
import static java.util.Arrays.spliterator;

import java.util.*;

/**
 *
 */
public class Cargo {
    public static final Command BUILD = command("build");
    public static final Command CLEAN = command("clean");
    public static final Command RUN = command("run");
    public static final Command TEST_PARALLEL = command("test");
    public static final Command TEST_SEQUENTIAL = command("test").withEnvVar("RUST_TEST_TASKS", "1");
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

    public CommandFuture run(Command... commands) {
        return run(asList(commands));
    }

    public CommandFuture run(List<Command> commands) {
        Template commandTemplate = template("{cargo} {command} --verbose");
        commandTemplate.interpolate("cargo", configuration.getCargoPath());
        List<String> commandLines = transform(commands, commandTemplate.interpolateFromInput("command"));
        ProcessBuilder process = shell.createProcess(commandLines).directory(project.dir());
        process.environment().putAll(getEnvVars(commands));
        return commandRunner.run(process);
    }

    private Map<String, String> getEnvVars(List<Command> commands) {
        Map<String, String> allEnvVars = new HashMap<>(1);
        for (Command command : commands) {
            allEnvVars.putAll(command.envVars);
        }
        return allEnvVars;
    }

    public static class Command {
        public static Command command(String command) {
            return new Command(command);
        }

        private final List<String> parts;
        private final Map<String, String> envVars;

        private Command(String command) {
            this(asList(command), Collections.<String, String>emptyMap());
        }

        private Command(List<String> parts, Map<String, String> envVars) {
            this.parts = new ArrayList<>(parts);
            this.envVars = new HashMap<>(envVars);
        }

        public Command withEnvVar(String key, String value) {
            HashMap<String, String> newEnvVars = new HashMap<>(envVars);
            newEnvVars.put(key, value);
            return new Command(parts, newEnvVars);
        }

        public Command withArg(String arg) {
            return withArgs(arg);
        }

        public Command withArgs(String... args) {
            List<String> newParts = new ArrayList<>(parts);
            newParts.addAll(asList(args));
            return new Command(newParts, envVars);
        }

        @Override
        public String toString() {
            return Joiner.on(" ").join(parts);
        }

        @Override
        public boolean equals(Object o) {
            return EqualsBuilder.reflectionEquals(this, o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(parts, envVars);
        }
    }
}
