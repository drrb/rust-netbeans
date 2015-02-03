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
package com.github.drrb.rust.netbeans.project;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.openide.util.Lookup;

/**
 *
 */
public class RustProjectActionProviderTest {

    private RustProject project;
    private Cargo cargo;
    private RustProjectActionProvider actionProvider;

    @Before
    public void setUp() {
        project = mock(RustProject.class);
        cargo = mock(Cargo.class);
        actionProvider = new RustProjectActionProvider(cargo);
    }

    @Test
    public void testBuild() {
        actionProvider.invokeAction("build", Lookup.EMPTY);

        verify(cargo).run("build");
    }

    @Test
    public void testRebuild() {
        actionProvider.invokeAction("rebuild", Lookup.EMPTY);

        verify(cargo).run("clean", "build");
    }

}
