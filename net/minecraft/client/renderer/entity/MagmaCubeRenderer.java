/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.LavaSlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.MagmaCube;

public class MagmaCubeRenderer
extends MobRenderer<MagmaCube, SlimeRenderState, LavaSlimeModel> {
    private static final ResourceLocation MAGMACUBE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/slime/magmacube.png");

    public MagmaCubeRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new LavaSlimeModel($$0.bakeLayer(ModelLayers.MAGMA_CUBE)), 0.25f);
    }

    @Override
    protected int getBlockLightLevel(MagmaCube $$0, BlockPos $$1) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(SlimeRenderState $$0) {
        return MAGMACUBE_LOCATION;
    }

    @Override
    public SlimeRenderState createRenderState() {
        return new SlimeRenderState();
    }

    @Override
    public void extractRenderState(MagmaCube $$0, SlimeRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.squish = Mth.lerp($$2, $$0.oSquish, $$0.squish);
        $$1.size = $$0.getSize();
    }

    @Override
    protected float getShadowRadius(SlimeRenderState $$0) {
        return (float)$$0.size * 0.25f;
    }

    @Override
    protected void scale(SlimeRenderState $$0, PoseStack $$1) {
        int $$2 = $$0.size;
        float $$3 = $$0.squish / ((float)$$2 * 0.5f + 1.0f);
        float $$4 = 1.0f / ($$3 + 1.0f);
        $$1.scale($$4 * (float)$$2, 1.0f / $$4 * (float)$$2, $$4 * (float)$$2);
    }

    @Override
    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((SlimeRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((SlimeRenderState)livingEntityRenderState);
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

