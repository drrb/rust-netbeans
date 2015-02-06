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
import java.io.File;
import static java.util.Arrays.stream;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.test.MockMimeLookup;
import org.netbeans.lib.lexer.test.TestLanguageProvider;
import org.netbeans.modules.csl.api.test.CslTestBase;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 */
public class CslTestHelper extends CslTestBase implements TestRule {
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
                    base.evaluate();
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
        //TODO: might need to recurse into subdirectories (getChidren(true))
        FileObject[] mimeFiles = FileUtil.getConfigFile("Editors/text/x-rust-source").getChildren();
        Object[] mimeObjects = stream(mimeFiles).filter(FileObject::isData).map(FileObject::getPath).map(path -> FileUtil.getConfigObject(path, Object.class)).toArray();
        MockMimeLookup.setInstances(MimePath.parse(getPreferredMimeType()), mimeObjects);
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
        //TODO: parent implementation gives a warning about 'dump' files. What are they?
        return getDataDir();
    }
}
