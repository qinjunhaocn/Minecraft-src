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
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.joml.Quaternionfc;

public class ThrownTridentRenderer
extends EntityRenderer<ThrownTrident, ThrownTridentRenderState> {
    public static final ResourceLocation TRIDENT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/trident.png");
    private final TridentModel model;

    public ThrownTridentRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.model = new TridentModel($$0.bakeLayer(ModelLayers.TRIDENT));
    }

    @Override
    public void render(ThrownTridentRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$0.yRot - 90.0f));
        $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees($$0.xRot + 90.0f));
        VertexConsumer $$4 = ItemRenderer.getFoilBuffer($$2, this.model.renderType(TRIDENT_LOCATION), false, $$0.isFoil);
        this.model.renderToBuffer($$1, $$4, $$3, OverlayTexture.NO_OVERLAY);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public ThrownTridentRenderState createRenderState() {
        return new ThrownTridentRenderState();
    }

    @Override
    public void extractRenderState(ThrownTrident $$0, ThrownTridentRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.yRot = $$0.getYRot($$2);
        $$1.xRot = $$0.getXRot($$2);
        $$1.isFoil = $$0.isFoil();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

