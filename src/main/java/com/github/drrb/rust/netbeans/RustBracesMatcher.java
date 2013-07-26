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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;

/**
 *
 */
public class RustBracesMatcher implements BracesMatcher {

    private static final Logger LOGGER = Logger.getLogger(RustBracesMatcher.class.getName());
    private final RustLexUtils rustLexUtils;

    private enum BracePair {

        PARENS(RustTokenId.LPAREN, RustTokenId.RPAREN),
        BRACES(RustTokenId.LBRACE, RustTokenId.RBRACE),
        BRACKETS(RustTokenId.LBRACKET, RustTokenId.RBRACKET),
        ANGLES(RustTokenId.LT, RustTokenId.GT);
        final RustTokenId open;
        final RustTokenId close;

        private BracePair(RustTokenId open, RustTokenId close) {
            this.open = open;
            this.close = close;
        }
    }
    private final MatcherContext context;

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
                return getBraceAtOffset(tokenSequence, offset);
            }
        } finally {
            document.readUnlock();
        }
    }

    private int[] getBraceAtOffset(TokenSequence<RustTokenId> tokenSequence, int offset) {
        tokenSequence.move(offset);
        if (tokenSequence.moveNext()) {
            Token<RustTokenId> token = tokenSequence.token();
            for (BracePair bracePair : BracePair.values()) {
                if (token.id() == bracePair.open || token.id() == bracePair.close) {
                    return new int[]{tokenSequence.offset(), tokenSequence.offset() + token.length()};
                }
            }
        } else {
            LOGGER.log(Level.WARNING, "No token at offset {0}", offset);
        }
        return null;
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
                return getBraceMatchingTheOneAtOffset(tokenSequence, offset);
            }
        } finally {
            document.readUnlock();
        }
    }

    private int[] getBraceMatchingTheOneAtOffset(TokenSequence<RustTokenId> tokenSequence, int offset) {
        tokenSequence.move(offset);
        if (tokenSequence.moveNext()) {
            Token<RustTokenId> token = tokenSequence.token();
            OffsetRange offsetRange;
            for (BracePair bracePair : BracePair.values()) {
                if (token.id() == bracePair.open) {
                    offsetRange = findForward(tokenSequence, bracePair.open, bracePair.close);
                    return new int[]{offsetRange.getStart(), offsetRange.getEnd()};
                } else if (token.id() == bracePair.close) {
                    offsetRange = findBackward(tokenSequence, bracePair.open, bracePair.close);
                    return new int[]{offsetRange.getStart(), offsetRange.getEnd()};
                }
            }
        } else {
            LOGGER.log(Level.WARNING, "No token at offset {0}", offset);
        }
        return null;
    }

    private static OffsetRange findForward(TokenSequence<? extends RustTokenId> ts, RustTokenId up, RustTokenId down) {
        int balance = 0;

        while (ts.moveNext()) {
            Token<? extends RustTokenId> token = ts.token();
            if (token.id() == up) {
                balance++;
            } else if (token.id() == down) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }
                balance--;
            }
        }

        return OffsetRange.NONE;
    }

    private static OffsetRange findBackward(TokenSequence<? extends RustTokenId> ts, RustTokenId up, RustTokenId down) {
        int balance = 0;

        while (ts.movePrevious()) {
            Token<? extends RustTokenId> token = ts.token();
            if (token.id() == up) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }
                balance++;
            } else if (token.id() == down) {
                balance--;
            }
        }

        return OffsetRange.NONE;
    }

    @MimeRegistration(mimeType = RustLanguage.MIME_TYPE, service = BracesMatcherFactory.class)
    public static class Factory implements BracesMatcherFactory {

        @Override
        public BracesMatcher createMatcher(MatcherContext context) {
            return new RustBracesMatcher(context, new RustLexUtils());
        }
    }
}
