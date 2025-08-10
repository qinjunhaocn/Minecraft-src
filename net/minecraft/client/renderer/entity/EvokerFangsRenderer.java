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
import net.minecraft.client.model.EvokerFangsModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.EvokerFangsRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.EvokerFangs;
import org.joml.Quaternionfc;

public class EvokerFangsRenderer
extends EntityRenderer<EvokerFangs, EvokerFangsRenderState> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/illager/evoker_fangs.png");
    private final EvokerFangsModel model;

    public EvokerFangsRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.model = new EvokerFangsModel($$0.bakeLayer(ModelLayers.EVOKER_FANGS));
    }

    @Override
    public void render(EvokerFangsRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        float $$4 = $$0.biteProgress;
        if ($$4 == 0.0f) {
            return;
        }
        $$1.pushPose();
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(90.0f - $$0.yRot));
        $$1.scale(-1.0f, -1.0f, 1.0f);
        $$1.translate(0.0f, -1.501f, 0.0f);
        this.model.setupAnim($$0);
        VertexConsumer $$5 = $$2.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer($$1, $$5, $$3, OverlayTexture.NO_OVERLAY);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public EvokerFangsRenderState createRenderState() {
        return new EvokerFangsRenderState();
    }

    @Override
    public void extractRenderState(EvokerFangs $$0, EvokerFangsRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.yRot = $$0.getYRot();
        $$1.biteProgress = $$0.getAnimationProgress($$2);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

