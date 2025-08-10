/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.server.level.progress;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.slf4j.Logger;

public class LoggerChunkProgressListener
implements ChunkProgressListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final int maxCount;
    private int count;
    private long startTime;
    private long nextTickTime = Long.MAX_VALUE;

    private LoggerChunkProgressListener(int $$0) {
        this.maxCount = $$0;
    }

    public static LoggerChunkProgressListener createFromGameruleRadius(int $$0) {
        return $$0 > 0 ? LoggerChunkProgressListener.create($$0 + 1) : LoggerChunkProgressListener.createCompleted();
    }

    public static LoggerChunkProgressListener create(int $$0) {
        int $$1 = ChunkProgressListener.calculateDiameter($$0);
        return new LoggerChunkProgressListener($$1 * $$1);
    }

    public static LoggerChunkProgressListener createCompleted() {
        return new LoggerChunkProgressListener(0);
    }

    @Override
    public void updateSpawnPos(ChunkPos $$0) {
        this.startTime = this.nextTickTime = Util.getMillis();
    }

    @Override
    public void onStatusChange(ChunkPos $$0, @Nullable ChunkStatus $$1) {
        if ($$1 == ChunkStatus.FULL) {
            ++this.count;
        }
        int $$2 = this.getProgress();
        if (Util.getMillis() > this.nextTickTime) {
            this.nextTickTime += 500L;
            LOGGER.info(Component.a("menu.preparingSpawn", Mth.clamp($$2, 0, 100)).getString());
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        LOGGER.info("Time elapsed: {} ms", (Object)(Util.getMillis() - this.startTime));
        this.nextTickTime = Long.MAX_VALUE;
    }

    public int getProgress() {
        if (this.maxCount == 0) {
            return 100;
        }
        return Mth.floor((float)this.count * 100.0f / (float)this.maxCount);
    }
}

