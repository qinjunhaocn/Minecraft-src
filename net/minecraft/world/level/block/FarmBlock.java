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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FarmBlock
extends Block {
    public static final MapCodec<FarmBlock> CODEC = FarmBlock.simpleCodec(FarmBlock::new);
    public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;
    private static final VoxelShape SHAPE = Block.column(16.0, 0.0, 15.0);
    public static final int MAX_MOISTURE = 7;

    public MapCodec<FarmBlock> codec() {
        return CODEC;
    }

    protected FarmBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(MOISTURE, 0));
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$4 == Direction.UP && !$$0.canSurvive($$1, $$3)) {
            $$2.scheduleTick($$3, this, 1);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockState $$3 = $$1.getBlockState($$2.above());
        return !$$3.isSolid() || $$3.getBlock() instanceof FenceGateBlock || $$3.getBlock() instanceof MovingPistonBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        if (!this.defaultBlockState().canSurvive($$0.getLevel(), $$0.getClickedPos())) {
            return Blocks.DIRT.defaultBlockState();
        }
        return super.getStateForPlacement($$0);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState $$0) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.canSurvive($$1, $$2)) {
            FarmBlock.turnToDirt(null, $$0, $$1, $$2);
        }
    }

    @Override
    protected void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        int $$4 = $$0.getValue(MOISTURE);
        if (FarmBlock.isNearWater($$1, $$2) || $$1.isRainingAt($$2.above())) {
            if ($$4 < 7) {
                $$1.setBlock($$2, (BlockState)$$0.setValue(MOISTURE, 7), 2);
            }
        } else if ($$4 > 0) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(MOISTURE, $$4 - 1), 2);
        } else if (!FarmBlock.shouldMaintainFarmland($$1, $$2)) {
            FarmBlock.turnToDirt(null, $$0, $$1, $$2);
        }
    }

    @Override
    public void fallOn(Level $$0, BlockState $$1, BlockPos $$2, Entity $$3, double $$4) {
        if ($$0 instanceof ServerLevel) {
            ServerLevel $$5 = (ServerLevel)$$0;
            if ((double)$$0.random.nextFloat() < $$4 - 0.5 && $$3 instanceof LivingEntity && ($$3 instanceof Player || $$5.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) && $$3.getBbWidth() * $$3.getBbWidth() * $$3.getBbHeight() > 0.512f) {
                FarmBlock.turnToDirt($$3, $$1, $$0, $$2);
            }
        }
        super.fallOn($$0, $$1, $$2, $$3, $$4);
    }

    public static void turnToDirt(@Nullable Entity $$0, BlockState $$1, Level $$2, BlockPos $$3) {
        BlockState $$4 = FarmBlock.pushEntitiesUp($$1, Blocks.DIRT.defaultBlockState(), $$2, $$3);
        $$2.setBlockAndUpdate($$3, $$4);
        $$2.gameEvent(GameEvent.BLOCK_CHANGE, $$3, GameEvent.Context.of($$0, $$4));
    }

    private static boolean shouldMaintainFarmland(BlockGetter $$0, BlockPos $$1) {
        return $$0.getBlockState($$1.above()).is(BlockTags.MAINTAINS_FARMLAND);
    }

    private static boolean isNearWater(LevelReader $$0, BlockPos $$1) {
        for (BlockPos $$2 : BlockPos.betweenClosed($$1.offset(-4, 0, -4), $$1.offset(4, 1, 4))) {
            if (!$$0.getFluidState($$2).is(FluidTags.WATER)) continue;
            return true;
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(MOISTURE);
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }
}

