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
package com.github.drrb.rust.netbeans.highlighting;

import com.github.drrb.rust.netbeans.RustLanguage;
import com.github.drrb.rust.netbeans.parsing.RustLexUtils;
import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class RustBracesMatcher implements BracesMatcher {

    private static final Logger LOGGER = Logger.getLogger(RustBracesMatcher.class.getName());

    @MimeRegistration(mimeType = RustLanguage.MIME_TYPE, service = BracesMatcherFactory.class)
    public static class Factory implements BracesMatcherFactory {

        @Override
        public BracesMatcher createMatcher(MatcherContext context) {
            return new RustBracesMatcher(context, new RustLexUtils());
            // TODO: is our implementation better than just doing this?:
            //return BracesMatcherSupport.defaultMatcher(context, -1, -1);
            // Probably, because it's dealing with tokens instead of characters
            // but let's keep the option open in case it does stuff ours doesn't
        }
    }
    private final MatcherContext context;
    private final RustLexUtils rustLexUtils;

    public RustBracesMatcher(MatcherContext context, RustLexUtils rustLexUtils) {
        this.context = context;
        this.rustLexUtils = rustLexUtils;
    }

    @Override
    public int[] findOrigin() throws InterruptedException, BadLocationException {
        AbstractDocument document = (AbstractDocument) context.getDocument();
        document.readLock();
        try {
            int offset = context.getSearchOffset();
            TokenSequence<RustTokenId> tokenSequence = rustLexUtils.getRustTokenSequence(document, offset);
            if (tokenSequence == null) {
                LOGGER.warning("Couldn't get Rust token sequence for braces matching");
                return null;
            } else {
                return getBraceAtOffset(tokenSequence, offset).ends();
            }
        } finally {
            document.readUnlock();
        }
    }

    private OffsetRange getBraceAtOffset(TokenSequence<RustTokenId> tokenSequence, int offset) {
        tokenSequence.move(offset);
        if (tokenSequence.moveNext()) {
            Token<RustTokenId> token = tokenSequence.token();
            for (BracePair bracePair : BracePair.values()) {
                if (token.id() == bracePair.open || token.id() == bracePair.close) {
                    return OffsetRange.ofCurrentToken(tokenSequence);
                }
            }
        } else {
            LOGGER.log(Level.WARNING, "No token at offset {0}", offset);
        }
        return OffsetRange.NONE;
    }

    @Override
    public int[] findMatches() throws InterruptedException, BadLocationException {
        AbstractDocument document = (AbstractDocument) context.getDocument();
        document.readLock();
        try {
            int offset = context.getSearchOffset();
            TokenSequence<RustTokenId> tokenSequence = rustLexUtils.getRustTokenSequence(document, offset);
            if (tokenSequence == null) {
                LOGGER.warning("Couldn't get Rust token sequence for braces matching");
                return null;
            } else {
                return getBraceMatchingTheOneAtOffset(tokenSequence, offset).ends();
            }
        } finally {
            document.readUnlock();
        }
    }

    private OffsetRange getBraceMatchingTheOneAtOffset(TokenSequence<RustTokenId> tokenSequence, int offset) {
        tokenSequence.move(offset);
        if (tokenSequence.moveNext()) {
            Token<RustTokenId> token = tokenSequence.token();
            for (BracePair bracePair : BracePair.values()) {
                if (token.id() == bracePair.open) {
                    return findCloseBraceForward(tokenSequence, bracePair);
                } else if (token.id() == bracePair.close) {
                    return findOpenBraceBackward(tokenSequence, bracePair);
                }
            }
        } else {
            LOGGER.log(Level.WARNING, "No token at offset {0}", offset);
        }
        return OffsetRange.NONE;
    }

    private static OffsetRange findCloseBraceForward(TokenSequence<? extends RustTokenId> tokenSequence, BracePair bracePair) {
        int balance = 0;

        while (tokenSequence.moveNext()) {
            Token<? extends RustTokenId> token = tokenSequence.token();
            if (token.id() == bracePair.open) {
                balance++;
            } else if (token.id() == bracePair.close) {
                if (balance == 0) {
                    return OffsetRange.ofCurrentToken(tokenSequence);
                }
                balance--;
            }
        }

        return OffsetRange.NONE;
    }

    private static OffsetRange findOpenBraceBackward(TokenSequence<? extends RustTokenId> tokenSequence, BracePair bracePair) {
        int balance = 0;

        while (tokenSequence.movePrevious()) {
            Token<? extends RustTokenId> token = tokenSequence.token();
            if (token.id() == bracePair.open) {
                if (balance == 0) {
                    return OffsetRange.ofCurrentToken(tokenSequence);
                }
                balance++;
            } else if (token.id() == bracePair.close) {
                balance--;
            }
        }

        return OffsetRange.NONE;
    }

    private enum BracePair {

        PARENS(RustTokenId.LEFT_PAREN, RustTokenId.RIGHT_PAREN),
        BRACES(RustTokenId.LEFT_BRACE, RustTokenId.RIGHT_BRACE),
        BRACKETS(RustTokenId.LEFT_BRACKET, RustTokenId.RIGHT_BRACKET),
        ANGLES(RustTokenId.LEFT_ANGLE_BRACKET, RustTokenId.RIGHT_ANGLE_BRACKET);
        final RustTokenId open;
        final RustTokenId close;

        private BracePair(RustTokenId open, RustTokenId close) {
            this.open = open;
            this.close = close;
        }
    }

    private static class OffsetRange {

        static final OffsetRange NONE = new OffsetRange(-1, -1);
        private final int start;
        private final int end;

        OffsetRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        int[] ends() {
            if (this == NONE) {
                return null;
            } else {
                return new int[]{start, end};
            }
        }

        static OffsetRange ofCurrentToken(TokenSequence<?> tokenSequence) {
            Token<?> token = tokenSequence.token();
            return new OffsetRange(tokenSequence.offset(), tokenSequence.offset() + token.length());
        }
    }
}
