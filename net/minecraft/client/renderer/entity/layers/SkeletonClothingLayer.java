/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.ResourceLocation;

public class SkeletonClothingLayer<S extends SkeletonRenderState, M extends EntityModel<S>>
extends RenderLayer<S, M> {
    private final SkeletonModel<S> layerModel;
    private final ResourceLocation clothesLocation;

    public SkeletonClothingLayer(RenderLayerParent<S, M> $$0, EntityModelSet $$1, ModelLayerLocation $$2, ResourceLocation $$3) {
        super($$0);
        this.clothesLocation = $$3;
        this.layerModel = new SkeletonModel($$1.bakeLayer($$2));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, S $$3, float $$4, float $$5) {
        SkeletonClothingLayer.coloredCutoutModelCopyLayerRender(this.layerModel, this.clothesLocation, $$0, $$1, $$2, $$3, -1);
    }
}

