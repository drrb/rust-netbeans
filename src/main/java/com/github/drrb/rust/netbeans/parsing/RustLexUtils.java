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

import com.github.drrb.rust.netbeans.util.Option;
import javax.swing.text.Document;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;

public class RustLexUtils {

    public TokenSequence<RustTokenId> getRustTokenSequence(Document doc, int offset) {
        TokenHierarchy<Document> tokenHierarchy = TokenHierarchy.get(doc);
        TokenSequence<RustTokenId> topLevelTokenSequence = tokenHierarchy.tokenSequence(RustTokenId.language());
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
            if (tokenSequence.language() == RustTokenId.language()) {
                @SuppressWarnings("unchecked")
                TokenSequence<RustTokenId> embeddedTokenSequence = (TokenSequence<RustTokenId>) tokenSequence;
                return embeddedTokenSequence;
            }
        }
        return null;
    }

    public static Option<OffsetRustToken> getIdentifierAt(int caretOffset, ParserResult info) {
        TokenHierarchy<?> tokenHierarchy = info.getSnapshot().getTokenHierarchy();
        return getIdentifierAt(caretOffset, tokenHierarchy);
    }

    public static Option<OffsetRustToken> getIdentifierAt(int caretOffset, TokenHierarchy<?> tokenHierarchy) {
        TokenSequence<RustTokenId> tokenSequence = tokenHierarchy.tokenSequence(RustTokenId.language());
        Option<OffsetRustToken> tokenAtOffset = offsetTokenAt(caretOffset, tokenSequence);
        if (tokenAtOffset.isNot()) {
            return Option.none();
        }

        if (tokenAtOffset.value().id() == RustTokenId.IDENT) {
            return tokenAtOffset;
        } else if (caretOffset > 0) {
            Option<OffsetRustToken> tokenBeforeOffset = offsetTokenAt(caretOffset - 1, tokenSequence);
            if (tokenBeforeOffset.is() && tokenBeforeOffset.value().id() == RustTokenId.IDENT) {
                return tokenBeforeOffset;
            } else {
                return Option.none();
            }
        } else {
            return Option.none();
        }
    }

    private static Option<OffsetRustToken> offsetTokenAt(int caretPosition, TokenSequence<RustTokenId> tokenSequence) {
        tokenSequence.move(caretPosition);
        if (tokenSequence.moveNext()) {
            return Option.is(OffsetRustToken.atCurrentLocation(tokenSequence));
        } else {
            return Option.none();
        }
    }

    public static OffsetRange offsetRangeFor(ParserRuleContext context) {
        return offsetRangeBetween(context.getStart(), context.getStop());
    }

    public static OffsetRange offsetRangeFor(TerminalNode node) {
        return offsetRangeBetween(node, node);
    }

    public static OffsetRange offsetRangeBetween(TerminalNode start, TerminalNode end) {
        return offsetRangeBetween(start.getSymbol(), end.getSymbol());
    }

    public static OffsetRange offsetRangeBetween(Token start, Token end) {
        return range(start.getStartIndex(), end.getStopIndex() + 1);
    }

    public static OffsetRange range(int start, int end) {
        return new OffsetRange(start, end);
    }
}
