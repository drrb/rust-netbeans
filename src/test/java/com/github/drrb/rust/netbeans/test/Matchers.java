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
package com.github.drrb.rust.netbeans.test;

import com.google.common.base.Objects;
import java.util.Map;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 *
 */
public class Matchers extends org.hamcrest.Matchers {

    public static <K> MapMatcher<K> containsKey(K key) {
        return new MapMatcher(key);
    }

    public static class MapMatcher<K> extends TypeSafeMatcher<Map<K, ?>> {

        private final K expectedKey;

        public MapMatcher(K expectedKey) {
            this.expectedKey = expectedKey;
        }

        @Override
        public boolean matchesSafely(Map<K, ?> item) {
            return item.containsKey(expectedKey);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Map containing key ").appendValue(expectedKey);
        }

        public <V> Matcher<Map<K, V>> mappedToValue(final V expectedValue) {
            return new TypeSafeMatcher<Map<K, V>>() {

                @Override
                public boolean matchesSafely(Map<K, V> item) {
                    if (item.containsKey(expectedKey)) {
                        V actualValue = item.get(expectedKey);
                        return Objects.equal(actualValue, expectedValue);
                    } else {
                        return false;
                    }
                }

                @Override
                public void describeTo(Description description) {
                    description.appendText("Map with key ").appendValue(expectedKey)
                            .appendText(" mapped to ").appendValue(expectedValue);
                }
            };
        }
    }
}
