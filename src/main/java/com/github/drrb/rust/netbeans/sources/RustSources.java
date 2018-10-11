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
package com.github.drrb.rust.netbeans.sources;

import com.github.drrb.rust.netbeans.project.RustProject;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.LookupProvider;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.netbeans.spi.project.support.GenericSources;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 */
@ProjectServiceProvider(service = Sources.class, projectType = RustProject.TYPE)
public class RustSources implements Sources, LookupProvider {

    private final Project project;

    public RustSources(Project project) {
        //WARNING: don't examine the project in the constructor, as per ProjectServiceProvider's JavaDoc
        this.project = project;
    }

    /**
     * Implementing LookupProvider to silence warnings.
     */
    @Override
    public Lookup createAdditionalLookup(Lookup baseContext) {
        return Lookup.EMPTY;
    }

    private RustProject project() {
        return project.getLookup().lookup(RustProject.class);
    }

    @Override
    public SourceGroup[] getSourceGroups(String type) {
        List<SourceGroup> sourceGroups = new LinkedList<>();
        if (Sources.TYPE_GENERIC.equals(type)) {
            // Return the project directory. Required by the Sources interface
            sourceGroups.add(projectRootFolderAsSourceGroup());
        } else if (RustSourceGroup.NAME.equals(type)) {
            sourceGroups.add(new RustSourceGroup(project()));
        }
        return sourceGroups.toArray(new SourceGroup[sourceGroups.size()]);
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
    }

    private SourceGroup projectRootFolderAsSourceGroup() {
        FileObject rootFolder = project.getProjectDirectory();
        String name = "ProjectRoot";
        String displayName = ProjectUtils.getInformation(project).getDisplayName();
        return GenericSources.group(project, rootFolder, name, displayName, null, null);
    }
}
