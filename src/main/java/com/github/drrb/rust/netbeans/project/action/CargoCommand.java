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
package com.github.drrb.rust.netbeans.project.action;

import com.github.drrb.rust.netbeans.cargo.Cargo;
import static org.netbeans.spi.project.ActionProvider.*;
import org.openide.util.Lookup;

/**
 *
 */
public enum CargoCommand implements Command {

    BUILD(COMMAND_BUILD, Cargo.BUILD),
    CLEAN(COMMAND_CLEAN, Cargo.CLEAN),
    REBUILD(COMMAND_REBUILD, Cargo.CLEAN, Cargo.BUILD),
    RUN(COMMAND_RUN, Cargo.RUN);

    private final String id;
    private final Cargo.Command[] cargoCommands;

    private CargoCommand(String id, Cargo.Command... cargoCommands) {
        this.id = id;
        this.cargoCommands = cargoCommands;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void run(Lookup context) {
        Cargo cargo = context.lookup(Cargo.class);
        cargo.run(cargoCommands);
    }

}
