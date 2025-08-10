/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.item.CrossbowItem;

public abstract class IllagerRenderer<T extends AbstractIllager, S extends IllagerRenderState>
extends MobRenderer<T, S, IllagerModel<S>> {
    protected IllagerRenderer(EntityRendererProvider.Context $$0, IllagerModel<S> $$1, float $$2) {
        super($$0, $$1, $$2);
        this.addLayer(new CustomHeadLayer(this, $$0.getModelSet()));
    }

    @Override
    public void extractRenderState(T $$0, S $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        ArmedEntityRenderState.extractArmedEntityRenderState($$0, $$1, this.itemModelResolver);
        ((IllagerRenderState)$$1).isRiding = ((Entity)$$0).isPassenger();
        ((IllagerRenderState)$$1).mainArm = ((Mob)$$0).getMainArm();
        ((IllagerRenderState)$$1).armPose = ((AbstractIllager)$$0).getArmPose();
        ((IllagerRenderState)$$1).maxCrossbowChargeDuration = ((IllagerRenderState)$$1).armPose == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE ? CrossbowItem.getChargeDuration(((LivingEntity)$$0).getUseItem(), $$0) : 0;
        ((IllagerRenderState)$$1).ticksUsingItem = ((LivingEntity)$$0).getTicksUsingItem();
        ((IllagerRenderState)$$1).attackAnim = ((LivingEntity)$$0).getAttackAnim($$2);
        ((IllagerRenderState)$$1).isAggressive = ((Mob)$$0).isAggressive();
    }
}

