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

import org.netbeans.modules.csl.api.OffsetRange;


import com.github.drrb.rust.netbeans.parsing.antlr.AntlrTokenID;
import com.github.drrb.rust.netbeans.parsing.antlr.CommonRustTokenIDs;
import java.util.Objects;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.TokenSource;
import org.netbeans.api.lexer.PartType;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;

public class RustToken extends Token implements org.antlr.v4.runtime.Token {
    private final AntlrTokenID enumKind;
    private int kind;
    private String image;
    private final OffsetRange range;
    public RustToken(int kind, String image, OffsetRange range) {
        this.kind = kind;
        this.enumKind = CommonRustTokenIDs.forTokenType(kind);
        this.image = image;
        this.range = range;
    }

    public boolean isEof() {
        return enumKind == CommonRustTokenIDs.eof();
    }

    public AntlrTokenID kind() {
        return enumKind;
    }

    public AntlrTokenID id() {
        return kind();
    }

    @Override
    public String toString() {
        return enumKind + ": '" + image + "'";
    }

    public OffsetRange offsetRange() {
        return range;
    }

    @Override
    public CharSequence text() {
        return image;
    }

    @Override
    public boolean isCustomText() {
        return !Objects.equals(image, enumKind.literalName());
    }

    @Override
    public int length() {
        return range.getLength();
    }

    @Override
    public int offset(TokenHierarchy th) {
        return range.getStart();
    }

    @Override
    public boolean isFlyweight() {
        return false;
    }

    @Override
    public PartType partType() {
        return PartType.COMPLETE;
    }

    @Override
    public boolean hasProperties() {
        return false;
    }

    @Override
    public Object getProperty(Object o) {
        return null;
    }

    @Override
    public String getText() {
        return image;
    }

    @Override
    public int getType() {
        return kind;
    }

    @Override
    public int getLine() {
        return 0;
    }

    @Override
    public int getCharPositionInLine() {
        return 0;
    }

    @Override
    public int getChannel() {
        return 0;
    }

    @Override
    public int getTokenIndex() {
        return 0;
    }

    @Override
    public int getStartIndex() {
        return range.getStart();
    }

    @Override
    public int getStopIndex() {
        return range.getEnd() -1;
    }

    @Override
    public TokenSource getTokenSource() {
        return null;
    }

    @Override
    public CharStream getInputStream() {
        return null;
    }
}
