/*
 * Copyright (C) 2022 alexander.kronenwett
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

package org.netbeans.modules.csl.api.test;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * A wrapper around a default {@link Toolkit} whose only purpose is to surpass
 * the {@link HeadlessException} that is thrown by
 * {@link Toolkit#getSystemSelection()}. The default Toolkit is headless and we
 * are in headless mode in the tests so that the NB UI is not captured all the
 * time.
 */
public class HeadlessDelegatingToolkit extends Toolkit {

    private final Toolkit wrapped;

    public HeadlessDelegatingToolkit(Toolkit wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Dimension getScreenSize() throws HeadlessException {
        return wrapped.getScreenSize();
    }

    @Override
    public int getScreenResolution() throws HeadlessException {
        return wrapped.getScreenResolution();
    }

    @Override
    public ColorModel getColorModel() throws HeadlessException {
        return wrapped.getColorModel();
    }

    @Override
    public String[] getFontList() {
        return wrapped.getFontList();
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
        return wrapped.getFontMetrics(font);
    }

    @Override
    public void sync() {
        wrapped.sync();
    }

    @Override
    public Image getImage(String filename) {
        return wrapped.getImage(filename);
    }

    @Override
    public Image getImage(URL url) {
        return wrapped.getImage(url);
    }

    @Override
    public Image createImage(String filename) {
        return wrapped.createImage(filename);
    }

    @Override
    public Image createImage(URL url) {
        return wrapped.createImage(url);
    }

    @Override
    public Image createImage(byte[] imagedata) {
        return wrapped.createImage(imagedata);
    }

    @Override
    public boolean prepareImage(Image image, int width, int height, ImageObserver observer) {
        return wrapped.prepareImage(image, width, height, observer);
    }

    @Override
    public int checkImage(Image image, int width, int height, ImageObserver observer) {
        return wrapped.checkImage(image, width, height, observer);
    }

    @Override
    public Image createImage(byte[] imagedata, int imageoffset, int imagelength) {
        return wrapped.createImage(imagedata, imageoffset, imagelength);
    }

    @Override
    public Image createImage(ImageProducer producer) {
        return wrapped.createImage(producer);
    }

    @Override
    public PrintJob getPrintJob(Frame frame, String jobtitle, Properties props) {
        return wrapped.getPrintJob(frame, jobtitle, props);
    }

    @Override
    public void beep() {
        wrapped.beep();
    }

    @Override
    public Clipboard getSystemClipboard() throws HeadlessException {
        return wrapped.getSystemClipboard();
    }

    @Override
    public boolean isModalityTypeSupported(Dialog.ModalityType modalityType) {
        return wrapped.isModalityTypeSupported(modalityType);
    }

    @Override
    public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType modalExclusionType) {
        return wrapped.isModalExclusionTypeSupported(modalExclusionType);
    }

    @Override
    public Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight highlight)
            throws HeadlessException {
        return wrapped.mapInputMethodHighlight(highlight);
    }

    @Override
    public Clipboard getSystemSelection() throws HeadlessException {
        // The original method throws a HeadlessException, which we don't want in tests
        return null;
    }

    @Override
    protected EventQueue getSystemEventQueueImpl() {
        return wrapped.getSystemEventQueue();
    }
}
