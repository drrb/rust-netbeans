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
package com.github.drrb.rust.netbeans.project.action;

import com.github.drrb.rust.netbeans.cargo.Cargo;
import com.github.drrb.rust.netbeans.project.RustProject;
import com.github.drrb.rust.netbeans.project.action.RustProjectActionProvider;
import org.junit.Before;
import org.junit.Test;

import static com.github.drrb.rust.netbeans.cargo.Cargo.BUILD;
import static com.github.drrb.rust.netbeans.cargo.Cargo.CLEAN;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.openide.util.Lookup;

/**
 *
 */
public class RustProjectActionProviderTest {

    private Cargo cargo;
    private RustProjectActionProvider actionProvider;

    @Before
    public void setUp() {
        RustProject project = mock(RustProject.class);
        cargo = mock(Cargo.class);
        actionProvider = new RustProjectActionProvider(project, cargo);
    }

    @Test
    public void testBuild() {
        actionProvider.invokeAction("build", Lookup.EMPTY);

        verify(cargo).run(BUILD);
    }

    @Test
    public void testRebuild() {
        actionProvider.invokeAction("rebuild", Lookup.EMPTY);

        verify(cargo).run(CLEAN, BUILD);
    }

}
