/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.Octree;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;

public class SectionOcclusionGraph {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int MINIMUM_ADVANCED_CULLING_DISTANCE = 60;
    private static final int MINIMUM_ADVANCED_CULLING_SECTION_DISTANCE = SectionPos.blockToSectionCoord(60);
    private static final double CEILED_SECTION_DIAGONAL = Math.ceil(Math.sqrt(3.0) * 16.0);
    private boolean needsFullUpdate = true;
    @Nullable
    private Future<?> fullUpdateTask;
    @Nullable
    private ViewArea viewArea;
    private final AtomicReference<GraphState> currentGraph = new AtomicReference();
    private final AtomicReference<GraphEvents> nextGraphEvents = new AtomicReference();
    private final AtomicBoolean needsFrustumUpdate = new AtomicBoolean(false);

    public void waitAndReset(@Nullable ViewArea $$0) {
        if (this.fullUpdateTask != null) {
            try {
                this.fullUpdateTask.get();
                this.fullUpdateTask = null;
            } catch (Exception $$1) {
                LOGGER.warn("Full update failed", $$1);
            }
        }
        this.viewArea = $$0;
        if ($$0 != null) {
            this.currentGraph.set(new GraphState($$0));
            this.invalidate();
        } else {
            this.currentGraph.set(null);
        }
    }

    public void invalidate() {
        this.needsFullUpdate = true;
    }

    public void addSectionsInFrustum(Frustum $$0, List<SectionRenderDispatcher.RenderSection> $$1, List<SectionRenderDispatcher.RenderSection> $$22) {
        this.currentGraph.get().storage().sectionTree.visitNodes(($$2, $$3, $$4, $$5) -> {
            SectionRenderDispatcher.RenderSection $$6 = $$2.getSection();
            if ($$6 != null) {
                $$1.add($$6);
                if ($$5) {
                    $$22.add($$6);
                }
            }
        }, $$0, 32);
    }

    public boolean consumeFrustumUpdate() {
        return this.needsFrustumUpdate.compareAndSet(true, false);
    }

    public void onChunkReadyToRender(ChunkPos $$0) {
        GraphEvents $$2;
        GraphEvents $$1 = this.nextGraphEvents.get();
        if ($$1 != null) {
            this.addNeighbors($$1, $$0);
        }
        if (($$2 = this.currentGraph.get().events) != $$1) {
            this.addNeighbors($$2, $$0);
        }
    }

    public void schedulePropagationFrom(SectionRenderDispatcher.RenderSection $$0) {
        GraphEvents $$2;
        GraphEvents $$1 = this.nextGraphEvents.get();
        if ($$1 != null) {
            $$1.sectionsToPropagateFrom.add($$0);
        }
        if (($$2 = this.currentGraph.get().events) != $$1) {
            $$2.sectionsToPropagateFrom.add($$0);
        }
    }

    public void update(boolean $$0, Camera $$1, Frustum $$2, List<SectionRenderDispatcher.RenderSection> $$3, LongOpenHashSet $$4) {
        Vec3 $$5 = $$1.getPosition();
        if (this.needsFullUpdate && (this.fullUpdateTask == null || this.fullUpdateTask.isDone())) {
            this.scheduleFullUpdate($$0, $$1, $$5, $$4);
        }
        this.runPartialUpdate($$0, $$2, $$3, $$5, $$4);
    }

    private void scheduleFullUpdate(boolean $$0, Camera $$1, Vec3 $$2, LongOpenHashSet $$3) {
        this.needsFullUpdate = false;
        LongOpenHashSet $$4 = $$3.clone();
        this.fullUpdateTask = CompletableFuture.runAsync(() -> {
            GraphState $$4 = new GraphState(this.viewArea);
            this.nextGraphEvents.set($$4.events);
            ArrayDeque<Node> $$5 = Queues.newArrayDeque();
            this.initializeQueueForFullUpdate($$1, $$5);
            $$5.forEach($$1 -> $$0.storage.sectionToNodeMap.put($$1.section, (Node)$$1));
            this.runUpdates($$4.storage, $$2, $$5, $$0, $$0 -> {}, $$4);
            this.currentGraph.set($$4);
            this.nextGraphEvents.set(null);
            this.needsFrustumUpdate.set(true);
        }, Util.backgroundExecutor());
    }

