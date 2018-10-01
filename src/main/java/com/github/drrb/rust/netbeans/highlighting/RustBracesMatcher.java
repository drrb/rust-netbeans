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
import com.github.drrb.rust.netbeans.parsing.antlr.AntlrRustLexUtils;
import com.github.drrb.rust.netbeans.parsing.antlr.AntlrTokenID;
import static com.github.drrb.rust.netbeans.parsing.antlr.CommonRustTokenIDs.leftAngleBracket;
import static com.github.drrb.rust.netbeans.parsing.antlr.CommonRustTokenIDs.leftBrace;
import static com.github.drrb.rust.netbeans.parsing.antlr.CommonRustTokenIDs.leftBracket;
import static com.github.drrb.rust.netbeans.parsing.antlr.CommonRustTokenIDs.rightAngleBracket;
import static com.github.drrb.rust.netbeans.parsing.antlr.CommonRustTokenIDs.rightBrace;
import static com.github.drrb.rust.netbeans.parsing.antlr.CommonRustTokenIDs.rightBracket;
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
import org.netbeans.api.lexer.TokenId;
import static com.github.drrb.rust.netbeans.parsing.antlr.CommonRustTokenIDs.leftParen;
import static com.github.drrb.rust.netbeans.parsing.antlr.CommonRustTokenIDs.rightParen;

/**
 *
 */
public class RustBracesMatcher implements BracesMatcher {

    private static final Logger LOGGER = Logger.getLogger(RustBracesMatcher.class.getName());

    @MimeRegistration(mimeType = RustLanguage.MIME_TYPE, service = BracesMatcherFactory.class)
    public static class Factory implements BracesMatcherFactory {

        @Override
        public BracesMatcher createMatcher(MatcherContext context) {
            return new RustBracesMatcher(context, new AntlrRustLexUtils());
            // TODO: is our implementation better than just doing this?:
            //return BracesMatcherSupport.defaultMatcher(context, -1, -1);
            // Probably, because it's dealing with tokens instead of characters
            // but let's keep the option open in case it does stuff ours doesn't
        }
    }
    private final MatcherContext context;
    private final AntlrRustLexUtils rustLexUtils;

    public RustBracesMatcher(MatcherContext context, AntlrRustLexUtils rustLexUtils) {
        this.context = context;
        this.rustLexUtils = rustLexUtils;
    }

    @Override
    public int[] findOrigin() throws InterruptedException, BadLocationException {
        AbstractDocument document = (AbstractDocument) context.getDocument();
        document.readLock();
        try {
            int offset = context.getSearchOffset();
            TokenSequence<?> tokenSequence = rustLexUtils.getRustTokenSequence(document, offset);
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

    private OffsetRange getBraceAtOffset(TokenSequence<?> tokenSequence, int offset) {
        tokenSequence.move(offset);
        if (tokenSequence.moveNext()) {
            Token<?> token = tokenSequence.token();
            for (BracePair bracePair : AntlrBracePair.values()) {
                if (token.id() == bracePair.open() || token.id() == bracePair.close()) {
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
            TokenSequence<?> tokenSequence = rustLexUtils.getRustTokenSequence(document, offset);
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

    private OffsetRange getBraceMatchingTheOneAtOffset(TokenSequence<?> tokenSequence, int offset) {
        tokenSequence.move(offset);
        if (tokenSequence.moveNext()) {
            Token<?> token = tokenSequence.token();
            for (BracePair bracePair : AntlrBracePair.values()) {
                if (token.id() == bracePair.open()) {
                    return findCloseBraceForward(tokenSequence, bracePair);
                } else if (token.id() == bracePair.close()) {
                    return findOpenBraceBackward(tokenSequence, bracePair);
                }
            }
        } else {
            LOGGER.log(Level.WARNING, "No token at offset {0}", offset);
        }
        return OffsetRange.NONE;
    }

    private static OffsetRange findCloseBraceForward(TokenSequence<?> tokenSequence, BracePair bracePair) {
        int balance = 0;

        while (tokenSequence.moveNext()) {
            Token<?> token = tokenSequence.token();
            if (token.id() == bracePair.open()) {
                balance++;
            } else if (token.id() == bracePair.close()) {
                if (balance == 0) {
                    return OffsetRange.ofCurrentToken(tokenSequence);
                }
                balance--;
            }
        }

        return OffsetRange.NONE;
    }

    private static OffsetRange findOpenBraceBackward(TokenSequence<?> tokenSequence, BracePair bracePair) {
        int balance = 0;

        while (tokenSequence.movePrevious()) {
            Token<?> token = tokenSequence.token();
            if (token.id() == bracePair.open()) {
                if (balance == 0) {
                    return OffsetRange.ofCurrentToken(tokenSequence);
                }
                balance++;
            } else if (token.id() == bracePair.close()) {
                balance--;
            }
        }

        return OffsetRange.NONE;
    }

    interface BracePair {
        TokenId open();
        TokenId close();
    }

    private enum AntlrBracePair implements BracePair {

        PARENS(leftParen(), rightParen()),
        BRACES(leftBrace(), rightBrace()),
        BRACKETS(leftBracket(), rightBracket()),
        ANGLES(leftAngleBracket(), rightAngleBracket());
        final TokenId open;
        final TokenId close;

        private AntlrBracePair(AntlrTokenID open, AntlrTokenID close) {
            this.open = open;
            this.close = close;
        }

        @Override
        public TokenId open() {
            return open;
        }

        @Override
        public TokenId close() {
            return close;
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
