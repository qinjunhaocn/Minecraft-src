/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public abstract class AbstractButton
extends AbstractWidget {
    protected static final int TEXT_MARGIN = 2;
    private static final WidgetSprites SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/button"), ResourceLocation.withDefaultNamespace("widget/button_disabled"), ResourceLocation.withDefaultNamespace("widget/button_highlighted"));

    public AbstractButton(int $$0, int $$1, int $$2, int $$3, Component $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    public abstract void onPress();

    @Override
    protected void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        Minecraft $$4 = Minecraft.getInstance();
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
        int $$5 = ARGB.color(this.alpha, this.active ? -1 : -6250336);
        this.renderString($$0, $$4.font, $$5);
    }

    public void renderString(GuiGraphics $$0, Font $$1, int $$2) {
        this.renderScrollingString($$0, $$1, 2, $$2);
    }

    @Override
    public void onClick(double $$0, double $$1) {
        this.onPress();
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (CommonInputs.selected($$0)) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress();
            return true;
        }
        return false;
    }
}

