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

import com.github.drrb.rust.antlr.RustLexer;
import com.github.drrb.rust.antlr.RustParser;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.event.ChangeListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;

/**
 *
 * @author Tim Boudreau
 */
public class RustAntlrParser extends Parser {
    private RustAntlrParserResult result;
    private AtomicBoolean cancelled = new AtomicBoolean();

    public RustParser parseString(String source) {
        RustLexer lexer = new RustLexer(CharStreams.fromString(source));
        return new RustParser(new CommonTokenStream(lexer, 0));
    }

    @Override
    public void parse(Snapshot snpsht, Task task, SourceModificationEvent sme) throws ParseException {
        cancelled.set(false);
        String source = snpsht.getText().toString();
        RustParser parser = parseString(source);
        RustAntlrParserResult result = new RustAntlrParserResult(snpsht, parser, cancelled);
        synchronized(this) {
            this.result = result;
        }
//        System.out.println("PARSE RESULT " + result);
    }

    @Override
    public void cancel(CancelReason cr, SourceModificationEvent sme) {
        System.out.println("cancelled because of " + cr);
        cancel();
    }

    @Override
    public void cancel() {
        System.out.println("parse cancelled");
        cancelled.set(true);
        RustAntlrParserResult result;
        synchronized(this) {
            result = this.result;
        }
        if (result != null) {
            result.invalidate();
        }
    }


    @Override
    public Result getResult(Task task) throws ParseException {
        return result;
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
        // do nothing
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
        // do nothing
    }
}
