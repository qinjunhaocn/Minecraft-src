/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.ColdChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.ChickenVariant;

public class ChickenRenderer
extends MobRenderer<Chicken, ChickenRenderState, ChickenModel> {
    private final Map<ChickenVariant.ModelType, AdultAndBabyModelPair<ChickenModel>> models;

    public ChickenRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new ChickenModel($$0.bakeLayer(ModelLayers.CHICKEN)), 0.3f);
        this.models = ChickenRenderer.bakeModels($$0);
    }

    private static Map<ChickenVariant.ModelType, AdultAndBabyModelPair<ChickenModel>> bakeModels(EntityRendererProvider.Context $$0) {
        return Maps.newEnumMap(Map.of((Object)ChickenVariant.ModelType.NORMAL, new AdultAndBabyModelPair<ChickenModel>(new ChickenModel($$0.bakeLayer(ModelLayers.CHICKEN)), new ChickenModel($$0.bakeLayer(ModelLayers.CHICKEN_BABY))), (Object)ChickenVariant.ModelType.COLD, new AdultAndBabyModelPair<ColdChickenModel>(new ColdChickenModel($$0.bakeLayer(ModelLayers.COLD_CHICKEN)), new ColdChickenModel($$0.bakeLayer(ModelLayers.COLD_CHICKEN_BABY)))));
    }

    @Override
    public void render(ChickenRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        if ($$0.variant == null) {
            return;
        }
        this.model = this.models.get($$0.variant.modelAndTexture().model()).getModel($$0.isBaby);
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public ResourceLocation getTextureLocation(ChickenRenderState $$0) {
        return $$0.variant == null ? MissingTextureAtlasSprite.getLocation() : $$0.variant.modelAndTexture().asset().texturePath();
    }

    @Override
    public ChickenRenderState createRenderState() {
        return new ChickenRenderState();
    }

    @Override
    public void extractRenderState(Chicken $$0, ChickenRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.flap = Mth.lerp($$2, $$0.oFlap, $$0.flap);
        $$1.flapSpeed = Mth.lerp($$2, $$0.oFlapSpeed, $$0.flapSpeed);
        $$1.variant = $$0.getVariant().value();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((ChickenRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

