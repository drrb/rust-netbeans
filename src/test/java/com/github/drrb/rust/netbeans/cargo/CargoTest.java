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
import com.github.drrb.rust.netbeans.test.TemporaryPreferences;
import java.io.File;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Matchers;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
public class CargoTest {

    @Rule
    public final TemporaryPreferences temporaryPreferences = new TemporaryPreferences();
    private final File file = new File("/tmp/myproject");
    private Cargo cargo;
    private CommandRunner shell;
    private RustProject project;
    private RustConfiguration config;
    private CommandFuture commandFuture;

    @Before
    public void setUp() {
        project = mock(RustProject.class);
        shell = mock(CommandRunner.class);
        commandFuture = mock(CommandFuture.class);
        config = new RustConfiguration(temporaryPreferences.get());
        cargo = new Cargo(project, shell, config);
        config.setCargoPath("/path/to/cargo");
        when(project.dir()).thenReturn(file);
        when(shell.run(any(String.class), any(File.class))).thenReturn(commandFuture);
    }

    @Test
    public void shouldRunCargoCommandsInShell() {
        when(shell.run("/path/to/cargo clean --verbose && /path/to/cargo build --verbose", file)).thenReturn(commandFuture);
        CommandFuture result = cargo.run("clean", "build");
        assertThat(result, is(commandFuture));
    }
}
