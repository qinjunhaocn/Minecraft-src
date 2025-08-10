/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MultifaceSpreader {
    public static final SpreadType[] DEFAULT_SPREAD_ORDER = new SpreadType[]{SpreadType.SAME_POSITION, SpreadType.SAME_PLANE, SpreadType.WRAP_AROUND};
    private final SpreadConfig config;

    public MultifaceSpreader(MultifaceBlock $$0) {
        this(new DefaultSpreaderConfig($$0));
    }

    public MultifaceSpreader(SpreadConfig $$0) {
        this.config = $$0;
    }

    public boolean canSpreadInAnyDirection(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return Direction.stream().anyMatch($$4 -> this.getSpreadFromFaceTowardDirection($$0, $$1, $$2, $$3, (Direction)$$4, this.config::canSpreadInto).isPresent());
    }

    public Optional<SpreadPos> spreadFromRandomFaceTowardRandomDirection(BlockState $$0, LevelAccessor $$12, BlockPos $$2, RandomSource $$3) {
        return Direction.allShuffled($$3).stream().filter($$1 -> this.config.canSpreadFrom($$0, (Direction)$$1)).map($$4 -> this.spreadFromFaceTowardRandomDirection($$0, $$12, $$2, (Direction)$$4, $$3, false)).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }

    public long spreadAll(BlockState $$0, LevelAccessor $$12, BlockPos $$2, boolean $$3) {
        return Direction.stream().filter($$1 -> this.config.canSpreadFrom($$0, (Direction)$$1)).map($$4 -> this.spreadFromFaceTowardAllDirections($$0, $$12, $$2, (Direction)$$4, $$3)).reduce(0L, Long::sum);
    }

    public Optional<SpreadPos> spreadFromFaceTowardRandomDirection(BlockState $$0, LevelAccessor $$1, BlockPos $$2, Direction $$3, RandomSource $$4, boolean $$52) {
        return Direction.allShuffled($$4).stream().map($$5 -> this.spreadFromFaceTowardDirection($$0, $$1, $$2, $$3, (Direction)$$5, $$52)).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }

    private long spreadFromFaceTowardAllDirections(BlockState $$0, LevelAccessor $$1, BlockPos $$2, Direction $$3, boolean $$4) {
        return Direction.stream().map($$5 -> this.spreadFromFaceTowardDirection($$0, $$1, $$2, $$3, (Direction)$$5, $$4)).filter(Optional::isPresent).count();
    }

    @VisibleForTesting
    public Optional<SpreadPos> spreadFromFaceTowardDirection(BlockState $$0, LevelAccessor $$1, BlockPos $$22, Direction $$3, Direction $$4, boolean $$5) {
        return this.getSpreadFromFaceTowardDirection($$0, $$1, $$22, $$3, $$4, this.config::canSpreadInto).flatMap($$2 -> this.spreadToFace($$1, (SpreadPos)((Object)$$2), $$5));
    }

    public Optional<SpreadPos> getSpreadFromFaceTowardDirection(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3, Direction $$4, SpreadPredicate $$5) {
        if ($$4.getAxis() == $$3.getAxis()) {
            return Optional.empty();
        }
        if (!(this.config.isOtherBlockValidAsSource($$0) || this.config.hasFace($$0, $$3) && !this.config.hasFace($$0, $$4))) {
            return Optional.empty();
        }
        for (SpreadType $$6 : this.config.a()) {
            SpreadPos $$7 = $$6.getSpreadPos($$2, $$4, $$3);
            if (!$$5.test($$1, $$2, $$7)) continue;
            return Optional.of($$7);
        }
        return Optional.empty();
    }

    public Optional<SpreadPos> spreadToFace(LevelAccessor $$0, SpreadPos $$1, boolean $$2) {
        BlockState $$3 = $$0.getBlockState($$1.pos());
        if (this.config.placeBlock($$0, $$1, $$3, $$2)) {
            return Optional.of($$1);
        }
        return Optional.empty();
    }

    public static class DefaultSpreaderConfig
    implements SpreadConfig {
        protected MultifaceBlock block;

        public DefaultSpreaderConfig(MultifaceBlock $$0) {
            this.block = $$0;
        }

        @Override
        @Nullable
        public BlockState getStateForPlacement(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
            return this.block.getStateForPlacement($$0, $$1, $$2, $$3);
        }

        protected boolean stateCanBeReplaced(BlockGetter $$0, BlockPos $$1, BlockPos $$2, Direction $$3, BlockState $$4) {
            return $$4.isAir() || $$4.is(this.block) || $$4.is(Blocks.WATER) && $$4.getFluidState().isSource();
        }

        @Override
        public boolean canSpreadInto(BlockGetter $$0, BlockPos $$1, SpreadPos $$2) {
            BlockState $$3 = $$0.getBlockState($$2.pos());
            return this.stateCanBeReplaced($$0, $$1, $$2.pos(), $$2.face(), $$3) && this.block.isValidStateForPlacement($$0, $$3, $$2.pos(), $$2.face());
        }
    }

    public static interface SpreadConfig {
        @Nullable
        public BlockState getStateForPlacement(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4);

        public boolean canSpreadInto(BlockGetter var1, BlockPos var2, SpreadPos var3);

        default public SpreadType[] a() {
            return DEFAULT_SPREAD_ORDER;
        }

        default public boolean hasFace(BlockState $$0, Direction $$1) {
            return MultifaceBlock.hasFace($$0, $$1);
        }

        default public boolean isOtherBlockValidAsSource(BlockState $$0) {
            return false;
        }

        default public boolean canSpreadFrom(BlockState $$0, Direction $$1) {
            return this.isOtherBlockValidAsSource($$0) || this.hasFace($$0, $$1);
        }

        default public boolean placeBlock(LevelAccessor $$0, SpreadPos $$1, BlockState $$2, boolean $$3) {
            BlockState $$4 = this.getStateForPlacement($$2, $$0, $$1.pos(), $$1.face());
            if ($$4 != null) {
                if ($$3) {
                    $$0.getChunk($$1.pos()).markPosForPostprocessing($$1.pos());
                }
                return $$0.setBlock($$1.pos(), $$4, 2);
            }
            return false;
        }
    }

    @FunctionalInterface
    public static interface SpreadPredicate {
        public boolean test(BlockGetter var1, BlockPos var2, SpreadPos var3);
    }

    public static abstract sealed class SpreadType
    extends Enum<SpreadType> {
        public static final /* enum */ SpreadType SAME_POSITION = new SpreadType(){

            @Override
            public SpreadPos getSpreadPos(BlockPos $$0, Direction $$1, Direction $$2) {
                return new SpreadPos($$0, $$1);
            }
        };
        public static final /* enum */ SpreadType SAME_PLANE = new SpreadType(){

            @Override
            public SpreadPos getSpreadPos(BlockPos $$0, Direction $$1, Direction $$2) {
                return new SpreadPos($$0.relative($$1), $$2);
            }
        };
        public static final /* enum */ SpreadType WRAP_AROUND = new SpreadType(){

            @Override
            public SpreadPos getSpreadPos(BlockPos $$0, Direction $$1, Direction $$2) {
                return new SpreadPos($$0.relative($$1).relative($$2), $$1.getOpposite());
            }
        };
        private static final /* synthetic */ SpreadType[] $VALUES;

        public static SpreadType[] values() {
            return (SpreadType[])$VALUES.clone();
        }

        public static SpreadType valueOf(String $$0) {
            return Enum.valueOf(SpreadType.class, $$0);
        }

        public abstract SpreadPos getSpreadPos(BlockPos var1, Direction var2, Direction var3);

        private static /* synthetic */ SpreadType[] a() {
            return new SpreadType[]{SAME_POSITION, SAME_PLANE, WRAP_AROUND};
        }

        static {
            $VALUES = SpreadType.a();
        }
    }

    public record SpreadPos(BlockPos pos, Direction face) {
    }
}

