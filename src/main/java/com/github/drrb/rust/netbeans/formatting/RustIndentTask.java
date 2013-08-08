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
package com.github.drrb.rust.netbeans.formatting;

import com.github.drrb.rust.netbeans.RustLanguage;
import static java.lang.Character.isWhitespace;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ExtraLock;
import org.netbeans.modules.editor.indent.spi.IndentTask;

public class RustIndentTask implements IndentTask {

    private final Context context;

    protected RustIndentTask(Context context) {
        this.context = context;
    }

    @Override
    public void reindent() throws BadLocationException {
        int lineStart = context.lineStartOffset(context.startOffset());
        int previousLineEnd = lineStart - 1;
        int previousLineStart = context.lineStartOffset(previousLineEnd);
        int previousLineIndent = context.lineIndent(previousLineStart);
        Document document = context.document();
        char lastCharOfPreviousLine = lastNonWhiteCharacter(previousLineStart, previousLineEnd, document);
        int targetIndent;
        if (lastCharOfPreviousLine == '{') {
            targetIndent = previousLineIndent + 4;
        } else {
            targetIndent = previousLineIndent;
        }
        context.modifyIndent(lineStart, targetIndent);
    }

    private static char lastNonWhiteCharacter(int lineStart, int lineEnd, Document document) throws BadLocationException {
        for (int i = lineEnd; i > lineStart; i--) {
            char character = document.getText(i - 1, 1).charAt(0);
            if (!isWhitespace(character)) {
                return character;
            }
        }
        return ' ';
    }

    @Override
    public ExtraLock indentLock() {
        return null;
    }

    @MimeRegistration(mimeType = RustLanguage.MIME_TYPE, service = IndentTask.Factory.class)
    public static class Factory implements IndentTask.Factory {

        @Override
        public IndentTask createTask(Context context) {
            return new RustIndentTask(context);
        }
    }
}
