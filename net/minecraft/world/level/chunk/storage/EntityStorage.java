/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.world.level.chunk.storage;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.thread.ConsecutiveExecutor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.slf4j.Logger;

public class EntityStorage
implements EntityPersistentStorage<Entity> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ENTITIES_TAG = "Entities";
    private static final String POSITION_TAG = "Position";
    private final ServerLevel level;
    private final SimpleRegionStorage simpleRegionStorage;
    private final LongSet emptyChunks = new LongOpenHashSet();
    private final ConsecutiveExecutor entityDeserializerQueue;

    public EntityStorage(SimpleRegionStorage $$0, ServerLevel $$1, Executor $$2) {
        this.simpleRegionStorage = $$0;
        this.level = $$1;
        this.entityDeserializerQueue = new ConsecutiveExecutor($$2, "entity-deserializer");
    }

    @Override
    public CompletableFuture<ChunkEntities<Entity>> loadEntities(ChunkPos $$0) {
        if (this.emptyChunks.contains($$0.toLong())) {
            return CompletableFuture.completedFuture(EntityStorage.emptyChunk($$0));
        }
        CompletableFuture<Optional<CompoundTag>> $$12 = this.simpleRegionStorage.read($$0);
        this.reportLoadFailureIfPresent($$12, $$0);
        return $$12.thenApplyAsync($$1 -> {
            if ($$1.isEmpty()) {
                this.emptyChunks.add($$0.toLong());
                return EntityStorage.emptyChunk($$0);
            }
            try {
                ChunkPos $$2 = (ChunkPos)((CompoundTag)$$1.get()).read(POSITION_TAG, ChunkPos.CODEC).orElseThrow();
                if (!Objects.equals($$0, $$2)) {
                    LOGGER.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", $$0, $$0, $$2);
                    this.level.getServer().reportMisplacedChunk($$2, $$0, this.simpleRegionStorage.storageInfo());
                }
            } catch (Exception $$3) {
                LOGGER.warn("Failed to parse chunk {} position info", (Object)$$0, (Object)$$3);
                this.level.getServer().reportChunkLoadFailure($$3, this.simpleRegionStorage.storageInfo(), $$0);
            }
            CompoundTag $$4 = this.simpleRegionStorage.upgradeChunkTag((CompoundTag)$$1.get(), -1);
            try (ProblemReporter.ScopedCollector $$5 = new ProblemReporter.ScopedCollector(ChunkAccess.problemPath($$0), LOGGER);){
                ValueInput $$6 = TagValueInput.create((ProblemReporter)$$5, (HolderLookup.Provider)this.level.registryAccess(), $$4);
                ValueInput.ValueInputList $$7 = $$6.childrenListOrEmpty(ENTITIES_TAG);
                List $$8 = EntityType.loadEntitiesRecursive($$7, this.level, EntitySpawnReason.LOAD).toList();
                ChunkEntities chunkEntities = new ChunkEntities($$0, $$8);
                return chunkEntities;
            }
        }, this.entityDeserializerQueue::schedule);
    }

    private static ChunkEntities<Entity> emptyChunk(ChunkPos $$0) {
        return new ChunkEntities<Entity>($$0, List.of());
    }

    @Override
    public void storeEntities(ChunkEntities<Entity> $$0) {
        ChunkPos $$1 = $$0.getPos();
        if ($$0.isEmpty()) {
            if (this.emptyChunks.add($$1.toLong())) {
                this.reportSaveFailureIfPresent(this.simpleRegionStorage.write($$1, null), $$1);
            }
            return;
        }
        try (ProblemReporter.ScopedCollector $$22 = new ProblemReporter.ScopedCollector(ChunkAccess.problemPath($$1), LOGGER);){
            ListTag $$3 = new ListTag();
            $$0.getEntities().forEach($$2 -> {
                TagValueOutput $$3 = TagValueOutput.createWithContext($$22.forChild($$2.problemPath()), $$2.registryAccess());
                if ($$2.save($$3)) {
                    CompoundTag $$4 = $$3.buildResult();
                    $$3.add($$4);
                }
            });
            CompoundTag $$4 = NbtUtils.addCurrentDataVersion(new CompoundTag());
            $$4.put(ENTITIES_TAG, $$3);
            $$4.store(POSITION_TAG, ChunkPos.CODEC, $$1);
            this.reportSaveFailureIfPresent(this.simpleRegionStorage.write($$1, $$4), $$1);
            this.emptyChunks.remove($$1.toLong());
        }
    }

    private void reportSaveFailureIfPresent(CompletableFuture<?> $$0, ChunkPos $$12) {
        $$0.exceptionally($$1 -> {
            LOGGER.error("Failed to store entity chunk {}", (Object)$$12, $$1);
            this.level.getServer().reportChunkSaveFailure((Throwable)$$1, this.simpleRegionStorage.storageInfo(), $$12);
            return null;
        });
    }

    private void reportLoadFailureIfPresent(CompletableFuture<?> $$0, ChunkPos $$12) {
        $$0.exceptionally($$1 -> {
            LOGGER.error("Failed to load entity chunk {}", (Object)$$12, $$1);
            this.level.getServer().reportChunkLoadFailure((Throwable)$$1, this.simpleRegionStorage.storageInfo(), $$12);
            return null;
        });
    }

    @Override
    public void flush(boolean $$0) {
        this.simpleRegionStorage.synchronize($$0).join();
        this.entityDeserializerQueue.runAll();
    }

    @Override
    public void close() throws IOException {
        this.simpleRegionStorage.close();
    }
}

