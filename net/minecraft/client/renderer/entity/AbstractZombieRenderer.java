/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;

public abstract class AbstractZombieRenderer<T extends Zombie, S extends ZombieRenderState, M extends ZombieModel<S>>
extends HumanoidMobRenderer<T, S, M> {
    private static final ResourceLocation ZOMBIE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");

    protected AbstractZombieRenderer(EntityRendererProvider.Context $$0, M $$1, M $$2, M $$3, M $$4, M $$5, M $$6) {
        super($$0, $$1, $$2, 0.5f);
        this.addLayer(new HumanoidArmorLayer(this, $$3, $$4, $$5, $$6, $$0.getEquipmentRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(S $$0) {
        return ZOMBIE_LOCATION;
    }

    @Override
    public void extractRenderState(T $$0, S $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        ((ZombieRenderState)$$1).isAggressive = ((Mob)$$0).isAggressive();
        ((ZombieRenderState)$$1).isConverting = ((Zombie)$$0).isUnderWaterConverting();
    }

    @Override
    protected boolean isShaking(S $$0) {
        return super.isShaking($$0) || ((ZombieRenderState)$$0).isConverting;
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntityRenderState livingEntityRenderState) {
        return this.isShaking((S)((ZombieRenderState)livingEntityRenderState));
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((S)((ZombieRenderState)livingEntityRenderState));
    }
}

