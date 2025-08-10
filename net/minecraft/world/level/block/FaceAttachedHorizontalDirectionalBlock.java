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
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class FaceAttachedHorizontalDirectionalBlock
extends HorizontalDirectionalBlock {
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;

    protected FaceAttachedHorizontalDirectionalBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    protected abstract MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec();

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return FaceAttachedHorizontalDirectionalBlock.canAttach($$1, $$2, FaceAttachedHorizontalDirectionalBlock.getConnectedDirection($$0).getOpposite());
    }

    public static boolean canAttach(LevelReader $$0, BlockPos $$1, Direction $$2) {
        BlockPos $$3 = $$1.relative($$2);
        return $$0.getBlockState($$3).isFaceSturdy($$0, $$3, $$2.getOpposite());
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        for (Direction $$1 : $$0.f()) {
            BlockState $$3;
            if ($$1.getAxis() == Direction.Axis.Y) {
                BlockState $$2 = (BlockState)((BlockState)this.defaultBlockState().setValue(FACE, $$1 == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR)).setValue(FACING, $$0.getHorizontalDirection());
            } else {
                $$3 = (BlockState)((BlockState)this.defaultBlockState().setValue(FACE, AttachFace.WALL)).setValue(FACING, $$1.getOpposite());
            }
            if (!$$3.canSurvive($$0.getLevel(), $$0.getClickedPos())) continue;
            return $$3;
        }
        return null;
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if (FaceAttachedHorizontalDirectionalBlock.getConnectedDirection($$0).getOpposite() == $$4 && !$$0.canSurvive($$1, $$3)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    protected static Direction getConnectedDirection(BlockState $$0) {
        switch ($$0.getValue(FACE)) {
            case CEILING: {
                return Direction.DOWN;
            }
            case FLOOR: {
                return Direction.UP;
            }
        }
        return (Direction)$$0.getValue(FACING);
    }
}

