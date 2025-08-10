/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

public class PlayerFaceRenderer {
    public static final int SKIN_HEAD_U = 8;
    public static final int SKIN_HEAD_V = 8;
    public static final int SKIN_HEAD_WIDTH = 8;
    public static final int SKIN_HEAD_HEIGHT = 8;
    public static final int SKIN_HAT_U = 40;
    public static final int SKIN_HAT_V = 8;
    public static final int SKIN_HAT_WIDTH = 8;
    public static final int SKIN_HAT_HEIGHT = 8;
    public static final int SKIN_TEX_WIDTH = 64;
    public static final int SKIN_TEX_HEIGHT = 64;

    public static void draw(GuiGraphics $$0, PlayerSkin $$1, int $$2, int $$3, int $$4) {
        PlayerFaceRenderer.draw($$0, $$1, $$2, $$3, $$4, -1);
    }

    public static void draw(GuiGraphics $$0, PlayerSkin $$1, int $$2, int $$3, int $$4, int $$5) {
        PlayerFaceRenderer.draw($$0, $$1.texture(), $$2, $$3, $$4, true, false, $$5);
    }

    public static void draw(GuiGraphics $$0, ResourceLocation $$1, int $$2, int $$3, int $$4, boolean $$5, boolean $$6, int $$7) {
        int $$8 = 8 + ($$6 ? 8 : 0);
        int $$9 = 8 * ($$6 ? -1 : 1);
        $$0.blit(RenderPipelines.GUI_TEXTURED, $$1, $$2, $$3, 8.0f, $$8, $$4, $$4, 8, $$9, 64, 64, $$7);
        if ($$5) {
            PlayerFaceRenderer.drawHat($$0, $$1, $$2, $$3, $$4, $$6, $$7);
        }
    }

    private static void drawHat(GuiGraphics $$0, ResourceLocation $$1, int $$2, int $$3, int $$4, boolean $$5, int $$6) {
        int $$7 = 8 + ($$5 ? 8 : 0);
        int $$8 = 8 * ($$5 ? -1 : 1);
        $$0.blit(RenderPipelines.GUI_TEXTURED, $$1, $$2, $$3, 40.0f, $$7, $$4, $$4, 8, $$8, 64, 64, $$6);
    }
}

