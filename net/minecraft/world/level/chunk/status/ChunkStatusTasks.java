/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.world.level.chunk.status;

import com.mojang.logging.LogUtils;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.slf4j.Logger;

public class ChunkStatusTasks {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static boolean isLighted(ChunkAccess $$0) {
        return $$0.getPersistedStatus().isOrAfter(ChunkStatus.LIGHT) && $$0.isLightCorrect();
    }

    static CompletableFuture<ChunkAccess> passThrough(WorldGenContext $$0, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess $$3) {
        return CompletableFuture.completedFuture($$3);
    }

    static CompletableFuture<ChunkAccess> generateStructureStarts(WorldGenContext $$0, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess $$3) {
        ServerLevel $$4 = $$0.level();
        if ($$4.getServer().getWorldData().worldGenOptions().generateStructures()) {
            $$0.generator().createStructures($$4.registryAccess(), $$4.getChunkSource().getGeneratorState(), $$4.structureManager(), $$3, $$0.structureManager(), $$4.dimension());
        }
        $$4.onStructureStartsAvailable($$3);
        return CompletableFuture.completedFuture($$3);
    }

    static CompletableFuture<ChunkAccess> loadStructureStarts(WorldGenContext $$0, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess $$3) {
        $$0.level().onStructureStartsAvailable($$3);
        return CompletableFuture.completedFuture($$3);
    }

    static CompletableFuture<ChunkAccess> generateStructureReferences(WorldGenContext $$0, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess $$3) {
        ServerLevel $$4 = $$0.level();
        WorldGenRegion $$5 = new WorldGenRegion($$4, $$2, $$1, $$3);
        $$0.generator().createReferences($$5, $$4.structureManager().forWorldGenRegion($$5), $$3);
        return CompletableFuture.completedFuture($$3);
    }

    static CompletableFuture<ChunkAccess> generateBiomes(WorldGenContext $$0, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess $$3) {
        ServerLevel $$4 = $$0.level();
        WorldGenRegion $$5 = new WorldGenRegion($$4, $$2, $$1, $$3);
        return $$0.generator().createBiomes($$4.getChunkSource().randomState(), Blender.of($$5), $$4.structureManager().forWorldGenRegion($$5), $$3);
    }

    static CompletableFuture<ChunkAccess> generateNoise(WorldGenContext $$02, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess $$3) {
        ServerLevel $$4 = $$02.level();
        WorldGenRegion $$5 = new WorldGenRegion($$4, $$2, $$1, $$3);
        return $$02.generator().fillFromNoise(Blender.of($$5), $$4.getChunkSource().randomState(), $$4.structureManager().forWorldGenRegion($$5), $$3).thenApply($$0 -> {
            ProtoChunk $$1;
            BelowZeroRetrogen $$2;
            if ($$0 instanceof ProtoChunk && ($$2 = ($$1 = (ProtoChunk)$$0).getBelowZeroRetrogen()) != null) {
                BelowZeroRetrogen.replaceOldBedrock($$1);
                if ($$2.hasBedrockHoles()) {
                    $$2.applyBedrockMask($$1);
                }
            }
            return $$0;
        });
    }

    static CompletableFuture<ChunkAccess> generateSurface(WorldGenContext $$0, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess $$3) {
        ServerLevel $$4 = $$0.level();
        WorldGenRegion $$5 = new WorldGenRegion($$4, $$2, $$1, $$3);
        $$0.generator().buildSurface($$5, $$4.structureManager().forWorldGenRegion($$5), $$4.getChunkSource().randomState(), $$3);
        return CompletableFuture.completedFuture($$3);
    }

