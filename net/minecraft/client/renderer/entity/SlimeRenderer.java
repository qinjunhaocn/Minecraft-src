/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Slime;

public class SlimeRenderer
extends MobRenderer<Slime, SlimeRenderState, SlimeModel> {
    public static final ResourceLocation SLIME_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/slime/slime.png");

    public SlimeRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new SlimeModel($$0.bakeLayer(ModelLayers.SLIME)), 0.25f);
        this.addLayer(new SlimeOuterLayer(this, $$0.getModelSet()));
    }

    @Override
    protected float getShadowRadius(SlimeRenderState $$0) {
        return (float)$$0.size * 0.25f;
    }

    @Override
    protected void scale(SlimeRenderState $$0, PoseStack $$1) {
        float $$2 = 0.999f;
        $$1.scale(0.999f, 0.999f, 0.999f);
        $$1.translate(0.0f, 0.001f, 0.0f);
        float $$3 = $$0.size;
        float $$4 = $$0.squish / ($$3 * 0.5f + 1.0f);
        float $$5 = 1.0f / ($$4 + 1.0f);
        $$1.scale($$5 * $$3, 1.0f / $$5 * $$3, $$5 * $$3);
    }

    @Override
    public ResourceLocation getTextureLocation(SlimeRenderState $$0) {
        return SLIME_LOCATION;
    }

    @Override
    public SlimeRenderState createRenderState() {
        return new SlimeRenderState();
    }

    @Override
    public void extractRenderState(Slime $$0, SlimeRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.squish = Mth.lerp($$2, $$0.oSquish, $$0.squish);
        $$1.size = $$0.getSize();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((SlimeRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState entityRenderState) {
        return this.getShadowRadius((SlimeRenderState)entityRenderState);
    }
}

