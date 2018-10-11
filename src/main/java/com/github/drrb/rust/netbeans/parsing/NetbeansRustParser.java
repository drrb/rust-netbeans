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
package com.github.drrb.rust.netbeans.parsing;

import com.github.drrb.rust.netbeans.parsing.javacc.RustParser;
import com.github.drrb.rust.netbeans.parsing.javacc.RustToken;
import com.github.drrb.rust.netbeans.parsing.javacc.SimpleNode;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.csl.spi.DefaultError;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.openide.filesystems.FileObject;

import javax.swing.event.ChangeListener;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;

/**
 *
 */
public class NetbeansRustParser extends Parser {
    private static final Logger LOG = Logger.getLogger(NetbeansRustParser.class.getName());
    private Snapshot snapshot;
    private NetbeansRustParserResult result;

    @Override
    public void parse(final Snapshot snapshot, Task task, SourceModificationEvent event) {
        this.snapshot = snapshot;
        String source = snapshot.getText().toString();

        RustParser.Result parseResult = RustParser.parse(source);
        result = NetbeansRustParserResult.complete(snapshot, parseResult);
    }

    @Override
    public NetbeansRustParserResult getResult(Task task) throws ParseException {
        return result;
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
    }

    public static class NetbeansRustParserResult extends ParserResult {

        private final AtomicBoolean valid = new AtomicBoolean(true);
        private final List<Error> diagnostics;
        private final RustParser.Result parseResult;

        public NetbeansRustParserResult(Snapshot snapshot, RustParser.Result parseResult, List<Error> diagnostics) {
            super(snapshot);
            this.parseResult = parseResult;
            this.diagnostics = Collections.unmodifiableList(diagnostics);
        }

        public SimpleNode rootNode() throws ParseException {
            //TODO: i think i've seen the valid field on the parser itself in an example. Where should it be?
            //TODO: also, this seems to be invalidated before the first use. Why?
//            if (!valid.get()) {
//                throw new ParseException();
//            }
            return parseResult.rootNode();
        }

        @Override
        protected void invalidate() {
            valid.set(false);
        }

        @Override
        public List<? extends Error> getDiagnostics() {
            return diagnostics;
        }

        public static NetbeansRustParserResult complete(Snapshot snapshot, RustParser.Result parseResult) {
            return new NetbeansRustParserResult(snapshot, parseResult, parseResult.syntaxErrors().stream().map(ex -> toError(snapshot, ex)).collect(toList()));
        }

        private static DefaultError toError(Snapshot snapshot, com.github.drrb.rust.netbeans.parsing.javacc.ParseException e) {
            RustToken currentToken = (RustToken) e.currentToken.next;
            FileObject file = snapshot.getSource().getFileObject();
            return new DefaultError(
                    "rust.parse.message",
                    "Parse error",
                    e.getMessage(),
                    file,
                    currentToken.absoluteBeginPosition - 1,
                    currentToken.absoluteEndPosition - 1,
                    false,
                    Severity.ERROR
            );
        }

        public boolean isFailure() throws ParseException {
            return rootNode() != null;
        }
    }
}
