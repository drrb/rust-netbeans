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
package com.github.drrb.rust.netbeans.formatting;

import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import static java.lang.Character.isWhitespace;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.Formatter;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.spi.lexer.MutableTextInput;
import org.openide.util.Exceptions;

/**
 *
 */
public class RustFormatter implements Formatter {

    @Override
    public void reformat(Context context, ParserResult compilationInfo) {
        NetbeansRustParserResult parseResult = (NetbeansRustParser.NetbeansRustParserResult) compilationInfo;
        final BaseDocument document = (BaseDocument) context.document();
        final RustDocumentFormatter formatter = new RustDocumentFormatter(this, parseResult, document, context);
        document.runAtomic(() -> {
            // Not sure why, but setActive(false)/(true) makes the formatting a lot faster
            MutableTextInput mti = (MutableTextInput) document.getProperty(MutableTextInput.class);
            try {
                mti.tokenHierarchyControl().setActive(false);
                formatter.format();
            } finally {
                mti.tokenHierarchyControl().setActive(true);
            }
        });
    }

    @Override
    public void reindent(Context context) {
        try {
            int lineStart = context.lineStartOffset(context.startOffset());
            int previousLineEnd = lineStart - 1;
            int previousLineStart = context.lineStartOffset(previousLineEnd);
            int previousLineIndent = context.lineIndent(previousLineStart);
            Document document = context.document();
            char lastCharOfPreviousLine = lastNonWhiteCharacter(previousLineStart, previousLineEnd, document);
            int targetIndent;
            if (lastCharOfPreviousLine == '{') {
                targetIndent = previousLineIndent + indentSize();
            } else {
                targetIndent = previousLineIndent;
            }
            context.modifyIndent(lineStart, targetIndent);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
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
    public boolean needsParserResult() {
        return true;
    }

    @Override
    public int indentSize() {
        return 4;
    }

    @Override
    public int hangingIndentSize() {
        return 8;
    }
}
