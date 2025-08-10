/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

public class PoweredRailBlock
extends BaseRailBlock {
    public static final MapCodec<PoweredRailBlock> CODEC = PoweredRailBlock.simpleCodec(PoweredRailBlock::new);
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public MapCodec<PoweredRailBlock> codec() {
        return CODEC;
    }

    protected PoweredRailBlock(BlockBehaviour.Properties $$0) {
        super(true, $$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(POWERED, false)).setValue(WATERLOGGED, false));
    }

    protected boolean findPoweredRailSignal(Level $$0, BlockPos $$1, BlockState $$2, boolean $$3, int $$4) {
        if ($$4 >= 8) {
            return false;
        }
        int $$5 = $$1.getX();
        int $$6 = $$1.getY();
        int $$7 = $$1.getZ();
        boolean $$8 = true;
        RailShape $$9 = $$2.getValue(SHAPE);
        switch ($$9) {
            case NORTH_SOUTH: {
                if ($$3) {
                    ++$$7;
                    break;
                }
                --$$7;
                break;
            }
            case EAST_WEST: {
                if ($$3) {
                    --$$5;
                    break;
                }
                ++$$5;
                break;
            }
            case ASCENDING_EAST: {
                if ($$3) {
                    --$$5;
                } else {
                    ++$$5;
                    ++$$6;
                    $$8 = false;
                }
                $$9 = RailShape.EAST_WEST;
                break;
            }
            case ASCENDING_WEST: {
                if ($$3) {
                    --$$5;
                    ++$$6;
                    $$8 = false;
                } else {
                    ++$$5;
                }
                $$9 = RailShape.EAST_WEST;
                break;
            }
            case ASCENDING_NORTH: {
                if ($$3) {
                    ++$$7;
                } else {
                    --$$7;
                    ++$$6;
                    $$8 = false;
                }
                $$9 = RailShape.NORTH_SOUTH;
                break;
            }
            case ASCENDING_SOUTH: {
                if ($$3) {
                    ++$$7;
                    ++$$6;
                    $$8 = false;
                } else {
                    --$$7;
                }
                $$9 = RailShape.NORTH_SOUTH;
            }
        }
        if (this.isSameRailWithPower($$0, new BlockPos($$5, $$6, $$7), $$3, $$4, $$9)) {
            return true;
        }
        return $$8 && this.isSameRailWithPower($$0, new BlockPos($$5, $$6 - 1, $$7), $$3, $$4, $$9);
    }

    protected boolean isSameRailWithPower(Level $$0, BlockPos $$1, boolean $$2, int $$3, RailShape $$4) {
        BlockState $$5 = $$0.getBlockState($$1);
        if (!$$5.is(this)) {
            return false;
        }
        RailShape $$6 = $$5.getValue(SHAPE);
        if ($$4 == RailShape.EAST_WEST && ($$6 == RailShape.NORTH_SOUTH || $$6 == RailShape.ASCENDING_NORTH || $$6 == RailShape.ASCENDING_SOUTH)) {
            return false;
        }
        if ($$4 == RailShape.NORTH_SOUTH && ($$6 == RailShape.EAST_WEST || $$6 == RailShape.ASCENDING_EAST || $$6 == RailShape.ASCENDING_WEST)) {
            return false;
        }
        if ($$5.getValue(POWERED).booleanValue()) {
            if ($$0.hasNeighborSignal($$1)) {
                return true;
            }
            return this.findPoweredRailSignal($$0, $$1, $$5, $$2, $$3 + 1);
        }
        return false;
    }

    @Override
    protected void updateState(BlockState $$0, Level $$1, BlockPos $$2, Block $$3) {
        boolean $$5;
        boolean $$4 = $$0.getValue(POWERED);
        boolean bl = $$5 = $$1.hasNeighborSignal($$2) || this.findPoweredRailSignal($$1, $$2, $$0, true, 0) || this.findPoweredRailSignal($$1, $$2, $$0, false, 0);
        if ($$5 != $$4) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, $$5), 3);
            $$1.updateNeighborsAt($$2.below(), this);
            if ($$0.getValue(SHAPE).isSlope()) {
                $$1.updateNeighborsAt($$2.above(), this);
            }
        }
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
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

