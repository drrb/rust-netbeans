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
package com.github.drrb.rust.netbeans.highlighting;

import com.github.drrb.rust.netbeans.RustLanguage;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.test.MockMimeLookup;
import org.netbeans.modules.csl.api.test.CslTestBase;
import org.netbeans.modules.csl.core.GsfParserFactory;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.ParserResult;
import org.openide.filesystems.FileUtil;

/**
 *
 */
public class RustSemanticAnalyzerCslTest extends CslTestBase {

    public RustSemanticAnalyzerCslTest(String testName) throws Exception {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //TODO: why do we have to do this? CslTestBase picks up our generated-layer.xml,
        // but it doesn't result in the parser factory being available in the MimeLookup.
        MockMimeLookup.setInstances(MimePath.parse(getPreferredMimeType()), GsfParserFactory.create(FileUtil.getConfigFile("Editors/text/x-rust-source/org-netbeans-modules-csl-core-GsfParserFactory-create.instance")));
        //TODO: php tests have this, but our tests seem to pass without it. Why?
        //TestLanguageProvider.register(getPreferredLanguage().getLexerLanguage());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testStructDeclaration() throws Exception {
        checkSemantic("semantic/struct.rs");
    }

    @Override
    protected void validateParserResult(@NullAllowed ParserResult result) {
        assertThat(result, not(nullValue()));
    }

    @Override
    protected DefaultLanguageConfig getPreferredLanguage() {
        return new RustLanguage();
    }

    @Override
    protected String getPreferredMimeType() {
        return RustLanguage.MIME_TYPE;
    }

}
