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

import com.github.drrb.rust.netbeans.parsing.javacc.RustParser;
import com.github.drrb.rust.netbeans.parsing.javacc.SimpleNode;
import com.github.drrb.rust.netbeans.parsing.javacc.Token;
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
import org.openide.text.NbDocument;

import javax.swing.event.ChangeListener;
import javax.swing.text.StyledDocument;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static java.util.Collections.emptyList;

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
        try {
            SimpleNode rootNode = RustParser.parse(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)));
            result = new NetbeansRustParserResult(snapshot, rootNode, emptyList());
        } catch (com.github.drrb.rust.netbeans.parsing.javacc.ParseException e) {
            result = NetbeansRustParserResult.failure(snapshot, e);
        }
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
        private final SimpleNode rootNode;

        public NetbeansRustParserResult(Snapshot snapshot, SimpleNode rootNode, List<Error> diagnostics) {
            super(snapshot);
            this.rootNode = rootNode;
            this.diagnostics = Collections.unmodifiableList(diagnostics);
        }

        public SimpleNode rootNode() throws ParseException {
            //TODO: is this what we should be doing to ensure people don't
            // access a released AST?
            if (!valid.get()) {
                throw new ParseException();
            }
            return rootNode;
        }

        @Override
        protected void invalidate() {
            valid.set(false);
        }

        @Override
        public List<? extends Error> getDiagnostics() {
            return diagnostics;
        }

        public static NetbeansRustParserResult failure(Snapshot snapshot, com.github.drrb.rust.netbeans.parsing.javacc.ParseException e) {
            Token currentToken = e.currentToken;
            FileObject file = snapshot.getSource().getFileObject();
            StyledDocument document = NbDocument.getDocument(file);
            List<Error> diagnostics = new LinkedList<>();
            int startOffset = NbDocument.findLineOffset(document, currentToken.beginLine - 1) + currentToken.beginColumn;
            int endOffset = NbDocument.findLineOffset(document, currentToken.endLine - 1) + currentToken.endColumn;
            diagnostics.add(new DefaultError("rust.parse.message", e.getMessage(), e.getMessage(), file, startOffset, endOffset, Severity.ERROR));
            return new NetbeansRustParserResult(snapshot, null, diagnostics);
        }

        public boolean isFailure() throws ParseException {
            return rootNode() != null;
        }


//
//        public RustSourceIndex getIndex() {
//            return getAst().accept(new IndexingVisitor());
//        }
    }
}
