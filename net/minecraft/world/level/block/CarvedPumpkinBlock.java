/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class CarvedPumpkinBlock
extends HorizontalDirectionalBlock {
    public static final MapCodec<CarvedPumpkinBlock> CODEC = CarvedPumpkinBlock.simpleCodec(CarvedPumpkinBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    @Nullable
    private BlockPattern snowGolemBase;
    @Nullable
    private BlockPattern snowGolemFull;
    @Nullable
    private BlockPattern ironGolemBase;
    @Nullable
    private BlockPattern ironGolemFull;
    private static final Predicate<BlockState> PUMPKINS_PREDICATE = $$0 -> $$0 != null && ($$0.is(Blocks.CARVED_PUMPKIN) || $$0.is(Blocks.JACK_O_LANTERN));

    public MapCodec<? extends CarvedPumpkinBlock> codec() {
        return CODEC;
    }

    protected CarvedPumpkinBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$3.is($$0.getBlock())) {
            return;
        }
        this.trySpawnGolem($$1, $$2);
    }

    public boolean canSpawnGolem(LevelReader $$0, BlockPos $$1) {
        return this.getOrCreateSnowGolemBase().find($$0, $$1) != null || this.getOrCreateIronGolemBase().find($$0, $$1) != null;
    }

    private void trySpawnGolem(Level $$0, BlockPos $$1) {
        BlockPattern.BlockPatternMatch $$2 = this.getOrCreateSnowGolemFull().find($$0, $$1);
        if ($$2 != null) {
            SnowGolem $$3 = EntityType.SNOW_GOLEM.create($$0, EntitySpawnReason.TRIGGERED);
            if ($$3 != null) {
                CarvedPumpkinBlock.spawnGolemInWorld($$0, $$2, $$3, $$2.getBlock(0, 2, 0).getPos());
            }
        } else {
            IronGolem $$5;
            BlockPattern.BlockPatternMatch $$4 = this.getOrCreateIronGolemFull().find($$0, $$1);
            if ($$4 != null && ($$5 = EntityType.IRON_GOLEM.create($$0, EntitySpawnReason.TRIGGERED)) != null) {
                $$5.setPlayerCreated(true);
                CarvedPumpkinBlock.spawnGolemInWorld($$0, $$4, $$5, $$4.getBlock(1, 2, 0).getPos());
            }
        }
    }

    private static void spawnGolemInWorld(Level $$0, BlockPattern.BlockPatternMatch $$1, Entity $$2, BlockPos $$3) {
        CarvedPumpkinBlock.clearPatternBlocks($$0, $$1);
        $$2.snapTo((double)$$3.getX() + 0.5, (double)$$3.getY() + 0.05, (double)$$3.getZ() + 0.5, 0.0f, 0.0f);
        $$0.addFreshEntity($$2);
        for (ServerPlayer $$4 : $$0.getEntitiesOfClass(ServerPlayer.class, $$2.getBoundingBox().inflate(5.0))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger($$4, $$2);
        }
        CarvedPumpkinBlock.updatePatternBlocks($$0, $$1);
    }

    public static void clearPatternBlocks(Level $$0, BlockPattern.BlockPatternMatch $$1) {
        for (int $$2 = 0; $$2 < $$1.getWidth(); ++$$2) {
            for (int $$3 = 0; $$3 < $$1.getHeight(); ++$$3) {
                BlockInWorld $$4 = $$1.getBlock($$2, $$3, 0);
                $$0.setBlock($$4.getPos(), Blocks.AIR.defaultBlockState(), 2);
                $$0.levelEvent(2001, $$4.getPos(), Block.getId($$4.getState()));
            }
        }
    }

    public static void updatePatternBlocks(Level $$0, BlockPattern.BlockPatternMatch $$1) {
        for (int $$2 = 0; $$2 < $$1.getWidth(); ++$$2) {
            for (int $$3 = 0; $$3 < $$1.getHeight(); ++$$3) {
                BlockInWorld $$4 = $$1.getBlock($$2, $$3, 0);
                $$0.updateNeighborsAt($$4.getPos(), Blocks.AIR);
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(FACING, $$0.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING);
    }

    private BlockPattern getOrCreateSnowGolemBase() {
        if (this.snowGolemBase == null) {
            this.snowGolemBase = BlockPatternBuilder.start().a(" ", "#", "#").a('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }
        return this.snowGolemBase;
    }

    private BlockPattern getOrCreateSnowGolemFull() {
        if (this.snowGolemFull == null) {
            this.snowGolemFull = BlockPatternBuilder.start().a("^", "#", "#").a('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).a('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }
        return this.snowGolemFull;
    }

    private BlockPattern getOrCreateIronGolemBase() {
        if (this.ironGolemBase == null) {
            this.ironGolemBase = BlockPatternBuilder.start().a("~ ~", "###", "~#~").a('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).a('~', (BlockInWorld $$0) -> $$0.getState().isAir()).build();
        }
        return this.ironGolemBase;
    }

    private BlockPattern getOrCreateIronGolemFull() {
        if (this.ironGolemFull == null) {
            this.ironGolemFull = BlockPatternBuilder.start().a("~^~", "###", "~#~").a('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).a('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).a('~', (BlockInWorld $$0) -> $$0.getState().isAir()).build();
        }
        return this.ironGolemFull;
    }
}

