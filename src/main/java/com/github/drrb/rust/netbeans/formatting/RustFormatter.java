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
import static com.github.drrb.rust.netbeans.parsing.RustTokenId.*;
import static java.lang.Character.isWhitespace;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
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

    private static class CurlyBrace {

        final char type;
        final int offset;
        final int desiredIndent;

        CurlyBrace(char type, int offset, int desiredIndent) {
            this.type = type;
            this.offset = offset;
            this.desiredIndent = desiredIndent;
        }
    }

    @Override
    public void reformat(Context context, ParserResult compilationInfo) {
        List<CurlyBrace> curlyBraces = new LinkedList<CurlyBrace>();
        Logger.getLogger(RustFormatter.class.getName()).log(Level.WARNING, "reformat: {0} - {1}, caret = {2}", new Object[]{context.startOffset(), context.endOffset(), context.caretOffset()});
        NetbeansRustParserResult parseResult = (NetbeansRustParser.NetbeansRustParserResult) compilationInfo;
        Snapshot snapshot = parseResult.getSnapshot();
        CharSequence text = snapshot.getText();
        TokenSequence<RustTokenId> tokenSequence = snapshot.getTokenHierarchy().tokenSequence(RustTokenId.language());
        tokenSequence.move(0);
        final AbstractDocument document = (AbstractDocument) context.document();

        int indent = 0;
        while (tokenSequence.moveNext()) {
            Token<RustTokenId> token = tokenSequence.token();
            if (!EnumSet.of(LBRACE, RBRACE).contains(token.id())) {
                continue;
            }
            int tokenOffset = tokenSequence.offset();
            if (token.id() == RustTokenId.LBRACE) {
                curlyBraces.add(new CurlyBrace('{', tokenOffset, indent));
                indent += indentSize();
            } else if (token.id() == RustTokenId.RBRACE) {
                indent -= indentSize();
                curlyBraces.add(new CurlyBrace('}', tokenOffset, indent));
            }
        }

        if (curlyBraces.isEmpty()) {
            return;
        }

        // Work backwards. Changing the document length while moving forwards wrecks offsets further ahead.
        for (int i = curlyBraces.size(); i > 0; i--) {
            CurlyBrace curlyBrace = curlyBraces.get(i - 1);
            //TODO: test the limits here
            if (curlyBrace.offset > context.endOffset()) {
                continue;
            } else if (curlyBrace.offset < context.startOffset()) {
                return;
            } else if (curlyBrace.type == '}') {
                continue;
            }
            int tokenOffset = curlyBrace.offset;
            try {
                final int startOfBraceLine = context.lineStartOffset(tokenOffset);
                final int braceLineIndentLength = context.lineIndent(startOfBraceLine);
                final int startOfGap = tokenOffset + 1;  //LBRACE width = 1
                int nextCharPosition = startOfGap;
                char nextChar = text.charAt(nextCharPosition);
                while (Character.isWhitespace(nextChar)) {
                    nextCharPosition++;
                    nextChar = text.charAt(nextCharPosition);
                }
                final int endOfGap = nextCharPosition;
                final int lengthOfGap = endOfGap - startOfGap;
                final int indentLength;
                if (nextChar == '}') {
                    indentLength = braceLineIndentLength;
                } else {
                    indentLength = braceLineIndentLength + indentSize();
                }
                //TODO: do we need a lock?
                //  Others use BaseDocument.runAtomic()
                //  PHP also does
                //MutableTextInput mti = (MutableTextInput) doc.getProperty(MutableTextInput.class);
                //try {
                //    mti.tokenHierarchyControl().setActive(false);
                //    <format>
                //} finally {
                //    mti.tokenHierarchyControl().setActive(true);
                //}
                document.replace(startOfGap, lengthOfGap, "\n", null);
                final int startOfNextLine = context.lineStartOffset(startOfGap + 1);
                context.modifyIndent(startOfNextLine, indentLength);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
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
