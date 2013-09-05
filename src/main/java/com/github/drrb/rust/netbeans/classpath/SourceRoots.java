/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package com.github.drrb.rust.netbeans.classpath;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.netbeans.api.project.ProjectManager;
import static com.github.drrb.rust.netbeans.classpath.SourceRoots.DEFAULT_SOURCE_LABEL;
import static com.github.drrb.rust.netbeans.classpath.SourceRoots.DEFAULT_TEST_LABEL;
import static com.github.drrb.rust.netbeans.classpath.SourceRoots.PROP_ROOTS;
import static com.github.drrb.rust.netbeans.classpath.SourceRoots.Type.SELENIUM;
import static com.github.drrb.rust.netbeans.classpath.SourceRoots.Type.SOURCES;
import static com.github.drrb.rust.netbeans.classpath.SourceRoots.Type.TESTS;
import static com.github.drrb.rust.netbeans.classpath.SourceRoots.createInitialDisplayName;
import com.github.drrb.rust.netbeans.project.RustProjectProperties;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

/**
 * Represents a helper for manipulation source roots. Based on SourceRoot class
 * (common.java.api) which was copied to all non java projecs.
 * <p>
 * For PHP project, it's simplified because there's no need to store source
 * roots in project.xml (they don't need to be propagated to any build.xml or
 * so). In fact, project.xml is not interesting for PHP project at all.
 *
 * @author Tomas Zezula, Tomas Mysik
 */
public final class SourceRoots {

    public enum Type {

        SOURCES,
        TESTS,
        SELENIUM
    }
    /**
     * Property name of a event that is fired when project properties change.
     */
    public static final String PROP_ROOTS = SourceRoots.class.getName() + ".roots"; //NOI18N
    /**
     * Default label for sources node used in
     * {@link org.netbeans.spi.project.ui.LogicalViewProvider}.
     */
    public static final String DEFAULT_SOURCE_LABEL = NbBundle.getMessage(SourceRoots.class, "NAME_src.dir");
    /**
     * Default label for tests node used in
     * {@link org.netbeans.spi.project.ui.LogicalViewProvider}.
     */
    public static final String DEFAULT_TEST_LABEL = NbBundle.getMessage(SourceRoots.class, "NAME_test.src.dir");
    private final UpdateHelper helper;
    private final PropertyEvaluator evaluator;
    private final ReferenceHelper refHelper;
    // @GuardedBy("Collections.synchronizedList")
    private final List<String> sourceRootProperties;
    // @GuardedBy("Collections.synchronizedList")
    private final List<String> sourceRootNames;
    // @GuardedBy("this")
    private List<FileObject> sourceRoots;
    // @GuardedBy("this")
    private List<URL> sourceRootURLs;
    private final PropertyChangeSupport support;
    private final SourceRoots.ProjectMetadataListener listener;
    private final File projectDir;
    private final SourceRoots.Type type;
    // #196060 - help to diagnose
    private AtomicLong firedChanges = new AtomicLong();

    public static SourceRoots create(UpdateHelper helper, PropertyEvaluator evaluator, ReferenceHelper refHelper, SourceRoots.Type type) {
        assert helper != null;
        assert evaluator != null;
        assert refHelper != null;
        assert type != null;
        return new SourceRoots(helper, evaluator, refHelper, type);
    }

    private SourceRoots(UpdateHelper helper, PropertyEvaluator evaluator, ReferenceHelper refHelper, SourceRoots.Type type) {

        this.helper = helper;
        this.evaluator = evaluator;
        this.refHelper = refHelper;
        this.type = type;

        switch (type) {
            case SOURCES:
                sourceRootProperties = Collections.synchronizedList(Arrays.asList(RustProjectProperties.SOURCE_DIR));
                sourceRootNames = Collections.synchronizedList(Arrays.asList(NbBundle.getMessage(SourceRoots.class, "LBL_Node_Sources")));
                break;
            case TESTS:
                sourceRootProperties = Collections.synchronizedList(Arrays.asList(RustProjectProperties.TEST_SOURCE_DIR));
                sourceRootNames = Collections.synchronizedList(Arrays.asList(NbBundle.getMessage(SourceRoots.class, "LBL_Node_Tests")));
                break;
            default:
                throw new IllegalStateException("Unknow sources roots type: " + type);
        }
        projectDir = FileUtil.toFile(this.helper.getAntProjectHelper().getProjectDirectory());
        support = new PropertyChangeSupport(this);
        listener = new SourceRoots.ProjectMetadataListener();
        this.evaluator.addPropertyChangeListener(WeakListeners.propertyChange(this.listener, this.evaluator));
    }

    /**
     * Returns the display names of source roots. The returned array has the
     * same length as an array returned by the {@link #getRootProperties()}. It
     * may contain empty {@link String}s but not
     * <code>null</code>.
     *
     * @return an array of source roots names.
     */
    public String[] getRootNames() {
        return sourceRootNames.toArray(new String[sourceRootNames.size()]);
    }

    /**
     * Returns names of Ant properties in the <i>project.properties</i> file
     * holding the source roots.
     *
     * @return an array of String.
     */
    public String[] getRootProperties() {
        return sourceRootProperties.toArray(new String[sourceRootProperties.size()]);
    }

