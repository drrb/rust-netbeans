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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
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
        final Position position;
        final AbstractDocument document;

        private CurlyBrace(char type, int offset, AbstractDocument document) throws BadLocationException {
            this.type = type;
            this.position = document.createPosition(offset);
            this.document = document;
        }

        int offset() {
            return position.getOffset();
        }

        void consumeSurroundingWhitespace() throws BadLocationException {
            consumeLeadingWhitespace();
            consumeTrailingWhitespace();
        }

        private void consumeLeadingWhitespace() throws BadLocationException {
            final int endOfGap = offset();
            int previousCharacterPosition = endOfGap;
            char nextChar = charAt(previousCharacterPosition - 1);
            while (Character.isWhitespace(nextChar)) {
                previousCharacterPosition--;
                nextChar = charAt(previousCharacterPosition - 1);
            }
            final int startOfGap = previousCharacterPosition;
            final int lengthOfGap = endOfGap - startOfGap;
            document.replace(startOfGap, lengthOfGap, "", null);
        }

        private void consumeTrailingWhitespace() throws BadLocationException {
            final int startOfGap = offset() + 1;
            int endOfGap = startOfGap;
            for (int i = startOfGap; i < document.getLength() && Character.isWhitespace(charAt(i)); i++) {
                endOfGap = i + 1;
            }
            final int lengthOfGap = endOfGap - startOfGap;
            document.replace(startOfGap, lengthOfGap, "", null);
        }

        private char charAt(int nextCharPosition) {
            return DocumentUtilities.getText(document).charAt(nextCharPosition);
        }
    }

    public enum State {

        IN_BLOCK,
        AFTER_OPEN_BRACE,
        AFTER_CLOSE_BRACE
    }

    @Override
    public void reformat(final Context context, ParserResult compilationInfo) {
        Logger.getLogger(RustFormatter.class.getName()).log(Level.WARNING, "reformat: {0} - {1}, caret = {2}", new Object[]{context.startOffset(), context.endOffset(), context.caretOffset()});
        NetbeansRustParserResult parseResult = (NetbeansRustParser.NetbeansRustParserResult) compilationInfo;
        final Snapshot snapshot = parseResult.getSnapshot();
        //TODO:
        //In addition to locking, PHP also does
        //MutableTextInput mti = (MutableTextInput) doc.getProperty(MutableTextInput.class);
        //try {
        //    mti.tokenHierarchyControl().setActive(false);
        //    <format>
        //} finally {
        //    mti.tokenHierarchyControl().setActive(true);
        //}
        final BaseDocument document = (BaseDocument) context.document();
        document.runAtomic(
                new Runnable() {
            @Override
            public void run() {
                try {
                    List<CurlyBrace> curlyBraces = new LinkedList<CurlyBrace>();
                    try {
                        TokenSequence<RustTokenId> tokenSequence = snapshot.getTokenHierarchy().tokenSequence(RustTokenId.language());
                        tokenSequence.move(0);

                        while (tokenSequence.moveNext()) {
                            Token<RustTokenId> token = tokenSequence.token();
                            int tokenOffset = tokenSequence.offset();
                            if (token.id() == RustTokenId.LBRACE) {
                                curlyBraces.add(new CurlyBrace('{', tokenOffset, document));
                            } else if (token.id() == RustTokenId.RBRACE) {
                                curlyBraces.add(new CurlyBrace('}', tokenOffset, document));
                            }
                        }
                    } finally {
                        document.readUnlock();
                    }

                    int depth = 0;
                    //We need these, because context.endOffset() doesn't update if we modify the document directly (i.e. not through
                    Position startPosition = document.createPosition(context.startOffset());
                    Position endPosition = document.createPosition(context.endOffset());
                    for (CurlyBrace curlyBrace : curlyBraces) {
                        //TODO: outside the zone, still modify indent depth, just don't format
                        if (curlyBrace.offset() < startPosition.getOffset()) {
                            continue;
                        } else if (curlyBrace.offset() > endPosition.getOffset()) {
                            break; //TODO: return?
                        }
                        curlyBrace.consumeSurroundingWhitespace();
                        switch (curlyBrace.type) {
                            case '{':
                                depth++;
                                document.insertString(curlyBrace.offset(), " ", null);
                                document.insertString(curlyBrace.offset() + 1, "\n", null);
                                final int startOfNextLine = curlyBrace.offset() + 2;
                                if (DocumentUtilities.getText(document).charAt(startOfNextLine) == '}') {
                                    context.modifyIndent(startOfNextLine, indentForDepth(depth - 1));
                                } else {
                                    context.modifyIndent(startOfNextLine, indentForDepth(depth));
                                }
                                break;
                            case '}':
                                depth--;
                                document.insertString(curlyBrace.offset(), "\n", null);
                                document.insertString(curlyBrace.offset() + 1, "\n", null);
                                context.modifyIndent(curlyBrace.offset(), indentForDepth(depth));
                                break;
                        }
                    }
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
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

    public int indentForDepth(int depth) {
        //TODO: return 0 if depth negative
        return depth * indentSize();
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
