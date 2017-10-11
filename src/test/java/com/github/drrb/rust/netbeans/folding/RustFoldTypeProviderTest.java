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
package com.github.drrb.rust.netbeans.folding;

import static com.github.drrb.rust.netbeans.test.Matchers.*;
import java.util.Collection;
import static org.hamcrest.core.Is.is;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.netbeans.api.editor.fold.FoldType;

/**
 *
 */
public class RustFoldTypeProviderTest {

    private RustFoldTypeProvider provider;

    @Before
    public void setUp() {
        provider = new RustFoldTypeProvider();
    }

    @Test
    public void shouldReturnCodeblockAndDocumentationCodeFoldTypes() {
        Collection<FoldType> values = provider.getValues(FoldType.class);
        assertThat(values, hasItem(FoldType.CODE_BLOCK));
        assertThat(values, hasItem(FoldType.DOCUMENTATION));
    }

    @Test
    public void shouldReturnNoValuesOfAnyOtherType() {
        Collection<?> values = provider.getValues(Object.class);
        assertThat(values, is(empty()));
    }

    @Test
    public void shouldNotBeInheritable() {
        assertThat(provider.inheritable(), is(false));
    }

}
