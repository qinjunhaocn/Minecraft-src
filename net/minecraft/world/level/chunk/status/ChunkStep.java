/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.chunk.status;

import com.google.common.collect.ImmutableList;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkDependencies;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStatusTask;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.WorldGenContext;

public final class ChunkStep
extends Record {
    final ChunkStatus targetStatus;
    private final ChunkDependencies directDependencies;
    final ChunkDependencies accumulatedDependencies;
    private final int blockStateWriteRadius;
    private final ChunkStatusTask task;

    public ChunkStep(ChunkStatus $$0, ChunkDependencies $$1, ChunkDependencies $$2, int $$3, ChunkStatusTask $$4) {
        this.targetStatus = $$0;
        this.directDependencies = $$1;
        this.accumulatedDependencies = $$2;
        this.blockStateWriteRadius = $$3;
        this.task = $$4;
    }

    public int getAccumulatedRadiusOf(ChunkStatus $$0) {
        if ($$0 == this.targetStatus) {
            return 0;
        }
        return this.accumulatedDependencies.getRadiusOf($$0);
    }

    public CompletableFuture<ChunkAccess> apply(WorldGenContext $$0, StaticCache2D<GenerationChunkHolder> $$12, ChunkAccess $$2) {
        if ($$2.getPersistedStatus().isBefore(this.targetStatus)) {
            ProfiledDuration $$3 = JvmProfiler.INSTANCE.onChunkGenerate($$2.getPos(), $$0.level().dimension(), this.targetStatus.getName());
            return this.task.doWork($$0, this, $$12, $$2).thenApply($$1 -> this.completeChunkGeneration((ChunkAccess)$$1, $$3));
        }
        return this.task.doWork($$0, this, $$12, $$2);
    }

    private ChunkAccess completeChunkGeneration(ChunkAccess $$0, @Nullable ProfiledDuration $$1) {
        ProtoChunk $$2;
        if ($$0 instanceof ProtoChunk && ($$2 = (ProtoChunk)$$0).getPersistedStatus().isBefore(this.targetStatus)) {
            $$2.setPersistedStatus(this.targetStatus);
        }
        if ($$1 != null) {
            $$1.finish(true);
        }
        return $$0;
    }

    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ChunkStep.class, "targetStatus;directDependencies;accumulatedDependencies;blockStateWriteRadius;task", "targetStatus", "directDependencies", "accumulatedDependencies", "blockStateWriteRadius", "task"}, this);
    }

    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ChunkStep.class, "targetStatus;directDependencies;accumulatedDependencies;blockStateWriteRadius;task", "targetStatus", "directDependencies", "accumulatedDependencies", "blockStateWriteRadius", "task"}, this);
    }

    public final boolean equals(Object $$0) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ChunkStep.class, "targetStatus;directDependencies;accumulatedDependencies;blockStateWriteRadius;task", "targetStatus", "directDependencies", "accumulatedDependencies", "blockStateWriteRadius", "task"}, this, $$0);
    }

    public ChunkStatus targetStatus() {
        return this.targetStatus;
    }

    public ChunkDependencies directDependencies() {
        return this.directDependencies;
    }

    public ChunkDependencies accumulatedDependencies() {
        return this.accumulatedDependencies;
    }

    public int blockStateWriteRadius() {
        return this.blockStateWriteRadius;
    }

    public ChunkStatusTask task() {
        return this.task;
    }

    public static class Builder {
        private final ChunkStatus status;
        @Nullable
        private final ChunkStep parent;
        private ChunkStatus[] directDependenciesByRadius;
        private int blockStateWriteRadius = -1;
        private ChunkStatusTask task = ChunkStatusTasks::passThrough;

        protected Builder(ChunkStatus $$0) {
            if ($$0.getParent() != $$0) {
                throw new IllegalArgumentException("Not starting with the first status: " + String.valueOf($$0));
            }
            this.status = $$0;
            this.parent = null;
            this.directDependenciesByRadius = new ChunkStatus[0];
        }

        protected Builder(ChunkStatus $$0, ChunkStep $$1) {
            if ($$1.targetStatus.getIndex() != $$0.getIndex() - 1) {
                throw new IllegalArgumentException("Out of order status: " + String.valueOf($$0));
            }
            this.status = $$0;
            this.parent = $$1;
            this.directDependenciesByRadius = new ChunkStatus[]{$$1.targetStatus};
        }

        public Builder addRequirement(ChunkStatus $$0, int $$1) {
            if ($$0.isOrAfter(this.status)) {
                throw new IllegalArgumentException("Status " + String.valueOf($$0) + " can not be required by " + String.valueOf(this.status));
            }
            int $$3 = $$1 + 1;
            ChunkStatus[] $$2 = this.directDependenciesByRadius;
            if ($$3 > $$2.length) {
                this.directDependenciesByRadius = new ChunkStatus[$$3];
                Arrays.fill(this.directDependenciesByRadius, $$0);
            }
            for (int $$4 = 0; $$4 < Math.min($$3, $$2.length); ++$$4) {
                this.directDependenciesByRadius[$$4] = ChunkStatus.max($$2[$$4], $$0);
            }
            return this;
        }

        public Builder blockStateWriteRadius(int $$0) {
            this.blockStateWriteRadius = $$0;
            return this;
        }

        public Builder setTask(ChunkStatusTask $$0) {
            this.task = $$0;
            return this;
        }

        public ChunkStep build() {
            return new ChunkStep(this.status, new ChunkDependencies(ImmutableList.copyOf(this.directDependenciesByRadius)), new ChunkDependencies(ImmutableList.copyOf(this.b())), this.blockStateWriteRadius, this.task);
        }

        private ChunkStatus[] b() {
            if (this.parent == null) {
                return this.directDependenciesByRadius;
            }
            int $$0 = this.getRadiusOfParent(this.parent.targetStatus);
            ChunkDependencies $$1 = this.parent.accumulatedDependencies;
            ChunkStatus[] $$2 = new ChunkStatus[Math.max($$0 + $$1.size(), this.directDependenciesByRadius.length)];
            for (int $$3 = 0; $$3 < $$2.length; ++$$3) {
                int $$4 = $$3 - $$0;
                $$2[$$3] = $$4 < 0 || $$4 >= $$1.size() ? this.directDependenciesByRadius[$$3] : ($$3 >= this.directDependenciesByRadius.length ? $$1.get($$4) : ChunkStatus.max(this.directDependenciesByRadius[$$3], $$1.get($$4)));
            }
            return $$2;
        }

        private int getRadiusOfParent(ChunkStatus $$0) {
            for (int $$1 = this.directDependenciesByRadius.length - 1; $$1 >= 0; --$$1) {
                if (!this.directDependenciesByRadius[$$1].isOrAfter($$0)) continue;
                return $$1;
            }
            return 0;
        }
    }
}

