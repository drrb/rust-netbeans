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

import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.parsing.api.Snapshot;
import org.openide.util.Exceptions;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class RustDocumentFormatter {

    private final RustFormatter formatter;
    private final NetbeansRustParserResult parseResult;
    private final BaseDocument document;
    private final Context context;

    RustDocumentFormatter(RustFormatter formatter, NetbeansRustParserResult parseResult, BaseDocument document, Context context) {
        this.formatter = formatter;
        this.parseResult = parseResult;
        this.document = document;
        this.context = context;
    }

    public void format() {
        final Snapshot snapshot = parseResult.getSnapshot();
        try {
            List<Delimiter> delimiters = new LinkedList<>();
            TokenHierarchy<?> tokenHierarchy = snapshot.getTokenHierarchy();
            TokenSequence<RustTokenId> tokenSequence = tokenHierarchy.tokenSequence(RustTokenId.language());
            tokenSequence.move(0);

            while (tokenSequence.moveNext()) {
                Token<RustTokenId> token = tokenSequence.token();
                int tokenOffset = tokenSequence.offset();
                if (token.id() == RustTokenId.LEFT_BRACE) {
                    delimiters.add(new Delimiter(DelimiterType.OPEN_BRACE, tokenOffset));
                } else if (token.id() == RustTokenId.RIGHT_BRACE) {
                    delimiters.add(new Delimiter(DelimiterType.CLOSE_BRACE, tokenOffset));
                } else if (token.id() == RustTokenId.SEMICOLON) {
                    delimiters.add(new Delimiter(DelimiterType.SEMICOLON, tokenOffset));
                }
            }

            int depth = 0;
            // We need these, because context.endOffset() doesn't update if we modify the document directly (i.e. not through the context object)
            Position startPosition = document.createPosition(context.startOffset());
            Position endPosition = document.createPosition(context.endOffset());
            for (Delimiter delimiter : delimiters) {
                depth += delimiter.type.depthChangeBefore;
                int nextDepth = depth + delimiter.type.depthChangeAfter;
                if (delimiter.offset() >= startPosition.getOffset() && delimiter.offset() < endPosition.getOffset()) {
                    delimiter.adjustSurroundings();
                    delimiter.modifyIndentDepth(depth);

                    //TODO: we wouldn't need this if we added a placeholding delimiter for lines before closing braces with no semicolon
                    if (delimiter.startOfNextLine() >= startPosition.getOffset() && delimiter.startOfNextLine() < endPosition.getOffset()) {
                        delimiter.modifyIndentDepthOfNextLine(nextDepth);
                    }
                }
                depth = nextDepth;
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public int indentForDepth(int depth) {
        //TODO: return 0 if depth negative
        return depth * formatter.indentSize();
    }

    private enum DelimiterType {

        OPEN_BRACE(" ", "\n", 0, 1),
        CLOSE_BRACE("\n", "\n", -1, 0),
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
            replaceLeadingWhitespace(before);
            replaceTrailingWhitespace(after);
        }

        void modifyIndentDepth(int newDepth) throws BadLocationException {
            context.modifyIndent(context.lineStartOffset(offset()), indentForDepth(newDepth));
        }

        void modifyIndentDepthOfNextLine(int depth) throws BadLocationException {
            context.modifyIndent(startOfNextLine(), indentForDepth(depth));
        }

        int startOfNextLine() throws BadLocationException {
            assert type.suffix.equals("\n") : "This only works for delimiter types that are followed by newlines";
            assert type.suffix.equals(getText(offset() + TOKEN_WIDTH, type.suffix.length())) : "Trying to find where the next line starts, but doing it dodgily!";
            return offset() + TOKEN_WIDTH + type.suffix.length();
        }

        private void replaceLeadingWhitespace(String prefix) throws BadLocationException {
            final int endOfGap = offset();
            int previousCharacterPosition = endOfGap;
            char nextChar = charAt(previousCharacterPosition - 1);
            while (Character.isWhitespace(nextChar)) {
                previousCharacterPosition--;
                nextChar = charAt(previousCharacterPosition - 1);
            }
            final int startOfGap = previousCharacterPosition;
            final int lengthOfGap = endOfGap - startOfGap;
            document.replace(startOfGap, lengthOfGap, prefix, null);
        }

        private void replaceTrailingWhitespace(String suffix) throws BadLocationException {
            final int startOfGap = offset() + TOKEN_WIDTH;
            int endOfGap = startOfGap;
            for (int i = startOfGap; i < document.getLength() && Character.isWhitespace(charAt(i)); i++) {
                endOfGap = i + 1;
            }
            final int lengthOfGap = endOfGap - startOfGap;
            document.replace(startOfGap, lengthOfGap, suffix, null);
        }

        private char charAt(int nextCharPosition) {
            return DocumentUtilities.getText(document).charAt(nextCharPosition);
        }

        private String getText(int start, int length) throws BadLocationException {
            return DocumentUtilities.getText(document, start, length).toString();
        }
    }
}
