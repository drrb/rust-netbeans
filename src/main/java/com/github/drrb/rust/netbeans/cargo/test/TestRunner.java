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
import com.github.drrb.rust.netbeans.cargo.CargoListener;
import com.github.drrb.rust.netbeans.commandrunner.CommandFuture;
import com.github.drrb.rust.netbeans.project.RustProject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.gsf.testrunner.api.Manager;
import org.netbeans.modules.gsf.testrunner.api.TestSuite;
import org.netbeans.modules.gsf.testrunner.api.Testcase;
import org.openide.filesystems.FileObject;

public class TestRunner {
    public static class Factory {
        public TestRunner create(RustProject project, Cargo cargo) {
            return new TestRunner(project, cargo, new WatcherFactory());
        }
    }

    static class WatcherFactory {
        public Watcher createWatcher(Project project) {
            TestUiSession session = new TestUiSession(project, Manager.getInstance());
            return new Watcher(session);
        }
    }

    private static final Logger LOG = Logger.getLogger(TestRunner.class.getName());
    private final RustProject project;
    private final Cargo cargo;
    private final WatcherFactory watcherFactory;

    public TestRunner(RustProject project, Cargo cargo, WatcherFactory watcherFactory) {
        this.project = project;
        this.cargo = cargo;
        this.watcherFactory = watcherFactory;
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
        Watcher watcher = watcherFactory.createWatcher(project);
        CommandFuture commandFuture = cargo.run(command);
        commandFuture.addListener(watcher);
    }

    static class Watcher extends CargoListener {

        private final Map<String, SuiteInProgress> suites = new TreeMap<>();
        private final TestUiSession session;

        Watcher(TestUiSession session) {
            this.session = session;
        }

        @Override
        public synchronized void onStart() {
            LOG.info("Starting test run");
            session.start();
        }

        @Override
        protected void onTestCompleted(TestResult test) {
            SuiteInProgress testSuite = getSuite(test.getModuleName());
            Testcase testCase = session.createTestCase(test.getTestName());
            testCase.setStatus(test.getStatus());
            testSuite.tests.add(testCase);
        }

        @Override
        public synchronized void onFinish() {
            LOG.info("Finished test run");
            for (SuiteInProgress suite : suites.values()) {
                session.startSuite(suite.suite);
                for (Testcase testCase : suite.tests) {
                    session.finishTest(testCase);
                }
                session.finishCurrentSuite();
            }
            session.finish();
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
