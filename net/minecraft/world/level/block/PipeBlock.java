/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class PipeBlock
extends Block {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Maps.newEnumMap(Map.of((Object)Direction.NORTH, (Object)NORTH, (Object)Direction.EAST, (Object)EAST, (Object)Direction.SOUTH, (Object)SOUTH, (Object)Direction.WEST, (Object)WEST, (Object)Direction.UP, (Object)UP, (Object)Direction.DOWN, (Object)DOWN)));
    private final Function<BlockState, VoxelShape> shapes;

    protected PipeBlock(float $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.shapes = this.makeShapes($$0);
    }

    protected abstract MapCodec<? extends PipeBlock> codec();

    private Function<BlockState, VoxelShape> makeShapes(float $$0) {
        VoxelShape $$1 = Block.cube($$0);
        Map<Direction, VoxelShape> $$22 = Shapes.rotateAll(Block.boxZ($$0, 0.0, 8.0));
        return this.getShapeForEachState($$2 -> {
            VoxelShape $$3 = $$1;
            for (Map.Entry<Direction, BooleanProperty> $$4 : PROPERTY_BY_DIRECTION.entrySet()) {
                if (!((Boolean)$$2.getValue($$4.getValue())).booleanValue()) continue;
                $$3 = Shapes.or((VoxelShape)$$22.get($$4.getKey()), $$3);
            }
            return $$3;
        });
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState $$0) {
        return false;
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.shapes.apply($$0);
    }
}

