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
package com.github.drrb.rust.netbeans.test;

import com.github.drrb.rust.netbeans.RustLanguage;
import com.github.drrb.rust.netbeans.cargo.CargoConfig;
import com.github.drrb.rust.netbeans.cargo.Crate;
import com.github.drrb.rust.netbeans.highlighting.RustCompileErrorHighlighter;
import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser;
import com.github.drrb.rust.netbeans.project.RustProject;
import com.github.drrb.rust.netbeans.sources.RustSourceGroup;
import org.junit.ComparisonFailure;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.IndexSearcher;
import org.netbeans.modules.csl.api.test.CslTestHelper;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.impl.indexing.CacheFolder;
import org.netbeans.modules.parsing.impl.indexing.FileObjectIndexable;
import org.netbeans.modules.parsing.impl.indexing.SPIAccessor;
import org.netbeans.modules.parsing.impl.indexing.SuspendSupport;
import org.netbeans.modules.parsing.impl.indexing.lucene.TestIndexFactoryImpl;
import org.netbeans.modules.parsing.lucene.support.DocumentIndex;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexer;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.netbeans.modules.project.uiapi.ProjectOpenedTrampoline;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.PositionBounds;
import org.openide.text.PositionRef;

import javax.swing.*;
import javax.swing.text.Document;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.annotation.Retention;
import java.nio.file.Path;
import java.util.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toMap;

/**
 *
 */
public class NetbeansWithRust extends CslTestHelper {


    @Retention(RUNTIME)
    public @interface Project {
        String value();
    }

    public NetbeansWithRust() {
        super(new RustLanguage());
    }

    private RustProject project;

