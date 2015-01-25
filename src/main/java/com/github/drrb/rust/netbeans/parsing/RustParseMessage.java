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
package com.github.drrb.rust.netbeans.parsing;

import com.sun.jna.Structure;
import static java.util.Arrays.asList;
import java.util.List;

/**
 *
 */
public class RustParseMessage extends Structure {

    public static class ByValue extends RustParseMessage implements Structure.ByValue {
    }

    public enum Level {
        BUG,
        FATAL,
        ERROR,
        WARNING,
        NOTE,
        HELP,
    }

    public int level;
    public int startLine;
    public int startCol;
    public int endLine;
    public int endCol;
    public String message;

    public Level getLevel() {
        return Level.values()[level];
    }

    public int getStartLine() {
        return startLine;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getEndCol() {
        return endCol;
    }

    public String getMessage() {
        return message;
    }

    @Override
    protected List<String> getFieldOrder() {
        return asList("level", "startLine", "startCol", "endLine", "endCol", "message");
    }

    @Override
    public String toString() {
        return String.format("[%s] %s,%s-%s,%s: \"%s\"", getLevel(), startLine, startCol, endLine, endCol, message);
    }

}
