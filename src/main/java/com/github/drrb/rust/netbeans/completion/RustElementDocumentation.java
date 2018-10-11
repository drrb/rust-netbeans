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
package com.github.drrb.rust.netbeans.completion;

import com.github.drrb.rust.netbeans.parsing.RustdocCommentTextExtractor;
import com.github.drrb.rust.netbeans.parsing.index.RustDocComment;
import org.pegdown.PegDownProcessor;

/**
 *
 */
public class RustElementDocumentation {

    public static final RustElementDocumentation NONE = new RustElementDocumentation(null);

    public static RustElementDocumentation forDocComment(RustDocComment docComment) {
        if (docComment == null || docComment.getText().isEmpty()) {
            return NONE;
        } else {
            return new RustElementDocumentation(docComment.getText());
        }
    }
    private final String rawComment;

    private RustElementDocumentation(String rawComment) {
        this.rawComment = rawComment;
    }

    public String getHtml(String defaultHtml) {
        if (this == NONE) {
            return defaultHtml;
        }
        String commentHtml = renderMarkdownAsHtml(getText());
        if (commentHtml == null) { // If the comment couldn't be parsed as markdown
            return defaultHtml;
        } else {
            return commentHtml;
        }
    }

    public String getText() {
        return new RustdocCommentTextExtractor().extractTextFromRustDoc(rawComment);
    }

    private String renderMarkdownAsHtml(String markdown) {
        return new PegDownProcessor().markdownToHtml(markdown);
    }
}
