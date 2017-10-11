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
package com.github.drrb.rust.netbeans.swing;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 */
public class PathsTableModelTest {
    private PathsTableModel model;

    @Before
    public void setUp() {
        model = new PathsTableModel();
    }

    @Test
    public void shouldReturnSetPaths() {
        model.setPaths(asList("/a/b/c", "/d/e/f"));
        assertThat(model.getPaths(), is(asList("/a/b/c", "/d/e/f")));
    }

    @Test
    public void shouldSupportAddingPaths() {
        model.addPath("/a/b/c");
        model.addPath("/d/e/f");
        assertThat(model.getPaths(), is(asList("/a/b/c", "/d/e/f")));
    }

    @Test
    public void shouldSupportRemovingAPath() {
        model.addPath("/a/b/c");
        model.addPath("/d/e/f");
        model.addPath("/g/h/i");
        model.removePath("/d/e/f");
        assertThat(model.getPaths(), is(asList("/a/b/c", "/g/h/i")));
    }

    @Test
    public void shouldSupportRemovingAListOfPaths() {
        model.addPath("/a/b/c");
        model.addPath("/d/e/f");
        model.addPath("/g/h/i");
        model.addPath("/j/k/l");
        model.removePathsAt(1, 3);
        assertThat(model.getPaths(), is(asList("/a/b/c", "/g/h/i")));
    }

}
