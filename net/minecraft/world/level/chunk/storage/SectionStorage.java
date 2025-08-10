/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongListIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import org.slf4j.Logger;

public class SectionStorage<R, P>
implements AutoCloseable {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String SECTIONS_TAG = "Sections";
    private final SimpleRegionStorage simpleRegionStorage;
    private final Long2ObjectMap<Optional<R>> storage = new Long2ObjectOpenHashMap();
    private final LongLinkedOpenHashSet dirtyChunks = new LongLinkedOpenHashSet();
    private final Codec<P> codec;
    private final Function<R, P> packer;
    private final BiFunction<P, Runnable, R> unpacker;
    private final Function<Runnable, R> factory;
    private final RegistryAccess registryAccess;
    private final ChunkIOErrorReporter errorReporter;
    protected final LevelHeightAccessor levelHeightAccessor;
    private final LongSet loadedChunks = new LongOpenHashSet();
    private final Long2ObjectMap<CompletableFuture<Optional<PackedChunk<P>>>> pendingLoads = new Long2ObjectOpenHashMap();
    private final Object loadLock = new Object();

    public SectionStorage(SimpleRegionStorage $$0, Codec<P> $$1, Function<R, P> $$2, BiFunction<P, Runnable, R> $$3, Function<Runnable, R> $$4, RegistryAccess $$5, ChunkIOErrorReporter $$6, LevelHeightAccessor $$7) {
        this.simpleRegionStorage = $$0;
        this.codec = $$1;
        this.packer = $$2;
        this.unpacker = $$3;
        this.factory = $$4;
        this.registryAccess = $$5;
        this.errorReporter = $$6;
        this.levelHeightAccessor = $$7;
    }

    protected void tick(BooleanSupplier $$0) {
        LongListIterator $$1 = this.dirtyChunks.iterator();
        while ($$1.hasNext() && $$0.getAsBoolean()) {
            ChunkPos $$2 = new ChunkPos($$1.nextLong());
            $$1.remove();
            this.writeChunk($$2);
        }
        this.unpackPendingLoads();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void unpackPendingLoads() {
        Object object = this.loadLock;
        synchronized (object) {
            ObjectIterator $$0 = Long2ObjectMaps.fastIterator(this.pendingLoads);
            while ($$0.hasNext()) {
                Long2ObjectMap.Entry $$1 = (Long2ObjectMap.Entry)$$0.next();
                Optional $$2 = ((CompletableFuture)$$1.getValue()).getNow(null);
                if ($$2 == null) continue;
                long $$3 = $$1.getLongKey();
                this.unpackChunk(new ChunkPos($$3), $$2.orElse(null));
                $$0.remove();
                this.loadedChunks.add($$3);
            }
        }
    }

    public void flushAll() {
        if (!this.dirtyChunks.isEmpty()) {
            this.dirtyChunks.forEach($$0 -> this.writeChunk(new ChunkPos($$0)));
            this.dirtyChunks.clear();
        }
    }

    public boolean hasWork() {
        return !this.dirtyChunks.isEmpty();
    }

    @Nullable
    protected Optional<R> get(long $$0) {
        return (Optional)this.storage.get($$0);
    }

    protected Optional<R> getOrLoad(long $$0) {
        if (this.outsideStoredRange($$0)) {
            return Optional.empty();
        }
        Optional<R> $$1 = this.get($$0);
        if ($$1 != null) {
            return $$1;
        }
        this.unpackChunk(SectionPos.of($$0).chunk());
        $$1 = this.get($$0);
        if ($$1 == null) {
            throw Util.pauseInIde(new IllegalStateException());
        }
        return $$1;
    }

    protected boolean outsideStoredRange(long $$0) {
        int $$1 = SectionPos.sectionToBlockCoord(SectionPos.y($$0));
        return this.levelHeightAccessor.isOutsideBuildHeight($$1);
    }

    protected R getOrCreate(long $$0) {
        if (this.outsideStoredRange($$0)) {
            throw Util.pauseInIde(new IllegalArgumentException("sectionPos out of bounds"));
        }
        Optional<R> $$1 = this.getOrLoad($$0);
        if ($$1.isPresent()) {
            return $$1.get();
        }
        R $$2 = this.factory.apply(() -> this.setDirty($$0));
        this.storage.put($$0, Optional.of($$2));
        return $$2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompletableFuture<?> prefetch(ChunkPos $$0) {
        Object object = this.loadLock;
        synchronized (object) {
            long $$12 = $$0.toLong();
            if (this.loadedChunks.contains($$12)) {
                return CompletableFuture.completedFuture(null);
            }
            return (CompletableFuture)this.pendingLoads.computeIfAbsent($$12, $$1 -> this.tryRead($$0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    private void unpackChunk(ChunkPos $$0) {
        void $$3;
        long $$12 = $$0.toLong();
        Object object = this.loadLock;
        synchronized (object) {
            if (!this.loadedChunks.add($$12)) {
                return;
            }
            CompletableFuture $$2 = (CompletableFuture)this.pendingLoads.computeIfAbsent($$12, $$1 -> this.tryRead($$0));
        }
        this.unpackChunk($$0, ((Optional)$$3.join()).orElse(null));
        object = this.loadLock;
        synchronized (object) {
            this.pendingLoads.remove($$12);
        }
    }

    private CompletableFuture<Optional<PackedChunk<P>>> tryRead(ChunkPos $$0) {
        RegistryOps<Tag> $$13 = this.registryAccess.createSerializationContext(NbtOps.INSTANCE);
        return ((CompletableFuture)this.simpleRegionStorage.read($$0).thenApplyAsync($$12 -> $$12.map($$1 -> PackedChunk.parse(this.codec, $$13, $$1, this.simpleRegionStorage, this.levelHeightAccessor)), Util.backgroundExecutor().forName("parseSection"))).exceptionally($$1 -> {
            if ($$1 instanceof CompletionException) {
                $$1 = $$1.getCause();
            }
            if ($$1 instanceof IOException) {
                IOException $$2 = (IOException)$$1;
                LOGGER.error("Error reading chunk {} data from disk", (Object)$$0, (Object)$$2);
                this.errorReporter.reportChunkLoadFailure($$2, this.simpleRegionStorage.storageInfo(), $$0);
                return Optional.empty();
            }
            throw new CompletionException((Throwable)$$1);
        });
    }

    private void unpackChunk(ChunkPos $$0, @Nullable PackedChunk<P> $$12) {
        if ($$12 == null) {
            for (int $$22 = this.levelHeightAccessor.getMinSectionY(); $$22 <= this.levelHeightAccessor.getMaxSectionY(); ++$$22) {
                this.storage.put(SectionStorage.getKey($$0, $$22), Optional.empty());
            }
        } else {
            boolean $$3 = $$12.versionChanged();
            for (int $$4 = this.levelHeightAccessor.getMinSectionY(); $$4 <= this.levelHeightAccessor.getMaxSectionY(); ++$$4) {
                long $$5 = SectionStorage.getKey($$0, $$4);
                Optional<Object> $$6 = Optional.ofNullable($$12.sectionsByY.get($$4)).map($$1 -> this.unpacker.apply($$1, () -> this.setDirty($$5)));
                this.storage.put($$5, $$6);
                $$6.ifPresent($$2 -> {
                    this.onSectionLoad($$5);
                    if ($$3) {
                        this.setDirty($$5);
                    }
                });
            }
        }
    }

    private void writeChunk(ChunkPos $$0) {
        RegistryOps<Tag> $$12 = this.registryAccess.createSerializationContext(NbtOps.INSTANCE);
        Dynamic<Tag> $$2 = this.writeChunk($$0, $$12);
        Tag $$3 = (Tag)$$2.getValue();
        if ($$3 instanceof CompoundTag) {
            this.simpleRegionStorage.write($$0, (CompoundTag)$$3).exceptionally($$1 -> {
                this.errorReporter.reportChunkSaveFailure((Throwable)$$1, this.simpleRegionStorage.storageInfo(), $$0);
                return null;
            });
        } else {
            LOGGER.error("Expected compound tag, got {}", (Object)$$3);
        }
    }

    private <T> Dynamic<T> writeChunk(ChunkPos $$0, DynamicOps<T> $$1) {
        HashMap $$2 = Maps.newHashMap();
        for (int $$32 = this.levelHeightAccessor.getMinSectionY(); $$32 <= this.levelHeightAccessor.getMaxSectionY(); ++$$32) {
            long $$4 = SectionStorage.getKey($$0, $$32);
            Optional $$5 = (Optional)this.storage.get($$4);
            if ($$5 == null || $$5.isEmpty()) continue;
            DataResult $$6 = this.codec.encodeStart($$1, this.packer.apply($$5.get()));
            String $$7 = Integer.toString($$32);
            $$6.resultOrPartial(LOGGER::error).ifPresent($$3 -> $$2.put($$1.createString($$7), $$3));
        }
        return new Dynamic($$1, $$1.createMap(ImmutableMap.of($$1.createString(SECTIONS_TAG), $$1.createMap($$2), $$1.createString("DataVersion"), $$1.createInt(SharedConstants.getCurrentVersion().dataVersion().version()))));
    }

    private static long getKey(ChunkPos $$0, int $$1) {
        return SectionPos.asLong($$0.x, $$1, $$0.z);
    }

    protected void onSectionLoad(long $$0) {
    }

    protected void setDirty(long $$0) {
        Optional $$1 = (Optional)this.storage.get($$0);
        if ($$1 == null || $$1.isEmpty()) {
            LOGGER.warn("No data for position: {}", (Object)SectionPos.of($$0));
            return;
        }
        this.dirtyChunks.add(ChunkPos.asLong(SectionPos.x($$0), SectionPos.z($$0)));
    }

    static int getVersion(Dynamic<?> $$0) {
        return $$0.get("DataVersion").asInt(1945);
    }

    public void flush(ChunkPos $$0) {
        if (this.dirtyChunks.remove($$0.toLong())) {
            this.writeChunk($$0);
        }
    }

    @Override
    public void close() throws IOException {
        this.simpleRegionStorage.close();
    }

    static final class PackedChunk<T>
    extends Record {
        final Int2ObjectMap<T> sectionsByY;
        private final boolean versionChanged;

        private PackedChunk(Int2ObjectMap<T> $$0, boolean $$1) {
            this.sectionsByY = $$0;
            this.versionChanged = $$1;
        }

        public static <T> PackedChunk<T> parse(Codec<T> $$0, DynamicOps<Tag> $$12, Tag $$2, SimpleRegionStorage $$3, LevelHeightAccessor $$4) {
            int $$7;
            Dynamic $$5 = new Dynamic($$12, (Object)$$2);
            int $$6 = SectionStorage.getVersion($$5);
            boolean $$8 = $$6 != ($$7 = SharedConstants.getCurrentVersion().dataVersion().version());
            Dynamic<Tag> $$9 = $$3.upgradeChunkTag((Dynamic<Tag>)$$5, $$6);
            OptionalDynamic $$10 = $$9.get(SectionStorage.SECTIONS_TAG);
            Int2ObjectOpenHashMap $$11 = new Int2ObjectOpenHashMap();
            for (int $$122 = $$4.getMinSectionY(); $$122 <= $$4.getMaxSectionY(); ++$$122) {
                Optional $$13 = $$10.get(Integer.toString($$122)).result().flatMap($$1 -> $$0.parse($$1).resultOrPartial(LOGGER::error));
                if (!$$13.isPresent()) continue;
                $$11.put($$122, $$13.get());
            }
            return new PackedChunk<T>($$11, $$8);
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{PackedChunk.class, "sectionsByY;versionChanged", "sectionsByY", "versionChanged"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PackedChunk.class, "sectionsByY;versionChanged", "sectionsByY", "versionChanged"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PackedChunk.class, "sectionsByY;versionChanged", "sectionsByY", "versionChanged"}, this, $$0);
        }

        public Int2ObjectMap<T> sectionsByY() {
            return this.sectionsByY;
        }

        public boolean versionChanged() {
            return this.versionChanged;
        }
    }
}

