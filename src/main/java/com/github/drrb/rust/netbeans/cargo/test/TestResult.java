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

import org.netbeans.modules.gsf.testrunner.api.Status;

import java.util.Objects;

public class TestResult {

    private final String moduleName;
    private final String testName;
    private final Status status;

    public TestResult(String moduleName, String testName) {
        this(moduleName, testName, null);
    }

    public TestResult(String moduleName, String testName, Status status) {
        this.moduleName = moduleName;
        this.testName = testName;
        this.status = status;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getTestName() {
        return testName;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new StringBuilder("TestResult: ")
                .append(moduleName)
                .append(" ").append(testName)
                .append(" - ").append(status)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleName, testName, status);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestResult other = (TestResult) obj;
        if (!Objects.equals(this.moduleName, other.moduleName)) {
            return false;
        }
        if (!Objects.equals(this.testName, other.testName)) {
            return false;
        }
        return this.status == other.status;
    }

}
