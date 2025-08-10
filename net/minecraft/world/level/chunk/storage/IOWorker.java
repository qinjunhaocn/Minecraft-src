/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  java.util.SequencedMap
 */
package net.minecraft.world.level.chunk.storage;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.IOException;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.PriorityConsecutiveExecutor;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.slf4j.Logger;

public class IOWorker
implements ChunkScanAccess,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final AtomicBoolean shutdownRequested = new AtomicBoolean();
    private final PriorityConsecutiveExecutor consecutiveExecutor;
    private final RegionFileStorage storage;
    private final SequencedMap<ChunkPos, PendingStore> pendingWrites = new LinkedHashMap();
    private final Long2ObjectLinkedOpenHashMap<CompletableFuture<BitSet>> regionCacheForBlender = new Long2ObjectLinkedOpenHashMap();
    private static final int REGION_CACHE_SIZE = 1024;

    protected IOWorker(RegionStorageInfo $$0, Path $$1, boolean $$2) {
        this.storage = new RegionFileStorage($$0, $$1, $$2);
        this.consecutiveExecutor = new PriorityConsecutiveExecutor(Priority.values().length, (Executor)Util.ioPool(), "IOWorker-" + $$0.type());
    }

    public boolean isOldChunkAround(ChunkPos $$0, int $$1) {
        ChunkPos $$2 = new ChunkPos($$0.x - $$1, $$0.z - $$1);
        ChunkPos $$3 = new ChunkPos($$0.x + $$1, $$0.z + $$1);
        for (int $$4 = $$2.getRegionX(); $$4 <= $$3.getRegionX(); ++$$4) {
            for (int $$5 = $$2.getRegionZ(); $$5 <= $$3.getRegionZ(); ++$$5) {
                BitSet $$6 = this.getOrCreateOldDataForRegion($$4, $$5).join();
                if ($$6.isEmpty()) continue;
                ChunkPos $$7 = ChunkPos.minFromRegion($$4, $$5);
                int $$8 = Math.max($$2.x - $$7.x, 0);
                int $$9 = Math.max($$2.z - $$7.z, 0);
                int $$10 = Math.min($$3.x - $$7.x, 31);
                int $$11 = Math.min($$3.z - $$7.z, 31);
                for (int $$12 = $$8; $$12 <= $$10; ++$$12) {
                    for (int $$13 = $$9; $$13 <= $$11; ++$$13) {
                        int $$14 = $$13 * 32 + $$12;
                        if (!$$6.get($$14)) continue;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CompletableFuture<BitSet> getOrCreateOldDataForRegion(int $$0, int $$1) {
        long $$2 = ChunkPos.asLong($$0, $$1);
        Long2ObjectLinkedOpenHashMap<CompletableFuture<BitSet>> long2ObjectLinkedOpenHashMap = this.regionCacheForBlender;
        synchronized (long2ObjectLinkedOpenHashMap) {
            CompletableFuture<BitSet> $$3 = (CompletableFuture<BitSet>)this.regionCacheForBlender.getAndMoveToFirst($$2);
            if ($$3 == null) {
                $$3 = this.createOldDataForRegion($$0, $$1);
                this.regionCacheForBlender.putAndMoveToFirst($$2, $$3);
                if (this.regionCacheForBlender.size() > 1024) {
                    this.regionCacheForBlender.removeLast();
                }
            }
            return $$3;
        }
    }

    private CompletableFuture<BitSet> createOldDataForRegion(int $$0, int $$1) {
        return CompletableFuture.supplyAsync(() -> {
            ChunkPos $$2 = ChunkPos.minFromRegion($$0, $$1);
            ChunkPos $$3 = ChunkPos.maxFromRegion($$0, $$1);
            BitSet $$4 = new BitSet();
            ChunkPos.rangeClosed($$2, $$3).forEach($$1 -> {
                CompoundTag $$5;
                CollectFields $$2 = new CollectFields(new FieldSelector(IntTag.TYPE, "DataVersion"), new FieldSelector(CompoundTag.TYPE, "blending_data"));
                try {
                    this.scanChunk((ChunkPos)$$1, $$2).join();
                } catch (Exception $$3) {
                    LOGGER.warn("Failed to scan chunk {}", $$1, (Object)$$3);
                    return;
                }
                Tag $$4 = $$2.getResult();
                if ($$4 instanceof CompoundTag && this.isOldChunk($$5 = (CompoundTag)$$4)) {
                    int $$6 = $$1.getRegionLocalZ() * 32 + $$1.getRegionLocalX();
                    $$4.set($$6);
                }
            });
            return $$4;
        }, Util.backgroundExecutor());
    }

    private boolean isOldChunk(CompoundTag $$0) {
        if ($$0.getIntOr("DataVersion", 0) < 4295) {
            return true;
        }
        return $$0.getCompound("blending_data").isPresent();
    }

    public CompletableFuture<Void> store(ChunkPos $$0, @Nullable CompoundTag $$1) {
        return this.store($$0, () -> $$1);
    }

    public CompletableFuture<Void> store(ChunkPos $$0, Supplier<CompoundTag> $$1) {
        return this.submitTask(() -> {
            CompoundTag $$2 = (CompoundTag)$$1.get();
            PendingStore $$3 = (PendingStore)this.pendingWrites.computeIfAbsent((Object)$$0, $$1 -> new PendingStore($$2));
            $$3.data = $$2;
            return $$3.result;
        }).thenCompose(Function.identity());
    }

    public CompletableFuture<Optional<CompoundTag>> loadAsync(ChunkPos $$0) {
        return this.submitThrowingTask(() -> {
            PendingStore $$1 = (PendingStore)this.pendingWrites.get((Object)$$0);
            if ($$1 != null) {
                return Optional.ofNullable($$1.copyData());
            }
            try {
                CompoundTag $$2 = this.storage.read($$0);
                return Optional.ofNullable($$2);
            } catch (Exception $$3) {
                LOGGER.warn("Failed to read chunk {}", (Object)$$0, (Object)$$3);
                throw $$3;
            }
        });
    }

    public CompletableFuture<Void> synchronize(boolean $$02) {
        CompletionStage $$1 = this.submitTask(() -> CompletableFuture.allOf((CompletableFuture[])this.pendingWrites.values().stream().map($$0 -> $$0.result).toArray(CompletableFuture[]::new))).thenCompose(Function.identity());
        if ($$02) {
            return ((CompletableFuture)$$1).thenCompose($$0 -> this.submitThrowingTask(() -> {
                try {
                    this.storage.flush();
                    return null;
                } catch (Exception $$0) {
                    LOGGER.warn("Failed to synchronize chunks", (Throwable)$$0);
                    throw $$0;
                }
            }));
        }
        return ((CompletableFuture)$$1).thenCompose($$0 -> this.submitTask(() -> null));
    }

    @Override
    public CompletableFuture<Void> scanChunk(ChunkPos $$0, StreamTagVisitor $$1) {
        return this.submitThrowingTask(() -> {
            try {
                PendingStore $$2 = (PendingStore)this.pendingWrites.get((Object)$$0);
                if ($$2 != null) {
                    if ($$2.data != null) {
                        $$2.data.acceptAsRoot($$1);
                    }
                } else {
                    this.storage.scanChunk($$0, $$1);
                }
                return null;
            } catch (Exception $$3) {
                LOGGER.warn("Failed to bulk scan chunk {}", (Object)$$0, (Object)$$3);
                throw $$3;
            }
        });
    }

    private <T> CompletableFuture<T> submitThrowingTask(ThrowingSupplier<T> $$0) {
        return this.consecutiveExecutor.scheduleWithResult(Priority.FOREGROUND.ordinal(), $$1 -> {
            if (!this.shutdownRequested.get()) {
                try {
                    $$1.complete($$0.get());
                } catch (Exception $$2) {
                    $$1.completeExceptionally($$2);
                }
            }
            this.tellStorePending();
        });
    }

    private <T> CompletableFuture<T> submitTask(Supplier<T> $$0) {
        return this.consecutiveExecutor.scheduleWithResult(Priority.FOREGROUND.ordinal(), $$1 -> {
            if (!this.shutdownRequested.get()) {
                $$1.complete($$0.get());
            }
            this.tellStorePending();
        });
    }

    private void storePendingChunk() {
        Map.Entry $$0 = this.pendingWrites.pollFirstEntry();
        if ($$0 == null) {
            return;
        }
        this.runStore((ChunkPos)$$0.getKey(), (PendingStore)$$0.getValue());
        this.tellStorePending();
    }

    private void tellStorePending() {
        this.consecutiveExecutor.schedule(new StrictQueue.RunnableWithPriority(Priority.BACKGROUND.ordinal(), this::storePendingChunk));
    }

    private void runStore(ChunkPos $$0, PendingStore $$1) {
        try {
            this.storage.write($$0, $$1.data);
            $$1.result.complete(null);
        } catch (Exception $$2) {
            LOGGER.error("Failed to store chunk {}", (Object)$$0, (Object)$$2);
            $$1.result.completeExceptionally($$2);
        }
    }

    @Override
    public void close() throws IOException {
        if (!this.shutdownRequested.compareAndSet(false, true)) {
            return;
        }
        this.waitForShutdown();
        this.consecutiveExecutor.close();
        try {
            this.storage.close();
        } catch (Exception $$0) {
            LOGGER.error("Failed to close storage", $$0);
        }
    }

    private void waitForShutdown() {
        this.consecutiveExecutor.scheduleWithResult(Priority.SHUTDOWN.ordinal(), $$0 -> $$0.complete(Unit.INSTANCE)).join();
    }

    public RegionStorageInfo storageInfo() {
        return this.storage.info();
    }

    static final class Priority
    extends Enum<Priority> {
        public static final /* enum */ Priority FOREGROUND = new Priority();
        public static final /* enum */ Priority BACKGROUND = new Priority();
        public static final /* enum */ Priority SHUTDOWN = new Priority();
        private static final /* synthetic */ Priority[] $VALUES;

        public static Priority[] values() {
            return (Priority[])$VALUES.clone();
        }

        public static Priority valueOf(String $$0) {
            return Enum.valueOf(Priority.class, $$0);
        }

        private static /* synthetic */ Priority[] a() {
            return new Priority[]{FOREGROUND, BACKGROUND, SHUTDOWN};
        }

        static {
            $VALUES = Priority.a();
        }
    }

    @FunctionalInterface
    static interface ThrowingSupplier<T> {
        @Nullable
        public T get() throws Exception;
    }

    static class PendingStore {
        @Nullable
        CompoundTag data;
        final CompletableFuture<Void> result = new CompletableFuture();

        public PendingStore(@Nullable CompoundTag $$0) {
            this.data = $$0;
        }

        @Nullable
        CompoundTag copyData() {
            CompoundTag $$0 = this.data;
            return $$0 == null ? null : $$0.copy();
        }
    }
}

