/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 */
package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.slf4j.Logger;

public class ClientChunkCache
extends ChunkSource {
    static final Logger LOGGER = LogUtils.getLogger();
    private final LevelChunk emptyChunk;
    private final LevelLightEngine lightEngine;
    volatile Storage storage;
    final ClientLevel level;

    public ClientChunkCache(ClientLevel $$0, int $$1) {
        this.level = $$0;
        this.emptyChunk = new EmptyLevelChunk($$0, new ChunkPos(0, 0), $$0.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS));
        this.lightEngine = new LevelLightEngine(this, true, $$0.dimensionType().hasSkyLight());
        this.storage = new Storage(ClientChunkCache.calculateStorageRange($$1));
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.lightEngine;
    }

    private static boolean isValidChunk(@Nullable LevelChunk $$0, int $$1, int $$2) {
        if ($$0 == null) {
            return false;
        }
        ChunkPos $$3 = $$0.getPos();
        return $$3.x == $$1 && $$3.z == $$2;
    }

    public void drop(ChunkPos $$0) {
        if (!this.storage.inRange($$0.x, $$0.z)) {
            return;
        }
        int $$1 = this.storage.getIndex($$0.x, $$0.z);
        LevelChunk $$2 = this.storage.getChunk($$1);
        if (ClientChunkCache.isValidChunk($$2, $$0.x, $$0.z)) {
            this.storage.drop($$1, $$2);
        }
    }

    @Override
    @Nullable
    public LevelChunk getChunk(int $$0, int $$1, ChunkStatus $$2, boolean $$3) {
        LevelChunk $$4;
        if (this.storage.inRange($$0, $$1) && ClientChunkCache.isValidChunk($$4 = this.storage.getChunk(this.storage.getIndex($$0, $$1)), $$0, $$1)) {
            return $$4;
        }
        if ($$3) {
            return this.emptyChunk;
        }
        return null;
    }

    @Override
    public BlockGetter getLevel() {
        return this.level;
    }

    public void replaceBiomes(int $$0, int $$1, FriendlyByteBuf $$2) {
        if (!this.storage.inRange($$0, $$1)) {
            LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", (Object)$$0, (Object)$$1);
            return;
        }
        int $$3 = this.storage.getIndex($$0, $$1);
        LevelChunk $$4 = this.storage.chunks.get($$3);
        if (!ClientChunkCache.isValidChunk($$4, $$0, $$1)) {
            LOGGER.warn("Ignoring chunk since it's not present: {}, {}", (Object)$$0, (Object)$$1);
        } else {
            $$4.replaceBiomes($$2);
        }
    }

    @Nullable
    public LevelChunk replaceWithPacketData(int $$0, int $$1, FriendlyByteBuf $$2, Map<Heightmap.Types, long[]> $$3, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> $$4) {
        if (!this.storage.inRange($$0, $$1)) {
            LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", (Object)$$0, (Object)$$1);
            return null;
        }
        int $$5 = this.storage.getIndex($$0, $$1);
        LevelChunk $$6 = this.storage.chunks.get($$5);
        ChunkPos $$7 = new ChunkPos($$0, $$1);
        if (!ClientChunkCache.isValidChunk($$6, $$0, $$1)) {
            $$6 = new LevelChunk(this.level, $$7);
            $$6.replaceWithPacketData($$2, $$3, $$4);
            this.storage.replace($$5, $$6);
        } else {
            $$6.replaceWithPacketData($$2, $$3, $$4);
            this.storage.refreshEmptySections($$6);
        }
        this.level.onChunkLoaded($$7);
        return $$6;
    }

    @Override
    public void tick(BooleanSupplier $$0, boolean $$1) {
    }

    public void updateViewCenter(int $$0, int $$1) {
        this.storage.viewCenterX = $$0;
        this.storage.viewCenterZ = $$1;
    }

    public void updateViewRadius(int $$0) {
        int $$1 = this.storage.chunkRadius;
        int $$2 = ClientChunkCache.calculateStorageRange($$0);
        if ($$1 != $$2) {
            Storage $$3 = new Storage($$2);
            $$3.viewCenterX = this.storage.viewCenterX;
            $$3.viewCenterZ = this.storage.viewCenterZ;
            for (int $$4 = 0; $$4 < this.storage.chunks.length(); ++$$4) {
                LevelChunk $$5 = this.storage.chunks.get($$4);
                if ($$5 == null) continue;
                ChunkPos $$6 = $$5.getPos();
                if (!$$3.inRange($$6.x, $$6.z)) continue;
                $$3.replace($$3.getIndex($$6.x, $$6.z), $$5);
            }
            this.storage = $$3;
        }
    }

    private static int calculateStorageRange(int $$0) {
        return Math.max(2, $$0) + 3;
    }

    @Override
    public String gatherStats() {
        return this.storage.chunks.length() + ", " + this.getLoadedChunksCount();
    }

    @Override
    public int getLoadedChunksCount() {
        return this.storage.chunkCount;
    }

    @Override
    public void onLightUpdate(LightLayer $$0, SectionPos $$1) {
        Minecraft.getInstance().levelRenderer.setSectionDirty($$1.x(), $$1.y(), $$1.z());
    }

    public LongOpenHashSet getLoadedEmptySections() {
        return this.storage.loadedEmptySections;
    }

    @Override
    public void onSectionEmptinessChanged(int $$0, int $$1, int $$2, boolean $$3) {
        this.storage.onSectionEmptinessChanged($$0, $$1, $$2, $$3);
    }

    @Override
    @Nullable
    public /* synthetic */ ChunkAccess getChunk(int n, int n2, ChunkStatus chunkStatus, boolean bl) {
        return this.getChunk(n, n2, chunkStatus, bl);
    }

    final class Storage {
        final AtomicReferenceArray<LevelChunk> chunks;
        final LongOpenHashSet loadedEmptySections = new LongOpenHashSet();
        final int chunkRadius;
        private final int viewRange;
        volatile int viewCenterX;
        volatile int viewCenterZ;
        int chunkCount;

        Storage(int $$0) {
            this.chunkRadius = $$0;
            this.viewRange = $$0 * 2 + 1;
            this.chunks = new AtomicReferenceArray(this.viewRange * this.viewRange);
        }

        int getIndex(int $$0, int $$1) {
            return Math.floorMod($$1, this.viewRange) * this.viewRange + Math.floorMod($$0, this.viewRange);
        }

        void replace(int $$0, @Nullable LevelChunk $$1) {
            LevelChunk $$2 = this.chunks.getAndSet($$0, $$1);
            if ($$2 != null) {
                --this.chunkCount;
                this.dropEmptySections($$2);
                ClientChunkCache.this.level.unload($$2);
            }
            if ($$1 != null) {
                ++this.chunkCount;
                this.addEmptySections($$1);
            }
        }

        void drop(int $$0, LevelChunk $$1) {
            if (this.chunks.compareAndSet($$0, $$1, null)) {
                --this.chunkCount;
                this.dropEmptySections($$1);
            }
            ClientChunkCache.this.level.unload($$1);
        }

        public void onSectionEmptinessChanged(int $$0, int $$1, int $$2, boolean $$3) {
            if (!this.inRange($$0, $$2)) {
                return;
            }
            long $$4 = SectionPos.asLong($$0, $$1, $$2);
            if ($$3) {
                this.loadedEmptySections.add($$4);
            } else if (this.loadedEmptySections.remove($$4)) {
                ClientChunkCache.this.level.onSectionBecomingNonEmpty($$4);
            }
        }

        private void dropEmptySections(LevelChunk $$0) {
            LevelChunkSection[] $$1 = $$0.d();
            for (int $$2 = 0; $$2 < $$1.length; ++$$2) {
                ChunkPos $$3 = $$0.getPos();
                this.loadedEmptySections.remove(SectionPos.asLong($$3.x, $$0.getSectionYFromSectionIndex($$2), $$3.z));
            }
        }

        private void addEmptySections(LevelChunk $$0) {
            LevelChunkSection[] $$1 = $$0.d();
            for (int $$2 = 0; $$2 < $$1.length; ++$$2) {
                LevelChunkSection $$3 = $$1[$$2];
                if (!$$3.hasOnlyAir()) continue;
                ChunkPos $$4 = $$0.getPos();
                this.loadedEmptySections.add(SectionPos.asLong($$4.x, $$0.getSectionYFromSectionIndex($$2), $$4.z));
            }
        }

        void refreshEmptySections(LevelChunk $$0) {
            ChunkPos $$1 = $$0.getPos();
            LevelChunkSection[] $$2 = $$0.d();
            for (int $$3 = 0; $$3 < $$2.length; ++$$3) {
                LevelChunkSection $$4 = $$2[$$3];
                long $$5 = SectionPos.asLong($$1.x, $$0.getSectionYFromSectionIndex($$3), $$1.z);
                if ($$4.hasOnlyAir()) {
                    this.loadedEmptySections.add($$5);
                    continue;
                }
                if (!this.loadedEmptySections.remove($$5)) continue;
                ClientChunkCache.this.level.onSectionBecomingNonEmpty($$5);
            }
        }

        boolean inRange(int $$0, int $$1) {
            return Math.abs($$0 - this.viewCenterX) <= this.chunkRadius && Math.abs($$1 - this.viewCenterZ) <= this.chunkRadius;
        }

        @Nullable
        protected LevelChunk getChunk(int $$0) {
            return this.chunks.get($$0);
        }

        private void dumpChunks(String $$0) {
            try (FileOutputStream $$1 = new FileOutputStream($$0);){
                int $$2 = ClientChunkCache.this.storage.chunkRadius;
                for (int $$3 = this.viewCenterZ - $$2; $$3 <= this.viewCenterZ + $$2; ++$$3) {
                    for (int $$4 = this.viewCenterX - $$2; $$4 <= this.viewCenterX + $$2; ++$$4) {
                        LevelChunk $$5 = ClientChunkCache.this.storage.chunks.get(ClientChunkCache.this.storage.getIndex($$4, $$3));
                        if ($$5 == null) continue;
                        ChunkPos $$6 = $$5.getPos();
                        $$1.write(($$6.x + "\t" + $$6.z + "\t" + $$5.isEmpty() + "\n").getBytes(StandardCharsets.UTF_8));
                    }
                }
            } catch (IOException $$7) {
                LOGGER.error("Failed to dump chunks to file {}", (Object)$$0, (Object)$$7);
            }
        }
    }
}

