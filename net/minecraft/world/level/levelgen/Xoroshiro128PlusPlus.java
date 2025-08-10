/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.stream.LongStream;
import net.minecraft.Util;
import net.minecraft.world.level.levelgen.RandomSupport;

public class Xoroshiro128PlusPlus {
    private long seedLo;
    private long seedHi;
    public static final Codec<Xoroshiro128PlusPlus> CODEC = Codec.LONG_STREAM.comapFlatMap($$02 -> Util.fixedSize($$02, 2).map($$0 -> new Xoroshiro128PlusPlus($$0[0], $$0[1])), $$0 -> LongStream.of($$0.seedLo, $$0.seedHi));

    public Xoroshiro128PlusPlus(RandomSupport.Seed128bit $$0) {
        this($$0.seedLo(), $$0.seedHi());
    }

    public Xoroshiro128PlusPlus(long $$0, long $$1) {
        this.seedLo = $$0;
        this.seedHi = $$1;
        if ((this.seedLo | this.seedHi) == 0L) {
            this.seedLo = -7046029254386353131L;
            this.seedHi = 7640891576956012809L;
        }
    }

    public long nextLong() {
        long $$0 = this.seedLo;
        long $$1 = this.seedHi;
        long $$2 = Long.rotateLeft($$0 + $$1, 17) + $$0;
        this.seedLo = Long.rotateLeft($$0, 49) ^ ($$1 ^= $$0) ^ $$1 << 21;
        this.seedHi = Long.rotateLeft($$1, 28);
        return $$2;
    }
}

