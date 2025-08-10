/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class FlatLevelSource
extends ChunkGenerator {
    public static final MapCodec<FlatLevelSource> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(FlatLevelSource::settings)).apply((Applicative)$$0, $$0.stable(FlatLevelSource::new)));
    private final FlatLevelGeneratorSettings settings;

    public FlatLevelSource(FlatLevelGeneratorSettings $$0) {
        super(new FixedBiomeSource($$0.getBiome()), Util.memoize($$0::adjustGenerationSettings));
        this.settings = $$0;
    }

    @Override
    public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> $$0, RandomState $$1, long $$2) {
        Stream $$3 = this.settings.structureOverrides().map(HolderSet::stream).orElseGet(() -> $$0.listElements().map($$0 -> $$0));
        return ChunkGeneratorStructureState.createForFlat($$1, $$2, this.biomeSource, $$3);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    public FlatLevelGeneratorSettings settings() {
        return this.settings;
    }

    @Override
    public void buildSurface(WorldGenRegion $$0, StructureManager $$1, RandomState $$2, ChunkAccess $$3) {
    }

    @Override
    public int getSpawnHeight(LevelHeightAccessor $$0) {
        return $$0.getMinY() + Math.min($$0.getHeight(), this.settings.getLayers().size());
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender $$0, RandomState $$1, StructureManager $$2, ChunkAccess $$3) {
        List<BlockState> $$4 = this.settings.getLayers();
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
        Heightmap $$6 = $$3.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap $$7 = $$3.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        for (int $$8 = 0; $$8 < Math.min($$3.getHeight(), $$4.size()); ++$$8) {
            BlockState $$9 = $$4.get($$8);
            if ($$9 == null) continue;
            int $$10 = $$3.getMinY() + $$8;
            for (int $$11 = 0; $$11 < 16; ++$$11) {
                for (int $$12 = 0; $$12 < 16; ++$$12) {
                    $$3.setBlockState($$5.set($$11, $$10, $$12), $$9);
                    $$6.update($$11, $$10, $$12, $$9);
                    $$7.update($$11, $$10, $$12, $$9);
                }
            }
        }
        return CompletableFuture.completedFuture($$3);
    }

    @Override
    public int getBaseHeight(int $$0, int $$1, Heightmap.Types $$2, LevelHeightAccessor $$3, RandomState $$4) {
        List<BlockState> $$5 = this.settings.getLayers();
        for (int $$6 = Math.min($$5.size() - 1, $$3.getMaxY()); $$6 >= 0; --$$6) {
            BlockState $$7 = $$5.get($$6);
            if ($$7 == null || !$$2.isOpaque().test($$7)) continue;
            return $$3.getMinY() + $$6 + 1;
        }
        return $$3.getMinY();
    }

    @Override
    public NoiseColumn getBaseColumn(int $$02, int $$1, LevelHeightAccessor $$2, RandomState $$3) {
        return new NoiseColumn($$2.getMinY(), (BlockState[])this.settings.getLayers().stream().limit($$2.getHeight()).map($$0 -> $$0 == null ? Blocks.AIR.defaultBlockState() : $$0).toArray(BlockState[]::new));
    }

    @Override
    public void addDebugScreenInfo(List<String> $$0, RandomState $$1, BlockPos $$2) {
    }

    @Override
    public void applyCarvers(WorldGenRegion $$0, long $$1, RandomState $$2, BiomeManager $$3, StructureManager $$4, ChunkAccess $$5) {
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion $$0) {
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getGenDepth() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return -63;
    }
}

