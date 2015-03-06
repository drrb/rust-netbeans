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

import com.github.drrb.rust.netbeans.cargo.test.TestResult;
import com.github.drrb.rust.netbeans.commandrunner.CommandFuture;
import com.github.drrb.rust.netbeans.commandrunner.CommandRunner;
import com.github.drrb.rust.netbeans.commandrunner.HumbleCommandFuture;
import com.github.drrb.rust.netbeans.commandrunner.Shell;
import com.github.drrb.rust.netbeans.configuration.RustConfiguration;
import com.github.drrb.rust.netbeans.project.RustProject;
import com.github.drrb.rust.netbeans.test.TemporaryPreferences;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.netbeans.modules.gsf.testrunner.api.Status;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static com.github.drrb.rust.netbeans.cargo.Cargo.*;
import static com.github.drrb.rust.netbeans.test.Matchers.isProcess;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class CargoTest {

    @Rule
    public final TemporaryPreferences temporaryPreferences = new TemporaryPreferences();
    private final File projectDir = new File("/tmp/myproject");
    private Cargo cargo;
    private CommandRunner commandRunner;
    private HumbleCommandFuture commandFuture;

    @Before
    public void setUp() {
        RustProject project = mock(RustProject.class);
        when(project.dir()).thenReturn(projectDir);
        commandRunner = mock(CommandRunner.class);
        commandFuture = new HumbleCommandFuture();
        RustConfiguration config = new RustConfiguration(temporaryPreferences.get());
        cargo = new Cargo(project, Shell.BASH, commandRunner, config);
        config.setCargoPath("/path/to/cargo");
    }

    @Test
    public void shouldRunCargoCommandsInShell() {
        when(commandRunner.run(argThat(isProcess("/bin/bash", "-lc", "/path/to/cargo clean --verbose && /path/to/cargo build --verbose").inDir(projectDir)))).thenReturn(commandFuture);
        CommandFuture result = cargo.run(CLEAN, BUILD);
        assertEquals(commandFuture, result);
    }

    @Test
    public void shouldRunCargoCommandsWithEnvVariables() {
        ProcessBuilder expectedCommand = argThat(isProcess("/bin/bash", "-lc", "/path/to/cargo clean --verbose && /path/to/cargo test --verbose")
                .inDir(projectDir)
                .withEnvVar("RUST_TEST_TASKS", "1"));
        when(commandRunner.run(expectedCommand)).thenReturn(commandFuture);
        CommandFuture result = cargo.run(CLEAN, TEST_SEQUENTIAL);
        assertEquals(commandFuture, result);
    }

    @Test
    public void shouldNotifyOfTestResults() {
        final List<TestResult> tests = new LinkedList<>();
        when(commandRunner.run(argThat(isProcess("/bin/bash", "-lc", "/path/to/cargo test --verbose").inDir(projectDir)))).thenReturn(commandFuture);
        CommandFuture result = cargo.run(TEST_PARALLEL);
        result.addListener(new CargoListener() {
            @Override
            protected void onTestCompleted(TestResult test) {
                tests.add(test);
            }
        });
        commandFuture.printLine("Starting to run tests");
        commandFuture.printLine("test myapp::mymodule::mytest ... ok");
        commandFuture.printLine("test myapp::myothermodule::myfailingtest ... FAILED");
        commandFuture.processEvents();
        assertThat(tests, contains(new TestResult("myapp::mymodule", "mytest", Status.PASSED), new TestResult("myapp::myothermodule", "myfailingtest", Status.FAILED)));
    }
}
