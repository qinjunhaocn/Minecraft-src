/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class MarsagliaPolarGaussian {
    public final RandomSource randomSource;
    private double nextNextGaussian;
    private boolean haveNextNextGaussian;

    public MarsagliaPolarGaussian(RandomSource $$0) {
        this.randomSource = $$0;
    }

    public void reset() {
        this.haveNextNextGaussian = false;
    }

    public double nextGaussian() {
        double $$1;
        double $$0;
        double $$2;
        if (this.haveNextNextGaussian) {
            this.haveNextNextGaussian = false;
            return this.nextNextGaussian;
        }
        do {
            $$0 = 2.0 * this.randomSource.nextDouble() - 1.0;
            $$1 = 2.0 * this.randomSource.nextDouble() - 1.0;
        } while (($$2 = Mth.square($$0) + Mth.square($$1)) >= 1.0 || $$2 == 0.0);
        double $$3 = Math.sqrt(-2.0 * Math.log($$2) / $$2);
        this.nextNextGaussian = $$1 * $$3;
        this.haveNextNextGaussian = true;
        return $$0 * $$3;
    }
}

