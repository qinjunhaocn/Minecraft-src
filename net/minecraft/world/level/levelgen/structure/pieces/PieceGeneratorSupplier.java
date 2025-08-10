/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen.structure.pieces;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

@FunctionalInterface
public interface PieceGeneratorSupplier<C extends FeatureConfiguration> {
    public Optional<PieceGenerator<C>> createGenerator(Context<C> var1);

    public static <C extends FeatureConfiguration> PieceGeneratorSupplier<C> simple(Predicate<Context<C>> $$0, PieceGenerator<C> $$1) {
        Optional $$22 = Optional.of($$1);
        return $$2 -> $$0.test($$2) ? $$22 : Optional.empty();
    }

    public static <C extends FeatureConfiguration> Predicate<Context<C>> checkForBiomeOnTop(Heightmap.Types $$0) {
        return $$1 -> $$1.validBiomeOnTop($$0);
    }

    public record Context<C extends FeatureConfiguration>(ChunkGenerator chunkGenerator, BiomeSource biomeSource, RandomState randomState, long seed, ChunkPos chunkPos, C config, LevelHeightAccessor heightAccessor, Predicate<Holder<Biome>> validBiome, StructureTemplateManager structureTemplateManager, RegistryAccess registryAccess) {
        public boolean validBiomeOnTop(Heightmap.Types $$0) {
            int $$1 = this.chunkPos.getMiddleBlockX();
            int $$2 = this.chunkPos.getMiddleBlockZ();
            int $$3 = this.chunkGenerator.getFirstOccupiedHeight($$1, $$2, $$0, this.heightAccessor, this.randomState);
            Holder<Biome> $$4 = this.chunkGenerator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock($$1), QuartPos.fromBlock($$3), QuartPos.fromBlock($$2), this.randomState.sampler());
            return this.validBiome.test($$4);
        }
    }
}

