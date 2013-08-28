/**
 * Copyright (C) 2013 drrb
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

import static com.github.drrb.rust.netbeans.TestParsing.*;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.SyntaxError;
import org.junit.Test;
import java.util.Iterator;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class RustParserTest {

    @Test
    public void shouldFindError() throws Exception {
        StringBuilder function = new StringBuilder();
        function.append("fn greet(name: str) {\n");
        function.append("    xxx io::println(fmt!(\"Hello, %?\", name));\n");
        function.append("}\n");

        NetbeansRustParserResult result = parse(function);

        Iterator<SyntaxError> syntaxErrors = result.getSyntaxErrors().iterator();
        SyntaxError syntaxError = syntaxErrors.next();
        assertThat(syntaxError.getLine(), is(2));
        assertThat(syntaxError.getCharPositionInLine(), is(8));
        assertThat(syntaxError.getMessage(), is("no viable alternative at input 'xxx io'"));
    }

    @Test
    public void shouldParseRustdoc() throws Exception {
        StringBuilder function = new StringBuilder();
        function.append("/**\n");
        function.append(" * Say Hello\n");
        function.append(" */\n");
        function.append("fn greet(name: str) {\n");
        function.append("    io::println(fmt!(\"Hello, %?\", name));\n");
        function.append("}\n");

        NetbeansRustParserResult result = parse(function);

        Iterator<Rustdoc> rustdocs = result.getRustdocs().iterator();
        Rustdoc rustdoc = rustdocs.next();
        assertThat(rustdoc.getText(), is("/**\n * Say Hello\n */"));
        assertThat(rustdoc.getIdentifier(), is("greet"));
    }
}
