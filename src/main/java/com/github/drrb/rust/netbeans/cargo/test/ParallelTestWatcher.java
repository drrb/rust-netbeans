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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

class ParallelTestWatcher extends CommandFuture.Listener {
    private static final Logger LOG = Logger.getLogger(ParallelTestWatcher.class.getName());
    private static final Pattern TEST_RESULT_REGEX = Pattern.compile("^test (?<testName>.+) ... (?<testResult>ok|FAILED)$");
    private final Map<String, SuiteInProgress> suites = new TreeMap<>();
    private final TestUiSession session;

    ParallelTestWatcher(TestUiSession session) {
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
        for (SuiteInProgress suite : suites.values()) {
            session.startSuite(suite.suite);
            for (Testcase testCase : suite.tests) {
                session.finishTest(testCase);
            }
            session.finishCurrentSuite();
        }
        session.finish();
    }

    @Override
    public void onLinePrinted(String line) {
        Matcher matcher = TEST_RESULT_REGEX.matcher(line);
        if (matcher.matches()) {
            String testFullName = matcher.group("testName");
            String testResult = matcher.group("testResult");
            List<String> testNameParts = asList(testFullName.split("::"));
            String testName = testNameParts.get(testNameParts.size() - 1);
            String moduleName;
            if (testNameParts.size() == 1) {
                moduleName = "Root";
            } else {
                moduleName = Joiner.on("::").join(testNameParts.subList(0, testNameParts.size() - 1));
            }
            Status result = testResult.equals("ok") ? Status.PASSED : Status.FAILED;
            onTestCompleted(new TestResult(moduleName, testName, result));
        }
    }

    private void onTestCompleted(TestResult test) {
        SuiteInProgress testSuite = getSuite(test.getModuleName());
        Testcase testCase = session.createTestCase(test.getTestName());
        testCase.setStatus(test.getStatus());
        testSuite.tests.add(testCase);
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
