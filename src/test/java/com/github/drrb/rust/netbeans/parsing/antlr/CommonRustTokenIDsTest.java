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
package com.github.drrb.rust.netbeans.parsing.antlr;

import com.github.drrb.rust.antlr.RustLexer;
import static com.github.drrb.rust.netbeans.parsing.antlr.AntlrTokenIDs.stripSingleQuotes;
import java.lang.reflect.Method;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import org.antlr.v4.runtime.Vocabulary;
import org.junit.Test;

/**
 *
 * @author Tim Boudreau
 */
public class CommonRustTokenIDsTest {

    Vocabulary vocab = RustLexer.VOCABULARY;

    @Test
    public void testOrdinals() {
        int max = vocab.getMaxTokenType() + 1;
        for (int i = 0; i < max; i++) {
            AntlrTokenID tok = CommonRustTokenIDs.forTokenType(i);
            assertNotNull(tok);
            assertEquals(i, tok.ordinal());
            assertEquals(stripSingleQuotes(vocab.getLiteralName(i)), tok.literalName());
            if (tok.literalName() != null) {
                AntlrTokenID test = CommonRustTokenIDs.forLiteralName(tok.literalName());
                assertSame("Got different token id for " + i, tok, test);
                if (tok.literalName().length() == 1) {
                    AntlrTokenID test2 = CommonRustTokenIDs.forSymbol(tok.literalName().charAt(0));
                    assertSame("Got different token id char '" + tok.literalName() + "'", tok, test2);
                }
            }
        }
    }

    @Test
    public void testStatics() throws Throwable {
        for (Method method : CommonRustTokenIDs.class.getMethods()) {
            if (method.getReturnType() == AntlrTokenID.class && method.getParameterCount() == 0) {
                AntlrTokenID id = (AntlrTokenID) method.invoke(null);
                assertNotNull(method.getName() + " returns null", id);
                if (id.literalName() != null && !"EOF".equals(id.literalName())) {
                    assertEquals(stripSingleQuotes(vocab.getLiteralName(id.ordinal())), id.literalName());
                }
            }
        }
    }


}
