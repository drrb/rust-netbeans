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

import java.io.File;
import static java.util.Arrays.asList;
import java.util.List;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CommandRunnerTest {
    private CommandRunnerUi ui;
    private CommandRunner commandRunner;

    @Before
    public void setUp() {
        ui = mock(CommandRunnerUi.class);
        commandRunner = new CommandRunner("My command runner", new CannedUiFactory(ui), Shell.BASH);
    }

    @Test
    public void shouldRunCommand() {
        commandRunner.run("cargo test", new File("/tmp"));
        verify(ui).printText("/bin/bash -lc 'cargo test'");
        verify(ui).runAndWatch(argThat(isProcessInDir(new File("/tmp"), "/bin/bash", "-lc", "cargo test")));
    }

    @Test
    public void shouldRunCommandWithBuilderApi() {
        commandRunner.run("cargo test").inDir(new File("/tmp")).withEnvVar("RUST_TEST_TASKS", "1").start();
        verify(ui).printText("/bin/bash -lc 'cargo test'");
        verify(ui).runAndWatch(argThat(isProcessInDir(new File("/tmp"), "/bin/bash", "-lc", "cargo test")));
    }

    private ArgumentMatcher<ProcessBuilder> isProcessInDir(File workingDir, String... commandParts) {
        return new ProcessBuilderMatcher(workingDir, commandParts);
    }

    static class ProcessBuilderMatcher extends ArgumentMatcher<ProcessBuilder> {
        private final File workingDir;
        private final List<String> commandParts;

        private ProcessBuilderMatcher(File workingDir, String... commandParts) {
            this.workingDir = workingDir;
            this.commandParts = asList(commandParts);
        }

        @Override
        public boolean matches(Object argument) {
            ProcessBuilder pb = (ProcessBuilder) argument;
            if (!pb.command().equals(commandParts)) {
                System.out.println("Commands differ");
                return false;
            }
            if (!pb.directory().equals(workingDir)) {
                System.out.println("working dirs differ");
                return false;
            }
            return true;
        }
    }

    static class CannedUiFactory extends CommandRunnerUi.Factory {
        private final CommandRunnerUi ui;

        private CannedUiFactory(CommandRunnerUi ui) {
            this.ui = ui;
        }

        @Override
        public CommandRunnerUi get(String name) {
            return ui;
        }
    }

}