    static CompletableFuture<ChunkAccess> generateCarvers(WorldGenContext $$0, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess $$3) {
        ServerLevel $$4 = $$0.level();
        WorldGenRegion $$5 = new WorldGenRegion($$4, $$2, $$1, $$3);
        if ($$3 instanceof ProtoChunk) {
            ProtoChunk $$6 = (ProtoChunk)$$3;
            Blender.addAroundOldChunksCarvingMaskFilter($$5, $$6);
        }
        $$0.generator().applyCarvers($$5, $$4.getSeed(), $$4.getChunkSource().randomState(), $$4.getBiomeManager(), $$4.structureManager().forWorldGenRegion($$5), $$3);
        return CompletableFuture.completedFuture($$3);
    }

    static CompletableFuture<ChunkAccess> generateFeatures(WorldGenContext $$0, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess $$3) {
        ServerLevel $$4 = $$0.level();
        Heightmap.primeHeightmaps($$3, EnumSet.of(Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE));
        WorldGenRegion $$5 = new WorldGenRegion($$4, $$2, $$1, $$3);
        $$0.generator().applyBiomeDecoration($$5, $$3, $$4.structureManager().forWorldGenRegion($$5));
        Blender.generateBorderTicks($$5, $$3);
        return CompletableFuture.completedFuture($$3);
    }

    static CompletableFuture<ChunkAccess> initializeLight(WorldGenContext $$0, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess $$3) {
        ThreadedLevelLightEngine $$4 = $$0.lightEngine();
        $$3.initializeLightSources();
        ((ProtoChunk)$$3).setLightEngine($$4);
        boolean $$5 = ChunkStatusTasks.isLighted($$3);
        return $$4.initializeLight($$3, $$5);
    }

    static CompletableFuture<ChunkAccess> light(WorldGenContext $$0, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess $$3) {
        boolean $$4 = ChunkStatusTasks.isLighted($$3);
        return $$0.lightEngine().lightChunk($$3, $$4);
    }

    static CompletableFuture<ChunkAccess> generateSpawn(WorldGenContext $$0, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess $$3) {
        if (!$$3.isUpgrading()) {
            $$0.generator().spawnOriginalMobs(new WorldGenRegion($$0.level(), $$2, $$1, $$3));
        }
        return CompletableFuture.completedFuture($$3);
    }

    static CompletableFuture<ChunkAccess> full(WorldGenContext $$0, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess $$3) {
        ChunkPos $$4 = $$3.getPos();
        GenerationChunkHolder $$5 = $$2.get($$4.x, $$4.z);
        return CompletableFuture.supplyAsync(() -> {
            LevelChunk $$7;
            ProtoChunk $$32 = (ProtoChunk)$$3;
            ServerLevel $$4 = $$0.level();
            if ($$32 instanceof ImposterProtoChunk) {
                ImposterProtoChunk $$5 = (ImposterProtoChunk)$$32;
                LevelChunk $$6 = $$5.getWrapped();
            } else {
                $$7 = new LevelChunk($$4, $$32, $$3 -> {
                    try (ProblemReporter.ScopedCollector $$4 = new ProblemReporter.ScopedCollector($$3.problemPath(), LOGGER);){
                        ChunkStatusTasks.postLoadProtoChunk($$4, TagValueInput.create((ProblemReporter)$$4, (HolderLookup.Provider)$$4.registryAccess(), $$32.getEntities()));
                    }
                });
                $$5.replaceProtoChunk(new ImposterProtoChunk($$7, false));
            }
            $$7.setFullStatus($$5::getFullStatus);
            $$7.runPostLoad();
            $$7.setLoaded(true);
            $$7.registerAllBlockEntitiesAfterLevelLoad();
            $$7.registerTickContainerInLevel($$4);
            $$7.setUnsavedListener($$0.unsavedListener());
            return $$7;
        }, $$0.mainThreadExecutor());
    }

    private static void postLoadProtoChunk(ServerLevel $$0, ValueInput.ValueInputList $$1) {
        if (!$$1.isEmpty()) {
            $$0.addWorldGenChunkEntities(EntityType.loadEntitiesRecursive($$1, $$0, EntitySpawnReason.LOAD));
        }
    }
}

