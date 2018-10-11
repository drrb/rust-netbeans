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
package com.github.drrb.rust.netbeans.parsing;

import com.github.drrb.rust.netbeans.RustLanguage;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

import java.util.Collection;
import java.util.EnumSet;

import static java.util.Collections.unmodifiableSet;

public class RustLanguageHierarchy extends LanguageHierarchy<RustTokenId> {
    private static final Collection<RustTokenId> TOKEN_IDS = unmodifiableSet(EnumSet.allOf(RustTokenId.class));

    @Override
    protected Collection<RustTokenId> createTokenIds() {
        return TOKEN_IDS;
    }

    @Override
    protected Lexer<RustTokenId> createLexer(LexerRestartInfo<RustTokenId> info) {
        return new NetbeansRustLexer(info);
    }

    @Override
    protected String mimeType() {
        return RustLanguage.MIME_TYPE;
    }
}
