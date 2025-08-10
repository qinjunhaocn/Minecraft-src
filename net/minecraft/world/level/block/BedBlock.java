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
import com.mojang.math.OctahedralGroup;
import com.mojang.math.Quadrant;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.ArrayUtils;

public class BedBlock
extends HorizontalDirectionalBlock
implements EntityBlock {
    public static final MapCodec<BedBlock> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)DyeColor.CODEC.fieldOf("color").forGetter(BedBlock::getColor), BedBlock.propertiesCodec()).apply((Applicative)$$0, BedBlock::new));
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    private static final Map<Direction, VoxelShape> SHAPES = Util.make(() -> {
        VoxelShape $$0 = Block.box(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
        VoxelShape $$1 = Shapes.rotate($$0, OctahedralGroup.fromXYAngles(Quadrant.R0, Quadrant.R90));
        return Shapes.rotateHorizontal(Shapes.a(Block.column(16.0, 3.0, 9.0), $$0, $$1));
    });
    private final DyeColor color;

    public MapCodec<BedBlock> codec() {
        return CODEC;
    }

    public BedBlock(DyeColor $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.color = $$0;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PART, BedPart.FOOT)).setValue(OCCUPIED, false));
    }

    @Nullable
    public static Direction getBedOrientation(BlockGetter $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState($$1);
        return $$2.getBlock() instanceof BedBlock ? (Direction)$$2.getValue(FACING) : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$12, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        if ($$12.isClientSide) {
            return InteractionResult.SUCCESS_SERVER;
        }
        if ($$0.getValue(PART) != BedPart.HEAD && !($$0 = $$12.getBlockState($$2 = $$2.relative((Direction)$$0.getValue(FACING)))).is(this)) {
            return InteractionResult.CONSUME;
        }
        if (!BedBlock.canSetSpawn($$12)) {
            $$12.removeBlock($$2, false);
            BlockPos $$5 = $$2.relative(((Direction)$$0.getValue(FACING)).getOpposite());
            if ($$12.getBlockState($$5).is(this)) {
                $$12.removeBlock($$5, false);
            }
            Vec3 $$6 = $$2.getCenter();
            $$12.explode(null, $$12.damageSources().badRespawnPointExplosion($$6), null, $$6, 5.0f, true, Level.ExplosionInteraction.BLOCK);
            return InteractionResult.SUCCESS_SERVER;
        }
        if ($$0.getValue(OCCUPIED).booleanValue()) {
            if (!this.kickVillagerOutOfBed($$12, $$2)) {
                $$3.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
            }
            return InteractionResult.SUCCESS_SERVER;
        }
        $$3.startSleepInBed($$2).ifLeft($$1 -> {
            if ($$1.getMessage() != null) {
                $$3.displayClientMessage($$1.getMessage(), true);
            }
        });
        return InteractionResult.SUCCESS_SERVER;
    }

    public static boolean canSetSpawn(Level $$0) {
        return $$0.dimensionType().bedWorks();
    }

    private boolean kickVillagerOutOfBed(Level $$0, BlockPos $$1) {
        List<Villager> $$2 = $$0.getEntitiesOfClass(Villager.class, new AABB($$1), LivingEntity::isSleeping);
        if ($$2.isEmpty()) {
            return false;
        }
        $$2.get(0).stopSleeping();
        return true;
    }

    @Override
    public void fallOn(Level $$0, BlockState $$1, BlockPos $$2, Entity $$3, double $$4) {
        super.fallOn($$0, $$1, $$2, $$3, $$4 * 0.5);
    }

    @Override
    public void updateEntityMovementAfterFallOn(BlockGetter $$0, Entity $$1) {
        if ($$1.isSuppressingBounce()) {
            super.updateEntityMovementAfterFallOn($$0, $$1);
        } else {
            this.bounceUp($$1);
        }
    }

    private void bounceUp(Entity $$0) {
        Vec3 $$1 = $$0.getDeltaMovement();
        if ($$1.y < 0.0) {
            double $$2 = $$0 instanceof LivingEntity ? 1.0 : 0.8;
            $$0.setDeltaMovement($$1.x, -$$1.y * (double)0.66f * $$2, $$1.z);
        }
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$4 == BedBlock.getNeighbourDirection($$0.getValue(PART), (Direction)$$0.getValue(FACING))) {
            if ($$6.is(this) && $$6.getValue(PART) != $$0.getValue(PART)) {
                return (BlockState)$$0.setValue(OCCUPIED, $$6.getValue(OCCUPIED));
            }
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    private static Direction getNeighbourDirection(BedPart $$0, Direction $$1) {
        return $$0 == BedPart.FOOT ? $$1 : $$1.getOpposite();
    }

    @Override
    public BlockState playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        BlockPos $$5;
        BlockState $$6;
        BedPart $$4;
        if (!$$0.isClientSide && $$3.preventsBlockDrops() && ($$4 = $$2.getValue(PART)) == BedPart.FOOT && ($$6 = $$0.getBlockState($$5 = $$1.relative(BedBlock.getNeighbourDirection($$4, (Direction)$$2.getValue(FACING))))).is(this) && $$6.getValue(PART) == BedPart.HEAD) {
            $$0.setBlock($$5, Blocks.AIR.defaultBlockState(), 35);
            $$0.levelEvent($$3, 2001, $$5, Block.getId($$6));
        }
        return super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Direction $$1 = $$0.getHorizontalDirection();
        BlockPos $$2 = $$0.getClickedPos();
        BlockPos $$3 = $$2.relative($$1);
        Level $$4 = $$0.getLevel();
        if ($$4.getBlockState($$3).canBeReplaced($$0) && $$4.getWorldBorder().isWithinBounds($$3)) {
            return (BlockState)this.defaultBlockState().setValue(FACING, $$1);
        }
        return null;
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES.get(BedBlock.getConnectedDirection($$0).getOpposite());
    }

    public static Direction getConnectedDirection(BlockState $$0) {
        Direction $$1 = (Direction)$$0.getValue(FACING);
        return $$0.getValue(PART) == BedPart.HEAD ? $$1.getOpposite() : $$1;
    }

    public static DoubleBlockCombiner.BlockType getBlockType(BlockState $$0) {
        BedPart $$1 = $$0.getValue(PART);
        if ($$1 == BedPart.HEAD) {
            return DoubleBlockCombiner.BlockType.FIRST;
        }
        return DoubleBlockCombiner.BlockType.SECOND;
    }

    private static boolean isBunkBed(BlockGetter $$0, BlockPos $$1) {
        return $$0.getBlockState($$1.below()).getBlock() instanceof BedBlock;
    }

    public static Optional<Vec3> findStandUpPosition(EntityType<?> $$0, CollisionGetter $$1, BlockPos $$2, Direction $$3, float $$4) {
        Direction $$6;
        Direction $$5 = $$3.getClockWise();
        Direction direction = $$6 = $$5.isFacingAngle($$4) ? $$5.getOpposite() : $$5;
        if (BedBlock.isBunkBed($$1, $$2)) {
            return BedBlock.findBunkBedStandUpPosition($$0, $$1, $$2, $$3, $$6);
        }
        int[][] $$7 = BedBlock.a($$3, $$6);
        Optional<Vec3> $$8 = BedBlock.a($$0, $$1, $$2, $$7, true);
        if ($$8.isPresent()) {
            return $$8;
        }
        return BedBlock.a($$0, $$1, $$2, $$7, false);
    }

    private static Optional<Vec3> findBunkBedStandUpPosition(EntityType<?> $$0, CollisionGetter $$1, BlockPos $$2, Direction $$3, Direction $$4) {
        int[][] $$5 = BedBlock.b($$3, $$4);
        Optional<Vec3> $$6 = BedBlock.a($$0, $$1, $$2, $$5, true);
        if ($$6.isPresent()) {
            return $$6;
        }
        BlockPos $$7 = $$2.below();
        Optional<Vec3> $$8 = BedBlock.a($$0, $$1, $$7, $$5, true);
        if ($$8.isPresent()) {
            return $$8;
        }
        int[][] $$9 = BedBlock.a($$3);
        Optional<Vec3> $$10 = BedBlock.a($$0, $$1, $$2, $$9, true);
        if ($$10.isPresent()) {
            return $$10;
        }
        Optional<Vec3> $$11 = BedBlock.a($$0, $$1, $$2, $$5, false);
        if ($$11.isPresent()) {
            return $$11;
        }
        Optional<Vec3> $$12 = BedBlock.a($$0, $$1, $$7, $$5, false);
        if ($$12.isPresent()) {
            return $$12;
        }
        return BedBlock.a($$0, $$1, $$2, $$9, false);
    }

    private static Optional<Vec3> a(EntityType<?> $$0, CollisionGetter $$1, BlockPos $$2, int[][] $$3, boolean $$4) {
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
        for (int[] $$6 : $$3) {
            $$5.set($$2.getX() + $$6[0], $$2.getY(), $$2.getZ() + $$6[1]);
            Vec3 $$7 = DismountHelper.findSafeDismountLocation($$0, $$1, $$5, $$4);
            if ($$7 == null) continue;
            return Optional.of($$7);
        }
        return Optional.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING, PART, OCCUPIED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new BedBlockEntity($$0, $$1, this.color);
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, @Nullable LivingEntity $$3, ItemStack $$4) {
        super.setPlacedBy($$0, $$1, $$2, $$3, $$4);
        if (!$$0.isClientSide) {
            BlockPos $$5 = $$1.relative((Direction)$$2.getValue(FACING));
            $$0.setBlock($$5, (BlockState)$$2.setValue(PART, BedPart.HEAD), 3);
            $$0.updateNeighborsAt($$1, Blocks.AIR);
            $$2.updateNeighbourShapes($$0, $$1, 3);
        }
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    protected long getSeed(BlockState $$0, BlockPos $$1) {
        BlockPos $$2 = $$1.relative((Direction)$$0.getValue(FACING), $$0.getValue(PART) == BedPart.HEAD ? 0 : 1);
        return Mth.getSeed($$2.getX(), $$1.getY(), $$2.getZ());
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }

    private static int[][] a(Direction $$0, Direction $$1) {
        return (int[][])ArrayUtils.addAll(BedBlock.b($$0, $$1), BedBlock.a($$0));
    }

    private static int[][] b(Direction $$0, Direction $$1) {
        return new int[][]{{$$1.getStepX(), $$1.getStepZ()}, {$$1.getStepX() - $$0.getStepX(), $$1.getStepZ() - $$0.getStepZ()}, {$$1.getStepX() - $$0.getStepX() * 2, $$1.getStepZ() - $$0.getStepZ() * 2}, {-$$0.getStepX() * 2, -$$0.getStepZ() * 2}, {-$$1.getStepX() - $$0.getStepX() * 2, -$$1.getStepZ() - $$0.getStepZ() * 2}, {-$$1.getStepX() - $$0.getStepX(), -$$1.getStepZ() - $$0.getStepZ()}, {-$$1.getStepX(), -$$1.getStepZ()}, {-$$1.getStepX() + $$0.getStepX(), -$$1.getStepZ() + $$0.getStepZ()}, {$$0.getStepX(), $$0.getStepZ()}, {$$1.getStepX() + $$0.getStepX(), $$1.getStepZ() + $$0.getStepZ()}};
    }

    private static int[][] a(Direction $$0) {
        return new int[][]{{0, 0}, {-$$0.getStepX(), -$$0.getStepZ()}};
    }
}

