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
import com.github.drrb.rust.netbeans.commandrunner.Shell;
import com.github.drrb.rust.netbeans.project.RustProject;
import com.google.common.base.Joiner;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.gsf.testrunner.api.Manager;
import org.netbeans.modules.gsf.testrunner.api.TestSession;
import static org.netbeans.spi.project.ActionProvider.COMMAND_TEST;
import org.openide.util.Lookup;
import static java.util.Arrays.asList;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.gsf.testrunner.api.Report;
import org.netbeans.modules.gsf.testrunner.api.Status;
import org.netbeans.modules.gsf.testrunner.api.TestSuite;
import org.netbeans.modules.gsf.testrunner.api.Testcase;

/**
 *
 */
public class TestCommand implements Command {
    private static final Logger LOG = Logger.getLogger(TestCommand.class.getName());
    private static final Pattern TEST_RESULT_REGEX = Pattern.compile("^test (.+) ... (ok|FAILED)$");
    public static final TestCommand INSTANCE = new TestCommand();

    @Override
    public String getId() {
        return COMMAND_TEST;
    }

    @Override
    public void run(Lookup context) {
        RustProject project = context.lookup(RustProject.class);
        Cargo cargo = context.lookup(Cargo.class);
//        cargo.run(new TestWatcher(project), "test");
    }

//    private static class TestWatcher extends Shell.OutputProcessor {
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
//        public void onLinePrinted(String line) {
//            Matcher matcher = TEST_RESULT_REGEX.matcher(line);
//            if (matcher.matches()) {
//                //TODO: named capture groups
//                String testFullName = matcher.group(1);
//                String testResult = matcher.group(2);
//                List<String> testNameParts = asList(testFullName.split("::"));
//                String testName = testNameParts.get(testNameParts.size() - 1);
//                String moduleName;
//                if (testNameParts.size() == 1) {
//                    moduleName = "Root";
//                } else {
//                    moduleName = Joiner.on("::").join(testNameParts.subList(0, testNameParts.size() - 1));
//                }
//                Status result = testResult.equals("ok") ? Status.PASSED : Status.FAILED;
//                addTest(moduleName, testName, result);
//            }
//        }
//
//        private synchronized void addTest(String suiteName, String testName, Status result) {
//            if (!testCases.containsKey(suiteName)) {
//                testCases.put(suiteName, new LinkedList<Testcase>());
//            }
//            Testcase testCase = new Testcase(testName, null, testSession);
////            testCase.setLocation(suiteName);
////            testCase.setClassName(testName);
//            testCase.setStatus(result);
//            testCases.get(suiteName).add(testCase);
//            LOG.warning(String.format("Test added:\n  suiteName=%s\n  testName=%s\n  result=%s\n", suiteName, testName, result));
//        }
//    }

}
