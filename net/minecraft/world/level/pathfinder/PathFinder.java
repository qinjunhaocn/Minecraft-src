/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.pathfinder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.BinaryHeap;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;

public class PathFinder {
    private static final float FUDGING = 1.5f;
    private final Node[] neighbors = new Node[32];
    private int maxVisitedNodes;
    private final NodeEvaluator nodeEvaluator;
    private static final boolean DEBUG = false;
    private final BinaryHeap openSet = new BinaryHeap();

    public PathFinder(NodeEvaluator $$0, int $$1) {
        this.nodeEvaluator = $$0;
        this.maxVisitedNodes = $$1;
    }

    public void setMaxVisitedNodes(int $$0) {
        this.maxVisitedNodes = $$0;
    }

    @Nullable
    public Path findPath(PathNavigationRegion $$02, Mob $$1, Set<BlockPos> $$2, float $$3, int $$4, float $$5) {
        this.openSet.clear();
        this.nodeEvaluator.prepare($$02, $$1);
        Node $$6 = this.nodeEvaluator.getStart();
        if ($$6 == null) {
            return null;
        }
        Map<Target, BlockPos> $$7 = $$2.stream().collect(Collectors.toMap($$0 -> this.nodeEvaluator.getTarget($$0.getX(), $$0.getY(), $$0.getZ()), Function.identity()));
        Path $$8 = this.findPath($$6, $$7, $$3, $$4, $$5);
        this.nodeEvaluator.done();
        return $$8;
    }

    @Nullable
    private Path findPath(Node $$0, Map<Target, BlockPos> $$12, float $$2, int $$3, float $$4) {
        ProfilerFiller $$5 = Profiler.get();
        $$5.push("find_path");
        $$5.markForCharting(MetricCategory.PATH_FINDING);
        Set<Target> $$6 = $$12.keySet();
        $$0.g = 0.0f;
        $$0.f = $$0.h = this.getBestH($$0, $$6);
        this.openSet.clear();
        this.openSet.insert($$0);
        ImmutableSet $$7 = ImmutableSet.of();
        int $$8 = 0;
        HashSet<Target> $$9 = Sets.newHashSetWithExpectedSize($$6.size());
        int $$10 = (int)((float)this.maxVisitedNodes * $$4);
        while (!this.openSet.isEmpty() && ++$$8 < $$10) {
            Node $$11 = this.openSet.pop();
            $$11.closed = true;
            for (Target $$122 : $$6) {
                if (!($$11.distanceManhattan($$122) <= (float)$$3)) continue;
                $$122.setReached();
                $$9.add($$122);
            }
            if (!$$9.isEmpty()) break;
            if ($$11.distanceTo($$0) >= $$2) continue;
            int $$13 = this.nodeEvaluator.a(this.neighbors, $$11);
            for (int $$14 = 0; $$14 < $$13; ++$$14) {
                Node $$15 = this.neighbors[$$14];
                float $$16 = this.distance($$11, $$15);
                $$15.walkedDistance = $$11.walkedDistance + $$16;
                float $$17 = $$11.g + $$16 + $$15.costMalus;
                if (!($$15.walkedDistance < $$2) || $$15.inOpenSet() && !($$17 < $$15.g)) continue;
                $$15.cameFrom = $$11;
                $$15.g = $$17;
                $$15.h = this.getBestH($$15, $$6) * 1.5f;
                if ($$15.inOpenSet()) {
                    this.openSet.changeCost($$15, $$15.g + $$15.h);
                    continue;
                }
                $$15.f = $$15.g + $$15.h;
                this.openSet.insert($$15);
            }
        }
        Optional<Path> $$18 = !$$9.isEmpty() ? $$9.stream().map($$1 -> this.reconstructPath($$1.getBestNode(), (BlockPos)$$12.get($$1), true)).min(Comparator.comparingInt(Path::getNodeCount)) : $$6.stream().map($$1 -> this.reconstructPath($$1.getBestNode(), (BlockPos)$$12.get($$1), false)).min(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getNodeCount));
        $$5.pop();
        if ($$18.isEmpty()) {
            return null;
        }
        Path $$19 = $$18.get();
        return $$19;
    }

    protected float distance(Node $$0, Node $$1) {
        return $$0.distanceTo($$1);
    }

    private float getBestH(Node $$0, Set<Target> $$1) {
        float $$2 = Float.MAX_VALUE;
        for (Target $$3 : $$1) {
            float $$4 = $$0.distanceTo($$3);
            $$3.updateBest($$4, $$0);
            $$2 = Math.min($$4, $$2);
        }
        return $$2;
    }

    private Path reconstructPath(Node $$0, BlockPos $$1, boolean $$2) {
        ArrayList<Node> $$3 = Lists.newArrayList();
        Node $$4 = $$0;
        $$3.add(0, $$4);
        while ($$4.cameFrom != null) {
            $$4 = $$4.cameFrom;
            $$3.add(0, $$4);
        }
        return new Path($$3, $$1, $$2);
    }

    private static /* synthetic */ Node[] b(int $$0) {
        return new Node[$$0];
    }
}