    private void runPartialUpdate(boolean $$0, Frustum $$12, List<SectionRenderDispatcher.RenderSection> $$2, Vec3 $$3, LongOpenHashSet $$4) {
        GraphState $$5 = this.currentGraph.get();
        this.queueSectionsWithNewNeighbors($$5);
        if (!$$5.events.sectionsToPropagateFrom.isEmpty()) {
            ArrayDeque<Node> $$6 = Queues.newArrayDeque();
            while (!$$5.events.sectionsToPropagateFrom.isEmpty()) {
                SectionRenderDispatcher.RenderSection $$7 = (SectionRenderDispatcher.RenderSection)$$5.events.sectionsToPropagateFrom.poll();
                Node $$8 = $$5.storage.sectionToNodeMap.get($$7);
                if ($$8 == null || $$8.section != $$7) continue;
                $$6.add($$8);
            }
            Frustum $$9 = LevelRenderer.offsetFrustum($$12);
            Consumer<SectionRenderDispatcher.RenderSection> $$10 = $$1 -> {
                if ($$9.isVisible($$1.getBoundingBox())) {
                    this.needsFrustumUpdate.set(true);
                }
            };
            this.runUpdates($$5.storage, $$3, $$6, $$0, $$10, $$4);
        }
    }

    private void queueSectionsWithNewNeighbors(GraphState $$0) {
        LongIterator $$1 = $$0.events.chunksWhichReceivedNeighbors.iterator();
        while ($$1.hasNext()) {
            long $$2 = $$1.nextLong();
            List $$3 = (List)$$0.storage.chunksWaitingForNeighbors.get($$2);
            if ($$3 == null || !((SectionRenderDispatcher.RenderSection)$$3.get(0)).hasAllNeighbors()) continue;
            $$0.events.sectionsToPropagateFrom.addAll($$3);
            $$0.storage.chunksWaitingForNeighbors.remove($$2);
        }
        $$0.events.chunksWhichReceivedNeighbors.clear();
    }

    private void addNeighbors(GraphEvents $$0, ChunkPos $$1) {
        $$0.chunksWhichReceivedNeighbors.add(ChunkPos.asLong($$1.x - 1, $$1.z));
        $$0.chunksWhichReceivedNeighbors.add(ChunkPos.asLong($$1.x, $$1.z - 1));
        $$0.chunksWhichReceivedNeighbors.add(ChunkPos.asLong($$1.x + 1, $$1.z));
        $$0.chunksWhichReceivedNeighbors.add(ChunkPos.asLong($$1.x, $$1.z + 1));
        $$0.chunksWhichReceivedNeighbors.add(ChunkPos.asLong($$1.x - 1, $$1.z - 1));
        $$0.chunksWhichReceivedNeighbors.add(ChunkPos.asLong($$1.x - 1, $$1.z + 1));
        $$0.chunksWhichReceivedNeighbors.add(ChunkPos.asLong($$1.x + 1, $$1.z - 1));
        $$0.chunksWhichReceivedNeighbors.add(ChunkPos.asLong($$1.x + 1, $$1.z + 1));
    }

