/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class AmphibiousNodeEvaluator
extends WalkNodeEvaluator {
    private final boolean prefersShallowSwimming;
    private float oldWalkableCost;
    private float oldWaterBorderCost;

    public AmphibiousNodeEvaluator(boolean $$0) {
        this.prefersShallowSwimming = $$0;
    }

    @Override
    public void prepare(PathNavigationRegion $$0, Mob $$1) {
        super.prepare($$0, $$1);
        $$1.setPathfindingMalus(PathType.WATER, 0.0f);
        this.oldWalkableCost = $$1.getPathfindingMalus(PathType.WALKABLE);
        $$1.setPathfindingMalus(PathType.WALKABLE, 6.0f);
        this.oldWaterBorderCost = $$1.getPathfindingMalus(PathType.WATER_BORDER);
        $$1.setPathfindingMalus(PathType.WATER_BORDER, 4.0f);
    }

    @Override
    public void done() {
        this.mob.setPathfindingMalus(PathType.WALKABLE, this.oldWalkableCost);
        this.mob.setPathfindingMalus(PathType.WATER_BORDER, this.oldWaterBorderCost);
        super.done();
    }

    @Override
    public Node getStart() {
        if (!this.mob.isInWater()) {
            return super.getStart();
        }
        return this.getStartNode(new BlockPos(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5), Mth.floor(this.mob.getBoundingBox().minZ)));
    }

    @Override
    public Target getTarget(double $$0, double $$1, double $$2) {
        return this.getTargetNodeAt($$0, $$1 + 0.5, $$2);
    }

    @Override
    public int a(Node[] $$0, Node $$1) {
        int $$6;
        int $$2 = super.a($$0, $$1);
        PathType $$3 = this.getCachedPathType($$1.x, $$1.y + 1, $$1.z);
        PathType $$4 = this.getCachedPathType($$1.x, $$1.y, $$1.z);
        if (this.mob.getPathfindingMalus($$3) >= 0.0f && $$4 != PathType.STICKY_HONEY) {
            int $$5 = Mth.floor(Math.max(1.0f, this.mob.maxUpStep()));
        } else {
            $$6 = 0;
        }
        double $$7 = this.getFloorLevel(new BlockPos($$1.x, $$1.y, $$1.z));
        Node $$8 = this.findAcceptedNode($$1.x, $$1.y + 1, $$1.z, Math.max(0, $$6 - 1), $$7, Direction.UP, $$4);
        Node $$9 = this.findAcceptedNode($$1.x, $$1.y - 1, $$1.z, $$6, $$7, Direction.DOWN, $$4);
        if (this.isVerticalNeighborValid($$8, $$1)) {
            $$0[$$2++] = $$8;
        }
        if (this.isVerticalNeighborValid($$9, $$1) && $$4 != PathType.TRAPDOOR) {
            $$0[$$2++] = $$9;
        }
        for (int $$10 = 0; $$10 < $$2; ++$$10) {
            Node $$11 = $$0[$$10];
            if ($$11.type != PathType.WATER || !this.prefersShallowSwimming || $$11.y >= this.mob.level().getSeaLevel() - 10) continue;
            $$11.costMalus += 1.0f;
        }
        return $$2;
    }

    private boolean isVerticalNeighborValid(@Nullable Node $$0, Node $$1) {
        return this.isNeighborValid($$0, $$1) && $$0.type == PathType.WATER;
    }

    @Override
    protected boolean isAmphibious() {
        return true;
    }

    @Override
    public PathType getPathType(PathfindingContext $$0, int $$1, int $$2, int $$3) {
        PathType $$4 = $$0.getPathTypeFromState($$1, $$2, $$3);
        if ($$4 == PathType.WATER) {
            BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
            for (Direction $$6 : Direction.values()) {
                $$5.set($$1, $$2, $$3).move($$6);
                PathType $$7 = $$0.getPathTypeFromState($$5.getX(), $$5.getY(), $$5.getZ());
                if ($$7 != PathType.BLOCKED) continue;
                return PathType.WATER_BORDER;
            }
            return PathType.WATER;
        }
        return super.getPathType($$0, $$1, $$2, $$3);
    }
}

