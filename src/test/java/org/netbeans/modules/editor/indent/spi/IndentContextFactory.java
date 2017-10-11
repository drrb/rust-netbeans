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
package org.netbeans.modules.editor.indent.spi;

import javax.swing.text.Document;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.editor.indent.MimeItemFactory;
import org.netbeans.modules.editor.indent.TaskHandler;

public class IndentContextFactory {

    public static Builder createFor(Document document) {
        return new Builder(document);
    }

    public static class Builder {

        private final Document document;
        private OffsetRange offsetRange;
        private int caretOffset;

        private Builder(Document document) {
            this.document = document;
            offsetRange = new OffsetRange(document.getStartPosition().getOffset(), document.getEndPosition().getOffset());
            caretOffset = 0;
        }

        public Builder withOffsetRange(int startOffset, int endOffset) {
            this.offsetRange = new OffsetRange(startOffset, endOffset);
            return this;
        }

        public Builder withCaretOffset(int caretOffset) {
            this.caretOffset = caretOffset;
            return this;
        }

        public Context build() {
            TaskHandler.MimeItem mimeItem = MimeItemFactory.create(document, offsetRange, caretOffset);
            return new Context(mimeItem);
        }
    }

    private IndentContextFactory() {
    }
}
