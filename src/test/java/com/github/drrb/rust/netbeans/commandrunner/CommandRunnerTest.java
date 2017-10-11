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

import java.io.File;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CommandRunnerTest {
    private CommandRunnerUi ui;
    private CommandRunner commandRunner;

    @Before
    public void setUp() {
        ui = mock(CommandRunnerUi.class);
        commandRunner = new CommandRunner("My command runner", new CannedUiFactory(ui));
    }

    @Test
    public void shouldRunCommand() {
        ProcessBuilder process = new ProcessBuilder("/path/to/cargo", "do something").directory(new File("/tmp"));
        commandRunner.run(process);
        verify(ui).printText("/path/to/cargo 'do something'");
        verify(ui).runAndWatch(process);
    }

    private static class CannedUiFactory extends CommandRunnerUi.Factory {
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