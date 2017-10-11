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

import org.junit.runners.model.TestClass;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * A {@code TestWithParameters} keeps the data together that are needed for
 * creating a runner for a single data set of a parameterized test. It has a
 * name, the test class and a list of parameters.
 *
 * @since 4.12
 */
public class TestWithParameters {
    private final String name;

    private final TestClass testClass;

    private final List<Object> parameters;

    public TestWithParameters(String name, TestClass testClass,
            List<Object> parameters) {
        notNull(name, "The name is missing.");
        notNull(testClass, "The test class is missing.");
        notNull(parameters, "The parameters are missing.");
        this.name = name;
        this.testClass = testClass;
        this.parameters = unmodifiableList(new ArrayList<Object>(parameters));
    }

    public String getName() {
        return name;
    }

    public TestClass getTestClass() {
        return testClass;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    @Override
    public int hashCode() {
        int prime = 14747;
        int result = prime + name.hashCode();
        result = prime * result + testClass.hashCode();
        return prime * result + parameters.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TestWithParameters other = (TestWithParameters) obj;
        return name.equals(other.name)
                && parameters.equals(other.parameters)
                && testClass.equals(other.testClass);
    }

    @Override
    public String toString() {
        return testClass.getName() + " '" + name + "' with parameters "
                + parameters;
    }

    private static void notNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}
