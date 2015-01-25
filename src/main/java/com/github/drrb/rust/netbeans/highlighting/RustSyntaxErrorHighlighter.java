/**
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
package com.github.drrb.rust.netbeans.highlighting;

import com.github.drrb.rust.netbeans.RustLanguage;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.parsing.RustParseMessage;
import static com.github.drrb.rust.netbeans.parsing.RustParseMessage.Level.HELP;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 *
 */
public class RustSyntaxErrorHighlighter extends ParserResultTask<NetbeansRustParserResult> {

    @MimeRegistration(mimeType = RustLanguage.MIME_TYPE, service = TaskFactory.class)
    public static class Factory extends TaskFactory {

        @Override
        public Collection<? extends SchedulerTask> create(Snapshot snapshot) {
            return Collections.singleton(new RustSyntaxErrorHighlighter());
        }
    }

    @Override
    public void run(NetbeansRustParserResult parseResult, SchedulerEvent event) {
        try {
            StyledDocument document = NbDocument.getDocument(parseResult.getSnapshot().getSource().getFileObject());
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

    protected List<ErrorDescription> getErrors(NetbeansRustParserResult parseResult, StyledDocument document) throws ParseException {
        List<RustParseMessage> messages = parseResult.getResult().getParseMessages();
        List<ErrorDescription> errors = new LinkedList<>();
        messages.stream().filter((message) -> message.getLevel() != HELP).forEach((message) -> {
            try {
                errors.add(toErrorDescription(message, document));
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        });
        return errors;
    }

    private ErrorDescription toErrorDescription(RustParseMessage message, StyledDocument document) throws BadLocationException {
        int startOffset = NbDocument.findLineOffset(document, message.getStartLine() - 1) + message.getStartCol();
        int endOffset = NbDocument.findLineOffset(document, message.getEndLine() - 1) + message.getEndCol();
        return ErrorDescriptionFactory.createErrorDescription(
                Severity.ERROR,
                message.getMessage(),
                document,
                document.createPosition(startOffset),
                document.createPosition(endOffset));
    }
}
