/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.csl.api.test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.DataLoadersBridge;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.text.FilterDocument;
import org.openide.util.UserQuestionException;

/**
 *
 */
public class GsfUtilitiesHack {

    private static final Logger LOG = Logger.getLogger(GsfUtilitiesHack.class.getName());

    public static Document getDocument(FileObject fileObject, boolean openIfNecessary) {
        return getDocument(fileObject, openIfNecessary, false);
    }

    public static Document getDocument(FileObject fileObject, boolean openIfNecessary, boolean skipLarge) {
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

    private static BaseDocument asBaseDocument(StyledDocument document) {
        if (document instanceof FilterDocument) {
            try {
                Field originalField = FilterDocument.class.getDeclaredField("original");
                originalField.setAccessible(true);
                return (BaseDocument) originalField.get(document);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return (BaseDocument) document;
    }
}
