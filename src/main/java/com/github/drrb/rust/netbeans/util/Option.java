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
package com.github.drrb.rust.netbeans.util;

import java.util.Collections;
import java.util.Iterator;

/**
 *
 */
public class Option<T> implements Iterable<T> {

    public static final Option<?> NONE = new Option<>(null);

    @SuppressWarnings("unchecked")
    public static <T> Option<T> none() {
        return (Option<T>) NONE;
    }

    public static <T> Option<T> is(T value) {
        return new Option<>(value);
    }

    public static <T> Option<T> isIfNotNull(T value) {
        if (value == null) {
            return none();
        } else {
            return is(value);
        }
    }
    private final T value;

    public Option(T value) {
        this.value = value;
    }

    public boolean is() {
        return !isNot();
    }

    public T value() {
        return value;
    }

    public boolean isNot() {
        return this == NONE;
    }

    @Override
    public Iterator<T> iterator() {
        if (is()) {
            return Collections.singletonList(value).iterator();
        } else {
            return Collections.<T>emptyList().iterator();
        }
    }
}
