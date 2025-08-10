/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;

public class LeashKnotRenderer
extends EntityRenderer<LeashFenceKnotEntity, EntityRenderState> {
    private static final ResourceLocation KNOT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/lead_knot.png");
    private final LeashKnotModel model;

    public LeashKnotRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.model = new LeashKnotModel($$0.bakeLayer(ModelLayers.LEASH_KNOT));
    }

    @Override
    public void render(EntityRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        $$1.scale(-1.0f, -1.0f, 1.0f);
        this.model.setupAnim($$0);
        VertexConsumer $$4 = $$2.getBuffer(this.model.renderType(KNOT_LOCATION));
        this.model.renderToBuffer($$1, $$4, $$3, OverlayTexture.NO_OVERLAY);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}

