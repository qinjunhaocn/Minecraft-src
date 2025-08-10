/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import org.slf4j.Logger;

public class BiasedToBottomHeight
extends HeightProvider {
    public static final MapCodec<BiasedToBottomHeight> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter($$0 -> $$0.minInclusive), (App)VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter($$0 -> $$0.maxInclusive), (App)Codec.intRange((int)1, (int)Integer.MAX_VALUE).optionalFieldOf("inner", (Object)1).forGetter($$0 -> $$0.inner)).apply((Applicative)$$02, BiasedToBottomHeight::new));
    private static final Logger LOGGER = LogUtils.getLogger();
    private final VerticalAnchor minInclusive;
    private final VerticalAnchor maxInclusive;
    private final int inner;

    private BiasedToBottomHeight(VerticalAnchor $$0, VerticalAnchor $$1, int $$2) {
        this.minInclusive = $$0;
        this.maxInclusive = $$1;
        this.inner = $$2;
    }

    public static BiasedToBottomHeight of(VerticalAnchor $$0, VerticalAnchor $$1, int $$2) {
        return new BiasedToBottomHeight($$0, $$1, $$2);
    }

    @Override
    public int sample(RandomSource $$0, WorldGenerationContext $$1) {
        int $$2 = this.minInclusive.resolveY($$1);
        int $$3 = this.maxInclusive.resolveY($$1);
        if ($$3 - $$2 - this.inner + 1 <= 0) {
            LOGGER.warn("Empty height range: {}", (Object)this);
            return $$2;
        }
        int $$4 = $$0.nextInt($$3 - $$2 - this.inner + 1);
        return $$0.nextInt($$4 + this.inner) + $$2;
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.BIASED_TO_BOTTOM;
    }

    public String toString() {
        return "biased[" + String.valueOf(this.minInclusive) + "-" + String.valueOf(this.maxInclusive) + " inner: " + this.inner + "]";
    }
}

