/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class DoubleBlockCombiner {
    public static <S extends BlockEntity> NeighborCombineResult<S> combineWithNeigbour(BlockEntityType<S> $$0, Function<BlockState, BlockType> $$1, Function<BlockState, Direction> $$2, Property<Direction> $$3, BlockState $$4, LevelAccessor $$5, BlockPos $$6, BiPredicate<LevelAccessor, BlockPos> $$7) {
        BlockType $$14;
        boolean $$11;
        S $$8 = $$0.getBlockEntity($$5, $$6);
        if ($$8 == null) {
            return Combiner::acceptNone;
        }
        if ($$7.test($$5, $$6)) {
            return Combiner::acceptNone;
        }
        BlockType $$9 = $$1.apply($$4);
        boolean $$10 = $$9 == BlockType.SINGLE;
        boolean bl = $$11 = $$9 == BlockType.FIRST;
        if ($$10) {
            return new NeighborCombineResult.Single<S>($$8);
        }
        BlockPos $$12 = $$6.relative($$2.apply($$4));
        BlockState $$13 = $$5.getBlockState($$12);
        if ($$13.is($$4.getBlock()) && ($$14 = $$1.apply($$13)) != BlockType.SINGLE && $$9 != $$14 && $$13.getValue($$3) == $$4.getValue($$3)) {
            if ($$7.test($$5, $$12)) {
                return Combiner::acceptNone;
            }
            S $$15 = $$0.getBlockEntity($$5, $$12);
            if ($$15 != null) {
                S $$16 = $$11 ? $$8 : $$15;
                S $$17 = $$11 ? $$15 : $$8;
                return new NeighborCombineResult.Double<S>($$16, $$17);
            }
        }
        return new NeighborCombineResult.Single<S>($$8);
    }

    public static interface NeighborCombineResult<S> {
        public <T> T apply(Combiner<? super S, T> var1);

        public static final class Single<S>
        implements NeighborCombineResult<S> {
            private final S single;

            public Single(S $$0) {
                this.single = $$0;
            }

            @Override
            public <T> T apply(Combiner<? super S, T> $$0) {
                return $$0.acceptSingle(this.single);
            }
        }

        public static final class Double<S>
        implements NeighborCombineResult<S> {
            private final S first;
            private final S second;

            public Double(S $$0, S $$1) {
                this.first = $$0;
                this.second = $$1;
            }

            @Override
            public <T> T apply(Combiner<? super S, T> $$0) {
                return $$0.acceptDouble(this.first, this.second);
            }
        }
    }

    public static final class BlockType
    extends Enum<BlockType> {
        public static final /* enum */ BlockType SINGLE = new BlockType();
        public static final /* enum */ BlockType FIRST = new BlockType();
        public static final /* enum */ BlockType SECOND = new BlockType();
        private static final /* synthetic */ BlockType[] $VALUES;

        public static BlockType[] values() {
            return (BlockType[])$VALUES.clone();
        }

        public static BlockType valueOf(String $$0) {
            return Enum.valueOf(BlockType.class, $$0);
        }

        private static /* synthetic */ BlockType[] a() {
            return new BlockType[]{SINGLE, FIRST, SECOND};
        }

        static {
            $VALUES = BlockType.a();
        }
    }

    public static interface Combiner<S, T> {
        public T acceptDouble(S var1, S var2);

        public T acceptSingle(S var1);

        public T acceptNone();
    }
}

