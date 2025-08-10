/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;

public abstract class ChunkSource
implements LightChunkGetter,
AutoCloseable {
    @Nullable
    public LevelChunk getChunk(int $$0, int $$1, boolean $$2) {
        return (LevelChunk)this.getChunk($$0, $$1, ChunkStatus.FULL, $$2);
    }

    @Nullable
    public LevelChunk getChunkNow(int $$0, int $$1) {
        return this.getChunk($$0, $$1, false);
    }

    @Override
    @Nullable
    public LightChunk getChunkForLighting(int $$0, int $$1) {
        return this.getChunk($$0, $$1, ChunkStatus.EMPTY, false);
    }

    public boolean hasChunk(int $$0, int $$1) {
        return this.getChunk($$0, $$1, ChunkStatus.FULL, false) != null;
    }

    @Nullable
    public abstract ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    public abstract void tick(BooleanSupplier var1, boolean var2);

    public void onSectionEmptinessChanged(int $$0, int $$1, int $$2, boolean $$3) {
    }

    public abstract String gatherStats();

    public abstract int getLoadedChunksCount();

    @Override
    public void close() throws IOException {
    }

    public abstract LevelLightEngine getLightEngine();

    public void setSpawnSettings(boolean $$0) {
    }

    public boolean updateChunkForced(ChunkPos $$0, boolean $$1) {
        return false;
    }

    public LongSet getForceLoadedChunks() {
        return LongSet.of();
    }
}

