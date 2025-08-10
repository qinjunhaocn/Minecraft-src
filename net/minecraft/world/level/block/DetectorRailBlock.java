/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;

public class DetectorRailBlock
extends BaseRailBlock {
    public static final MapCodec<DetectorRailBlock> CODEC = DetectorRailBlock.simpleCodec(DetectorRailBlock::new);
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final int PRESSED_CHECK_PERIOD = 20;

    public MapCodec<DetectorRailBlock> codec() {
        return CODEC;
    }

    public DetectorRailBlock(BlockBehaviour.Properties $$0) {
        super(true, $$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, false)).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(WATERLOGGED, false));
    }

    @Override
    protected boolean isSignalSource(BlockState $$0) {
        return true;
    }

    @Override
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        if ($$1.isClientSide) {
            return;
        }
        if ($$0.getValue(POWERED).booleanValue()) {
            return;
        }
        this.checkPressed($$1, $$2, $$0);
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.getValue(POWERED).booleanValue()) {
            return;
        }
        this.checkPressed($$1, $$2, $$0);
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
        return $$3 == Direction.UP ? 15 : 0;
    }

    private void checkPressed(Level $$02, BlockPos $$1, BlockState $$2) {
        if (!this.canSurvive($$2, $$02, $$1)) {
            return;
        }
        boolean $$3 = $$2.getValue(POWERED);
        boolean $$4 = false;
        List<AbstractMinecart> $$5 = this.getInteractingMinecartOfType($$02, $$1, AbstractMinecart.class, $$0 -> true);
        if (!$$5.isEmpty()) {
            $$4 = true;
        }
        if ($$4 && !$$3) {
            BlockState $$6 = (BlockState)$$2.setValue(POWERED, true);
            $$02.setBlock($$1, $$6, 3);
            this.updatePowerToConnected($$02, $$1, $$6, true);
            $$02.updateNeighborsAt($$1, this);
            $$02.updateNeighborsAt($$1.below(), this);
            $$02.setBlocksDirty($$1, $$2, $$6);
        }
        if (!$$4 && $$3) {
            BlockState $$7 = (BlockState)$$2.setValue(POWERED, false);
            $$02.setBlock($$1, $$7, 3);
            this.updatePowerToConnected($$02, $$1, $$7, false);
            $$02.updateNeighborsAt($$1, this);
            $$02.updateNeighborsAt($$1.below(), this);
            $$02.setBlocksDirty($$1, $$2, $$7);
        }
        if ($$4) {
            $$02.scheduleTick($$1, this, 20);
        }
        $$02.updateNeighbourForOutputSignal($$1, this);
    }

    protected void updatePowerToConnected(Level $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        RailState $$4 = new RailState($$0, $$1, $$2);
        List<BlockPos> $$5 = $$4.getConnections();
        for (BlockPos $$6 : $$5) {
            BlockState $$7 = $$0.getBlockState($$6);
            $$0.neighborChanged($$7, $$6, $$7.getBlock(), null, false);
        }
    }

    @Override
    protected void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$3.is($$0.getBlock())) {
            return;
        }
        BlockState $$5 = this.updateState($$0, $$1, $$2, $$4);
        this.checkPressed($$1, $$2, $$5);
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState $$02, Level $$1, BlockPos $$2) {
        if ($$02.getValue(POWERED).booleanValue()) {
            List<MinecartCommandBlock> $$3 = this.getInteractingMinecartOfType($$1, $$2, MinecartCommandBlock.class, $$0 -> true);
            if (!$$3.isEmpty()) {
                return $$3.get(0).getCommandBlock().getSuccessCount();
            }
            List<AbstractMinecart> $$4 = this.getInteractingMinecartOfType($$1, $$2, AbstractMinecart.class, EntitySelector.CONTAINER_ENTITY_SELECTOR);
            if (!$$4.isEmpty()) {
                return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)((Object)$$4.get(0)));
            }
        }
        return 0;
    }

    private <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(Level $$0, BlockPos $$1, Class<T> $$2, Predicate<Entity> $$3) {
        return $$0.getEntitiesOfClass($$2, this.getSearchBB($$1), $$3);
    }

    private AABB getSearchBB(BlockPos $$0) {
        double $$1 = 0.2;
        return new AABB((double)$$0.getX() + 0.2, $$0.getY(), (double)$$0.getZ() + 0.2, (double)($$0.getX() + 1) - 0.2, (double)($$0.getY() + 1) - 0.2, (double)($$0.getZ() + 1) - 0.2);
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        RailShape $$2 = $$0.getValue(SHAPE);
        RailShape $$3 = this.rotate($$2, $$1);
        return (BlockState)$$0.setValue(SHAPE, $$3);
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        RailShape $$2 = $$0.getValue(SHAPE);
        RailShape $$3 = this.mirror($$2, $$1);
        return (BlockState)$$0.setValue(SHAPE, $$3);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(SHAPE, POWERED, WATERLOGGED);
    }
}

