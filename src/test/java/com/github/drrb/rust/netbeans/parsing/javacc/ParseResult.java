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
package com.github.drrb.rust.netbeans.parsing.javacc;

import com.github.drrb.rust.netbeans.parsing.RustTokenId;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static java.util.Comparator.comparing;

public class ParseResult extends JsonSerializable {
    public static class Error extends JsonSerializable {

        private final RustTokenId tokenKind;
        private final int beginLine;
        private final int beginColumn;
        private final int endLine;
        private final int endColumn;

        public Error(RustToken unexpectedToken, String message) {
            tokenKind = unexpectedToken.kind();
            beginLine = unexpectedToken.beginLine;
            beginColumn = unexpectedToken.beginColumn;
            RustToken followingToken = unexpectedToken.nextTokenMaybeSpecial();
            if (unexpectedToken == followingToken) {
                endLine = unexpectedToken.endLine;
                endColumn = unexpectedToken.endColumn;
            } else {
                endLine = followingToken.beginLine;
                endColumn = followingToken.beginColumn;
            }
        }

        public Error(ParseException parseException) {
            this((RustToken) parseException.currentToken.next, parseException.getMessage());
        }
    }

    public List<Error> errors = new LinkedList<>();

    public ParseResult(RustParser.Result result) {
        result.syntaxErrors().stream()
                .sorted(byFilePosition())
                .map(Error::new).forEach(errors::add);
    }

    private Comparator<ParseException> byFilePosition() {
        return comparing((ParseException e) -> e.currentToken.beginLine)
                .thenComparing(e -> e.currentToken.beginColumn);
    }
}
