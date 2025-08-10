/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Blaze;

public class BlazeRenderer
extends MobRenderer<Blaze, LivingEntityRenderState, BlazeModel> {
    private static final ResourceLocation BLAZE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/blaze.png");

    public BlazeRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new BlazeModel($$0.bakeLayer(ModelLayers.BLAZE)), 0.5f);
    }

    @Override
    protected int getBlockLightLevel(Blaze $$0, BlockPos $$1) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(LivingEntityRenderState $$0) {
        return BLAZE_LOCATION;
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

