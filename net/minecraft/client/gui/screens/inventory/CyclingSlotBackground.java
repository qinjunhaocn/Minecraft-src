/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public class CyclingSlotBackground {
    private static final int ICON_CHANGE_TICK_RATE = 30;
    private static final int ICON_SIZE = 16;
    private static final int ICON_TRANSITION_TICK_DURATION = 4;
    private final int slotIndex;
    private List<ResourceLocation> icons = List.of();
    private int tick;
    private int iconIndex;

    public CyclingSlotBackground(int $$0) {
        this.slotIndex = $$0;
    }

    public void tick(List<ResourceLocation> $$0) {
        if (!this.icons.equals($$0)) {
            this.icons = $$0;
            this.iconIndex = 0;
        }
        if (!this.icons.isEmpty() && ++this.tick % 30 == 0) {
            this.iconIndex = (this.iconIndex + 1) % this.icons.size();
        }
    }

    public void render(AbstractContainerMenu $$0, GuiGraphics $$1, float $$2, int $$3, int $$4) {
        float $$7;
        Slot $$5 = $$0.getSlot(this.slotIndex);
        if (this.icons.isEmpty() || $$5.hasItem()) {
            return;
        }
        boolean $$6 = this.icons.size() > 1 && this.tick >= 30;
        float f = $$7 = $$6 ? this.getIconTransitionTransparency($$2) : 1.0f;
        if ($$7 < 1.0f) {
            int $$8 = Math.floorMod(this.iconIndex - 1, this.icons.size());
            this.renderIcon($$5, this.icons.get($$8), 1.0f - $$7, $$1, $$3, $$4);
        }
        this.renderIcon($$5, this.icons.get(this.iconIndex), $$7, $$1, $$3, $$4);
    }

    private void renderIcon(Slot $$0, ResourceLocation $$1, float $$2, GuiGraphics $$3, int $$4, int $$5) {
        $$3.blitSprite(RenderPipelines.GUI_TEXTURED, $$1, $$4 + $$0.x, $$5 + $$0.y, 16, 16, ARGB.white($$2));
    }

    private float getIconTransitionTransparency(float $$0) {
        float $$1 = (float)(this.tick % 30) + $$0;
        return Math.min($$1, 4.0f) / 4.0f;
    }
}

