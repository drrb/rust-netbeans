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
import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import static java.lang.Character.isWhitespace;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.Formatter;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.parsing.api.Snapshot;
import org.openide.util.Exceptions;

/**
 *
 */
public class RustFormatter implements Formatter {

    @Override
    public void reformat(Context context, ParserResult compilationInfo) {
        Logger.getLogger(RustFormatter.class.getName()).log(Level.WARNING, "reformat: {0} - {1}, caret = {2}", new Object[]{context.startOffset(), context.endOffset(), context.caretOffset()});
        NetbeansRustParserResult parseResult = (NetbeansRustParser.NetbeansRustParserResult) compilationInfo;
        Snapshot snapshot = parseResult.getSnapshot();
        CharSequence text = snapshot.getText();
        TokenSequence<RustTokenId> tokenSequence = snapshot.getTokenHierarchy().tokenSequence(RustTokenId.language());
        tokenSequence.move(context.endOffset());
        final AbstractDocument document = (AbstractDocument) context.document();

        // Work backwards. Changing the document length while moving forwards wrecks offsets further ahead.
        while (tokenSequence.movePrevious()) {
            //TODO: test the limits here
            if (tokenSequence.offset() < context.startOffset()) {
                return;
            }
            Token<RustTokenId> token = tokenSequence.token();
            if (token.id() == RustTokenId.LBRACE) {
                final int startOfGap = tokenSequence.offset() + 1;  //LBRACE width = 1
                int nextCharPosition = startOfGap;
                char nextChar = text.charAt(nextCharPosition);
                while (Character.isWhitespace(nextChar)) {
                    nextCharPosition++;
                    nextChar = text.charAt(nextCharPosition);
                }
                int endOfGap = nextCharPosition; //We moved forward one extra to check
                final int lengthOfGap = endOfGap - startOfGap;
                try {
                    //TODO: do we need a lock?
                    System.out.format("replacing %s - %s with '\n'%n", startOfGap, endOfGap);
                    document.replace(startOfGap, lengthOfGap, "\n", null);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
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
