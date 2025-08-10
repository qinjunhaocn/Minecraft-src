/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class SheepWoolUndercoatLayer
extends RenderLayer<SheepRenderState, SheepModel> {
    private static final ResourceLocation SHEEP_WOOL_UNDERCOAT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/sheep/sheep_wool_undercoat.png");
    private final EntityModel<SheepRenderState> adultModel;
    private final EntityModel<SheepRenderState> babyModel;

    public SheepWoolUndercoatLayer(RenderLayerParent<SheepRenderState, SheepModel> $$0, EntityModelSet $$1) {
        super($$0);
        this.adultModel = new SheepFurModel($$1.bakeLayer(ModelLayers.SHEEP_WOOL_UNDERCOAT));
        this.babyModel = new SheepFurModel($$1.bakeLayer(ModelLayers.SHEEP_BABY_WOOL_UNDERCOAT));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, SheepRenderState $$3, float $$4, float $$5) {
        if ($$3.isInvisible || !$$3.isJebSheep() && $$3.woolColor == DyeColor.WHITE) {
            return;
        }
        EntityModel<SheepRenderState> $$6 = $$3.isBaby ? this.babyModel : this.adultModel;
        SheepWoolUndercoatLayer.coloredCutoutModelCopyLayerRender($$6, SHEEP_WOOL_UNDERCOAT_LOCATION, $$0, $$1, $$2, $$3, $$3.getWoolColor());
    }
}

