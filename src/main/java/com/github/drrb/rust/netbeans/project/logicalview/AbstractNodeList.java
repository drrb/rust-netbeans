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
package com.github.drrb.rust.netbeans.project.logicalview;

import javax.swing.event.ChangeListener;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.util.ChangeSupport;

public abstract class AbstractNodeList<K> implements NodeList<K> {

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    @Override
    public void addChangeListener(ChangeListener list) {
        changeSupport.addChangeListener(list);
    }

    @Override
    public void removeChangeListener(ChangeListener list) {
        changeSupport.removeChangeListener(list);
    }

    protected void fireChange() {
        changeSupport.fireChange();
    }

    @Override
    public void addNotify() {
    }

    @Override
    public void removeNotify() {
    }
}
