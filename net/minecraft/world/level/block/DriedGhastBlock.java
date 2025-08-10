/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.LevelTicks;

public class DriedGhastBlock
extends HorizontalDirectionalBlock
implements SimpleWaterloggedBlock {
    public static final MapCodec<DriedGhastBlock> CODEC = DriedGhastBlock.simpleCodec(DriedGhastBlock::new);
    public static final int MAX_HYDRATION_LEVEL = 3;
    public static final IntegerProperty HYDRATION_LEVEL = BlockStateProperties.DRIED_GHAST_HYDRATION_LEVELS;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final int HYDRATION_TICK_DELAY = 5000;
    private static final VoxelShape SHAPE = Block.column(10.0, 10.0, 0.0, 10.0);

    public MapCodec<DriedGhastBlock> codec() {
        return CODEC;
    }

    public DriedGhastBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(HYDRATION_LEVEL, 0)).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING, HYDRATION_LEVEL, WATERLOGGED);
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$2.scheduleTick($$3, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    public int getHydrationLevel(BlockState $$0) {
        return $$0.getValue(HYDRATION_LEVEL);
    }

    private boolean isReadyToSpawn(BlockState $$0) {
        return this.getHydrationLevel($$0) == 3;
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            this.tickWaterlogged($$0, $$1, $$2, $$3);
            return;
        }
        int $$4 = this.getHydrationLevel($$0);
        if ($$4 > 0) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(HYDRATION_LEVEL, $$4 - 1), 2);
            $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$0));
        }
    }

    private void tickWaterlogged(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!this.isReadyToSpawn($$0)) {
            $$1.playSound(null, $$2, SoundEvents.DRIED_GHAST_TRANSITION, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$1.setBlock($$2, (BlockState)$$0.setValue(HYDRATION_LEVEL, this.getHydrationLevel($$0) + 1), 2);
            $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$0));
        } else {
            this.spawnGhastling($$1, $$2, $$0);
        }
    }

    private void spawnGhastling(ServerLevel $$0, BlockPos $$1, BlockState $$2) {
        $$0.removeBlock($$1, false);
        HappyGhast $$3 = EntityType.HAPPY_GHAST.create($$0, EntitySpawnReason.BREEDING);
        if ($$3 != null) {
            Vec3 $$4 = $$1.getBottomCenter();
            $$3.setBaby(true);
            float $$5 = Direction.getYRot((Direction)$$2.getValue(FACING));
            $$3.setYHeadRot($$5);
            $$3.snapTo($$4.x(), $$4.y(), $$4.z(), $$5, 0.0f);
            $$0.addFreshEntity($$3);
            $$0.playSound(null, $$3, SoundEvents.GHASTLING_SPAWN, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        double $$4 = (double)$$2.getX() + 0.5;
        double $$5 = (double)$$2.getY() + 0.5;
        double $$6 = (double)$$2.getZ() + 0.5;
        if (!$$0.getValue(WATERLOGGED).booleanValue()) {
            if ($$3.nextInt(40) == 0 && $$1.getBlockState($$2.below()).is(BlockTags.TRIGGERS_AMBIENT_DRIED_GHAST_BLOCK_SOUNDS)) {
                $$1.playLocalSound($$4, $$5, $$6, SoundEvents.DRIED_GHAST_AMBIENT, SoundSource.BLOCKS, 1.0f, 1.0f, false);
            }
            if ($$3.nextInt(6) == 0) {
                $$1.addParticle(ParticleTypes.WHITE_SMOKE, $$4, $$5, $$6, 0.0, 0.02, 0.0);
            }
        } else {
            if ($$3.nextInt(40) == 0) {
                $$1.playLocalSound($$4, $$5, $$6, SoundEvents.DRIED_GHAST_AMBIENT_WATER, SoundSource.BLOCKS, 1.0f, 1.0f, false);
            }
            if ($$3.nextInt(6) == 0) {
                $$1.addParticle(ParticleTypes.HAPPY_VILLAGER, $$4 + (double)(($$3.nextFloat() * 2.0f - 1.0f) / 3.0f), $$5 + 0.4, $$6 + (double)(($$3.nextFloat() * 2.0f - 1.0f) / 3.0f), 0.0, $$3.nextFloat(), 0.0);
            }
        }
    }

    @Override
    protected void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (($$0.getValue(WATERLOGGED).booleanValue() || $$0.getValue(HYDRATION_LEVEL) > 0) && !((LevelTicks)$$1.getBlockTicks()).hasScheduledTick($$2, this)) {
            $$1.scheduleTick($$2, this, 5000);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        FluidState $$1 = $$0.getLevel().getFluidState($$0.getClickedPos());
        boolean $$2 = $$1.getType() == Fluids.WATER;
        return (BlockState)((BlockState)super.getStateForPlacement($$0).setValue(WATERLOGGED, $$2)).setValue(FACING, $$0.getHorizontalDirection().getOpposite());
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public boolean placeLiquid(LevelAccessor $$0, BlockPos $$1, BlockState $$2, FluidState $$3) {
        if ($$2.getValue(BlockStateProperties.WATERLOGGED).booleanValue() || $$3.getType() != Fluids.WATER) {
            return false;
        }
        if (!$$0.isClientSide()) {
            $$0.setBlock($$1, (BlockState)$$2.setValue(BlockStateProperties.WATERLOGGED, true), 3);
            $$0.scheduleTick($$1, $$3.getType(), $$3.getType().getTickDelay($$0));
            $$0.playSound(null, $$1, SoundEvents.DRIED_GHAST_PLACE_IN_WATER, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        return true;
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, @Nullable LivingEntity $$3, ItemStack $$4) {
        super.setPlacedBy($$0, $$1, $$2, $$3, $$4);
        $$0.playSound(null, $$1, $$2.getValue(WATERLOGGED) != false ? SoundEvents.DRIED_GHAST_PLACE_IN_WATER : SoundEvents.DRIED_GHAST_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }
}

