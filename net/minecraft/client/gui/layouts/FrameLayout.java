/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.layouts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.Mth;

public class FrameLayout
extends AbstractLayout {
    private final List<ChildContainer> children = new ArrayList<ChildContainer>();
    private int minWidth;
    private int minHeight;
    private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults().align(0.5f, 0.5f);

    public FrameLayout() {
        this(0, 0, 0, 0);
    }

    public FrameLayout(int $$0, int $$1) {
        this(0, 0, $$0, $$1);
    }

    public FrameLayout(int $$0, int $$1, int $$2, int $$3) {
        super($$0, $$1, $$2, $$3);
        this.setMinDimensions($$2, $$3);
    }

    public FrameLayout setMinDimensions(int $$0, int $$1) {
        return this.setMinWidth($$0).setMinHeight($$1);
    }

    public FrameLayout setMinHeight(int $$0) {
        this.minHeight = $$0;
        return this;
    }

    public FrameLayout setMinWidth(int $$0) {
        this.minWidth = $$0;
        return this;
    }

    public LayoutSettings newChildLayoutSettings() {
        return this.defaultChildLayoutSettings.copy();
    }

    public LayoutSettings defaultChildLayoutSetting() {
        return this.defaultChildLayoutSettings;
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();
        int $$0 = this.minWidth;
        int $$1 = this.minHeight;
        for (ChildContainer $$2 : this.children) {
            $$0 = Math.max($$0, $$2.getWidth());
            $$1 = Math.max($$1, $$2.getHeight());
        }
        for (ChildContainer $$3 : this.children) {
            $$3.setX(this.getX(), $$0);
            $$3.setY(this.getY(), $$1);
        }
        this.width = $$0;
        this.height = $$1;
    }

    public <T extends LayoutElement> T addChild(T $$0) {
        return this.addChild($$0, this.newChildLayoutSettings());
    }

    public <T extends LayoutElement> T addChild(T $$0, LayoutSettings $$1) {
        this.children.add(new ChildContainer($$0, $$1));
        return $$0;
    }

    public <T extends LayoutElement> T addChild(T $$0, Consumer<LayoutSettings> $$1) {
        return this.addChild($$0, Util.make(this.newChildLayoutSettings(), $$1));
    }

    @Override
    public void visitChildren(Consumer<LayoutElement> $$0) {
        this.children.forEach($$1 -> $$0.accept($$1.child));
    }

    public static void centerInRectangle(LayoutElement $$0, int $$1, int $$2, int $$3, int $$4) {
        FrameLayout.alignInRectangle($$0, $$1, $$2, $$3, $$4, 0.5f, 0.5f);
    }

    public static void centerInRectangle(LayoutElement $$0, ScreenRectangle $$1) {
        FrameLayout.centerInRectangle($$0, $$1.position().x(), $$1.position().y(), $$1.width(), $$1.height());
    }

    public static void alignInRectangle(LayoutElement $$0, ScreenRectangle $$1, float $$2, float $$3) {
        FrameLayout.alignInRectangle($$0, $$1.left(), $$1.top(), $$1.width(), $$1.height(), $$2, $$3);
    }

    public static void alignInRectangle(LayoutElement $$0, int $$1, int $$2, int $$3, int $$4, float $$5, float $$6) {
        FrameLayout.alignInDimension($$1, $$3, $$0.getWidth(), $$0::setX, $$5);
        FrameLayout.alignInDimension($$2, $$4, $$0.getHeight(), $$0::setY, $$6);
    }

    public static void alignInDimension(int $$0, int $$1, int $$2, Consumer<Integer> $$3, float $$4) {
        int $$5 = (int)Mth.lerp($$4, 0.0f, $$1 - $$2);
        $$3.accept($$0 + $$5);
    }

    static class ChildContainer
    extends AbstractLayout.AbstractChildWrapper {
        protected ChildContainer(LayoutElement $$0, LayoutSettings $$1) {
            super($$0, $$1);
        }
    }
}

