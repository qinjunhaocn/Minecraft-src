/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package com.mojang.realmsclient.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsConfirmScreen
extends RealmsScreen {
    protected BooleanConsumer callback;
    private final Component title1;
    private final Component title2;

    public RealmsConfirmScreen(BooleanConsumer $$0, Component $$1, Component $$2) {
        super(GameNarrator.NO_TITLE);
        this.callback = $$0;
        this.title1 = $$1;
        this.title2 = $$2;
    }

    @Override
    public void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_YES, $$0 -> this.callback.accept(true)).bounds(this.width / 2 - 105, RealmsConfirmScreen.row(9), 100, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_NO, $$0 -> this.callback.accept(false)).bounds(this.width / 2 + 5, RealmsConfirmScreen.row(9), 100, 20).build());
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title1, this.width / 2, RealmsConfirmScreen.row(3), -1);
        $$0.drawCenteredString(this.font, this.title2, this.width / 2, RealmsConfirmScreen.row(5), -1);
    }
}

