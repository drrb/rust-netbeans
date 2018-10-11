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

import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.modules.gsf.testrunner.api.TestSession;
import org.netbeans.modules.gsf.testrunner.api.TestSuite;
import org.netbeans.modules.gsf.testrunner.api.Testcase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

public class FakeTestUiSession extends TestUiSession {
    private final LinkedList<TestSuite> suites = new LinkedList<>();
    private boolean started;
    private boolean finished;

    public FakeTestUiSession() {
        super(new DummyProject(), null);
    }

    @Override
    void finishCurrentSuite() {
    }

    @Override
    void finishTest(Testcase testCase) {
        // Relies on the unsafe API. May break on a NetBeans upgrade
        suites.getLast().getTestcases().add(testCase);
    }

    @Override
    Testcase createTestCase(String name) {
        return new Testcase(name, null, new TestSession(name, new DummyProject(), TestSession.SessionType.TEST));
    }

    @Override
    void startSuite(TestSuite testSuite) {
        suites.add(testSuite);
    }

    @Override
    void finish() {
        finished = true;
    }

    @Override
    void start() {
        started = true;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isFinished() {
        return finished;
    }

    public List<TestSuite> getSuites() {
        return suites;
    }

    private static class DummyProject implements Project {
        @Override
        public FileObject getProjectDirectory() {
            try {
                return FileUtil.toFileObject(Files.createTempDirectory("dummy-project").toFile());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public Lookup getLookup() {
            return Lookup.EMPTY;
        }
    }
}
