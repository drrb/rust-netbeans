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
import com.github.drrb.rust.netbeans.project.RustProject;
import com.github.drrb.rust.netbeans.util.Untested;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.gsf.testrunner.api.Manager;
import org.netbeans.modules.gsf.testrunner.api.Report;
import org.netbeans.modules.gsf.testrunner.api.TestSession;
import org.netbeans.modules.gsf.testrunner.api.TestSuite;
import org.netbeans.modules.gsf.testrunner.api.Testcase;
import org.openide.filesystems.FileObject;

@Untested(excuses = "API classes are final")
public class TestRunner {
    public static class Factory {
        public TestRunner create(RustProject project, Cargo cargo) {
            return new TestRunner(project, cargo, Manager.getInstance());
        }
    }

    private static final Logger LOG = Logger.getLogger(TestRunner.class.getName());
    private final RustProject project;
    private final Cargo cargo;
    private final Manager testManager;

    public TestRunner(RustProject project, Cargo cargo, Manager testManager) {
        this.project = project;
        this.cargo = cargo;
        this.testManager = testManager;
    }

    //TODO: for both of these, run 'RUST_TEST_TASKS=1 cargo test --jobs 1 -- --nocapture'
    // (we'll have to work out how to do that on Windows)
    public void run() {
        LOG.info("Running all tests");
        watchCargoCommand("test");
    }

    public void run(FileObject file) {
        LOG.log(Level.INFO, "Running tests for file {0}", file);
        String moduleName = project.getCargoConfig().getModuleName(file);
        watchCargoCommand("test " + moduleName + "::"); // Cargo matches this against all tests in this module
    }

    private void watchCargoCommand(String command) {
        CommandFuture commandFuture = cargo.run(command);
        commandFuture.addListener(new TestWatcher(project, testManager));
    }

    private static class TestWatcher extends CargoListener {

        private final Map<String, SuiteInProgress> suites = new TreeMap<>();
        private final Manager testManager;
        private final TestSession testSession;

        TestWatcher(Project project, Manager testManager) {
            this.testManager = testManager;
            this.testSession = new TestSession("Cargo Tests", project, TestSession.SessionType.TEST);
        }

        @Override
        public synchronized void onStart() {
            LOG.info("Starting test run");
            testManager.setTestingFramework("CARGO");
            testManager.testStarted(testSession);
        }

        @Override
        protected void onTestCompleted(TestResult test) {
            SuiteInProgress testSuite = getSuite(test.getModuleName());
            Testcase testCase = new Testcase(test.getTestName(), null, testSession);
            testCase.setStatus(test.getStatus());
            testSuite.tests.add(testCase);
        }

        @Override
        public synchronized void onFinish() {
            LOG.info("Finished test run");
            for (SuiteInProgress suite : suites.values()) {
                testSession.addSuite(suite.suite);
                testManager.displaySuiteRunning(testSession, suite.suite);
                for (Testcase testCase : suite.tests) {
                    testSession.addTestCase(testCase);
                }
                Report report = testSession.getReport(0);
                testManager.displayReport(testSession, report, true); // `true` means don't display "running..." next to the suite
            }
            testManager.sessionFinished(testSession);
        }

        private SuiteInProgress getSuite(String suiteName) {
            if (!suites.containsKey(suiteName)) {
                SuiteInProgress testSuite = new SuiteInProgress(suiteName);
                suites.put(suiteName, testSuite);
            }
            return suites.get(suiteName);
        }

        private static class SuiteInProgress {
            final TestSuite suite;
            final List<Testcase> tests = new LinkedList<>();

            SuiteInProgress(String name) {
                this.suite = new TestSuite(name);
            }
        }
    }
}
