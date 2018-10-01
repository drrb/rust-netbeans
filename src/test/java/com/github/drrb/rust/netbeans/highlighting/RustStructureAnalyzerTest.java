/*
 * Copyright (C) 2018 Tim Boudreau
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

package com.github.drrb.rust.netbeans.highlighting;

import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import org.junit.Rule;
import org.junit.Test;

/**
 * Analyzes the output of RustStructureAnalyzer.
 *
 * @author Tim Boudreau
 */
public class RustStructureAnalyzerTest {
    @Rule
    public final NetbeansWithRust netbeans = new NetbeansWithRust();

    @Test(timeout=15000)
    public void shouldFindStructDeclaration() throws Exception {
        netbeans.checkStructure("semantic/struct.rs");
    }

    @Test(timeout=15000)
    public void shouldFindImplDeclaration() throws Exception {
        netbeans.checkStructure("semantic/impl.rs");
    }

    @Test(timeout=15000)
    public void shouldFindEnumDeclaration() throws Exception {
        netbeans.checkStructure("semantic/enum.rs");
    }

    @Test(timeout=15000)
    public void shouldFindTraitDeclaration() throws Exception {
        netbeans.checkStructure("semantic/trait.rs");
    }

    @Test(timeout=15000)
    public void shouldFindTraitImplDeclaration() throws Exception {
        netbeans.checkStructure("semantic/trait_impl.rs");
    }
}
