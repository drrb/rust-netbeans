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
package org.netbeans.modules.csl.api.test;

import com.google.common.base.Strings;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.test.MockMimeLookup;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.project.Project;
import org.netbeans.editor.BaseDocument;
import org.netbeans.lib.lexer.test.TestLanguageProvider;
import org.netbeans.modules.csl.api.Formatter;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;

import javax.swing.*;
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import java.awt.*;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *
 */
public class CslTestHelper extends CslTestBase implements TestRule {
    @Retention(RUNTIME)
    public @interface RunInEventQueueThread {

    }
    @Retention(RUNTIME)
    public @interface Classpath {
        String id();
        String[] value();
    }

    private boolean runInEventQueueThread = false;
    protected Map<String, ClassPath> classpaths = null;

    public static CslTestHelper forLanguage(DefaultLanguageConfig language) {
        return new CslTestHelper(language);
    }

    private final DefaultLanguageConfig language;
    public CslTestHelper(DefaultLanguageConfig language) {
        super("");
        this.language = language;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Classpath definedClasspath = description.getAnnotation(Classpath.class);
                if (definedClasspath != null) {
                    FileObject[] roots = new FileObject[definedClasspath.value().length];
                    for (int i = 0; i < definedClasspath.value().length; i++) {
                        String definedSourceRoot = definedClasspath.value()[i];
                        roots[i] = getTestFile(definedSourceRoot);
                    }
                    ClassPath classPath = ClassPathSupport.createClassPath(roots);
                    classpaths = new HashMap<>();
                    classpaths.put(definedClasspath.id(), classPath);
                }
                setUp();
                try {
                    if (description.getAnnotation(RunInEventQueueThread.class) == null) {
                        base.evaluate();
                    } else {
                        runInEventQueueThread = true;
                        try {
                            EventQueue.invokeAndWait(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        base.evaluate();
                                    } catch (RuntimeException | Error e) {
                                        throw e;
                                    } catch (Throwable e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        } catch (InvocationTargetException ex) {
                            throw ex.getCause();
                        }
                    }
                } catch (AssumptionViolatedException e) {
                    throw e;
                } catch (Throwable t) {
                    throw t;
                } finally {
                    tearDown();
                }
            }
        };
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //This makes our language available to the formatter (at least: it may do other things too)!
        TestLanguageProvider.register(getPreferredLanguage().getLexerLanguage());

        //Reference files in the main filesystem from the mime lookup:

