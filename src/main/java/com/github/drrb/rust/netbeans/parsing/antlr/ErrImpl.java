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

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.parsing.api.Snapshot;
import org.openide.filesystems.FileObject;

/**
 * Implementation of Error which wraps an ErrorNode encountered in a
 * parse tree, distinguishable from syntax errors.
 *
 * @author Tim Boudreau
 */
final class ErrImpl implements Error {

    private final Severity severity;
    private final String message;
    private final FileObject file;
    private final int start;
    private final int end;
    private final boolean line;

    ErrImpl(ErrorNode nd, Snapshot snaphsot) {
        this(nd, Severity.FATAL, snaphsot);
    }

    ErrImpl(ErrorNode nd, Severity severity, Snapshot snapshot) {
        // Ensure we do not hold the snapshot or any objects from
        // the parse, as these will be de-facto memory leaks
        this.severity = severity;
        message = nd.toString();
        file = snapshot.getSource().getFileObject();
        Interval interval = nd.getSourceInterval();
        int start = interval.a;
        int end = interval.b;
        if (start == -1) {
            start = nd.getSymbol().getStartIndex();
            end = nd.getSymbol().getStopIndex() + 1;
        }
        // Negative starts will go boom, and start must be > end
        // to produce highlighting
        this.start = Math.max(0, start);
        this.end = Math.max(1, end);
        line = nd.getText() != null && !nd.getText().trim().contains("\n");
    }

    @Override
    public int getStartPosition() {
        return start;
    }

    @Override
    public int getEndPosition() {
        return end;
    }

    @Override
    public boolean isLineError() {
        return line;
    }

    @Override
    public String getDisplayName() {
        return getDescription();
    }

    @Override
    public String getDescription() {
        return message;
    }

    @Override
    public String getKey() {
        return getStartPosition() + ":" + getEndPosition() + ":" + getDescription();
    }

    @Override
    public FileObject getFile() {
        return file;
    }

    @Override
    public Severity getSeverity() {
        return severity;
    }

    @Override
    public Object[] getParameters() {
        // XXX what is this for?
        return new Object[0];
    }
}
