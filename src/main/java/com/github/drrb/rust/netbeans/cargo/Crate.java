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
package com.github.drrb.rust.netbeans.cargo;

import com.github.drrb.rust.netbeans.rustbridge.RustCrateType;
import java.util.Objects;
import org.openide.filesystems.FileObject;

/**
 *
 */
public class Crate {

    private final RustCrateType type;
    private final FileObject file;

    public Crate(RustCrateType type, FileObject file) {
        this.type = type;
        this.file = file;
    }

    public RustCrateType getType() {
        return type;
    }

    public FileObject getFile() {
        return file;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, file);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Crate other = (Crate) obj;
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.file, other.file)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("Crate(%s): '%s'", type, file);
    }
}
