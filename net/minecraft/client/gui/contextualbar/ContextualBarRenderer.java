/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.contextualbar;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface ContextualBarRenderer {
    public static final int WIDTH = 182;
    public static final int HEIGHT = 5;
    public static final int MARGIN_BOTTOM = 24;
    public static final ContextualBarRenderer EMPTY = new ContextualBarRenderer(){

        @Override
        public void renderBackground(GuiGraphics $$0, DeltaTracker $$1) {
        }

        @Override
        public void render(GuiGraphics $$0, DeltaTracker $$1) {
        }
    };

    default public int left(Window $$0) {
        return ($$0.getGuiScaledWidth() - 182) / 2;
    }

    default public int top(Window $$0) {
        return $$0.getGuiScaledHeight() - 24 - 5;
    }

    public void renderBackground(GuiGraphics var1, DeltaTracker var2);

    public void render(GuiGraphics var1, DeltaTracker var2);

    public static void renderExperienceLevel(GuiGraphics $$0, Font $$1, int $$2) {
        MutableComponent $$3 = Component.a("gui.experience.level", $$2);
        int $$4 = ($$0.guiWidth() - $$1.width($$3)) / 2;
        int $$5 = $$0.guiHeight() - 24 - $$1.lineHeight - 2;
        $$0.drawString($$1, $$3, $$4 + 1, $$5, -16777216, false);
        $$0.drawString($$1, $$3, $$4 - 1, $$5, -16777216, false);
        $$0.drawString($$1, $$3, $$4, $$5 + 1, -16777216, false);
        $$0.drawString($$1, $$3, $$4, $$5 - 1, -16777216, false);
        $$0.drawString($$1, $$3, $$4, $$5, -8323296, false);
    }
}

