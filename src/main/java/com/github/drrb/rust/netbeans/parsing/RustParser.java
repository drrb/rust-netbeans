/*
 * Copyright (C) 2015 drrb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.drrb.rust.netbeans.parsing;

import com.google.common.annotations.VisibleForTesting;
import java.util.Collections;
import static java.util.Collections.emptyList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
public class RustParser {

    Result parse(String fileName, String source) {
        AtomicReference<RustAst> astHolder = new AtomicReference<>();
        List<RustParseMessage> parseMessages = new LinkedList<>();
        RustNative.INSTANCE.parse(fileName, source, astHolder::set, parseMessages::add);
        return new Result(astHolder.get(), parseMessages);
    }

    public static class Result {

        public static final Result NONE = new Result(null, emptyList());

        private final RustAst ast;
        private final List<RustParseMessage> parseErrors;

        @VisibleForTesting
        public Result(RustAst ast, List<RustParseMessage> parseErrors) {
            this.ast = ast;
            this.parseErrors = Collections.unmodifiableList(parseErrors);
        }

        public boolean isSuccess() {
            return ast != null;
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
