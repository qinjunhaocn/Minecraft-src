/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArrowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.joml.Quaternionfc;

public abstract class ArrowRenderer<T extends AbstractArrow, S extends ArrowRenderState>
extends EntityRenderer<T, S> {
    private final ArrowModel model;

    public ArrowRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.model = new ArrowModel($$0.bakeLayer(ModelLayers.ARROW));
    }

    @Override
    public void render(S $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(((ArrowRenderState)$$0).yRot - 90.0f));
        $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(((ArrowRenderState)$$0).xRot));
        VertexConsumer $$4 = $$2.getBuffer(RenderType.entityCutout(this.getTextureLocation($$0)));
        this.model.setupAnim((ArrowRenderState)$$0);
        this.model.renderToBuffer($$1, $$4, $$3, OverlayTexture.NO_OVERLAY);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    protected abstract ResourceLocation getTextureLocation(S var1);

    @Override
    public void extractRenderState(T $$0, S $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        ((ArrowRenderState)$$1).xRot = ((Entity)$$0).getXRot($$2);
        ((ArrowRenderState)$$1).yRot = ((Entity)$$0).getYRot($$2);
        ((ArrowRenderState)$$1).shake = (float)((AbstractArrow)$$0).shakeTime - $$2;
    }
}

