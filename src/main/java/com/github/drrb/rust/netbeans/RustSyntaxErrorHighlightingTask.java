/*
 * Copyright (C) 2013 drrb
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
package com.github.drrb.rust.netbeans;

import com.github.drrb.rust.netbeans.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.NetbeansRustParser.SyntaxError;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.util.Exceptions;

/**
 *
 */
public class RustSyntaxErrorHighlightingTask extends ParserResultTask {

    @Override
    public void run(Parser.Result result, SchedulerEvent event) {
        try {
            NetbeansRustParserResult parseResult = (NetbeansRustParserResult) result;
            Document document = result.getSnapshot().getSource().getDocument(false);
            List<ErrorDescription> errors = getErrors(parseResult, document);
            HintsController.setErrors(document, "rust-errors", errors);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public int getPriority() {
        // From tutorial
        return 100;
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
    }

    @Override
    public void cancel() {
    }

    protected List<ErrorDescription> getErrors(NetbeansRustParserResult parseResult, Document document) {
        List<SyntaxError> syntaxErrors = parseResult.getSyntaxErrors();
        List<ErrorDescription> errors = new LinkedList<ErrorDescription>();
        for (SyntaxError syntaxError : syntaxErrors) {
            String message = syntaxError.getMessage();

            int line = syntaxError.getLine();
            if (line <= 0) { //TODO: Copied from tutorial. Does this ever really happen?
                continue;
            }
            ErrorDescription errorDescription = ErrorDescriptionFactory.createErrorDescription(
                    Severity.ERROR,
                    message,
                    document,
                    line);
            errors.add(errorDescription);
        }
        return errors;
    }
}
