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
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;

/**
 *
 */
public class RustSourceGroup implements SourceGroup {

    public static final String NAME = "rust";
    private final RustProject project;

    public RustSourceGroup(RustProject project) {
        this.project = project;
    }

    @Override
    public FileObject getRootFolder() {
        return project.getProjectDirectory().getFileObject("src");
    }

    @Override
    public String getName() {
        return "Sources";
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public Icon getIcon(boolean opened) {
        return null;
    }

    @Override
    public boolean contains(FileObject file) {
        return file.getPath().startsWith(getRootFolder().getPath());
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    }
}
