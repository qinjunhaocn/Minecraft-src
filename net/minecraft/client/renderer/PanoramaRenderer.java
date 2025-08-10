/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class PanoramaRenderer {
    public static final ResourceLocation PANORAMA_OVERLAY = ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama_overlay.png");
    private final Minecraft minecraft;
    private final CubeMap cubeMap;
    private float spin;

    public PanoramaRenderer(CubeMap $$0) {
        this.cubeMap = $$0;
        this.minecraft = Minecraft.getInstance();
    }

    public void render(GuiGraphics $$0, int $$1, int $$2, boolean $$3) {
        if ($$3) {
            float $$4 = this.minecraft.getDeltaTracker().getRealtimeDeltaTicks();
            float $$5 = (float)((double)$$4 * this.minecraft.options.panoramaSpeed().get());
            this.spin = PanoramaRenderer.wrap(this.spin + $$5 * 0.1f, 360.0f);
        }
        this.cubeMap.render(this.minecraft, 10.0f, -this.spin);
        $$0.blit(RenderPipelines.GUI_TEXTURED, PANORAMA_OVERLAY, 0, 0, 0.0f, 0.0f, $$1, $$2, 16, 128, 16, 128);
    }

    private static float wrap(float $$0, float $$1) {
        return $$0 > $$1 ? $$0 - $$1 : $$0;
    }

    public void registerTextures(TextureManager $$0) {
        this.cubeMap.registerTextures($$0);
    }
}

