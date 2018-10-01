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

import org.netbeans.modules.csl.api.ElementKind;

/**
 *
 * @author Tim Boudreau
 */
public enum RustElementKind {

    STRUCT,
    TRAIT,
    TYPE,
    FUNCTION,
    ENUM,
    FIELD,
    TYPE_REFERENCE,
    LIFETIME,
    ENUM_CONSTANT,
    ATTR,
    IMPL
    ;

    public boolean isStructural() {
        switch(this) {
            case TRAIT :
            case TYPE :
            case ENUM :
            case FIELD :
            case FUNCTION :
            case ENUM_CONSTANT :
            case IMPL :
                return true;
            default :
                return false;
        }
    }

    /**
     * Provides a rough mapping to NetBeans' ElementKind enum
     * (which has no items to distingush type vs structure,
     * for example.
     *
     * @return
     */
    public ElementKind toElementKind() {
        switch (this) {
            // An imperfect mapping to say the least
            case TRAIT :
                return ElementKind.ATTRIBUTE;
            case STRUCT:
                return ElementKind.INTERFACE;
            case TYPE:
            case ENUM:
                return ElementKind.CLASS;
            case FIELD:
            case ENUM_CONSTANT:
                return ElementKind.FIELD;
            case FUNCTION:
                return ElementKind.METHOD;
            case LIFETIME:
                return ElementKind.RULE;
            case TYPE_REFERENCE :
                return ElementKind.GLOBAL;
            case ATTR :
                return ElementKind.ATTRIBUTE;
            case IMPL :
                return ElementKind.CLASS;
            default:
                throw new AssertionError(this);
        }
    }
}
