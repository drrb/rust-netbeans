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
package com.github.drrb.rust.netbeans.cargo.test;

import com.github.drrb.rust.netbeans.cargo.Cargo;
import static java.util.Arrays.asList;

import java.util.LinkedList;
import java.util.List;

public class FakeCargo extends Cargo {
    private final FakeCargoCommandFuture commandFuture;
    private List<String> commandsRun;

    public FakeCargo(FakeCargoCommandFuture commandFuture) {
        super(null, null, null, null);
        this.commandFuture = commandFuture;
    }

    @Override
    public FakeCargoCommandFuture run(Command... commands) {
        this.commandsRun = new LinkedList<>();
        for (Command command : commands) {
            commandsRun.add(command.renderForCargoPath("cargo"));
        }
        return commandFuture;
    }

    public List<String> getCommandsRun() {
        return commandsRun;
    }
}
