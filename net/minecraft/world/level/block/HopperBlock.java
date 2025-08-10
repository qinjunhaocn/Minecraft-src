/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HopperBlock
extends BaseEntityBlock {
    public static final MapCodec<HopperBlock> CODEC = HopperBlock.simpleCodec(HopperBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING_HOPPER;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    private final Function<BlockState, VoxelShape> shapes;
    private final Map<Direction, VoxelShape> interactionShapes;

    public MapCodec<HopperBlock> codec() {
        return CODEC;
    }

    public HopperBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.DOWN)).setValue(ENABLED, true));
        VoxelShape $$1 = Block.column(12.0, 11.0, 16.0);
        this.shapes = this.makeShapes($$1);
        this.interactionShapes = ImmutableMap.builderWithExpectedSize(5).putAll(Shapes.rotateHorizontal(Shapes.or($$1, Block.boxZ(4.0, 8.0, 10.0, 0.0, 4.0)))).put(Direction.DOWN, $$1).build();
    }

    private Function<BlockState, VoxelShape> makeShapes(VoxelShape $$0) {
        VoxelShape $$1 = Shapes.or(Block.column(16.0, 10.0, 16.0), Block.column(8.0, 4.0, 10.0));
        VoxelShape $$22 = Shapes.join($$1, $$0, BooleanOp.ONLY_FIRST);
        Map<Direction, VoxelShape> $$3 = Shapes.rotateAll(Block.boxZ(4.0, 4.0, 8.0, 0.0, 8.0), new Vec3(8.0, 6.0, 8.0).scale(0.0625));
        return this.a($$2 -> Shapes.or($$22, Shapes.join((VoxelShape)$$3.get($$2.getValue(FACING)), Shapes.block(), BooleanOp.AND)), ENABLED);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.shapes.apply($$0);
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return this.interactionShapes.get($$0.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Direction $$1 = $$0.getClickedFace().getOpposite();
        return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, $$1.getAxis() == Direction.Axis.Y ? Direction.DOWN : $$1)).setValue(ENABLED, true);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new HopperBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return $$0.isClientSide ? null : HopperBlock.createTickerHelper($$2, BlockEntityType.HOPPER, HopperBlockEntity::pushItemsTick);
    }

    @Override
    protected void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$3.is($$0.getBlock())) {
            return;
        }
        this.checkPoweredState($$1, $$2, $$0);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        BlockEntity blockEntity;
        if (!$$1.isClientSide && (blockEntity = $$1.getBlockEntity($$2)) instanceof HopperBlockEntity) {
            HopperBlockEntity $$5 = (HopperBlockEntity)blockEntity;
            $$3.openMenu($$5);
            $$3.awardStat(Stats.INSPECT_HOPPER);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, @Nullable Orientation $$4, boolean $$5) {
        this.checkPoweredState($$1, $$2, $$0);
    }

    private void checkPoweredState(Level $$0, BlockPos $$1, BlockState $$2) {
        boolean $$3;
        boolean bl = $$3 = !$$0.hasNeighborSignal($$1);
        if ($$3 != $$2.getValue(ENABLED)) {
            $$0.setBlock($$1, (BlockState)$$2.setValue(ENABLED, $$3), 2);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState $$0, ServerLevel $$1, BlockPos $$2, boolean $$3) {
        Containers.updateNeighboursAfterDestroy($$0, $$1, $$2);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity($$1.getBlockEntity($$2));
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
        $$0.a(FACING, ENABLED);
    }

    @Override
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if ($$5 instanceof HopperBlockEntity) {
            HopperBlockEntity.entityInside($$1, $$2, $$0, $$3, (HopperBlockEntity)$$5);
        }
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }
}

