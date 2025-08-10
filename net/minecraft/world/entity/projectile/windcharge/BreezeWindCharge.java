/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.projectile.windcharge;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BreezeWindCharge
extends AbstractWindCharge {
    private static final float RADIUS = 3.0f;

    public BreezeWindCharge(EntityType<? extends AbstractWindCharge> $$0, Level $$1) {
        super($$0, $$1);
    }

    public BreezeWindCharge(Breeze $$0, Level $$1) {
        super(EntityType.BREEZE_WIND_CHARGE, $$1, $$0, $$0.getX(), $$0.getFiringYPosition(), $$0.getZ());
    }

    @Override
    protected void explode(Vec3 $$0) {
        this.level().explode(this, null, EXPLOSION_DAMAGE_CALCULATOR, $$0.x(), $$0.y(), $$0.z(), 3.0f, false, Level.ExplosionInteraction.TRIGGER, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.BREEZE_WIND_CHARGE_BURST);
    }
}

