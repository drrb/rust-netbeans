/**
 * Copyright (C) 2013 drrb
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
package com.github.drrb.rust.netbeans.util;

/**
 *
 */
public class Option<T> {

    public static final Option<?> NONE = new Option<Void>(null);

    public static <T> Option<T> none() {
        return (Option<T>) NONE;
    }

    public static <T> Option<T> is(T value) {
        return new Option<T>(value);
    }

    private final T value;

    public Option(T value) {
        this.value = value;
    }

    public boolean is() {
        return ! isNot();
    }

    public T value() {
        return value;
    }

    public boolean isNot() {
        return this == NONE;
    }
}
