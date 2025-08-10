/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class BeehiveDecorator
extends TreeDecorator {
    public static final MapCodec<BeehiveDecorator> CODEC = Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("probability").xmap(BeehiveDecorator::new, $$0 -> Float.valueOf($$0.probability));
    private static final Direction WORLDGEN_FACING = Direction.SOUTH;
    private static final Direction[] SPAWN_DIRECTIONS = (Direction[])Direction.Plane.HORIZONTAL.stream().filter($$0 -> $$0 != WORLDGEN_FACING.getOpposite()).toArray(Direction[]::new);
    private final float probability;

    public BeehiveDecorator(float $$0) {
        this.probability = $$0;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.BEEHIVE;
    }

    @Override
    public void place(TreeDecorator.Context $$02) {
        ObjectArrayList<BlockPos> $$12 = $$02.leaves();
        ObjectArrayList<BlockPos> $$2 = $$02.logs();
        if ($$2.isEmpty()) {
            return;
        }
        RandomSource $$3 = $$02.random();
        if ($$3.nextFloat() >= this.probability) {
            return;
        }
        int $$4 = !$$12.isEmpty() ? Math.max(((BlockPos)$$12.getFirst()).getY() - 1, ((BlockPos)$$2.getFirst()).getY() + 1) : Math.min(((BlockPos)$$2.getFirst()).getY() + 1 + $$3.nextInt(3), ((BlockPos)$$2.getLast()).getY());
        List $$5 = $$2.stream().filter($$1 -> $$1.getY() == $$4).flatMap($$0 -> Stream.of(SPAWN_DIRECTIONS).map($$0::relative)).collect(Collectors.toList());
        if ($$5.isEmpty()) {
            return;
        }
        Util.shuffle($$5, $$3);
        Optional<BlockPos> $$6 = $$5.stream().filter($$1 -> $$02.isAir((BlockPos)$$1) && $$02.isAir($$1.relative(WORLDGEN_FACING))).findFirst();
        if ($$6.isEmpty()) {
            return;
        }
        $$02.setBlock($$6.get(), (BlockState)Blocks.BEE_NEST.defaultBlockState().setValue(BeehiveBlock.FACING, WORLDGEN_FACING));
        $$02.level().getBlockEntity($$6.get(), BlockEntityType.BEEHIVE).ifPresent($$1 -> {
            int $$2 = 2 + $$3.nextInt(2);
            for (int $$3 = 0; $$3 < $$2; ++$$3) {
                $$1.storeBee(BeehiveBlockEntity.Occupant.create($$3.nextInt(599)));
            }
        });
    }
}

