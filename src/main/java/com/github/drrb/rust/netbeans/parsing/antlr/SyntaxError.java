/*
 * Copyright (C) 2018 Tim Boudreau
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
package com.github.drrb.rust.netbeans.parsing.antlr;

import org.antlr.v4.runtime.Token;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.Severity;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Tim Boudreau
 */
public class SyntaxError implements Error {

    private final AntlrTokenID type;
    private final int startIndex;
    private final int stopIndex;
    private final int line;
    private final int charPositionInLine;
    private final int channel;
    private final int tokenIndex;
    private final String description;
    private final FileObject fo;
    private final String text;

    public SyntaxError(Token token, String description, FileObject fo) {
        this.description = description;
        this.type = CommonRustTokenIDs.forTokenType(token.getType());
        this.line = token.getLine();
        this.charPositionInLine = token.getCharPositionInLine();
        this.startIndex = token.getStartIndex();
        this.stopIndex = token.getStopIndex();
        this.channel = token.getChannel();
        this.tokenIndex = token.getTokenIndex();
        this.fo = fo;
        this.text = token.getText();
    }

    public AntlrTokenID type() {
        return type;
    }

    @Override
    public String toString() {
        return "@line: " + line + ":" + charPositionInLine
                + " (pos: " + startIndex + ":" + (stopIndex + 1) + ") tok "
                + tokenIndex + " '" + text + "' (" + type + "): " + description;
    }

    public int line() {
        return line;
    }

    public int charPositionInLine() {
        return charPositionInLine;
    }

    public int channel() {
        return channel;
    }

    public int tokenIndex() {
        return tokenIndex;
    }

    @Override
    public String getDisplayName() {
        return "Syntax error";
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getKey() {
        return type.name()
                + ":" + line
                + ":" + charPositionInLine
                + ":" + fo.getName();
    }

    @Override
    public FileObject getFile() {
        return fo;
    }

    @Override
    public int getStartPosition() {
        return startIndex;
    }

    @Override
    public int getEndPosition() {
        return stopIndex + 1;
    }

    @Override
    public boolean isLineError() {
        return true;
    }

    @Override
    public Severity getSeverity() {
        return Severity.FATAL;
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o instanceof SyntaxError) {
            return getKey().equals(((SyntaxError) o).getKey());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ((line + 1) * (charPositionInLine * 7)
                + (this.type.ordinal() + 1)) *
                (51 * (fo.getName().hashCode() + 1));
    }
}
