/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4fStack
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.GuiSkinRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4fStack;
import org.joml.Quaternionfc;

public class GuiSkinRenderer
extends PictureInPictureRenderer<GuiSkinRenderState> {
    public GuiSkinRenderer(MultiBufferSource.BufferSource $$0) {
        super($$0);
    }

    @Override
    public Class<GuiSkinRenderState> getRenderStateClass() {
        return GuiSkinRenderState.class;
    }

    @Override
    protected void renderToTexture(GuiSkinRenderState $$0, PoseStack $$1) {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.PLAYER_SKIN);
        int $$2 = Minecraft.getInstance().getWindow().getGuiScale();
        Matrix4fStack $$3 = RenderSystem.getModelViewStack();
        $$3.pushMatrix();
        float $$4 = $$0.scale() * (float)$$2;
        $$3.rotateAround((Quaternionfc)Axis.XP.rotationDegrees($$0.rotationX()), 0.0f, $$4 * -$$0.pivotY(), 0.0f);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-$$0.rotationY()));
        $$1.translate(0.0f, -1.6010001f, 0.0f);
        RenderType $$5 = $$0.playerModel().renderType($$0.texture());
        $$0.playerModel().renderToBuffer($$1, this.bufferSource.getBuffer($$5), 0xF000F0, OverlayTexture.NO_OVERLAY);
        this.bufferSource.endBatch();
        $$3.popMatrix();
    }

    @Override
    protected String getTextureLabel() {
        return "player skin";
    }
}

