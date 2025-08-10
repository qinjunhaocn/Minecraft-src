/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.level.progress;

import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.thread.ConsecutiveExecutor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class ProcessorChunkProgressListener
implements ChunkProgressListener {
    private final ChunkProgressListener delegate;
    private final ConsecutiveExecutor consecutiveExecutor;
    private boolean started;

    private ProcessorChunkProgressListener(ChunkProgressListener $$0, Executor $$1) {
        this.delegate = $$0;
        this.consecutiveExecutor = new ConsecutiveExecutor($$1, "progressListener");
    }

    public static ProcessorChunkProgressListener createStarted(ChunkProgressListener $$0, Executor $$1) {
        ProcessorChunkProgressListener $$2 = new ProcessorChunkProgressListener($$0, $$1);
        $$2.start();
        return $$2;
    }

    @Override
    public void updateSpawnPos(ChunkPos $$0) {
        this.consecutiveExecutor.schedule(() -> this.delegate.updateSpawnPos($$0));
    }

    @Override
    public void onStatusChange(ChunkPos $$0, @Nullable ChunkStatus $$1) {
        if (this.started) {
            this.consecutiveExecutor.schedule(() -> this.delegate.onStatusChange($$0, $$1));
        }
    }

    @Override
    public void start() {
        this.started = true;
        this.consecutiveExecutor.schedule(this.delegate::start);
    }

    @Override
    public void stop() {
        this.started = false;
        this.consecutiveExecutor.schedule(this.delegate::stop);
    }
}

