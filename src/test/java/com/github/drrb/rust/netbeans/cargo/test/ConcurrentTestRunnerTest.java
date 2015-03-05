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

package com.github.drrb.rust.netbeans.cargo.test;

import com.github.drrb.rust.netbeans.cargo.Cargo;
import com.github.drrb.rust.netbeans.project.RustProject;
import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.netbeans.api.project.Project;
import static org.netbeans.modules.gsf.testrunner.api.Status.FAILED;
import static org.netbeans.modules.gsf.testrunner.api.Status.PASSED;
import org.netbeans.modules.gsf.testrunner.api.TestSuite;
import org.netbeans.modules.gsf.testrunner.api.Testcase;
import org.openide.filesystems.FileObject;

public class ConcurrentTestRunnerTest {

    @Rule
    public final NetbeansWithRust netbeans = new NetbeansWithRust();
    private RustProject project;
    private FakeTestUiSession session;
    private TestRunner.Watcher watcher;
    private FakeCargo cargo;
    private TestRunner testRunner;
    private FakeCargoCommandFuture testsProgress;

    @Before
    public void setUp() throws Exception {
        project = netbeans.getTestProject("project/simple");
        testsProgress = new FakeCargoCommandFuture();
        cargo = new FakeCargo(testsProgress);
        session = new FakeTestUiSession();
        watcher = new TestRunner.Watcher(session);
        testRunner = new TestRunner(project, cargo, new TestRunner.WatcherFactory() {
            @Override
            public TestRunner.Watcher createWatcher(Project project) {
                return watcher;
            }
        });
    }

    @Test
    public void shouldRunTestCommandWhenTestingProject() {
        testRunner.run();
        assertThat(cargo.getCommandsRun(), contains("test"));
    }

    @Test
    public void shouldSpecifyModuleAsFilterWhenTestingFile() {
        FileObject sourceFile = project.getProjectDirectory().getFileObject("src/dirmod/mod.rs");

        testRunner.run(sourceFile);

        assertThat(cargo.getCommandsRun(), contains("test dirmod::"));
    }

    @Test
    public void shouldStartSessionWhenTestsStartRunning() {
        testRunner.run();
        testsProgress.testsStarted();
        assertTrue(session.isStarted());
    }

    @Test
    public void shouldFinishSessionWhenTestsFinishRunning() {
        testRunner.run();
        testsProgress.testsFinished();
        assertTrue(session.isFinished());
    }

    @Test
    public void shouldRecordTestsAtTheEnd() {
        testRunner.run();
        testsProgress.printLine("");
        watcher.onTestCompleted(new TestResult("mymodule", "my_passing_test", PASSED));
        watcher.onTestCompleted(new TestResult("myothermodule", "myothertest", PASSED));
        watcher.onTestCompleted(new TestResult("mymodule", "my_failing_test", FAILED));
        assertThat(session.getSuites(), is(empty()));

        watcher.onFinish();
        assertThat(session.getSuites(), hasSize(2));
        TestSuite suite = session.getSuites().get(0);
        assertThat(suite.getName(), is("mymodule"));
        assertThat(suite.getTestcases(), hasSize(2));
        Testcase test = suite.getTestcases().get(0);
        assertThat(test.getName(), is("my_passing_test"));
        assertThat(test.getStatus(), is (PASSED));
    }
}
