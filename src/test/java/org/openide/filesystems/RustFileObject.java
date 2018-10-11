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
package org.openide.filesystems;

import java.io.File;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.util.Task;
import org.openide.util.lookup.LookupIntruder;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 */
public class RustFileObject {

    public static FileObject forDocument(final Document doc) {
        try {
            File sourceFile = File.createTempFile("test", ".rs");
            sourceFile.deleteOnExit();
            Files.write(sourceFile.toPath(), doc.getText(0, doc.getLength()).getBytes(UTF_8));
            FileObject sourceFileObject = FileUtil.toFileObject(sourceFile);
            doc.putProperty(Document.StreamDescriptionProperty, DataObject.find(sourceFileObject));
            LookupIntruder.setLookups((ProxyLookup) sourceFileObject.getLookup(), Lookups.singleton(new EditorCookie() {

                @Override
                public void open() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean close() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public Task prepareDocument() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public StyledDocument openDocument() throws IOException {
                    return (StyledDocument) doc;
                }

                @Override
                public StyledDocument getDocument() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void saveDocument() throws IOException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean isModified() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public JEditorPane[] getOpenedPanes() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public Line.Set getLineSet() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            }));
            return sourceFileObject;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
