/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

public class DebugLevelSource
extends ChunkGenerator {
    public static final MapCodec<DebugLevelSource> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group(RegistryOps.retrieveElement(Biomes.PLAINS)).apply((Applicative)$$0, $$0.stable(DebugLevelSource::new)));
    private static final int BLOCK_MARGIN = 2;
    private static final List<BlockState> ALL_BLOCKS = StreamSupport.stream(BuiltInRegistries.BLOCK.spliterator(), false).flatMap($$0 -> $$0.getStateDefinition().getPossibleStates().stream()).collect(Collectors.toList());
    private static final int GRID_WIDTH = Mth.ceil(Mth.sqrt(ALL_BLOCKS.size()));
    private static final int GRID_HEIGHT = Mth.ceil((float)ALL_BLOCKS.size() / (float)GRID_WIDTH);
    protected static final BlockState AIR = Blocks.AIR.defaultBlockState();
    protected static final BlockState BARRIER = Blocks.BARRIER.defaultBlockState();
    public static final int HEIGHT = 70;
    public static final int BARRIER_HEIGHT = 60;

    public DebugLevelSource(Holder.Reference<Biome> $$0) {
        super(new FixedBiomeSource($$0));
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void buildSurface(WorldGenRegion $$0, StructureManager $$1, RandomState $$2, ChunkAccess $$3) {
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel $$0, ChunkAccess $$1, StructureManager $$2) {
        BlockPos.MutableBlockPos $$3 = new BlockPos.MutableBlockPos();
        ChunkPos $$4 = $$1.getPos();
        int $$5 = $$4.x;
        int $$6 = $$4.z;
        for (int $$7 = 0; $$7 < 16; ++$$7) {
            for (int $$8 = 0; $$8 < 16; ++$$8) {
                int $$9 = SectionPos.sectionToBlockCoord($$5, $$7);
                int $$10 = SectionPos.sectionToBlockCoord($$6, $$8);
                $$0.setBlock($$3.set($$9, 60, $$10), BARRIER, 2);
                BlockState $$11 = DebugLevelSource.getBlockStateFor($$9, $$10);
                $$0.setBlock($$3.set($$9, 70, $$10), $$11, 2);
            }
        }
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender $$0, RandomState $$1, StructureManager $$2, ChunkAccess $$3) {
        return CompletableFuture.completedFuture($$3);
    }

    @Override
    public int getBaseHeight(int $$0, int $$1, Heightmap.Types $$2, LevelHeightAccessor $$3, RandomState $$4) {
        return 0;
    }

    @Override
    public NoiseColumn getBaseColumn(int $$0, int $$1, LevelHeightAccessor $$2, RandomState $$3) {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> $$0, RandomState $$1, BlockPos $$2) {
    }

    public static BlockState getBlockStateFor(int $$0, int $$1) {
        int $$3;
        BlockState $$2 = AIR;
        if ($$0 > 0 && $$1 > 0 && $$0 % 2 != 0 && $$1 % 2 != 0 && ($$0 /= 2) <= GRID_WIDTH && ($$1 /= 2) <= GRID_HEIGHT && ($$3 = Mth.abs($$0 * GRID_WIDTH + $$1)) < ALL_BLOCKS.size()) {
            $$2 = ALL_BLOCKS.get($$3);
        }
        return $$2;
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
        return 63;
    }
}

