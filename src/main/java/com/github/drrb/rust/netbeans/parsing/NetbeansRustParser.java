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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;

/**
 *
 */
public class NetbeansRustParser extends Parser {

    private final RustParser rustParser = new RustParser();
    private RustParser.Result result = RustParser.Result.NONE;
    private Snapshot snapshot;

    @Override
    public void parse(final Snapshot snapshot, Task task, SourceModificationEvent event) {
        //TODO: if we get segfaults, it's probably to do with this.
        // we should probably make sure we don't try to access the AST from
        // a stale (invalidated) result because the AST will have been freed.
        // (assuming that's actually what ParserResult.invalidate() actually means)
        this.result.destroy();
        this.snapshot = snapshot;
        this.result = parse(snapshot);
    }

    private RustParser.Result parse(Snapshot snapshot) {
        String fileName = snapshot.getSource().getFileObject().getNameExt();
        String source = snapshot.getText().toString();
        return rustParser.parse(fileName, source);
    }

    @Override
    public NetbeansRustParserResult getResult(Task task) throws ParseException {
        return new NetbeansRustParserResult(snapshot, result);
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
    }

    public static class SyntaxError {

        private final int line;
        private final int column;
        private final String message;

        public SyntaxError(int line, int column, String message) {
            this.line = line;
            this.column = column;
            this.message = message;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class NetbeansRustParserResult extends ParserResult {

        private final RustParser.Result result;
        private final AtomicBoolean valid = new AtomicBoolean(true);

        public NetbeansRustParserResult(Snapshot snapshot, RustParser.Result result) {
            super(snapshot);
            this.result = result;
        }

        public List<SyntaxError> getSyntaxErrors() {
            return Collections.emptyList();
        }

        public RustAst getAst() throws ParseException {
            //TODO: is this what we should be doing to ensure people don't
            // access a released AST?
            if (!valid.get()) {
                throw new ParseException();
            }
            return result.getAst();
        }

        @Override
        protected void invalidate() {
            valid.set(false);
        }

        @Override
        public List<? extends Error> getDiagnostics() {
            //TODO: why do we need this?
            return Collections.emptyList();
        }
//
//        public RustSourceIndex getIndex() {
//            return getAst().accept(new IndexingVisitor());
//        }
    }
}
