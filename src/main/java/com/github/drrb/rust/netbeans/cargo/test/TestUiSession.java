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

import org.netbeans.api.project.Project;
import org.netbeans.modules.gsf.testrunner.ui.api.Manager;
import org.netbeans.modules.gsf.testrunner.api.Report;
import org.netbeans.modules.gsf.testrunner.api.TestSession;
import org.netbeans.modules.gsf.testrunner.api.TestSuite;
import org.netbeans.modules.gsf.testrunner.api.Testcase;

class TestUiSession {

    private final Manager testManager;
    private final TestSession testSession;

    TestUiSession(Project project, Manager testManager) {
        this.testManager = testManager;
        this.testSession = new TestSession("Cargo Tests", project, TestSession.SessionType.TEST);
    }

    void start() {
        testManager.setTestingFramework("CARGO");
        testManager.testStarted(testSession);
    }

    void finish() {
        testManager.sessionFinished(testSession);
    }

    void startSuite(TestSuite testSuite) {
        testSession.addSuite(testSuite);
        testManager.displaySuiteRunning(testSession, testSuite);
    }

    Testcase createTestCase(String name) {
        return new Testcase(name, null, testSession);
    }

    void finishTest(Testcase testCase) {
        testSession.addTestCase(testCase);
    }

    void finishCurrentSuite() {
        Report report = testSession.getReport(0);
        testManager.displayReport(testSession, report, true); // `true` means don't display "running..." next to the suite
    }
}
