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
package net.minecraft.client.resources.metadata.animation;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.ExtraCodecs;

public record AnimationFrame(int index, Optional<Integer> time) {
    public static final Codec<AnimationFrame> FULL_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("index").forGetter(AnimationFrame::index), (App)ExtraCodecs.POSITIVE_INT.optionalFieldOf("time").forGetter(AnimationFrame::time)).apply((Applicative)$$0, AnimationFrame::new));
    public static final Codec<AnimationFrame> CODEC = Codec.either(ExtraCodecs.NON_NEGATIVE_INT, FULL_CODEC).xmap($$02 -> (AnimationFrame)((Object)((Object)$$02.map(AnimationFrame::new, $$0 -> $$0))), $$0 -> $$0.time.isPresent() ? Either.right((Object)$$0) : Either.left((Object)$$0.index));

    public AnimationFrame(int $$0) {
        this($$0, Optional.empty());
    }

    public int timeOr(int $$0) {
        return this.time.orElse($$0);
    }
}

