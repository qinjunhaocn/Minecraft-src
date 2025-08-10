/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 */
package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;

public class FlyNodeEvaluator
extends WalkNodeEvaluator {
    private final Long2ObjectMap<PathType> pathTypeByPosCache = new Long2ObjectOpenHashMap();
    private static final float SMALL_MOB_SIZE = 1.0f;
    private static final float SMALL_MOB_INFLATED_START_NODE_BOUNDING_BOX = 1.1f;
    private static final int MAX_START_NODE_CANDIDATES = 10;

    @Override
    public void prepare(PathNavigationRegion $$0, Mob $$1) {
        super.prepare($$0, $$1);
        this.pathTypeByPosCache.clear();
        $$1.onPathfindingStart();
    }

    @Override
    public void done() {
        this.mob.onPathfindingDone();
        this.pathTypeByPosCache.clear();
        super.done();
    }

    @Override
    public Node getStart() {
        BlockPos $$4;
        int $$3;
        if (this.canFloat() && this.mob.isInWater()) {
            int $$0 = this.mob.getBlockY();
            BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos(this.mob.getX(), (double)$$0, this.mob.getZ());
            BlockState $$2 = this.currentContext.getBlockState($$1);
            while ($$2.is(Blocks.WATER)) {
                $$1.set(this.mob.getX(), (double)(++$$0), this.mob.getZ());
                $$2 = this.currentContext.getBlockState($$1);
            }
        } else {
            $$3 = Mth.floor(this.mob.getY() + 0.5);
        }
        if (!this.canStartAt($$4 = BlockPos.containing(this.mob.getX(), $$3, this.mob.getZ()))) {
            for (BlockPos $$5 : this.iteratePathfindingStartNodeCandidatePositions(this.mob)) {
                if (!this.canStartAt($$5)) continue;
                return super.getStartNode($$5);
            }
        }
        return super.getStartNode($$4);
    }

    @Override
    protected boolean canStartAt(BlockPos $$0) {
        PathType $$1 = this.getCachedPathType($$0.getX(), $$0.getY(), $$0.getZ());
        return this.mob.getPathfindingMalus($$1) >= 0.0f;
    }

    @Override
    public Target getTarget(double $$0, double $$1, double $$2) {
        return this.getTargetNodeAt($$0, $$1, $$2);
    }

    @Override
    public int a(Node[] $$0, Node $$1) {
        Node $$28;
        Node $$27;
        Node $$26;
        Node $$25;
        Node $$24;
        Node $$23;
        Node $$22;
        Node $$21;
        Node $$20;
        Node $$19;
        Node $$18;
        Node $$17;
        Node $$16;
        Node $$15;
        Node $$14;
        Node $$13;
        Node $$12;
        Node $$11;
        Node $$10;
        Node $$9;
        Node $$8;
        Node $$7;
        Node $$6;
        Node $$5;
        Node $$4;
        int $$2 = 0;
        Node $$3 = this.findAcceptedNode($$1.x, $$1.y, $$1.z + 1);
        if (this.isOpen($$3)) {
            $$0[$$2++] = $$3;
        }
        if (this.isOpen($$4 = this.findAcceptedNode($$1.x - 1, $$1.y, $$1.z))) {
            $$0[$$2++] = $$4;
        }
        if (this.isOpen($$5 = this.findAcceptedNode($$1.x + 1, $$1.y, $$1.z))) {
            $$0[$$2++] = $$5;
        }
        if (this.isOpen($$6 = this.findAcceptedNode($$1.x, $$1.y, $$1.z - 1))) {
            $$0[$$2++] = $$6;
        }
        if (this.isOpen($$7 = this.findAcceptedNode($$1.x, $$1.y + 1, $$1.z))) {
            $$0[$$2++] = $$7;
        }
        if (this.isOpen($$8 = this.findAcceptedNode($$1.x, $$1.y - 1, $$1.z))) {
            $$0[$$2++] = $$8;
        }
        if (this.isOpen($$9 = this.findAcceptedNode($$1.x, $$1.y + 1, $$1.z + 1)) && this.hasMalus($$3) && this.hasMalus($$7)) {
            $$0[$$2++] = $$9;
        }
        if (this.isOpen($$10 = this.findAcceptedNode($$1.x - 1, $$1.y + 1, $$1.z)) && this.hasMalus($$4) && this.hasMalus($$7)) {
            $$0[$$2++] = $$10;
        }
        if (this.isOpen($$11 = this.findAcceptedNode($$1.x + 1, $$1.y + 1, $$1.z)) && this.hasMalus($$5) && this.hasMalus($$7)) {
            $$0[$$2++] = $$11;
        }
        if (this.isOpen($$12 = this.findAcceptedNode($$1.x, $$1.y + 1, $$1.z - 1)) && this.hasMalus($$6) && this.hasMalus($$7)) {
            $$0[$$2++] = $$12;
        }
        if (this.isOpen($$13 = this.findAcceptedNode($$1.x, $$1.y - 1, $$1.z + 1)) && this.hasMalus($$3) && this.hasMalus($$8)) {
            $$0[$$2++] = $$13;
        }
        if (this.isOpen($$14 = this.findAcceptedNode($$1.x - 1, $$1.y - 1, $$1.z)) && this.hasMalus($$4) && this.hasMalus($$8)) {
            $$0[$$2++] = $$14;
        }
        if (this.isOpen($$15 = this.findAcceptedNode($$1.x + 1, $$1.y - 1, $$1.z)) && this.hasMalus($$5) && this.hasMalus($$8)) {
            $$0[$$2++] = $$15;
        }
        if (this.isOpen($$16 = this.findAcceptedNode($$1.x, $$1.y - 1, $$1.z - 1)) && this.hasMalus($$6) && this.hasMalus($$8)) {
            $$0[$$2++] = $$16;
        }
        if (this.isOpen($$17 = this.findAcceptedNode($$1.x + 1, $$1.y, $$1.z - 1)) && this.hasMalus($$6) && this.hasMalus($$5)) {
            $$0[$$2++] = $$17;
        }
        if (this.isOpen($$18 = this.findAcceptedNode($$1.x + 1, $$1.y, $$1.z + 1)) && this.hasMalus($$3) && this.hasMalus($$5)) {
            $$0[$$2++] = $$18;
        }
        if (this.isOpen($$19 = this.findAcceptedNode($$1.x - 1, $$1.y, $$1.z - 1)) && this.hasMalus($$6) && this.hasMalus($$4)) {
            $$0[$$2++] = $$19;
        }
        if (this.isOpen($$20 = this.findAcceptedNode($$1.x - 1, $$1.y, $$1.z + 1)) && this.hasMalus($$3) && this.hasMalus($$4)) {
            $$0[$$2++] = $$20;
        }
        if (this.isOpen($$21 = this.findAcceptedNode($$1.x + 1, $$1.y + 1, $$1.z - 1)) && this.hasMalus($$17) && this.hasMalus($$6) && this.hasMalus($$5) && this.hasMalus($$7) && this.hasMalus($$12) && this.hasMalus($$11)) {
            $$0[$$2++] = $$21;
        }
        if (this.isOpen($$22 = this.findAcceptedNode($$1.x + 1, $$1.y + 1, $$1.z + 1)) && this.hasMalus($$18) && this.hasMalus($$3) && this.hasMalus($$5) && this.hasMalus($$7) && this.hasMalus($$9) && this.hasMalus($$11)) {
            $$0[$$2++] = $$22;
        }
        if (this.isOpen($$23 = this.findAcceptedNode($$1.x - 1, $$1.y + 1, $$1.z - 1)) && this.hasMalus($$19) && this.hasMalus($$6) && this.hasMalus($$4) && this.hasMalus($$7) && this.hasMalus($$12) && this.hasMalus($$10)) {
            $$0[$$2++] = $$23;
        }
        if (this.isOpen($$24 = this.findAcceptedNode($$1.x - 1, $$1.y + 1, $$1.z + 1)) && this.hasMalus($$20) && this.hasMalus($$3) && this.hasMalus($$4) && this.hasMalus($$7) && this.hasMalus($$9) && this.hasMalus($$10)) {
            $$0[$$2++] = $$24;
        }
        if (this.isOpen($$25 = this.findAcceptedNode($$1.x + 1, $$1.y - 1, $$1.z - 1)) && this.hasMalus($$17) && this.hasMalus($$6) && this.hasMalus($$5) && this.hasMalus($$8) && this.hasMalus($$16) && this.hasMalus($$15)) {
            $$0[$$2++] = $$25;
        }
        if (this.isOpen($$26 = this.findAcceptedNode($$1.x + 1, $$1.y - 1, $$1.z + 1)) && this.hasMalus($$18) && this.hasMalus($$3) && this.hasMalus($$5) && this.hasMalus($$8) && this.hasMalus($$13) && this.hasMalus($$15)) {
            $$0[$$2++] = $$26;
        }
        if (this.isOpen($$27 = this.findAcceptedNode($$1.x - 1, $$1.y - 1, $$1.z - 1)) && this.hasMalus($$19) && this.hasMalus($$6) && this.hasMalus($$4) && this.hasMalus($$8) && this.hasMalus($$16) && this.hasMalus($$14)) {
            $$0[$$2++] = $$27;
        }
        if (this.isOpen($$28 = this.findAcceptedNode($$1.x - 1, $$1.y - 1, $$1.z + 1)) && this.hasMalus($$20) && this.hasMalus($$3) && this.hasMalus($$4) && this.hasMalus($$8) && this.hasMalus($$13) && this.hasMalus($$14)) {
            $$0[$$2++] = $$28;
        }
        return $$2;
    }

    private boolean hasMalus(@Nullable Node $$0) {
        return $$0 != null && $$0.costMalus >= 0.0f;
    }

    private boolean isOpen(@Nullable Node $$0) {
        return $$0 != null && !$$0.closed;
    }

    @Nullable
    protected Node findAcceptedNode(int $$0, int $$1, int $$2) {
        Node $$3 = null;
        PathType $$4 = this.getCachedPathType($$0, $$1, $$2);
        float $$5 = this.mob.getPathfindingMalus($$4);
        if ($$5 >= 0.0f) {
            $$3 = this.getNode($$0, $$1, $$2);
            $$3.type = $$4;
            $$3.costMalus = Math.max($$3.costMalus, $$5);
            if ($$4 == PathType.WALKABLE) {
                $$3.costMalus += 1.0f;
            }
        }
        return $$3;
    }

    @Override
    protected PathType getCachedPathType(int $$0, int $$1, int $$2) {
        return (PathType)((Object)this.pathTypeByPosCache.computeIfAbsent(BlockPos.asLong($$0, $$1, $$2), $$3 -> this.getPathTypeOfMob(this.currentContext, $$0, $$1, $$2, this.mob)));
    }

    @Override
    public PathType getPathType(PathfindingContext $$0, int $$1, int $$2, int $$3) {
        PathType $$4 = $$0.getPathTypeFromState($$1, $$2, $$3);
        if ($$4 == PathType.OPEN && $$2 >= $$0.level().getMinY() + 1) {
            BlockPos $$5 = new BlockPos($$1, $$2 - 1, $$3);
            PathType $$6 = $$0.getPathTypeFromState($$5.getX(), $$5.getY(), $$5.getZ());
            if ($$6 == PathType.DAMAGE_FIRE || $$6 == PathType.LAVA) {
                $$4 = PathType.DAMAGE_FIRE;
            } else if ($$6 == PathType.DAMAGE_OTHER) {
                $$4 = PathType.DAMAGE_OTHER;
            } else if ($$6 == PathType.COCOA) {
                $$4 = PathType.COCOA;
            } else if ($$6 == PathType.FENCE) {
                if (!$$5.equals($$0.mobPosition())) {
                    $$4 = PathType.FENCE;
                }
            } else {
                PathType pathType = $$4 = $$6 == PathType.WALKABLE || $$6 == PathType.OPEN || $$6 == PathType.WATER ? PathType.OPEN : PathType.WALKABLE;
            }
        }
        if ($$4 == PathType.WALKABLE || $$4 == PathType.OPEN) {
            $$4 = FlyNodeEvaluator.checkNeighbourBlocks($$0, $$1, $$2, $$3, $$4);
        }
        return $$4;
    }

    private Iterable<BlockPos> iteratePathfindingStartNodeCandidatePositions(Mob $$0) {
        boolean $$2;
        AABB $$1 = $$0.getBoundingBox();
        boolean bl = $$2 = $$1.getSize() < 1.0;
        if (!$$2) {
            return List.of((Object)BlockPos.containing($$1.minX, $$0.getBlockY(), $$1.minZ), (Object)BlockPos.containing($$1.minX, $$0.getBlockY(), $$1.maxZ), (Object)BlockPos.containing($$1.maxX, $$0.getBlockY(), $$1.minZ), (Object)BlockPos.containing($$1.maxX, $$0.getBlockY(), $$1.maxZ));
        }
        double $$3 = Math.max(0.0, (double)1.1f - $$1.getZsize());
        double $$4 = Math.max(0.0, (double)1.1f - $$1.getXsize());
        double $$5 = Math.max(0.0, (double)1.1f - $$1.getYsize());
        AABB $$6 = $$1.inflate($$4, $$5, $$3);
        return BlockPos.randomBetweenClosed($$0.getRandom(), 10, Mth.floor($$6.minX), Mth.floor($$6.minY), Mth.floor($$6.minZ), Mth.floor($$6.maxX), Mth.floor($$6.maxY), Mth.floor($$6.maxZ));
    }
}

