/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.core;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IdMap;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public interface Registry<T>
extends Keyable,
HolderLookup.RegistryLookup<T>,
IdMap<T> {
    @Override
    public ResourceKey<? extends Registry<T>> key();

    default public Codec<T> byNameCodec() {
        return this.referenceHolderWithLifecycle().flatComapMap(Holder.Reference::value, $$0 -> this.safeCastToReference(this.wrapAsHolder($$0)));
    }

    default public Codec<Holder<T>> holderByNameCodec() {
        return this.referenceHolderWithLifecycle().flatComapMap($$0 -> $$0, this::safeCastToReference);
    }

    private Codec<Holder.Reference<T>> referenceHolderWithLifecycle() {
        Codec $$02 = ResourceLocation.CODEC.comapFlatMap($$0 -> this.get((ResourceLocation)$$0).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown registry key in " + String.valueOf(this.key()) + ": " + String.valueOf($$0))), $$0 -> $$0.key().location());
        return ExtraCodecs.overrideLifecycle($$02, $$0 -> this.registrationInfo($$0.key()).map(RegistrationInfo::lifecycle).orElse(Lifecycle.experimental()));
    }

    private DataResult<Holder.Reference<T>> safeCastToReference(Holder<T> $$0) {
        DataResult dataResult;
        if ($$0 instanceof Holder.Reference) {
            Holder.Reference $$1 = (Holder.Reference)$$0;
            dataResult = DataResult.success((Object)$$1);
        } else {
            dataResult = DataResult.error(() -> "Unregistered holder in " + String.valueOf(this.key()) + ": " + String.valueOf($$0));
        }
        return dataResult;
    }

    default public <U> Stream<U> keys(DynamicOps<U> $$0) {
        return this.keySet().stream().map($$1 -> $$0.createString($$1.toString()));
    }

    @Nullable
    public ResourceLocation getKey(T var1);

    public Optional<ResourceKey<T>> getResourceKey(T var1);

    @Override
    public int getId(@Nullable T var1);

    @Nullable
    public T getValue(@Nullable ResourceKey<T> var1);

    @Nullable
    public T getValue(@Nullable ResourceLocation var1);

    public Optional<RegistrationInfo> registrationInfo(ResourceKey<T> var1);

    default public Optional<T> getOptional(@Nullable ResourceLocation $$0) {
        return Optional.ofNullable(this.getValue($$0));
    }

    default public Optional<T> getOptional(@Nullable ResourceKey<T> $$0) {
        return Optional.ofNullable(this.getValue($$0));
    }

    public Optional<Holder.Reference<T>> getAny();

    default public T getValueOrThrow(ResourceKey<T> $$0) {
        T $$1 = this.getValue($$0);
        if ($$1 == null) {
            throw new IllegalStateException("Missing key in " + String.valueOf(this.key()) + ": " + String.valueOf($$0));
        }
        return $$1;
    }

    public Set<ResourceLocation> keySet();

    public Set<Map.Entry<ResourceKey<T>, T>> entrySet();

    public Set<ResourceKey<T>> registryKeySet();

    public Optional<Holder.Reference<T>> getRandom(RandomSource var1);

    default public Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public boolean containsKey(ResourceLocation var1);

    public boolean containsKey(ResourceKey<T> var1);

    public static <T> T register(Registry<? super T> $$0, String $$1, T $$2) {
        return Registry.register($$0, ResourceLocation.parse($$1), $$2);
    }

    public static <V, T extends V> T register(Registry<V> $$0, ResourceLocation $$1, T $$2) {
        return Registry.register($$0, ResourceKey.create($$0.key(), $$1), $$2);
    }

    public static <V, T extends V> T register(Registry<V> $$0, ResourceKey<V> $$1, T $$2) {
        ((WritableRegistry)$$0).register($$1, $$2, RegistrationInfo.BUILT_IN);
        return $$2;
    }

    public static <T> Holder.Reference<T> registerForHolder(Registry<T> $$0, ResourceKey<T> $$1, T $$2) {
        return ((WritableRegistry)$$0).register($$1, $$2, RegistrationInfo.BUILT_IN);
    }

    public static <T> Holder.Reference<T> registerForHolder(Registry<T> $$0, ResourceLocation $$1, T $$2) {
        return Registry.registerForHolder($$0, ResourceKey.create($$0.key(), $$1), $$2);
    }

    public Registry<T> freeze();

    public Holder.Reference<T> createIntrusiveHolder(T var1);

    public Optional<Holder.Reference<T>> get(int var1);

    public Optional<Holder.Reference<T>> get(ResourceLocation var1);

    public Holder<T> wrapAsHolder(T var1);

    default public Iterable<Holder<T>> getTagOrEmpty(TagKey<T> $$0) {
        return (Iterable)DataFixUtils.orElse((Optional)this.get($$0), (Object)List.of());
    }

    default public Optional<Holder<T>> getRandomElementOf(TagKey<T> $$0, RandomSource $$12) {
        return this.get($$0).flatMap($$1 -> $$1.getRandomElement($$12));
    }

    public Stream<HolderSet.Named<T>> getTags();

    default public IdMap<Holder<T>> asHolderIdMap() {
        return new IdMap<Holder<T>>(){

            @Override
            public int getId(Holder<T> $$0) {
                return Registry.this.getId($$0.value());
            }

            @Override
            @Nullable
            public Holder<T> byId(int $$0) {
                return Registry.this.get($$0).orElse(null);
            }

            @Override
            public int size() {
                return Registry.this.size();
            }

            @Override
            public Iterator<Holder<T>> iterator() {
                return Registry.this.listElements().map($$0 -> $$0).iterator();
            }

            @Override
            @Nullable
            public /* synthetic */ Object byId(int n) {
                return this.byId(n);
            }
        };
    }

    public PendingTags<T> prepareTagReload(TagLoader.LoadResult<T> var1);

    public static interface PendingTags<T> {
        public ResourceKey<? extends Registry<? extends T>> key();

        public HolderLookup.RegistryLookup<T> lookup();

        public void apply();

        public int size();
    }
}

