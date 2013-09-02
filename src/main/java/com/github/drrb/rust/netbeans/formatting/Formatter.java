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

import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.parsing.api.Snapshot;
import org.openide.util.Exceptions;

/**
 *
 */
public class Formatter {

    private final RustFormatter formatter;
    private final NetbeansRustParserResult parseResult;
    private final BaseDocument document;
    private final Context context;

    Formatter(RustFormatter formatter, NetbeansRustParserResult parseResult, BaseDocument document, Context context) {
        this.formatter = formatter;
        this.parseResult = parseResult;
        this.document = document;
        this.context = context;
    }

    public void format() {
        final Snapshot snapshot = parseResult.getSnapshot();
        try {
            List<Delimiter> delimiters = new LinkedList<Delimiter>();
            TokenSequence<RustTokenId> tokenSequence = snapshot.getTokenHierarchy().tokenSequence(RustTokenId.language());
            tokenSequence.move(0);

            while (tokenSequence.moveNext()) {
                Token<RustTokenId> token = tokenSequence.token();
                int tokenOffset = tokenSequence.offset();
                if (token.id() == RustTokenId.LBRACE) {
                    delimiters.add(new Delimiter(DelimiterType.OPEN_CURLY, tokenOffset));
                } else if (token.id() == RustTokenId.RBRACE) {
                    delimiters.add(new Delimiter(DelimiterType.CLOSE_CURLY, tokenOffset));
                } else if (token.id() == RustTokenId.SEMI) {
                    //delimiters.add(new Delimiter(DelimiterType.SEMICOLON, tokenOffset));
                }
            }

            int depth = 0;
            // We need these, because context.endOffset() doesn't update if we modify the document directly (i.e. not through the context object)
            Position startPosition = document.createPosition(context.startOffset());
            Position endPosition = document.createPosition(context.endOffset());
            for (Delimiter delimiter : delimiters) {
                //TODO: outside the zone, still modify indent depth, just don't format
                //TODO: do more checking of these limits
                if (delimiter.offset() < startPosition.getOffset()) {
                    continue;
                } else if (delimiter.offset() > endPosition.getOffset()) {
                    break;
                }

                delimiter.adjustSurroundings();
                depth += delimiter.type.depthChangeBefore;
                delimiter.modifyIndentDepth(depth);
                depth += delimiter.type.depthChangeAfter;
                //TODO: encapsulate these (surroundings / depth changing as Delimiter properties, delimiter types to include line endings)
                if (delimiter.type == DelimiterType.OPEN_CURLY) {
                    int startOfNextLine = delimiter.offset() + 2;
                    //TODO: move this out of here and do it for *every* line
                    if (DocumentUtilities.getText(document).charAt(startOfNextLine) != '}') {
                        context.modifyIndent(startOfNextLine, indentForDepth(depth));
                    }
                }
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public int indentForDepth(int depth) {
        //TODO: return 0 if depth negative
        return depth * formatter.indentSize();
    }

    enum DelimiterType {

        OPEN_CURLY(" ", "\n", 0, 1),
        CLOSE_CURLY("\n", "\n", -1, 0),
        SEMICOLON("", "\n", 0, 0);
        final String prefix;
        final String suffix;
        final int depthChangeBefore;
        final int depthChangeAfter;

        DelimiterType(String prefix, String suffix, int depthChangeBefore, int depthChangeAfter) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.depthChangeBefore = depthChangeBefore;
            this.depthChangeAfter = depthChangeAfter;
        }
    }

    private class Delimiter {

        private static final int TOKEN_WIDTH = 1;
        final DelimiterType type;
        final Position position;

        private Delimiter(DelimiterType type, int offset) throws BadLocationException {
            this.type = type;
            this.position = document.createPosition(offset);
        }

        int offset() {
            return position.getOffset();
        }

        void adjustSurroundings() throws BadLocationException {
            setSurrounding(type.prefix, type.suffix);
        }

        void setSurrounding(String before, String after) throws BadLocationException {
            consumeSurroundingWhitespace();
            surroundWith(before, after);
        }

        void consumeSurroundingWhitespace() throws BadLocationException {
            consumeLeadingWhitespace();
            consumeTrailingWhitespace();
        }

        void surroundWith(String before, String after) throws BadLocationException {
            document.insertString(offset(), before, null);
            document.insertString(offset() + TOKEN_WIDTH, after, null);
        }

        private void modifyIndentDepth(int newDepth) throws BadLocationException {
            context.modifyIndent(context.lineStartOffset(offset()), indentForDepth(newDepth));
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
            final int startOfGap = offset() + TOKEN_WIDTH;
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
}
