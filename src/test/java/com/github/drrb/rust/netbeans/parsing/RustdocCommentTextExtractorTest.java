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
package com.github.drrb.rust.netbeans.parsing;

import com.github.drrb.rust.netbeans.RustSourceSnapshot;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class RustdocCommentTextExtractorTest {

    private RustdocCommentTextExtractor extractor;

    @Before
    public void setUp() {
        extractor = new RustdocCommentTextExtractor();
    }

    @Test
    public void shouldReturnNullWhenNullGiven() {
        String markdown = extractor.extractTextFromRustDoc(null);
        assertThat(markdown, is(nullValue()));
    }

    @Test
    public void shouldExtractTextFromSingleLineDocComment() {
        String comment = "///Does something";
        String markdown = extractor.extractTextFromRustDoc(comment);
        assertThat(markdown, is("Does something"));
    }

    @Test
    public void shouldRemoveIndentFromSingleLineDocComment() {
        String comment = "///   Does something";
        String markdown = extractor.extractTextFromRustDoc(comment);
        assertThat(markdown, is("Does something"));
    }

    @Test
    public void shouldExtractTextFromSingleLineMultilineDocComment() {
        StringBuilder comment = new StringBuilder();
        comment.append("/**Does something*/");
        String markdown = extractor.extractTextFromRustDoc(comment.toString());
        assertThat(markdown, is("Does something"));
    }

    @Test
    public void shouldExtractTextFromMultilineDocComment() {
        RustSourceSnapshot comment = new RustSourceSnapshot();
        comment.appendln("/**");
        comment.appendln("Does something");
        comment.append("*/");
        String markdown = extractor.extractTextFromRustDoc(comment.toString());
        assertThat(markdown, is("Does something"));
    }

    @Test
    public void shouldRemoveIndentFromMultilineDocComment() {
        RustSourceSnapshot comment = new RustSourceSnapshot();
        comment.appendln("/**");
        comment.appendln("  Does something");
        comment.appendln("");
        comment.appendln("  Also does something else");
        comment.append("*/");
        String markdown = extractor.extractTextFromRustDoc(comment.toString());
        assertThat(markdown, is("Does something\n\nAlso does something else"));
    }

    @Test
    public void shouldExtractTextFromMultilineDocCommentWithLeadingStars() {
        RustSourceSnapshot comment = new RustSourceSnapshot();
        comment.appendln("/**");
        comment.appendln(" * Does Something");
        comment.appendln(" * ");
        comment.appendln(" * Also does something else");
        comment.append("*/");
        String markdown = extractor.extractTextFromRustDoc(comment.toString());
        assertThat(markdown, is("Does Something\n\nAlso does something else"));
    }

    @Test
    public void shouldOutdentCommentToTheSmallestIndentation() {
        RustSourceSnapshot comment = new RustSourceSnapshot();
        comment.appendln("/**");
        comment.appendln(" * Does Something");
        comment.appendln(" * ");
        comment.appendln(" * Also does the following");
        comment.appendln(" *    - this");
        comment.appendln(" *    - and this");
        comment.appendln(" *    - and this other thing");
        comment.append("*/");

        StringBuilder expectedText = new StringBuilder();
        expectedText.append("Does Something\n");
        expectedText.append("\n");
        expectedText.append("Also does the following\n");
        expectedText.append("   - this\n");
        expectedText.append("   - and this\n");
        expectedText.append("   - and this other thing");
        String markdown = extractor.extractTextFromRustDoc(comment.toString());
        assertThat(markdown, is(expectedText.toString()));
    }
}
