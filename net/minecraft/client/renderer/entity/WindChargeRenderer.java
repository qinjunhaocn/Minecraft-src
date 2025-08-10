/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.WindChargeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;

public class WindChargeRenderer
extends EntityRenderer<AbstractWindCharge, EntityRenderState> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/wind_charge.png");
    private final WindChargeModel model;

    public WindChargeRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.model = new WindChargeModel($$0.bakeLayer(ModelLayers.WIND_CHARGE));
    }

    @Override
    public void render(EntityRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        VertexConsumer $$4 = $$2.getBuffer(RenderType.breezeWind(TEXTURE_LOCATION, this.xOffset($$0.ageInTicks) % 1.0f, 0.0f));
        this.model.setupAnim($$0);
        this.model.renderToBuffer($$1, $$4, $$3, OverlayTexture.NO_OVERLAY);
        super.render($$0, $$1, $$2, $$3);
    }

    protected float xOffset(float $$0) {
        return $$0 * 0.03f;
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}

