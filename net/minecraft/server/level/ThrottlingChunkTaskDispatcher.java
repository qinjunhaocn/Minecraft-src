/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.server.level.ChunkTaskDispatcher;
import net.minecraft.server.level.ChunkTaskPriorityQueue;
import net.minecraft.util.thread.TaskScheduler;
import net.minecraft.world.level.ChunkPos;

public class ThrottlingChunkTaskDispatcher
extends ChunkTaskDispatcher {
    private final LongSet chunkPositionsInExecution = new LongOpenHashSet();
    private final int maxChunksInExecution;
    private final String executorSchedulerName;

    public ThrottlingChunkTaskDispatcher(TaskScheduler<Runnable> $$0, Executor $$1, int $$2) {
        super($$0, $$1);
        this.maxChunksInExecution = $$2;
        this.executorSchedulerName = $$0.name();
    }

    @Override
    protected void onRelease(long $$0) {
        this.chunkPositionsInExecution.remove($$0);
    }

    @Override
    @Nullable
    protected ChunkTaskPriorityQueue.TasksForChunk popTasks() {
        return this.chunkPositionsInExecution.size() < this.maxChunksInExecution ? super.popTasks() : null;
    }

    @Override
    protected void scheduleForExecution(ChunkTaskPriorityQueue.TasksForChunk $$0) {
        this.chunkPositionsInExecution.add($$0.chunkPos());
        super.scheduleForExecution($$0);
    }

    @VisibleForTesting
    public String getDebugStatus() {
        return this.executorSchedulerName + "=[" + this.chunkPositionsInExecution.longStream().mapToObj($$0 -> $$0 + ":" + String.valueOf(new ChunkPos($$0))).collect(Collectors.joining(",")) + "], s=" + this.sleeping;
    }
}

