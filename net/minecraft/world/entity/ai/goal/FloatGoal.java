/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class FloatGoal
extends Goal {
    private final Mob mob;

    public FloatGoal(Mob $$0) {
        this.mob = $$0;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP));
        $$0.getNavigation().setCanFloat(true);
    }

    @Override
    public boolean canUse() {
        return this.mob.isInWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getFluidJumpThreshold() || this.mob.isInLava();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.mob.getRandom().nextFloat() < 0.8f) {
            this.mob.getJumpControl().jump();
        }
    }
}

