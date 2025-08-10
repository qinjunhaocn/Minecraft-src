/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallHangingSignBlock
extends SignBlock {
    public static final MapCodec<WallHangingSignBlock> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)WoodType.CODEC.fieldOf("wood_type").forGetter(SignBlock::type), WallHangingSignBlock.propertiesCodec()).apply((Applicative)$$0, WallHangingSignBlock::new));
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    private static final Map<Direction.Axis, VoxelShape> SHAPES_PLANK = Shapes.rotateHorizontalAxis(Block.column(16.0, 4.0, 14.0, 16.0));
    private static final Map<Direction.Axis, VoxelShape> SHAPES = Shapes.rotateHorizontalAxis(Shapes.or(SHAPES_PLANK.get(Direction.Axis.Z), Block.column(14.0, 2.0, 0.0, 10.0)));

    public MapCodec<WallHangingSignBlock> codec() {
        return CODEC;
    }

    public WallHangingSignBlock(WoodType $$0, BlockBehaviour.Properties $$1) {
        super($$0, $$1.sound($$0.hangingSignSoundType()));
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack $$0, BlockState $$1, Level $$2, BlockPos $$3, Player $$4, InteractionHand $$5, BlockHitResult $$6) {
        SignBlockEntity $$7;
        BlockEntity blockEntity = $$2.getBlockEntity($$3);
        if (blockEntity instanceof SignBlockEntity && this.shouldTryToChainAnotherHangingSign($$1, $$4, $$6, $$7 = (SignBlockEntity)blockEntity, $$0)) {
            return InteractionResult.PASS;
        }
        return super.useItemOn($$0, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    private boolean shouldTryToChainAnotherHangingSign(BlockState $$0, Player $$1, BlockHitResult $$2, SignBlockEntity $$3, ItemStack $$4) {
        return !$$3.canExecuteClickCommands($$3.isFacingFrontText($$1), $$1) && $$4.getItem() instanceof HangingSignItem && !this.isHittingEditableSide($$2, $$0);
    }

    private boolean isHittingEditableSide(BlockHitResult $$0, BlockState $$1) {
        return $$0.getDirection().getAxis() == $$1.getValue(FACING).getAxis();
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES.get($$0.getValue(FACING).getAxis());
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return this.getShape($$0, $$1, $$2, CollisionContext.empty());
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES_PLANK.get($$0.getValue(FACING).getAxis());
    }

    public boolean canPlace(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Direction $$3 = $$0.getValue(FACING).getClockWise();
        Direction $$4 = $$0.getValue(FACING).getCounterClockWise();
        return this.canAttachTo($$1, $$0, $$2.relative($$3), $$4) || this.canAttachTo($$1, $$0, $$2.relative($$4), $$3);
    }

    public boolean canAttachTo(LevelReader $$0, BlockState $$1, BlockPos $$2, Direction $$3) {
        BlockState $$4 = $$0.getBlockState($$2);
        if ($$4.is(BlockTags.WALL_HANGING_SIGNS)) {
            return $$4.getValue(FACING).getAxis().test($$1.getValue(FACING));
        }
        return $$4.isFaceSturdy($$0, $$2, $$3, SupportType.FULL);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = this.defaultBlockState();
        FluidState $$2 = $$0.getLevel().getFluidState($$0.getClickedPos());
        Level $$3 = $$0.getLevel();
        BlockPos $$4 = $$0.getClickedPos();
        for (Direction $$5 : $$0.f()) {
            Direction $$6;
            if (!$$5.getAxis().isHorizontal() || $$5.getAxis().test($$0.getClickedFace()) || !($$1 = (BlockState)$$1.setValue(FACING, $$6 = $$5.getOpposite())).canSurvive($$3, $$4) || !this.canPlace($$1, $$3, $$4)) continue;
            return (BlockState)$$1.setValue(WATERLOGGED, $$2.getType() == Fluids.WATER);
        }
        return null;
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$4.getAxis() == $$0.getValue(FACING).getClockWise().getAxis() && !$$0.canSurvive($$1, $$3)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    public float getYRotationDegrees(BlockState $$0) {
        return $$0.getValue(FACING).toYRot();
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation($$0.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING, WATERLOGGED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new HangingSignBlockEntity($$0, $$1);
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return WallHangingSignBlock.createTickerHelper($$2, BlockEntityType.HANGING_SIGN, SignBlockEntity::tick);
    }
}

