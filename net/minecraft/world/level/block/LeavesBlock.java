/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class LeavesBlock
extends Block
implements SimpleWaterloggedBlock {
    public static final int DECAY_DISTANCE = 7;
    public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE;
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected final float leafParticleChance;
    private static final int TICK_DELAY = 1;

    public abstract MapCodec<? extends LeavesBlock> codec();

    public LeavesBlock(float $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.leafParticleChance = $$0;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(DISTANCE, 7)).setValue(PERSISTENT, false)).setValue(WATERLOGGED, false));
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return Shapes.empty();
    }

    @Override
    protected boolean isRandomlyTicking(BlockState $$0) {
        return $$0.getValue(DISTANCE) == 7 && $$0.getValue(PERSISTENT) == false;
    }

    @Override
    protected void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (this.decaying($$0)) {
            LeavesBlock.dropResources($$0, $$1, $$2);
            $$1.removeBlock($$2, false);
        }
    }

    protected boolean decaying(BlockState $$0) {
        return $$0.getValue(PERSISTENT) == false && $$0.getValue(DISTANCE) == 7;
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        $$1.setBlock($$2, LeavesBlock.updateDistance($$0, $$1, $$2), 3);
    }

    @Override
    protected int getLightBlock(BlockState $$0) {
        return 1;
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        int $$8;
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$2.scheduleTick($$3, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        if (($$8 = LeavesBlock.getDistanceAt($$6) + 1) != 1 || $$0.getValue(DISTANCE) != $$8) {
            $$2.scheduleTick($$3, this, 1);
        }
        return $$0;
    }

    private static BlockState updateDistance(BlockState $$0, LevelAccessor $$1, BlockPos $$2) {
        int $$3 = 7;
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        for (Direction $$5 : Direction.values()) {
            $$4.setWithOffset((Vec3i)$$2, $$5);
            $$3 = Math.min($$3, LeavesBlock.getDistanceAt($$1.getBlockState($$4)) + 1);
            if ($$3 == 1) break;
        }
        return (BlockState)$$0.setValue(DISTANCE, $$3);
    }

    private static int getDistanceAt(BlockState $$0) {
        return LeavesBlock.getOptionalDistanceAt($$0).orElse(7);
    }

    public static OptionalInt getOptionalDistanceAt(BlockState $$0) {
        if ($$0.is(BlockTags.LOGS)) {
            return OptionalInt.of(0);
        }
        if ($$0.hasProperty(DISTANCE)) {
            return OptionalInt.of($$0.getValue(DISTANCE));
        }
        return OptionalInt.empty();
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        super.animateTick($$0, $$1, $$2, $$3);
        BlockPos $$4 = $$2.below();
        BlockState $$5 = $$1.getBlockState($$4);
        LeavesBlock.makeDrippingWaterParticles($$1, $$2, $$3, $$5, $$4);
        this.makeFallingLeavesParticles($$1, $$2, $$3, $$5, $$4);
    }

    private static void makeDrippingWaterParticles(Level $$0, BlockPos $$1, RandomSource $$2, BlockState $$3, BlockPos $$4) {
        if (!$$0.isRainingAt($$1.above())) {
            return;
        }
        if ($$2.nextInt(15) != 1) {
            return;
        }
        if ($$3.canOcclude() && $$3.isFaceSturdy($$0, $$4, Direction.UP)) {
            return;
        }
        ParticleUtils.spawnParticleBelow($$0, $$1, $$2, ParticleTypes.DRIPPING_WATER);
    }

    private void makeFallingLeavesParticles(Level $$0, BlockPos $$1, RandomSource $$2, BlockState $$3, BlockPos $$4) {
        if ($$2.nextFloat() >= this.leafParticleChance) {
            return;
        }
        if (LeavesBlock.isFaceFull($$3.getCollisionShape($$0, $$4), Direction.UP)) {
            return;
        }
        this.spawnFallingLeavesParticle($$0, $$1, $$2);
    }

    protected abstract void spawnFallingLeavesParticle(Level var1, BlockPos var2, RandomSource var3);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(DISTANCE, PERSISTENT, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        FluidState $$1 = $$0.getLevel().getFluidState($$0.getClickedPos());
        BlockState $$2 = (BlockState)((BlockState)this.defaultBlockState().setValue(PERSISTENT, true)).setValue(WATERLOGGED, $$1.getType() == Fluids.WATER);
        return LeavesBlock.updateDistance($$2, $$0.getLevel(), $$0.getClickedPos());
    }
}

