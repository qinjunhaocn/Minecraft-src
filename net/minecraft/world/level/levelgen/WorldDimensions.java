/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.storage.PrimaryLevelData;

public record WorldDimensions(Map<ResourceKey<LevelStem>, LevelStem> dimensions) {
    public static final MapCodec<WorldDimensions> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.unboundedMap(ResourceKey.codec(Registries.LEVEL_STEM), LevelStem.CODEC).fieldOf("dimensions").forGetter(WorldDimensions::dimensions)).apply((Applicative)$$0, $$0.stable(WorldDimensions::new)));
    private static final Set<ResourceKey<LevelStem>> BUILTIN_ORDER = ImmutableSet.of(LevelStem.OVERWORLD, LevelStem.NETHER, LevelStem.END);
    private static final int VANILLA_DIMENSION_COUNT = BUILTIN_ORDER.size();

    public WorldDimensions {
        LevelStem $$1 = $$0.get(LevelStem.OVERWORLD);
        if ($$1 == null) {
            throw new IllegalStateException("Overworld settings missing");
        }
    }

    public WorldDimensions(Registry<LevelStem> $$0) {
        this($$0.listElements().collect(Collectors.toMap(Holder.Reference::key, Holder.Reference::value)));
    }

    public static Stream<ResourceKey<LevelStem>> keysInOrder(Stream<ResourceKey<LevelStem>> $$02) {
        return Stream.concat(BUILTIN_ORDER.stream(), $$02.filter($$0 -> !BUILTIN_ORDER.contains($$0)));
    }

    public WorldDimensions replaceOverworldGenerator(HolderLookup.Provider $$0, ChunkGenerator $$1) {
        HolderGetter $$2 = $$0.lookupOrThrow(Registries.DIMENSION_TYPE);
        Map<ResourceKey<LevelStem>, LevelStem> $$3 = WorldDimensions.withOverworld((HolderLookup<DimensionType>)$$2, this.dimensions, $$1);
        return new WorldDimensions($$3);
    }

    public static Map<ResourceKey<LevelStem>, LevelStem> withOverworld(HolderLookup<DimensionType> $$0, Map<ResourceKey<LevelStem>, LevelStem> $$1, ChunkGenerator $$2) {
        LevelStem $$3 = $$1.get(LevelStem.OVERWORLD);
        Holder<DimensionType> $$4 = $$3 == null ? $$0.getOrThrow(BuiltinDimensionTypes.OVERWORLD) : $$3.type();
        return WorldDimensions.withOverworld($$1, $$4, $$2);
    }

    public static Map<ResourceKey<LevelStem>, LevelStem> withOverworld(Map<ResourceKey<LevelStem>, LevelStem> $$0, Holder<DimensionType> $$1, ChunkGenerator $$2) {
        ImmutableMap.Builder $$3 = ImmutableMap.builder();
        $$3.putAll($$0);
        $$3.put(LevelStem.OVERWORLD, new LevelStem($$1, $$2));
        return $$3.buildKeepingLast();
    }

    public ChunkGenerator overworld() {
        LevelStem $$0 = this.dimensions.get(LevelStem.OVERWORLD);
        if ($$0 == null) {
            throw new IllegalStateException("Overworld settings missing");
        }
        return $$0.generator();
    }

    public Optional<LevelStem> get(ResourceKey<LevelStem> $$0) {
        return Optional.ofNullable(this.dimensions.get($$0));
    }

    public ImmutableSet<ResourceKey<Level>> levels() {
        return this.dimensions().keySet().stream().map(Registries::levelStemToLevel).collect(ImmutableSet.toImmutableSet());
    }

    public boolean isDebug() {
        return this.overworld() instanceof DebugLevelSource;
    }

    private static PrimaryLevelData.SpecialWorldProperty specialWorldProperty(Registry<LevelStem> $$02) {
        return $$02.getOptional(LevelStem.OVERWORLD).map($$0 -> {
            ChunkGenerator $$1 = $$0.generator();
            if ($$1 instanceof DebugLevelSource) {
                return PrimaryLevelData.SpecialWorldProperty.DEBUG;
            }
            if ($$1 instanceof FlatLevelSource) {
                return PrimaryLevelData.SpecialWorldProperty.FLAT;
            }
            return PrimaryLevelData.SpecialWorldProperty.NONE;
        }).orElse(PrimaryLevelData.SpecialWorldProperty.NONE);
    }

    static Lifecycle checkStability(ResourceKey<LevelStem> $$0, LevelStem $$1) {
        return WorldDimensions.isVanillaLike($$0, $$1) ? Lifecycle.stable() : Lifecycle.experimental();
    }

    private static boolean isVanillaLike(ResourceKey<LevelStem> $$0, LevelStem $$1) {
        if ($$0 == LevelStem.OVERWORLD) {
            return WorldDimensions.isStableOverworld($$1);
        }
        if ($$0 == LevelStem.NETHER) {
            return WorldDimensions.isStableNether($$1);
        }
        if ($$0 == LevelStem.END) {
            return WorldDimensions.isStableEnd($$1);
        }
        return false;
    }

    private static boolean isStableOverworld(LevelStem $$0) {
        MultiNoiseBiomeSource $$2;
        Holder<DimensionType> $$1 = $$0.type();
        if (!$$1.is(BuiltinDimensionTypes.OVERWORLD) && !$$1.is(BuiltinDimensionTypes.OVERWORLD_CAVES)) {
            return false;
        }
        BiomeSource biomeSource = $$0.generator().getBiomeSource();
        return !(biomeSource instanceof MultiNoiseBiomeSource) || ($$2 = (MultiNoiseBiomeSource)biomeSource).stable(MultiNoiseBiomeSourceParameterLists.OVERWORLD);
    }

    private static boolean isStableNether(LevelStem $$0) {
        MultiNoiseBiomeSource $$2;
        NoiseBasedChunkGenerator $$1;
        Object object;
        return $$0.type().is(BuiltinDimensionTypes.NETHER) && (object = $$0.generator()) instanceof NoiseBasedChunkGenerator && ($$1 = (NoiseBasedChunkGenerator)object).stable(NoiseGeneratorSettings.NETHER) && (object = $$1.getBiomeSource()) instanceof MultiNoiseBiomeSource && ($$2 = (MultiNoiseBiomeSource)object).stable(MultiNoiseBiomeSourceParameterLists.NETHER);
    }

    private static boolean isStableEnd(LevelStem $$0) {
        NoiseBasedChunkGenerator $$1;
        ChunkGenerator chunkGenerator;
        return $$0.type().is(BuiltinDimensionTypes.END) && (chunkGenerator = $$0.generator()) instanceof NoiseBasedChunkGenerator && ($$1 = (NoiseBasedChunkGenerator)chunkGenerator).stable(NoiseGeneratorSettings.END) && $$1.getBiomeSource() instanceof TheEndBiomeSource;
    }

    public Complete bake(Registry<LevelStem> $$0) {
        final class Entry
        extends Record {
            final ResourceKey<LevelStem> key;
            final LevelStem value;

            Entry(ResourceKey<LevelStem> $$0, LevelStem $$1) {
                this.key = $$0;
                this.value = $$1;
            }

            RegistrationInfo registrationInfo() {
                return new RegistrationInfo(Optional.empty(), WorldDimensions.checkStability(this.key, this.value));
            }

            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "key;value", "key", "value"}, this);
            }

            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "key;value", "key", "value"}, this);
            }

            public final boolean equals(Object $$0) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "key;value", "key", "value"}, this, $$0);
            }

            public ResourceKey<LevelStem> key() {
                return this.key;
            }

            public LevelStem value() {
                return this.value;
            }
        }
        Stream<ResourceKey<LevelStem>> $$12 = Stream.concat($$0.registryKeySet().stream(), this.dimensions.keySet().stream()).distinct();
        ArrayList $$2 = new ArrayList();
        WorldDimensions.keysInOrder($$12).forEach($$22 -> $$0.getOptional((ResourceKey<LevelStem>)$$22).or(() -> Optional.ofNullable(this.dimensions.get($$22))).ifPresent($$2 -> $$2.add(new Entry((ResourceKey<LevelStem>)$$22, (LevelStem)((Object)((Object)$$2))))));
        Lifecycle $$3 = $$2.size() == VANILLA_DIMENSION_COUNT ? Lifecycle.stable() : Lifecycle.experimental();
        MappedRegistry<LevelStem> $$4 = new MappedRegistry<LevelStem>(Registries.LEVEL_STEM, $$3);
        $$2.forEach($$1 -> $$4.register($$1.key, $$1.value, $$1.registrationInfo()));
        Registry<LevelStem> $$5 = $$4.freeze();
        PrimaryLevelData.SpecialWorldProperty $$6 = WorldDimensions.specialWorldProperty($$5);
        return new Complete($$5.freeze(), $$6);
    }

    public record Complete(Registry<LevelStem> dimensions, PrimaryLevelData.SpecialWorldProperty specialWorldProperty) {
        public Lifecycle lifecycle() {
            return this.dimensions.registryLifecycle();
        }

        public RegistryAccess.Frozen dimensionsRegistryAccess() {
            return new RegistryAccess.ImmutableRegistryAccess(List.of(this.dimensions)).freeze();
        }
    }
}

