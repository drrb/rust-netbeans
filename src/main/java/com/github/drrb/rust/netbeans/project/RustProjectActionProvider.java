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
package com.github.drrb.rust.netbeans.project;

import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.spi.project.ActionProvider;
import org.openide.util.Lookup;

public class RustProjectActionProvider implements ActionProvider {

    private static final Map<String, List<String>> COMMANDS;

    static {
        Map<String, List<String>> commands = new HashMap<>();
        commands.put(COMMAND_BUILD, asList("build"));
        commands.put(COMMAND_CLEAN, asList("clean"));
        commands.put(COMMAND_REBUILD, asList("clean", "build"));
        commands.put(COMMAND_TEST, asList("test"));
        COMMANDS = Collections.unmodifiableMap(commands);
    }

    private final Cargo cargo;

    public RustProjectActionProvider(RustProject project) {
        this(new Cargo(project));
    }

    RustProjectActionProvider(Cargo cargo) {
        this.cargo = cargo;
    }

    @Override
    public String[] getSupportedActions() {
        return COMMANDS.keySet().toArray(new String[COMMANDS.size()]);
    }

    @Override
    public void invokeAction(String action, Lookup context) throws IllegalArgumentException {
        switch (action) {
            case COMMAND_BUILD:
            case COMMAND_CLEAN:
            case COMMAND_TEST:
                cargo.run(action);
                break;
            case COMMAND_REBUILD:
                cargo.run("clean");
                cargo.run("build");
        }
    }

    @Override
    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        return true;
    }
}
