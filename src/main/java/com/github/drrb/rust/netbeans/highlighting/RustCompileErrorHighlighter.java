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
package com.github.drrb.rust.netbeans.highlighting;

import com.github.drrb.rust.netbeans.cargo.Crate;
import com.github.drrb.rust.netbeans.configuration.RustConfiguration;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser.NetbeansRustParserResult;
import com.github.drrb.rust.netbeans.project.RustProject;
import com.github.drrb.rust.netbeans.rustbridge.RustCompiler;
import com.github.drrb.rust.netbeans.rustbridge.RustParseMessage;
import com.github.drrb.rust.netbeans.util.GsfUtilitiesHack;
import com.google.common.annotations.VisibleForTesting;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.*;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import static com.github.drrb.rust.netbeans.rustbridge.RustParseMessage.Level.HELP;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 *
 */
public class RustCompileErrorHighlighter extends ParserResultTask<NetbeansRustParserResult> {
    private static final Logger LOG = Logger.getLogger(RustCompileErrorHighlighter.class.getName());
    private static final RequestProcessor EXECUTOR = new RequestProcessor("Rust Compile", 12);

//    @MimeRegistration(mimeType = RustLanguage.MIME_TYPE, service = TaskFactory.class)
    public static class Factory extends TaskFactory {

        @Override
        public Collection<? extends SchedulerTask> create(Snapshot snapshot) {
            return Collections.singleton(new RustCompileErrorHighlighter());
        }
    }

    @Override
    public void run(NetbeansRustParserResult parseResult, SchedulerEvent event) {
        try {
            if (parseResult.isFailure()) {
                return;
            }
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
        final Snapshot snapshot = parseResult.getSnapshot();
        EXECUTOR.post(new Runnable() {

            @Override
            public void run() {
                compile(snapshot);
            }
        });
    }

    @VisibleForTesting
    public void compile(Snapshot snapshot) {
        //TODO: this approach makes highlighting slow if project is more than
        // a handful of files. We need some caching, which will probably require
        // changes to be made to rustc.
        try {
            FileObject sourceFile = snapshot.getSource().getFileObject();

            //TODO: What if it's not in a project, or it's in a aproject of a different type?
            RustProject project = FileOwnerQuery.getOwner(sourceFile).getLookup().lookup(RustProject.class);
            Crate crate = project.getCargoConfig().getOwningCrate(sourceFile);
            FileObject crateFile = crate.getFile();

            // If the crate is the one being edited, use its snapshot (i.e. the
            //  version that is currently in the editor.
            // TODO: this is a workaround for rustc only letting us provide the
            //  root file as a string (the others get read directly from disk).
            //  Ideally we'd like to be able to have Java read the files and
            //  pass them into Rust via a callback so that we can use snapshots
            //  for all files.
            String source;
            if (crateFile.equals(sourceFile)) {
                source = snapshot.getText().toString();
            } else {
                source = crateFile.asText(UTF_8.name());
            }
            List<RustParseMessage> messages = new RustCompiler().compile(FileUtil.toFile(crateFile), source, FileUtil.toFile(sourceFile), RustConfiguration.get().getLibrariesPaths());
            StyledDocument document = GsfUtilitiesHack.getDocument(sourceFile, true);
            List<ErrorDescription> errors = getErrors(messages, document);
            setErrors(document, "rust-compile-errors", errors);
        } catch (BadLocationException | IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @VisibleForTesting
    protected void setErrors(Document document, String layerName, List<ErrorDescription> errors) {
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
        //TODO
    }

    @VisibleForTesting
    protected List<ErrorDescription> getErrors(List<RustParseMessage> messages, StyledDocument document) throws BadLocationException {
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
