/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class CatCollarLayer
extends RenderLayer<CatRenderState, CatModel> {
    private static final ResourceLocation CAT_COLLAR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/cat/cat_collar.png");
    private final CatModel adultModel;
    private final CatModel babyModel;

    public CatCollarLayer(RenderLayerParent<CatRenderState, CatModel> $$0, EntityModelSet $$1) {
        super($$0);
        this.adultModel = new CatModel($$1.bakeLayer(ModelLayers.CAT_COLLAR));
        this.babyModel = new CatModel($$1.bakeLayer(ModelLayers.CAT_BABY_COLLAR));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, CatRenderState $$3, float $$4, float $$5) {
        DyeColor $$6 = $$3.collarColor;
        if ($$6 == null) {
            return;
        }
        int $$7 = $$6.getTextureDiffuseColor();
        CatModel $$8 = $$3.isBaby ? this.babyModel : this.adultModel;
        CatCollarLayer.coloredCutoutModelCopyLayerRender($$8, CAT_COLLAR_LOCATION, $$0, $$1, $$2, $$3, $$7);
    }
}

