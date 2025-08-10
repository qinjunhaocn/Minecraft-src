/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

public class WitherSkullBlock
extends SkullBlock {
    public static final MapCodec<WitherSkullBlock> CODEC = WitherSkullBlock.simpleCodec(WitherSkullBlock::new);
    @Nullable
    private static BlockPattern witherPatternFull;
    @Nullable
    private static BlockPattern witherPatternBase;

    public MapCodec<WitherSkullBlock> codec() {
        return CODEC;
    }

    protected WitherSkullBlock(BlockBehaviour.Properties $$0) {
        super(SkullBlock.Types.WITHER_SKELETON, $$0);
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, @Nullable LivingEntity $$3, ItemStack $$4) {
        WitherSkullBlock.checkSpawn($$0, $$1);
    }

    public static void checkSpawn(Level $$0, BlockPos $$1) {
        BlockEntity blockEntity = $$0.getBlockEntity($$1);
        if (blockEntity instanceof SkullBlockEntity) {
            SkullBlockEntity $$2 = (SkullBlockEntity)blockEntity;
            WitherSkullBlock.checkSpawn($$0, $$1, $$2);
        }
    }

    public static void checkSpawn(Level $$0, BlockPos $$1, SkullBlockEntity $$2) {
        boolean $$4;
        if ($$0.isClientSide) {
            return;
        }
        BlockState $$3 = $$2.getBlockState();
        boolean bl = $$4 = $$3.is(Blocks.WITHER_SKELETON_SKULL) || $$3.is(Blocks.WITHER_SKELETON_WALL_SKULL);
        if (!$$4 || $$1.getY() < $$0.getMinY() || $$0.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        BlockPattern.BlockPatternMatch $$5 = WitherSkullBlock.getOrCreateWitherFull().find($$0, $$1);
        if ($$5 == null) {
            return;
        }
        WitherBoss $$6 = EntityType.WITHER.create($$0, EntitySpawnReason.TRIGGERED);
        if ($$6 != null) {
            CarvedPumpkinBlock.clearPatternBlocks($$0, $$5);
            BlockPos $$7 = $$5.getBlock(1, 2, 0).getPos();
            $$6.snapTo((double)$$7.getX() + 0.5, (double)$$7.getY() + 0.55, (double)$$7.getZ() + 0.5, $$5.getForwards().getAxis() == Direction.Axis.X ? 0.0f : 90.0f, 0.0f);
            $$6.yBodyRot = $$5.getForwards().getAxis() == Direction.Axis.X ? 0.0f : 90.0f;
            $$6.makeInvulnerable();
            for (ServerPlayer $$8 : $$0.getEntitiesOfClass(ServerPlayer.class, $$6.getBoundingBox().inflate(50.0))) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger($$8, $$6);
            }
            $$0.addFreshEntity($$6);
            CarvedPumpkinBlock.updatePatternBlocks($$0, $$5);
        }
    }

    public static boolean canSpawnMob(Level $$0, BlockPos $$1, ItemStack $$2) {
        if ($$2.is(Items.WITHER_SKELETON_SKULL) && $$1.getY() >= $$0.getMinY() + 2 && $$0.getDifficulty() != Difficulty.PEACEFUL && !$$0.isClientSide) {
            return WitherSkullBlock.getOrCreateWitherBase().find($$0, $$1) != null;
        }
        return false;
    }

    private static BlockPattern getOrCreateWitherFull() {
        if (witherPatternFull == null) {
            witherPatternFull = BlockPatternBuilder.start().a("^^^", "###", "~#~").a('#', (BlockInWorld $$0) -> $$0.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS)).a('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).a('~', (BlockInWorld $$0) -> $$0.getState().isAir()).build();
        }
        return witherPatternFull;
    }

    private static BlockPattern getOrCreateWitherBase() {
        if (witherPatternBase == null) {
            witherPatternBase = BlockPatternBuilder.start().a("   ", "###", "~#~").a('#', (BlockInWorld $$0) -> $$0.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS)).a('~', (BlockInWorld $$0) -> $$0.getState().isAir()).build();
        }
        return witherPatternBase;
    }
}

