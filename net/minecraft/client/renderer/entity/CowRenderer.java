/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.CowRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.CowVariant;

public class CowRenderer
extends MobRenderer<Cow, CowRenderState, CowModel> {
    private final Map<CowVariant.ModelType, AdultAndBabyModelPair<CowModel>> models;

    public CowRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new CowModel($$0.bakeLayer(ModelLayers.COW)), 0.7f);
        this.models = CowRenderer.bakeModels($$0);
    }

    private static Map<CowVariant.ModelType, AdultAndBabyModelPair<CowModel>> bakeModels(EntityRendererProvider.Context $$0) {
        return Maps.newEnumMap(Map.of((Object)CowVariant.ModelType.NORMAL, new AdultAndBabyModelPair<CowModel>(new CowModel($$0.bakeLayer(ModelLayers.COW)), new CowModel($$0.bakeLayer(ModelLayers.COW_BABY))), (Object)CowVariant.ModelType.WARM, new AdultAndBabyModelPair<CowModel>(new CowModel($$0.bakeLayer(ModelLayers.WARM_COW)), new CowModel($$0.bakeLayer(ModelLayers.WARM_COW_BABY))), (Object)CowVariant.ModelType.COLD, new AdultAndBabyModelPair<CowModel>(new CowModel($$0.bakeLayer(ModelLayers.COLD_COW)), new CowModel($$0.bakeLayer(ModelLayers.COLD_COW_BABY)))));
    }

    @Override
    public ResourceLocation getTextureLocation(CowRenderState $$0) {
        return $$0.variant == null ? MissingTextureAtlasSprite.getLocation() : $$0.variant.modelAndTexture().asset().texturePath();
    }

    @Override
    public CowRenderState createRenderState() {
        return new CowRenderState();
    }

    @Override
    public void extractRenderState(Cow $$0, CowRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.variant = $$0.getVariant().value();
    }

    @Override
    public void render(CowRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        if ($$0.variant == null) {
            return;
        }
        this.model = this.models.get($$0.variant.modelAndTexture().model()).getModel($$0.isBaby);
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((CowRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

