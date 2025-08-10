/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonSittingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;

public class DragonSittingAttackingPhase
extends AbstractDragonSittingPhase {
    private static final int ROAR_DURATION = 40;
    private int attackingTicks;

    public DragonSittingAttackingPhase(EnderDragon $$0) {
        super($$0);
    }

    @Override
    public void doClientTick() {
        this.dragon.level().playLocalSound(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ(), SoundEvents.ENDER_DRAGON_GROWL, this.dragon.getSoundSource(), 2.5f, 0.8f + this.dragon.getRandom().nextFloat() * 0.3f, false);
    }

    @Override
    public void doServerTick(ServerLevel $$0) {
        if (this.attackingTicks++ >= 40) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_FLAMING);
        }
    }

    @Override
    public void begin() {
        this.attackingTicks = 0;
    }

    public EnderDragonPhase<DragonSittingAttackingPhase> getPhase() {
        return EnderDragonPhase.SITTING_ATTACKING;
    }
}

