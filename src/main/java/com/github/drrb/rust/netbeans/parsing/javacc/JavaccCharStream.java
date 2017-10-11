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
package com.github.drrb.rust.netbeans.parsing.javacc;

import org.netbeans.spi.lexer.LexerInput;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class JavaccCharStream implements CharStream {

    private LexerInput input;

    public int offset = 0;
    private int tokenOffset = 0;
    private boolean trackLineColumn = true;

    public JavaccCharStream(LexerInput input) {
        this.input = input;
    }

    public char BeginToken() throws IOException {
        tokenOffset = 0;
        return readChar();
    }

    public String GetImage() {
        return input.readText(input.readLength() - tokenOffset, input.readLength()).toString();
    }

    public char[] GetSuffix(int len) {
        if (len > input.readLength())
            throw new IllegalArgumentException();
        return input.readText(input.readLength() - len, input.readLength()).toString().toCharArray();
    }

    public void ReInit(Reader stream, int i, int i0) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void ReInit(InputStream stream, String encoding, int i, int i0) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void backup(int i) {
        offset -= i;
        tokenOffset -= i;
        tokenOffset = tokenOffset < 0 ? 0 : tokenOffset;
        input.backup(i);
    }

    public int getBeginColumn() {
        return 0;
    }

    public int getBeginLine() {
        return 0;
    }

    public int getEndColumn() {
        return 0;
    }

    public int getEndLine() {
        return 0;
    }

    public char readChar() throws IOException {
        offset++;
        tokenOffset++;
        int result = input.read();
        if (result == LexerInput.EOF) {
            if (tokenOffset > 1) { //todo: why?
                backup(1);
            }
            throw new IOException("LexerInput EOF");
        }
        return (char) result;
    }

    @Override
    public int getColumn() {
        return 0;
    }

    @Override
    public int getLine() {
        return 0;
    }

    @Override
    public void Done() {

    }

    @Override
    public void setTabSize(int i) {

    }

    @Override
    public int getTabSize() {
        return 0;
    }

    @Override
    public boolean getTrackLineColumn() {
        return trackLineColumn;
    }

    @Override
    public void setTrackLineColumn(boolean trackLineColumn) {
        this.trackLineColumn = trackLineColumn;
    }
}

