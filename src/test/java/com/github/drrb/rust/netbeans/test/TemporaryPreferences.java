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
package com.github.drrb.rust.netbeans.test;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 *
 */
public class TemporaryPreferences extends TestWatcher {
    private Preferences preferences;

    @Override
    protected void starting(Description description) {
        this.preferences = Preferences.userNodeForPackage(description.getTestClass());
    }

    @Override
    protected void finished(Description description) {
        try {
            this.preferences.removeNode();
        } catch (BackingStoreException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Preferences get() {
        return preferences;
    }
}
