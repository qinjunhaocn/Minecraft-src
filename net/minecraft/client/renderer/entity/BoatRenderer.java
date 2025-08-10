/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BoatRenderer
extends AbstractBoatRenderer {
    private final Model waterPatchModel;
    private final ResourceLocation texture;
    private final EntityModel<BoatRenderState> model;

    public BoatRenderer(EntityRendererProvider.Context $$02, ModelLayerLocation $$1) {
        super($$02);
        this.texture = $$1.model().withPath($$0 -> "textures/entity/" + $$0 + ".png");
        this.waterPatchModel = new Model.Simple($$02.bakeLayer(ModelLayers.BOAT_WATER_PATCH), $$0 -> RenderType.waterMask());
        this.model = new BoatModel($$02.bakeLayer($$1));
    }

    @Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }

    @Override
    protected RenderType renderType() {
        return this.model.renderType(this.texture);
    }

    @Override
    protected void renderTypeAdditions(BoatRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        if (!$$0.isUnderWater) {
            this.waterPatchModel.renderToBuffer($$1, $$2.getBuffer(this.waterPatchModel.renderType(this.texture)), $$3, OverlayTexture.NO_OVERLAY);
        }
    }
}

