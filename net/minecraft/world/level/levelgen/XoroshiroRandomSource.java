/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.MarsagliaPolarGaussian;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.Xoroshiro128PlusPlus;

public class XoroshiroRandomSource
implements RandomSource {
    private static final float FLOAT_UNIT = 5.9604645E-8f;
    private static final double DOUBLE_UNIT = (double)1.110223E-16f;
    public static final Codec<XoroshiroRandomSource> CODEC = Xoroshiro128PlusPlus.CODEC.xmap($$0 -> new XoroshiroRandomSource((Xoroshiro128PlusPlus)$$0), $$0 -> $$0.randomNumberGenerator);
    private Xoroshiro128PlusPlus randomNumberGenerator;
    private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

    public XoroshiroRandomSource(long $$0) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus(RandomSupport.upgradeSeedTo128bit($$0));
    }

    public XoroshiroRandomSource(RandomSupport.Seed128bit $$0) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus($$0);
    }

    public XoroshiroRandomSource(long $$0, long $$1) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus($$0, $$1);
    }

    private XoroshiroRandomSource(Xoroshiro128PlusPlus $$0) {
        this.randomNumberGenerator = $$0;
    }

    @Override
    public RandomSource fork() {
        return new XoroshiroRandomSource(this.randomNumberGenerator.nextLong(), this.randomNumberGenerator.nextLong());
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return new XoroshiroPositionalRandomFactory(this.randomNumberGenerator.nextLong(), this.randomNumberGenerator.nextLong());
    }

    @Override
    public void setSeed(long $$0) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus(RandomSupport.upgradeSeedTo128bit($$0));
        this.gaussianSource.reset();
    }

    @Override
    public int nextInt() {
        return (int)this.randomNumberGenerator.nextLong();
    }

    @Override
    public int nextInt(int $$0) {
        if ($$0 <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        }
        long $$1 = Integer.toUnsignedLong(this.nextInt());
        long $$2 = $$1 * (long)$$0;
        long $$3 = $$2 & 0xFFFFFFFFL;
        if ($$3 < (long)$$0) {
            int $$4 = Integer.remainderUnsigned(~$$0 + 1, $$0);
            while ($$3 < (long)$$4) {
                $$1 = Integer.toUnsignedLong(this.nextInt());
                $$2 = $$1 * (long)$$0;
                $$3 = $$2 & 0xFFFFFFFFL;
            }
        }
        long $$5 = $$2 >> 32;
        return (int)$$5;
    }

    @Override
    public long nextLong() {
        return this.randomNumberGenerator.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return (this.randomNumberGenerator.nextLong() & 1L) != 0L;
    }

    @Override
    public float nextFloat() {
        return (float)this.nextBits(24) * 5.9604645E-8f;
    }

    @Override
    public double nextDouble() {
        return (double)this.nextBits(53) * (double)1.110223E-16f;
    }

    @Override
    public double nextGaussian() {
        return this.gaussianSource.nextGaussian();
    }

    @Override
    public void consumeCount(int $$0) {
        for (int $$1 = 0; $$1 < $$0; ++$$1) {
            this.randomNumberGenerator.nextLong();
        }
    }

    private long nextBits(int $$0) {
        return this.randomNumberGenerator.nextLong() >>> 64 - $$0;
    }

    public static class XoroshiroPositionalRandomFactory
    implements PositionalRandomFactory {
        private final long seedLo;
        private final long seedHi;

        public XoroshiroPositionalRandomFactory(long $$0, long $$1) {
            this.seedLo = $$0;
            this.seedHi = $$1;
        }

        @Override
        public RandomSource at(int $$0, int $$1, int $$2) {
            long $$3 = Mth.getSeed($$0, $$1, $$2);
            long $$4 = $$3 ^ this.seedLo;
            return new XoroshiroRandomSource($$4, this.seedHi);
        }

        @Override
        public RandomSource fromHashOf(String $$0) {
            RandomSupport.Seed128bit $$1 = RandomSupport.seedFromHashOf($$0);
            return new XoroshiroRandomSource($$1.xor(this.seedLo, this.seedHi));
        }

        @Override
        public RandomSource fromSeed(long $$0) {
            return new XoroshiroRandomSource($$0 ^ this.seedLo, $$0 ^ this.seedHi);
        }

        @Override
        @VisibleForTesting
        public void parityConfigString(StringBuilder $$0) {
            $$0.append("seedLo: ").append(this.seedLo).append(", seedHi: ").append(this.seedHi);
        }
    }
}

