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
package com.github.drrb.rust.netbeans.parsing;

import com.github.drrb.rust.netbeans.test.CslTestHelper;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 */
public class NetbeansRustParserTest {

    @Rule
    public final CslTestHelper csl = new CslTestHelper();

    @Test
    public void shouldHighlightReferenceToMissingFunction() throws Exception {
        csl.checkParseMessages("parse/missing_parenthesis.rs");
    }

}
