/*
 * Copyright (C) 2018 Tim Boudreau
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
package com.github.drrb.rust.netbeans;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.contrib.yenta.Yenta;

/**
 * Allows deployment in multiple versions of NetBeans, with the caveat
 * that either of these modules may have broken its ABI in the meantime
 * (but in practice, they haven't changed in years).
 *
 * @author Tim Boudreau
 */
public class Installer extends Yenta {

    @Override
    protected Set<String> siblings() {
        return new HashSet<>(Arrays.asList("org.netbeans.modules.gsf.testrunner", "org.netbeans.modules.gsf.testrunner.ui"));
    }

}
