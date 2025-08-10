/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.GuiSignRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;

public class GuiSignRenderer
extends PictureInPictureRenderer<GuiSignRenderState> {
    public GuiSignRenderer(MultiBufferSource.BufferSource $$0) {
        super($$0);
    }

    @Override
    public Class<GuiSignRenderState> getRenderStateClass() {
        return GuiSignRenderState.class;
    }

    @Override
    protected void renderToTexture(GuiSignRenderState $$0, PoseStack $$1) {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        $$1.translate(0.0f, -0.75f, 0.0f);
        Material $$2 = Sheets.getSignMaterial($$0.woodType());
        Model $$3 = $$0.signModel();
        VertexConsumer $$4 = $$2.buffer(this.bufferSource, $$3::renderType);
        $$3.renderToBuffer($$1, $$4, 0xF000F0, OverlayTexture.NO_OVERLAY);
    }

    @Override
    protected String getTextureLabel() {
        return "sign";
    }
}

