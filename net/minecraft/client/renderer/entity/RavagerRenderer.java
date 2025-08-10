/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.RavagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.RavagerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ravager;

public class RavagerRenderer
extends MobRenderer<Ravager, RavagerRenderState, RavagerModel> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/illager/ravager.png");

    public RavagerRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new RavagerModel($$0.bakeLayer(ModelLayers.RAVAGER)), 1.1f);
    }

    @Override
    public ResourceLocation getTextureLocation(RavagerRenderState $$0) {
        return TEXTURE_LOCATION;
    }

    @Override
    public RavagerRenderState createRenderState() {
        return new RavagerRenderState();
    }

    @Override
    public void extractRenderState(Ravager $$0, RavagerRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.stunnedTicksRemaining = (float)$$0.getStunnedTick() > 0.0f ? (float)$$0.getStunnedTick() - $$2 : 0.0f;
        $$1.attackTicksRemaining = (float)$$0.getAttackTick() > 0.0f ? (float)$$0.getAttackTick() - $$2 : 0.0f;
        $$1.roarAnimation = $$0.getRoarTick() > 0 ? ((float)(20 - $$0.getRoarTick()) + $$2) / 20.0f : 0.0f;
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((RavagerRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

