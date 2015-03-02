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
package com.github.drrb.rust.netbeans.cargo;

import com.github.drrb.rust.netbeans.commandrunner.CommandFuture;
import com.google.common.base.Joiner;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.gsf.testrunner.api.Status;

public class CargoListener extends CommandFuture.Listener {

    private static final Pattern TEST_RESULT_REGEX = Pattern.compile("^test (?<testName>.+) ... (?<testResult>ok|FAILED)$");

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

    protected void onTestCompleted(TestResult test) {
    }
}
