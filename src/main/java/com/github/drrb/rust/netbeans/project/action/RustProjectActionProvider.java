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
import com.github.drrb.rust.netbeans.project.RustProject;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.netbeans.spi.project.ActionProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

public class RustProjectActionProvider implements ActionProvider {

    private static final Map<String, Command> COMMANDS;

    static {
        List<Command> supportedCommands = new LinkedList<>();
        supportedCommands.addAll(asList(CargoCommand.values()));
        supportedCommands.add(TestCommand.INSTANCE);
        supportedCommands.add(TestFileCommand.INSTANCE);
        Map<String, Command> commandIndex = new HashMap<>(supportedCommands.size());
        for (Command command : supportedCommands) {
            commandIndex.put(command.getId(), command);
        }
        COMMANDS = Collections.unmodifiableMap(commandIndex);
    }

    private final RustProject project;
    private final Cargo cargo;

    public RustProjectActionProvider(RustProject project) {
        this(project, new Cargo(project));
    }

    RustProjectActionProvider(RustProject project, Cargo cargo) {
        this.project = project;
        this.cargo = cargo;
    }

    @Override
    public String[] getSupportedActions() {
        return COMMANDS.keySet().toArray(new String[COMMANDS.size()]);
    }

    @Override
    public void invokeAction(String action, Lookup context) throws IllegalArgumentException {
        if (COMMANDS.containsKey(action)) {
            COMMANDS.get(action).run(new ProxyLookup(Lookups.fixed(project, cargo), context));
        }
    }

    @Override
    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        return true;
    }
}
