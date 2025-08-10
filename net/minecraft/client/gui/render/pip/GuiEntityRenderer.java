/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class GuiEntityRenderer
extends PictureInPictureRenderer<GuiEntityRenderState> {
    private final EntityRenderDispatcher entityRenderDispatcher;

    public GuiEntityRenderer(MultiBufferSource.BufferSource $$0, EntityRenderDispatcher $$1) {
        super($$0);
        this.entityRenderDispatcher = $$1;
    }

    @Override
    public Class<GuiEntityRenderState> getRenderStateClass() {
        return GuiEntityRenderState.class;
    }

    @Override
    protected void renderToTexture(GuiEntityRenderState $$0, PoseStack $$1) {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        Vector3f $$2 = $$0.translation();
        $$1.translate($$2.x, $$2.y, $$2.z);
        $$1.mulPose((Quaternionfc)$$0.rotation());
        Quaternionf $$3 = $$0.overrideCameraAngle();
        if ($$3 != null) {
            this.entityRenderDispatcher.overrideCameraOrientation($$3.conjugate(new Quaternionf()).rotateY((float)Math.PI));
        }
        this.entityRenderDispatcher.setRenderShadow(false);
        this.entityRenderDispatcher.render($$0.renderState(), 0.0, 0.0, 0.0, $$1, this.bufferSource, 0xF000F0);
        this.entityRenderDispatcher.setRenderShadow(true);
    }

    @Override
    protected float getTranslateY(int $$0, int $$1) {
        return (float)$$0 / 2.0f;
    }

    @Override
    protected String getTextureLabel() {
        return "entity";
    }
}

