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
package com.github.drrb.rust.netbeans.parsing;

import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 */
public class NetbeansRustParserTest {

    @Rule
    public final NetbeansWithRust netbeans = new NetbeansWithRust();

    @Test
    public void highlightsSingleError() throws Exception {
        netbeans.checkParseMessages("parse/errors/missing_parenthesis.rs");
    }

    @Test
    public void highlightsMultipleErrors() throws Exception {
        netbeans.checkParseMessages("parse/errors/two_errors.rs");
    }

    @Test
    public void highlightsAcrossItems() throws Exception {
        netbeans.checkParseMessages("parse/errors/errors_in_items.rs");
    }

}
