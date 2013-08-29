/**
 * Copyright (C) 2013 drrb
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
package com.github.drrb.rust.netbeans.formatting;

import com.github.drrb.rust.netbeans.RustSourceSnapshot;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.modules.editor.indent.spi.Context;

/**
 *
 */
public class RustFormatterTest {

    private Context context;
    private RustFormatter formatter;
    private RustSourceSnapshot source = new RustSourceSnapshot();
    private RustSourceSnapshot formattedSource = new RustSourceSnapshot();

    @Before
    public void setUp() {
        formatter = new RustFormatter();
    }

    @Test
    public void shouldIndentLineTheSameAsThePreviousLine() throws Exception {
        source.appendln("    let x = 0;"); //As though we just pressed return at the end of this line
        context = source.getIndentContext()
                .withOffsetRange(15, 15)
                .withCaretOffset(15)
                .build();

        formatter.reindent(context);

        formattedSource.appendln("    let x = 0;");
        formattedSource.appendln("    ");
        assertThat(textOf(context), is(formattedSource.toString()));
    }

    @Test
    public void shouldIncreaseIndentIfPreviousLineEndsInOpenBrace() throws Exception {
        source.appendln("    fn main() {"); //As though we just pressed return at the end of this line
        context = source.getIndentContext()
                .withOffsetRange(16, 16)
                .withCaretOffset(16)
                .build();

        formatter.reindent(context);

        formattedSource.appendln("    fn main() {");
        formattedSource.appendln("        ");
        assertThat(textOf(context), is(formattedSource.toString()));
    }

    @Test
    public void shouldIgnoreSpaceAfterBrace() throws Exception {
        source.appendln("    fn main() {  \t "); //As though we just pressed return at the end of this line
        context = source.getIndentContext()
                .withOffsetRange(20, 20)
                .withCaretOffset(20)
                .build();

        formatter.reindent(context);

        formattedSource.appendln("    fn main() {  \t ");
        formattedSource.appendln("        ");
        assertThat(textOf(context), is(formattedSource.toString()));
    }

    @Test
    public void shouldReformatMethodBraces() throws Exception {
        source.appendln("fn main() {}");
        source.appendln("fn other() {}");
        context = source.getIndentContext()
                .withOffsetRange(0, 26)
                .withCaretOffset(0)
                .build();

        formatter.reformat(context, source.parse());

        formattedSource.appendln("fn main() {");
        formattedSource.appendln("}");
        formattedSource.appendln("fn other() {");
        formattedSource.appendln("}");
        formattedSource.appendln();
        assertThat(textOf(context), is(formattedSource.toString()));
    }

    @Test
    public void shouldOnlyReformatInsideSelection() throws Exception {
        source.appendln("fn one() {}");
        source.appendln("fn two() {}"); //This method is selected
        source.appendln("fn three() {}");
        context = source.getIndentContext()
                .withOffsetRange(12, 23)
                .withCaretOffset(12)
                .build();

        formatter.reformat(context, source.parse());

        formattedSource.appendln("fn one() {}");
        formattedSource.appendln("fn two() {");
        formattedSource.appendln("}");
        formattedSource.appendln("fn three() {}");
        formattedSource.appendln();
        assertThat(textOf(context), is(formattedSource.toString()));
    }

    private String textOf(Context context) {
        return DocumentUtilities.getText(context.document()).toString();
    }
}
