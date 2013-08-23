/**
 * Copyright (C) 2013 drrb
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

import com.github.drrb.rust.netbeans.highlighting.RustOccurrencesFinder;
import com.github.drrb.rust.netbeans.parsing.index.IndexingVisitor;
import com.github.drrb.rust.netbeans.parsing.index.RustSourceIndex;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.event.ChangeListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.openide.util.Exceptions;

/**
 *
 */
public class NetbeansRustParser extends Parser {

    private Snapshot snapshot;
    private RustParser parser;
    private LinkedList<Rustdoc> rustdocs;
    private List<SyntaxError> syntaxErrors;
    private RustParser.ProgContext ast;

    @Override
    public void parse(final Snapshot snapshot, Task task, SourceModificationEvent event) {
        this.snapshot = snapshot;
        this.parser = createParser(snapshot);
        this.rustdocs = new LinkedList<Rustdoc>();
        this.syntaxErrors = new LinkedList<SyntaxError>();
        this.parser.addErrorListener(new BaseErrorListener() {

            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String message, RecognitionException e) {
                syntaxErrors.add(new SyntaxError(line, charPositionInLine, message));
            }

        });
        this.parser.addParseListener(new RustdocCollectingParseListener(rustdocs));
        try {
            ast = parser.prog();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public NetbeansRustParserResult getResult(Task task) throws ParseException {
        return new NetbeansRustParserResult(snapshot, parser, ast, rustdocs, syntaxErrors);
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
    }

    private static RustParser createParser(Snapshot snapshot) {
        CharStream input = new ANTLRInputStream(snapshot.getText().toString());
        Lexer lexer = new RustLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        return new RustParser(tokens);
    }

    public static class SyntaxError {
        private final int line;
        private final int charPositionInLine;
        private final String message;

        public SyntaxError(int line, int charPositionInLine, String message) {
            this.line = line;
            this.charPositionInLine = charPositionInLine;
            this.message = message;
        }

        public int getLine() {
            return line;
        }

        public int getCharPositionInLine() {
            return charPositionInLine;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class NetbeansRustParserResult extends ParserResult {
        private final RustParser parser;
        private final List<SyntaxError> syntaxErrors;
        private final AtomicBoolean valid = new AtomicBoolean(true);
        private final RustParser.ProgContext ast;
        private final List<Rustdoc> rustdocs;

        public NetbeansRustParserResult(Snapshot snapshot, RustParser parser, RustParser.ProgContext ast, List<Rustdoc> rustdocs, List<SyntaxError> syntaxErrors) {
            super(snapshot);
            this.parser = parser;
            this.ast = ast;
            this.rustdocs = rustdocs;
            this.syntaxErrors = new ArrayList<SyntaxError>(syntaxErrors);
        }

        public RustParser getRustParser() throws ParseException {
            if (!valid.get()) {
                throw new ParseException();
            }
            return parser;
        }

        public List<SyntaxError> getSyntaxErrors() {
            return Collections.unmodifiableList(syntaxErrors);
        }

        public List<Rustdoc> getRustdocs() {
            return Collections.unmodifiableList(rustdocs);
        }

        public RustParser.ProgContext getAst() {
            return ast;
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

        public RustSourceIndex getIndex() {
            return getAst().accept(new IndexingVisitor());
        }
    }
}
