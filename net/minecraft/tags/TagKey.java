/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.tags;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record TagKey<T>(ResourceKey<? extends Registry<T>> registry, ResourceLocation location) {
    private static final Interner<TagKey<?>> VALUES = Interners.newWeakInterner();

    public static <T> Codec<TagKey<T>> codec(ResourceKey<? extends Registry<T>> $$0) {
        return ResourceLocation.CODEC.xmap($$1 -> TagKey.create($$0, $$1), TagKey::location);
    }

    public static <T> Codec<TagKey<T>> hashedCodec(ResourceKey<? extends Registry<T>> $$02) {
        return Codec.STRING.comapFlatMap($$12 -> $$12.startsWith("#") ? ResourceLocation.read($$12.substring(1)).map($$1 -> TagKey.create($$02, $$1)) : DataResult.error(() -> "Not a tag id"), $$0 -> "#" + String.valueOf($$0.location));
    }

    public static <T> StreamCodec<ByteBuf, TagKey<T>> streamCodec(ResourceKey<? extends Registry<T>> $$0) {
        return ResourceLocation.STREAM_CODEC.map($$1 -> TagKey.create($$0, $$1), TagKey::location);
    }

    public static <T> TagKey<T> create(ResourceKey<? extends Registry<T>> $$0, ResourceLocation $$1) {
        return VALUES.intern(new TagKey<T>($$0, $$1));
    }

    public boolean isFor(ResourceKey<? extends Registry<?>> $$0) {
        return this.registry == $$0;
    }

    public <E> Optional<TagKey<E>> cast(ResourceKey<? extends Registry<E>> $$0) {
        return this.isFor($$0) ? Optional.of(this) : Optional.empty();
    }

    public String toString() {
        return "TagKey[" + String.valueOf(this.registry.location()) + " / " + String.valueOf(this.location) + "]";
    }
}

