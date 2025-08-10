/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.world.level;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface BlockGetter
extends LevelHeightAccessor {
    public static final int MAX_BLOCK_ITERATIONS_ALONG_TRAVEL = 16;

    @Nullable
    public BlockEntity getBlockEntity(BlockPos var1);

    default public <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos $$0, BlockEntityType<T> $$1) {
        BlockEntity $$2 = this.getBlockEntity($$0);
        if ($$2 == null || $$2.getType() != $$1) {
            return Optional.empty();
        }
        return Optional.of($$2);
    }

    public BlockState getBlockState(BlockPos var1);

    public FluidState getFluidState(BlockPos var1);

    default public int getLightEmission(BlockPos $$0) {
        return this.getBlockState($$0).getLightEmission();
    }

    default public Stream<BlockState> getBlockStates(AABB $$0) {
        return BlockPos.betweenClosedStream($$0).map(this::getBlockState);
    }

    default public BlockHitResult isBlockInLine(ClipBlockStateContext $$02) {
        return BlockGetter.traverseBlocks($$02.getFrom(), $$02.getTo(), $$02, ($$0, $$1) -> {
            BlockState $$2 = this.getBlockState((BlockPos)$$1);
            Vec3 $$3 = $$0.getFrom().subtract($$0.getTo());
            return $$0.isTargetBlock().test($$2) ? new BlockHitResult($$0.getTo(), Direction.getApproximateNearest($$3.x, $$3.y, $$3.z), BlockPos.containing($$0.getTo()), false) : null;
        }, $$0 -> {
            Vec3 $$1 = $$0.getFrom().subtract($$0.getTo());
            return BlockHitResult.miss($$0.getTo(), Direction.getApproximateNearest($$1.x, $$1.y, $$1.z), BlockPos.containing($$0.getTo()));
        });
    }

    default public BlockHitResult clip(ClipContext $$02) {
        return BlockGetter.traverseBlocks($$02.getFrom(), $$02.getTo(), $$02, ($$0, $$1) -> {
            BlockState $$2 = this.getBlockState((BlockPos)$$1);
            FluidState $$3 = this.getFluidState((BlockPos)$$1);
            Vec3 $$4 = $$0.getFrom();
            Vec3 $$5 = $$0.getTo();
            VoxelShape $$6 = $$0.getBlockShape($$2, this, (BlockPos)$$1);
            BlockHitResult $$7 = this.clipWithInteractionOverride($$4, $$5, (BlockPos)$$1, $$6, $$2);
            VoxelShape $$8 = $$0.getFluidShape($$3, this, (BlockPos)$$1);
            BlockHitResult $$9 = $$8.clip($$4, $$5, (BlockPos)$$1);
            double $$10 = $$7 == null ? Double.MAX_VALUE : $$0.getFrom().distanceToSqr($$7.getLocation());
            double $$11 = $$9 == null ? Double.MAX_VALUE : $$0.getFrom().distanceToSqr($$9.getLocation());
            return $$10 <= $$11 ? $$7 : $$9;
        }, $$0 -> {
            Vec3 $$1 = $$0.getFrom().subtract($$0.getTo());
            return BlockHitResult.miss($$0.getTo(), Direction.getApproximateNearest($$1.x, $$1.y, $$1.z), BlockPos.containing($$0.getTo()));
        });
    }

    @Nullable
    default public BlockHitResult clipWithInteractionOverride(Vec3 $$0, Vec3 $$1, BlockPos $$2, VoxelShape $$3, BlockState $$4) {
        BlockHitResult $$6;
        BlockHitResult $$5 = $$3.clip($$0, $$1, $$2);
        if ($$5 != null && ($$6 = $$4.getInteractionShape(this, $$2).clip($$0, $$1, $$2)) != null && $$6.getLocation().subtract($$0).lengthSqr() < $$5.getLocation().subtract($$0).lengthSqr()) {
            return $$5.withDirection($$6.getDirection());
        }
        return $$5;
    }

    default public double getBlockFloorHeight(VoxelShape $$0, Supplier<VoxelShape> $$1) {
        if (!$$0.isEmpty()) {
            return $$0.max(Direction.Axis.Y);
        }
        double $$2 = $$1.get().max(Direction.Axis.Y);
        if ($$2 >= 1.0) {
            return $$2 - 1.0;
        }
        return Double.NEGATIVE_INFINITY;
    }

    default public double getBlockFloorHeight(BlockPos $$0) {
        return this.getBlockFloorHeight(this.getBlockState($$0).getCollisionShape(this, $$0), () -> {
            BlockPos $$1 = $$0.below();
            return this.getBlockState($$1).getCollisionShape(this, $$1);
        });
    }

    public static <T, C> T traverseBlocks(Vec3 $$0, Vec3 $$1, C $$2, BiFunction<C, BlockPos, T> $$3, Function<C, T> $$4) {
        int $$13;
        int $$12;
        if ($$0.equals($$1)) {
            return $$4.apply($$2);
        }
        double $$5 = Mth.lerp(-1.0E-7, $$1.x, $$0.x);
        double $$6 = Mth.lerp(-1.0E-7, $$1.y, $$0.y);
        double $$7 = Mth.lerp(-1.0E-7, $$1.z, $$0.z);
        double $$8 = Mth.lerp(-1.0E-7, $$0.x, $$1.x);
        double $$9 = Mth.lerp(-1.0E-7, $$0.y, $$1.y);
        double $$10 = Mth.lerp(-1.0E-7, $$0.z, $$1.z);
        int $$11 = Mth.floor($$8);
        BlockPos.MutableBlockPos $$14 = new BlockPos.MutableBlockPos($$11, $$12 = Mth.floor($$9), $$13 = Mth.floor($$10));
        T $$15 = $$3.apply($$2, $$14);
        if ($$15 != null) {
            return $$15;
        }
        double $$16 = $$5 - $$8;
        double $$17 = $$6 - $$9;
        double $$18 = $$7 - $$10;
        int $$19 = Mth.sign($$16);
        int $$20 = Mth.sign($$17);
        int $$21 = Mth.sign($$18);
        double $$22 = $$19 == 0 ? Double.MAX_VALUE : (double)$$19 / $$16;
        double $$23 = $$20 == 0 ? Double.MAX_VALUE : (double)$$20 / $$17;
        double $$24 = $$21 == 0 ? Double.MAX_VALUE : (double)$$21 / $$18;
        double $$25 = $$22 * ($$19 > 0 ? 1.0 - Mth.frac($$8) : Mth.frac($$8));
        double $$26 = $$23 * ($$20 > 0 ? 1.0 - Mth.frac($$9) : Mth.frac($$9));
        double $$27 = $$24 * ($$21 > 0 ? 1.0 - Mth.frac($$10) : Mth.frac($$10));
        while ($$25 <= 1.0 || $$26 <= 1.0 || $$27 <= 1.0) {
            T $$28;
            if ($$25 < $$26) {
                if ($$25 < $$27) {
                    $$11 += $$19;
                    $$25 += $$22;
                } else {
                    $$13 += $$21;
                    $$27 += $$24;
                }
            } else if ($$26 < $$27) {
                $$12 += $$20;
                $$26 += $$23;
            } else {
                $$13 += $$21;
                $$27 += $$24;
            }
            if (($$28 = $$3.apply($$2, $$14.set($$11, $$12, $$13))) == null) continue;
            return $$28;
        }
        return $$4.apply($$2);
    }

    public static boolean forEachBlockIntersectedBetween(Vec3 $$0, Vec3 $$1, AABB $$2, BlockStepVisitor $$3) {
        Vec3 $$4 = $$1.subtract($$0);
        if ($$4.lengthSqr() < (double)Mth.square(0.99999f)) {
            for (BlockPos $$5 : BlockPos.betweenClosed($$2)) {
                if ($$3.visit($$5, 0)) continue;
                return false;
            }
            return true;
        }
        LongOpenHashSet $$6 = new LongOpenHashSet();
        Vec3 $$7 = $$2.getMinPosition();
        Vec3 $$8 = $$7.subtract($$4);
        int $$9 = BlockGetter.addCollisionsAlongTravel((LongSet)$$6, $$8, $$7, $$2, $$3);
        if ($$9 < 0) {
            return false;
        }
        for (BlockPos $$10 : BlockPos.betweenClosed($$2)) {
            if ($$6.contains($$10.asLong()) || $$3.visit($$10, $$9 + 1)) continue;
            return false;
        }
        return true;
    }

    private static int addCollisionsAlongTravel(LongSet $$0, Vec3 $$1, Vec3 $$2, AABB $$3, BlockStepVisitor $$4) {
        Vec3 $$5 = $$2.subtract($$1);
        int $$6 = Mth.floor($$1.x);
        int $$7 = Mth.floor($$1.y);
        int $$8 = Mth.floor($$1.z);
        int $$9 = Mth.sign($$5.x);
        int $$10 = Mth.sign($$5.y);
        int $$11 = Mth.sign($$5.z);
        double $$12 = $$9 == 0 ? Double.MAX_VALUE : (double)$$9 / $$5.x;
        double $$13 = $$10 == 0 ? Double.MAX_VALUE : (double)$$10 / $$5.y;
        double $$14 = $$11 == 0 ? Double.MAX_VALUE : (double)$$11 / $$5.z;
        double $$15 = $$12 * ($$9 > 0 ? 1.0 - Mth.frac($$1.x) : Mth.frac($$1.x));
        double $$16 = $$13 * ($$10 > 0 ? 1.0 - Mth.frac($$1.y) : Mth.frac($$1.y));
        double $$17 = $$14 * ($$11 > 0 ? 1.0 - Mth.frac($$1.z) : Mth.frac($$1.z));
        int $$18 = 0;
        BlockPos.MutableBlockPos $$19 = new BlockPos.MutableBlockPos();
        while ($$15 <= 1.0 || $$16 <= 1.0 || $$17 <= 1.0) {
            if ($$15 < $$16) {
                if ($$15 < $$17) {
                    $$6 += $$9;
                    $$15 += $$12;
                } else {
                    $$8 += $$11;
                    $$17 += $$14;
                }
            } else if ($$16 < $$17) {
                $$7 += $$10;
                $$16 += $$13;
            } else {
                $$8 += $$11;
                $$17 += $$14;
            }
            if ($$18++ > 16) break;
            Optional<Vec3> $$20 = AABB.clip($$6, $$7, $$8, $$6 + 1, $$7 + 1, $$8 + 1, $$1, $$2);
            if ($$20.isEmpty()) continue;
            Vec3 $$21 = $$20.get();
            double $$22 = Mth.clamp($$21.x, (double)$$6 + (double)1.0E-5f, (double)$$6 + 1.0 - (double)1.0E-5f);
            double $$23 = Mth.clamp($$21.y, (double)$$7 + (double)1.0E-5f, (double)$$7 + 1.0 - (double)1.0E-5f);
            double $$24 = Mth.clamp($$21.z, (double)$$8 + (double)1.0E-5f, (double)$$8 + 1.0 - (double)1.0E-5f);
            int $$25 = Mth.floor($$22 + $$3.getXsize());
            int $$26 = Mth.floor($$23 + $$3.getYsize());
            int $$27 = Mth.floor($$24 + $$3.getZsize());
            for (int $$28 = $$6; $$28 <= $$25; ++$$28) {
                for (int $$29 = $$7; $$29 <= $$26; ++$$29) {
                    for (int $$30 = $$8; $$30 <= $$27; ++$$30) {
                        if (!$$0.add(BlockPos.asLong($$28, $$29, $$30)) || $$4.visit($$19.set($$28, $$29, $$30), $$18)) continue;
                        return -1;
                    }
                }
            }
        }
        return $$18;
    }

    @FunctionalInterface
    public static interface BlockStepVisitor {
        public boolean visit(BlockPos var1, int var2);
    }
}

