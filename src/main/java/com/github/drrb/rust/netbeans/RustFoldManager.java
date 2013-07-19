/*
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

import java.util.EnumSet;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.editor.fold.Fold;
import org.netbeans.api.editor.fold.FoldHierarchy;
import org.netbeans.api.editor.fold.FoldType;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.fold.FoldHierarchyTransaction;
import org.netbeans.spi.editor.fold.FoldManager;
import org.netbeans.spi.editor.fold.FoldOperation;
import org.openide.util.Exceptions;
import static com.github.drrb.rust.netbeans.RustTokenId.*;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.fold.FoldManagerFactory;

public class RustFoldManager implements FoldManager {
    
    private static final FoldType COMMENT_FOLD_TYPE = new FoldType("/*...*/");
    private FoldOperation operations;

    @Override
    public void init(FoldOperation operations) {
        this.operations = operations;
    }

    @Override
    public void initFolds(FoldHierarchyTransaction transaction) {
        removeAllFolds(transaction);
        createAllFolds(transaction);
    }

    @Override
    public void insertUpdate(DocumentEvent event, FoldHierarchyTransaction transaction) {
        removeAllFolds(transaction);
        createAllFolds(transaction);
    }

    @Override
    public void removeUpdate(DocumentEvent event, FoldHierarchyTransaction transaction) {
        removeAllFolds(transaction);
        createAllFolds(transaction);
    }

    @Override
    public void changedUpdate(DocumentEvent event, FoldHierarchyTransaction transaction) {
        removeAllFolds(transaction);
        createAllFolds(transaction);
    }

    @Override
    public void removeEmptyNotify(Fold fold) {
    }

    @Override
    public void removeDamagedNotify(Fold fold) {
    }

    @Override
    public void expandNotify(Fold fold) {
    }

    @Override
    public void release() {
    }

    private void removeAllFolds(FoldHierarchyTransaction transaction) {
        FoldHierarchy hierarchy = operations.getHierarchy();
        Fold rootFold = hierarchy.getRootFold();
        recursivelyRemoveFolds(rootFold, transaction);
    }
    
    private void recursivelyRemoveFolds(Fold fold, FoldHierarchyTransaction transaction) {
        for (int i = 0; i < fold.getFoldCount(); i++) {
            Fold childFold = fold.getFold(i);
            recursivelyRemoveFolds(childFold, transaction);
        }
        if (!fold.equals(fold.getHierarchy().getRootFold())) {
            operations.removeFromHierarchy(fold, transaction);
        }
    }

    private void createAllFolds(FoldHierarchyTransaction transaction) {
        FoldHierarchy hierarchy = operations.getHierarchy();
        Document document = hierarchy.getComponent().getDocument();
        TokenHierarchy<Document> hi = TokenHierarchy.get(document);
        TokenSequence<RustTokenId> ts = (TokenSequence<RustTokenId>) hi.tokenSequence();
        while (ts.moveNext()) {
            int offset = ts.offset();
            Token<RustTokenId> token = ts.token();
            RustTokenId id = token.id();
            if (EnumSet.of(OTHER_BLOCK_COMMENT, OUTER_DOC_COMMENT).contains(id)) {
                FoldType type = COMMENT_FOLD_TYPE;
                try {
                    operations.addToHierarchy(
                            type,
                            type.toString(),
                            false,
                            offset,
                            offset + token.length(),
                            0,
                            0,
                            hierarchy, //Could be null, but maybe we pass this so we can get at it later?
                            transaction);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @MimeRegistration(mimeType = RustLanguage.MIME_TYPE, service = FoldManagerFactory.class)
    public static class RustFoldManagerFactory implements FoldManagerFactory {

        @Override
        public FoldManager createFoldManager() {
            return new RustFoldManager();
        }
    }
}