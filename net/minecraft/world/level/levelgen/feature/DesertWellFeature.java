/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class DesertWellFeature
extends Feature<NoneFeatureConfiguration> {
    private static final BlockStatePredicate IS_SAND = BlockStatePredicate.forBlock(Blocks.SAND);
    private final BlockState sand = Blocks.SAND.defaultBlockState();
    private final BlockState sandSlab = Blocks.SANDSTONE_SLAB.defaultBlockState();
    private final BlockState sandstone = Blocks.SANDSTONE.defaultBlockState();
    private final BlockState water = Blocks.WATER.defaultBlockState();

    public DesertWellFeature(Codec<NoneFeatureConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> $$0) {
        WorldGenLevel $$1 = $$0.level();
        BlockPos $$2 = $$0.origin();
        $$2 = $$2.above();
        while ($$1.isEmptyBlock($$2) && $$2.getY() > $$1.getMinY() + 2) {
            $$2 = $$2.below();
        }
        if (!IS_SAND.test($$1.getBlockState($$2))) {
            return false;
        }
        for (int $$3 = -2; $$3 <= 2; ++$$3) {
            for (int $$4 = -2; $$4 <= 2; ++$$4) {
                if (!$$1.isEmptyBlock($$2.offset($$3, -1, $$4)) || !$$1.isEmptyBlock($$2.offset($$3, -2, $$4))) continue;
                return false;
            }
        }
        for (int $$5 = -2; $$5 <= 0; ++$$5) {
            for (int $$6 = -2; $$6 <= 2; ++$$6) {
                for (int $$7 = -2; $$7 <= 2; ++$$7) {
                    $$1.setBlock($$2.offset($$6, $$5, $$7), this.sandstone, 2);
                }
            }
        }
        $$1.setBlock($$2, this.water, 2);
        for (Direction $$8 : Direction.Plane.HORIZONTAL) {
            $$1.setBlock($$2.relative($$8), this.water, 2);
        }
        BlockPos $$9 = $$2.below();
        $$1.setBlock($$9, this.sand, 2);
        for (Direction $$10 : Direction.Plane.HORIZONTAL) {
            $$1.setBlock($$9.relative($$10), this.sand, 2);
        }
        for (int $$11 = -2; $$11 <= 2; ++$$11) {
            for (int $$12 = -2; $$12 <= 2; ++$$12) {
                if ($$11 != -2 && $$11 != 2 && $$12 != -2 && $$12 != 2) continue;
                $$1.setBlock($$2.offset($$11, 1, $$12), this.sandstone, 2);
            }
        }
        $$1.setBlock($$2.offset(2, 1, 0), this.sandSlab, 2);
        $$1.setBlock($$2.offset(-2, 1, 0), this.sandSlab, 2);
        $$1.setBlock($$2.offset(0, 1, 2), this.sandSlab, 2);
        $$1.setBlock($$2.offset(0, 1, -2), this.sandSlab, 2);
        for (int $$13 = -1; $$13 <= 1; ++$$13) {
            for (int $$14 = -1; $$14 <= 1; ++$$14) {
                if ($$13 == 0 && $$14 == 0) {
                    $$1.setBlock($$2.offset($$13, 4, $$14), this.sandstone, 2);
                    continue;
                }
                $$1.setBlock($$2.offset($$13, 4, $$14), this.sandSlab, 2);
            }
        }
        for (int $$15 = 1; $$15 <= 3; ++$$15) {
            $$1.setBlock($$2.offset(-1, $$15, -1), this.sandstone, 2);
            $$1.setBlock($$2.offset(-1, $$15, 1), this.sandstone, 2);
            $$1.setBlock($$2.offset(1, $$15, -1), this.sandstone, 2);
            $$1.setBlock($$2.offset(1, $$15, 1), this.sandstone, 2);
        }
        BlockPos $$16 = $$2;
        List $$17 = List.of((Object)$$16, (Object)$$16.east(), (Object)$$16.south(), (Object)$$16.west(), (Object)$$16.north());
        RandomSource $$18 = $$0.random();
        DesertWellFeature.placeSusSand($$1, ((BlockPos)Util.getRandom($$17, $$18)).below(1));
        DesertWellFeature.placeSusSand($$1, ((BlockPos)Util.getRandom($$17, $$18)).below(2));
        return true;
    }

    private static void placeSusSand(WorldGenLevel $$0, BlockPos $$12) {
        $$0.setBlock($$12, Blocks.SUSPICIOUS_SAND.defaultBlockState(), 3);
        $$0.getBlockEntity($$12, BlockEntityType.BRUSHABLE_BLOCK).ifPresent($$1 -> $$1.setLootTable(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY, $$12.asLong()));
    }
}

