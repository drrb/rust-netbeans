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
package com.github.drrb.rust.netbeans.project.action;

import com.github.drrb.rust.netbeans.cargo.Cargo;
import com.github.drrb.rust.netbeans.cargo.test.TestRunner;
import com.github.drrb.rust.netbeans.configuration.RustConfiguration;
import com.github.drrb.rust.netbeans.project.RustProject;
import static org.netbeans.spi.project.ActionProvider.COMMAND_TEST;
import org.openide.util.Lookup;

/**
 *
 */
public class TestCommand implements Command {
    public static final TestCommand INSTANCE = new TestCommand(new TestRunner.Factory(RustConfiguration.get()));
    private final TestRunner.Factory testRunnerFactory;

    public TestCommand(TestRunner.Factory testRunnerFactory) {
        this.testRunnerFactory = testRunnerFactory;
    }

    @Override
    public String getId() {
        return COMMAND_TEST;
    }

    @Override
    public void run(Lookup context) {
        RustProject project = context.lookup(RustProject.class);
        Cargo cargo = context.lookup(Cargo.class);
        TestRunner testRunner = testRunnerFactory.create(project, cargo);
        testRunner.run();
    }
}