    private void initializeQueueForFullUpdate(Camera $$0, Queue<Node> $$12) {
        BlockPos $$2 = $$0.getBlockPosition();
        long $$3 = SectionPos.asLong($$2);
        int $$4 = SectionPos.y($$3);
        SectionRenderDispatcher.RenderSection $$5 = this.viewArea.getRenderSection($$3);
        if ($$5 == null) {
            LevelHeightAccessor $$6 = this.viewArea.getLevelHeightAccessor();
            boolean $$7 = $$4 < $$6.getMinSectionY();
            int $$8 = $$7 ? $$6.getMinSectionY() : $$6.getMaxSectionY();
            int $$9 = this.viewArea.getViewDistance();
            ArrayList<Node> $$10 = Lists.newArrayList();
            int $$11 = SectionPos.x($$3);
            int $$122 = SectionPos.z($$3);
            for (int $$13 = -$$9; $$13 <= $$9; ++$$13) {
                for (int $$14 = -$$9; $$14 <= $$9; ++$$14) {
                    SectionRenderDispatcher.RenderSection $$15 = this.viewArea.getRenderSection(SectionPos.asLong($$13 + $$11, $$8, $$14 + $$122));
                    if ($$15 == null || !this.isInViewDistance($$3, $$15.getSectionNode())) continue;
                    Direction $$16 = $$7 ? Direction.UP : Direction.DOWN;
                    Node $$17 = new Node($$15, $$16, 0);
                    $$17.setDirections($$17.directions, $$16);
                    if ($$13 > 0) {
                        $$17.setDirections($$17.directions, Direction.EAST);
                    } else if ($$13 < 0) {
                        $$17.setDirections($$17.directions, Direction.WEST);
                    }
                    if ($$14 > 0) {
                        $$17.setDirections($$17.directions, Direction.SOUTH);
                    } else if ($$14 < 0) {
                        $$17.setDirections($$17.directions, Direction.NORTH);
                    }
                    $$10.add($$17);
                }
            }
            $$10.sort(Comparator.comparingDouble($$1 -> $$2.distSqr(SectionPos.of($$1.section.getSectionNode()).center())));
            $$12.addAll($$10);
        } else {
            $$12.add(new Node($$5, null, 0));
        }
    }

    private void runUpdates(GraphStorage $$02, Vec3 $$1, Queue<Node> $$2, boolean $$3, Consumer<SectionRenderDispatcher.RenderSection> $$4, LongOpenHashSet $$5) {
        SectionPos $$6 = SectionPos.of($$1);
        long $$7 = $$6.asLong();
        BlockPos $$8 = $$6.center();
        while (!$$2.isEmpty()) {
            long $$11;
            Node $$9 = $$2.poll();
            SectionRenderDispatcher.RenderSection $$10 = $$9.section;
            if (!$$5.contains($$9.section.getSectionNode())) {
                if ($$02.sectionTree.add($$9.section)) {
                    $$4.accept($$9.section);
                }
            } else {
                $$9.section.sectionMesh.compareAndSet(CompiledSectionMesh.UNCOMPILED, CompiledSectionMesh.EMPTY);
            }
            boolean $$12 = Math.abs(SectionPos.x($$11 = $$10.getSectionNode()) - $$6.x()) > MINIMUM_ADVANCED_CULLING_SECTION_DISTANCE || Math.abs(SectionPos.y($$11) - $$6.y()) > MINIMUM_ADVANCED_CULLING_SECTION_DISTANCE || Math.abs(SectionPos.z($$11) - $$6.z()) > MINIMUM_ADVANCED_CULLING_SECTION_DISTANCE;
            for (Direction $$13 : DIRECTIONS) {
                Node $$29;
                SectionRenderDispatcher.RenderSection $$14 = this.getRelativeFrom($$7, $$10, $$13);
                if ($$14 == null || $$3 && $$9.hasDirection($$13.getOpposite())) continue;
                if ($$3 && $$9.hasSourceDirections()) {
                    SectionMesh $$15 = $$10.getSectionMesh();
                    boolean $$16 = false;
                    for (int $$17 = 0; $$17 < DIRECTIONS.length; ++$$17) {
                        if (!$$9.hasSourceDirection($$17) || !$$15.facesCanSeeEachother(DIRECTIONS[$$17].getOpposite(), $$13)) continue;
                        $$16 = true;
                        break;
                    }
                    if (!$$16) continue;
                }
                if ($$3 && $$12) {
                    boolean $$22;
                    boolean $$21;
                    int $$18 = SectionPos.sectionToBlockCoord(SectionPos.x($$11));
                    int $$19 = SectionPos.sectionToBlockCoord(SectionPos.y($$11));
                    int $$20 = SectionPos.sectionToBlockCoord(SectionPos.z($$11));
                    boolean bl = $$13.getAxis() == Direction.Axis.X ? $$8.getX() > $$18 : ($$21 = $$8.getX() < $$18);
                    boolean bl2 = $$13.getAxis() == Direction.Axis.Y ? $$8.getY() > $$19 : ($$22 = $$8.getY() < $$19);
                    boolean $$23 = $$13.getAxis() == Direction.Axis.Z ? $$8.getZ() > $$20 : $$8.getZ() < $$20;
                    Vector3d $$24 = new Vector3d((double)($$18 + ($$21 ? 16 : 0)), (double)($$19 + ($$22 ? 16 : 0)), (double)($$20 + ($$23 ? 16 : 0)));
                    Vector3d $$25 = new Vector3d($$1.x, $$1.y, $$1.z).sub((Vector3dc)$$24).normalize().mul(CEILED_SECTION_DIAGONAL);
                    boolean $$26 = true;
                    while ($$24.distanceSquared($$1.x, $$1.y, $$1.z) > 3600.0) {
                        $$24.add((Vector3dc)$$25);
                        LevelHeightAccessor $$27 = this.viewArea.getLevelHeightAccessor();
                        if ($$24.y > (double)$$27.getMaxY() || $$24.y < (double)$$27.getMinY()) break;
                        SectionRenderDispatcher.RenderSection $$28 = this.viewArea.getRenderSectionAt(BlockPos.containing($$24.x, $$24.y, $$24.z));
                        if ($$28 != null && $$02.sectionToNodeMap.get($$28) != null) continue;
                        $$26 = false;
                        break;
                    }
                    if (!$$26) continue;
                }
                if (($$29 = $$02.sectionToNodeMap.get($$14)) != null) {
                    $$29.addSourceDirection($$13);
                    continue;
                }
                Node $$30 = new Node($$14, $$13, $$9.step + 1);
                $$30.setDirections($$9.directions, $$13);
                if ($$14.hasAllNeighbors()) {
                    $$2.add($$30);
                    $$02.sectionToNodeMap.put($$14, $$30);
                    continue;
                }
                if (!this.isInViewDistance($$7, $$14.getSectionNode())) continue;
                $$02.sectionToNodeMap.put($$14, $$30);
                long $$31 = SectionPos.sectionToChunk($$14.getSectionNode());
                ((List)$$02.chunksWaitingForNeighbors.computeIfAbsent($$31, $$0 -> new ArrayList())).add($$14);
            }
        }
    }

