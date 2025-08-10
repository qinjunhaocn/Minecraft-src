/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface BonemealableBlock {
    public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3);

    public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4);

    public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4);

    public static boolean hasSpreadableNeighbourPos(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        return BonemealableBlock.getSpreadableNeighbourPos(Direction.Plane.HORIZONTAL.stream().toList(), $$0, $$1, $$2).isPresent();
    }

    public static Optional<BlockPos> findSpreadableNeighbourPos(Level $$0, BlockPos $$1, BlockState $$2) {
        return BonemealableBlock.getSpreadableNeighbourPos(Direction.Plane.HORIZONTAL.shuffledCopy($$0.random), $$0, $$1, $$2);
    }

    private static Optional<BlockPos> getSpreadableNeighbourPos(List<Direction> $$0, LevelReader $$1, BlockPos $$2, BlockState $$3) {
        for (Direction $$4 : $$0) {
            BlockPos $$5 = $$2.relative($$4);
            if (!$$1.isEmptyBlock($$5) || !$$3.canSurvive($$1, $$5)) continue;
            return Optional.of($$5);
        }
        return Optional.empty();
    }

    default public BlockPos getParticlePos(BlockPos $$0) {
        return switch (this.getType().ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> $$0.above();
            case 1 -> $$0;
        };
    }

    default public Type getType() {
        return Type.GROWER;
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type NEIGHBOR_SPREADER = new Type();
        public static final /* enum */ Type GROWER = new Type();
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private static /* synthetic */ Type[] a() {
            return new Type[]{NEIGHBOR_SPREADER, GROWER};
        }

        static {
            $VALUES = Type.a();
        }
    }
}

