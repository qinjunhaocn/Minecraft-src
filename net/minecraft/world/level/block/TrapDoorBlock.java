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
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrapDoorBlock
extends HorizontalDirectionalBlock
implements SimpleWaterloggedBlock {
    public static final MapCodec<TrapDoorBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)BlockSetType.CODEC.fieldOf("block_set_type").forGetter($$0 -> $$0.type), TrapDoorBlock.propertiesCodec()).apply((Applicative)$$02, TrapDoorBlock::new));
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateAll(Block.boxZ(16.0, 13.0, 16.0));
    private final BlockSetType type;

    public MapCodec<? extends TrapDoorBlock> codec() {
        return CODEC;
    }

    protected TrapDoorBlock(BlockSetType $$0, BlockBehaviour.Properties $$1) {
        super($$1.sound($$0.soundType()));
        this.type = $$0;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(OPEN, false)).setValue(HALF, Half.BOTTOM)).setValue(POWERED, false)).setValue(WATERLOGGED, false));
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES.get($$0.getValue(OPEN) != false ? $$0.getValue(FACING) : ($$0.getValue(HALF) == Half.TOP ? Direction.DOWN : Direction.UP));
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        switch ($$1) {
            case LAND: {
                return $$0.getValue(OPEN);
            }
            case WATER: {
                return $$0.getValue(WATERLOGGED);
            }
            case AIR: {
                return $$0.getValue(OPEN);
            }
        }
        return false;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        if (!this.type.canOpenByHand()) {
            return InteractionResult.PASS;
        }
        this.toggle($$0, $$1, $$2, $$3);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onExplosionHit(BlockState $$0, ServerLevel $$1, BlockPos $$2, Explosion $$3, BiConsumer<ItemStack, BlockPos> $$4) {
        if ($$3.canTriggerBlocks() && this.type.canOpenByWindCharge() && !$$0.getValue(POWERED).booleanValue()) {
            this.toggle($$0, $$1, $$2, null);
        }
        super.onExplosionHit($$0, $$1, $$2, $$3, $$4);
    }

    private void toggle(BlockState $$0, Level $$1, BlockPos $$2, @Nullable Player $$3) {
        BlockState $$4 = (BlockState)$$0.cycle(OPEN);
        $$1.setBlock($$2, $$4, 2);
        if ($$4.getValue(WATERLOGGED).booleanValue()) {
            $$1.scheduleTick($$2, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        this.playSound($$3, $$1, $$2, $$4.getValue(OPEN));
    }

    protected void playSound(@Nullable Player $$0, Level $$1, BlockPos $$2, boolean $$3) {
        $$1.playSound((Entity)$$0, $$2, $$3 ? this.type.trapdoorOpen() : this.type.trapdoorClose(), SoundSource.BLOCKS, 1.0f, $$1.getRandom().nextFloat() * 0.1f + 0.9f);
        $$1.gameEvent((Entity)$$0, $$3 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, $$2);
    }

    @Override
    protected void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, @Nullable Orientation $$4, boolean $$5) {
        if ($$1.isClientSide) {
            return;
        }
        boolean $$6 = $$1.hasNeighborSignal($$2);
        if ($$6 != $$0.getValue(POWERED)) {
            if ($$0.getValue(OPEN) != $$6) {
                $$0 = (BlockState)$$0.setValue(OPEN, $$6);
                this.playSound(null, $$1, $$2, $$6);
            }
            $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, $$6), 2);
            if ($$0.getValue(WATERLOGGED).booleanValue()) {
                $$1.scheduleTick($$2, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = this.defaultBlockState();
        FluidState $$2 = $$0.getLevel().getFluidState($$0.getClickedPos());
        Direction $$3 = $$0.getClickedFace();
        $$1 = $$0.replacingClickedOnBlock() || !$$3.getAxis().isHorizontal() ? (BlockState)((BlockState)$$1.setValue(FACING, $$0.getHorizontalDirection().getOpposite())).setValue(HALF, $$3 == Direction.UP ? Half.BOTTOM : Half.TOP) : (BlockState)((BlockState)$$1.setValue(FACING, $$3)).setValue(HALF, $$0.getClickLocation().y - (double)$$0.getClickedPos().getY() > 0.5 ? Half.TOP : Half.BOTTOM);
        if ($$0.getLevel().hasNeighborSignal($$0.getClickedPos())) {
            $$1 = (BlockState)((BlockState)$$1.setValue(OPEN, true)).setValue(POWERED, true);
        }
        return (BlockState)$$1.setValue(WATERLOGGED, $$2.getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING, OPEN, HALF, POWERED, WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$2.scheduleTick($$3, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    protected BlockSetType getType() {
        return this.type;
    }
}

