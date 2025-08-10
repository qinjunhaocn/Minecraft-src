/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class TemptGoal
extends Goal {
    private static final TargetingConditions TEMPT_TARGETING = TargetingConditions.forNonCombat().ignoreLineOfSight();
    private static final double DEFAULT_STOP_DISTANCE = 2.5;
    private final TargetingConditions targetingConditions;
    protected final Mob mob;
    protected final double speedModifier;
    private double px;
    private double py;
    private double pz;
    private double pRotX;
    private double pRotY;
    @Nullable
    protected Player player;
    private int calmDown;
    private boolean isRunning;
    private final Predicate<ItemStack> items;
    private final boolean canScare;
    private final double stopDistance;

    public TemptGoal(PathfinderMob $$0, double $$1, Predicate<ItemStack> $$2, boolean $$3) {
        this((Mob)$$0, $$1, $$2, $$3, 2.5);
    }

    public TemptGoal(PathfinderMob $$0, double $$1, Predicate<ItemStack> $$2, boolean $$3, double $$4) {
        this((Mob)$$0, $$1, $$2, $$3, $$4);
    }

    TemptGoal(Mob $$02, double $$12, Predicate<ItemStack> $$2, boolean $$3, double $$4) {
        this.mob = $$02;
        this.speedModifier = $$12;
        this.items = $$2;
        this.canScare = $$3;
        this.stopDistance = $$4;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.targetingConditions = TEMPT_TARGETING.copy().selector(($$0, $$1) -> this.shouldFollow($$0));
    }

    @Override
    public boolean canUse() {
        if (this.calmDown > 0) {
            --this.calmDown;
            return false;
        }
        this.player = TemptGoal.getServerLevel(this.mob).getNearestPlayer(this.targetingConditions.range(this.mob.getAttributeValue(Attributes.TEMPT_RANGE)), this.mob);
        return this.player != null;
    }

    private boolean shouldFollow(LivingEntity $$0) {
        return this.items.test($$0.getMainHandItem()) || this.items.test($$0.getOffhandItem());
    }

    @Override
    public boolean canContinueToUse() {
        if (this.canScare()) {
            if (this.mob.distanceToSqr(this.player) < 36.0) {
                if (this.player.distanceToSqr(this.px, this.py, this.pz) > 0.010000000000000002) {
                    return false;
                }
                if (Math.abs((double)this.player.getXRot() - this.pRotX) > 5.0 || Math.abs((double)this.player.getYRot() - this.pRotY) > 5.0) {
                    return false;
                }
            } else {
                this.px = this.player.getX();
                this.py = this.player.getY();
                this.pz = this.player.getZ();
            }
            this.pRotX = this.player.getXRot();
            this.pRotY = this.player.getYRot();
        }
        return this.canUse();
    }

    protected boolean canScare() {
        return this.canScare;
    }

    @Override
    public void start() {
        this.px = this.player.getX();
        this.py = this.player.getY();
        this.pz = this.player.getZ();
        this.isRunning = true;
    }

    @Override
    public void stop() {
        this.player = null;
        this.stopNavigation();
        this.calmDown = TemptGoal.reducedTickDelay(100);
        this.isRunning = false;
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(this.player, this.mob.getMaxHeadYRot() + 20, this.mob.getMaxHeadXRot());
        if (this.mob.distanceToSqr(this.player) < this.stopDistance * this.stopDistance) {
            this.stopNavigation();
        } else {
            this.navigateTowards(this.player);
        }
    }

    protected void stopNavigation() {
        this.mob.getNavigation().stop();
    }

    protected void navigateTowards(Player $$0) {
        this.mob.getNavigation().moveTo($$0, this.speedModifier);
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public static class ForNonPathfinders
    extends TemptGoal {
        public ForNonPathfinders(Mob $$0, double $$1, Predicate<ItemStack> $$2, boolean $$3, double $$4) {
            super($$0, $$1, $$2, $$3, $$4);
        }

        @Override
        protected void stopNavigation() {
            this.mob.getMoveControl().setWait();
        }

        @Override
        protected void navigateTowards(Player $$0) {
            Vec3 $$1 = $$0.getEyePosition().subtract(this.mob.position()).scale(this.mob.getRandom().nextDouble()).add(this.mob.position());
            this.mob.getMoveControl().setWantedPosition($$1.x, $$1.y, $$1.z, this.speedModifier);
        }
    }
}

