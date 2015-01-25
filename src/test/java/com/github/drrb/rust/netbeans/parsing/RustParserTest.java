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

import java.util.List;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class RustParserTest {

    private RustParser parser;
    private RustParser.Result result;

    @Before
    public void setUp() {
        parser = new RustParser();
    }

    @After
    public void tearDown() {
        result.destroy();
    }

    @Test
    public void shouldParseValidSource() {
        result = parser.parse("test.rs", "fn main() {}");
        assertTrue(result.isSuccess());
        assertThat(result.getAst(), not(nullValue()));
        assertThat(result.getParseMessages(), is(empty()));
    }

    @Test
    public void shouldHaveErrorsWhenSourceIsInvalid() {
        result = parser.parse("test.rs", "fn main() { 1 2 }");
        assertFalse(result.isSuccess());
        assertThat(result.getAst(), is(nullValue()));
        assertThat(result.getParseMessages(), not(empty()));
    }

    @Test
    public void shouldNotDieWhenThereAreObviousSyntaxErrors() {
        result = parser.parse("test.rs", "fn main() {");
        assertFalse(result.isSuccess());
        assertThat(result.getAst(), is(nullValue()));

        RustParseMessage firstMessage = result.getParseMessages().get(0);
        assertThat(firstMessage.getLevel(), is(RustParseMessage.Level.HELP));
        assertThat(firstMessage.getStartLine(), is(1));
        assertThat(firstMessage.getStartCol(), is(10));
        assertThat(firstMessage.getEndLine(), is(1));
        assertThat(firstMessage.getEndCol(), is(11));
        assertThat(firstMessage.getMessage(), is("did you mean to close this delimiter?"));
    }
}
