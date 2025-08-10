/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.TracingExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.SectionBufferBuilderPool;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.CompileTaskDynamicQueue;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.TranslucencyPointOfView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.util.thread.ConsecutiveExecutor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SectionRenderDispatcher {
    private final CompileTaskDynamicQueue compileQueue = new CompileTaskDynamicQueue();
    private final Queue<Runnable> toUpload = Queues.newConcurrentLinkedQueue();
    final Executor mainThreadUploadExecutor = this.toUpload::add;
    final Queue<SectionMesh> toClose = Queues.newConcurrentLinkedQueue();
    final SectionBufferBuilderPack fixedBuffers;
    private final SectionBufferBuilderPool bufferPool;
    volatile boolean closed;
    private final ConsecutiveExecutor consecutiveExecutor;
    private final TracingExecutor executor;
    ClientLevel level;
    final LevelRenderer renderer;
    Vec3 cameraPosition = Vec3.ZERO;
    final SectionCompiler sectionCompiler;

    public SectionRenderDispatcher(ClientLevel $$0, LevelRenderer $$1, TracingExecutor $$2, RenderBuffers $$3, BlockRenderDispatcher $$4, BlockEntityRenderDispatcher $$5) {
        this.level = $$0;
        this.renderer = $$1;
        this.fixedBuffers = $$3.fixedBufferPack();
        this.bufferPool = $$3.sectionBufferPool();
        this.executor = $$2;
        this.consecutiveExecutor = new ConsecutiveExecutor($$2, "Section Renderer");
        this.consecutiveExecutor.schedule(this::runTask);
        this.sectionCompiler = new SectionCompiler($$4, $$5);
    }

    public void setLevel(ClientLevel $$0) {
        this.level = $$0;
    }

    private void runTask() {
        if (this.closed || this.bufferPool.isEmpty()) {
            return;
        }
        RenderSection.CompileTask $$02 = this.compileQueue.poll(this.cameraPosition);
        if ($$02 == null) {
            return;
        }
        SectionBufferBuilderPack $$1 = Objects.requireNonNull(this.bufferPool.acquire());
        ((CompletableFuture)CompletableFuture.supplyAsync(() -> $$02.doTask($$1), this.executor.forName($$02.name())).thenCompose($$0 -> $$0)).whenComplete(($$2, $$3) -> {
            if ($$3 != null) {
                Minecraft.getInstance().delayCrash(CrashReport.forThrowable($$3, "Batching sections"));
                return;
            }
            $$0.isCompleted.set(true);
            this.consecutiveExecutor.schedule(() -> {
                if ($$2 == SectionTaskResult.SUCCESSFUL) {
                    $$1.clearAll();
                } else {
                    $$1.discardAll();
                }
                this.bufferPool.release($$1);
                this.runTask();
            });
        });
    }

    public void setCameraPosition(Vec3 $$0) {
        this.cameraPosition = $$0;
    }

    public void uploadAllPendingUploads() {
        SectionMesh $$1;
        Runnable $$0;
        while (($$0 = this.toUpload.poll()) != null) {
            $$0.run();
        }
        while (($$1 = this.toClose.poll()) != null) {
            $$1.close();
        }
    }

    public void rebuildSectionSync(RenderSection $$0, RenderRegionCache $$1) {
        $$0.compileSync($$1);
    }

    public void schedule(RenderSection.CompileTask $$0) {
        if (this.closed) {
            return;
        }
        this.consecutiveExecutor.schedule(() -> {
            if (this.closed) {
                return;
            }
            this.compileQueue.add($$0);
            this.runTask();
        });
    }

    public void clearCompileQueue() {
        this.compileQueue.clear();
    }

    public boolean isQueueEmpty() {
        return this.compileQueue.size() == 0 && this.toUpload.isEmpty();
    }

    public void dispose() {
        this.closed = true;
        this.clearCompileQueue();
        this.uploadAllPendingUploads();
    }

    @VisibleForDebug
    public String getStats() {
        return String.format(Locale.ROOT, "pC: %03d, pU: %02d, aB: %02d", this.compileQueue.size(), this.toUpload.size(), this.bufferPool.getFreeBufferCount());
    }

    @VisibleForDebug
    public int getCompileQueueSize() {
        return this.compileQueue.size();
    }

    @VisibleForDebug
    public int getToUpload() {
        return this.toUpload.size();
    }

    @VisibleForDebug
    public int getFreeBufferCount() {
        return this.bufferPool.getFreeBufferCount();
    }

    public class RenderSection {
        public static final int SIZE = 16;
        public final int index;
        public final AtomicReference<SectionMesh> sectionMesh = new AtomicReference<SectionMesh>(CompiledSectionMesh.UNCOMPILED);
        @Nullable
        private RebuildTask lastRebuildTask;
        @Nullable
        private ResortTransparencyTask lastResortTransparencyTask;
        private AABB bb;
        private boolean dirty = true;
        volatile long sectionNode = SectionPos.asLong(-1, -1, -1);
        final BlockPos.MutableBlockPos renderOrigin = new BlockPos.MutableBlockPos(-1, -1, -1);
        private boolean playerChanged;

        public RenderSection(int $$1, long $$2) {
            this.index = $$1;
            this.setSectionNode($$2);
        }

        private boolean doesChunkExistAt(long $$0) {
            ChunkAccess $$1 = SectionRenderDispatcher.this.level.getChunk(SectionPos.x($$0), SectionPos.z($$0), ChunkStatus.FULL, false);
            return $$1 != null && SectionRenderDispatcher.this.level.getLightEngine().lightOnInColumn(SectionPos.getZeroNode($$0));
        }

        public boolean hasAllNeighbors() {
            return this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.WEST)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.NORTH)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.EAST)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.SOUTH)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, -1, 0, -1)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, -1, 0, 1)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, 1, 0, -1)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, 1, 0, 1));
        }

        public AABB getBoundingBox() {
            return this.bb;
        }

        public CompletableFuture<Void> upload(Map<ChunkSectionLayer, MeshData> $$0, CompiledSectionMesh $$1) {
            if (SectionRenderDispatcher.this.closed) {
                $$0.values().forEach(MeshData::close);
                return CompletableFuture.completedFuture(null);
            }
            return CompletableFuture.runAsync(() -> $$0.forEach(($$1, $$2) -> {
                try (Zone $$3 = Profiler.get().zone("Upload Section Layer");){
                    $$1.uploadMeshLayer((ChunkSectionLayer)((Object)((Object)$$1)), (MeshData)$$2, this.sectionNode);
                    $$2.close();
                }
            }), SectionRenderDispatcher.this.mainThreadUploadExecutor);
        }

        public CompletableFuture<Void> uploadSectionIndexBuffer(CompiledSectionMesh $$0, ByteBufferBuilder.Result $$1, ChunkSectionLayer $$2) {
            if (SectionRenderDispatcher.this.closed) {
                $$1.close();
                return CompletableFuture.completedFuture(null);
            }
            return CompletableFuture.runAsync(() -> {
                try (Zone $$3 = Profiler.get().zone("Upload Section Indices");){
                    $$0.uploadLayerIndexBuffer($$2, $$1, this.sectionNode);
                    $$1.close();
                }
            }, SectionRenderDispatcher.this.mainThreadUploadExecutor);
        }

        public void setSectionNode(long $$0) {
            this.reset();
            this.sectionNode = $$0;
            int $$1 = SectionPos.sectionToBlockCoord(SectionPos.x($$0));
            int $$2 = SectionPos.sectionToBlockCoord(SectionPos.y($$0));
            int $$3 = SectionPos.sectionToBlockCoord(SectionPos.z($$0));
            this.renderOrigin.set($$1, $$2, $$3);
            this.bb = new AABB($$1, $$2, $$3, $$1 + 16, $$2 + 16, $$3 + 16);
        }

        public SectionMesh getSectionMesh() {
            return this.sectionMesh.get();
        }

        public void reset() {
            this.cancelTasks();
            this.sectionMesh.getAndSet(CompiledSectionMesh.UNCOMPILED).close();
            this.dirty = true;
        }

        public BlockPos getRenderOrigin() {
            return this.renderOrigin;
        }

        public long getSectionNode() {
            return this.sectionNode;
        }

        public void setDirty(boolean $$0) {
            boolean $$1 = this.dirty;
            this.dirty = true;
            this.playerChanged = $$0 | ($$1 && this.playerChanged);
        }

        public void setNotDirty() {
            this.dirty = false;
            this.playerChanged = false;
        }

        public boolean isDirty() {
            return this.dirty;
        }

        public boolean isDirtyFromPlayer() {
            return this.dirty && this.playerChanged;
        }

        public long getNeighborSectionNode(Direction $$0) {
            return SectionPos.offset(this.sectionNode, $$0);
        }

        public void resortTransparency(SectionRenderDispatcher $$0) {
            SectionMesh sectionMesh = this.getSectionMesh();
            if (sectionMesh instanceof CompiledSectionMesh) {
                CompiledSectionMesh $$1 = (CompiledSectionMesh)sectionMesh;
                this.lastResortTransparencyTask = new ResortTransparencyTask($$1);
                $$0.schedule(this.lastResortTransparencyTask);
            }
        }

        public boolean hasTranslucentGeometry() {
            return this.getSectionMesh().hasTranslucentGeometry();
        }

        public boolean transparencyResortingScheduled() {
            return this.lastResortTransparencyTask != null && !this.lastResortTransparencyTask.isCompleted.get();
        }

        protected void cancelTasks() {
            if (this.lastRebuildTask != null) {
                this.lastRebuildTask.cancel();
                this.lastRebuildTask = null;
            }
            if (this.lastResortTransparencyTask != null) {
                this.lastResortTransparencyTask.cancel();
                this.lastResortTransparencyTask = null;
            }
        }

        public CompileTask createCompileTask(RenderRegionCache $$0) {
            this.cancelTasks();
            RenderSectionRegion $$1 = $$0.createRegion(SectionRenderDispatcher.this.level, this.sectionNode);
            boolean $$2 = this.sectionMesh.get() != CompiledSectionMesh.UNCOMPILED;
            this.lastRebuildTask = new RebuildTask($$1, $$2);
            return this.lastRebuildTask;
        }

        public void rebuildSectionAsync(RenderRegionCache $$0) {
            CompileTask $$1 = this.createCompileTask($$0);
            SectionRenderDispatcher.this.schedule($$1);
        }

        public void compileSync(RenderRegionCache $$0) {
            CompileTask $$1 = this.createCompileTask($$0);
            $$1.doTask(SectionRenderDispatcher.this.fixedBuffers);
        }

        void setSectionMesh(SectionMesh $$0) {
            SectionMesh $$1 = this.sectionMesh.getAndSet($$0);
            SectionRenderDispatcher.this.toClose.add($$1);
            SectionRenderDispatcher.this.renderer.addRecentlyCompiledSection(this);
        }

        VertexSorting createVertexSorting(SectionPos $$0) {
            Vec3 $$1 = SectionRenderDispatcher.this.cameraPosition;
            return VertexSorting.byDistance((float)($$1.x - (double)$$0.minBlockX()), (float)($$1.y - (double)$$0.minBlockY()), (float)($$1.z - (double)$$0.minBlockZ()));
        }

        class ResortTransparencyTask
        extends CompileTask {
            private final CompiledSectionMesh compiledSectionMesh;

            public ResortTransparencyTask(CompiledSectionMesh $$0) {
                super(true);
                this.compiledSectionMesh = $$0;
            }

            @Override
            protected String name() {
                return "rend_chk_sort";
            }

            @Override
            public CompletableFuture<SectionTaskResult> doTask(SectionBufferBuilderPack $$0) {
                if (this.isCancelled.get()) {
                    return CompletableFuture.completedFuture(SectionTaskResult.CANCELLED);
                }
                MeshData.SortState $$12 = this.compiledSectionMesh.getTransparencyState();
                if ($$12 == null || this.compiledSectionMesh.isEmpty(ChunkSectionLayer.TRANSLUCENT)) {
                    return CompletableFuture.completedFuture(SectionTaskResult.CANCELLED);
                }
                long $$22 = RenderSection.this.sectionNode;
                VertexSorting $$3 = RenderSection.this.createVertexSorting(SectionPos.of($$22));
                TranslucencyPointOfView $$4 = TranslucencyPointOfView.of(SectionRenderDispatcher.this.cameraPosition, $$22);
                if (!this.compiledSectionMesh.isDifferentPointOfView($$4) && !$$4.isAxisAligned()) {
                    return CompletableFuture.completedFuture(SectionTaskResult.CANCELLED);
                }
                ByteBufferBuilder.Result $$5 = $$12.buildSortedIndexBuffer($$0.buffer(ChunkSectionLayer.TRANSLUCENT), $$3);
                if ($$5 == null) {
                    return CompletableFuture.completedFuture(SectionTaskResult.CANCELLED);
                }
                if (this.isCancelled.get()) {
                    $$5.close();
                    return CompletableFuture.completedFuture(SectionTaskResult.CANCELLED);
                }
                CompletableFuture<Void> $$6 = RenderSection.this.uploadSectionIndexBuffer(this.compiledSectionMesh, $$5, ChunkSectionLayer.TRANSLUCENT);
                return $$6.handle(($$1, $$2) -> {
                    if ($$2 != null && !($$2 instanceof CancellationException) && !($$2 instanceof InterruptedException)) {
                        Minecraft.getInstance().delayCrash(CrashReport.forThrowable($$2, "Rendering section"));
                    }
                    if (this.isCancelled.get()) {
                        return SectionTaskResult.CANCELLED;
                    }
                    this.compiledSectionMesh.setTranslucencyPointOfView($$4);
                    return SectionTaskResult.SUCCESSFUL;
                });
            }

            @Override
            public void cancel() {
                this.isCancelled.set(true);
            }
        }

        public abstract class CompileTask {
            protected final AtomicBoolean isCancelled = new AtomicBoolean(false);
            protected final AtomicBoolean isCompleted = new AtomicBoolean(false);
            protected final boolean isRecompile;

            public CompileTask(boolean $$1) {
                this.isRecompile = $$1;
            }

            public abstract CompletableFuture<SectionTaskResult> doTask(SectionBufferBuilderPack var1);

            public abstract void cancel();

            protected abstract String name();

            public boolean isRecompile() {
                return this.isRecompile;
            }

            public BlockPos getRenderOrigin() {
                return RenderSection.this.renderOrigin;
            }
        }

        class RebuildTask
        extends CompileTask {
            protected final RenderSectionRegion region;

            public RebuildTask(RenderSectionRegion $$0, boolean $$1) {
                super($$1);
                this.region = $$0;
            }

            @Override
            protected String name() {
                return "rend_chk_rebuild";
            }

            /*
             * WARNING - void declaration
             */
            @Override
            public CompletableFuture<SectionTaskResult> doTask(SectionBufferBuilderPack $$0) {
                void $$5;
                if (this.isCancelled.get()) {
                    return CompletableFuture.completedFuture(SectionTaskResult.CANCELLED);
                }
                long $$12 = RenderSection.this.sectionNode;
                SectionPos $$22 = SectionPos.of($$12);
                if (this.isCancelled.get()) {
                    return CompletableFuture.completedFuture(SectionTaskResult.CANCELLED);
                }
                try (Zone $$3 = Profiler.get().zone("Compile Section");){
                    SectionCompiler.Results $$4 = SectionRenderDispatcher.this.sectionCompiler.compile($$22, this.region, RenderSection.this.createVertexSorting($$22), $$0);
                }
                TranslucencyPointOfView $$6 = TranslucencyPointOfView.of(SectionRenderDispatcher.this.cameraPosition, $$12);
                if (this.isCancelled.get()) {
                    $$5.release();
                    return CompletableFuture.completedFuture(SectionTaskResult.CANCELLED);
                }
                CompiledSectionMesh $$7 = new CompiledSectionMesh($$6, (SectionCompiler.Results)$$5);
                CompletableFuture<Void> $$8 = RenderSection.this.upload($$5.renderedLayers, $$7);
                return $$8.handle(($$1, $$2) -> {
                    if ($$2 != null && !($$2 instanceof CancellationException) && !($$2 instanceof InterruptedException)) {
                        Minecraft.getInstance().delayCrash(CrashReport.forThrowable($$2, "Rendering section"));
                    }
                    if (this.isCancelled.get() || SectionRenderDispatcher.this.closed) {
                        SectionRenderDispatcher.this.toClose.add($$7);
                        return SectionTaskResult.CANCELLED;
                    }
                    RenderSection.this.setSectionMesh($$7);
                    return SectionTaskResult.SUCCESSFUL;
                });
            }

            @Override
            public void cancel() {
                if (this.isCancelled.compareAndSet(false, true)) {
                    RenderSection.this.setDirty(false);
                }
            }
        }
    }

    static final class SectionTaskResult
    extends Enum<SectionTaskResult> {
        public static final /* enum */ SectionTaskResult SUCCESSFUL = new SectionTaskResult();
        public static final /* enum */ SectionTaskResult CANCELLED = new SectionTaskResult();
        private static final /* synthetic */ SectionTaskResult[] $VALUES;

        public static SectionTaskResult[] values() {
            return (SectionTaskResult[])$VALUES.clone();
        }

        public static SectionTaskResult valueOf(String $$0) {
            return Enum.valueOf(SectionTaskResult.class, $$0);
        }

        private static /* synthetic */ SectionTaskResult[] a() {
            return new SectionTaskResult[]{SUCCESSFUL, CANCELLED};
        }

        static {
            $VALUES = SectionTaskResult.a();
        }
    }
}

