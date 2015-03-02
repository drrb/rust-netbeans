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
package com.github.drrb.rust.netbeans.project.action;

import com.github.drrb.rust.netbeans.cargo.Cargo;
import com.github.drrb.rust.netbeans.cargo.CargoListener;
import com.github.drrb.rust.netbeans.cargo.TestResult;
import com.github.drrb.rust.netbeans.commandrunner.CommandFuture;
import com.github.drrb.rust.netbeans.project.RustProject;
import java.util.List;
import org.netbeans.modules.gsf.testrunner.api.Manager;
import org.netbeans.modules.gsf.testrunner.api.TestSession;
import static org.netbeans.spi.project.ActionProvider.COMMAND_TEST;
import org.openide.util.Lookup;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.gsf.testrunner.api.Report;
import org.netbeans.modules.gsf.testrunner.api.TestSuite;
import org.netbeans.modules.gsf.testrunner.api.Testcase;

/**
 *
 */
public class TestCommand implements Command {
    private static final Logger LOG = Logger.getLogger(TestCommand.class.getName());
    public static final TestCommand INSTANCE = new TestCommand();

    @Override
    public String getId() {
        return COMMAND_TEST;
    }

    @Override
    public void run(Lookup context) {
        RustProject project = context.lookup(RustProject.class);
        Cargo cargo = context.lookup(Cargo.class);
        CommandFuture commandFuture = cargo.run("test");
        //commandFuture.addListener(new TestWatcher(project));
    }

//    private static class TestWatcher extends CargoListener {
//
//        private final Map<String, List<Testcase>> testCases = new ConcurrentHashMap<>();
//        private final TestSession testSession;
//
//        public TestWatcher(Project project) {
//            this.testSession = new TestSession("Cargo Tests", project, TestSession.SessionType.TEST/*, new TestRunnerNodeFactory() {}*/);
//        }
//
//        @Override
//        public synchronized void onStart() {
//            Manager.getInstance().setTestingFramework("CARGO");
//        }
//
//        @Override
//        public synchronized void onFinish() {
//            LOG.warning("finishing...");
//            testSession.addSuite(TestSuite.ANONYMOUS_TEST_SUITE);
//            Manager.getInstance().testStarted(testSession);
//            for (Map.Entry<String, List<Testcase>> testSuite : testCases.entrySet()) {
//                String suiteName = testSuite.getKey();
//                System.out.println("Adding suite " + suiteName);
//                //TestSuite suite = suiteName.equals("Root") ? TestSuite.ANONYMOUS_TEST_SUITE : new TestSuite(suiteName);
//                TestSuite suite = new TestSuite(suiteName);
//                testSession.addSuite(suite);
//                Manager.getInstance().displaySuiteRunning(testSession, suite);
//                List<Testcase> testCases = testSuite.getValue();
//                for (Testcase testCase : testCases) {
//                    System.out.println("  Adding case " + testCase.getName());
//                    testSession.addOutput("output from " + testCase.getName());
//                    testSession.addTestCase(testCase);
//                }
//                Report report = testSession.getReport(3000);
//                Manager.getInstance().displayReport(testSession, report, true);
//            }
//            Manager.getInstance().sessionFinished(testSession);
//        }
//
//        @Override
//        protected void onTestCompleted(TestResult test) {
//            if (!testCases.containsKey(test.getModuleName())) {
//                testCases.put(test.getModuleName(), new LinkedList<Testcase>());
//            }
//            Testcase testCase = new Testcase(test.getTestName(), null, testSession);
////            testCase.setLocation(suiteName);
////            testCase.setClassName(testName);
//            testCase.setStatus(test.getStatus());
//            testCases.get(test.getModuleName()).add(testCase);
//        }
//    }
}
