/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class EatBlockGoal
extends Goal {
    private static final int EAT_ANIMATION_TICKS = 40;
    private static final Predicate<BlockState> IS_EDIBLE = $$0 -> $$0.is(BlockTags.EDIBLE_FOR_SHEEP);
    private final Mob mob;
    private final Level level;
    private int eatAnimationTick;

    public EatBlockGoal(Mob $$0) {
        this.mob = $$0;
        this.level = $$0.level();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
            return false;
        }
        BlockPos $$0 = this.mob.blockPosition();
        if (IS_EDIBLE.test(this.level.getBlockState($$0))) {
            return true;
        }
        return this.level.getBlockState($$0.below()).is(Blocks.GRASS_BLOCK);
    }

    @Override
    public void start() {
        this.eatAnimationTick = this.adjustedTickDelay(40);
        this.level.broadcastEntityEvent(this.mob, (byte)10);
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.eatAnimationTick = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.eatAnimationTick > 0;
    }

    public int getEatAnimationTick() {
        return this.eatAnimationTick;
    }

    @Override
    public void tick() {
        this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        if (this.eatAnimationTick != this.adjustedTickDelay(4)) {
            return;
        }
        BlockPos $$0 = this.mob.blockPosition();
        if (IS_EDIBLE.test(this.level.getBlockState($$0))) {
            if (EatBlockGoal.getServerLevel(this.level).getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                this.level.destroyBlock($$0, false);
            }
            this.mob.ate();
        } else {
            BlockPos $$1 = $$0.below();
            if (this.level.getBlockState($$1).is(Blocks.GRASS_BLOCK)) {
                if (EatBlockGoal.getServerLevel(this.level).getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    this.level.levelEvent(2001, $$1, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                    this.level.setBlock($$1, Blocks.DIRT.defaultBlockState(), 2);
                }
                this.mob.ate();
            }
        }
    }
}

