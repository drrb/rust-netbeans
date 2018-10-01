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
package com.github.drrb.rust.netbeans.parsing.antlr;

import java.util.Optional;
import java.util.Set;
import org.netbeans.modules.csl.api.ColoringAttributes;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 * @author Tim Boudreau
 */
public interface RustSourceRegion {

    Set<ColoringAttributes> attributes();

    Optional<RustElementKind> childOf();

    boolean hasVisibility(RustVisibility vis);

    boolean isMutable();

    boolean isStatic();

    RustElementKind kind();

    OffsetRange range();

    Set<RustVisibility> visibility();

}
