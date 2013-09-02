/**
 * Copyright (C) 2013 drrb
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.modules.csl.api.Formatter;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.editor.indent.spi.Context;
import org.openide.util.Exceptions;

/**
 *
 */
public class RustFormatter implements Formatter {

    @Override
    public void reformat(Context context, ParserResult compilationInfo) {
        Logger.getLogger(RustFormatter.class.getName()).log(Level.WARNING, "reformat: {0} - {1}, caret = {2}", new Object[]{context.startOffset(), context.endOffset(), context.caretOffset()});
        NetbeansRustParserResult parseResult = (NetbeansRustParser.NetbeansRustParserResult) compilationInfo;
        BaseDocument document = (BaseDocument) context.document();
        final com.github.drrb.rust.netbeans.formatting.Formatter formatter = new com.github.drrb.rust.netbeans.formatting.Formatter(this, parseResult, document, context);
        //TODO:
        //In addition to locking, PHP also does
        //MutableTextInput mti = (MutableTextInput) doc.getProperty(MutableTextInput.class);
        //try {
        //    mti.tokenHierarchyControl().setActive(false);
        //    <format>
        //} finally {
        //    mti.tokenHierarchyControl().setActive(true);
        //}
        //TODO: do we need the write lock the whole time? Looking for the braces just needs a read lock, but it seems like we can't get the write lock when we already have the read lock
        document.runAtomic(new Runnable() {
            @Override
            public void run() {
                formatter.format();
            }
        });
    }

    @Override
    public void reindent(Context context) {
        Logger.getLogger(RustFormatter.class.getName()).log(Level.WARNING, "reindent: {0} - {1}, {2}", new Object[]{context.startOffset(), context.endOffset(), context.caretOffset()});
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

    private String renderPosition(AbstractDocument document, int offset) {
        return "(between '" + renderCharAt(document, offset - 1) + "' and '" + renderCharAt(document, offset) + "')";
    }

    private String renderCharAt(AbstractDocument document, int offset) {
        char c = DocumentUtilities.getText(document).charAt(offset);
        switch (c) {
            case '\n':
                return "\\n";
            default:
                return String.valueOf(c);
        }
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
