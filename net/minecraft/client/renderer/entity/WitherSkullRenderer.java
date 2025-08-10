/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.WitherSkullRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.WitherSkull;

public class WitherSkullRenderer
extends EntityRenderer<WitherSkull, WitherSkullRenderState> {
    private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_invulnerable.png");
    private static final ResourceLocation WITHER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither.png");
    private final SkullModel model;

    public WitherSkullRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.model = new SkullModel($$0.bakeLayer(ModelLayers.WITHER_SKULL));
    }

    public static LayerDefinition createSkullLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 35).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    protected int getBlockLightLevel(WitherSkull $$0, BlockPos $$1) {
        return 15;
    }

    @Override
    public void render(WitherSkullRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        $$1.scale(-1.0f, -1.0f, 1.0f);
        VertexConsumer $$4 = $$2.getBuffer(this.model.renderType(this.getTextureLocation($$0)));
        this.model.setupAnim(0.0f, $$0.yRot, $$0.xRot);
        this.model.renderToBuffer($$1, $$4, $$3, OverlayTexture.NO_OVERLAY);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    private ResourceLocation getTextureLocation(WitherSkullRenderState $$0) {
        return $$0.isDangerous ? WITHER_INVULNERABLE_LOCATION : WITHER_LOCATION;
    }

    @Override
    public WitherSkullRenderState createRenderState() {
        return new WitherSkullRenderState();
    }

    @Override
    public void extractRenderState(WitherSkull $$0, WitherSkullRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.isDangerous = $$0.isDangerous();
        $$1.yRot = $$0.getYRot($$2);
        $$1.xRot = $$0.getXRot($$2);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

