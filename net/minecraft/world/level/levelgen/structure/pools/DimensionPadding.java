/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;

public record DimensionPadding(int bottom, int top) {
    private static final Codec<DimensionPadding> RECORD_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ExtraCodecs.NON_NEGATIVE_INT.lenientOptionalFieldOf("bottom", (Object)0).forGetter($$0 -> $$0.bottom), (App)ExtraCodecs.NON_NEGATIVE_INT.lenientOptionalFieldOf("top", (Object)0).forGetter($$0 -> $$0.top)).apply((Applicative)$$02, DimensionPadding::new));
    public static final Codec<DimensionPadding> CODEC = Codec.either(ExtraCodecs.NON_NEGATIVE_INT, RECORD_CODEC).xmap($$0 -> (DimensionPadding)((Object)((Object)$$0.map(DimensionPadding::new, Function.identity()))), $$0 -> $$0.hasEqualTopAndBottom() ? Either.left((Object)$$0.bottom) : Either.right((Object)$$0));
    public static final DimensionPadding ZERO = new DimensionPadding(0);

    public DimensionPadding(int $$0) {
        this($$0, $$0);
    }

    public boolean hasEqualTopAndBottom() {
        return this.top == this.bottom;
    }
}

