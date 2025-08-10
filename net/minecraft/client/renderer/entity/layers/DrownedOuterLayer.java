/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.ResourceLocation;

public class DrownedOuterLayer
extends RenderLayer<ZombieRenderState, DrownedModel> {
    private static final ResourceLocation DROWNED_OUTER_LAYER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/drowned_outer_layer.png");
    private final DrownedModel model;
    private final DrownedModel babyModel;

    public DrownedOuterLayer(RenderLayerParent<ZombieRenderState, DrownedModel> $$0, EntityModelSet $$1) {
        super($$0);
        this.model = new DrownedModel($$1.bakeLayer(ModelLayers.DROWNED_OUTER_LAYER));
        this.babyModel = new DrownedModel($$1.bakeLayer(ModelLayers.DROWNED_BABY_OUTER_LAYER));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, ZombieRenderState $$3, float $$4, float $$5) {
        DrownedModel $$6 = $$3.isBaby ? this.babyModel : this.model;
        DrownedOuterLayer.coloredCutoutModelCopyLayerRender($$6, DROWNED_OUTER_LAYER_LOCATION, $$0, $$1, $$2, $$3, -1);
    }
}

