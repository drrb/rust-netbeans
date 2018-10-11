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
package com.github.drrb.rust.netbeans.classpath;

import com.github.drrb.rust.netbeans.project.RustProject;
import com.google.common.annotations.VisibleForTesting;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.netbeans.spi.project.ui.ProjectOpenedHook;

/**
 *
 */
@ProjectServiceProvider(service = ProjectOpenedHook.class, projectType = RustProject.TYPE)
public class ClasspathSettingProjectOpenedHook extends ProjectOpenedHook {

    private final RustProject project;
    private final GlobalPathRegistry globalPathRegistry;

    @SuppressWarnings("unused") // Used by NetBeans
    public ClasspathSettingProjectOpenedHook(Project project) {
        this((RustProject) project, GlobalPathRegistry.getDefault());
    }

    ClasspathSettingProjectOpenedHook(RustProject project, GlobalPathRegistry globalPathRegistry) {
        this.project = project;
        this.globalPathRegistry = globalPathRegistry;
    }

    @Override
    @VisibleForTesting
    public void projectOpened() {
        project.getClassPaths().forEach(globalPathRegistry::register);
    }

    @Override
    protected void projectClosed() {
    }

}
