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
 */
@Ignore
public class RustCompileErrorHighlighterTest {

    @Rule
    public final NetbeansWithRust netbeans = new NetbeansWithRust();

    @Test
    public void shouldExtractErrorForHighlighting() throws Exception {
        netbeans.checkCompileErrors("compile/singlefile", "src/main.rs");
    }

    @Test
    public void shouldOnlyShowErrorsFromThisFile() throws Exception {
        netbeans.checkCompileErrors("compile/errorinotherfile", "src/in_editor.rs");
    }

    @Test
    public void shouldParseFromCrate() throws Exception {
        netbeans.checkCompileErrors("compile/siblingmoduleimport", "src/in_editor.rs");
    }
}
