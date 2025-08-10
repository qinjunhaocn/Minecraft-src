/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.fog.environment;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.MobEffectFogEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BlindnessFogEnvironment
extends MobEffectFogEnvironment {
    @Override
    public Holder<MobEffect> getMobEffect() {
        return MobEffects.BLINDNESS;
    }

    @Override
    public void setupFog(FogData $$0, Entity $$1, BlockPos $$2, ClientLevel $$3, float $$4, DeltaTracker $$5) {
        LivingEntity $$6;
        MobEffectInstance $$7;
        if ($$1 instanceof LivingEntity && ($$7 = ($$6 = (LivingEntity)$$1).getEffect(this.getMobEffect())) != null) {
            float $$8 = $$7.isInfiniteDuration() ? 5.0f : Mth.lerp(Math.min(1.0f, (float)$$7.getDuration() / 20.0f), $$4, 5.0f);
            $$0.environmentalStart = $$8 * 0.25f;
            $$0.environmentalEnd = $$8;
            $$0.skyEnd = $$8 * 0.8f;
            $$0.cloudEnd = $$8 * 0.8f;
        }
    }

    @Override
    public float getModifiedDarkness(LivingEntity $$0, float $$1, float $$2) {
        MobEffectInstance $$3 = $$0.getEffect(this.getMobEffect());
        if ($$3 != null) {
            $$1 = $$3.endsWithin(19) ? Math.max((float)$$3.getDuration() / 20.0f, $$1) : 1.0f;
        }
        return $$1;
    }
}

