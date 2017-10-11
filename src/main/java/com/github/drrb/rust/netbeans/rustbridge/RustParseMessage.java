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

import com.sun.jna.Structure;
import java.io.File;
import static java.util.Arrays.asList;
import java.util.List;
import org.netbeans.modules.csl.api.Severity;

/**
 *
 */
public class RustParseMessage extends Structure {

    public static class ByValue extends RustParseMessage implements Structure.ByValue {
    }

    public enum Level {
        BUG(Severity.ERROR),
        FATAL(Severity.ERROR),
        ERROR(Severity.ERROR),
        WARNING(Severity.WARNING),
        NOTE(Severity.INFO),
        HELP(Severity.INFO);

        private final Severity severity;

        private Level(Severity severity) {
            this.severity = severity;
        }

        public Severity severity() {
            return severity;
        }
    }

    public String fileName;
    public int level;
    public int startLine;
    public int startCol;
    public int endLine;
    public int endCol;
    public String message;

    public RustParseMessage() {
    }

    public RustParseMessage(Level level, int startLine, int startCol, int endLine, int endCol, String message) {
        this.level = level.ordinal();
        this.startLine = startLine;
        this.startCol = startCol;
        this.endLine = endLine;
        this.endCol = endCol;
        this.message = message;
    }

    public Level getLevel() {
        return Level.values()[level];
    }

    public File getFile() {
        return new File(fileName);
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
        return asList("fileName", "level", "startLine", "startCol", "endLine", "endCol", "message");
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s,%s-%s,%s: \"%s\"", getLevel(), fileName, startLine, startCol, endLine, endCol, message);
    }

}
