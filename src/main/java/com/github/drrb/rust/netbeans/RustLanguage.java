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
package com.github.drrb.rust.netbeans;

import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser;
import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import java.util.Collections;
import java.util.Set;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.indexing.PathRecognizerRegistration;
import org.openide.util.NbBundle;

@LanguageRegistration(mimeType = RustLanguage.MIME_TYPE)
@PathRecognizerRegistration(mimeTypes = RustLanguage.MIME_TYPE, sourcePathIds = RustLanguage.SOURCE_CLASSPATH_ID, libraryPathIds = RustLanguage.BOOT_CLASSPATH_ID, binaryLibraryPathIds = {})
public class RustLanguage extends DefaultLanguageConfig {

    public static final String MIME_TYPE = "text/x-rust-source";
    public static final String BOOT_CLASSPATH_ID = "classpath/rust-boot"; //NOI18N
    public static final String SOURCE_CLASSPATH_ID = "classpath/rust-source"; //NOI18N

    public static boolean isRustIdentifierChar(char c) {
        //TODO: is this true?
        return Character.isJavaIdentifierPart(c);
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(RustLanguage.class, MIME_TYPE);
    }

    @Override
    public String getPreferredExtension() {
        return "rs";
    }

    @Override
    public String getLineCommentPrefix() {
        return "//";
    }

    @Override
    public boolean isIdentifierChar(char c) {
        return isRustIdentifierChar(c);
    }

    @Override
    public Language<RustTokenId> getLexerLanguage() {
        return RustTokenId.language();
    }

    @Override
    public Parser getParser() {
        return new NetbeansRustParser();
    }

    //TODO: are these required? Is the annotation enough?
    @Override
    public Set<String> getLibraryPathIds() {
        return Collections.singleton(RustLanguage.BOOT_CLASSPATH_ID);
    }

    @Override
    public Set<String> getSourcePathIds() {
        return Collections.singleton(RustLanguage.SOURCE_CLASSPATH_ID);
    }
}
