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
import com.google.common.collect.ObjectArrays;
import java.awt.EventQueue;
import java.io.File;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.reflect.InvocationTargetException;
import static java.util.Arrays.asList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import static junit.framework.Assert.assertNotNull;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.test.MockMimeLookup;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.lib.lexer.test.TestLanguageProvider;
import org.netbeans.modules.csl.api.Formatter;
import org.netbeans.modules.csl.api.test.CslTestBase;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.editor.bracesmatching.BraceMatchingSidebarFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 */
public class CslTestHelper extends CslTestBase implements TestRule {

    @Retention(RUNTIME)
    public @interface RunInEventQueueThread {
    }

    public CslTestHelper() {
        super("");
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                setUp();
                try {
                    if (description.getAnnotation(RunInEventQueueThread.class) == null) {
                        base.evaluate();
                    } else {
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
        //This makes RustLanguage available to the formatter (at least: it may do other things too)!
        TestLanguageProvider.register(getPreferredLanguage().getLexerLanguage());

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
        return new RustLanguage();
    }

    @Override
    protected String getPreferredMimeType() {
        return RustLanguage.MIME_TYPE;
    }

    @Override
    protected File getDataSourceDir() {
        return new File("src/test/data");
    }

    @Override
    protected void configureIndenters(Document document, Formatter formatter, boolean indentOnly, String mimeType) {
        // Parent implementation messes with the MimeLookup, so don't call it
    }

    @Override
    protected boolean runInEQ() {
        // Formatter test needs this
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T extends TokenId> TokenSequence<T> tokenize(String text) {
        TokenHierarchy<?> hi = TokenHierarchy.create(text, getPreferredLanguage().getLexerLanguage());
        return hi.tokenSequence(getPreferredLanguage().getLexerLanguage());
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
}
