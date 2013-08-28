/**
 * Copyright (C) 2013 drrb
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
import java.lang.reflect.Field;
import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Source;

/**
 *
 */
public class TestParsing {

    public static NetbeansRustParser.NetbeansRustParserResult parse(CharSequence input) {
        try {
            Snapshot snapshot = snapshotOf(input);
            NetbeansRustParser parser = new NetbeansRustParser();
            parser.parse(snapshot, null, null);
            return parser.getResult(null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Snapshot snapshotOf(CharSequence input) {
        try {
            Document document = rustDocumentContaining(input);
            Source source = Source.create(document);
            Snapshot snapshot = source.createSnapshot();
            Field tokenHierarchyField = Snapshot.class.getDeclaredField("tokenHierarchy");
            tokenHierarchyField.setAccessible(true);
            try {
                tokenHierarchyField.set(snapshot, rustTokenHierarchyFor(input));
            } finally {
                tokenHierarchyField.setAccessible(false);
            }
            return snapshot;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Document rustDocumentContaining(CharSequence input) {
        return RustDocument.containing(input);
    }

    public static TokenHierarchy<CharSequence> rustTokenHierarchyFor(CharSequence input) {
        return TokenHierarchy.create(input, RustTokenId.language());
    }

    public static TokenSequence<RustTokenId> rustTokenSequenceFor(CharSequence input) {
        return rustTokenHierarchyFor(input).tokenSequence(RustTokenId.language());
    }
}
