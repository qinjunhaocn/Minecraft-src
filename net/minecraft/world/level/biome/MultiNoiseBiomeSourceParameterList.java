/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;

public class MultiNoiseBiomeSourceParameterList {
    public static final Codec<MultiNoiseBiomeSourceParameterList> DIRECT_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Preset.CODEC.fieldOf("preset").forGetter($$0 -> $$0.preset), RegistryOps.retrieveGetter(Registries.BIOME)).apply((Applicative)$$02, MultiNoiseBiomeSourceParameterList::new));
    public static final Codec<Holder<MultiNoiseBiomeSourceParameterList>> CODEC = RegistryFileCodec.create(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, DIRECT_CODEC);
    private final Preset preset;
    private final Climate.ParameterList<Holder<Biome>> parameters;

    public MultiNoiseBiomeSourceParameterList(Preset $$0, HolderGetter<Biome> $$1) {
        this.preset = $$0;
        this.parameters = $$0.provider.apply($$1::getOrThrow);
    }

    public Climate.ParameterList<Holder<Biome>> parameters() {
        return this.parameters;
    }

    public static Map<Preset, Climate.ParameterList<ResourceKey<Biome>>> knownPresets() {
        return Preset.BY_NAME.values().stream().collect(Collectors.toMap($$0 -> $$0, $$02 -> $$02.provider().apply($$0 -> $$0)));
    }

    public static final class Preset
    extends Record {
        private final ResourceLocation id;
        final SourceProvider provider;
        public static final Preset NETHER = new Preset(ResourceLocation.withDefaultNamespace("nether"), new SourceProvider(){

            @Override
            public <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> $$0) {
                return new Climate.ParameterList(List.of((Object)Pair.of((Object)((Object)Climate.parameters(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)), $$0.apply(Biomes.NETHER_WASTES)), (Object)Pair.of((Object)((Object)Climate.parameters(0.0f, -0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)), $$0.apply(Biomes.SOUL_SAND_VALLEY)), (Object)Pair.of((Object)((Object)Climate.parameters(0.4f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)), $$0.apply(Biomes.CRIMSON_FOREST)), (Object)Pair.of((Object)((Object)Climate.parameters(0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.375f)), $$0.apply(Biomes.WARPED_FOREST)), (Object)Pair.of((Object)((Object)Climate.parameters(-0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.175f)), $$0.apply(Biomes.BASALT_DELTAS))));
            }
        });
        public static final Preset OVERWORLD = new Preset(ResourceLocation.withDefaultNamespace("overworld"), new SourceProvider(){

            @Override
            public <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> $$0) {
                return Preset.generateOverworldBiomes($$0);
            }
        });
        static final Map<ResourceLocation, Preset> BY_NAME = Stream.of(NETHER, OVERWORLD).collect(Collectors.toMap(Preset::id, $$0 -> $$0));
        public static final Codec<Preset> CODEC = ResourceLocation.CODEC.flatXmap($$0 -> Optional.ofNullable(BY_NAME.get($$0)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown preset: " + String.valueOf($$0))), $$0 -> DataResult.success((Object)$$0.id));

        public Preset(ResourceLocation $$0, SourceProvider $$1) {
            this.id = $$0;
            this.provider = $$1;
        }

        static <T> Climate.ParameterList<T> generateOverworldBiomes(Function<ResourceKey<Biome>, T> $$0) {
            ImmutableList.Builder $$1 = ImmutableList.builder();
            new OverworldBiomeBuilder().addBiomes($$2 -> $$1.add($$2.mapSecond($$0)));
            return new Climate.ParameterList($$1.build());
        }

        public Stream<ResourceKey<Biome>> usedBiomes() {
            return this.provider.apply($$0 -> $$0).values().stream().map(Pair::getSecond).distinct();
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Preset.class, "id;provider", "id", "provider"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Preset.class, "id;provider", "id", "provider"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Preset.class, "id;provider", "id", "provider"}, this, $$0);
        }

        public ResourceLocation id() {
            return this.id;
        }

        public SourceProvider provider() {
            return this.provider;
        }

        @FunctionalInterface
        static interface SourceProvider {
            public <T> Climate.ParameterList<T> apply(Function<ResourceKey<Biome>, T> var1);
        }
    }
}

