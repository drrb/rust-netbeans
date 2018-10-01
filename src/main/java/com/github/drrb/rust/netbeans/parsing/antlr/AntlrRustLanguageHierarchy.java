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
package com.github.drrb.rust.netbeans.parsing.antlr;

import com.github.drrb.rust.antlr.RustParser;
import com.github.drrb.rust.netbeans.RustLanguage;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AntlrRustLanguageHierarchy extends LanguageHierarchy<AntlrTokenID> {

    public static final AntlrRustLanguageHierarchy INSTANCE = new AntlrRustLanguageHierarchy();

    final AntlrTokenIDs tokenIds;
    public AntlrRustLanguageHierarchy() {
        tokenIds = AntlrTokenIDs.forVocabulary(RustParser.VOCABULARY, RustAntlrLexer::categoryFor);
    }

    @Override
    protected Collection<AntlrTokenID> createTokenIds() {
        return tokenIds.all();
    }

    @Override
    protected Lexer<AntlrTokenID> createLexer(LexerRestartInfo<AntlrTokenID> info) {
        return new RustAntlrLexer(info);
    }

    @Override
    protected String mimeType() {
        return RustLanguage.MIME_TYPE;
    }

    @Override
    protected Map<String, Collection<AntlrTokenID>> createTokenCategories() {
        Map<String,Collection<AntlrTokenID>> result = new HashMap<>();
        for (AntlrTokenID id : CommonRustTokenIDs.all()) {
            Collection<AntlrTokenID> tokens = result.get(id.primaryCategory());
            if (tokens == null) {
                tokens = new HashSet<>();
                result.put(id.primaryCategory(), tokens);
            }
            tokens.add(id);
        }
        return result;
    }
}