    /**
     * Returns the source roots in the form of absolute paths.
     *
     * @return an array of {@link FileObject}s.
     */
    public FileObject[] getRoots() {
        return ProjectManager.mutex().readAccess(new Mutex.Action<FileObject[]>() {
            @Override
            public FileObject[] run() {
                synchronized (SourceRoots.this) {
                    // local caching
                    if (sourceRoots == null) {
                        String[] srcProps = getRootProperties();
                        List<FileObject> result = new ArrayList<FileObject>();
                        for (String p : srcProps) {
                            String prop = evaluator.getProperty(p);
                            if (prop != null) {
                                FileObject f = helper.getAntProjectHelper().resolveFileObject(prop);
                                if (f == null) {
                                    continue;
                                }
                                if (FileUtil.isArchiveFile(f)) {
                                    f = FileUtil.getArchiveRoot(f);
                                }
                                result.add(f);
                            }
                        }
                        sourceRoots = Collections.unmodifiableList(result);
                    }
                    return sourceRoots.toArray(new FileObject[sourceRoots.size()]);
                }
            }
        });
    }

    /**
     * Returns the source roots as {@link URL}s.
     *
     * @return an array of {@link URL}.
     */
    public URL[] getRootURLs() {
        return ProjectManager.mutex().readAccess(new Mutex.Action<URL[]>() {
            @Override
            public URL[] run() {
                synchronized (SourceRoots.this) {
                    // local caching
                    if (sourceRootURLs == null) {
                        List<URL> result = new ArrayList<URL>();
                        for (String srcProp : getRootProperties()) {
                            String prop = evaluator.getProperty(srcProp);
                            if (prop != null) {
                                File f = helper.getAntProjectHelper().resolveFile(prop);
                                try {
                                    URL url = Utilities.toURI(f).toURL();
                                    if (!f.exists()) {
                                        url = new URL(url.toExternalForm() + "/"); // NOI18N
                                    } else if (f.isFile()) {
                                        // file cannot be a source root (archives are not supported as source roots).
                                        continue;
                                    }
                                    assert url.toExternalForm().endsWith("/") : "#90639 violation for " + url + "; "
                                            + f + " exists? " + f.exists() + " dir? " + f.isDirectory()
                                            + " file? " + f.isFile();
                                    result.add(url);
                                } catch (MalformedURLException e) {
                                    Exceptions.printStackTrace(e);
                                }
                            }
                        }
                        sourceRootURLs = Collections.unmodifiableList(result);
                    }
                    return sourceRootURLs.toArray(new URL[sourceRootURLs.size()]);
                }
            }
        });
    }

    /**
     * Adds {@link PropertyChangeListener}, see class description for more
     * information about listening to the source roots changes.
     *
     * @param listener a listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Removes {@link PropertyChangeListener}, see class description for more
     * information about listening to the source roots changes.
     *
     * @param listener a listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /**
     * Translates root name into display name of source/test root.
     *
     * @param rootName the name of root got from
     * {@link SourceRoots#getRootNames()}.
     * @param propName the name of a property the root is stored in.
     * @return the label to be displayed.
     */
    public String getRootDisplayName(String rootName, String propName) {
        if (rootName == null || rootName.length() == 0) {
            // if the prop is src.dir use the default name
            switch (type) {
                case SOURCES:
                    rootName = DEFAULT_SOURCE_LABEL;
                    break;
                case TESTS:
                    rootName = DEFAULT_TEST_LABEL;
                    break;
                default:
                    // if the name is not given, it should be either a relative path in the project dir
                    // or absolute path when the root is not under the project dir
                    String propValue = evaluator.getProperty(propName);
                    File sourceRoot = propValue == null ? null : helper.getAntProjectHelper().resolveFile(propValue);
                    rootName = createInitialDisplayName(sourceRoot);
                    break;
            }
        }
        return rootName;
    }

    /**
     * Creates initial display name of source/test root.
     *
     * @param sourceRoot the source root.
     * @return the label to be displayed.
     */
    public String createInitialDisplayName(File sourceRoot) {
        return createInitialDisplayName(sourceRoot, projectDir, type);
    }

    public static String createInitialDisplayName(File sourceRoot, File projectDir, SourceRoots.Type type) {
        String rootName;
        if (sourceRoot != null) {
            String srPath = sourceRoot.getAbsolutePath();
            String pdPath = projectDir.getAbsolutePath() + File.separatorChar;
            if (srPath.startsWith(pdPath)) {
                rootName = srPath.substring(pdPath.length());
            } else {
                rootName = sourceRoot.getAbsolutePath();
            }
        } else {
            rootName = type.equals(SourceRoots.Type.SOURCES) ? DEFAULT_SOURCE_LABEL : DEFAULT_TEST_LABEL;
        }
        return rootName;
    }

    /**
     * Returns
     * <code>true</code> if the current {@link SourceRoots} instance represents
     * source roots belonging to the test compilation unit.
     *
     * @return boolean <code>true</code> if the instance belongs to the test
     * compilation unit, false otherwise.
     */
    public boolean isTest() {
        boolean isTest = false;
        switch (type) {
            case SOURCES:
                isTest = false;
                break;
            case TESTS:
            case SELENIUM:
                isTest = true;
                break;
            default:
                assert false : "Unknown source roots type: " + type;
        }
        return isTest;
    }

    private void resetCache(String propName) {
        boolean fire = false;
        synchronized (this) {
            // in case of change reset local cache
            if (propName == null
                    || sourceRootProperties.contains(propName)) {
                sourceRoots = null;
                sourceRootURLs = null;
                fire = true;
            }
        }
        if (fire) {
            firedChanges.incrementAndGet();
            support.firePropertyChange(PROP_ROOTS, null, null);
        }
    }

    public void fireChange() {
        resetCache(null);
    }

    public long getFiredChanges() {
        return firedChanges.get();
    }

    //~ Inner classes
    private final class ProjectMetadataListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            resetCache(evt.getPropertyName());
        }
    }
}
