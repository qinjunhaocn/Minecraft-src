/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.Control;

public class BodyRotationControl
implements Control {
    private final Mob mob;
    private static final int HEAD_STABLE_ANGLE = 15;
    private static final int DELAY_UNTIL_STARTING_TO_FACE_FORWARD = 10;
    private static final int HOW_LONG_IT_TAKES_TO_FACE_FORWARD = 10;
    private int headStableTime;
    private float lastStableYHeadRot;

    public BodyRotationControl(Mob $$0) {
        this.mob = $$0;
    }

    public void clientTick() {
        if (this.isMoving()) {
            this.mob.yBodyRot = this.mob.getYRot();
            this.rotateHeadIfNecessary();
            this.lastStableYHeadRot = this.mob.yHeadRot;
            this.headStableTime = 0;
            return;
        }
        if (this.notCarryingMobPassengers()) {
            if (Math.abs(this.mob.yHeadRot - this.lastStableYHeadRot) > 15.0f) {
                this.headStableTime = 0;
                this.lastStableYHeadRot = this.mob.yHeadRot;
                this.rotateBodyIfNecessary();
            } else {
                ++this.headStableTime;
                if (this.headStableTime > 10) {
                    this.rotateHeadTowardsFront();
                }
            }
        }
    }

    private void rotateBodyIfNecessary() {
        this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, this.mob.getMaxHeadYRot());
    }

    private void rotateHeadIfNecessary() {
        this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, this.mob.getMaxHeadYRot());
    }

    private void rotateHeadTowardsFront() {
        int $$0 = this.headStableTime - 10;
        float $$1 = Mth.clamp((float)$$0 / 10.0f, 0.0f, 1.0f);
        float $$2 = (float)this.mob.getMaxHeadYRot() * (1.0f - $$1);
        this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, $$2);
    }

    private boolean notCarryingMobPassengers() {
        return !(this.mob.getFirstPassenger() instanceof Mob);
    }

    private boolean isMoving() {
        double $$1;
        double $$0 = this.mob.getX() - this.mob.xo;
        return $$0 * $$0 + ($$1 = this.mob.getZ() - this.mob.zo) * $$1 > 2.500000277905201E-7;
    }
}

