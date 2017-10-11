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
package com.github.drrb.rust.netbeans.configuration;

import com.github.drrb.rust.netbeans.commandrunner.Shell;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public enum Os {

    MAC(asList("mac", "darwin"), Shell.BASH),
    WINDOWS(asList("win"), Shell.CMD),
    GNU_SLASH_LINUX(asList("nux"), Shell.BASH),
    UNKNOWN(Collections.<String>emptyList(), Shell.BASH);

    private final List<String> substrings;
    private final Shell shell;

    private Os(List<String> substrings, Shell shell) {
        this.substrings = substrings;
        this.shell = shell;
    }

    public Shell shell() {
        return shell;
    }

    public static Os getCurrent() {
        for (Os os : values()) {
            if (os.isCurrent()) {
                return os;
            }
        }
        return UNKNOWN;
    }

    public boolean isCurrent() {
        for (String substring : substrings) {
            if (currentOsString().contains(substring)) {
                return true;
            }
        }
        return false;
    }

    private static boolean currentIs64Bit() {
        return System.getProperty("os.arch").contains("64");
    }

    private static String currentOsString() {
        return System.getProperty("os.name", "unknown").toLowerCase(Locale.ENGLISH);
    }
}
