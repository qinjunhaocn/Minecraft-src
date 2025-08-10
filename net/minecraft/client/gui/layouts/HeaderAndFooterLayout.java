/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HeaderAndFooterLayout
implements Layout {
    public static final int DEFAULT_HEADER_AND_FOOTER_HEIGHT = 33;
    private static final int CONTENT_MARGIN_TOP = 30;
    private final FrameLayout headerFrame = new FrameLayout();
    private final FrameLayout footerFrame = new FrameLayout();
    private final FrameLayout contentsFrame = new FrameLayout();
    private final Screen screen;
    private int headerHeight;
    private int footerHeight;

    public HeaderAndFooterLayout(Screen $$0) {
        this($$0, 33);
    }

    public HeaderAndFooterLayout(Screen $$0, int $$1) {
        this($$0, $$1, $$1);
    }

    public HeaderAndFooterLayout(Screen $$0, int $$1, int $$2) {
        this.screen = $$0;
        this.headerHeight = $$1;
        this.footerHeight = $$2;
        this.headerFrame.defaultChildLayoutSetting().align(0.5f, 0.5f);
        this.footerFrame.defaultChildLayoutSetting().align(0.5f, 0.5f);
    }

    @Override
    public void setX(int $$0) {
    }

    @Override
    public void setY(int $$0) {
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public int getWidth() {
        return this.screen.width;
    }

    @Override
    public int getHeight() {
        return this.screen.height;
    }

    public int getFooterHeight() {
        return this.footerHeight;
    }

    public void setFooterHeight(int $$0) {
        this.footerHeight = $$0;
    }

    public void setHeaderHeight(int $$0) {
        this.headerHeight = $$0;
    }

    public int getHeaderHeight() {
        return this.headerHeight;
    }

    public int getContentHeight() {
        return this.screen.height - this.getHeaderHeight() - this.getFooterHeight();
    }

    @Override
    public void visitChildren(Consumer<LayoutElement> $$0) {
        this.headerFrame.visitChildren($$0);
        this.contentsFrame.visitChildren($$0);
        this.footerFrame.visitChildren($$0);
    }

    @Override
    public void arrangeElements() {
        int $$0 = this.getHeaderHeight();
        int $$1 = this.getFooterHeight();
        this.headerFrame.setMinWidth(this.screen.width);
        this.headerFrame.setMinHeight($$0);
        this.headerFrame.setPosition(0, 0);
        this.headerFrame.arrangeElements();
        this.footerFrame.setMinWidth(this.screen.width);
        this.footerFrame.setMinHeight($$1);
        this.footerFrame.arrangeElements();
        this.footerFrame.setY(this.screen.height - $$1);
        this.contentsFrame.setMinWidth(this.screen.width);
        this.contentsFrame.arrangeElements();
        int $$2 = $$0 + 30;
        int $$3 = this.screen.height - $$1 - this.contentsFrame.getHeight();
        this.contentsFrame.setPosition(0, Math.min($$2, $$3));
    }

    public <T extends LayoutElement> T addToHeader(T $$0) {
        return this.headerFrame.addChild($$0);
    }

    public <T extends LayoutElement> T addToHeader(T $$0, Consumer<LayoutSettings> $$1) {
        return this.headerFrame.addChild($$0, $$1);
    }

    public void addTitleHeader(Component $$0, Font $$1) {
        this.headerFrame.addChild(new StringWidget($$0, $$1));
    }

    public <T extends LayoutElement> T addToFooter(T $$0) {
        return this.footerFrame.addChild($$0);
    }

    public <T extends LayoutElement> T addToFooter(T $$0, Consumer<LayoutSettings> $$1) {
        return this.footerFrame.addChild($$0, $$1);
    }

    public <T extends LayoutElement> T addToContents(T $$0) {
        return this.contentsFrame.addChild($$0);
    }

    public <T extends LayoutElement> T addToContents(T $$0, Consumer<LayoutSettings> $$1) {
        return this.contentsFrame.addChild($$0, $$1);
    }
}

