/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public class LoadingDotsWidget
extends AbstractWidget {
    private final Font font;

    public LoadingDotsWidget(Font $$0, Component $$1) {
        super(0, 0, $$0.width($$1), $$0.lineHeight * 3, $$1);
        this.font = $$0;
    }

    @Override
    protected void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        int $$4 = this.getX() + this.getWidth() / 2;
        int $$5 = this.getY() + this.getHeight() / 2;
        Component $$6 = this.getMessage();
        $$0.drawString(this.font, $$6, $$4 - this.font.width($$6) / 2, $$5 - this.font.lineHeight, -1);
        String $$7 = LoadingDotsText.get(Util.getMillis());
        $$0.drawString(this.font, $$7, $$4 - this.font.width($$7) / 2, $$5 + this.font.lineHeight, -8355712);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput $$0) {
    }

    @Override
    public void playDownSound(SoundManager $$0) {
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent $$0) {
        return null;
    }
}

