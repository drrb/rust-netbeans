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
package com.github.drrb.rust.netbeans.swing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 */
public class PathsTableModel extends AbstractTableModel {

    private final List<String> paths = new LinkedList<>();

    @Override
    public int getRowCount() {
        return paths.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getValueAt(int rowIndex, int columnIndex) {
        return paths.get(rowIndex);
    }

    public void setPaths(List<String> newPaths) {
        paths.clear();
        paths.addAll(newPaths);
        fireTableDataChanged();
    }

    public List<String> getPaths() {
        return new ArrayList<>(paths);
    }

    public void addPath(String path) {
        paths.add(path);
        fireTableDataChanged();
    }

    public void removePath(String path) {
        paths.remove(path);
        fireTableDataChanged();
    }

    public void removePathsAt(int... rows) {
        for (int i = rows.length - 1; i >= 0; i--) {
            int row = rows[i];
            paths.remove(row);
        }
        fireTableDataChanged();
    }
}
