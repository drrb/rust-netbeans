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
package com.github.drrb.rust.netbeans.util;

import com.google.common.base.Function;

public class Template implements Function<Object, String> {

    public static Template template(String format) {
        return new Template(format);
    }
    private String template;
    private String inputKey = "";

    private Template(String format) {
        this.template = format;
    }

    public Template interpolate(String key, Object value) {
        template = renderWith(key, value);
        return this;
    }

    public Template interpolateFromInput(String key) {
        inputKey = key;
        return this;
    }

    @Override
    public String apply(Object input) {
        return renderWith(inputKey, input);
    }

    public String renderWith(String key, Object value) {
        return template.replace("{" + key + "}", value.toString());
    }
}
