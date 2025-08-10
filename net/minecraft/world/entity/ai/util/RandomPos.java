/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class RandomPos {
    private static final int RANDOM_POS_ATTEMPTS = 10;

    public static BlockPos generateRandomDirection(RandomSource $$0, int $$1, int $$2) {
        int $$3 = $$0.nextInt(2 * $$1 + 1) - $$1;
        int $$4 = $$0.nextInt(2 * $$2 + 1) - $$2;
        int $$5 = $$0.nextInt(2 * $$1 + 1) - $$1;
        return new BlockPos($$3, $$4, $$5);
    }

    @Nullable
    public static BlockPos generateRandomDirectionWithinRadians(RandomSource $$0, int $$1, int $$2, int $$3, double $$4, double $$5, double $$6) {
        double $$7 = Mth.atan2($$5, $$4) - 1.5707963705062866;
        double $$8 = $$7 + (double)(2.0f * $$0.nextFloat() - 1.0f) * $$6;
        double $$9 = Math.sqrt($$0.nextDouble()) * (double)Mth.SQRT_OF_TWO * (double)$$1;
        double $$10 = -$$9 * Math.sin($$8);
        double $$11 = $$9 * Math.cos($$8);
        if (Math.abs($$10) > (double)$$1 || Math.abs($$11) > (double)$$1) {
            return null;
        }
        int $$12 = $$0.nextInt(2 * $$2 + 1) - $$2 + $$3;
        return BlockPos.containing($$10, $$12, $$11);
    }

    @VisibleForTesting
    public static BlockPos moveUpOutOfSolid(BlockPos $$0, int $$1, Predicate<BlockPos> $$2) {
        if ($$2.test($$0)) {
            BlockPos.MutableBlockPos $$3 = $$0.mutable().move(Direction.UP);
            while ($$3.getY() <= $$1 && $$2.test($$3)) {
                $$3.move(Direction.UP);
            }
            return $$3.immutable();
        }
        return $$0;
    }

    @VisibleForTesting
    public static BlockPos moveUpToAboveSolid(BlockPos $$0, int $$1, int $$2, Predicate<BlockPos> $$3) {
        if ($$1 < 0) {
            throw new IllegalArgumentException("aboveSolidAmount was " + $$1 + ", expected >= 0");
        }
        if ($$3.test($$0)) {
            BlockPos.MutableBlockPos $$4 = $$0.mutable().move(Direction.UP);
            while ($$4.getY() <= $$2 && $$3.test($$4)) {
                $$4.move(Direction.UP);
            }
            int $$5 = $$4.getY();
            while ($$4.getY() <= $$2 && $$4.getY() - $$5 < $$1) {
                $$4.move(Direction.UP);
                if (!$$3.test($$4)) continue;
                $$4.move(Direction.DOWN);
                break;
            }
            return $$4.immutable();
        }
        return $$0;
    }

    @Nullable
    public static Vec3 generateRandomPos(PathfinderMob $$0, Supplier<BlockPos> $$1) {
        return RandomPos.generateRandomPos($$1, $$0::getWalkTargetValue);
    }

    @Nullable
    public static Vec3 generateRandomPos(Supplier<BlockPos> $$0, ToDoubleFunction<BlockPos> $$1) {
        double $$2 = Double.NEGATIVE_INFINITY;
        BlockPos $$3 = null;
        for (int $$4 = 0; $$4 < 10; ++$$4) {
            double $$6;
            BlockPos $$5 = $$0.get();
            if ($$5 == null || !(($$6 = $$1.applyAsDouble($$5)) > $$2)) continue;
            $$2 = $$6;
            $$3 = $$5;
        }
        return $$3 != null ? Vec3.atBottomCenterOf($$3) : null;
    }

    public static BlockPos generateRandomPosTowardDirection(PathfinderMob $$0, int $$1, RandomSource $$2, BlockPos $$3) {
        int $$4 = $$3.getX();
        int $$5 = $$3.getZ();
        if ($$0.hasHome() && $$1 > 1) {
            BlockPos $$6 = $$0.getHomePosition();
            $$4 = $$0.getX() > (double)$$6.getX() ? ($$4 -= $$2.nextInt($$1 / 2)) : ($$4 += $$2.nextInt($$1 / 2));
            $$5 = $$0.getZ() > (double)$$6.getZ() ? ($$5 -= $$2.nextInt($$1 / 2)) : ($$5 += $$2.nextInt($$1 / 2));
        }
        return BlockPos.containing((double)$$4 + $$0.getX(), (double)$$3.getY() + $$0.getY(), (double)$$5 + $$0.getZ());
    }
}

