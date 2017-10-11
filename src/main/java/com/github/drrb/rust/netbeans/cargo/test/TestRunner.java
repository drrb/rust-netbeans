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

import com.github.drrb.rust.netbeans.cargo.Cargo;
import com.github.drrb.rust.netbeans.cargo.CargoConfig;
import com.github.drrb.rust.netbeans.commandrunner.CommandFuture;
import com.github.drrb.rust.netbeans.configuration.RustConfiguration;
import com.github.drrb.rust.netbeans.project.RustProject;
import org.netbeans.modules.gsf.testrunner.ui.api.Manager;
import org.openide.filesystems.FileObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TestRunner {
    public static class Factory {

        private RustConfiguration config;

        public Factory() {
            this(RustConfiguration.get());
        }

        public Factory(RustConfiguration config) {
            this.config = config;
        }

        public TestRunner create(RustProject project, Cargo cargo) {
            //TODO: if (config.runTestsParallel() {
            //TODO: } else {
            return new TestRunner(project, cargo, new SequentialTestStrategy());
            //TODO: }
        }
    }

    interface TestStrategy {
        Cargo.Command getTestCommand();
        CommandFuture.Listener createWatcher(TestUiSession session);
    }

    static class SequentialTestStrategy implements TestStrategy {
        public Cargo.Command getTestCommand() {
            return Cargo.TEST_SEQUENTIAL;
        }
        public CommandFuture.Listener createWatcher(TestUiSession session) {
            return new SequentialTestWatcher(session);
        }
    }

    static class ParallelTestStrategy implements TestStrategy {
        public Cargo.Command getTestCommand() {
            return Cargo.TEST_PARALLEL;
        }
        public CommandFuture.Listener createWatcher(TestUiSession session) {
            return new ParallelTestWatcher(session);
        }
    }

    private static final Logger LOG = Logger.getLogger(TestRunner.class.getName());
    private final RustProject project;
    private final Cargo cargo;
    private final TestStrategy testStrategy;

    public TestRunner(RustProject project, Cargo cargo, TestStrategy testStrategy) {
        this.project = project;
        this.cargo = cargo;
        this.testStrategy = testStrategy;
    }

    public void run() {
        LOG.info("Running all tests");
        watchCargoCommand(testStrategy.getTestCommand());
    }

    public void run(FileObject file) {
        LOG.log(Level.INFO, "Running tests for file {0}", file);
        CargoConfig cargoConfig = project.getCargoConfig();
        String moduleFilter;
        if (cargoConfig.isCrate(file)) {
            moduleFilter = "";
        } else {
            String moduleName = cargoConfig.getModuleName(file);
            moduleFilter = moduleName + "::";  // Rust's test runner matches "my_module::" against all tests in my_module
        }
        watchCargoCommand(testStrategy.getTestCommand().withArg(moduleFilter));
    }

    private void watchCargoCommand(Cargo.Command command) {
        TestUiSession session = new TestUiSession(project, Manager.getInstance());
        CommandFuture.Listener watcher = testStrategy.createWatcher(session);
        CommandFuture commandFuture = cargo.run(command);
        commandFuture.addListener(watcher);
    }
}
