/**
 * Copyright (C) 2013 drrb
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
package com.github.drrb.rust.netbeans;

import com.github.drrb.rust.netbeans.parsing.NetbeansRustParser;
import com.github.drrb.rust.netbeans.highlighting.RustSemanticAnalyzer;
import com.github.drrb.rust.netbeans.parsing.RustTokenId;
import com.github.drrb.rust.netbeans.highlighting.RustOccurrencesFinder;
import com.github.drrb.rust.netbeans.refactor.RustInstantRenamer;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.api.InstantRenamer;
import org.netbeans.modules.csl.api.OccurrencesFinder;
import org.netbeans.modules.csl.api.SemanticAnalyzer;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;
import org.openide.util.NbBundle;

@LanguageRegistration(mimeType = RustLanguage.MIME_TYPE)
public class RustLanguage extends DefaultLanguageConfig {

    public static final String MIME_TYPE = "text/x-rust-source";

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
    public Language<RustTokenId> getLexerLanguage() {
        return RustTokenId.getLanguage();
    }

    @Override
    public Parser getParser() {
        return new NetbeansRustParser();
    }

    @Override
    public SemanticAnalyzer getSemanticAnalyzer() {
        return new RustSemanticAnalyzer();
    }

    @Override
    public boolean hasOccurrencesFinder() {
        return true;
    }

    @Override
    public OccurrencesFinder getOccurrencesFinder() {
        return new RustOccurrencesFinder();
    }

    @Override
    public InstantRenamer getInstantRenamer() {
        return new RustInstantRenamer();
    }
}