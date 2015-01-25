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
package com.github.drrb.rust.netbeans.highlighting;

import com.github.drrb.rust.netbeans.RustDocument;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.parsing.RustParseMessage;
import static com.github.drrb.rust.netbeans.parsing.RustParseMessage.Level.FATAL;
import com.github.drrb.rust.netbeans.parsing.RustParser;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Severity;

/**
 */
public class RustSyntaxErrorHighlighterTest {

    @Test
    public void shouldExtractErrorForHighlighting() throws Exception {
        StringBuilder function = new StringBuilder();
        function.append("fn greet(name: String) {\n");
        function.append("    xxx io::println(fmt!(\"Hello, %?\", name));\n");
        function.append("}\n");
        RustParseMessage syntaxError = new RustParseMessage(FATAL, 2, 8, 2, 10, "bad syntax!");
        RustParser.Result result = new RustParser.Result(null, asList(syntaxError));

        RustDocument document = RustDocument.containing(function);
        Snapshot snapshot = snapshotOf(document);
        RustSyntaxErrorHighlighter.Factory factory = new RustSyntaxErrorHighlighter.Factory();
        Collection<? extends SchedulerTask> tasks = factory.create(snapshot);
        RustSyntaxErrorHighlighter highlightingTask = (RustSyntaxErrorHighlighter) tasks.iterator().next();
        NetbeansRustParserResult parseResult = new NetbeansRustParserResult(snapshot, result);
        Iterator<ErrorDescription> errors = highlightingTask.getErrors(parseResult, document).iterator();

        ErrorDescription error = errors.next();
        assertThat(error.getSeverity(), is(Severity.ERROR));
        assertThat(error.getDescription(), is("bad syntax!"));
        //TODO: getRange() returns null in the test. Fix it?
        //assertThat(error.getRange().getBegin().getLine(), is(2));
        //assertThat(error.getRange().getBegin().getColumn(), is(8));
    }

    private Snapshot snapshotOf(Document document) throws BadLocationException {
        Source source = Source.create(document);
        return source.createSnapshot();
    }
}
