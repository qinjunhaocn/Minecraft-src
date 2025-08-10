/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class TrunkVineDecorator
extends TreeDecorator {
    public static final MapCodec<TrunkVineDecorator> CODEC = MapCodec.unit(() -> INSTANCE);
    public static final TrunkVineDecorator INSTANCE = new TrunkVineDecorator();

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.TRUNK_VINE;
    }

    @Override
    public void place(TreeDecorator.Context $$0) {
        RandomSource $$1 = $$0.random();
        $$0.logs().forEach($$2 -> {
            BlockPos $$6;
            BlockPos $$5;
            BlockPos $$4;
            BlockPos $$3;
            if ($$1.nextInt(3) > 0 && $$0.isAir($$3 = $$2.west())) {
                $$0.placeVine($$3, VineBlock.EAST);
            }
            if ($$1.nextInt(3) > 0 && $$0.isAir($$4 = $$2.east())) {
                $$0.placeVine($$4, VineBlock.WEST);
            }
            if ($$1.nextInt(3) > 0 && $$0.isAir($$5 = $$2.north())) {
                $$0.placeVine($$5, VineBlock.SOUTH);
            }
            if ($$1.nextInt(3) > 0 && $$0.isAir($$6 = $$2.south())) {
                $$0.placeVine($$6, VineBlock.NORTH);
            }
        });
    }
}

