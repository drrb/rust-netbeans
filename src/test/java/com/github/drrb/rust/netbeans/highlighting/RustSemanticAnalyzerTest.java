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
package com.github.drrb.rust.netbeans.highlighting;

import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 */
public class RustSemanticAnalyzerTest {

    @Rule
    public final NetbeansWithRust netbeans = new NetbeansWithRust();

    @Test
    public void shouldFindFunctionDeclaration() throws Exception {
        netbeans.checkSemantic("semantic/function.rs");
    }

    @Test
    public void shouldFindStructDeclaration() throws Exception {
        netbeans.checkSemantic("semantic/struct.rs");
    }

    @Test
    public void shouldFindAnnotationOnFunction() throws Exception {
        netbeans.checkSemantic("semantic/annotation.rs");
    }

    @Test
    @Ignore
    public void shouldFindImplDeclaration() throws Exception {
        netbeans.checkSemantic("semantic/impl.rs");
    }

    @Test
    @Ignore
    public void shouldFindEnumDeclaration() throws Exception {
        netbeans.checkSemantic("semantic/enum.rs");
    }

    @Test
    @Ignore
    public void shouldFindTraitDeclaration() throws Exception {
        netbeans.checkSemantic("semantic/trait.rs");
    }

    @Test
    @Ignore
    public void shouldFindTraitImplDeclaration() throws Exception {
        netbeans.checkSemantic("semantic/trait_impl.rs");
    }
}
