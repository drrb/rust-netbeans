/**
 * Copyright (C) 2013 drrb
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
package com.github.drrb.rust.netbeans.test;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Map;
import javax.swing.ImageIcon;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.StructureItem;

/**
 *
 */
public class Matchers extends org.hamcrest.Matchers {

    public static Matcher<Iterable<? extends Object>> empty() {
        return new TypeSafeMatcher<Iterable<? extends Object>>() {

            @Override
            public boolean matchesSafely(Iterable<? extends Object> item) {
                int elements = 0;
                for (Object element : item) {
                    elements++;
                }
                return elements == 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Empty collection");
            }
        };
    }

    public static <T> Matcher<Iterable<T>> contains(Matcher<? extends T> elementMatcher) {
        return hasItem(elementMatcher);
    }

    public static <T> Matcher<Iterable<T>> contains(T element) {
        return hasItem(element);
    }

    public static <K> MapMatcher<K> containsKey(K key) {
        return new MapMatcher<>(key);
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

    public static Matcher<StructureItem> structureItem(final String name, final OffsetRange offsetRange, final ElementKind elementKind, final Modifier... modifiers) {
        return new TypeSafeMatcher<StructureItem>() {
            @Override
            public boolean matchesSafely(StructureItem item) {
                return name.equals(item.getName())
                        && item.getPosition() == offsetRange.getStart()
                        && item.getEndPosition() == offsetRange.getEnd()
                        && item.getKind() == elementKind
                        && item.getModifiers().containsAll(Arrays.asList(modifiers));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("StructureItem with name ").appendValue(name)
                        .appendText(" and OffsetRange: ").appendValue(offsetRange)
                        .appendText(" and ElementKind ").appendValue(elementKind)
                        .appendText(" and Modifiers ").appendValueList("<", ", ", ">", modifiers);
            }
        };
    }

    public static CompletionProposalMatcher completionProposal(final String name, final ElementKind kind, final Modifier... modifiers) {
        return new CompletionProposalMatcher(name, kind, modifiers);
    }

    public static Matcher<ImageIcon> hasDimensions(final int width, final int height) {
        return new TypeSafeMatcher<ImageIcon>() {
            @Override
            public boolean matchesSafely(ImageIcon item) {
                return item.getIconWidth() == width
                        && item.getIconHeight() == height;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Icon with dimensions ").appendValueList("[", ",", "]", width, height);
            }
        };
    }

    public static class CompletionProposalMatcher extends TypeSafeMatcher<CompletionProposal> {

        private final String name;
        private final ElementKind kind;
        private final Modifier[] modifiers;
        private Matcher<? super ImageIcon> iconMatcher = is(nullValue());

        public CompletionProposalMatcher(String name, ElementKind kind, Modifier[] modifiers) {
            this.name = name;
            this.kind = kind;
            this.modifiers = modifiers;
        }

        public CompletionProposalMatcher withImageThat(Matcher<? super ImageIcon> iconMatcher) {
            this.iconMatcher = iconMatcher;
            return this;
        }

        @Override
        public boolean matchesSafely(CompletionProposal item) {
            return name.equals(item.getName())
                    && item.getKind() == kind
                    && Sets.newHashSet(modifiers).equals(item.getModifiers())
                    && iconMatcher.matches(item.getIcon());
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("CompletionProposal with name ").appendValue(name)
                    .appendText(" and kind ").appendValue(kind)
                    .appendText(" and modifiers ").appendValueList("<", ",", ">", modifiers)
                    .appendText(" and icon that ").appendDescriptionOf(iconMatcher);
        }
    }
}
