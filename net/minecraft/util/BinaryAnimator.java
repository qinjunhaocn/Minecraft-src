/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util;

import net.minecraft.util.Mth;

public class BinaryAnimator {
    private final int animationLength;
    private final EasingFunction easingFunction;
    private int ticks;
    private int ticksOld;

    public BinaryAnimator(int $$0, EasingFunction $$1) {
        this.animationLength = $$0;
        this.easingFunction = $$1;
    }

    public BinaryAnimator(int $$02) {
        this($$02, $$0 -> $$0);
    }

    public void tick(boolean $$0) {
        this.ticksOld = this.ticks;
        if ($$0) {
            if (this.ticks < this.animationLength) {
                ++this.ticks;
            }
        } else if (this.ticks > 0) {
            --this.ticks;
        }
    }

    public float getFactor(float $$0) {
        float $$1 = Mth.lerp($$0, this.ticksOld, this.ticks) / (float)this.animationLength;
        return this.easingFunction.apply($$1);
    }

    public static interface EasingFunction {
        public float apply(float var1);
    }
}

