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
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireBlock
extends Block {
    public static final MapCodec<TripWireBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)BuiltInRegistries.BLOCK.byNameCodec().fieldOf("hook").forGetter($$0 -> $$0.hook), TripWireBlock.propertiesCodec()).apply((Applicative)$$02, TripWireBlock::new));
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    public static final BooleanProperty DISARMED = BlockStateProperties.DISARMED;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = CrossCollisionBlock.PROPERTY_BY_DIRECTION;
    private static final VoxelShape SHAPE_ATTACHED = Block.column(16.0, 1.0, 2.5);
    private static final VoxelShape SHAPE_NOT_ATTACHED = Block.column(16.0, 0.0, 8.0);
    private static final int RECHECK_PERIOD = 10;
    private final Block hook;

    public MapCodec<TripWireBlock> codec() {
        return CODEC;
    }

    public TripWireBlock(Block $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, false)).setValue(ATTACHED, false)).setValue(DISARMED, false)).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false));
        this.hook = $$0;
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return $$0.getValue(ATTACHED) != false ? SHAPE_ATTACHED : SHAPE_NOT_ATTACHED;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(NORTH, this.shouldConnectTo($$1.getBlockState($$2.north()), Direction.NORTH))).setValue(EAST, this.shouldConnectTo($$1.getBlockState($$2.east()), Direction.EAST))).setValue(SOUTH, this.shouldConnectTo($$1.getBlockState($$2.south()), Direction.SOUTH))).setValue(WEST, this.shouldConnectTo($$1.getBlockState($$2.west()), Direction.WEST));
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$4.getAxis().isHorizontal()) {
            return (BlockState)$$0.setValue(PROPERTY_BY_DIRECTION.get($$4), this.shouldConnectTo($$6, $$4));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$3.is($$0.getBlock())) {
            return;
        }
        this.updateSource($$1, $$2, $$0);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState $$0, ServerLevel $$1, BlockPos $$2, boolean $$3) {
        if (!$$3) {
            this.updateSource($$1, $$2, (BlockState)$$0.setValue(POWERED, true));
        }
    }

    @Override
    public BlockState playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        if (!$$0.isClientSide && !$$3.getMainHandItem().isEmpty() && $$3.getMainHandItem().is(Items.SHEARS)) {
            $$0.setBlock($$1, (BlockState)$$2.setValue(DISARMED, true), 260);
            $$0.gameEvent((Entity)$$3, GameEvent.SHEAR, $$1);
        }
        return super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    private void updateSource(Level $$0, BlockPos $$1, BlockState $$2) {
        block0: for (Direction $$3 : new Direction[]{Direction.SOUTH, Direction.WEST}) {
            for (int $$4 = 1; $$4 < 42; ++$$4) {
                BlockPos $$5 = $$1.relative($$3, $$4);
                BlockState $$6 = $$0.getBlockState($$5);
                if ($$6.is(this.hook)) {
                    if ($$6.getValue(TripWireHookBlock.FACING) != $$3.getOpposite()) continue block0;
                    TripWireHookBlock.calculateState($$0, $$5, $$6, false, true, $$4, $$2);
                    continue block0;
                }
                if (!$$6.is(this)) continue block0;
            }
        }
    }

    @Override
    protected VoxelShape getEntityInsideCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, Entity $$3) {
        return $$0.getShape($$1, $$2);
    }

    @Override
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        if ($$1.isClientSide) {
            return;
        }
        if ($$0.getValue(POWERED).booleanValue()) {
            return;
        }
        this.checkPressed($$1, $$2, List.of((Object)$$3));
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$1.getBlockState($$2).getValue(POWERED).booleanValue()) {
            return;
        }
        this.checkPressed($$1, $$2);
    }

    private void checkPressed(Level $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState($$1);
        List<Entity> $$3 = $$0.getEntities(null, $$2.getShape($$0, $$1).bounds().move($$1));
        this.checkPressed($$0, $$1, $$3);
    }

    private void checkPressed(Level $$0, BlockPos $$1, List<? extends Entity> $$2) {
        BlockState $$3 = $$0.getBlockState($$1);
        boolean $$4 = $$3.getValue(POWERED);
        boolean $$5 = false;
        if (!$$2.isEmpty()) {
            for (Entity entity : $$2) {
                if (entity.isIgnoringBlockTriggers()) continue;
                $$5 = true;
                break;
            }
        }
        if ($$5 != $$4) {
            $$3 = (BlockState)$$3.setValue(POWERED, $$5);
            $$0.setBlock($$1, $$3, 3);
            this.updateSource($$0, $$1, $$3);
        }
        if ($$5) {
            $$0.scheduleTick(new BlockPos($$1), this, 10);
        }
    }

    public boolean shouldConnectTo(BlockState $$0, Direction $$1) {
        if ($$0.is(this.hook)) {
            return $$0.getValue(TripWireHookBlock.FACING) == $$1.getOpposite();
        }
        return $$0.is(this);
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        switch ($$1) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(SOUTH))).setValue(EAST, $$0.getValue(WEST))).setValue(SOUTH, $$0.getValue(NORTH))).setValue(WEST, $$0.getValue(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(EAST))).setValue(EAST, $$0.getValue(SOUTH))).setValue(SOUTH, $$0.getValue(WEST))).setValue(WEST, $$0.getValue(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(WEST))).setValue(EAST, $$0.getValue(NORTH))).setValue(SOUTH, $$0.getValue(EAST))).setValue(WEST, $$0.getValue(SOUTH));
            }
        }
        return $$0;
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        switch ($$1) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(SOUTH))).setValue(SOUTH, $$0.getValue(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)$$0.setValue(EAST, $$0.getValue(WEST))).setValue(WEST, $$0.getValue(EAST));
            }
        }
        return super.mirror($$0, $$1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
    }
}

