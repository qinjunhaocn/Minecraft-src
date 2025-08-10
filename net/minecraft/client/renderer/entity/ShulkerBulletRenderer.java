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
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ShulkerBulletRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import org.joml.Quaternionfc;

public class ShulkerBulletRenderer
extends EntityRenderer<ShulkerBullet, ShulkerBulletRenderState> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/shulker/spark.png");
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE_LOCATION);
    private final ShulkerBulletModel model;

    public ShulkerBulletRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.model = new ShulkerBulletModel($$0.bakeLayer(ModelLayers.SHULKER_BULLET));
    }

    @Override
    protected int getBlockLightLevel(ShulkerBullet $$0, BlockPos $$1) {
        return 15;
    }

    @Override
    public void render(ShulkerBulletRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        float $$4 = $$0.ageInTicks;
        $$1.translate(0.0f, 0.15f, 0.0f);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(Mth.sin($$4 * 0.1f) * 180.0f));
        $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(Mth.cos($$4 * 0.1f) * 180.0f));
        $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(Mth.sin($$4 * 0.15f) * 360.0f));
        $$1.scale(-0.5f, -0.5f, 0.5f);
        this.model.setupAnim($$0);
        VertexConsumer $$5 = $$2.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer($$1, $$5, $$3, OverlayTexture.NO_OVERLAY);
        $$1.scale(1.5f, 1.5f, 1.5f);
        VertexConsumer $$6 = $$2.getBuffer(RENDER_TYPE);
        this.model.renderToBuffer($$1, $$6, $$3, OverlayTexture.NO_OVERLAY, 0x26FFFFFF);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public ShulkerBulletRenderState createRenderState() {
        return new ShulkerBulletRenderState();
    }

    @Override
    public void extractRenderState(ShulkerBullet $$0, ShulkerBulletRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.yRot = $$0.getYRot($$2);
        $$1.xRot = $$0.getXRot($$2);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