        //This one adds (at least) a DocumentFactory implementation, which (at least) allows getTestSource() to attach a fileobject to a source
        MockMimeLookup.setInstances(MimePath.EMPTY, mimeObjects(MimePath.EMPTY, "/"));
        //TODO: why do we have to do this? CslTestBase picks up our generated-layer.xml,
        // but it doesn't result in the parser factory, formatter factory, etc being
        // available in the MimeLookup.
        //TODO: maybe get more selective (specify per test what you need with annotations)
        // so that we know more of what's going on behind the scenes
        //TODO: might need to recurse into subdirectories (getChidren(true)), although
        // that seemed to add some files that broke things...
        MimePath mimePath = MimePath.parse(getPreferredMimeType());
        Object[] mimeObjects = mimeObjects(mimePath, "/", "/BracesMatchers");
        MockMimeLookup.setInstances(mimePath, mimeObjects);
    }

    private Object[] mimeObjects(MimePath mimePath, String... mimeRoots) {
        List<Object> mimeObjects = new LinkedList<>();
        for (String mimeRoot : mimeRoots) {
            String mimeFilePath = "Editors/" + mimePath.getPath() + mimeRoot;
            FileObject mimeDir = FileUtil.getConfigFile(mimeFilePath);
            assertNotNull(String.format("Tried to load files from config dir '%s', but it did not exist.", mimeFilePath), mimeDir);
            FileObject[] mimeFiles = mimeDir.getChildren();
            for (FileObject mimeFile : mimeFiles) {
                if (mimeFile.isData()) {
                    mimeObjects.add(FileUtil.getConfigObject(mimeFile.getPath(), Object.class));
                }
            }
        }
        return mimeObjects.toArray();
    }

    @Override
    public File getDataDir() {
        return new File(super.getDataDir().getParentFile(), "test-data");
    }

    @Override
    protected DefaultLanguageConfig getPreferredLanguage() {
        return language;
    }

    @Override
    protected String getPreferredMimeType() {
        return getPreferredLanguage().getLexerLanguage().mimeType();
    }

    @Override
    protected File getDataSourceDir() {
        return new File("src/test/data");
    }

    public FileObject getTestFile(String relFilePath) {
        return super.getTestFile(relFilePath);
    }

    @Override
    protected void configureIndenters(Document document, Formatter formatter, boolean indentOnly, String mimeType) {
        // Parent implementation messes with the MimeLookup, so don't call it
    }

    @Override
    protected boolean runInEQ() {
        return runInEventQueueThread;
    }

    @Override
    protected Map<String, ClassPath> createClassPathsForTest() {
        return classpaths;
    }

    @SuppressWarnings("unchecked")
    public <T extends TokenId> TokenSequence<T> tokenize(String text) {
        TokenHierarchy<?> hi = TokenHierarchy.create(text, getPreferredLanguage().getLexerLanguage());
        return hi.tokenSequence(getPreferredLanguage().getLexerLanguage());
    }

    @Override
    public void assertDescriptionMatches(String relFilePath, String description, boolean includeTestName, String ext) throws Exception {
        super.assertDescriptionMatches(relFilePath, description, includeTestName, ext);
    }

    public void checkBracketsMatch(String sourceWithBracketPointers) throws Exception {
        super.assertMatches2(sourceWithBracketPointers);
    }

    public void reformatFileContents(String file) throws Exception {
        super.reformatFileContents(file, null);
    }

    @Override
    public void checkSemantic(String relFilePath) throws Exception {
        super.checkSemantic(relFilePath);
    }

    public void checkParseMessages(String relFilePath) throws Exception {
        super.checkErrors(relFilePath);
    }

    public void checkLogicalView(String relProjectPath) throws Exception {
        Project project = getTestProject(relProjectPath);
        LogicalViewProvider logicalViewProvider = project.getLookup().lookup(LogicalViewProvider.class);
        String logicalView = new TreeView(logicalViewProvider.createLogicalView()).render();
        assertDescriptionMatches(relProjectPath, logicalView, false, ".logicalview");
    }

    //From PHPNetLineIndenterTest
    public void testIndentInFile(String file) throws Exception {
        testIndentInFile(file, null);
    }

    public void testIndentInFile(String file, IndentPrefs indentPrefs) throws Exception {
        FileObject fo = getTestFile(file);
        assertNotNull(fo);
        String source = readFile(fo);

        int sourcePos = source.indexOf('^');
        assertNotNull(sourcePos);
        String sourceWithoutMarker = source.substring(0, sourcePos) + source.substring(sourcePos + 1);
        Formatter formatter = getFormatter(indentPrefs);

        JEditorPane ta = getPane(sourceWithoutMarker);
        Caret caret = ta.getCaret();
        caret.setDot(sourcePos);
        BaseDocument doc = (BaseDocument) ta.getDocument();
        if (formatter != null) {
            configureIndenters(doc, formatter, true);
        }

        setupDocumentIndentation(doc, indentPrefs);

        runKitAction(ta, DefaultEditorKit.insertBreakAction, "\n");

        doc.getText(0, doc.getLength());
        doc.insertString(caret.getDot(), "^", null);

        String target = doc.getText(0, doc.getLength());
        assertDescriptionMatches(file, target, false, ".indented");
    }

    public void indexFile(String relFilePath) throws Exception {
        super.indexFile(relFilePath);
    }

    public void checkIndexer(String relFilePath) throws Exception {
        super.checkIndexer(relFilePath);
    }

    protected static class TreeView {
        private final Node root;
        private StringBuilder view;

        public TreeView(Node root) {
            this.root = root;
        }

        public String render() {
            view = new StringBuilder();
            renderTree(root, 0);
            return view.toString();
        }

        private void renderTree(Node node, int indentDepth) {
            String indent;
            if (indentDepth == 0) {
                indent = "";
            } else {
                indent = Strings.repeat(" ", indentDepth * 4 - 2) + "- ";
            }
            view.append(indent).append(node.getDisplayName()).append(":");
            Node[] childNodes = node.getChildren().getNodes(true);
            if (childNodes.length == 0) {
                view.append(" []");
            }
            view.append("\n");
            for (Node child : childNodes) {
                renderTree(child, indentDepth + 1);
            }
        }
    }
}
