/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public abstract class CoralFeature
extends Feature<NoneFeatureConfiguration> {
    public CoralFeature(Codec<NoneFeatureConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> $$0) {
        RandomSource $$1 = $$0.random();
        WorldGenLevel $$2 = $$0.level();
        BlockPos $$3 = $$0.origin();
        Optional<Block> $$4 = BuiltInRegistries.BLOCK.getRandomElementOf(BlockTags.CORAL_BLOCKS, $$1).map(Holder::value);
        if ($$4.isEmpty()) {
            return false;
        }
        return this.placeFeature($$2, $$1, $$3, $$4.get().defaultBlockState());
    }

    protected abstract boolean placeFeature(LevelAccessor var1, RandomSource var2, BlockPos var3, BlockState var4);

    protected boolean placeCoralBlock(LevelAccessor $$0, RandomSource $$1, BlockPos $$22, BlockState $$32) {
        BlockPos $$4 = $$22.above();
        BlockState $$5 = $$0.getBlockState($$22);
        if (!$$5.is(Blocks.WATER) && !$$5.is(BlockTags.CORALS) || !$$0.getBlockState($$4).is(Blocks.WATER)) {
            return false;
        }
        $$0.setBlock($$22, $$32, 3);
        if ($$1.nextFloat() < 0.25f) {
            BuiltInRegistries.BLOCK.getRandomElementOf(BlockTags.CORALS, $$1).map(Holder::value).ifPresent($$2 -> $$0.setBlock($$4, $$2.defaultBlockState(), 2));
        } else if ($$1.nextFloat() < 0.05f) {
            $$0.setBlock($$4, (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, $$1.nextInt(4) + 1), 2);
        }
        for (Direction $$6 : Direction.Plane.HORIZONTAL) {
            BlockPos $$7;
            if (!($$1.nextFloat() < 0.2f) || !$$0.getBlockState($$7 = $$22.relative($$6)).is(Blocks.WATER)) continue;
            BuiltInRegistries.BLOCK.getRandomElementOf(BlockTags.WALL_CORALS, $$1).map(Holder::value).ifPresent($$3 -> {
                BlockState $$4 = $$3.defaultBlockState();
                if ($$4.hasProperty(BaseCoralWallFanBlock.FACING)) {
                    $$4 = (BlockState)$$4.setValue(BaseCoralWallFanBlock.FACING, $$6);
                }
                $$0.setBlock($$7, $$4, 2);
            });
        }
        return true;
    }
}

