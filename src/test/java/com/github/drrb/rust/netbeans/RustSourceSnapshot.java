/**
 * Copyright (C) 2015 drrb
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
package com.github.drrb.rust.netbeans;

import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.modules.editor.indent.spi.IndentContextFactory;

/**
 *
 */
public class RustSourceSnapshot implements CharSequence {

    @SuppressWarnings("StringBufferWithoutInitialCapacity")
    private final StringBuilder source = new StringBuilder();

    public RustSourceSnapshot appendln() {
        return append("\n");
    }

    public RustSourceSnapshot appendln(String line) {
        return append(line).appendln();
    }

    public RustSourceSnapshot append(String string) {
        source.append(string);
        return this;
    }

    public NetbeansRustParser.NetbeansRustParserResult parse() {
        return TestParsing.parse(source);
    }

    @Override
    public int length() {
        return source.length();
    }

    @Override
    public char charAt(int index) {
        return source.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return source.subSequence(start, end);
    }

    @Override
    public String toString() {
        return source.toString();
    }

    public RustDocument getDocument() {
        return RustDocument.containing(this);
    }

    public String getDocumentText() {
        return DocumentUtilities.getText(getDocument()).toString();
    }

    public IndentContextFactory.Builder getIndentContext() {
        return IndentContextFactory.createFor(getDocument());
    }
}
