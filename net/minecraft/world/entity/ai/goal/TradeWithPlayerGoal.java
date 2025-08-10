/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;

public class TradeWithPlayerGoal
extends Goal {
    private final AbstractVillager mob;

    public TradeWithPlayerGoal(AbstractVillager $$0) {
        this.mob = $$0;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isAlive()) {
            return false;
        }
        if (this.mob.isInWater()) {
            return false;
        }
        if (!this.mob.onGround()) {
            return false;
        }
        if (this.mob.hurtMarked) {
            return false;
        }
        Player $$0 = this.mob.getTradingPlayer();
        if ($$0 == null) {
            return false;
        }
        if (this.mob.distanceToSqr($$0) > 16.0) {
            return false;
        }
        return $$0.containerMenu != null;
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.mob.setTradingPlayer(null);
    }
}

