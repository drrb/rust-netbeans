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
package com.github.drrb.rust.netbeans.indexing;

import com.github.drrb.rust.netbeans.completion.RustElementHandle;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.IndexSearcher;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.GsfUtilities;
import org.openide.filesystems.FileObject;

/**
 *
 */
public class RustIndexSearchResult extends IndexSearcher.Descriptor {

    private final String simpleName;
    private final String moduleName;
    private final ElementKind elementKind;
    private final OffsetRange offsetRange;
    private final FileObject fileObject;
    private final Project project;
    private final Set<Modifier> modifiers;

    public RustIndexSearchResult(String simpleName, String moduleName, ElementKind elementKind, OffsetRange offsetRange, FileObject fileObject, Project project) {
        this.simpleName = simpleName;
        this.moduleName = moduleName;
        this.elementKind = elementKind;
        this.offsetRange = offsetRange;
        this.fileObject = fileObject;
        this.project = project;
        this.modifiers = Collections.emptySet();
    }

    @Override
    public ElementHandle getElement() {
        return RustElementHandle.with(simpleName, offsetRange, elementKind).withModifiers(modifiers).withFileObject(fileObject).build();
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }

    @Override
    public String getOuterName() {
        return null;
    }

    @Override
    public String getTypeName() {
        return simpleName;
    }

    @Override
    public String getContextName() {
        return moduleName;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getProjectName() {
        return project == null ? null : ProjectUtils.getInformation(project).getDisplayName();
    }

    @Override
    public Icon getProjectIcon() {
        return project == null ? null : ProjectUtils.getInformation(project).getIcon();
    }

    @Override
    public FileObject getFileObject() {
        return fileObject;
    }

    @Override
    public int getOffset() {
        return offsetRange.getStart();
    }

    @Override
    public void open() {
        if (fileObject != null) {
            GsfUtilities.open(fileObject, getOffset(), simpleName);
        } else {
            Logger logger = Logger.getLogger(RustIndexSearchResult.class.getName());
            logger.log(Level.INFO, "Cannot find {0}", fileObject.toURL());
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
