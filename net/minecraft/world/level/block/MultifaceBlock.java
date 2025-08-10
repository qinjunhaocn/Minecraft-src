/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MultifaceBlock
extends Block
implements SimpleWaterloggedBlock {
    public static final MapCodec<MultifaceBlock> CODEC = MultifaceBlock.simpleCodec(MultifaceBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
    protected static final Direction[] DIRECTIONS = Direction.values();
    private final Function<BlockState, VoxelShape> shapes;
    private final boolean canRotate;
    private final boolean canMirrorX;
    private final boolean canMirrorZ;

    protected MapCodec<? extends MultifaceBlock> codec() {
        return CODEC;
    }

    public MultifaceBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState(MultifaceBlock.getDefaultMultifaceState(this.stateDefinition));
        this.shapes = this.makeShapes();
        this.canRotate = Direction.Plane.HORIZONTAL.stream().allMatch(this::isFaceSupported);
        this.canMirrorX = Direction.Plane.HORIZONTAL.stream().filter(Direction.Axis.X).filter(this::isFaceSupported).count() % 2L == 0L;
        this.canMirrorZ = Direction.Plane.HORIZONTAL.stream().filter(Direction.Axis.Z).filter(this::isFaceSupported).count() % 2L == 0L;
    }

    private Function<BlockState, VoxelShape> makeShapes() {
        Map<Direction, VoxelShape> $$0 = Shapes.rotateAll(Block.boxZ(16.0, 0.0, 1.0));
        return this.a($$1 -> {
            VoxelShape $$2 = Shapes.empty();
            for (Direction $$3 : DIRECTIONS) {
                if (!MultifaceBlock.hasFace($$1, $$3)) continue;
                $$2 = Shapes.or($$2, (VoxelShape)$$0.get($$3));
            }
            return $$2.isEmpty() ? Shapes.block() : $$2;
        }, WATERLOGGED);
    }

    public static Set<Direction> availableFaces(BlockState $$0) {
        if (!($$0.getBlock() instanceof MultifaceBlock)) {
            return Set.of();
        }
        EnumSet<Direction> $$1 = EnumSet.noneOf(Direction.class);
        for (Direction $$2 : Direction.values()) {
            if (!MultifaceBlock.hasFace($$0, $$2)) continue;
            $$1.add($$2);
        }
        return $$1;
    }

    public static Set<Direction> unpack(byte $$0) {
        EnumSet<Direction> $$1 = EnumSet.noneOf(Direction.class);
        for (Direction $$2 : Direction.values()) {
            if (($$0 & (byte)(1 << $$2.ordinal())) <= 0) continue;
            $$1.add($$2);
        }
        return $$1;
    }

    public static byte pack(Collection<Direction> $$0) {
        byte $$1 = 0;
        for (Direction $$2 : $$0) {
            $$1 = (byte)($$1 | 1 << $$2.ordinal());
        }
        return $$1;
    }

    protected boolean isFaceSupported(Direction $$0) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        for (Direction $$1 : DIRECTIONS) {
            if (!this.isFaceSupported($$1)) continue;
            $$0.a(MultifaceBlock.getFaceProperty($$1));
        }
        $$0.a(WATERLOGGED);
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$2.scheduleTick($$3, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        if (!MultifaceBlock.hasAnyFace($$0)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (!MultifaceBlock.hasFace($$0, $$4) || MultifaceBlock.canAttachTo($$1, $$4, $$5, $$6)) {
            return $$0;
        }
        return MultifaceBlock.removeFace($$0, MultifaceBlock.getFaceProperty($$4));
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.shapes.apply($$0);
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        boolean $$3 = false;
        for (Direction $$4 : DIRECTIONS) {
            if (!MultifaceBlock.hasFace($$0, $$4)) continue;
            if (!MultifaceBlock.canAttachTo($$1, $$2, $$4)) {
                return false;
            }
            $$3 = true;
        }
        return $$3;
    }

    @Override
    protected boolean canBeReplaced(BlockState $$0, BlockPlaceContext $$1) {
        return !$$1.getItemInHand().is(this.asItem()) || MultifaceBlock.hasAnyVacantFace($$0);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        BlockState $$32 = $$1.getBlockState($$2);
        return Arrays.stream($$0.f()).map($$3 -> this.getStateForPlacement($$32, $$1, $$2, (Direction)$$3)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public boolean isValidStateForPlacement(BlockGetter $$0, BlockState $$1, BlockPos $$2, Direction $$3) {
        if (!this.isFaceSupported($$3) || $$1.is(this) && MultifaceBlock.hasFace($$1, $$3)) {
            return false;
        }
        BlockPos $$4 = $$2.relative($$3);
        return MultifaceBlock.canAttachTo($$0, $$3, $$4, $$0.getBlockState($$4));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        BlockState $$6;
        if (!this.isValidStateForPlacement($$1, $$0, $$2, $$3)) {
            return null;
        }
        if ($$0.is(this)) {
            BlockState $$4 = $$0;
        } else if ($$0.getFluidState().isSourceOfType(Fluids.WATER)) {
            BlockState $$5 = (BlockState)this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true);
        } else {
            $$6 = this.defaultBlockState();
        }
        return (BlockState)$$6.setValue(MultifaceBlock.getFaceProperty($$3), true);
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        if (!this.canRotate) {
            return $$0;
        }
        return this.mapDirections($$0, $$1::rotate);
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        if ($$1 == Mirror.FRONT_BACK && !this.canMirrorX) {
            return $$0;
        }
        if ($$1 == Mirror.LEFT_RIGHT && !this.canMirrorZ) {
            return $$0;
        }
        return this.mapDirections($$0, $$1::mirror);
    }

    private BlockState mapDirections(BlockState $$0, Function<Direction, Direction> $$1) {
        BlockState $$2 = $$0;
        for (Direction $$3 : DIRECTIONS) {
            if (!this.isFaceSupported($$3)) continue;
            $$2 = (BlockState)$$2.setValue(MultifaceBlock.getFaceProperty($$1.apply($$3)), $$0.getValue(MultifaceBlock.getFaceProperty($$3)));
        }
        return $$2;
    }

    public static boolean hasFace(BlockState $$0, Direction $$1) {
        BooleanProperty $$2 = MultifaceBlock.getFaceProperty($$1);
        return $$0.getValueOrElse($$2, false);
    }

    public static boolean canAttachTo(BlockGetter $$0, BlockPos $$1, Direction $$2) {
        BlockPos $$3 = $$1.relative($$2);
        BlockState $$4 = $$0.getBlockState($$3);
        return MultifaceBlock.canAttachTo($$0, $$2, $$3, $$4);
    }

    public static boolean canAttachTo(BlockGetter $$0, Direction $$1, BlockPos $$2, BlockState $$3) {
        return Block.isFaceFull($$3.getBlockSupportShape($$0, $$2), $$1.getOpposite()) || Block.isFaceFull($$3.getCollisionShape($$0, $$2), $$1.getOpposite());
    }

    private static BlockState removeFace(BlockState $$0, BooleanProperty $$1) {
        BlockState $$2 = (BlockState)$$0.setValue($$1, false);
        if (MultifaceBlock.hasAnyFace($$2)) {
            return $$2;
        }
        return Blocks.AIR.defaultBlockState();
    }

    public static BooleanProperty getFaceProperty(Direction $$0) {
        return PROPERTY_BY_DIRECTION.get($$0);
    }

    private static BlockState getDefaultMultifaceState(StateDefinition<Block, BlockState> $$0) {
        BlockState $$1 = (BlockState)$$0.any().setValue(WATERLOGGED, false);
        for (BooleanProperty $$2 : PROPERTY_BY_DIRECTION.values()) {
            $$1 = (BlockState)$$1.trySetValue($$2, false);
        }
        return $$1;
    }

    protected static boolean hasAnyFace(BlockState $$0) {
        for (Direction $$1 : DIRECTIONS) {
            if (!MultifaceBlock.hasFace($$0, $$1)) continue;
            return true;
        }
        return false;
    }

    private static boolean hasAnyVacantFace(BlockState $$0) {
        for (Direction $$1 : DIRECTIONS) {
            if (MultifaceBlock.hasFace($$0, $$1)) continue;
            return true;
        }
        return false;
    }
}

