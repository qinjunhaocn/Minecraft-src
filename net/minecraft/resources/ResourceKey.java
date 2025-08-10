/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.resources;

import com.google.common.collect.MapMaker;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class ResourceKey<T> {
    private static final ConcurrentMap<InternKey, ResourceKey<?>> VALUES = new MapMaker().weakValues().makeMap();
    private final ResourceLocation registryName;
    private final ResourceLocation location;

    public static <T> Codec<ResourceKey<T>> codec(ResourceKey<? extends Registry<T>> $$0) {
        return ResourceLocation.CODEC.xmap($$1 -> ResourceKey.create($$0, $$1), ResourceKey::location);
    }

    public static <T> StreamCodec<ByteBuf, ResourceKey<T>> streamCodec(ResourceKey<? extends Registry<T>> $$0) {
        return ResourceLocation.STREAM_CODEC.map($$1 -> ResourceKey.create($$0, $$1), ResourceKey::location);
    }

    public static <T> ResourceKey<T> create(ResourceKey<? extends Registry<T>> $$0, ResourceLocation $$1) {
        return ResourceKey.create($$0.location, $$1);
    }

    public static <T> ResourceKey<Registry<T>> createRegistryKey(ResourceLocation $$0) {
        return ResourceKey.create(Registries.ROOT_REGISTRY_NAME, $$0);
    }

    private static <T> ResourceKey<T> create(ResourceLocation $$02, ResourceLocation $$1) {
        return VALUES.computeIfAbsent(new InternKey($$02, $$1), $$0 -> new ResourceKey($$0.registry, $$0.location));
    }

    private ResourceKey(ResourceLocation $$0, ResourceLocation $$1) {
        this.registryName = $$0;
        this.location = $$1;
    }

    public String toString() {
        return "ResourceKey[" + String.valueOf(this.registryName) + " / " + String.valueOf(this.location) + "]";
    }

    public boolean isFor(ResourceKey<? extends Registry<?>> $$0) {
        return this.registryName.equals($$0.location());
    }

    public <E> Optional<ResourceKey<E>> cast(ResourceKey<? extends Registry<E>> $$0) {
        return this.isFor($$0) ? Optional.of(this) : Optional.empty();
    }

    public ResourceLocation location() {
        return this.location;
    }

    public ResourceLocation registry() {
        return this.registryName;
    }

    public ResourceKey<Registry<T>> registryKey() {
        return ResourceKey.createRegistryKey(this.registryName);
    }

    static final class InternKey
    extends Record {
        final ResourceLocation registry;
        final ResourceLocation location;

        InternKey(ResourceLocation $$0, ResourceLocation $$1) {
            this.registry = $$0;
            this.location = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{InternKey.class, "registry;location", "registry", "location"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{InternKey.class, "registry;location", "registry", "location"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{InternKey.class, "registry;location", "registry", "location"}, this, $$0);
        }

        public ResourceLocation registry() {
            return this.registry;
        }

        public ResourceLocation location() {
            return this.location;
        }
    }
}

