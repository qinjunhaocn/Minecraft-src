/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen;

import java.util.function.LongFunction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class WorldgenRandom
extends LegacyRandomSource {
    private final RandomSource randomSource;
    private int count;

    public WorldgenRandom(RandomSource $$0) {
        super(0L);
        this.randomSource = $$0;
    }

    public int getCount() {
        return this.count;
    }

    @Override
    public RandomSource fork() {
        return this.randomSource.fork();
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return this.randomSource.forkPositional();
    }

    @Override
    public int next(int $$0) {
        ++this.count;
        RandomSource randomSource = this.randomSource;
        if (randomSource instanceof LegacyRandomSource) {
            LegacyRandomSource $$1 = (LegacyRandomSource)randomSource;
            return $$1.next($$0);
        }
        return (int)(this.randomSource.nextLong() >>> 64 - $$0);
    }

    @Override
    public synchronized void setSeed(long $$0) {
        if (this.randomSource == null) {
            return;
        }
        this.randomSource.setSeed($$0);
    }

    public long setDecorationSeed(long $$0, int $$1, int $$2) {
        this.setSeed($$0);
        long $$3 = this.nextLong() | 1L;
        long $$4 = this.nextLong() | 1L;
        long $$5 = (long)$$1 * $$3 + (long)$$2 * $$4 ^ $$0;
        this.setSeed($$5);
        return $$5;
    }

    public void setFeatureSeed(long $$0, int $$1, int $$2) {
        long $$3 = $$0 + (long)$$1 + (long)(10000 * $$2);
        this.setSeed($$3);
    }

    public void setLargeFeatureSeed(long $$0, int $$1, int $$2) {
        this.setSeed($$0);
        long $$3 = this.nextLong();
        long $$4 = this.nextLong();
        long $$5 = (long)$$1 * $$3 ^ (long)$$2 * $$4 ^ $$0;
        this.setSeed($$5);
    }

    public void setLargeFeatureWithSalt(long $$0, int $$1, int $$2, int $$3) {
        long $$4 = (long)$$1 * 341873128712L + (long)$$2 * 132897987541L + $$0 + (long)$$3;
        this.setSeed($$4);
    }

    public static RandomSource seedSlimeChunk(int $$0, int $$1, long $$2, long $$3) {
        return RandomSource.create($$2 + (long)($$0 * $$0 * 4987142) + (long)($$0 * 5947611) + (long)($$1 * $$1) * 4392871L + (long)($$1 * 389711) ^ $$3);
    }

    public static final class Algorithm
    extends Enum<Algorithm> {
        public static final /* enum */ Algorithm LEGACY = new Algorithm(LegacyRandomSource::new);
        public static final /* enum */ Algorithm XOROSHIRO = new Algorithm(XoroshiroRandomSource::new);
        private final LongFunction<RandomSource> constructor;
        private static final /* synthetic */ Algorithm[] $VALUES;

        public static Algorithm[] values() {
            return (Algorithm[])$VALUES.clone();
        }

        public static Algorithm valueOf(String $$0) {
            return Enum.valueOf(Algorithm.class, $$0);
        }

        private Algorithm(LongFunction<RandomSource> $$0) {
            this.constructor = $$0;
        }

        public RandomSource newInstance(long $$0) {
            return this.constructor.apply($$0);
        }

        private static /* synthetic */ Algorithm[] a() {
            return new Algorithm[]{LEGACY, XOROSHIRO};
        }

        static {
            $VALUES = Algorithm.a();
        }
    }
}

