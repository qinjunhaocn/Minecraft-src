/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ImageButton
extends Button {
    protected final WidgetSprites sprites;

    public ImageButton(int $$0, int $$1, int $$2, int $$3, WidgetSprites $$4, Button.OnPress $$5) {
        this($$0, $$1, $$2, $$3, $$4, $$5, CommonComponents.EMPTY);
    }

    public ImageButton(int $$0, int $$1, int $$2, int $$3, WidgetSprites $$4, Button.OnPress $$5, Component $$6) {
        super($$0, $$1, $$2, $$3, $$6, $$5, DEFAULT_NARRATION);
        this.sprites = $$4;
    }

    public ImageButton(int $$0, int $$1, WidgetSprites $$2, Button.OnPress $$3, Component $$4) {
        this(0, 0, $$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        ResourceLocation $$4 = this.sprites.get(this.isActive(), this.isHoveredOrFocused());
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$4, this.getX(), this.getY(), this.width, this.height);
    }
}

