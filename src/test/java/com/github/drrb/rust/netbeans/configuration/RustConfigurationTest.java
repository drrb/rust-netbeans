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

import com.github.drrb.rust.netbeans.test.TemporaryPreferences;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;

/**
 *
 */
public class RustConfigurationTest {

    @Rule
    public final TemporaryPreferences preferences = new TemporaryPreferences();
    private RustConfiguration config;

    @Before
    public void setUp() throws Exception {
        config = new RustConfiguration(preferences.get());
    }

    @Test
    public void shouldReturnSavedCargoPath() throws Exception {
        preferences.get().put(RustConfiguration.KEY_CARGO_PATH, "/path/to/cargo");
        assertThat(config.getCargoPath(), is("/path/to/cargo"));
    }

    @Test
    public void shouldReturnSavedLibrariesPath() throws Exception {
        preferences.get().put(RustConfiguration.KEY_LIBRARIES_PATH, "/path/to/libraries");
        assertThat(config.getLibrariesPath(), is("/path/to/libraries"));
    }

    @Test
    public void shouldReturnDefaultCargoPathIfNoneSaved() throws Exception {
        assertThat(config.getCargoPath(), is("/usr/local/bin/cargo"));
    }

    @Test
    public void shouldReturnDefaultLibrariesPathIfNoneSaved() throws Exception {
        assertThat(config.getLibrariesPath(), is("/usr/local/lib/rustlib/x86_64-apple-darwin/lib"));
    }

    @Test
    public void shouldSavePreferredCargoPath() throws Exception {
        config.setCargoPath("/new/path/to/cargo");

        assertThat(config.getCargoPath(), is("/new/path/to/cargo"));
    }

    @Test
    public void shouldSavePreferredLibrariesPath() throws Exception {
        config.setLibrariesPath("/new/path/to/libraries");

        assertThat(config.getLibrariesPath(), is("/new/path/to/libraries"));
    }
}
