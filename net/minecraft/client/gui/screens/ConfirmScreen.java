/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfirmScreen
extends Screen {
    private final Component message;
    protected LinearLayout layout = LinearLayout.vertical().spacing(8);
    protected Component yesButtonComponent;
    protected Component noButtonComponent;
    @Nullable
    protected Button yesButton;
    @Nullable
    protected Button noButton;
    private int delayTicker;
    protected final BooleanConsumer callback;

    public ConfirmScreen(BooleanConsumer $$0, Component $$1, Component $$2) {
        this($$0, $$1, $$2, CommonComponents.GUI_YES, CommonComponents.GUI_NO);
    }

    public ConfirmScreen(BooleanConsumer $$0, Component $$1, Component $$2, Component $$3, Component $$4) {
        super($$1);
        this.callback = $$0;
        this.message = $$2;
        this.yesButtonComponent = $$3;
        this.noButtonComponent = $$4;
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.a(super.getNarrationMessage(), this.message);
    }

    @Override
    protected void init() {
        super.init();
        this.layout.defaultCellSetting().alignHorizontallyCenter();
        this.layout.addChild(new StringWidget(this.title, this.font));
        this.layout.addChild(new MultiLineTextWidget(this.message, this.font).setMaxWidth(this.width - 50).setMaxRows(15).setCentered(true));
        this.addAdditionalText();
        LinearLayout $$0 = this.layout.addChild(LinearLayout.horizontal().spacing(4));
        $$0.defaultCellSetting().paddingTop(16);
        this.addButtons($$0);
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        FrameLayout.centerInRectangle(this.layout, this.getRectangle());
    }

    protected void addAdditionalText() {
    }

    protected void addButtons(LinearLayout $$02) {
        this.yesButton = $$02.addChild(Button.builder(this.yesButtonComponent, $$0 -> this.callback.accept(true)).build());
        this.noButton = $$02.addChild(Button.builder(this.noButtonComponent, $$0 -> this.callback.accept(false)).build());
    }

    public void setDelay(int $$0) {
        this.delayTicker = $$0;
        this.yesButton.active = false;
        this.noButton.active = false;
    }

    @Override
    public void tick() {
        super.tick();
        if (--this.delayTicker == 0) {
            this.yesButton.active = true;
            this.noButton.active = true;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.callback.accept(false);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }
}