    public RustProject getProject() {
        assertNotNull("add @Project to enable getProject()", project);
        return project;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        Statement inner = super.apply(base, description);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Project configuredProject = description.getAnnotation(Project.class);
                if (configuredProject != null) {
                    project = getTestProject(configuredProject.value());
                    Collection<? extends ProjectOpenedHook> hooks = project.getLookup().lookupAll(ProjectOpenedHook.class);
                    for (ProjectOpenedHook hook : hooks) {
                        ProjectOpenedTrampoline.DEFAULT.projectOpened(hook);
                    }
                    //TODO: this isn't required now that we run the ClasspathSettingProjectOpenedHook above
                    //for (String classpathId : project.getClassPaths().keySet()) {
                    //    if (project.getClassPaths().get(classpathId).length > 1) {
                    //        fail("Test project specified with @Project has multiple classpath roots for classpath '" + classpathId + "'. This isn't supported by CslTestBase");
                    //    }
                    //}
                    //classpaths = project.getClassPaths().entrySet().stream().collect(toMap(Map.Entry::getKey, e -> e.getValue()[0]));
                }
                inner.evaluate();
            }
        };
    }

    @Override
    public RustProject getTestProject(String relativePath) throws Exception {
        return super.getTestProject(relativePath).getLookup().lookup(RustProject.class);
    }

    public void checkCrates(String relativePath) throws Exception {
        RustProject project = getTestProject(relativePath);
        CargoConfig cargoConfig = project.getCargoConfig();
        FileObject projectDir = project.getProjectDirectory();
        List<Crate> crates = cargoConfig.getCrates();

        StringBuilder crateMap = new StringBuilder();
        crateMap.append("crates:\n");
        Collections.sort(crates, new CratesAlphabetically());
        for (Crate crate : crates) {
            crateMap.append("    ").append(relativize(projectDir, crate.getFile())).append(": ").append(crate.getType()).append("\n");
        }

        crateMap.append("sources:\n");
        ArrayList<? extends FileObject> files = Collections.list(project.getProjectDirectory().getData(true));
        Collections.sort(files, new FilesAlphabetically());
        for (FileObject file : files) {
            Crate owningCrate = cargoConfig.getOwningCrate(file);
            if (!file.getExt().equals("rs")) {
                continue;
            }
            crateMap.append("    ").append(relativize(projectDir, file)).append(":\n");
            crateMap.append("        crate: ").append(relativize(projectDir, owningCrate.getFile())).append("\n");
        }
        assertDescriptionMatches(relativePath, crateMap.toString(), false, ".crates");
    }

    private String relativize(FileObject ancestor, FileObject file) {
        Path ancestorPath = FileUtil.toFile(ancestor).toPath();
        Path path = FileUtil.toFile(file).toPath();
        return ancestorPath.relativize(path).toString().replace('\\', '/');
    }

    public void checkCompileErrors(final String relativeProjectPath, String relativeSourceFilePath) throws Exception {
        RustProject project = getTestProject(relativeProjectPath);
        FileObject sourceFile = project.getProjectDirectory().getFileObject(relativeSourceFilePath);
        assertNotNull(String.format("Couldn't find file '%s' in project '%s", relativeSourceFilePath, relativeProjectPath), sourceFile);
        Source source = Source.create(sourceFile);
        ParserManager.parse(singleton(source), new UserTask() {
            public @Override
            void run(ResultIterator resultIterator) throws Exception {
                NetbeansRustParser.NetbeansRustParserResult parseResult = (NetbeansRustParser.NetbeansRustParserResult) resultIterator.getParserResult();
                assertNotNull(parseResult);

                TestableRustCompileErrorHighlighter highlightingTask = new TestableRustCompileErrorHighlighter();
                highlightingTask.compile(parseResult.getSnapshot());

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


    protected void assertDescriptionMatches(String relFilePath,
                                            String description, boolean includeTestName, String ext, boolean checkFileExistence) throws Exception {
        File rubyFile = getDataFile(relFilePath);
        if (checkFileExistence && !rubyFile.exists()) {
            NbTestCase.fail("File " + rubyFile + " not found.");
        }

        File goldenFile = getDataFile(relFilePath + (includeTestName ? ("." + getName()) : "") + ext);
        if (!goldenFile.exists()) {
            if (!goldenFile.createNewFile()) {
                NbTestCase.fail("Cannot create file " + goldenFile);
            }
            FileWriter fw = new FileWriter(goldenFile);
            try {
                fw.write(description);
            }
            finally{
                fw.close();
            }
            if (failOnMissingGoldenFile()) {
                NbTestCase.fail("Created generated golden file " + goldenFile + "\nPlease re-run the test.");
            }
            return;
        }

        String expected = readFile(goldenFile);

        // Because the unit test differ is so bad...
        if (false) { // disabled
            if (!expected.equals(description)) {
                BufferedWriter fw = new BufferedWriter(new FileWriter("/tmp/expected.txt"));
                fw.write(expected);
                fw.close();
                fw = new BufferedWriter(new FileWriter("/tmp/actual.txt"));
                fw.write(description);
                fw.close();
            }
        }

        //drrb: don't trim. It removes the leading whitespace.
        String expectedTrimmed = expected.replaceFirst("\n+\\Z", ""); //expected.trim();
        String actualTrimmed = description.replaceFirst("\n+\\Z", ""); //description.trim();

        if (expectedTrimmed.equals(actualTrimmed)) {
            return; // Actual and expected content are equals --> Test passed
        } else {
            // We want to ignore different line separators (like \r\n against \n) because they
            // might be causing failing tests on a different operation systems like Windows :]
            String expectedUnified = expectedTrimmed.replaceAll("\r", "");
            String actualUnified = actualTrimmed.replaceAll("\r", "");

            if (expectedUnified.equals(actualUnified)) {
                return; // Only difference is in line separation --> Test passed
            }

            // There are some diffrerences between expected and actual content --> Test failed
//            fail(getContentDifferences(relFilePath, ext, includeTestName, expectedUnified, actualUnified));

            throw new ComparisonFailure(
                   description + " " + relFilePath,
                    expectedUnified,
                    actualUnified
            );
        }
    }

    public List<TestIndexFactoryImpl.TestIndexDocumentImpl> index(RustProject project) {
        try {

            EmbeddingIndexerFactory indexerFactory = getIndexerFactory();

            Sources sources = ProjectUtils.getSources(project);
            SourceGroup[] projectSourceGroups = sources.getSourceGroups(RustSourceGroup.NAME);
            List<TestIndexFactoryImpl.TestIndexDocumentImpl> list = new LinkedList<>();
            for (SourceGroup sourceGroup : projectSourceGroups) {
                FileObject sourceRoot = sourceGroup.getRootFolder();
                TestIndexFactoryImpl tifi = new TestIndexFactoryImpl();

                Context context = SPIAccessor.getInstance().createContext(
                        CacheFolder.getDataFolder(sourceRoot.toURL()),
                        sourceRoot.toURL(),
                        indexerFactory.getIndexerName(),
                        indexerFactory.getIndexVersion(),
                        tifi,
                        false,
                        false,
                        false,
                        SuspendSupport.NOP,
                        null,
                        null
                );

                Enumeration<? extends FileObject> sourceFiles = sourceRoot.getChildren(true);
                while (sourceFiles.hasMoreElements()) {
                    FileObject sourceFile = sourceFiles.nextElement();

                    Indexable indexable = SPIAccessor.getInstance().create(new FileObjectIndexable(sourceRoot, sourceFile));
                    try {
                        ParserManager.parse(singleton(getTestSource(sourceFile)), new UserTask() {
                            public @Override
                            void run(ResultIterator resultIterator) throws Exception {
                                Parser.Result parseResult = resultIterator.getParserResult();
                                if (parseResult != null) {
                                    EmbeddingIndexer indexer = indexerFactory.createIndexer(indexable, parseResult.getSnapshot());
                                    SPIAccessor.getInstance().index(indexer, indexable, parseResult, context);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        DocumentIndex index = SPIAccessor.getInstance().getIndexFactory(context).getIndex(context.getIndexFolder());
                        if (index != null) {
                            index.removeDirtyKeys(singleton(indexable.getRelativePath()));
                            index.store(true);
                        }

                    }

                    TestIndexFactoryImpl.TestIndexImpl tii = tifi.getTestIndex(context.getIndexFolder());
                    if (tii != null) {
                        List<TestIndexFactoryImpl.TestIndexDocumentImpl> documents = tii.documents.get(indexable.getRelativePath());
                        if (list != null) {
                            list.addAll(documents);
                        }
                    }
                }
            }
            return list;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<? extends IndexSearcher.Descriptor> searchIndex(RustProject project, String queryText, QuerySupport.Kind searchType) {
        try {
            IndexSearcher indexSearcher = getPreferredLanguage().getIndexSearcher();
            return indexSearcher.getTypes(project, queryText, searchType, new HelperStub());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class TestableRustCompileErrorHighlighter extends RustCompileErrorHighlighter {
        private final List<ErrorDescription> errors = new LinkedList<>();

        @Override
        protected void setErrors(Document document, String layerName, List<ErrorDescription> reportedErrors) {
            errors.addAll(reportedErrors);
        }
    }

    private static class CratesAlphabetically implements Comparator<Crate> {
        @Override
        public int compare(Crate left, Crate right) {
            return left.getFile().getPath().compareTo(right.getFile().getPath());
        }
    }

    private static class FilesAlphabetically implements Comparator<FileObject> {
        @Override
        public int compare(FileObject left, FileObject right) {
            return left.getPath().compareTo(right.getPath());
        }
    }


    private static class HelperStub implements IndexSearcher.Helper {
        @Override
        public Icon getIcon(ElementHandle element) {
            return null;
        }

        @Override
        public void open(FileObject fileObject, ElementHandle element) {

        }
    }
}
