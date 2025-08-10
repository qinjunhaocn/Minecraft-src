/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory.tooltip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

public class TooltipRenderUtil {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("tooltip/background");
    private static final ResourceLocation FRAME_SPRITE = ResourceLocation.withDefaultNamespace("tooltip/frame");
    public static final int MOUSE_OFFSET = 12;
    private static final int PADDING = 3;
    public static final int PADDING_LEFT = 3;
    public static final int PADDING_RIGHT = 3;
    public static final int PADDING_TOP = 3;
    public static final int PADDING_BOTTOM = 3;
    private static final int MARGIN = 9;

    public static void renderTooltipBackground(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, @Nullable ResourceLocation $$5) {
        int $$6 = $$1 - 3 - 9;
        int $$7 = $$2 - 3 - 9;
        int $$8 = $$3 + 3 + 3 + 18;
        int $$9 = $$4 + 3 + 3 + 18;
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, TooltipRenderUtil.getBackgroundSprite($$5), $$6, $$7, $$8, $$9);
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, TooltipRenderUtil.getFrameSprite($$5), $$6, $$7, $$8, $$9);
    }

    private static ResourceLocation getBackgroundSprite(@Nullable ResourceLocation $$02) {
        if ($$02 == null) {
            return BACKGROUND_SPRITE;
        }
        return $$02.withPath($$0 -> "tooltip/" + $$0 + "_background");
    }

    private static ResourceLocation getFrameSprite(@Nullable ResourceLocation $$02) {
        if ($$02 == null) {
            return FRAME_SPRITE;
        }
        return $$02.withPath($$0 -> "tooltip/" + $$0 + "_frame");
    }
}

