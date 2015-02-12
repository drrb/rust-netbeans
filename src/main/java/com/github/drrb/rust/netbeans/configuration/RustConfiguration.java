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
package com.github.drrb.rust.netbeans.configuration;

import com.github.drrb.rust.netbeans.RustLanguage;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 */
public class RustConfiguration {
    static final String KEY_CARGO_PATH = "com.github.drrb.rust.netbeans.cargoPath";
    static final String KEY_LIBRARIES_PATH = "com.github.drrb.rust.netbeans.libraryPath";

    public static RustConfiguration get() {
        return new RustConfiguration(NbPreferences.forModule(RustLanguage.class));
    }

    private final Preferences preferences;

    public RustConfiguration(Preferences preferences) {
        this.preferences = preferences;
    }

    public String getCargoPath() {
        return preferences.get(KEY_CARGO_PATH, getDefaultCargoPath());
    }

    public String getLibrariesPath() {
        return preferences.get(KEY_LIBRARIES_PATH, getDefaultLibrariesPath());
    }

    public List<String> getSearchPaths() {
        return Os.getCurrent().splitLibrariesPath(getLibrariesPath());
    }

    public void setCargoPath(String cargoPath) {
        preferences.put(KEY_CARGO_PATH, cargoPath);
    }

    public void setLibrariesPath(String librariesPath) {
        preferences.put(KEY_LIBRARIES_PATH, librariesPath);
    }

    private String getDefaultCargoPath() {
        return Os.getCurrent().defaultCargoPath;
    }

    private static String getDefaultLibrariesPath() {
        return Os.getCurrent().defaultLibrariesPath;
    }

    private enum Os {
        MAC_OS(asList("mac", "darwin"), "/usr/local/bin/cargo", "/usr/local/lib/rustlib/x86_64-apple-darwin/lib", ":"),
        WINDOWS(asList("win"), "C:\\Rust\\cargo.exe", "C:\\Rust\\libs", ";"),
        GNU_SLASH_LINUX(asList("nux"), "/usr/local/bin/cargo", "/usr/local/lib", ":"),
        UNKNOWN(Collections.<String>emptyList(), "/usr/local/bin/cargo", "/usr/local/lib", ":");

        private final List<String> substrings;
        private final String defaultCargoPath;
        private final String defaultLibrariesPath;
        private final String pathDelimiter;

        private Os(List<String> substrings, String defaultCargoPath, String defaultLibrariesPath, String pathDelimiter) {
            this.substrings = substrings;
            this.defaultCargoPath = defaultCargoPath;
            this.defaultLibrariesPath = defaultLibrariesPath;
            this.pathDelimiter = pathDelimiter;
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

        private List<String> splitLibrariesPath(String librariesPath) {
            return asList(librariesPath.split(pathDelimiter));
        }

        private static boolean currentIs64Bit() {
            return System.getProperty("os.arch").contains("64");
        }

        private static String currentOsString() {
            return System.getProperty("os.name", "unknown").toLowerCase(Locale.ENGLISH);
        }
    }
}
