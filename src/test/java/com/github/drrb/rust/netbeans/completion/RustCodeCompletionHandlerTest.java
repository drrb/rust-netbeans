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
package com.github.drrb.rust.netbeans.completion;

import com.github.drrb.rust.netbeans.RustSourceSnapshot;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import static com.github.drrb.rust.netbeans.parsing.RustLexUtils.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static com.github.drrb.rust.netbeans.test.Matchers.*;
import java.util.EnumSet;
import org.netbeans.modules.csl.api.CodeCompletionContext;
import org.netbeans.modules.csl.api.CodeCompletionHandler;
import org.netbeans.modules.csl.api.CodeCompletionResult;
import static org.netbeans.modules.csl.api.ElementKind.*;
import static org.netbeans.modules.csl.api.Modifier.*;
import org.netbeans.modules.csl.spi.ParserResult;

/**
 *
 */
public class RustCodeCompletionHandlerTest {

    private RustCodeCompletionHandler completionHandler;

    @Before
    public void setUp() {
        completionHandler = new RustCodeCompletionHandler();
    }

    @Test
    public void shouldFindCompletionPrefix() {
        RustSourceSnapshot source = new RustSourceSnapshot();
        source.appendln("/// Say hello");
        source.appendln("fn greet() {");
        source.appendln("   println(\"hello\");");
        source.appendln("}");
        source.appendln("");
        source.appendln("fn main() {");
        source.appendln("   gr"); //caret is at: gr|
        source.appendln("}");
        NetbeansRustParserResult parseResult = source.parse();

        String prefix = completionHandler.getPrefix(parseResult, 68, true);
        assertThat(prefix, is("gr"));
    }

    @Test
    public void shouldSuggestMatchingMethod() {
        RustSourceSnapshot source = new RustSourceSnapshot();
        source.appendln("/// Say hello");
        source.appendln("fn greet() {");
        source.appendln("   println(\"hello\");");
        source.appendln("}");
        source.appendln("");
        source.appendln("fn main() {");
        source.appendln("   gr"); //Caret is at: gr|
        source.appendln("}");

        CodeCompletionResult completionResult = completionHandler.complete(completionContextFor(source, 68));
        assertThat(completionResult.getItems(), contains(completionProposal("greet", METHOD, STATIC)));
    }

    @Test
    public void shouldProvideDocumentationForMethod() {
        RustSourceSnapshot source = new RustSourceSnapshot();
        source.appendln("/// Say hello");
        source.appendln("fn greet() {");
        source.appendln("   println(\"hello\");");
        source.appendln("}");
        source.appendln("");
        source.appendln("fn main() {");
        source.appendln("   gr"); //Caret is at: gr|
        source.appendln("}");

        String documentation = completionHandler.document(source.parse(), new RustElementHandle("greet", range(14, 49), METHOD, EnumSet.of(STATIC)));
        assertThat(documentation, is("/// Say hello"));
    }

    private CodeCompletionContext completionContextFor(final RustSourceSnapshot source, final int caretOffset) {
        return new CodeCompletionContext() {
            @Override
            public int getCaretOffset() {
                return caretOffset;
            }

            @Override
            public ParserResult getParserResult() {
                return source.parse();
            }

            @Override
            public String getPrefix() {
                return completionHandler.getPrefix(getParserResult(), caretOffset, true);
            }

            @Override
            public CodeCompletionHandler.QueryType getQueryType() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isPrefixMatch() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isCaseSensitive() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }
}
