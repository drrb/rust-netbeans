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
package com.github.drrb.rust.netbeans.parsing.index;

import com.github.drrb.rust.netbeans.util.Option;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 */
public class RangeMap<T> {

    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final Map<OffsetRange, T> items = new HashMap<>();

    public void put(OffsetRange offsetRange, T item) {
        items.put(offsetRange, item);
    }

    public Option<T> get(int offset) {
        for (Map.Entry<OffsetRange, T> entry : items.entrySet()) {
            OffsetRange offsetRange = entry.getKey();
            T item = entry.getValue();
            if (offsetRange.containsInclusive(offset)) {
                return Option.is(item);
            }
        }
        return Option.none();
    }
}
