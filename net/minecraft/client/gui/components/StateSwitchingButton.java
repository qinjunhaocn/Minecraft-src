/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;

public class StateSwitchingButton
extends AbstractWidget {
    @Nullable
    protected WidgetSprites sprites;
    protected boolean isStateTriggered;

    public StateSwitchingButton(int $$0, int $$1, int $$2, int $$3, boolean $$4) {
        super($$0, $$1, $$2, $$3, CommonComponents.EMPTY);
        this.isStateTriggered = $$4;
    }

    public void initTextureValues(WidgetSprites $$0) {
        this.sprites = $$0;
    }

    public void setStateTriggered(boolean $$0) {
        this.isStateTriggered = $$0;
    }

    public boolean isStateTriggered() {
        return this.isStateTriggered;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        this.defaultButtonNarrationText($$0);
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (this.sprites == null) {
            return;
        }
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprites.get(this.isStateTriggered, this.isHoveredOrFocused()), this.getX(), this.getY(), this.width, this.height);
    }
}

