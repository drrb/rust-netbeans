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

import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class RustParser {

    //TODO: this looks identical to RustCompiler.compile()
    public Result parse(File file, String source) {
        // It's important to tell rustc the full path, because it uses it to find
        //  mods referenced from this file.
        if (!file.isAbsolute()) {
            throw new IllegalArgumentException("Expected an absolute file, but got " + file);
        }
        RustNative.AstHolder astHolder = new RustNative.AstHolder();
        RustNative.ParseMessageAccumulator parseMessageAccumulator = new RustNative.ParseMessageAccumulator();
        RustNative.INSTANCE.parse(file.getAbsolutePath(), source, astHolder, parseMessageAccumulator);
        //TODO: it'd be faster to filter these while we're collecting them.
        // Can we do that, or do we need the other files' messages for later?
        List<RustParseMessage> relevantParseMessages = new LinkedList<>();
        for (RustParseMessage message : parseMessageAccumulator.getMessages()) {
            if (message.getFile().equals(file)) {
                relevantParseMessages.add(message);
            }
        }
        return new Result(astHolder.getAst(), relevantParseMessages);
    }

    public static class Result {

        public static final Result NONE = new Result(null, Collections.<RustParseMessage>emptyList());

        private final RustAst ast;
        private final List<RustParseMessage> parseErrors;

        @VisibleForTesting
        public Result(RustAst ast, List<RustParseMessage> parseErrors) {
            this.ast = ast;
            this.parseErrors = Collections.unmodifiableList(parseErrors);
        }

        public boolean isSuccess() {
            return !isFailure();
        }

        public boolean isFailure() {
            return ast == null;
        }

        public RustAst getAst() {
            return ast;
        }

        public List<RustParseMessage> getParseMessages() {
            return parseErrors;
        }

        public void destroy() {
            if (ast != null) {
                RustNative.INSTANCE.destroyAst(ast);
            }
        }
    }
}
