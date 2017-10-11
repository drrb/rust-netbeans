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
package com.github.drrb.rust.netbeans.formatting;

import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import org.junit.Rule;
import org.junit.Test;
import org.netbeans.modules.csl.api.test.CslTestHelper.RunInEventQueueThread;

/**
 *
 */
public class RustFormatterTest {

    @Rule
    public final NetbeansWithRust netbeans = new NetbeansWithRust();

    @Test
    public void shouldFormatFunction() throws Exception {
        netbeans.reformatFileContents("format/function.rs");
    }

    @Test
    public void shouldFormatStruct() throws Exception {
        netbeans.reformatFileContents("format/struct.rs");
    }

    @Test
    @RunInEventQueueThread
    public void shouldIndentSameInsideBlock() throws Exception {
        netbeans.testIndentInFile("indent/inside_block.rs");
    }

    @Test
    @RunInEventQueueThread
    public void shouldIndentFurtherAfterBrace() throws Exception {
        netbeans.testIndentInFile("indent/into_existing_function.rs");
    }

    @Test
    @RunInEventQueueThread
    public void shouldIgnoreWhitespaceAfterBrace() throws Exception {
        netbeans.testIndentInFile("indent/ignore_trailing_whitespace.rs");
    }

    @Test
    @RunInEventQueueThread
    public void shouldInsertWhenInsertingBreakAfterBrace() throws Exception {
        netbeans.testIndentInFile("indent/after_brace.rs");
    }

}
