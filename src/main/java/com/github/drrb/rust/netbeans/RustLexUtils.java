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
package com.github.drrb.rust.netbeans;

import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;

public class RustLexUtils {

    public TokenSequence<RustTokenId> getRustTokenSequence(Document doc, int offset) {
        TokenHierarchy<Document> tokenHierarchy = TokenHierarchy.get(doc);
        TokenSequence<RustTokenId> topLevelTokenSequence = tokenHierarchy.tokenSequence(RustTokenId.getLanguage());
        if (topLevelTokenSequence != null) {
            return topLevelTokenSequence;
        }

        TokenSequence<RustTokenId> embeddedRustTokenSequence = getEmbeddedRustTokenSequence(tokenHierarchy, offset, true);
        if (embeddedRustTokenSequence != null) {
            return embeddedRustTokenSequence;
        }

        return getEmbeddedRustTokenSequence(tokenHierarchy, offset, false);
    }

    private TokenSequence<RustTokenId> getEmbeddedRustTokenSequence(TokenHierarchy<Document> tokenHierarchy, int offset, boolean backwardBias) {
        for (TokenSequence<? extends TokenId> tokenSequence : tokenHierarchy.embeddedTokenSequences(offset, backwardBias)) {
            if (tokenSequence.language() == RustTokenId.getLanguage()) {
                @SuppressWarnings("unchecked")
                TokenSequence<RustTokenId> embeddedTokenSequence = (TokenSequence<RustTokenId>) tokenSequence;
                return embeddedTokenSequence;
            }
        }
        return null;
    }
}
