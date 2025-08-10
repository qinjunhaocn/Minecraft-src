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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BambooStalkBlock
extends Block
implements BonemealableBlock {
    public static final MapCodec<BambooStalkBlock> CODEC = BambooStalkBlock.simpleCodec(BambooStalkBlock::new);
    private static final VoxelShape SHAPE_SMALL = Block.column(6.0, 0.0, 16.0);
    private static final VoxelShape SHAPE_LARGE = Block.column(10.0, 0.0, 16.0);
    private static final VoxelShape SHAPE_COLLISION = Block.column(3.0, 0.0, 16.0);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
    public static final EnumProperty<BambooLeaves> LEAVES = BlockStateProperties.BAMBOO_LEAVES;
    public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
    public static final int MAX_HEIGHT = 16;
    public static final int STAGE_GROWING = 0;
    public static final int STAGE_DONE_GROWING = 1;
    public static final int AGE_THIN_BAMBOO = 0;
    public static final int AGE_THICK_BAMBOO = 1;

    public MapCodec<BambooStalkBlock> codec() {
        return CODEC;
    }

    public BambooStalkBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0)).setValue(LEAVES, BambooLeaves.NONE)).setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(AGE, LEAVES, STAGE);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState $$0) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        VoxelShape $$4 = $$0.getValue(LEAVES) == BambooLeaves.LARGE ? SHAPE_LARGE : SHAPE_SMALL;
        return $$4.move($$0.getOffset($$2));
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE_COLLISION.move($$0.getOffset($$2));
    }

    @Override
    protected boolean isCollisionShapeFullBlock(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return false;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        FluidState $$1 = $$0.getLevel().getFluidState($$0.getClickedPos());
        if (!$$1.isEmpty()) {
            return null;
        }
        BlockState $$2 = $$0.getLevel().getBlockState($$0.getClickedPos().below());
        if ($$2.is(BlockTags.BAMBOO_PLANTABLE_ON)) {
            if ($$2.is(Blocks.BAMBOO_SAPLING)) {
                return (BlockState)this.defaultBlockState().setValue(AGE, 0);
            }
            if ($$2.is(Blocks.BAMBOO)) {
                int $$3 = $$2.getValue(AGE) > 0 ? 1 : 0;
                return (BlockState)this.defaultBlockState().setValue(AGE, $$3);
            }
            BlockState $$4 = $$0.getLevel().getBlockState($$0.getClickedPos().above());
            if ($$4.is(Blocks.BAMBOO)) {
                return (BlockState)this.defaultBlockState().setValue(AGE, $$4.getValue(AGE));
            }
            return Blocks.BAMBOO_SAPLING.defaultBlockState();
        }
        return null;
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.canSurvive($$1, $$2)) {
            $$1.destroyBlock($$2, true);
        }
    }

    @Override
    protected boolean isRandomlyTicking(BlockState $$0) {
        return $$0.getValue(STAGE) == 0;
    }

    @Override
    protected void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        int $$4;
        if ($$0.getValue(STAGE) != 0) {
            return;
        }
        if ($$3.nextInt(3) == 0 && $$1.isEmptyBlock($$2.above()) && $$1.getRawBrightness($$2.above(), 0) >= 9 && ($$4 = this.getHeightBelowUpToMax($$1, $$2) + 1) < 16) {
            this.growBamboo($$0, $$1, $$2, $$3, $$4);
        }
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return $$1.getBlockState($$2.below()).is(BlockTags.BAMBOO_PLANTABLE_ON);
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if (!$$0.canSurvive($$1, $$3)) {
            $$2.scheduleTick($$3, this, 1);
        }
        if ($$4 == Direction.UP && $$6.is(Blocks.BAMBOO) && $$6.getValue(AGE) > $$0.getValue(AGE)) {
            return (BlockState)$$0.cycle(AGE);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        int $$4;
        int $$3 = this.getHeightAboveUpToMax($$0, $$1);
        return $$3 + ($$4 = this.getHeightBelowUpToMax($$0, $$1)) + 1 < 16 && $$0.getBlockState($$1.above($$3)).getValue(STAGE) != 1;
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        int $$4 = this.getHeightAboveUpToMax($$0, $$2);
        int $$5 = this.getHeightBelowUpToMax($$0, $$2);
        int $$6 = $$4 + $$5 + 1;
        int $$7 = 1 + $$1.nextInt(2);
        for (int $$8 = 0; $$8 < $$7; ++$$8) {
            BlockPos $$9 = $$2.above($$4);
            BlockState $$10 = $$0.getBlockState($$9);
            if ($$6 >= 16 || $$10.getValue(STAGE) == 1 || !$$0.isEmptyBlock($$9.above())) {
                return;
            }
            this.growBamboo($$10, $$0, $$9, $$1, $$6);
            ++$$4;
            ++$$6;
        }
    }

    protected void growBamboo(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3, int $$4) {
        BlockState $$5 = $$1.getBlockState($$2.below());
        BlockPos $$6 = $$2.below(2);
        BlockState $$7 = $$1.getBlockState($$6);
        BambooLeaves $$8 = BambooLeaves.NONE;
        if ($$4 >= 1) {
            if (!$$5.is(Blocks.BAMBOO) || $$5.getValue(LEAVES) == BambooLeaves.NONE) {
                $$8 = BambooLeaves.SMALL;
            } else if ($$5.is(Blocks.BAMBOO) && $$5.getValue(LEAVES) != BambooLeaves.NONE) {
                $$8 = BambooLeaves.LARGE;
                if ($$7.is(Blocks.BAMBOO)) {
                    $$1.setBlock($$2.below(), (BlockState)$$5.setValue(LEAVES, BambooLeaves.SMALL), 3);
                    $$1.setBlock($$6, (BlockState)$$7.setValue(LEAVES, BambooLeaves.NONE), 3);
                }
            }
        }
        int $$9 = $$0.getValue(AGE) == 1 || $$7.is(Blocks.BAMBOO) ? 1 : 0;
        int $$10 = $$4 >= 11 && $$3.nextFloat() < 0.25f || $$4 == 15 ? 1 : 0;
        $$1.setBlock($$2.above(), (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(AGE, $$9)).setValue(LEAVES, $$8)).setValue(STAGE, $$10), 3);
    }

    protected int getHeightAboveUpToMax(BlockGetter $$0, BlockPos $$1) {
        int $$2;
        for ($$2 = 0; $$2 < 16 && $$0.getBlockState($$1.above($$2 + 1)).is(Blocks.BAMBOO); ++$$2) {
        }
        return $$2;
    }

    protected int getHeightBelowUpToMax(BlockGetter $$0, BlockPos $$1) {
        int $$2;
        for ($$2 = 0; $$2 < 16 && $$0.getBlockState($$1.below($$2 + 1)).is(Blocks.BAMBOO); ++$$2) {
        }
        return $$2;
    }
}

