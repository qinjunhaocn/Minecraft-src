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

public class DarknessFogEnvironment
extends MobEffectFogEnvironment {
    @Override
    public Holder<MobEffect> getMobEffect() {
        return MobEffects.DARKNESS;
    }

    @Override
    public void setupFog(FogData $$0, Entity $$1, BlockPos $$2, ClientLevel $$3, float $$4, DeltaTracker $$5) {
        LivingEntity $$6;
        MobEffectInstance $$7;
        if ($$1 instanceof LivingEntity && ($$7 = ($$6 = (LivingEntity)$$1).getEffect(this.getMobEffect())) != null) {
            float $$8 = Mth.lerp($$7.getBlendFactor($$6, $$5.getGameTimeDeltaPartialTick(false)), $$4, 15.0f);
            $$0.environmentalStart = $$8 * 0.75f;
            $$0.environmentalEnd = $$8;
            $$0.skyEnd = $$8;
            $$0.cloudEnd = $$8;
        }
    }

    @Override
    public float getModifiedDarkness(LivingEntity $$0, float $$1, float $$2) {
        MobEffectInstance $$3 = $$0.getEffect(this.getMobEffect());
        return $$3 != null ? Math.max($$3.getBlendFactor($$0, $$2), $$1) : $$1;
    }
}

