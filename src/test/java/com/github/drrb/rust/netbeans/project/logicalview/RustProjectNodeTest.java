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
package com.github.drrb.rust.netbeans.project.logicalview;

import com.github.drrb.rust.netbeans.project.RustProject;
import java.awt.Image;
import java.beans.BeanInfo;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import javax.swing.Action;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import org.netbeans.junit.NbTestCase;
import static org.netbeans.spi.project.ui.support.CommonProjectActions.closeProjectAction;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.UtilitiesTest.NamedServicesProviderImpl;
import org.openide.util.lookup.Lookups;
import org.openide.util.test.MockLookup;

/**
 *
 */
public class RustProjectNodeTest extends NbTestCase {

    private RustProjectNode projectNode;
    private NamedServicesProviderImpl namedServicesProvider;

    public RustProjectNodeTest(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        clearWorkDir();
        namedServicesProvider = new NamedServicesProviderImpl();
        MockLookup.setInstances(namedServicesProvider);
        RustProject rustProject = new RustProject(FileUtil.toFileObject(getWorkDir()), null);
        projectNode = new RustProjectNode(new AbstractNode(Children.LEAF, Lookup.EMPTY), rustProject);
    }

    public void testDisplaysProjectNodeWithDisplayName() throws Exception {
        Files.write(getWorkDir().toPath().resolve("Cargo.toml"), "[package]\nname = \"my rust project\"\n".getBytes(UTF_8));

        assertThat(projectNode.getDisplayName(), is("my rust project"));
    }

    public void testDisplaysProjectIcon() {
        assertThat(projectNode.getIcon(BeanInfo.ICON_COLOR_16x16), isA(Image.class));
    }

    public void testHasActionsRegisteredInLayer() {
        Action closeProjectAction = closeProjectAction();
        namedServicesProvider.addNamedLookup("Projects/com-github-drrb-rust-netbeans-project/Actions", Lookups.singleton(closeProjectAction));

        Action[] actions = projectNode.getActions(true);

        assertThat(actions, is(arrayContaining(closeProjectAction)));
    }
}