    private boolean isInViewDistance(long $$0, long $$1) {
        return ChunkTrackingView.isInViewDistance(SectionPos.x($$0), SectionPos.z($$0), this.viewArea.getViewDistance(), SectionPos.x($$1), SectionPos.z($$1));
    }

    @Nullable
    private SectionRenderDispatcher.RenderSection getRelativeFrom(long $$0, SectionRenderDispatcher.RenderSection $$1, Direction $$2) {
        long $$3 = $$1.getNeighborSectionNode($$2);
        if (!this.isInViewDistance($$0, $$3)) {
            return null;
        }
        if (Mth.abs(SectionPos.y($$0) - SectionPos.y($$3)) > this.viewArea.getViewDistance()) {
            return null;
        }
        return this.viewArea.getRenderSection($$3);
    }

    @Nullable
    @VisibleForDebug
    public Node getNode(SectionRenderDispatcher.RenderSection $$0) {
        return this.currentGraph.get().storage.sectionToNodeMap.get($$0);
    }

    public Octree getOctree() {
        return this.currentGraph.get().storage.sectionTree;
    }

    static final class GraphState
    extends Record {
        final GraphStorage storage;
        final GraphEvents events;

        GraphState(ViewArea $$0) {
            this(new GraphStorage($$0), new GraphEvents());
        }

        private GraphState(GraphStorage $$0, GraphEvents $$1) {
            this.storage = $$0;
            this.events = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{GraphState.class, "storage;events", "storage", "events"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GraphState.class, "storage;events", "storage", "events"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GraphState.class, "storage;events", "storage", "events"}, this, $$0);
        }

        public GraphStorage storage() {
            return this.storage;
        }

