/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class GroundPathNavigation
extends PathNavigation {
    private boolean avoidSun;

    public GroundPathNavigation(Mob $$0, Level $$1) {
        super($$0, $$1);
    }

    @Override
    protected PathFinder createPathFinder(int $$0) {
        this.nodeEvaluator = new WalkNodeEvaluator();
        return new PathFinder(this.nodeEvaluator, $$0);
    }

    @Override
    protected boolean canUpdatePath() {
        return this.mob.onGround() || this.mob.isInLiquid() || this.mob.isPassenger();
    }

    @Override
    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), this.getSurfaceY(), this.mob.getZ());
    }

    @Override
    public Path createPath(BlockPos $$0, int $$1) {
        LevelChunk $$2 = this.level.getChunkSource().getChunkNow(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()));
        if ($$2 == null) {
            return null;
        }
        if ($$2.getBlockState($$0).isAir()) {
            BlockPos.MutableBlockPos $$3 = $$0.mutable().move(Direction.DOWN);
            while ($$3.getY() >= this.level.getMinY() && $$2.getBlockState($$3).isAir()) {
                $$3.move(Direction.DOWN);
            }
            if ($$3.getY() >= this.level.getMinY()) {
                return super.createPath((BlockPos)$$3.above(), $$1);
            }
            $$3.setY($$0.getY() + 1);
            while ($$3.getY() <= this.level.getMaxY() && $$2.getBlockState($$3).isAir()) {
                $$3.move(Direction.UP);
            }
            $$0 = $$3;
        }
        if ($$2.getBlockState($$0).isSolid()) {
            BlockPos.MutableBlockPos $$4 = $$0.mutable().move(Direction.UP);
            while ($$4.getY() <= this.level.getMaxY() && $$2.getBlockState($$4).isSolid()) {
                $$4.move(Direction.UP);
            }
            return super.createPath($$4.immutable(), $$1);
        }
        return super.createPath($$0, $$1);
    }

    @Override
    public Path createPath(Entity $$0, int $$1) {
        return this.createPath($$0.blockPosition(), $$1);
    }

    private int getSurfaceY() {
        if (!this.mob.isInWater() || !this.canFloat()) {
            return Mth.floor(this.mob.getY() + 0.5);
        }
        int $$0 = this.mob.getBlockY();
        BlockState $$1 = this.level.getBlockState(BlockPos.containing(this.mob.getX(), $$0, this.mob.getZ()));
        int $$2 = 0;
        while ($$1.is(Blocks.WATER)) {
            $$1 = this.level.getBlockState(BlockPos.containing(this.mob.getX(), ++$$0, this.mob.getZ()));
            if (++$$2 <= 16) continue;
            return this.mob.getBlockY();
        }
        return $$0;
    }

    @Override
    protected void trimPath() {
        super.trimPath();
        if (this.avoidSun) {
            if (this.level.canSeeSky(BlockPos.containing(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()))) {
                return;
            }
            for (int $$0 = 0; $$0 < this.path.getNodeCount(); ++$$0) {
                Node $$1 = this.path.getNode($$0);
                if (!this.level.canSeeSky(new BlockPos($$1.x, $$1.y, $$1.z))) continue;
                this.path.truncateNodes($$0);
                return;
            }
        }
    }

    @Override
    public boolean canNavigateGround() {
        return true;
    }

    protected boolean hasValidPathType(PathType $$0) {
        if ($$0 == PathType.WATER) {
            return false;
        }
        if ($$0 == PathType.LAVA) {
            return false;
        }
        return $$0 != PathType.OPEN;
    }

    public void setAvoidSun(boolean $$0) {
        this.avoidSun = $$0;
    }

    public void setCanWalkOverFences(boolean $$0) {
        this.nodeEvaluator.setCanWalkOverFences($$0);
    }
}

