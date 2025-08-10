/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.tabs;

import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

public class LoadingTab
implements Tab {
    private final Component title;
    private final Component loadingTitle;
    protected final LinearLayout layout = LinearLayout.vertical();

    public LoadingTab(Font $$02, Component $$1, Component $$2) {
        this.title = $$1;
        this.loadingTitle = $$2;
        LoadingDotsWidget $$3 = new LoadingDotsWidget($$02, $$2);
        this.layout.defaultCellSetting().alignVerticallyMiddle().alignHorizontallyCenter();
        this.layout.addChild($$3, $$0 -> $$0.paddingBottom(30));
    }

    @Override
    public Component getTabTitle() {
        return this.title;
    }

    @Override
    public Component getTabExtraNarration() {
        return this.loadingTitle;
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> $$0) {
        this.layout.visitWidgets($$0);
    }

    @Override
    public void doLayout(ScreenRectangle $$0) {
        this.layout.arrangeElements();
        FrameLayout.alignInRectangle(this.layout, $$0, 0.5f, 0.5f);
    }
}

