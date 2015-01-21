/*
 * Copyright (C) 2015 drrb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.drrb.rust.netbeans.project;

import java.io.File;
import java.io.IOException;
import org.openide.util.Exceptions;

/**
 *
 */
public class Cargo {
    private final RustProject project;

    public Cargo(RustProject project) {
        this.project = project;
    }

    public void run(String command) {
        try {
            Process process = new ProcessBuilder()
                    .command("/bin/bash", "-lc", "cargo " + command + " --verbose")
                    .directory(project.dir())
                    .inheritIO()
                    .start();
            process.waitFor();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InterruptedException ex) {
            System.out.println("Cargo interrupted. Cleaning up...");
        }
    }
}
