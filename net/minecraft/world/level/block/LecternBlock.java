/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LecternBlock
extends BaseEntityBlock {
    public static final MapCodec<LecternBlock> CODEC = LecternBlock.simpleCodec(LecternBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty HAS_BOOK = BlockStateProperties.HAS_BOOK;
    private static final VoxelShape SHAPE_COLLISION = Shapes.or(Block.column(16.0, 0.0, 2.0), Block.column(8.0, 2.0, 14.0));
    private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(Shapes.a(Block.boxZ(16.0, 10.0, 14.0, 1.0, 5.333333), Block.boxZ(16.0, 12.0, 16.0, 5.333333, 9.666667), Block.boxZ(16.0, 14.0, 18.0, 9.666667, 14.0), SHAPE_COLLISION));
    private static final int PAGE_CHANGE_IMPULSE_TICKS = 2;

    public MapCodec<LecternBlock> codec() {
        return CODEC;
    }

    protected LecternBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(HAS_BOOK, false));
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState $$0) {
        return SHAPE_COLLISION;
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState $$0) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        CustomData $$5;
        Level $$1 = $$0.getLevel();
        ItemStack $$2 = $$0.getItemInHand();
        Player $$3 = $$0.getPlayer();
        boolean $$4 = false;
        if (!$$1.isClientSide && $$3 != null && $$3.canUseGameMasterBlocks() && ($$5 = $$2.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY)).contains("Book")) {
            $$4 = true;
        }
        return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, $$0.getHorizontalDirection().getOpposite())).setValue(HAS_BOOK, $$4);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE_COLLISION;
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES.get($$0.getValue(FACING));
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
        $$0.a(FACING, POWERED, HAS_BOOK);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new LecternBlockEntity($$0, $$1);
    }

    public static boolean tryPlaceBook(@Nullable LivingEntity $$0, Level $$1, BlockPos $$2, BlockState $$3, ItemStack $$4) {
        if (!$$3.getValue(HAS_BOOK).booleanValue()) {
            if (!$$1.isClientSide) {
                LecternBlock.placeBook($$0, $$1, $$2, $$3, $$4);
            }
            return true;
        }
        return false;
    }

    private static void placeBook(@Nullable LivingEntity $$0, Level $$1, BlockPos $$2, BlockState $$3, ItemStack $$4) {
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if ($$5 instanceof LecternBlockEntity) {
            LecternBlockEntity $$6 = (LecternBlockEntity)$$5;
            $$6.setBook($$4.consumeAndReturn(1, $$0));
            LecternBlock.resetBookState($$0, $$1, $$2, $$3, true);
            $$1.playSound(null, $$2, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    public static void resetBookState(@Nullable Entity $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        BlockState $$5 = (BlockState)((BlockState)$$3.setValue(POWERED, false)).setValue(HAS_BOOK, $$4);
        $$1.setBlock($$2, $$5, 3);
        $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$0, $$5));
        LecternBlock.updateBelow($$1, $$2, $$3);
    }

    public static void signalPageChange(Level $$0, BlockPos $$1, BlockState $$2) {
        LecternBlock.changePowered($$0, $$1, $$2, true);
        $$0.scheduleTick($$1, $$2.getBlock(), 2);
        $$0.levelEvent(1043, $$1, 0);
    }

    private static void changePowered(Level $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        $$0.setBlock($$1, (BlockState)$$2.setValue(POWERED, $$3), 3);
        LecternBlock.updateBelow($$0, $$1, $$2);
    }

    private static void updateBelow(Level $$0, BlockPos $$1, BlockState $$2) {
        Orientation $$3 = ExperimentalRedstoneUtils.initialOrientation($$0, $$2.getValue(FACING).getOpposite(), Direction.UP);
        $$0.updateNeighborsAt($$1.below(), $$2.getBlock(), $$3);
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        LecternBlock.changePowered($$1, $$2, $$0, false);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState $$0, ServerLevel $$1, BlockPos $$2, boolean $$3) {
        if ($$0.getValue(POWERED).booleanValue()) {
            LecternBlock.updateBelow($$1, $$2, $$0);
        }
    }

    @Override
    protected boolean isSignalSource(BlockState $$0) {
        return true;
    }

    @Override
    protected int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$0.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    protected int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$3 == Direction.UP && $$0.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        BlockEntity $$3;
        if ($$0.getValue(HAS_BOOK).booleanValue() && ($$3 = $$1.getBlockEntity($$2)) instanceof LecternBlockEntity) {
            return ((LecternBlockEntity)$$3).getRedstoneSignal();
        }
        return 0;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack $$0, BlockState $$1, Level $$2, BlockPos $$3, Player $$4, InteractionHand $$5, BlockHitResult $$6) {
        if ($$1.getValue(HAS_BOOK).booleanValue()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if ($$0.is(ItemTags.LECTERN_BOOKS)) {
            return LecternBlock.tryPlaceBook($$4, $$2, $$3, $$1, $$0) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        if ($$0.isEmpty() && $$5 == InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        if ($$0.getValue(HAS_BOOK).booleanValue()) {
            if (!$$1.isClientSide) {
                this.openScreen($$1, $$2, $$3);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    @Nullable
    protected MenuProvider getMenuProvider(BlockState $$0, Level $$1, BlockPos $$2) {
        if (!$$0.getValue(HAS_BOOK).booleanValue()) {
            return null;
        }
        return super.getMenuProvider($$0, $$1, $$2);
    }

    private void openScreen(Level $$0, BlockPos $$1, Player $$2) {
        BlockEntity $$3 = $$0.getBlockEntity($$1);
        if ($$3 instanceof LecternBlockEntity) {
            $$2.openMenu((LecternBlockEntity)$$3);
            $$2.awardStat(Stats.INTERACT_WITH_LECTERN);
        }
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }
}

