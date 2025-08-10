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

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FenceGateBlock
extends HorizontalDirectionalBlock {
    public static final MapCodec<FenceGateBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)WoodType.CODEC.fieldOf("wood_type").forGetter($$0 -> $$0.type), FenceGateBlock.propertiesCodec()).apply((Applicative)$$02, FenceGateBlock::new));
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty IN_WALL = BlockStateProperties.IN_WALL;
    private static final Map<Direction.Axis, VoxelShape> SHAPES = Shapes.rotateHorizontalAxis(Block.cube(16.0, 16.0, 4.0));
    private static final Map<Direction.Axis, VoxelShape> SHAPES_WALL = Maps.newEnumMap(Util.mapValues(SHAPES, $$0 -> Shapes.join($$0, Block.column(16.0, 13.0, 16.0), BooleanOp.ONLY_FIRST)));
    private static final Map<Direction.Axis, VoxelShape> SHAPE_COLLISION = Shapes.rotateHorizontalAxis(Block.column(16.0, 4.0, 0.0, 24.0));
    private static final Map<Direction.Axis, VoxelShape> SHAPE_SUPPORT = Shapes.rotateHorizontalAxis(Block.column(16.0, 4.0, 5.0, 24.0));
    private static final Map<Direction.Axis, VoxelShape> SHAPE_OCCLUSION = Shapes.rotateHorizontalAxis(Shapes.or(Block.box(0.0, 5.0, 7.0, 2.0, 16.0, 9.0), Block.box(14.0, 5.0, 7.0, 16.0, 16.0, 9.0)));
    private static final Map<Direction.Axis, VoxelShape> SHAPE_OCCLUSION_WALL = Maps.newEnumMap(Util.mapValues(SHAPE_OCCLUSION, $$0 -> $$0.move(0.0, -0.1875, 0.0).optimize()));
    private final WoodType type;

    public MapCodec<FenceGateBlock> codec() {
        return CODEC;
    }

    public FenceGateBlock(WoodType $$0, BlockBehaviour.Properties $$1) {
        super($$1.sound($$0.soundType()));
        this.type = $$0;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(OPEN, false)).setValue(POWERED, false)).setValue(IN_WALL, false));
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        Direction.Axis $$4 = ((Direction)$$0.getValue(FACING)).getAxis();
        return ($$0.getValue(IN_WALL) != false ? SHAPES_WALL : SHAPES).get($$4);
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        Direction.Axis $$8 = $$4.getAxis();
        if (((Direction)$$0.getValue(FACING)).getClockWise().getAxis() == $$8) {
            boolean $$9 = this.isWall($$6) || this.isWall($$1.getBlockState($$3.relative($$4.getOpposite())));
            return (BlockState)$$0.setValue(IN_WALL, $$9);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        Direction.Axis $$3 = ((Direction)$$0.getValue(FACING)).getAxis();
        return $$0.getValue(OPEN) != false ? Shapes.empty() : SHAPE_SUPPORT.get($$3);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        Direction.Axis $$4 = ((Direction)$$0.getValue(FACING)).getAxis();
        return $$0.getValue(OPEN) != false ? Shapes.empty() : SHAPE_COLLISION.get($$4);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState $$0) {
        Direction.Axis $$1 = ((Direction)$$0.getValue(FACING)).getAxis();
        return ($$0.getValue(IN_WALL) != false ? SHAPE_OCCLUSION_WALL : SHAPE_OCCLUSION).get($$1);
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        switch ($$1) {
            case LAND: {
                return $$0.getValue(OPEN);
            }
            case WATER: {
                return false;
            }
            case AIR: {
                return $$0.getValue(OPEN);
            }
        }
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        boolean $$3 = $$1.hasNeighborSignal($$2);
        Direction $$4 = $$0.getHorizontalDirection();
        Direction.Axis $$5 = $$4.getAxis();
        boolean $$6 = $$5 == Direction.Axis.Z && (this.isWall($$1.getBlockState($$2.west())) || this.isWall($$1.getBlockState($$2.east()))) || $$5 == Direction.Axis.X && (this.isWall($$1.getBlockState($$2.north())) || this.isWall($$1.getBlockState($$2.south())));
        return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, $$4)).setValue(OPEN, $$3)).setValue(POWERED, $$3)).setValue(IN_WALL, $$6);
    }

    private boolean isWall(BlockState $$0) {
        return $$0.is(BlockTags.WALLS);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        if ($$0.getValue(OPEN).booleanValue()) {
            $$0 = (BlockState)$$0.setValue(OPEN, false);
            $$1.setBlock($$2, $$0, 10);
        } else {
            Direction $$5 = $$3.getDirection();
            if ($$0.getValue(FACING) == $$5.getOpposite()) {
                $$0 = (BlockState)$$0.setValue(FACING, $$5);
            }
            $$0 = (BlockState)$$0.setValue(OPEN, true);
            $$1.setBlock($$2, $$0, 10);
        }
        boolean $$6 = $$0.getValue(OPEN);
        $$1.playSound((Entity)$$3, $$2, $$6 ? this.type.fenceGateOpen() : this.type.fenceGateClose(), SoundSource.BLOCKS, 1.0f, $$1.getRandom().nextFloat() * 0.1f + 0.9f);
        $$1.gameEvent((Entity)$$3, $$6 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, $$2);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onExplosionHit(BlockState $$0, ServerLevel $$1, BlockPos $$2, Explosion $$3, BiConsumer<ItemStack, BlockPos> $$4) {
        if ($$3.canTriggerBlocks() && !$$0.getValue(POWERED).booleanValue()) {
            boolean $$5 = $$0.getValue(OPEN);
            $$1.setBlockAndUpdate($$2, (BlockState)$$0.setValue(OPEN, !$$5));
            $$1.playSound(null, $$2, $$5 ? this.type.fenceGateClose() : this.type.fenceGateOpen(), SoundSource.BLOCKS, 1.0f, $$1.getRandom().nextFloat() * 0.1f + 0.9f);
            $$1.gameEvent($$5 ? GameEvent.BLOCK_CLOSE : GameEvent.BLOCK_OPEN, $$2, GameEvent.Context.of($$0));
        }
        super.onExplosionHit($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    protected void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, @Nullable Orientation $$4, boolean $$5) {
        if ($$1.isClientSide) {
            return;
        }
        boolean $$6 = $$1.hasNeighborSignal($$2);
        if ($$0.getValue(POWERED) != $$6) {
            $$1.setBlock($$2, (BlockState)((BlockState)$$0.setValue(POWERED, $$6)).setValue(OPEN, $$6), 2);
            if ($$0.getValue(OPEN) != $$6) {
                $$1.playSound(null, $$2, $$6 ? this.type.fenceGateOpen() : this.type.fenceGateClose(), SoundSource.BLOCKS, 1.0f, $$1.getRandom().nextFloat() * 0.1f + 0.9f);
                $$1.gameEvent(null, $$6 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, $$2);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING, OPEN, POWERED, IN_WALL);
    }

    public static boolean connectsToDirection(BlockState $$0, Direction $$1) {
        return ((Direction)$$0.getValue(FACING)).getAxis() == $$1.getClockWise().getAxis();
    }
}

