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
package com.github.drrb.rust.netbeans.rustbridge;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.NativeLibrary;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public interface RustNative extends Library {

    String JNA_LIBRARY_NAME = "javarustbridge";
    NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(JNA_LIBRARY_NAME);
    RustNative INSTANCE = null;//(RustNative) Native.loadLibrary(JNA_LIBRARY_NAME, RustNative.class);

    NativeRustLexer createLexer(String input);

    void getNextToken(NativeRustLexer lexer, TokenCallback callback);

    void destroyLexer(NativeRustLexer box);

    void parse(String fileName, String source, AstCallback resultCallback, ParseMessageCallback parseMessageCallback);

    void destroyAst(RustAst box);

    void getHighlights(RustAst ast, HighlightCallback callback);

    int compile(String path, String source, String[] searchPath, int searchPathLength, ParseMessageCallback parseMessageCallback);

    interface TokenCallback extends Callback {
        void tokenRead(RustToken.ByValue token);
    }

    interface AstCallback extends Callback {
        void sourceParsed(RustAst ast);
    }

    interface ParseMessageCallback extends Callback {
        void errorFound(RustParseMessage.ByValue message);
    }

    interface HighlightCallback extends Callback {
        void highlightFound(RustHighlight.ByValue highlight);
    }

    class TokenHolder implements TokenCallback {
        private RustToken.ByValue token;
        @Override
        public void tokenRead(RustToken.ByValue token) {
            this.token = token;
        }

        public RustToken getToken() {
            return token;
        }
    }

    class AstHolder implements AstCallback {
        private RustAst ast;

        @Override
        public void sourceParsed(RustAst ast) {
            this.ast = ast;
        }

        public RustAst getAst() {
            return ast;
        }
    }

    class ParseMessageAccumulator implements ParseMessageCallback {
        private final List<RustParseMessage> messages = new LinkedList<>();

        @Override
        public void errorFound(RustParseMessage.ByValue message) {
            messages.add(message);
        }

        public List<RustParseMessage> getMessages() {
            return messages;
        }
    }

    class HighlightAccumulator implements HighlightCallback {
        private final List<RustHighlight> highlights = new LinkedList<>();
        private final File file;

        public HighlightAccumulator(File file) {
            this.file = file;
        }

        @Override
        public void highlightFound(RustHighlight.ByValue highlight) {
            if (file.equals(highlight.getFile())) {
                highlights.add(highlight);
            }
        }

        public List<RustHighlight> getHighlights() {
            return highlights;
        }
    }
}
