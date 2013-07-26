/**
 * Copyright (C) 2013 drrb
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
package com.github.drrb.rust.netbeans.parse;

import com.github.drrb.rust.netbeans.RustFunction;
import com.github.drrb.rust.netbeans.RustLexer;
import com.github.drrb.rust.netbeans.RustParser;
import java.util.Collection;
import java.util.Iterator;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 */
public class RustFunctionCollectingVisitorTest {

    @Test
    public void shouldFindNoFunctionsWhenThereAreNone() {
        CharSequence input = new StringBuilder("//");
        RustParser.ProgContext prog = parse(input).prog();
        Collection<RustFunction> functions = prog.accept(new RustFunctionCollectingVisitor());
        assertThat(functions, is(emptyIterable()));
    }

    @Test
    public void shouldFindAFunction() {
        CharSequence input = new StringBuilder("fn main() { }");
        RustParser.ProgContext prog = parse(input).prog();
        Iterator<RustFunction> functions = prog.accept(new RustFunctionCollectingVisitor()).iterator();
        RustFunction function = functions.next();
        assertThat(functions.hasNext(), is(false));
        assertThat(function.getName(), is("main"));
        assertThat(function.getStartIndex(), is(0));
        assertThat(function.getEndIndex(), is(12));
    }

    private RustParser parse(CharSequence input) {
        return new RustParser(new CommonTokenStream(new RustLexer(new ANTLRInputStream(input.toString()))));
    }
}
