/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BreezeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.BreezeRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BreezeWindLayer
extends RenderLayer<BreezeRenderState, BreezeModel> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/breeze/breeze_wind.png");
    private final BreezeModel model;

    public BreezeWindLayer(EntityRendererProvider.Context $$0, RenderLayerParent<BreezeRenderState, BreezeModel> $$1) {
        super($$1);
        this.model = new BreezeModel($$0.bakeLayer(ModelLayers.BREEZE_WIND));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, BreezeRenderState $$3, float $$4, float $$5) {
        VertexConsumer $$6 = $$1.getBuffer(RenderType.breezeWind(TEXTURE_LOCATION, this.xOffset($$3.ageInTicks) % 1.0f, 0.0f));
        this.model.setupAnim($$3);
        BreezeRenderer.a(this.model, this.model.wind()).renderToBuffer($$0, $$6, $$2, OverlayTexture.NO_OVERLAY);
    }

    private float xOffset(float $$0) {
        return $$0 * 0.02f;
    }
}

