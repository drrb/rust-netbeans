/*
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
package com.github.drrb.rust.netbeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
    private List<SyntaxError> syntaxErrors;

    @Override
    public void parse(final Snapshot snapshot, Task task, SourceModificationEvent event) {
        this.snapshot = snapshot;
        this.parser = createParser(snapshot);
        this.syntaxErrors = new LinkedList<SyntaxError>();
        this.parser.addErrorListener(new BaseErrorListener() {

            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String message, RecognitionException e) {
                syntaxErrors.add(new SyntaxError(line, charPositionInLine, message));
            }
            
        });
        try {
            parser.prog();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public NetbeansRustParserResult getResult(Task task) throws ParseException {
        return new NetbeansRustParserResult(snapshot, parser, syntaxErrors);
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

        protected SyntaxError(int line, int charPositionInLine, String message) {
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
        private boolean valid = true;
        
        protected NetbeansRustParserResult(Snapshot snapshot, RustParser parser, List<SyntaxError> syntaxErrors) {
            super(snapshot);
            this.parser = parser;
            this.syntaxErrors = Collections.unmodifiableList(new ArrayList<SyntaxError>(syntaxErrors));
        }

        public RustParser getRustParser() throws ParseException {
            if (!valid) {
                throw new ParseException();
            }
            return parser;
        }

        public List<SyntaxError> getSyntaxErrors() {
            return syntaxErrors;
        }

        @Override
        protected void invalidate() {
            valid = false;
        }

        @Override
        public List<? extends Error> getDiagnostics() {
            //TODO: why do we need this?
            return Collections.emptyList();
        }
    }
}
