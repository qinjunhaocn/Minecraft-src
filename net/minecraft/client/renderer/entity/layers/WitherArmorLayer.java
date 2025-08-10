/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.client.renderer.entity.state.WitherRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WitherArmorLayer
extends EnergySwirlLayer<WitherRenderState, WitherBossModel> {
    private static final ResourceLocation WITHER_ARMOR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_armor.png");
    private final WitherBossModel model;

    public WitherArmorLayer(RenderLayerParent<WitherRenderState, WitherBossModel> $$0, EntityModelSet $$1) {
        super($$0);
        this.model = new WitherBossModel($$1.bakeLayer(ModelLayers.WITHER_ARMOR));
    }

    @Override
    protected boolean isPowered(WitherRenderState $$0) {
        return $$0.isPowered;
    }

    @Override
    protected float xOffset(float $$0) {
        return Mth.cos($$0 * 0.02f) * 3.0f;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return WITHER_ARMOR_LOCATION;
    }

    @Override
    protected WitherBossModel model() {
        return this.model;
    }

    @Override
    protected /* synthetic */ EntityModel model() {
        return this.model();
    }
}

