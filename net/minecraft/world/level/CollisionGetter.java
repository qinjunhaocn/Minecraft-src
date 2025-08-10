/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CollisionGetter
extends BlockGetter {
    public WorldBorder getWorldBorder();

    @Nullable
    public BlockGetter getChunkForCollisions(int var1, int var2);

    default public boolean isUnobstructed(@Nullable Entity $$0, VoxelShape $$1) {
        return true;
    }

    default public boolean isUnobstructed(BlockState $$0, BlockPos $$1, CollisionContext $$2) {
        VoxelShape $$3 = $$0.getCollisionShape(this, $$1, $$2);
        return $$3.isEmpty() || this.isUnobstructed(null, $$3.move($$1));
    }

    default public boolean isUnobstructed(Entity $$0) {
        return this.isUnobstructed($$0, Shapes.create($$0.getBoundingBox()));
    }

    default public boolean noCollision(AABB $$0) {
        return this.noCollision(null, $$0);
    }

    default public boolean noCollision(Entity $$0) {
        return this.noCollision($$0, $$0.getBoundingBox());
    }

    default public boolean noCollision(@Nullable Entity $$0, AABB $$1) {
        return this.noCollision($$0, $$1, false);
    }

    default public boolean noCollision(@Nullable Entity $$0, AABB $$1, boolean $$2) {
        Iterable<VoxelShape> $$3 = $$2 ? this.getBlockAndLiquidCollisions($$0, $$1) : this.getBlockCollisions($$0, $$1);
        for (VoxelShape $$4 : $$3) {
            if ($$4.isEmpty()) continue;
            return false;
        }
        if (!this.getEntityCollisions($$0, $$1).isEmpty()) {
            return false;
        }
        if ($$0 != null) {
            VoxelShape $$5 = this.borderCollision($$0, $$1);
            return $$5 == null || !Shapes.joinIsNotEmpty($$5, Shapes.create($$1), BooleanOp.AND);
        }
        return true;
    }

    default public boolean noBlockCollision(@Nullable Entity $$0, AABB $$1) {
        for (VoxelShape $$2 : this.getBlockCollisions($$0, $$1)) {
            if ($$2.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public List<VoxelShape> getEntityCollisions(@Nullable Entity var1, AABB var2);

    default public Iterable<VoxelShape> getCollisions(@Nullable Entity $$0, AABB $$1) {
        List<VoxelShape> $$2 = this.getEntityCollisions($$0, $$1);
        Iterable<VoxelShape> $$3 = this.getBlockCollisions($$0, $$1);
        return $$2.isEmpty() ? $$3 : Iterables.concat($$2, $$3);
    }

    default public Iterable<VoxelShape> getPreMoveCollisions(@Nullable Entity $$0, AABB $$1, Vec3 $$2) {
        List<VoxelShape> $$3 = this.getEntityCollisions($$0, $$1);
        Iterable<VoxelShape> $$4 = this.getBlockCollisionsFromContext(CollisionContext.withPosition($$0, $$2.y), $$1);
        return $$3.isEmpty() ? $$4 : Iterables.concat($$3, $$4);
    }

    default public Iterable<VoxelShape> getBlockCollisions(@Nullable Entity $$0, AABB $$1) {
        return this.getBlockCollisionsFromContext($$0 == null ? CollisionContext.empty() : CollisionContext.of($$0), $$1);
    }

    default public Iterable<VoxelShape> getBlockAndLiquidCollisions(@Nullable Entity $$0, AABB $$1) {
        return this.getBlockCollisionsFromContext($$0 == null ? CollisionContext.empty() : CollisionContext.of($$0, true), $$1);
    }

    private Iterable<VoxelShape> getBlockCollisionsFromContext(CollisionContext $$0, AABB $$1) {
        return () -> new BlockCollisions<VoxelShape>(this, $$0, $$1, false, ($$0, $$1) -> $$1);
    }

    @Nullable
    private VoxelShape borderCollision(Entity $$0, AABB $$1) {
        WorldBorder $$2 = this.getWorldBorder();
        return $$2.isInsideCloseToBorder($$0, $$1) ? $$2.getCollisionShape() : null;
    }

    default public BlockHitResult clipIncludingBorder(ClipContext $$0) {
        BlockHitResult $$1 = this.clip($$0);
        WorldBorder $$2 = this.getWorldBorder();
        if ($$2.isWithinBounds($$0.getFrom()) && !$$2.isWithinBounds($$1.getLocation())) {
            Vec3 $$3 = $$1.getLocation().subtract($$0.getFrom());
            Direction $$4 = Direction.getApproximateNearest($$3.x, $$3.y, $$3.z);
            Vec3 $$5 = $$2.clampVec3ToBound($$1.getLocation());
            return new BlockHitResult($$5, $$4, BlockPos.containing($$5), false, true);
        }
        return $$1;
    }

    default public boolean collidesWithSuffocatingBlock(@Nullable Entity $$02, AABB $$12) {
        BlockCollisions<VoxelShape> $$2 = new BlockCollisions<VoxelShape>(this, $$02, $$12, true, ($$0, $$1) -> $$1);
        while ($$2.hasNext()) {
            if (((VoxelShape)$$2.next()).isEmpty()) continue;
            return true;
        }
        return false;
    }

    default public Optional<BlockPos> findSupportingBlock(Entity $$02, AABB $$12) {
        BlockPos $$2 = null;
        double $$3 = Double.MAX_VALUE;
        BlockCollisions<BlockPos> $$4 = new BlockCollisions<BlockPos>(this, $$02, $$12, false, ($$0, $$1) -> $$0);
        while ($$4.hasNext()) {
            BlockPos $$5 = (BlockPos)$$4.next();
            double $$6 = $$5.distToCenterSqr($$02.position());
            if (!($$6 < $$3) && ($$6 != $$3 || $$2 != null && $$2.compareTo($$5) >= 0)) continue;
            $$2 = $$5.immutable();
            $$3 = $$6;
        }
        return Optional.ofNullable($$2);
    }

    default public Optional<Vec3> findFreePosition(@Nullable Entity $$02, VoxelShape $$1, Vec3 $$2, double $$32, double $$4, double $$5) {
        if ($$1.isEmpty()) {
            return Optional.empty();
        }
        AABB $$6 = $$1.bounds().inflate($$32, $$4, $$5);
        VoxelShape $$7 = StreamSupport.stream(this.getBlockCollisions($$02, $$6).spliterator(), false).filter($$0 -> this.getWorldBorder() == null || this.getWorldBorder().isWithinBounds($$0.bounds())).flatMap($$0 -> $$0.toAabbs().stream()).map($$3 -> $$3.inflate($$32 / 2.0, $$4 / 2.0, $$5 / 2.0)).map(Shapes::create).reduce(Shapes.empty(), Shapes::or);
        VoxelShape $$8 = Shapes.join($$1, $$7, BooleanOp.ONLY_FIRST);
        return $$8.closestPointTo($$2);
    }
}

