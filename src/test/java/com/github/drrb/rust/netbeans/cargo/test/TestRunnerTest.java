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
import com.github.drrb.rust.netbeans.commandrunner.HumbleCommandFuture;
import com.github.drrb.rust.netbeans.project.RustProject;
import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Matchers;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.netbeans.modules.gsf.testrunner.api.Status;
import static org.netbeans.modules.gsf.testrunner.api.Status.FAILED;
import static org.netbeans.modules.gsf.testrunner.api.Status.PASSED;
import org.netbeans.modules.gsf.testrunner.api.TestSuite;
import org.netbeans.modules.gsf.testrunner.api.Testcase;

public class TestRunnerTest {

    @Rule
    public final NetbeansWithRust netbeans = new NetbeansWithRust();
    private RustProject project;
    private FakeTestUiSession session;
    private TestRunner.Watcher watcher;
    private Cargo cargo;
    private TestRunner testRunner;
    private TestRunFuture testsProgress;

    @Before
    public void setUp() throws Exception {
        project = netbeans.getTestProject("project/simple");
        cargo = mock(Cargo.class);
        testsProgress = new TestRunFuture();
        when(cargo.run(Matchers.<String[]>anyVararg())).thenReturn(testsProgress);
        session = new FakeTestUiSession();
        watcher = new TestRunner.Watcher(session);
        testRunner = new TestRunner(project, cargo, null) {
            @Override
            protected TestRunner.Watcher createTestWatcher() {
                return watcher;
            }
        };
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

    private static class TestRunFuture extends HumbleCommandFuture {
        void testsStarted() {
            start();
            processEvents();
        }

        void testsFinished() {
            finish();
            processEvents();
        }

        void testFinished(String module, String name, Status status) {
            String statusString = status == PASSED ? "ok" : "FAILED";
            printLine(String.format("test %s::%s ... %s", module, name, statusString));
            processEvents();
        }
    }
}
