/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DecoratedPotBlock
extends BaseEntityBlock
implements SimpleWaterloggedBlock {
    public static final MapCodec<DecoratedPotBlock> CODEC = DecoratedPotBlock.simpleCodec(DecoratedPotBlock::new);
    public static final ResourceLocation SHERDS_DYNAMIC_DROP_ID = ResourceLocation.withDefaultNamespace("sherds");
    public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty CRACKED = BlockStateProperties.CRACKED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.column(14.0, 0.0, 16.0);

    public MapCodec<DecoratedPotBlock> codec() {
        return CODEC;
    }

    protected DecoratedPotBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HORIZONTAL_FACING, Direction.NORTH)).setValue(WATERLOGGED, false)).setValue(CRACKED, false));
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$2.scheduleTick($$3, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        FluidState $$1 = $$0.getLevel().getFluidState($$0.getClickedPos());
        return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(HORIZONTAL_FACING, $$0.getHorizontalDirection())).setValue(WATERLOGGED, $$1.getType() == Fluids.WATER)).setValue(CRACKED, false);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected InteractionResult useItemOn(ItemStack $$0, BlockState $$1, Level $$2, BlockPos $$3, Player $$4, InteractionHand $$5, BlockHitResult $$6) {
        void $$8;
        BlockEntity blockEntity = $$2.getBlockEntity($$3);
        if (!(blockEntity instanceof DecoratedPotBlockEntity)) {
            return InteractionResult.PASS;
        }
        DecoratedPotBlockEntity $$7 = (DecoratedPotBlockEntity)blockEntity;
        if ($$2.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ItemStack $$9 = $$8.getTheItem();
        if (!$$0.isEmpty() && ($$9.isEmpty() || ItemStack.isSameItemSameComponents($$9, $$0) && $$9.getCount() < $$9.getMaxStackSize())) {
            float $$12;
            $$8.wobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
            $$4.awardStat(Stats.ITEM_USED.get($$0.getItem()));
            ItemStack $$10 = $$0.consumeAndReturn(1, $$4);
            if ($$8.isEmpty()) {
                $$8.setTheItem($$10);
                float $$11 = (float)$$10.getCount() / (float)$$10.getMaxStackSize();
            } else {
                $$9.grow(1);
                $$12 = (float)$$9.getCount() / (float)$$9.getMaxStackSize();
            }
            $$2.playSound(null, $$3, SoundEvents.DECORATED_POT_INSERT, SoundSource.BLOCKS, 1.0f, 0.7f + 0.5f * $$12);
            if ($$2 instanceof ServerLevel) {
                ServerLevel $$13 = (ServerLevel)$$2;
                $$13.sendParticles(ParticleTypes.DUST_PLUME, (double)$$3.getX() + 0.5, (double)$$3.getY() + 1.2, (double)$$3.getZ() + 0.5, 7, 0.0, 0.0, 0.0, 0.0);
            }
            $$8.setChanged();
            $$2.gameEvent((Entity)$$4, GameEvent.BLOCK_CHANGE, $$3);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        void $$6;
        BlockEntity blockEntity = $$1.getBlockEntity($$2);
        if (!(blockEntity instanceof DecoratedPotBlockEntity)) {
            return InteractionResult.PASS;
        }
        DecoratedPotBlockEntity $$5 = (DecoratedPotBlockEntity)blockEntity;
        $$1.playSound(null, $$2, SoundEvents.DECORATED_POT_INSERT_FAIL, SoundSource.BLOCKS, 1.0f, 1.0f);
        $$6.wobble(DecoratedPotBlockEntity.WobbleStyle.NEGATIVE);
        $$1.gameEvent((Entity)$$3, GameEvent.BLOCK_CHANGE, $$2);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(HORIZONTAL_FACING, WATERLOGGED, CRACKED);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new DecoratedPotBlockEntity($$0, $$1);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState $$0, ServerLevel $$1, BlockPos $$2, boolean $$3) {
        Containers.updateNeighboursAfterDestroy($$0, $$1, $$2);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState $$0, LootParams.Builder $$12) {
        BlockEntity $$2 = $$12.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if ($$2 instanceof DecoratedPotBlockEntity) {
            DecoratedPotBlockEntity $$3 = (DecoratedPotBlockEntity)$$2;
            $$12.withDynamicDrop(SHERDS_DYNAMIC_DROP_ID, $$1 -> {
                for (Item $$2 : $$3.getDecorations().ordered()) {
                    $$1.accept($$2.getDefaultInstance());
                }
            });
        }
        return super.getDrops($$0, $$12);
    }

    @Override
    public BlockState playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        ItemStack $$4 = $$3.getMainHandItem();
        BlockState $$5 = $$2;
        if ($$4.is(ItemTags.BREAKS_DECORATED_POTS) && !EnchantmentHelper.hasTag($$4, EnchantmentTags.PREVENTS_DECORATED_POT_SHATTERING)) {
            $$5 = (BlockState)$$2.setValue(CRACKED, true);
            $$0.setBlock($$1, $$5, 260);
        }
        return super.playerWillDestroy($$0, $$1, $$5, $$3);
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    protected SoundType getSoundType(BlockState $$0) {
        if ($$0.getValue(CRACKED).booleanValue()) {
            return SoundType.DECORATED_POT_CRACKED;
        }
        return SoundType.DECORATED_POT;
    }

    @Override
    protected void onProjectileHit(Level $$0, BlockState $$1, BlockHitResult $$2, Projectile $$3) {
        ServerLevel $$5;
        BlockPos $$4 = $$2.getBlockPos();
        if ($$0 instanceof ServerLevel && $$3.mayInteract($$5 = (ServerLevel)$$0, $$4) && $$3.mayBreak($$5)) {
            $$0.setBlock($$4, (BlockState)$$1.setValue(CRACKED, true), 260);
            $$0.destroyBlock($$4, true, $$3);
        }
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        BlockEntity blockEntity = $$0.getBlockEntity($$1);
        if (blockEntity instanceof DecoratedPotBlockEntity) {
            DecoratedPotBlockEntity $$4 = (DecoratedPotBlockEntity)blockEntity;
            PotDecorations $$5 = $$4.getDecorations();
            return DecoratedPotBlockEntity.createDecoratedPotItem($$5);
        }
        return super.getCloneItemStack($$0, $$1, $$2, $$3);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity($$1.getBlockEntity($$2));
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(HORIZONTAL_FACING, $$1.rotate($$0.getValue(HORIZONTAL_FACING)));
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation($$0.getValue(HORIZONTAL_FACING)));
    }
}

