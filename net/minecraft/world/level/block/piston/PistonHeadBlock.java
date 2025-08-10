/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block.piston;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonHeadBlock
extends DirectionalBlock {
    public static final MapCodec<PistonHeadBlock> CODEC = PistonHeadBlock.simpleCodec(PistonHeadBlock::new);
    public static final EnumProperty<PistonType> TYPE = BlockStateProperties.PISTON_TYPE;
    public static final BooleanProperty SHORT = BlockStateProperties.SHORT;
    public static final int PLATFORM_THICKNESS = 4;
    private static final VoxelShape SHAPE_PLATFORM = Block.boxZ(16.0, 0.0, 4.0);
    private static final Map<Direction, VoxelShape> SHAPES_SHORT = Shapes.rotateAll(Shapes.or(SHAPE_PLATFORM, Block.boxZ(4.0, 4.0, 16.0)));
    private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateAll(Shapes.or(SHAPE_PLATFORM, Block.boxZ(4.0, 4.0, 20.0)));

    protected MapCodec<PistonHeadBlock> codec() {
        return CODEC;
    }

    public PistonHeadBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, PistonType.DEFAULT)).setValue(SHORT, false));
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState $$0) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return ($$0.getValue(SHORT) != false ? SHAPES_SHORT : SHAPES).get($$0.getValue(FACING));
    }

    private boolean isFittingBase(BlockState $$0, BlockState $$1) {
        Block $$2 = $$0.getValue(TYPE) == PistonType.DEFAULT ? Blocks.PISTON : Blocks.STICKY_PISTON;
        return $$1.is($$2) && $$1.getValue(PistonBaseBlock.EXTENDED) != false && $$1.getValue(FACING) == $$0.getValue(FACING);
    }

    @Override
    public BlockState playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        BlockPos $$4;
        if (!$$0.isClientSide && $$3.preventsBlockDrops() && this.isFittingBase($$2, $$0.getBlockState($$4 = $$1.relative(((Direction)$$2.getValue(FACING)).getOpposite())))) {
            $$0.destroyBlock($$4, false);
        }
        return super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState $$0, ServerLevel $$1, BlockPos $$2, boolean $$3) {
        BlockPos $$4 = $$2.relative(((Direction)$$0.getValue(FACING)).getOpposite());
        if (this.isFittingBase($$0, $$1.getBlockState($$4))) {
            $$1.destroyBlock($$4, true);
        }
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$4.getOpposite() == $$0.getValue(FACING) && !$$0.canSurvive($$1, $$3)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockState $$3 = $$1.getBlockState($$2.relative(((Direction)$$0.getValue(FACING)).getOpposite()));
        return this.isFittingBase($$0, $$3) || $$3.is(Blocks.MOVING_PISTON) && $$3.getValue(FACING) == $$0.getValue(FACING);
    }

    @Override
    protected void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, @Nullable Orientation $$4, boolean $$5) {
        if ($$0.canSurvive($$1, $$2)) {
            $$1.neighborChanged($$2.relative(((Direction)$$0.getValue(FACING)).getOpposite()), $$3, ExperimentalRedstoneUtils.withFront($$4, ((Direction)$$0.getValue(FACING)).getOpposite()));
        }
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return new ItemStack($$2.getValue(TYPE) == PistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
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
        $$0.a(FACING, TYPE, SHORT);
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }
}

