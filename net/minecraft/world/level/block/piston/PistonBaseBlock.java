/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block.piston;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonBaseBlock
extends DirectionalBlock {
    public static final MapCodec<PistonBaseBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.BOOL.fieldOf("sticky").forGetter($$0 -> $$0.isSticky), PistonBaseBlock.propertiesCodec()).apply((Applicative)$$02, PistonBaseBlock::new));
    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
    public static final int TRIGGER_EXTEND = 0;
    public static final int TRIGGER_CONTRACT = 1;
    public static final int TRIGGER_DROP = 2;
    public static final int PLATFORM_THICKNESS = 4;
    private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateAll(Block.boxZ(16.0, 4.0, 16.0));
    private final boolean isSticky;

    public MapCodec<PistonBaseBlock> codec() {
        return CODEC;
    }

    public PistonBaseBlock(boolean $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(EXTENDED, false));
        this.isSticky = $$0;
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        if ($$0.getValue(EXTENDED).booleanValue()) {
            return SHAPES.get($$0.getValue(FACING));
        }
        return Shapes.block();
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, LivingEntity $$3, ItemStack $$4) {
        if (!$$0.isClientSide) {
            this.checkIfExtend($$0, $$1, $$2);
        }
    }

    @Override
    protected void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, @Nullable Orientation $$4, boolean $$5) {
        if (!$$1.isClientSide) {
            this.checkIfExtend($$1, $$2, $$0);
        }
    }

    @Override
    protected void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$3.is($$0.getBlock())) {
            return;
        }
        if (!$$1.isClientSide && $$1.getBlockEntity($$2) == null) {
            this.checkIfExtend($$1, $$2, $$0);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, $$0.getNearestLookingDirection().getOpposite())).setValue(EXTENDED, false);
    }

    private void checkIfExtend(Level $$0, BlockPos $$1, BlockState $$2) {
        Direction $$3 = (Direction)$$2.getValue(FACING);
        boolean $$4 = this.getNeighborSignal($$0, $$1, $$3);
        if ($$4 && !$$2.getValue(EXTENDED).booleanValue()) {
            if (new PistonStructureResolver($$0, $$1, $$3, true).resolve()) {
                $$0.blockEvent($$1, this, 0, $$3.get3DDataValue());
            }
        } else if (!$$4 && $$2.getValue(EXTENDED).booleanValue()) {
            PistonMovingBlockEntity $$9;
            BlockEntity $$8;
            BlockPos $$5 = $$1.relative($$3, 2);
            BlockState $$6 = $$0.getBlockState($$5);
            int $$7 = 1;
            if ($$6.is(Blocks.MOVING_PISTON) && $$6.getValue(FACING) == $$3 && ($$8 = $$0.getBlockEntity($$5)) instanceof PistonMovingBlockEntity && ($$9 = (PistonMovingBlockEntity)$$8).isExtending() && ($$9.getProgress(0.0f) < 0.5f || $$0.getGameTime() == $$9.getLastTicked() || ((ServerLevel)$$0).isHandlingTick())) {
                $$7 = 2;
            }
            $$0.blockEvent($$1, this, $$7, $$3.get3DDataValue());
        }
    }

    private boolean getNeighborSignal(SignalGetter $$0, BlockPos $$1, Direction $$2) {
        for (Direction $$3 : Direction.values()) {
            if ($$3 == $$2 || !$$0.hasSignal($$1.relative($$3), $$3)) continue;
            return true;
        }
        if ($$0.hasSignal($$1, Direction.DOWN)) {
            return true;
        }
        BlockPos $$4 = $$1.above();
        for (Direction $$5 : Direction.values()) {
            if ($$5 == Direction.DOWN || !$$0.hasSignal($$4.relative($$5), $$5)) continue;
            return true;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected boolean triggerEvent(BlockState $$0, Level $$1, BlockPos $$2, int $$3, int $$4) {
        Direction $$5 = (Direction)$$0.getValue(FACING);
        BlockState $$6 = (BlockState)$$0.setValue(EXTENDED, true);
        if (!$$1.isClientSide) {
            boolean $$7 = this.getNeighborSignal($$1, $$2, $$5);
            if ($$7 && ($$3 == 1 || $$3 == 2)) {
                $$1.setBlock($$2, $$6, 2);
                return false;
            }
            if (!$$7 && $$3 == 0) {
                return false;
            }
        }
        if ($$3 == 0) {
            if (!this.moveBlocks($$1, $$2, $$5, true)) return false;
            $$1.setBlock($$2, $$6, 67);
            $$1.playSound(null, $$2, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5f, $$1.random.nextFloat() * 0.25f + 0.6f);
            $$1.gameEvent(GameEvent.BLOCK_ACTIVATE, $$2, GameEvent.Context.of($$6));
            return true;
        } else {
            if ($$3 != 1 && $$3 != 2) return true;
            BlockEntity $$8 = $$1.getBlockEntity($$2.relative($$5));
            if ($$8 instanceof PistonMovingBlockEntity) {
                ((PistonMovingBlockEntity)$$8).finalTick();
            }
            BlockState $$9 = (BlockState)((BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, $$5)).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            $$1.setBlock($$2, $$9, 276);
            $$1.setBlockEntity(MovingPistonBlock.newMovingBlockEntity($$2, $$9, (BlockState)this.defaultBlockState().setValue(FACING, Direction.from3DDataValue($$4 & 7)), $$5, false, true));
            $$1.updateNeighborsAt($$2, $$9.getBlock());
            $$9.updateNeighbourShapes($$1, $$2, 2);
            if (this.isSticky) {
                PistonMovingBlockEntity $$14;
                BlockEntity $$13;
                BlockPos $$10 = $$2.offset($$5.getStepX() * 2, $$5.getStepY() * 2, $$5.getStepZ() * 2);
                BlockState $$11 = $$1.getBlockState($$10);
                boolean $$12 = false;
                if ($$11.is(Blocks.MOVING_PISTON) && ($$13 = $$1.getBlockEntity($$10)) instanceof PistonMovingBlockEntity && ($$14 = (PistonMovingBlockEntity)$$13).getDirection() == $$5 && $$14.isExtending()) {
                    $$14.finalTick();
                    $$12 = true;
                }
                if (!$$12) {
                    if ($$3 == 1 && !$$11.isAir() && PistonBaseBlock.isPushable($$11, $$1, $$10, $$5.getOpposite(), false, $$5) && ($$11.getPistonPushReaction() == PushReaction.NORMAL || $$11.is(Blocks.PISTON) || $$11.is(Blocks.STICKY_PISTON))) {
                        this.moveBlocks($$1, $$2, $$5, false);
                    } else {
                        $$1.removeBlock($$2.relative($$5), false);
                    }
                }
            } else {
                $$1.removeBlock($$2.relative($$5), false);
            }
            $$1.playSound(null, $$2, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5f, $$1.random.nextFloat() * 0.15f + 0.6f);
            $$1.gameEvent(GameEvent.BLOCK_DEACTIVATE, $$2, GameEvent.Context.of($$9));
        }
        return true;
    }

    public static boolean isPushable(BlockState $$0, Level $$1, BlockPos $$2, Direction $$3, boolean $$4, Direction $$5) {
        if ($$2.getY() < $$1.getMinY() || $$2.getY() > $$1.getMaxY() || !$$1.getWorldBorder().isWithinBounds($$2)) {
            return false;
        }
        if ($$0.isAir()) {
            return true;
        }
        if ($$0.is(Blocks.OBSIDIAN) || $$0.is(Blocks.CRYING_OBSIDIAN) || $$0.is(Blocks.RESPAWN_ANCHOR) || $$0.is(Blocks.REINFORCED_DEEPSLATE)) {
            return false;
        }
        if ($$3 == Direction.DOWN && $$2.getY() == $$1.getMinY()) {
            return false;
        }
        if ($$3 == Direction.UP && $$2.getY() == $$1.getMaxY()) {
            return false;
        }
        if ($$0.is(Blocks.PISTON) || $$0.is(Blocks.STICKY_PISTON)) {
            if ($$0.getValue(EXTENDED).booleanValue()) {
                return false;
            }
        } else {
            if ($$0.getDestroySpeed($$1, $$2) == -1.0f) {
                return false;
            }
            switch ($$0.getPistonPushReaction()) {
                case BLOCK: {
                    return false;
                }
                case DESTROY: {
                    return $$4;
                }
                case PUSH_ONLY: {
                    return $$3 == $$5;
                }
            }
        }
        return !$$0.hasBlockEntity();
    }

    /*
     * WARNING - void declaration
     */
    private boolean moveBlocks(Level $$0, BlockPos $$1, Direction $$2, boolean $$3) {
        void var16_30;
        void var16_28;
        PistonStructureResolver $$5;
        BlockPos $$4 = $$1.relative($$2);
        if (!$$3 && $$0.getBlockState($$4).is(Blocks.PISTON_HEAD)) {
            $$0.setBlock($$4, Blocks.AIR.defaultBlockState(), 276);
        }
        if (!($$5 = new PistonStructureResolver($$0, $$1, $$2, $$3)).resolve()) {
            return false;
        }
        HashMap<BlockPos, BlockState> $$6 = Maps.newHashMap();
        List<BlockPos> $$7 = $$5.getToPush();
        ArrayList<BlockState> $$8 = Lists.newArrayList();
        for (BlockPos $$9 : $$7) {
            BlockState $$10 = $$0.getBlockState($$9);
            $$8.add($$10);
            $$6.put($$9, $$10);
        }
        List<BlockPos> $$11 = $$5.getToDestroy();
        BlockState[] $$12 = new BlockState[$$7.size() + $$11.size()];
        Direction $$13 = $$3 ? $$2 : $$2.getOpposite();
        int $$14 = 0;
        for (int $$15 = $$11.size() - 1; $$15 >= 0; --$$15) {
            BlockPos $$16 = $$11.get($$15);
            BlockState blockState = $$0.getBlockState($$16);
            BlockEntity $$18 = blockState.hasBlockEntity() ? $$0.getBlockEntity($$16) : null;
            PistonBaseBlock.dropResources(blockState, $$0, $$16, $$18);
            if (!blockState.is(BlockTags.FIRE) && $$0.isClientSide()) {
                $$0.levelEvent(2001, $$16, PistonBaseBlock.getId(blockState));
            }
            $$0.setBlock($$16, Blocks.AIR.defaultBlockState(), 18);
            $$0.gameEvent(GameEvent.BLOCK_DESTROY, $$16, GameEvent.Context.of(blockState));
            $$12[$$14++] = blockState;
        }
        for (int $$19 = $$7.size() - 1; $$19 >= 0; --$$19) {
            BlockPos $$20 = $$7.get($$19);
            BlockState blockState = $$0.getBlockState($$20);
            $$20 = $$20.relative($$13);
            $$6.remove($$20);
            BlockState $$22 = (BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(FACING, $$2);
            $$0.setBlock($$20, $$22, 324);
            $$0.setBlockEntity(MovingPistonBlock.newMovingBlockEntity($$20, $$22, (BlockState)$$8.get($$19), $$2, $$3, false));
            $$12[$$14++] = blockState;
        }
        if ($$3) {
            PistonType $$23 = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState $$24 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, $$2)).setValue(PistonHeadBlock.TYPE, $$23);
            BlockState blockState = (BlockState)((BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, $$2)).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            $$6.remove($$4);
            $$0.setBlock($$4, blockState, 324);
            $$0.setBlockEntity(MovingPistonBlock.newMovingBlockEntity($$4, blockState, $$24, $$2, true, true));
        }
        BlockState $$26 = Blocks.AIR.defaultBlockState();
        for (BlockPos blockPos : $$6.keySet()) {
            $$0.setBlock(blockPos, $$26, 82);
        }
        for (Map.Entry entry : $$6.entrySet()) {
            BlockPos $$29 = (BlockPos)entry.getKey();
            BlockState $$30 = (BlockState)entry.getValue();
            $$30.updateIndirectNeighbourShapes($$0, $$29, 2);
            $$26.updateNeighbourShapes($$0, $$29, 2);
            $$26.updateIndirectNeighbourShapes($$0, $$29, 2);
        }
        Orientation $$31 = ExperimentalRedstoneUtils.initialOrientation($$0, $$5.getPushDirection(), null);
        $$14 = 0;
        int n = $$11.size() - 1;
        while (var16_28 >= 0) {
            BlockState $$33 = $$12[$$14++];
            BlockPos $$34 = $$11.get((int)var16_28);
            if ($$0 instanceof ServerLevel) {
                ServerLevel $$35 = (ServerLevel)$$0;
                $$33.affectNeighborsAfterRemoval($$35, $$34, false);
            }
            $$33.updateIndirectNeighbourShapes($$0, $$34, 2);
            $$0.updateNeighborsAt($$34, $$33.getBlock(), $$31);
            --var16_28;
        }
        int n2 = $$7.size() - 1;
        while (var16_30 >= 0) {
            $$0.updateNeighborsAt($$7.get((int)var16_30), $$12[$$14++].getBlock(), $$31);
            --var16_30;
        }
        if ($$3) {
            $$0.updateNeighborsAt($$4, Blocks.PISTON_HEAD, $$31);
        }
        return true;
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate((Direction)$$0.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation((Direction)$$0.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING, EXTENDED);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState $$0) {
        return $$0.getValue(EXTENDED);
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }
}

