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
package com.github.drrb.rust.netbeans.rustbridge;

import com.github.drrb.rust.netbeans.parsing.antlr.AntlrTokenID;
import com.github.drrb.rust.netbeans.parsing.antlr.CommonRustTokenIDs;
import com.sun.jna.Structure;
import static java.util.Arrays.asList;
import java.util.List;
import org.antlr.v4.runtime.Token;

/**
 *
 */
public class RustToken extends Structure {

    public static class ByValue extends RustToken implements Structure.ByValue {
    }

    public int startLine;
    public int startCol;
    public int startByte;
    public int startChar;
    public int endLine;
    public int endCol;
    public int endByte;
    public int endChar;
    public int type;

    public static RustToken of(Token tok) {
        RustToken result = new RustToken();
        result.startLine = tok.getLine();
        result.startCol = tok.getCharPositionInLine();
        result.type = tok.getType();
        result.endCol = tok.getCharPositionInLine() + ((tok.getStopIndex() + 1) - tok.getStartIndex());
        result.endLine = tok.getLine();
        return result;
    }

    boolean isEof() {
        return getType() == CommonRustTokenIDs.eof();
    }

    public AntlrTokenID getType() {
        return CommonRustTokenIDs.forTokenType(type);
    }

    public int length() {
        return endChar - startChar;
    }

    @Override
    protected List<String> getFieldOrder() {
        return asList("startLine", "startCol", "startByte", "startChar", "endLine", "endCol", "endByte", "endChar", "type");
    }

    @Override
    public String toString() {
        return String.format("%s: %s,%s-%s,%s (%s chars)", getType(), startLine, startCol, endLine, endCol, length());//, super.toString(true));
    }
}
