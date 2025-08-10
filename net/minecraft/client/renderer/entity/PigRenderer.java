/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.model.ColdPigModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PigRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PigVariant;

public class PigRenderer
extends MobRenderer<Pig, PigRenderState, PigModel> {
    private final Map<PigVariant.ModelType, AdultAndBabyModelPair<PigModel>> models;

    public PigRenderer(EntityRendererProvider.Context $$02) {
        super($$02, new PigModel($$02.bakeLayer(ModelLayers.PIG)), 0.7f);
        this.models = PigRenderer.bakeModels($$02);
        this.addLayer(new SimpleEquipmentLayer<PigRenderState, PigModel, PigModel>(this, $$02.getEquipmentRenderer(), EquipmentClientInfo.LayerType.PIG_SADDLE, $$0 -> $$0.saddle, new PigModel($$02.bakeLayer(ModelLayers.PIG_SADDLE)), new PigModel($$02.bakeLayer(ModelLayers.PIG_BABY_SADDLE))));
    }

    private static Map<PigVariant.ModelType, AdultAndBabyModelPair<PigModel>> bakeModels(EntityRendererProvider.Context $$0) {
        return Maps.newEnumMap(Map.of((Object)PigVariant.ModelType.NORMAL, new AdultAndBabyModelPair<PigModel>(new PigModel($$0.bakeLayer(ModelLayers.PIG)), new PigModel($$0.bakeLayer(ModelLayers.PIG_BABY))), (Object)PigVariant.ModelType.COLD, new AdultAndBabyModelPair<ColdPigModel>(new ColdPigModel($$0.bakeLayer(ModelLayers.COLD_PIG)), new ColdPigModel($$0.bakeLayer(ModelLayers.COLD_PIG_BABY)))));
    }

    @Override
    public void render(PigRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        if ($$0.variant == null) {
            return;
        }
        this.model = this.models.get($$0.variant.modelAndTexture().model()).getModel($$0.isBaby);
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public ResourceLocation getTextureLocation(PigRenderState $$0) {
        return $$0.variant == null ? MissingTextureAtlasSprite.getLocation() : $$0.variant.modelAndTexture().asset().texturePath();
    }

    @Override
    public PigRenderState createRenderState() {
        return new PigRenderState();
    }

    @Override
    public void extractRenderState(Pig $$0, PigRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.saddle = $$0.getItemBySlot(EquipmentSlot.SADDLE).copy();
        $$1.variant = $$0.getVariant().value();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((PigRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

