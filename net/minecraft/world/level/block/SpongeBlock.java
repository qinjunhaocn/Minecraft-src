/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.redstone.Orientation;

public class SpongeBlock
extends Block {
    public static final MapCodec<SpongeBlock> CODEC = SpongeBlock.simpleCodec(SpongeBlock::new);
    public static final int MAX_DEPTH = 6;
    public static final int MAX_COUNT = 64;
    private static final Direction[] ALL_DIRECTIONS = Direction.values();

    public MapCodec<SpongeBlock> codec() {
        return CODEC;
    }

    protected SpongeBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$3.is($$0.getBlock())) {
            return;
        }
        this.tryAbsorbWater($$1, $$2);
    }

    @Override
    protected void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, @Nullable Orientation $$4, boolean $$5) {
        this.tryAbsorbWater($$1, $$2);
        super.neighborChanged($$0, $$1, $$2, $$3, $$4, $$5);
    }

    protected void tryAbsorbWater(Level $$0, BlockPos $$1) {
        if (this.removeWaterBreadthFirstSearch($$0, $$1)) {
            $$0.setBlock($$1, Blocks.WET_SPONGE.defaultBlockState(), 2);
            $$0.playSound(null, $$1, SoundEvents.SPONGE_ABSORB, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    private boolean removeWaterBreadthFirstSearch(Level $$02, BlockPos $$12) {
        return BlockPos.breadthFirstTraversal($$12, 6, 65, ($$0, $$1) -> {
            for (Direction $$2 : ALL_DIRECTIONS) {
                $$1.accept($$0.relative($$2));
            }
        }, $$2 -> {
            BucketPickup $$6;
            if ($$2.equals($$12)) {
                return BlockPos.TraversalNodeStatus.ACCEPT;
            }
            BlockState $$3 = $$02.getBlockState((BlockPos)$$2);
            FluidState $$4 = $$02.getFluidState((BlockPos)$$2);
            if (!$$4.is(FluidTags.WATER)) {
                return BlockPos.TraversalNodeStatus.SKIP;
            }
            Block $$5 = $$3.getBlock();
            if ($$5 instanceof BucketPickup && !($$6 = (BucketPickup)((Object)$$5)).pickupBlock(null, $$02, (BlockPos)$$2, $$3).isEmpty()) {
                return BlockPos.TraversalNodeStatus.ACCEPT;
            }
            if ($$3.getBlock() instanceof LiquidBlock) {
                $$02.setBlock((BlockPos)$$2, Blocks.AIR.defaultBlockState(), 3);
            } else if ($$3.is(Blocks.KELP) || $$3.is(Blocks.KELP_PLANT) || $$3.is(Blocks.SEAGRASS) || $$3.is(Blocks.TALL_SEAGRASS)) {
                BlockEntity $$7 = $$3.hasBlockEntity() ? $$02.getBlockEntity((BlockPos)$$2) : null;
                SpongeBlock.dropResources($$3, $$02, $$2, $$7);
                $$02.setBlock((BlockPos)$$2, Blocks.AIR.defaultBlockState(), 3);
            } else {
                return BlockPos.TraversalNodeStatus.SKIP;
            }
            return BlockPos.TraversalNodeStatus.ACCEPT;
        }) > 1;
    }
}

