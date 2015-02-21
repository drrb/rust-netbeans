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
package com.github.drrb.rust.netbeans.test;

import com.github.drrb.rust.netbeans.RustLanguage;
import com.github.drrb.rust.netbeans.highlighting.RustCompileErrorHighlighter;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser;
import com.github.drrb.rust.netbeans.project.CargoConfig;
import com.github.drrb.rust.netbeans.project.Crate;
import com.github.drrb.rust.netbeans.project.RustProject;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.Document;
import static junit.framework.Assert.assertNotNull;
import org.netbeans.modules.csl.api.test.CslTestHelper;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.PositionBounds;
import org.openide.text.PositionRef;

/**
 *
 */
public class NetbeansWithRust extends CslTestHelper {

    public NetbeansWithRust() {
        super(new RustLanguage());
    }

    @Override
    public RustProject getTestProject(String relativePath) throws Exception {
        return super.getTestProject(relativePath).getLookup().lookup(RustProject.class);
    }

    public void checkCrates(String relativePath) throws Exception {
        RustProject project = getTestProject(relativePath);
        CargoConfig cargoConfig = project.getCargoConfig();
        FileObject projectDir = project.getProjectDirectory();

        StringBuilder crateMap = new StringBuilder();
        crateMap.append("crates:\n");
        for (Crate crate : cargoConfig.getCrates()) {
            crateMap.append("    ").append(relativize(projectDir, crate.getFile())).append(": ").append(crate.getType()).append("\n");
        }

        crateMap.append("sources:\n");
        Enumeration<? extends FileObject> files = project.getProjectDirectory().getData(true);
        while (files.hasMoreElements()) {
            FileObject file = files.nextElement();
            Crate owningCrate = cargoConfig.getOwningCrate(file);
            if (!file.getExt().equals("rs")) {
                continue;
            }
            crateMap.append("    ").append(relativize(projectDir, file)).append(":\n");
            crateMap.append("        crate: ").append(relativize(projectDir, owningCrate.getFile())).append("\n");
        }
        assertDescriptionMatches(relativePath, crateMap.toString(), false, ".crates");
    }

    private Path relativize(FileObject ancestor, FileObject file) {
        Path ancestorPath = FileUtil.toFile(ancestor).toPath();
        Path path = FileUtil.toFile(file).toPath();
        return ancestorPath.relativize(path);
    }

    public void checkCompileErrors(final String relativeProjectPath, String relativeSourceFilePath) throws Exception {
        RustProject project = getTestProject(relativeProjectPath);
        FileObject sourceFile = project.getProjectDirectory().getFileObject(relativeSourceFilePath);
        assertNotNull(String.format("Couldn't find file '%s' in project '%s", relativeSourceFilePath, relativeProjectPath), sourceFile);
        Source source = Source.create(sourceFile);
        ParserManager.parse(Collections.singleton(source), new UserTask() {
            public @Override
            void run(ResultIterator resultIterator) throws Exception {
                NetbeansRustParser.NetbeansRustParserResult parseResult = (NetbeansRustParser.NetbeansRustParserResult) resultIterator.getParserResult();
                assertNotNull(parseResult);

                TestableRustCompileErrorHighlighter highlightingTask = new TestableRustCompileErrorHighlighter();
                highlightingTask.run(parseResult, null);

                StringBuilder renderedErrors = new StringBuilder();
                for (ErrorDescription error : highlightingTask.errors) {
                    PositionBounds range = error.getRange();
                    PositionRef rangeStart = range.getBegin();
                    PositionRef rangeEnd = range.getEnd();
                    renderedErrors.append(error.getSeverity())
                            .append(" (").append(rangeStart.getLine()).append(",").append(rangeStart.getColumn())
                            .append(" - ").append(rangeEnd.getLine()).append(",").append(rangeEnd.getColumn())
                            .append(") : ").append(error.getDescription()).append("\n");
                }

                assertDescriptionMatches(relativeProjectPath, renderedErrors.toString(), false, ".compile_errors");
            }
        });
    }

    private static class TestableRustCompileErrorHighlighter extends RustCompileErrorHighlighter {

        private final List<ErrorDescription> errors = new LinkedList<>();

        @Override
        protected void setErrors(Document document, String layerName, List<ErrorDescription> reportedErrors) {
            errors.addAll(reportedErrors);
        }
    }
}
