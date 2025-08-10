/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireHookBlock
extends Block {
    public static final MapCodec<TripWireHookBlock> CODEC = TripWireHookBlock.simpleCodec(TripWireHookBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    protected static final int WIRE_DIST_MIN = 1;
    protected static final int WIRE_DIST_MAX = 42;
    private static final int RECHECK_PERIOD = 10;
    private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(Block.boxZ(6.0, 0.0, 10.0, 10.0, 16.0));

    public MapCodec<TripWireHookBlock> codec() {
        return CODEC;
    }

    public TripWireHookBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(ATTACHED, false));
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES.get($$0.getValue(FACING));
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Direction $$3 = $$0.getValue(FACING);
        BlockPos $$4 = $$2.relative($$3.getOpposite());
        BlockState $$5 = $$1.getBlockState($$4);
        return $$3.getAxis().isHorizontal() && $$5.isFaceSturdy($$1, $$4, $$3);
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$4.getOpposite() == $$0.getValue(FACING) && !$$0.canSurvive($$1, $$3)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Direction[] $$4;
        BlockState $$1 = (BlockState)((BlockState)this.defaultBlockState().setValue(POWERED, false)).setValue(ATTACHED, false);
        Level $$2 = $$0.getLevel();
        BlockPos $$3 = $$0.getClickedPos();
        for (Direction $$5 : $$4 = $$0.f()) {
            Direction $$6;
            if (!$$5.getAxis().isHorizontal() || !($$1 = (BlockState)$$1.setValue(FACING, $$6 = $$5.getOpposite())).canSurvive($$2, $$3)) continue;
            return $$1;
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, LivingEntity $$3, ItemStack $$4) {
        TripWireHookBlock.calculateState($$0, $$1, $$2, false, false, -1, null);
    }

    public static void calculateState(Level $$0, BlockPos $$1, BlockState $$2, boolean $$3, boolean $$4, int $$5, @Nullable BlockState $$6) {
        Optional<Direction> $$7 = $$2.getOptionalValue(FACING);
        if (!$$7.isPresent()) {
            return;
        }
        Direction $$8 = $$7.get();
        boolean $$9 = $$2.getOptionalValue(ATTACHED).orElse(false);
        boolean $$10 = $$2.getOptionalValue(POWERED).orElse(false);
        Block $$11 = $$2.getBlock();
        boolean $$12 = !$$3;
        boolean $$13 = false;
        int $$14 = 0;
        BlockState[] $$15 = new BlockState[42];
        for (int $$16 = 1; $$16 < 42; ++$$16) {
            BlockPos $$17 = $$1.relative($$8, $$16);
            BlockState $$18 = $$0.getBlockState($$17);
            if ($$18.is(Blocks.TRIPWIRE_HOOK)) {
                if ($$18.getValue(FACING) != $$8.getOpposite()) break;
                $$14 = $$16;
                break;
            }
            if ($$18.is(Blocks.TRIPWIRE) || $$16 == $$5) {
                if ($$16 == $$5) {
                    $$18 = MoreObjects.firstNonNull($$6, $$18);
                }
                boolean $$19 = $$18.getValue(TripWireBlock.DISARMED) == false;
                boolean $$20 = $$18.getValue(TripWireBlock.POWERED);
                $$13 |= $$19 && $$20;
                $$15[$$16] = $$18;
                if ($$16 != $$5) continue;
                $$0.scheduleTick($$1, $$11, 10);
                $$12 &= $$19;
                continue;
            }
            $$15[$$16] = null;
            $$12 = false;
        }
        BlockState $$21 = (BlockState)((BlockState)$$11.defaultBlockState().trySetValue(ATTACHED, $$12)).trySetValue(POWERED, $$13 &= ($$12 &= $$14 > 1));
        if ($$14 > 0) {
            BlockPos $$22 = $$1.relative($$8, $$14);
            Direction $$23 = $$8.getOpposite();
            $$0.setBlock($$22, (BlockState)$$21.setValue(FACING, $$23), 3);
            TripWireHookBlock.notifyNeighbors($$11, $$0, $$22, $$23);
            TripWireHookBlock.emitState($$0, $$22, $$12, $$13, $$9, $$10);
        }
        TripWireHookBlock.emitState($$0, $$1, $$12, $$13, $$9, $$10);
        if (!$$3) {
            $$0.setBlock($$1, (BlockState)$$21.setValue(FACING, $$8), 3);
            if ($$4) {
                TripWireHookBlock.notifyNeighbors($$11, $$0, $$1, $$8);
            }
        }
        if ($$9 != $$12) {
            for (int $$24 = 1; $$24 < $$14; ++$$24) {
                BlockState $$27;
                BlockPos $$25 = $$1.relative($$8, $$24);
                BlockState $$26 = $$15[$$24];
                if ($$26 == null || !($$27 = $$0.getBlockState($$25)).is(Blocks.TRIPWIRE) && !$$27.is(Blocks.TRIPWIRE_HOOK)) continue;
                $$0.setBlock($$25, (BlockState)$$26.trySetValue(ATTACHED, $$12), 3);
            }
        }
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        TripWireHookBlock.calculateState($$1, $$2, $$0, false, true, -1, null);
    }

    private static void emitState(Level $$0, BlockPos $$1, boolean $$2, boolean $$3, boolean $$4, boolean $$5) {
        if ($$3 && !$$5) {
            $$0.playSound(null, $$1, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 0.4f, 0.6f);
            $$0.gameEvent(null, GameEvent.BLOCK_ACTIVATE, $$1);
        } else if (!$$3 && $$5) {
            $$0.playSound(null, $$1, SoundEvents.TRIPWIRE_CLICK_OFF, SoundSource.BLOCKS, 0.4f, 0.5f);
            $$0.gameEvent(null, GameEvent.BLOCK_DEACTIVATE, $$1);
        } else if ($$2 && !$$4) {
            $$0.playSound(null, $$1, SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS, 0.4f, 0.7f);
            $$0.gameEvent(null, GameEvent.BLOCK_ATTACH, $$1);
        } else if (!$$2 && $$4) {
            $$0.playSound(null, $$1, SoundEvents.TRIPWIRE_DETACH, SoundSource.BLOCKS, 0.4f, 1.2f / ($$0.random.nextFloat() * 0.2f + 0.9f));
            $$0.gameEvent(null, GameEvent.BLOCK_DETACH, $$1);
        }
    }

    private static void notifyNeighbors(Block $$0, Level $$1, BlockPos $$2, Direction $$3) {
        Direction $$4 = $$3.getOpposite();
        Orientation $$5 = ExperimentalRedstoneUtils.initialOrientation($$1, $$4, Direction.UP);
        $$1.updateNeighborsAt($$2, $$0, $$5);
        $$1.updateNeighborsAt($$2.relative($$4), $$0, $$5);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState $$0, ServerLevel $$1, BlockPos $$2, boolean $$3) {
        if ($$3) {
            return;
        }
        boolean $$4 = $$0.getValue(ATTACHED);
        boolean $$5 = $$0.getValue(POWERED);
        if ($$4 || $$5) {
            TripWireHookBlock.calculateState($$1, $$2, $$0, true, false, -1, null);
        }
        if ($$5) {
            TripWireHookBlock.notifyNeighbors(this, $$1, $$2, $$0.getValue(FACING));
        }
    }

    @Override
    protected int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$0.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    protected int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        if (!$$0.getValue(POWERED).booleanValue()) {
            return 0;
        }
        if ($$0.getValue(FACING) == $$3) {
            return 15;
        }
        return 0;
    }

    @Override
    protected boolean isSignalSource(BlockState $$0) {
        return true;
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
        $$0.a(FACING, POWERED, ATTACHED);
    }
}

