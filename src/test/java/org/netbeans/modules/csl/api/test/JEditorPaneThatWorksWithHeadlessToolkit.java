package org.netbeans.modules.csl.api.test;

import sun.awt.HeadlessToolkit;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;

class JEditorPaneThatWorksWithHeadlessToolkit extends JEditorPane {
    @Override
    public Toolkit getToolkit() {
        Toolkit originalToolkit = super.getToolkit();
        return new HeadlessToolkit(originalToolkit) {
            @Override
            public Clipboard getSystemSelection() throws HeadlessException {
                return null; // The default implementation raises a HeadlessException in tests.
            }
        };
    }
}