        public GraphEvents events() {
            return this.events;
        }
    }

    static class GraphStorage {
        public final SectionToNodeMap sectionToNodeMap;
        public final Octree sectionTree;
        public final Long2ObjectMap<List<SectionRenderDispatcher.RenderSection>> chunksWaitingForNeighbors;

        public GraphStorage(ViewArea $$0) {
            this.sectionToNodeMap = new SectionToNodeMap($$0.sections.length);
            this.sectionTree = new Octree($$0.getCameraSectionPos(), $$0.getViewDistance(), $$0.sectionGridSizeY, $$0.level.getMinY());
            this.chunksWaitingForNeighbors = new Long2ObjectOpenHashMap();
        }
    }

    static final class GraphEvents
    extends Record {
        final LongSet chunksWhichReceivedNeighbors;
        final BlockingQueue<SectionRenderDispatcher.RenderSection> sectionsToPropagateFrom;

        GraphEvents() {
            this((LongSet)new LongOpenHashSet(), new LinkedBlockingQueue<SectionRenderDispatcher.RenderSection>());
        }

        private GraphEvents(LongSet $$0, BlockingQueue<SectionRenderDispatcher.RenderSection> $$1) {
            this.chunksWhichReceivedNeighbors = $$0;
            this.sectionsToPropagateFrom = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{GraphEvents.class, "chunksWhichReceivedNeighbors;sectionsToPropagateFrom", "chunksWhichReceivedNeighbors", "sectionsToPropagateFrom"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GraphEvents.class, "chunksWhichReceivedNeighbors;sectionsToPropagateFrom", "chunksWhichReceivedNeighbors", "sectionsToPropagateFrom"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GraphEvents.class, "chunksWhichReceivedNeighbors;sectionsToPropagateFrom", "chunksWhichReceivedNeighbors", "sectionsToPropagateFrom"}, this, $$0);
        }

        public LongSet chunksWhichReceivedNeighbors() {
            return this.chunksWhichReceivedNeighbors;
        }

        public BlockingQueue<SectionRenderDispatcher.RenderSection> sectionsToPropagateFrom() {
            return this.sectionsToPropagateFrom;
        }
    }

    static class SectionToNodeMap {
        private final Node[] nodes;

        SectionToNodeMap(int $$0) {
            this.nodes = new Node[$$0];
        }

        public void put(SectionRenderDispatcher.RenderSection $$0, Node $$1) {
            this.nodes[$$0.index] = $$1;
        }

        @Nullable
        public Node get(SectionRenderDispatcher.RenderSection $$0) {
            int $$1 = $$0.index;
            if ($$1 < 0 || $$1 >= this.nodes.length) {
                return null;
            }
            return this.nodes[$$1];
        }
    }

    @VisibleForDebug
    public static class Node {
        @VisibleForDebug
        protected final SectionRenderDispatcher.RenderSection section;
        private byte sourceDirections;
        byte directions;
        @VisibleForDebug
        public final int step;

        Node(SectionRenderDispatcher.RenderSection $$0, @Nullable Direction $$1, int $$2) {
            this.section = $$0;
            if ($$1 != null) {
                this.addSourceDirection($$1);
            }
            this.step = $$2;
        }

        void setDirections(byte $$0, Direction $$1) {
            this.directions = (byte)(this.directions | ($$0 | 1 << $$1.ordinal()));
        }

        boolean hasDirection(Direction $$0) {
            return (this.directions & 1 << $$0.ordinal()) > 0;
        }

        void addSourceDirection(Direction $$0) {
            this.sourceDirections = (byte)(this.sourceDirections | (this.sourceDirections | 1 << $$0.ordinal()));
        }

        @VisibleForDebug
        public boolean hasSourceDirection(int $$0) {
            return (this.sourceDirections & 1 << $$0) > 0;
        }

        boolean hasSourceDirections() {
            return this.sourceDirections != 0;
        }

        public int hashCode() {
            return Long.hashCode(this.section.getSectionNode());
        }

        public boolean equals(Object $$0) {
            if (!($$0 instanceof Node)) {
                return false;
            }
            Node $$1 = (Node)$$0;
            return this.section.getSectionNode() == $$1.section.getSectionNode();
        }
    }
}

