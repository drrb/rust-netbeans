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
package com.github.drrb.rust.netbeans.cargo.test;

import com.github.drrb.rust.netbeans.commandrunner.CommandFuture;
import com.google.common.base.Joiner;
import org.netbeans.modules.gsf.testrunner.api.Status;
import org.netbeans.modules.gsf.testrunner.api.TestSuite;
import org.netbeans.modules.gsf.testrunner.api.Testcase;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

class SequentialTestWatcher extends CommandFuture.Listener {
    private static final Logger LOG = Logger.getLogger(SequentialTestWatcher.class.getName());
    private static final Pattern TEST_START_REGEX = Pattern.compile("^test (?<testName>.+) ... .*$");
    private static final Pattern TEST_FINISH_REGEX = Pattern.compile("^.*(?<testResult>ok|FAILED)$");
    private final Map<String, TestSuite> suites = new TreeMap<>();
    private final TestUiSession session;
    private TestResult currentTest;
    private TestResult previousTest;

    SequentialTestWatcher(TestUiSession session) {
        this.session = session;
    }

    @Override
    public void onStart() {
        LOG.info("Starting test run");
        session.start();
    }

    @Override
    public void onFinish() {
        LOG.info("Finished test run");
        session.finishCurrentSuite();
        session.finish();
    }

    @Override
    public void onLinePrinted(String line) {
        Matcher testStartMatcher = TEST_START_REGEX.matcher(line);
        if (testStartMatcher.matches()) {
            String testFullName = testStartMatcher.group("testName");
            List<String> testNameParts = asList(testFullName.split("::"));
            String testName = testNameParts.get(testNameParts.size() - 1);
            String moduleName;
            if (testNameParts.size() == 1) {
                moduleName = "Root";
            } else {
                moduleName = Joiner.on("::").join(testNameParts.subList(0, testNameParts.size() - 1));
            }
            currentTest = new TestResult(moduleName, testName);
            if (suiteHasChanged()) {
                if (previousTest != null) {
                    session.finishCurrentSuite();
                }
                session.startSuite(getSuite(moduleName));
            }
        }
        Matcher testFinishMatcher = TEST_FINISH_REGEX.matcher(line);
        if (testFinishMatcher.matches()) {
            String testResult = testFinishMatcher.group("testResult");
            Status result = testResult.equals("ok") ? Status.PASSED : Status.FAILED;
            Testcase testCase = session.createTestCase(currentTest.getTestName());
            testCase.setStatus(result);
            session.finishTest(testCase);
            previousTest = currentTest;
        }
    }

    private boolean suiteHasChanged() {
        if (currentTest == null) {
            return false;
        }
        if (previousTest == null) {
            return true;
        }
        return !currentTest.getModuleName().equals(previousTest.getModuleName());
    }

    private TestSuite getSuite(String suiteName) {
        if (!suites.containsKey(suiteName)) {
            TestSuite testSuite = new TestSuite(suiteName);
            suites.put(suiteName, testSuite);
        }
        return suites.get(suiteName);
    }
}
