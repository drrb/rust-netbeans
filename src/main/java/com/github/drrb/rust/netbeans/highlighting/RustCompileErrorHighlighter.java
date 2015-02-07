/**
 * Copyright (C) 2015 drrb
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
package com.github.drrb.rust.netbeans.highlighting;

import com.github.drrb.rust.netbeans.RustLanguage;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.rustbridge.RustCompiler;
import com.github.drrb.rust.netbeans.rustbridge.RustParseMessage;
import static com.github.drrb.rust.netbeans.rustbridge.RustParseMessage.Level.HELP;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
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
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 *
 */
public class RustCompileErrorHighlighter extends ParserResultTask<NetbeansRustParserResult> {

    @MimeRegistration(mimeType = RustLanguage.MIME_TYPE, service = TaskFactory.class)
    public static class Factory extends TaskFactory {

        @Override
        public Collection<? extends SchedulerTask> create(Snapshot snapshot) {
            return Collections.singleton(new RustCompileErrorHighlighter());
        }
    }

    @Override
    public void run(NetbeansRustParserResult parseResult, SchedulerEvent event) {
        try {
            if (parseResult.getResult().isFailure()) {
                return;
            }
            Snapshot snapshot = parseResult.getSnapshot();
            FileObject sourceFileObject = snapshot.getSource().getFileObject();
            File sourceFile = FileUtil.toFile(sourceFileObject);
            List<RustParseMessage> messages = new RustCompiler().compile(sourceFile, snapshot.getText().toString());
            StyledDocument document = NbDocument.getDocument(sourceFileObject);
            List<ErrorDescription> errors = getErrors(messages, document);
            setErrors(document, "rust-compile-errors", errors);
        } catch (ParseException | BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @VisibleForTesting
    protected void setErrors(StyledDocument document, String layerName, List<ErrorDescription> errors) {
        HintsController.setErrors(document, layerName, errors);
    }

    @Override
    public int getPriority() {
        // After parse error highlighting runs
        return 1000;
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
    }

    @Override
    public void cancel() {
    }

    protected List<ErrorDescription> getErrors(List<RustParseMessage> messages, StyledDocument document) throws ParseException, BadLocationException {
        List<ErrorDescription> errors = new LinkedList<>();
        for (RustParseMessage message : messages) {
            if (message.getLevel() != HELP) {
                errors.add(toErrorDescription(message, document));
            }
        }
        return errors;
    }

    private ErrorDescription toErrorDescription(RustParseMessage message, StyledDocument document) throws BadLocationException {
        int startOffset = NbDocument.findLineOffset(document, message.getStartLine() - 1) + message.getStartCol();
        int endOffset = NbDocument.findLineOffset(document, message.getEndLine() - 1) + message.getEndCol();
        return ErrorDescriptionFactory.createErrorDescription(
                message.getLevel() == RustParseMessage.Level.WARNING ? Severity.WARNING : Severity.ERROR,
                message.getMessage(),
                document,
                document.createPosition(startOffset),
                document.createPosition(endOffset));
    }
}
