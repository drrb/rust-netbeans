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

import com.github.drrb.rust.netbeans.RustSourceSnapshot;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.StyledDocument;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Severity;

/**
 */
public class RustCompileErrorHighlighterTest {

    @Test
    public void shouldExtractErrorForHighlighting() throws Exception {
        RustSourceSnapshot function = new RustSourceSnapshot();
        function.appendln("fn main(name: String) {");
        function.appendln("    printtheline!(\"Hello, {}\", name);");
        function.appendln("}");
        NetbeansRustParserResult parseResult = function.parse();
        final List<ErrorDescription> errors = new LinkedList<>();
        RustCompileErrorHighlighter highlightingTask = new RustCompileErrorHighlighter() {

            @Override
            protected void setErrors(StyledDocument document, String layerName, List<ErrorDescription> reportedErrors) {
                errors.addAll(reportedErrors);
            }

        };
        highlightingTask.run(parseResult, null);

        assertThat(errors, hasSize(1));
        ErrorDescription error = errors.get(0);
        assertThat(error.getSeverity(), is(Severity.ERROR));
        assertThat(error.getDescription(), is("macro undefined: 'printtheline!'"));
        //TODO: getRange() returns null in the test. Fix it?
        //assertThat(error.getRange().getBegin().getLine(), is(2));
        //assertThat(error.getRange().getBegin().getColumn(), is(8));
    }
}
