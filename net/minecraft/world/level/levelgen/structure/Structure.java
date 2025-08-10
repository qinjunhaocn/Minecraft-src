/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public abstract class Structure {
    public static final Codec<Structure> DIRECT_CODEC = BuiltInRegistries.STRUCTURE_TYPE.byNameCodec().dispatch(Structure::type, StructureType::codec);
    public static final Codec<Holder<Structure>> CODEC = RegistryFileCodec.create(Registries.STRUCTURE, DIRECT_CODEC);
    protected final StructureSettings settings;

    public static <S extends Structure> RecordCodecBuilder<S, StructureSettings> settingsCodec(RecordCodecBuilder.Instance<S> $$02) {
        return StructureSettings.CODEC.forGetter($$0 -> $$0.settings);
    }

    public static <S extends Structure> MapCodec<S> simpleCodec(Function<StructureSettings, S> $$0) {
        return RecordCodecBuilder.mapCodec($$1 -> $$1.group(Structure.settingsCodec($$1)).apply((Applicative)$$1, $$0));
    }

    protected Structure(StructureSettings $$0) {
        this.settings = $$0;
    }

    public HolderSet<Biome> biomes() {
        return this.settings.biomes;
    }

    public Map<MobCategory, StructureSpawnOverride> spawnOverrides() {
        return this.settings.spawnOverrides;
    }

    public GenerationStep.Decoration step() {
        return this.settings.step;
    }

    public TerrainAdjustment terrainAdaptation() {
        return this.settings.terrainAdaptation;
    }

    public BoundingBox adjustBoundingBox(BoundingBox $$0) {
        if (this.terrainAdaptation() != TerrainAdjustment.NONE) {
            return $$0.inflatedBy(12);
        }
        return $$0;
    }

    public StructureStart generate(Holder<Structure> $$0, ResourceKey<Level> $$1, RegistryAccess $$2, ChunkGenerator $$3, BiomeSource $$4, RandomState $$5, StructureTemplateManager $$6, long $$7, ChunkPos $$8, int $$9, LevelHeightAccessor $$10, Predicate<Holder<Biome>> $$11) {
        StructurePiecesBuilder $$15;
        StructureStart $$16;
        ProfiledDuration $$12 = JvmProfiler.INSTANCE.onStructureGenerate($$8, $$1, $$0);
        GenerationContext $$13 = new GenerationContext($$2, $$3, $$4, $$5, $$6, $$7, $$8, $$10, $$11);
        Optional<GenerationStub> $$14 = this.findValidGenerationPoint($$13);
        if ($$14.isPresent() && ($$16 = new StructureStart(this, $$8, $$9, ($$15 = $$14.get().getPiecesBuilder()).build())).isValid()) {
            if ($$12 != null) {
                $$12.finish(true);
            }
            return $$16;
        }
        if ($$12 != null) {
            $$12.finish(false);
        }
        return StructureStart.INVALID_START;
    }

    protected static Optional<GenerationStub> onTopOfChunkCenter(GenerationContext $$0, Heightmap.Types $$1, Consumer<StructurePiecesBuilder> $$2) {
        ChunkPos $$3 = $$0.chunkPos();
        int $$4 = $$3.getMiddleBlockX();
        int $$5 = $$3.getMiddleBlockZ();
        int $$6 = $$0.chunkGenerator().getFirstOccupiedHeight($$4, $$5, $$1, $$0.heightAccessor(), $$0.randomState());
        return Optional.of(new GenerationStub(new BlockPos($$4, $$6, $$5), $$2));
    }

    private static boolean isValidBiome(GenerationStub $$0, GenerationContext $$1) {
        BlockPos $$2 = $$0.position();
        return $$1.validBiome.test($$1.chunkGenerator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock($$2.getX()), QuartPos.fromBlock($$2.getY()), QuartPos.fromBlock($$2.getZ()), $$1.randomState.sampler()));
    }

    public void afterPlace(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, PiecesContainer $$6) {
    }

    private static int[] c(GenerationContext $$0, int $$1, int $$2, int $$3, int $$4) {
        ChunkGenerator $$5 = $$0.chunkGenerator();
        LevelHeightAccessor $$6 = $$0.heightAccessor();
        RandomState $$7 = $$0.randomState();
        return new int[]{$$5.getFirstOccupiedHeight($$1, $$3, Heightmap.Types.WORLD_SURFACE_WG, $$6, $$7), $$5.getFirstOccupiedHeight($$1, $$3 + $$4, Heightmap.Types.WORLD_SURFACE_WG, $$6, $$7), $$5.getFirstOccupiedHeight($$1 + $$2, $$3, Heightmap.Types.WORLD_SURFACE_WG, $$6, $$7), $$5.getFirstOccupiedHeight($$1 + $$2, $$3 + $$4, Heightmap.Types.WORLD_SURFACE_WG, $$6, $$7)};
    }

    public static int getMeanFirstOccupiedHeight(GenerationContext $$0, int $$1, int $$2, int $$3, int $$4) {
        int[] $$5 = Structure.c($$0, $$1, $$2, $$3, $$4);
        return ($$5[0] + $$5[1] + $$5[2] + $$5[3]) / 4;
    }

    protected static int getLowestY(GenerationContext $$0, int $$1, int $$2) {
        ChunkPos $$3 = $$0.chunkPos();
        int $$4 = $$3.getMinBlockX();
        int $$5 = $$3.getMinBlockZ();
        return Structure.getLowestY($$0, $$4, $$5, $$1, $$2);
    }

    protected static int getLowestY(GenerationContext $$0, int $$1, int $$2, int $$3, int $$4) {
        int[] $$5 = Structure.c($$0, $$1, $$3, $$2, $$4);
        return Math.min(Math.min($$5[0], $$5[1]), Math.min($$5[2], $$5[3]));
    }

    @Deprecated
    protected BlockPos getLowestYIn5by5BoxOffset7Blocks(GenerationContext $$0, Rotation $$1) {
        int $$2 = 5;
        int $$3 = 5;
        if ($$1 == Rotation.CLOCKWISE_90) {
            $$2 = -5;
        } else if ($$1 == Rotation.CLOCKWISE_180) {
            $$2 = -5;
            $$3 = -5;
        } else if ($$1 == Rotation.COUNTERCLOCKWISE_90) {
            $$3 = -5;
        }
        ChunkPos $$4 = $$0.chunkPos();
        int $$5 = $$4.getBlockX(7);
        int $$6 = $$4.getBlockZ(7);
        return new BlockPos($$5, Structure.getLowestY($$0, $$5, $$6, $$2, $$3), $$6);
    }

    protected abstract Optional<GenerationStub> findGenerationPoint(GenerationContext var1);

    public Optional<GenerationStub> findValidGenerationPoint(GenerationContext $$0) {
        return this.findGenerationPoint($$0).filter($$1 -> Structure.isValidBiome($$1, $$0));
    }

    public abstract StructureType<?> type();

    public static final class StructureSettings
    extends Record {
        final HolderSet<Biome> biomes;
        final Map<MobCategory, StructureSpawnOverride> spawnOverrides;
        final GenerationStep.Decoration step;
        final TerrainAdjustment terrainAdaptation;
        static final StructureSettings DEFAULT = new StructureSettings(HolderSet.a(new Holder[0]), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE);
        public static final MapCodec<StructureSettings> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(StructureSettings::biomes), (App)Codec.simpleMap(MobCategory.CODEC, StructureSpawnOverride.CODEC, (Keyable)StringRepresentable.a(MobCategory.values())).fieldOf("spawn_overrides").forGetter(StructureSettings::spawnOverrides), (App)GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(StructureSettings::step), (App)TerrainAdjustment.CODEC.optionalFieldOf("terrain_adaptation", (Object)StructureSettings.DEFAULT.terrainAdaptation).forGetter(StructureSettings::terrainAdaptation)).apply((Applicative)$$0, StructureSettings::new));

        public StructureSettings(HolderSet<Biome> $$0) {
            this($$0, StructureSettings.DEFAULT.spawnOverrides, StructureSettings.DEFAULT.step, StructureSettings.DEFAULT.terrainAdaptation);
        }

        public StructureSettings(HolderSet<Biome> $$0, Map<MobCategory, StructureSpawnOverride> $$1, GenerationStep.Decoration $$2, TerrainAdjustment $$3) {
            this.biomes = $$0;
            this.spawnOverrides = $$1;
            this.step = $$2;
            this.terrainAdaptation = $$3;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{StructureSettings.class, "biomes;spawnOverrides;step;terrainAdaptation", "biomes", "spawnOverrides", "step", "terrainAdaptation"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StructureSettings.class, "biomes;spawnOverrides;step;terrainAdaptation", "biomes", "spawnOverrides", "step", "terrainAdaptation"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StructureSettings.class, "biomes;spawnOverrides;step;terrainAdaptation", "biomes", "spawnOverrides", "step", "terrainAdaptation"}, this, $$0);
        }

        public HolderSet<Biome> biomes() {
            return this.biomes;
        }

        public Map<MobCategory, StructureSpawnOverride> spawnOverrides() {
            return this.spawnOverrides;
        }

        public GenerationStep.Decoration step() {
            return this.step;
        }

        public TerrainAdjustment terrainAdaptation() {
            return this.terrainAdaptation;
        }

        public static class Builder {
            private final HolderSet<Biome> biomes;
            private Map<MobCategory, StructureSpawnOverride> spawnOverrides;
            private GenerationStep.Decoration step;
            private TerrainAdjustment terrainAdaption;

            public Builder(HolderSet<Biome> $$0) {
                this.spawnOverrides = StructureSettings.DEFAULT.spawnOverrides;
                this.step = StructureSettings.DEFAULT.step;
                this.terrainAdaption = StructureSettings.DEFAULT.terrainAdaptation;
                this.biomes = $$0;
            }

            public Builder spawnOverrides(Map<MobCategory, StructureSpawnOverride> $$0) {
                this.spawnOverrides = $$0;
                return this;
            }

            public Builder generationStep(GenerationStep.Decoration $$0) {
                this.step = $$0;
                return this;
            }

            public Builder terrainAdapation(TerrainAdjustment $$0) {
                this.terrainAdaption = $$0;
                return this;
            }

            public StructureSettings build() {
                return new StructureSettings(this.biomes, this.spawnOverrides, this.step, this.terrainAdaption);
            }
        }
    }

    public static final class GenerationContext
    extends Record {
        private final RegistryAccess registryAccess;
        final ChunkGenerator chunkGenerator;
        private final BiomeSource biomeSource;
        final RandomState randomState;
        private final StructureTemplateManager structureTemplateManager;
        private final WorldgenRandom random;
        private final long seed;
        private final ChunkPos chunkPos;
        private final LevelHeightAccessor heightAccessor;
        final Predicate<Holder<Biome>> validBiome;

        public GenerationContext(RegistryAccess $$0, ChunkGenerator $$1, BiomeSource $$2, RandomState $$3, StructureTemplateManager $$4, long $$5, ChunkPos $$6, LevelHeightAccessor $$7, Predicate<Holder<Biome>> $$8) {
            this($$0, $$1, $$2, $$3, $$4, GenerationContext.makeRandom($$5, $$6), $$5, $$6, $$7, $$8);
        }

        public GenerationContext(RegistryAccess $$0, ChunkGenerator $$1, BiomeSource $$2, RandomState $$3, StructureTemplateManager $$4, WorldgenRandom $$5, long $$6, ChunkPos $$7, LevelHeightAccessor $$8, Predicate<Holder<Biome>> $$9) {
            this.registryAccess = $$0;
            this.chunkGenerator = $$1;
            this.biomeSource = $$2;
            this.randomState = $$3;
            this.structureTemplateManager = $$4;
            this.random = $$5;
            this.seed = $$6;
            this.chunkPos = $$7;
            this.heightAccessor = $$8;
            this.validBiome = $$9;
        }

        private static WorldgenRandom makeRandom(long $$0, ChunkPos $$1) {
            WorldgenRandom $$2 = new WorldgenRandom(new LegacyRandomSource(0L));
            $$2.setLargeFeatureSeed($$0, $$1.x, $$1.z);
            return $$2;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{GenerationContext.class, "registryAccess;chunkGenerator;biomeSource;randomState;structureTemplateManager;random;seed;chunkPos;heightAccessor;validBiome", "registryAccess", "chunkGenerator", "biomeSource", "randomState", "structureTemplateManager", "random", "seed", "chunkPos", "heightAccessor", "validBiome"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GenerationContext.class, "registryAccess;chunkGenerator;biomeSource;randomState;structureTemplateManager;random;seed;chunkPos;heightAccessor;validBiome", "registryAccess", "chunkGenerator", "biomeSource", "randomState", "structureTemplateManager", "random", "seed", "chunkPos", "heightAccessor", "validBiome"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GenerationContext.class, "registryAccess;chunkGenerator;biomeSource;randomState;structureTemplateManager;random;seed;chunkPos;heightAccessor;validBiome", "registryAccess", "chunkGenerator", "biomeSource", "randomState", "structureTemplateManager", "random", "seed", "chunkPos", "heightAccessor", "validBiome"}, this, $$0);
        }

        public RegistryAccess registryAccess() {
            return this.registryAccess;
        }

        public ChunkGenerator chunkGenerator() {
            return this.chunkGenerator;
        }

        public BiomeSource biomeSource() {
            return this.biomeSource;
        }

        public RandomState randomState() {
            return this.randomState;
        }

        public StructureTemplateManager structureTemplateManager() {
            return this.structureTemplateManager;
        }

        public WorldgenRandom random() {
            return this.random;
        }

        public long seed() {
            return this.seed;
        }

        public ChunkPos chunkPos() {
            return this.chunkPos;
        }

        public LevelHeightAccessor heightAccessor() {
            return this.heightAccessor;
        }

        public Predicate<Holder<Biome>> validBiome() {
            return this.validBiome;
        }
    }

    public record GenerationStub(BlockPos position, Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder> generator) {
        public GenerationStub(BlockPos $$0, Consumer<StructurePiecesBuilder> $$1) {
            this($$0, (Either<Consumer<StructurePiecesBuilder>, StructurePiecesBuilder>)Either.left($$1));
        }

        public StructurePiecesBuilder getPiecesBuilder() {
            return (StructurePiecesBuilder)this.generator.map($$0 -> {
                StructurePiecesBuilder $$1 = new StructurePiecesBuilder();
                $$0.accept($$1);
                return $$1;
            }, $$0 -> $$0);
        }
    }
}

