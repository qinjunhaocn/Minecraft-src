/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public abstract class AbstractHorseRenderer<T extends AbstractHorse, S extends EquineRenderState, M extends EntityModel<? super S>>
extends AgeableMobRenderer<T, S, M> {
    public AbstractHorseRenderer(EntityRendererProvider.Context $$0, M $$1, M $$2) {
        super($$0, $$1, $$2, 0.75f);
    }

    @Override
    public void extractRenderState(T $$0, S $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        ((EquineRenderState)$$1).saddle = ((LivingEntity)$$0).getItemBySlot(EquipmentSlot.SADDLE).copy();
        ((EquineRenderState)$$1).isRidden = ((Entity)$$0).isVehicle();
        ((EquineRenderState)$$1).eatAnimation = ((AbstractHorse)$$0).getEatAnim($$2);
        ((EquineRenderState)$$1).standAnimation = ((AbstractHorse)$$0).getStandAnim($$2);
        ((EquineRenderState)$$1).feedingAnimation = ((AbstractHorse)$$0).getMouthAnim($$2);
        ((EquineRenderState)$$1).animateTail = ((AbstractHorse)$$0).tailCounter > 0;
    }
}

