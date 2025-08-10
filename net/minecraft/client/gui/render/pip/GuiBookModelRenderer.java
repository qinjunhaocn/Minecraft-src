/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.GuiBookModelRenderState;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionfc;

public class GuiBookModelRenderer
extends PictureInPictureRenderer<GuiBookModelRenderState> {
    public GuiBookModelRenderer(MultiBufferSource.BufferSource $$0) {
        super($$0);
    }

    @Override
    public Class<GuiBookModelRenderState> getRenderStateClass() {
        return GuiBookModelRenderState.class;
    }

    @Override
    protected void renderToTexture(GuiBookModelRenderState $$0, PoseStack $$1) {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f));
        $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(25.0f));
        float $$2 = $$0.open();
        $$1.translate((1.0f - $$2) * 0.2f, (1.0f - $$2) * 0.1f, (1.0f - $$2) * 0.25f);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-(1.0f - $$2) * 90.0f - 90.0f));
        $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(180.0f));
        float $$3 = $$0.flip();
        float $$4 = Mth.clamp(Mth.frac($$3 + 0.25f) * 1.6f - 0.3f, 0.0f, 1.0f);
        float $$5 = Mth.clamp(Mth.frac($$3 + 0.75f) * 1.6f - 0.3f, 0.0f, 1.0f);
        BookModel $$6 = $$0.bookModel();
        $$6.setupAnim(0.0f, $$4, $$5, $$2);
        ResourceLocation $$7 = $$0.texture();
        VertexConsumer $$8 = this.bufferSource.getBuffer($$6.renderType($$7));
        $$6.renderToBuffer($$1, $$8, 0xF000F0, OverlayTexture.NO_OVERLAY);
    }

    @Override
    protected float getTranslateY(int $$0, int $$1) {
        return 17 * $$1;
    }

    @Override
    protected String getTextureLabel() {
        return "book model";
    }
}

