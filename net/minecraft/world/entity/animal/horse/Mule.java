/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.level.Level;

public class Mule
extends AbstractChestedHorse {
    public Mule(EntityType<? extends Mule> $$0, Level $$1) {
        super((EntityType<? extends AbstractChestedHorse>)$$0, $$1);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.MULE_AMBIENT;
    }

    @Override
    protected SoundEvent getAngrySound() {
        return SoundEvents.MULE_ANGRY;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.MULE_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent getEatingSound() {
        return SoundEvents.MULE_EAT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.MULE_HURT;
    }

    @Override
    protected void playJumpSound() {
        this.playSound(SoundEvents.MULE_JUMP, 0.4f, 1.0f);
    }

    @Override
    protected void playChestEquipsSound() {
        this.playSound(SoundEvents.MULE_CHEST, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        return EntityType.MULE.create($$0, EntitySpawnReason.BREEDING);
    }
}

