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
package com.github.drrb.rust.netbeans.classpath;

import com.github.drrb.rust.netbeans.RustLanguage;
import com.github.drrb.rust.netbeans.project.RustProject;
import com.github.drrb.rust.netbeans.project.RustSourceGroup;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.filesystems.FileObject;

/**
 *
 */
@ProjectServiceProvider(service = ProjectOpenedHook.class, projectType = RustProject.TYPE)
public class ClasspathSettingProjectOpenedHook extends ProjectOpenedHook {

    private final Project project;
    private final GlobalPathRegistry globalPathRegistry;

    public ClasspathSettingProjectOpenedHook(Project project) {
        this(project, GlobalPathRegistry.getDefault());
    }

    ClasspathSettingProjectOpenedHook(Project project, GlobalPathRegistry globalPathRegistry) {
        this.project = project;
        this.globalPathRegistry = globalPathRegistry;
    }

    @Override
    protected void projectOpened() {
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] projectSourceGroups = sources.getSourceGroups(RustSourceGroup.NAME);
        FileObject[] projectSourceGroupRoots = getRoots(projectSourceGroups);
        ClassPath projectClassPath = ClassPathSupport.createClassPath(projectSourceGroupRoots);
        ClassPath[] projectClassPaths = {projectClassPath};

        //TODO: add Rust library paths too, when the parser is fast enough
        globalPathRegistry.register(RustLanguage.SOURCE_CLASSPATH_ID, projectClassPaths);
    }

    @Override
    protected void projectClosed() {
    }

    private FileObject[] getRoots(SourceGroup[] sourceGroups) {
        FileObject[] roots = new FileObject[sourceGroups.length];
        for (int i = 0; i < sourceGroups.length; i++) {
            SourceGroup sourceGroup = sourceGroups[i];
            roots[i] = sourceGroup.getRootFolder();
        }
        return roots;
    }
}
