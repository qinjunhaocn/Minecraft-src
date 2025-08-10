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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.CreakingHeartState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class CreakingHeartDecorator
extends TreeDecorator {
    public static final MapCodec<CreakingHeartDecorator> CODEC = Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("probability").xmap(CreakingHeartDecorator::new, $$0 -> Float.valueOf($$0.probability));
    private final float probability;

    public CreakingHeartDecorator(float $$0) {
        this.probability = $$0;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.CREAKING_HEART;
    }

    @Override
    public void place(TreeDecorator.Context $$0) {
        RandomSource $$12 = $$0.random();
        ObjectArrayList<BlockPos> $$2 = $$0.logs();
        if ($$2.isEmpty()) {
            return;
        }
        if ($$12.nextFloat() >= this.probability) {
            return;
        }
        ArrayList<BlockPos> $$3 = new ArrayList<BlockPos>((Collection<BlockPos>)$$2);
        Util.shuffle($$3, $$12);
        Optional<BlockPos> $$4 = $$3.stream().filter($$1 -> {
            for (Direction $$2 : Direction.values()) {
                if ($$0.checkBlock($$1.relative($$2), $$0 -> $$0.is(BlockTags.LOGS))) continue;
                return false;
            }
            return true;
        }).findFirst();
        if ($$4.isEmpty()) {
            return;
        }
        $$0.setBlock($$4.get(), (BlockState)((BlockState)Blocks.CREAKING_HEART.defaultBlockState().setValue(CreakingHeartBlock.STATE, CreakingHeartState.DORMANT)).setValue(CreakingHeartBlock.NATURAL, true));
    }
}

