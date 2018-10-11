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
package com.github.drrb.rust.netbeans.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.csl.api.DataLoadersBridge;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.util.UserQuestionException;

/**
 * @deprecated We're using this because GsfUtilities casts the document to
 * a BaseDocument (unnecessarily?), but we need a StyledDocument to calculate
 * line offsets with NbDocument.findLineOffset. Why?
 */
@Deprecated
public class GsfUtilitiesHack {

    private static final Logger LOG = Logger.getLogger(GsfUtilitiesHack.class.getName());

    /**
     * @deprecated We're using this because GsfUtilities casts the document to
     * a BaseDocument (unnecessarily?), but we need a StyledDocument to calculate
     * line offsets with NbDocument.findLineOffset. Why?
     */
    @Deprecated
    public static StyledDocument getDocument(FileObject fileObject, boolean openIfNecessary) {
        return getDocument(fileObject, openIfNecessary, false);
    }

    /**
     * @deprecated We're using this because GsfUtilities casts the document to
     * a BaseDocument (unnecessarily?), but we need a StyledDocument to calculate
     * line offsets with NbDocument.findLineOffset. Why?
     */
    @Deprecated
    public static StyledDocument getDocument(FileObject fileObject, boolean openIfNecessary, boolean skipLarge) {
        if (skipLarge) {
            // Make sure we're not dealing with a huge file!
            // Causes issues like 132306
            // openide.loaders/src/org/openide/text/DataEditorSupport.java
            // has an Env#inputStream method which posts a warning to the user
            // if the file is greater than 1Mb...
            //SG_ObjectIsTooBig=The file {1} seems to be too large ({2,choice,0#{2}b|1024#{3} Kb|1100000#{4} Mb|1100000000#{5} Gb}) to safely open. \n\
            //  Opening the file could cause OutOfMemoryError, which would make the IDE unusable. Do you really want to open it?

            // Apparently there is a way to handle this
            // (see issue http://www.netbeans.org/issues/show_bug.cgi?id=148702 )
            // but for many cases, the user probably doesn't want really large files as indicated
            // by the skipLarge parameter).
            if (fileObject.getSize() > 1024 * 1024) {
                return null;
            }
        }

        try {
            EditorCookie ec = fileObject.isValid() ? DataLoadersBridge.getDefault().getCookie(fileObject, EditorCookie.class) : null;
            if (ec != null) {
                if (openIfNecessary) {
                    try {
                        return ec.openDocument();
                    } catch (UserQuestionException uqe) {
                        uqe.confirmed();
                        return ec.openDocument();
                    }
                } else {
                    return ec.getDocument();
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.WARNING, null, ex);
        }

        return null;
    }
}
