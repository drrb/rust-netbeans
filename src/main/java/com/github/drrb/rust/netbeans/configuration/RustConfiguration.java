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

import com.github.drrb.rust.netbeans.RustLanguage;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import org.openide.util.NbPreferences;

/**
 *
 */
public class RustConfiguration {

    public static RustConfiguration get() {
        return new RustConfiguration(NbPreferences.forModule(RustLanguage.class));
    }

    private final Preferences preferences;
    private final ConfigFlavour os;

    @VisibleForTesting
    public RustConfiguration(Preferences preferences) {
        this(Os.getCurrent(), preferences);
    }

    @VisibleForTesting
    public RustConfiguration(Os os, Preferences preferences) {
        this.os = ConfigFlavour.of(os);
        this.preferences = preferences;
    }

    public String getCargoPath() {
        return getPref(Preference.CARGO_PATH, os.defaultCargoPath);
    }

    public List<String> getLibrariesPaths() {
        String librariesPath = getPref(Preference.LIBRARIES_PATH, os.defaultLibrariesPath);
        return os.deserializePath(librariesPath);
    }

    public void setCargoPath(String cargoPath) {
        setPref(Preference.CARGO_PATH, cargoPath);
    }

    public void setLibrariesPaths(List<String> paths) {
        setPref(Preference.LIBRARIES_PATH, os.serializePath(paths));
    }

    private String getPref(Preference pref, String defaultValue) {
        return preferences.get(pref.key, defaultValue);
    }

    private void setPref(Preference pref, String value) {
        preferences.put(pref.key, value);
    }

    private enum Preference {
        CARGO_PATH("com.github.drrb.rust.netbeans.cargoPath"),
        LIBRARIES_PATH("com.github.drrb.rust.netbeans.libraryPath");

        private final String key;

        private Preference(String key) {
            this.key = key;
        }
    }

    private enum ConfigFlavour {
        // TODO: Support 32 bit too
        WINDOWS(";", "C:\\Rust\\bin\\cargo.exe", "C:\\Rust\\bin\\rustlib\\x86_64-pc-windows-gnu\\lib"),
        UNIXY(":", "/usr/local/bin/cargo", "/usr/local/lib/rustlib/x86_64-apple-darwin/lib");

        private final String pathSeparator;
        private final String defaultCargoPath;
        private final String defaultLibrariesPath;

        private ConfigFlavour(String pathSeparator, String defaultCargoPath, String defaultLibrariesPath) {
            this.pathSeparator = pathSeparator;
            this.defaultCargoPath = defaultCargoPath;
            this.defaultLibrariesPath = defaultLibrariesPath;
        }

        public static ConfigFlavour of(Os os) {
            return os == Os.WINDOWS ? WINDOWS : UNIXY;
        }

        private String serializePath(List<String> paths) {
            return Joiner.on(pathSeparator).join(paths);
        }

        private List<String> deserializePath(String path) {
            return asList(path.split(Pattern.quote(pathSeparator)));
        }
    }
}
