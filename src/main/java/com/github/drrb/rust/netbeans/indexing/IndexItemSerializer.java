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
package com.github.drrb.rust.netbeans.indexing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.parsing.spi.indexing.support.IndexDocument;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;

/**
 *
 */
public class IndexItemSerializer {

    private static final Logger LOGGER = Logger.getLogger(IndexItemSerializer.class.getName());

    public <T> T deserialize(IndexResult indexResult, Class<T> type) {
        Map<Field, IndexedString> annotatedStringFields = getAnnotatedFields(type, IndexedString.class);
        Map<Field, IndexedFile> annotatedFileFields = getAnnotatedFields(type, IndexedFile.class);
        T object = instantiate(type);
        for (Map.Entry<Field, IndexedString> entry : annotatedStringFields.entrySet()) {
            Field field = entry.getKey();
            IndexedString mapping = entry.getValue();
            String value = indexResult.getValue(mapping.value());
            setValueAsString(field, object, value);
        }

        for (Map.Entry<Field, IndexedFile> entry : annotatedFileFields.entrySet()) {
            Field field = entry.getKey();
            setValue(field, object, indexResult.getFile());
        }
        return object;
    }

    public void serialize(IndexDocument document, Object object) {
        Class<? extends Object> type = object.getClass();
        Map<Field, IndexedString> annotatedFields = getAnnotatedFields(type, IndexedString.class);
        for (Map.Entry<Field, IndexedString> entry : annotatedFields.entrySet()) {
            Field field = entry.getKey();
            IndexedString mapping = entry.getValue();
            String fieldValue = getFieldValueAsString(field, object);
            LOGGER.log(Level.WARNING, "Adding ''{0}'' = ''{1}'' to index", new Object[]{mapping.value(), fieldValue});
            document.addPair(mapping.value(), fieldValue, true, true);
        }
    }

    private String getFieldValueAsString(Field field, Object object) {
        try {
            Object value = field.get(object);
            return value.toString();
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException(String.format("Couldn't serialize class '%s' to index. (Couldn't get value of field '%s')", object.getClass().getName(), field.getName()), ex);
        }
    }

    private void setValueAsString(Field field, Object object, String valueString) {
        try {
            Class<?> type = field.getType();
            Object value = coerceStringToType(valueString, type);
            field.set(object, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException(String.format("Couldn't deserialize class '%s' from index. (Couldn't set value of field '%s' to '%s')", object.getClass().getName(), field.getName(), valueString), ex);
        }
    }

    private void setValue(Field field, Object object, Object value) {
        try {
            field.set(object, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException(String.format("Couldn't deserialize class '%s' from index. (Couldn't set value of field '%s' to '%s')", object.getClass().getName(), field.getName(), value), ex);
        }
    }

    private Object coerceStringToType(String string, Class<?> type) {
        if (type == String.class) {
            return string;
        } else if (type == int.class || type == Integer.class) {
            return Integer.valueOf(string);
        } else {
            throw new IllegalArgumentException(String.format("Couldn't coerce '%s' to a '%s'", string, type.getName()));
        }
    }

    private <T> T instantiate(Class<T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(String.format("Couldn't deserialize class '%s'. (Couldn't instantiated class)", type.getName()), ex);
        }
    }

    private <T extends Annotation> Map<Field, T> getAnnotatedFields(Class<?> type, Class<T> annotationType) {
        Field[] fields = type.getDeclaredFields();
        Map<Field, T> annotatedFields = new HashMap<>(fields.length);
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotationType)) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                annotatedFields.put(field, field.getAnnotation(annotationType));
            }
        }
        return annotatedFields;
    }

    public String[] getKeys(Class<?> indexedType) {
        Map<Field, IndexedString> annotatedFields = getAnnotatedFields(indexedType, IndexedString.class);
        String[] keys = new String[annotatedFields.size()];
        Collection<IndexedString> keyMappings = annotatedFields.values();
        int i = 0;
        for (Iterator<IndexedString> it = keyMappings.iterator(); it.hasNext();) {
            IndexedString keyMapping = it.next();
            keys[i] = keyMapping.value();
            i++;
        }
        return keys;
    }
}
