/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.GuiBannerResultRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;

public class GuiBannerResultRenderer
extends PictureInPictureRenderer<GuiBannerResultRenderState> {
    public GuiBannerResultRenderer(MultiBufferSource.BufferSource $$0) {
        super($$0);
    }

    @Override
    public Class<GuiBannerResultRenderState> getRenderStateClass() {
        return GuiBannerResultRenderState.class;
    }

    @Override
    protected void renderToTexture(GuiBannerResultRenderState $$0, PoseStack $$1) {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        $$1.translate(0.0f, 0.25f, 0.0f);
        BannerRenderer.renderPatterns($$1, this.bufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY, $$0.flag(), ModelBakery.BANNER_BASE, true, $$0.baseColor(), $$0.resultBannerPatterns());
    }

    @Override
    protected String getTextureLabel() {
        return "banner result";
    }
}

