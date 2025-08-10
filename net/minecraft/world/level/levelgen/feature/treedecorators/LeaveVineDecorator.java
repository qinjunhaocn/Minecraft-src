/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class LeaveVineDecorator
extends TreeDecorator {
    public static final MapCodec<LeaveVineDecorator> CODEC = Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("probability").xmap(LeaveVineDecorator::new, $$0 -> Float.valueOf($$0.probability));
    private final float probability;

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.LEAVE_VINE;
    }

    public LeaveVineDecorator(float $$0) {
        this.probability = $$0;
    }

    @Override
    public void place(TreeDecorator.Context $$0) {
        RandomSource $$1 = $$0.random();
        $$0.leaves().forEach($$2 -> {
            BlockPos $$6;
            BlockPos $$5;
            BlockPos $$4;
            BlockPos $$3;
            if ($$1.nextFloat() < this.probability && $$0.isAir($$3 = $$2.west())) {
                LeaveVineDecorator.addHangingVine($$3, VineBlock.EAST, $$0);
            }
            if ($$1.nextFloat() < this.probability && $$0.isAir($$4 = $$2.east())) {
                LeaveVineDecorator.addHangingVine($$4, VineBlock.WEST, $$0);
            }
            if ($$1.nextFloat() < this.probability && $$0.isAir($$5 = $$2.north())) {
                LeaveVineDecorator.addHangingVine($$5, VineBlock.SOUTH, $$0);
            }
            if ($$1.nextFloat() < this.probability && $$0.isAir($$6 = $$2.south())) {
                LeaveVineDecorator.addHangingVine($$6, VineBlock.NORTH, $$0);
            }
        });
    }

    private static void addHangingVine(BlockPos $$0, BooleanProperty $$1, TreeDecorator.Context $$2) {
        $$2.placeVine($$0, $$1);
        $$0 = $$0.below();
        for (int $$3 = 4; $$2.isAir($$0) && $$3 > 0; --$$3) {
            $$2.placeVine($$0, $$1);
            $$0 = $$0.below();
        }
    }
}

