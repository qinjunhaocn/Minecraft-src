/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 */
package net.minecraft.server.level.progress;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class StoringChunkProgressListener
implements ChunkProgressListener {
    private final LoggerChunkProgressListener delegate;
    private final Long2ObjectOpenHashMap<ChunkStatus> statuses = new Long2ObjectOpenHashMap();
    private ChunkPos spawnPos = new ChunkPos(0, 0);
    private final int fullDiameter;
    private final int radius;
    private final int diameter;
    private boolean started;

    private StoringChunkProgressListener(LoggerChunkProgressListener $$0, int $$1, int $$2, int $$3) {
        this.delegate = $$0;
        this.fullDiameter = $$1;
        this.radius = $$2;
        this.diameter = $$3;
    }

    public static StoringChunkProgressListener createFromGameruleRadius(int $$0) {
        return $$0 > 0 ? StoringChunkProgressListener.create($$0 + 1) : StoringChunkProgressListener.createCompleted();
    }

    public static StoringChunkProgressListener create(int $$0) {
        LoggerChunkProgressListener $$1 = LoggerChunkProgressListener.create($$0);
        int $$2 = ChunkProgressListener.calculateDiameter($$0);
        int $$3 = $$0 + ChunkLevel.RADIUS_AROUND_FULL_CHUNK;
        int $$4 = ChunkProgressListener.calculateDiameter($$3);
        return new StoringChunkProgressListener($$1, $$2, $$3, $$4);
    }

    public static StoringChunkProgressListener createCompleted() {
        return new StoringChunkProgressListener(LoggerChunkProgressListener.createCompleted(), 0, 0, 0);
    }

    @Override
    public void updateSpawnPos(ChunkPos $$0) {
        if (!this.started) {
            return;
        }
        this.delegate.updateSpawnPos($$0);
        this.spawnPos = $$0;
    }

    @Override
    public void onStatusChange(ChunkPos $$0, @Nullable ChunkStatus $$1) {
        if (!this.started) {
            return;
        }
        this.delegate.onStatusChange($$0, $$1);
        if ($$1 == null) {
            this.statuses.remove($$0.toLong());
        } else {
            this.statuses.put($$0.toLong(), (Object)$$1);
        }
    }

    @Override
    public void start() {
        this.started = true;
        this.statuses.clear();
        this.delegate.start();
    }

    @Override
    public void stop() {
        this.started = false;
        this.delegate.stop();
    }

    public int getFullDiameter() {
        return this.fullDiameter;
    }

    public int getDiameter() {
        return this.diameter;
    }

    public int getProgress() {
        return this.delegate.getProgress();
    }

    @Nullable
    public ChunkStatus getStatus(int $$0, int $$1) {
        return (ChunkStatus)this.statuses.get(ChunkPos.asLong($$0 + this.spawnPos.x - this.radius, $$1 + this.spawnPos.z - this.radius));
    }
}

