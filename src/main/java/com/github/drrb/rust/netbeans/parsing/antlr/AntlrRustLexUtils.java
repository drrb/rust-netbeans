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

import com.github.drrb.rust.netbeans.parsing.*;
import com.github.drrb.rust.netbeans.util.Option;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AntlrRustLexUtils {
    private static final Logger LOG = Logger.getLogger(AntlrRustLexUtils.class.getName());

    public TokenSequence<AntlrTokenID> getRustTokenSequence(Document doc, int offset) {
        TokenHierarchy<?> tokenHierarchy = TokenHierarchy.get(doc);
        TokenSequence<AntlrTokenID> topLevelTokenSequence = tokenHierarchy.tokenSequence(AntlrRustLanguageHierarchy.INSTANCE.language());
        if (topLevelTokenSequence != null) {
            return topLevelTokenSequence;
        }

        TokenSequence<AntlrTokenID> embeddedRustTokenSequence = getEmbeddedRustTokenSequence(tokenHierarchy, offset, true);
        if (embeddedRustTokenSequence != null) {
            return embeddedRustTokenSequence;
        }

        TokenSequence<AntlrTokenID> embeddedRustTokenSequenceForwards = getEmbeddedRustTokenSequence(tokenHierarchy, offset, false);
        if (embeddedRustTokenSequenceForwards != null) {
            return embeddedRustTokenSequenceForwards;
        }

        try {
            LOG.warning("Couldn't get Rust token sequence for document. Falling back to lexing it ourselves.");
            tokenHierarchy = TokenHierarchy.create(doc.getText(0, doc.getLength()), AntlrRustLanguageHierarchy.INSTANCE.language());
            return tokenHierarchy.tokenSequence(AntlrRustLanguageHierarchy.INSTANCE.language());
        } catch (BadLocationException ex) {
            LOG.log(Level.WARNING, "Couldn't get Rust token sequence for document at all!", ex);
            return null;
        }
    }

    private TokenSequence<AntlrTokenID> getEmbeddedRustTokenSequence(TokenHierarchy<?> tokenHierarchy, int offset, boolean backwardBias) {
        for (TokenSequence<? extends TokenId> tokenSequence : tokenHierarchy.embeddedTokenSequences(offset, backwardBias)) {
            if (tokenSequence.language() == AntlrRustLanguageHierarchy.INSTANCE.language()) {
                @SuppressWarnings("unchecked")
                TokenSequence<AntlrTokenID> embeddedTokenSequence = (TokenSequence<AntlrTokenID>) tokenSequence;
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
        TokenSequence<?> tokenSequence = tokenHierarchy.tokenSequence(AntlrRustLanguageHierarchy.INSTANCE.language());
        Option<OffsetRustToken> tokenAtOffset = offsetTokenAt(caretOffset, tokenSequence);
        if (tokenAtOffset.isNot()) {
            return Option.none();
        }
        TokenId identId = CommonRustTokenIDs.identifierTokenID();
        if (tokenAtOffset.value().id() == identId) {
            return tokenAtOffset;
        } else if (caretOffset > 0) {
            Option<OffsetRustToken> tokenBeforeOffset = offsetTokenAt(caretOffset - 1, tokenSequence);
            if (tokenBeforeOffset.is() && tokenBeforeOffset.value().id() == identId) {
                return tokenBeforeOffset;
            } else {
                return Option.none();
            }
        } else {
            return Option.none();
        }
    }

    private static Option<OffsetRustToken> offsetTokenAt(int caretPosition, TokenSequence<?> tokenSequence) {
        tokenSequence.move(caretPosition);
        if (tokenSequence.moveNext()) {
            return Option.is(OffsetRustToken.atCurrentLocation(tokenSequence));
        } else {
            return Option.none();
        }
    }

    public static OffsetRange range(int start, int end) {
        return new OffsetRange(start, end);
    }
}
