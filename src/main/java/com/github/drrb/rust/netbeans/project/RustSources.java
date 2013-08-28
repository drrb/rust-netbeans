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
package com.github.drrb.rust.netbeans.project;

import java.util.LinkedList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.support.GenericSources;

/**
 *
 */
public class RustSources implements Sources {

    private final RustProject project;

    public RustSources(RustProject project) {
        this.project = project;
    }

    @Override
    public SourceGroup[] getSourceGroups(String type) {
        List<SourceGroup> sourceGroups = new LinkedList<SourceGroup>();
        if (Sources.TYPE_GENERIC.equals(type)) {
            sourceGroups.add(GenericSources.group(project, project.getProjectDirectory(), "ProjectRoot", ProjectUtils.getInformation(project).getDisplayName(), null, null));
        } else if (RustSourceGroup.NAME.equals(type)) {
            sourceGroups.add(new RustSourceGroup(project));
        }
        return sourceGroups.toArray(new SourceGroup[sourceGroups.size()]);
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
    }
}
