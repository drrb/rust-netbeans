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
package com.github.drrb.rust.netbeans.keypress;

import com.github.drrb.rust.netbeans.RustLanguage;
import com.github.drrb.rust.netbeans.parsing.RustLexUtils;
import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.indent.api.IndentUtils;
import org.netbeans.spi.editor.typinghooks.TypedBreakInterceptor;

import javax.swing.text.BadLocationException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.drrb.rust.netbeans.parsing.RustTokenId.*;

/**
 *
 */
public class RustBreakInterceptor implements TypedBreakInterceptor {

    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    @Override
    public boolean beforeInsert(Context context) throws BadLocationException {
        cancelled.set(false);
        return false;
    }

    @Override
    public void insert(MutableContext ctx) throws BadLocationException {
        if (cancelled.get()) return;

        ContextHolder context = new ContextHolder(ctx);

        if (context.previousTokenKind() != LEFT_BRACE) {
            return; // Only insert a close brace after an open brace
        } else if (context.nextRowIndent() > context.currentRowIndent()) {
            return; // There's already stuff in this block
        } else if (context.nextTokenKind() == RIGHT_BRACE && context.currentRowIndent() == context.nextRowIndent()) {
            return; // There's already a closing brace
        }

        ctx.setText("\n\n" + context.currentRowIndentString() + "}", 0, 1);
    }

    @Override
    public void afterInsert(Context context) throws BadLocationException {
    }

    @Override
    public void cancelled(Context context) {
        cancelled.set(true);
    }

    private static class ContextHolder {

        private final MutableContext context;
        private final TokenSequence<RustTokenId> tokenSequence;
        private Integer currentRowIndent;
        private Integer nextRowIndent;

        private ContextHolder(MutableContext context) {
            this.context = context;
            this.tokenSequence = new RustLexUtils().getRustTokenSequence(context.getDocument(), context.getCaretOffset());
            tokenSequence.move(context.getCaretOffset());
        }

        private RustTokenId previousTokenKind() {
            return findNonWhitespaceToken(Direction.BACKWARD);
        }

        private RustTokenId nextTokenKind() {
            return findNonWhitespaceToken(Direction.FORWARD);
        }

        private String currentRowIndentString() throws BadLocationException {
            return IndentUtils.createIndentString(context.getDocument(), currentRowIndent());
        }

        private int currentRowIndent() throws BadLocationException {
            if (currentRowIndent == null) {
                int currentRowStart = IndentUtils.lineStartOffset(context.getDocument(), context.getCaretOffset());
                currentRowIndent = IndentUtils.lineIndent(context.getDocument(), currentRowStart);
            }
            return currentRowIndent;
        }

        private int nextRowIndent() throws BadLocationException {
            if (nextRowIndent == null) {
                int currentRowEnd = Utilities.getRowEnd(context.getComponent(), context.getCaretOffset());
                int nextRowStart = currentRowEnd + 1;
                nextRowIndent = IndentUtils.lineIndent(context.getDocument(), nextRowStart);
            }
            return nextRowIndent;
        }

        private RustTokenId findNonWhitespaceToken(Direction direction) {
            while (direction.move(tokenSequence)) {
                RustTokenId nextTokenKind = tokenSequence.token().id();
                if (nextTokenKind != WHITESPACE) {
                    return nextTokenKind;
                }
            }
            return null;
        }
    }

    private enum Direction {

        FORWARD {
            @Override
            public boolean move(TokenSequence<? extends TokenId> tokenSequence) {
                return tokenSequence.moveNext();
            }
        },
        BACKWARD {
            @Override
            public boolean move(TokenSequence<? extends TokenId> tokenSequence) {
                return tokenSequence.movePrevious();
            }
        };

        public abstract boolean move(TokenSequence<? extends TokenId> tokenSequence);
    }

    @MimeRegistration(mimeType = RustLanguage.MIME_TYPE, service = TypedBreakInterceptor.Factory.class)
    public static class Factory implements TypedBreakInterceptor.Factory {

        @Override
        public TypedBreakInterceptor createTypedBreakInterceptor(MimePath mimePath) {
            if (RustLanguage.MIME_TYPE.equals(mimePath.getPath())) {
                return new RustBreakInterceptor();
            } else {
                return null;
            }
        }
    }
}
