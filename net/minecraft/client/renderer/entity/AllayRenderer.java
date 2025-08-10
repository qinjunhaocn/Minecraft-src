/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.AllayModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.AllayRenderState;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.allay.Allay;

public class AllayRenderer
extends MobRenderer<Allay, AllayRenderState, AllayModel> {
    private static final ResourceLocation ALLAY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/allay/allay.png");

    public AllayRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new AllayModel($$0.bakeLayer(ModelLayers.ALLAY)), 0.4f);
        this.addLayer(new ItemInHandLayer<AllayRenderState, AllayModel>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(AllayRenderState $$0) {
        return ALLAY_TEXTURE;
    }

    @Override
    public AllayRenderState createRenderState() {
        return new AllayRenderState();
    }

    @Override
    public void extractRenderState(Allay $$0, AllayRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        ArmedEntityRenderState.extractArmedEntityRenderState($$0, $$1, this.itemModelResolver);
        $$1.isDancing = $$0.isDancing();
        $$1.isSpinning = $$0.isSpinning();
        $$1.spinningProgress = $$0.getSpinningProgress($$2);
        $$1.holdingAnimationProgress = $$0.getHoldingItemAnimationProgress($$2);
    }

    @Override
    protected int getBlockLightLevel(Allay $$0, BlockPos $$1) {
        return 15;
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((AllayRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

