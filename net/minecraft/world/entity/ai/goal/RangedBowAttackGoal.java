/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Items;

public class RangedBowAttackGoal<T extends Monster>
extends Goal {
    private final T mob;
    private final double speedModifier;
    private int attackIntervalMin;
    private final float attackRadiusSqr;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public RangedBowAttackGoal(T $$0, double $$1, int $$2, float $$3) {
        this.mob = $$0;
        this.speedModifier = $$1;
        this.attackIntervalMin = $$2;
        this.attackRadiusSqr = $$3 * $$3;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public void setMinAttackInterval(int $$0) {
        this.attackIntervalMin = $$0;
    }

    @Override
    public boolean canUse() {
        if (((Mob)this.mob).getTarget() == null) {
            return false;
        }
        return this.isHoldingBow();
    }

    protected boolean isHoldingBow() {
        return ((LivingEntity)this.mob).isHolding(Items.BOW);
    }

    @Override
    public boolean canContinueToUse() {
        return (this.canUse() || !((Mob)this.mob).getNavigation().isDone()) && this.isHoldingBow();
    }

    @Override
    public void start() {
        super.start();
        ((Mob)this.mob).setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        ((Mob)this.mob).setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
        ((LivingEntity)this.mob).stopUsingItem();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        boolean $$3;
        LivingEntity $$0 = ((Mob)this.mob).getTarget();
        if ($$0 == null) {
            return;
        }
        double $$1 = ((Entity)this.mob).distanceToSqr($$0.getX(), $$0.getY(), $$0.getZ());
        boolean $$2 = ((Mob)this.mob).getSensing().hasLineOfSight($$0);
        boolean bl = $$3 = this.seeTime > 0;
        if ($$2 != $$3) {
            this.seeTime = 0;
        }
        this.seeTime = $$2 ? ++this.seeTime : --this.seeTime;
        if ($$1 > (double)this.attackRadiusSqr || this.seeTime < 20) {
            ((Mob)this.mob).getNavigation().moveTo($$0, this.speedModifier);
            this.strafingTime = -1;
        } else {
            ((Mob)this.mob).getNavigation().stop();
            ++this.strafingTime;
        }
        if (this.strafingTime >= 20) {
            if ((double)((Entity)this.mob).getRandom().nextFloat() < 0.3) {
                boolean bl2 = this.strafingClockwise = !this.strafingClockwise;
            }
            if ((double)((Entity)this.mob).getRandom().nextFloat() < 0.3) {
                this.strafingBackwards = !this.strafingBackwards;
            }
            this.strafingTime = 0;
        }
        if (this.strafingTime > -1) {
            if ($$1 > (double)(this.attackRadiusSqr * 0.75f)) {
                this.strafingBackwards = false;
            } else if ($$1 < (double)(this.attackRadiusSqr * 0.25f)) {
                this.strafingBackwards = true;
            }
            ((Mob)this.mob).getMoveControl().strafe(this.strafingBackwards ? -0.5f : 0.5f, this.strafingClockwise ? 0.5f : -0.5f);
            Entity entity = ((Entity)this.mob).getControlledVehicle();
            if (entity instanceof Mob) {
                Mob $$4 = (Mob)entity;
                $$4.lookAt($$0, 30.0f, 30.0f);
            }
            ((Mob)this.mob).lookAt($$0, 30.0f, 30.0f);
        } else {
            ((Mob)this.mob).getLookControl().setLookAt($$0, 30.0f, 30.0f);
        }
        if (((LivingEntity)this.mob).isUsingItem()) {
            int $$5;
            if (!$$2 && this.seeTime < -60) {
                ((LivingEntity)this.mob).stopUsingItem();
            } else if ($$2 && ($$5 = ((LivingEntity)this.mob).getTicksUsingItem()) >= 20) {
                ((LivingEntity)this.mob).stopUsingItem();
                ((RangedAttackMob)this.mob).performRangedAttack($$0, BowItem.getPowerForTime($$5));
                this.attackTime = this.attackIntervalMin;
            }
        } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
            ((LivingEntity)this.mob).startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, Items.BOW));
        }
    }
}

