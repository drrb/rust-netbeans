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

import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.util.Exceptions;
import static com.github.drrb.rust.netbeans.RustCompletionItem.Type.*;

@MimeRegistration(mimeType = RustLanguage.MIME_TYPE, service = CompletionProvider.class)
public class RustCompletionProvider implements CompletionProvider {

    @Override
    public CompletionTask createTask(int queryType, JTextComponent textComponent) {

        if (queryType == CompletionProvider.COMPLETION_QUERY_TYPE) {
            return new AsyncCompletionTask(new RustCompletionQuery(), textComponent);
        } else {
            return null;
        }
    }

    @Override
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return 0; //Only offer completion when it's explicitly requested
    }

    private static class RustCompletionQuery extends AsyncCompletionQuery {

        @Override
        protected void query(CompletionResultSet completionResultSet, Document document, int caretOffset) {
            String filter = null;
            int startOffset = caretOffset - 1;

            try {
                final int lineStartOffset = DocUtil.getRowFirstNonWhite(document, caretOffset);
                final char[] line = document.getText(lineStartOffset, caretOffset - lineStartOffset).toCharArray();
                final int whiteOffset = indexOfWhite(line);
                filter = new String(line, whiteOffset + 1, line.length - whiteOffset - 1);
                if (whiteOffset > 0) {
                    startOffset = lineStartOffset + whiteOffset + 1;
                } else {
                    startOffset = lineStartOffset;
                }
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }

            Set<String> suggestions = new LinkedHashSet<String>();
            AbstractDocument doc = (AbstractDocument) document;
            doc.readLock();
            try {
                TokenSequence<RustTokenId> tokenSequence = new RustLexUtils().getRustTokenSequence(document, 0);
                tokenSequence.move(0);
                while (tokenSequence.moveNext()) {
                    Token<RustTokenId> token = tokenSequence.token();
                    int start = tokenSequence.offset();
                    int end = start + token.length();
                    String tokenText = token.text().toString();
                    if (token.id() == RustTokenId.IDENT
                            && caretOffset != end
                            && tokenText.startsWith(filter)) {
                        completionResultSet.addItem(new RustCompletionItem(FUNCTION, tokenText, startOffset, caretOffset));
                    }
                }
            } finally {
                doc.readUnlock();
            }
            for (RustKeyword keyword : RustKeyword.values()) {
                String keywordImage = keyword.image();
                if (keywordImage.startsWith(filter)) {
                    completionResultSet.addItem(new RustCompletionItem(KEYWORD, keywordImage, startOffset, caretOffset));
                }
            }

            completionResultSet.finish();
        }
    }

    static int indexOfWhite(char[] line) {
        int i = line.length;
        while (--i > -1) {
            final char c = line[i];
            if (Character.isWhitespace(c)) {
                return i;
            }
        }
        return -1;
    }
}