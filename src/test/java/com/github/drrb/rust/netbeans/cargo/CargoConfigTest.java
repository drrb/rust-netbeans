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
package com.github.drrb.rust.netbeans.cargo;

import com.github.drrb.rust.netbeans.test.NetbeansWithRust;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 */
public class CargoConfigTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();
    @Rule
    public final NetbeansWithRust netbeans = new NetbeansWithRust();

    @Test
    public void shouldFindCratesInCargoConfig() throws Exception {
        netbeans.checkCrates("crates/types");
    }

    @Test
    public void shouldFindCratesForEachSource() throws Exception {
        netbeans.checkCrates("crates/dependencies");
    }

}
