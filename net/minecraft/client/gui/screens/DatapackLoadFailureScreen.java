/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class DatapackLoadFailureScreen
extends Screen {
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    private final Runnable cancelCallback;
    private final Runnable safeModeCallback;

    public DatapackLoadFailureScreen(Runnable $$0, Runnable $$1) {
        super(Component.translatable("datapackFailure.title"));
        this.cancelCallback = $$0;
        this.safeModeCallback = $$1;
    }

    @Override
    protected void init() {
        super.init();
        this.message = MultiLineLabel.create(this.font, this.getTitle(), this.width - 50);
        this.addRenderableWidget(Button.builder(Component.translatable("datapackFailure.safeMode"), $$0 -> this.safeModeCallback.run()).bounds(this.width / 2 - 155, this.height / 6 + 96, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.cancelCallback.run()).bounds(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20).build());
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.message.renderCentered($$0, this.width / 2, 70);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

