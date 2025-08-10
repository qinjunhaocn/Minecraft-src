/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.NoiseUtils;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public final class ImprovedNoise {
    private static final float SHIFT_UP_EPSILON = 1.0E-7f;
    private final byte[] p;
    public final double xo;
    public final double yo;
    public final double zo;

    public ImprovedNoise(RandomSource $$0) {
        this.xo = $$0.nextDouble() * 256.0;
        this.yo = $$0.nextDouble() * 256.0;
        this.zo = $$0.nextDouble() * 256.0;
        this.p = new byte[256];
        for (int $$1 = 0; $$1 < 256; ++$$1) {
            this.p[$$1] = (byte)$$1;
        }
        for (int $$2 = 0; $$2 < 256; ++$$2) {
            int $$3 = $$0.nextInt(256 - $$2);
            byte $$4 = this.p[$$2];
            this.p[$$2] = this.p[$$2 + $$3];
            this.p[$$2 + $$3] = $$4;
        }
    }

    public double noise(double $$0, double $$1, double $$2) {
        return this.noise($$0, $$1, $$2, 0.0, 0.0);
    }

    @Deprecated
    public double noise(double $$0, double $$1, double $$2, double $$3, double $$4) {
        double $$17;
        double $$5 = $$0 + this.xo;
        double $$6 = $$1 + this.yo;
        double $$7 = $$2 + this.zo;
        int $$8 = Mth.floor($$5);
        int $$9 = Mth.floor($$6);
        int $$10 = Mth.floor($$7);
        double $$11 = $$5 - (double)$$8;
        double $$12 = $$6 - (double)$$9;
        double $$13 = $$7 - (double)$$10;
        if ($$3 != 0.0) {
            double $$15;
            if ($$4 >= 0.0 && $$4 < $$12) {
                double $$14 = $$4;
            } else {
                $$15 = $$12;
            }
            double $$16 = (double)Mth.floor($$15 / $$3 + (double)1.0E-7f) * $$3;
        } else {
            $$17 = 0.0;
        }
        return this.sampleAndLerp($$8, $$9, $$10, $$11, $$12 - $$17, $$13, $$12);
    }

    public double a(double $$0, double $$1, double $$2, double[] $$3) {
        double $$4 = $$0 + this.xo;
        double $$5 = $$1 + this.yo;
        double $$6 = $$2 + this.zo;
        int $$7 = Mth.floor($$4);
        int $$8 = Mth.floor($$5);
        int $$9 = Mth.floor($$6);
        double $$10 = $$4 - (double)$$7;
        double $$11 = $$5 - (double)$$8;
        double $$12 = $$6 - (double)$$9;
        return this.a($$7, $$8, $$9, $$10, $$11, $$12, $$3);
    }

    private static double gradDot(int $$0, double $$1, double $$2, double $$3) {
        return SimplexNoise.a(SimplexNoise.GRADIENT[$$0 & 0xF], $$1, $$2, $$3);
    }

    private int p(int $$0) {
        return this.p[$$0 & 0xFF] & 0xFF;
    }

    private double sampleAndLerp(int $$0, int $$1, int $$2, double $$3, double $$4, double $$5, double $$6) {
        int $$7 = this.p($$0);
        int $$8 = this.p($$0 + 1);
        int $$9 = this.p($$7 + $$1);
        int $$10 = this.p($$7 + $$1 + 1);
        int $$11 = this.p($$8 + $$1);
        int $$12 = this.p($$8 + $$1 + 1);
        double $$13 = ImprovedNoise.gradDot(this.p($$9 + $$2), $$3, $$4, $$5);
        double $$14 = ImprovedNoise.gradDot(this.p($$11 + $$2), $$3 - 1.0, $$4, $$5);
        double $$15 = ImprovedNoise.gradDot(this.p($$10 + $$2), $$3, $$4 - 1.0, $$5);
        double $$16 = ImprovedNoise.gradDot(this.p($$12 + $$2), $$3 - 1.0, $$4 - 1.0, $$5);
        double $$17 = ImprovedNoise.gradDot(this.p($$9 + $$2 + 1), $$3, $$4, $$5 - 1.0);
        double $$18 = ImprovedNoise.gradDot(this.p($$11 + $$2 + 1), $$3 - 1.0, $$4, $$5 - 1.0);
        double $$19 = ImprovedNoise.gradDot(this.p($$10 + $$2 + 1), $$3, $$4 - 1.0, $$5 - 1.0);
        double $$20 = ImprovedNoise.gradDot(this.p($$12 + $$2 + 1), $$3 - 1.0, $$4 - 1.0, $$5 - 1.0);
        double $$21 = Mth.smoothstep($$3);
        double $$22 = Mth.smoothstep($$6);
        double $$23 = Mth.smoothstep($$5);
        return Mth.lerp3($$21, $$22, $$23, $$13, $$14, $$15, $$16, $$17, $$18, $$19, $$20);
    }

    private double a(int $$0, int $$1, int $$2, double $$3, double $$4, double $$5, double[] $$6) {
        int $$7 = this.p($$0);
        int $$8 = this.p($$0 + 1);
        int $$9 = this.p($$7 + $$1);
        int $$10 = this.p($$7 + $$1 + 1);
        int $$11 = this.p($$8 + $$1);
        int $$12 = this.p($$8 + $$1 + 1);
        int $$13 = this.p($$9 + $$2);
        int $$14 = this.p($$11 + $$2);
        int $$15 = this.p($$10 + $$2);
        int $$16 = this.p($$12 + $$2);
        int $$17 = this.p($$9 + $$2 + 1);
        int $$18 = this.p($$11 + $$2 + 1);
        int $$19 = this.p($$10 + $$2 + 1);
        int $$20 = this.p($$12 + $$2 + 1);
        int[] $$21 = SimplexNoise.GRADIENT[$$13 & 0xF];
        int[] $$22 = SimplexNoise.GRADIENT[$$14 & 0xF];
        int[] $$23 = SimplexNoise.GRADIENT[$$15 & 0xF];
        int[] $$24 = SimplexNoise.GRADIENT[$$16 & 0xF];
        int[] $$25 = SimplexNoise.GRADIENT[$$17 & 0xF];
        int[] $$26 = SimplexNoise.GRADIENT[$$18 & 0xF];
        int[] $$27 = SimplexNoise.GRADIENT[$$19 & 0xF];
        int[] $$28 = SimplexNoise.GRADIENT[$$20 & 0xF];
        double $$29 = SimplexNoise.a($$21, $$3, $$4, $$5);
        double $$30 = SimplexNoise.a($$22, $$3 - 1.0, $$4, $$5);
        double $$31 = SimplexNoise.a($$23, $$3, $$4 - 1.0, $$5);
        double $$32 = SimplexNoise.a($$24, $$3 - 1.0, $$4 - 1.0, $$5);
        double $$33 = SimplexNoise.a($$25, $$3, $$4, $$5 - 1.0);
        double $$34 = SimplexNoise.a($$26, $$3 - 1.0, $$4, $$5 - 1.0);
        double $$35 = SimplexNoise.a($$27, $$3, $$4 - 1.0, $$5 - 1.0);
        double $$36 = SimplexNoise.a($$28, $$3 - 1.0, $$4 - 1.0, $$5 - 1.0);
        double $$37 = Mth.smoothstep($$3);
        double $$38 = Mth.smoothstep($$4);
        double $$39 = Mth.smoothstep($$5);
        double $$40 = Mth.lerp3($$37, $$38, $$39, $$21[0], $$22[0], $$23[0], $$24[0], $$25[0], $$26[0], $$27[0], $$28[0]);
        double $$41 = Mth.lerp3($$37, $$38, $$39, $$21[1], $$22[1], $$23[1], $$24[1], $$25[1], $$26[1], $$27[1], $$28[1]);
        double $$42 = Mth.lerp3($$37, $$38, $$39, $$21[2], $$22[2], $$23[2], $$24[2], $$25[2], $$26[2], $$27[2], $$28[2]);
        double $$43 = Mth.lerp2($$38, $$39, $$30 - $$29, $$32 - $$31, $$34 - $$33, $$36 - $$35);
        double $$44 = Mth.lerp2($$39, $$37, $$31 - $$29, $$35 - $$33, $$32 - $$30, $$36 - $$34);
        double $$45 = Mth.lerp2($$37, $$38, $$33 - $$29, $$34 - $$30, $$35 - $$31, $$36 - $$32);
        double $$46 = Mth.smoothstepDerivative($$3);
        double $$47 = Mth.smoothstepDerivative($$4);
        double $$48 = Mth.smoothstepDerivative($$5);
        double $$49 = $$40 + $$46 * $$43;
        double $$50 = $$41 + $$47 * $$44;
        double $$51 = $$42 + $$48 * $$45;
        $$6[0] = $$6[0] + $$49;
        $$6[1] = $$6[1] + $$50;
        $$6[2] = $$6[2] + $$51;
        return Mth.lerp3($$37, $$38, $$39, $$29, $$30, $$31, $$32, $$33, $$34, $$35, $$36);
    }

    @VisibleForTesting
    public void parityConfigString(StringBuilder $$0) {
        NoiseUtils.a($$0, this.xo, this.yo, this.zo, this.p);
    }
}

