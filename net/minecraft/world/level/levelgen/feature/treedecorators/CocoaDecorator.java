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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class CocoaDecorator
extends TreeDecorator {
    public static final MapCodec<CocoaDecorator> CODEC = Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("probability").xmap(CocoaDecorator::new, $$0 -> Float.valueOf($$0.probability));
    private final float probability;

    public CocoaDecorator(float $$0) {
        this.probability = $$0;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.COCOA;
    }

    @Override
    public void place(TreeDecorator.Context $$0) {
        RandomSource $$12 = $$0.random();
        if ($$12.nextFloat() >= this.probability) {
            return;
        }
        ObjectArrayList<BlockPos> $$22 = $$0.logs();
        if ($$22.isEmpty()) {
            return;
        }
        int $$3 = ((BlockPos)$$22.getFirst()).getY();
        $$22.stream().filter($$1 -> $$1.getY() - $$3 <= 2).forEach($$2 -> {
            for (Direction $$3 : Direction.Plane.HORIZONTAL) {
                Direction $$4;
                BlockPos $$5;
                if (!($$12.nextFloat() <= 0.25f) || !$$0.isAir($$5 = $$2.offset(($$4 = $$3.getOpposite()).getStepX(), 0, $$4.getStepZ()))) continue;
                $$0.setBlock($$5, (BlockState)((BlockState)Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE, $$12.nextInt(3))).setValue(CocoaBlock.FACING, $$3));
            }
        });
    }
}

