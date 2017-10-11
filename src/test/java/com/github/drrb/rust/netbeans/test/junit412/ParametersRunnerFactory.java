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
package com.github.drrb.rust.netbeans.test.junit412;

import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;

/**
 * A {@code ParameterizedRunnerFactory} creates a runner for a single
 * {@link TestWithParameters}.
 *
 * @since 4.12
 */
public interface ParametersRunnerFactory {
    /**
     * Returns a runner for the specified {@link TestWithParameters}.
     *
     * @throws InitializationError
     *             if the runner could not be created.
     */
    Runner createRunnerForTestWithParameters(TestWithParameters test)
            throws InitializationError;
}
