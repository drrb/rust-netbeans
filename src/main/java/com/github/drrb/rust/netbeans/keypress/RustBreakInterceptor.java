/**
 * Copyright (C) 2015 drrb
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
import static com.github.drrb.rust.netbeans.parsing.RustTokenId.*;
import javax.swing.text.BadLocationException;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.DocumentUtilities;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.csl.spi.GsfUtilities;
import org.netbeans.modules.editor.indent.api.IndentUtils;
import org.netbeans.spi.editor.typinghooks.TypedBreakInterceptor;
import org.openide.text.NbDocument;

/**
 *
 */
public class RustBreakInterceptor implements TypedBreakInterceptor {

    @Override
    public boolean beforeInsert(Context context) throws BadLocationException {
        return false;
    }

    @Override
    @SuppressWarnings("UnnecessaryContinue")
    public void insert(MutableContext context) throws BadLocationException {
        BaseDocument document = (BaseDocument) context.getDocument();
        int caretOffset = context.getCaretOffset();
        TokenSequence<RustTokenId> tokenSequence = new RustLexUtils().getRustTokenSequence(document, caretOffset);
        tokenSequence.move(caretOffset);
        tokenSequence.movePrevious();
        Token<RustTokenId> previousToken = tokenSequence.token();
        if (previousToken.id() != OPEN_BRACE) {
            return;
        }
        int currentRowEnd = Utilities.getRowEnd(document, caretOffset);
        int currentIndent = IndentUtils.lineIndent(document, IndentUtils.lineStartOffset(document, caretOffset));
        int nextIndent = IndentUtils.lineIndent(document, IndentUtils.lineStartOffset(document, currentRowEnd + 1));
        if (nextIndent > currentIndent) {
            return; // There's already stuff in this block
        }
        while (tokenSequence.moveNext()) {
            if (tokenSequence.token().id() == WHITESPACE) {
                continue;
            } else if (tokenSequence.token().id() == CLOSE_BRACE && currentIndent == nextIndent) {
                return;
            } else {
                break;
            }
        }
        String indent = IndentUtils.createIndentString(document, currentIndent);
        context.setText("\n\n" + indent + "}", 0, 1);
    }

    @Override
    public void afterInsert(Context context) throws BadLocationException {
    }

    @Override
    public void cancelled(Context context) {
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
