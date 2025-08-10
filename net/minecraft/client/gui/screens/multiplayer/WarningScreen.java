/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.multiplayer;

import javax.annotation.Nullable;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class WarningScreen
extends Screen {
    private static final int MESSAGE_PADDING = 100;
    private final Component message;
    @Nullable
    private final Component check;
    private final Component narration;
    @Nullable
    protected Checkbox stopShowing;
    @Nullable
    private FocusableTextWidget messageWidget;
    private final FrameLayout layout;

    protected WarningScreen(Component $$0, Component $$1, Component $$2) {
        this($$0, $$1, null, $$2);
    }

    protected WarningScreen(Component $$0, Component $$1, @Nullable Component $$2, Component $$3) {
        super($$0);
        this.message = $$1;
        this.check = $$2;
        this.narration = $$3;
        this.layout = new FrameLayout(0, 0, this.width, this.height);
    }

    protected abstract Layout addFooterButtons();

    @Override
    protected void init() {
        LinearLayout $$02 = this.layout.addChild(LinearLayout.vertical().spacing(8));
        $$02.defaultCellSetting().alignHorizontallyCenter();
        $$02.addChild(new StringWidget(this.getTitle(), this.font));
        this.messageWidget = $$02.addChild(new FocusableTextWidget(this.width - 100, this.message, this.font, 12), $$0 -> $$0.padding(12));
        this.messageWidget.setCentered(false);
        LinearLayout $$12 = $$02.addChild(LinearLayout.vertical().spacing(8));
        $$12.defaultCellSetting().alignHorizontallyCenter();
        if (this.check != null) {
            this.stopShowing = $$12.addChild(Checkbox.builder(this.check, this.font).build());
        }
        $$12.addChild(this.addFooterButtons());
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (this.messageWidget != null) {
            this.messageWidget.setMaxWidth(this.width - 100);
        }
        this.layout.arrangeElements();
        FrameLayout.centerInRectangle(this.layout, this.getRectangle());
    }

    @Override
    public Component getNarrationMessage() {
        return this.narration;
    }
}

