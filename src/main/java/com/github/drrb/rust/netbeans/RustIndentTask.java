/*
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

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ExtraLock;
import org.netbeans.modules.editor.indent.spi.IndentTask;
import org.openide.awt.StatusDisplayer;

public class RustIndentTask implements IndentTask {

    private final Context context;

    RustIndentTask(Context context) {
        this.context = context;
    }

    @Override
    public void reindent() throws BadLocationException {
        StatusDisplayer.getDefault().setStatusText("Indenting...");
        Document document = context.document();
        int lineStart = context.lineStartOffset(context.startOffset());
        int previousLineStart = context.lineStartOffset(lineStart - 1);
        int previousLineIndent = context.lineIndent(previousLineStart);
        context.modifyIndent(lineStart, previousLineIndent + 4);
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
