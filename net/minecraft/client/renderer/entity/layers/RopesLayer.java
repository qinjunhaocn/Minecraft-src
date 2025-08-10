/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HappyGhastModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HappyGhastRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;

public class RopesLayer<M extends HappyGhastModel>
extends RenderLayer<HappyGhastRenderState, M> {
    private final RenderType ropes;
    private final HappyGhastModel adultModel;
    private final HappyGhastModel babyModel;

    public RopesLayer(RenderLayerParent<HappyGhastRenderState, M> $$0, EntityModelSet $$1, ResourceLocation $$2) {
        super($$0);
        this.ropes = RenderType.entityCutoutNoCull($$2);
        this.adultModel = new HappyGhastModel($$1.bakeLayer(ModelLayers.HAPPY_GHAST_ROPES));
        this.babyModel = new HappyGhastModel($$1.bakeLayer(ModelLayers.HAPPY_GHAST_BABY_ROPES));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, HappyGhastRenderState $$3, float $$4, float $$5) {
        if (!$$3.isLeashHolder || !$$3.bodyItem.is(ItemTags.HARNESSES)) {
            return;
        }
        HappyGhastModel $$6 = $$3.isBaby ? this.babyModel : this.adultModel;
        $$6.setupAnim($$3);
        $$6.renderToBuffer($$0, $$1.getBuffer(this.ropes), $$2, OverlayTexture.NO_OVERLAY);
    }
}

