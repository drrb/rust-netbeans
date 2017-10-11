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
package org.netbeans.modules.editor.indent;

import com.github.drrb.rust.netbeans.RustLanguage;
import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.editor.indent.TaskHandler.MimeItem;

/**
 *
 */
public class MimeItemFactory {

    public static TaskHandler.MimeItem create(Document document, OffsetRange offsetRange, int caretOffset) {
        TaskHandler taskHandler = new TaskHandler(true, document);
        AbsolutePosition startPos = new AbsolutePosition(offsetRange.getStart());
        AbsolutePosition endPos = new AbsolutePosition(offsetRange.getEnd());
        taskHandler.setGlobalBounds(startPos, endPos);
        try {
            taskHandler.setCaretOffset(caretOffset);
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
        LanguagePath languagePath = LanguagePath.get(RustTokenId.language());
        return new MimeItem(taskHandler, MimePath.get(RustLanguage.MIME_TYPE), languagePath);
    }

    private static class AbsolutePosition implements Position {

        private final int offset;

        AbsolutePosition(int offset) {
            this.offset = offset;
        }

        @Override
        public int getOffset() {
            return offset;
        }
    }

    private MimeItemFactory() {
    }
}
