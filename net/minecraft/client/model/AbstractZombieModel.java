/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;

public abstract class AbstractZombieModel<S extends ZombieRenderState>
extends HumanoidModel<S> {
    protected AbstractZombieModel(ModelPart $$0) {
        super($$0);
    }

    @Override
    public void setupAnim(S $$0) {
        super.setupAnim($$0);
        float $$1 = ((ZombieRenderState)$$0).attackTime;
        AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, ((ZombieRenderState)$$0).isAggressive, $$1, ((ZombieRenderState)$$0).ageInTicks);
    }
}

