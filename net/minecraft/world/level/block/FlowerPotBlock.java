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

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.EyeblossomBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowerPotBlock
extends Block {
    public static final MapCodec<FlowerPotBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)BuiltInRegistries.BLOCK.byNameCodec().fieldOf("potted").forGetter($$0 -> $$0.potted), FlowerPotBlock.propertiesCodec()).apply((Applicative)$$02, FlowerPotBlock::new));
    private static final Map<Block, Block> POTTED_BY_CONTENT = Maps.newHashMap();
    private static final VoxelShape SHAPE = Block.column(6.0, 0.0, 6.0);
    private final Block potted;

    public MapCodec<FlowerPotBlock> codec() {
        return CODEC;
    }

    public FlowerPotBlock(Block $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.potted = $$0;
        POTTED_BY_CONTENT.put($$0, this);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack $$0, BlockState $$1, Level $$2, BlockPos $$3, Player $$4, InteractionHand $$5, BlockHitResult $$6) {
        Block block;
        Item item = $$0.getItem();
        if (item instanceof BlockItem) {
            BlockItem $$7 = (BlockItem)item;
            block = POTTED_BY_CONTENT.getOrDefault($$7.getBlock(), Blocks.AIR);
        } else {
            block = Blocks.AIR;
        }
        BlockState $$8 = block.defaultBlockState();
        if ($$8.isAir()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!this.isEmpty()) {
            return InteractionResult.CONSUME;
        }
        $$2.setBlock($$3, $$8, 3);
        $$2.gameEvent((Entity)$$4, GameEvent.BLOCK_CHANGE, $$3);
        $$4.awardStat(Stats.POT_FLOWER);
        $$0.consume(1, $$4);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        if (this.isEmpty()) {
            return InteractionResult.CONSUME;
        }
        ItemStack $$5 = new ItemStack(this.potted);
        if (!$$3.addItem($$5)) {
            $$3.drop($$5, false);
        }
        $$1.setBlock($$2, Blocks.FLOWER_POT.defaultBlockState(), 3);
        $$1.gameEvent((Entity)$$3, GameEvent.BLOCK_CHANGE, $$2);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        if (this.isEmpty()) {
            return super.getCloneItemStack($$0, $$1, $$2, $$3);
        }
        return new ItemStack(this.potted);
    }

    private boolean isEmpty() {
        return this.potted == Blocks.AIR;
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$4 == Direction.DOWN && !$$0.canSurvive($$1, $$3)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    public Block getPotted() {
        return this.potted;
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState $$0) {
        return $$0.is(Blocks.POTTED_OPEN_EYEBLOSSOM) || $$0.is(Blocks.POTTED_CLOSED_EYEBLOSSOM);
    }

    @Override
    protected void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        boolean $$5;
        boolean $$4;
        if (this.isRandomlyTicking($$0) && $$1.dimensionType().natural() && ($$4 = this.potted == Blocks.OPEN_EYEBLOSSOM) != ($$5 = CreakingHeartBlock.isNaturalNight($$1))) {
            $$1.setBlock($$2, this.opposite($$0), 3);
            EyeblossomBlock.Type $$6 = EyeblossomBlock.Type.fromBoolean($$4).transform();
            $$6.spawnTransformParticle($$1, $$2, $$3);
            $$1.playSound(null, $$2, $$6.longSwitchSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        super.randomTick($$0, $$1, $$2, $$3);
    }

    public BlockState opposite(BlockState $$0) {
        if ($$0.is(Blocks.POTTED_OPEN_EYEBLOSSOM)) {
            return Blocks.POTTED_CLOSED_EYEBLOSSOM.defaultBlockState();
        }
        if ($$0.is(Blocks.POTTED_CLOSED_EYEBLOSSOM)) {
            return Blocks.POTTED_OPEN_EYEBLOSSOM.defaultBlockState();
        }
        return $$0;
    }
}

