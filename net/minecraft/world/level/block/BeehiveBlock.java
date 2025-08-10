/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeehiveBlock
extends BaseEntityBlock {
    public static final MapCodec<BeehiveBlock> CODEC = BeehiveBlock.simpleCodec(BeehiveBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty HONEY_LEVEL = BlockStateProperties.LEVEL_HONEY;
    public static final int MAX_HONEY_LEVELS = 5;
    private static final int SHEARED_HONEYCOMB_COUNT = 3;

    public MapCodec<BeehiveBlock> codec() {
        return CODEC;
    }

    public BeehiveBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HONEY_LEVEL, 0)).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return $$0.getValue(HONEY_LEVEL);
    }

    @Override
    public void playerDestroy(Level $$0, Player $$1, BlockPos $$2, BlockState $$3, @Nullable BlockEntity $$4, ItemStack $$5) {
        super.playerDestroy($$0, $$1, $$2, $$3, $$4, $$5);
        if (!$$0.isClientSide && $$4 instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity $$6 = (BeehiveBlockEntity)$$4;
            if (!EnchantmentHelper.hasTag($$5, EnchantmentTags.PREVENTS_BEE_SPAWNS_WHEN_MINING)) {
                $$6.emptyAllLivingFromHive($$1, $$3, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
                Containers.updateNeighboursAfterDestroy($$3, $$0, $$2);
                this.angerNearbyBees($$0, $$2);
            }
            CriteriaTriggers.BEE_NEST_DESTROYED.trigger((ServerPlayer)$$1, $$3, $$5, $$6.getOccupantCount());
        }
    }

    @Override
    protected void onExplosionHit(BlockState $$0, ServerLevel $$1, BlockPos $$2, Explosion $$3, BiConsumer<ItemStack, BlockPos> $$4) {
        super.onExplosionHit($$0, $$1, $$2, $$3, $$4);
        this.angerNearbyBees($$1, $$2);
    }

    private void angerNearbyBees(Level $$0, BlockPos $$1) {
        AABB $$2 = new AABB($$1).inflate(8.0, 6.0, 8.0);
        List<Bee> $$3 = $$0.getEntitiesOfClass(Bee.class, $$2);
        if (!$$3.isEmpty()) {
            List<Player> $$4 = $$0.getEntitiesOfClass(Player.class, $$2);
            if ($$4.isEmpty()) {
                return;
            }
            for (Bee $$5 : $$3) {
                if ($$5.getTarget() != null) continue;
                Player $$6 = Util.getRandom($$4, $$0.random);
                $$5.setTarget($$6);
            }
        }
    }

    public static void dropHoneycomb(Level $$0, BlockPos $$1) {
        BeehiveBlock.popResource($$0, $$1, new ItemStack(Items.HONEYCOMB, 3));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack $$0, BlockState $$1, Level $$2, BlockPos $$3, Player $$4, InteractionHand $$5, BlockHitResult $$6) {
        int $$7 = $$1.getValue(HONEY_LEVEL);
        boolean $$8 = false;
        if ($$7 >= 5) {
            Item $$9 = $$0.getItem();
            if ($$0.is(Items.SHEARS)) {
                $$2.playSound((Entity)$$4, $$4.getX(), $$4.getY(), $$4.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0f, 1.0f);
                BeehiveBlock.dropHoneycomb($$2, $$3);
                $$0.hurtAndBreak(1, (LivingEntity)$$4, LivingEntity.getSlotForHand($$5));
                $$8 = true;
                $$2.gameEvent((Entity)$$4, GameEvent.SHEAR, $$3);
            } else if ($$0.is(Items.GLASS_BOTTLE)) {
                $$0.shrink(1);
                $$2.playSound((Entity)$$4, $$4.getX(), $$4.getY(), $$4.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                if ($$0.isEmpty()) {
                    $$4.setItemInHand($$5, new ItemStack(Items.HONEY_BOTTLE));
                } else if (!$$4.getInventory().add(new ItemStack(Items.HONEY_BOTTLE))) {
                    $$4.drop(new ItemStack(Items.HONEY_BOTTLE), false);
                }
                $$8 = true;
                $$2.gameEvent((Entity)$$4, GameEvent.FLUID_PICKUP, $$3);
            }
            if (!$$2.isClientSide() && $$8) {
                $$4.awardStat(Stats.ITEM_USED.get($$9));
            }
        }
        if ($$8) {
            if (!CampfireBlock.isSmokeyPos($$2, $$3)) {
                if (this.hiveContainsBees($$2, $$3)) {
                    this.angerNearbyBees($$2, $$3);
                }
                this.releaseBeesAndResetHoneyLevel($$2, $$1, $$3, $$4, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
            } else {
                this.resetHoneyLevel($$2, $$1, $$3);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn($$0, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    private boolean hiveContainsBees(Level $$0, BlockPos $$1) {
        BlockEntity $$2 = $$0.getBlockEntity($$1);
        if ($$2 instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity $$3 = (BeehiveBlockEntity)$$2;
            return !$$3.isEmpty();
        }
        return false;
    }

    public void releaseBeesAndResetHoneyLevel(Level $$0, BlockState $$1, BlockPos $$2, @Nullable Player $$3, BeehiveBlockEntity.BeeReleaseStatus $$4) {
        this.resetHoneyLevel($$0, $$1, $$2);
        BlockEntity $$5 = $$0.getBlockEntity($$2);
        if ($$5 instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity $$6 = (BeehiveBlockEntity)$$5;
            $$6.emptyAllLivingFromHive($$3, $$1, $$4);
        }
    }

    public void resetHoneyLevel(Level $$0, BlockState $$1, BlockPos $$2) {
        $$0.setBlock($$2, (BlockState)$$1.setValue(HONEY_LEVEL, 0), 3);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if ($$0.getValue(HONEY_LEVEL) >= 5) {
            for (int $$4 = 0; $$4 < $$3.nextInt(1) + 1; ++$$4) {
                this.trySpawnDripParticles($$1, $$2, $$0);
            }
        }
    }

    private void trySpawnDripParticles(Level $$0, BlockPos $$1, BlockState $$2) {
        if (!$$2.getFluidState().isEmpty() || $$0.random.nextFloat() < 0.3f) {
            return;
        }
        VoxelShape $$3 = $$2.getCollisionShape($$0, $$1);
        double $$4 = $$3.max(Direction.Axis.Y);
        if ($$4 >= 1.0 && !$$2.is(BlockTags.IMPERMEABLE)) {
            double $$5 = $$3.min(Direction.Axis.Y);
            if ($$5 > 0.0) {
                this.spawnParticle($$0, $$1, $$3, (double)$$1.getY() + $$5 - 0.05);
            } else {
                BlockPos $$6 = $$1.below();
                BlockState $$7 = $$0.getBlockState($$6);
                VoxelShape $$8 = $$7.getCollisionShape($$0, $$6);
                double $$9 = $$8.max(Direction.Axis.Y);
                if (($$9 < 1.0 || !$$7.isCollisionShapeFullBlock($$0, $$6)) && $$7.getFluidState().isEmpty()) {
                    this.spawnParticle($$0, $$1, $$3, (double)$$1.getY() - 0.05);
                }
            }
        }
    }

    private void spawnParticle(Level $$0, BlockPos $$1, VoxelShape $$2, double $$3) {
        this.spawnFluidParticle($$0, (double)$$1.getX() + $$2.min(Direction.Axis.X), (double)$$1.getX() + $$2.max(Direction.Axis.X), (double)$$1.getZ() + $$2.min(Direction.Axis.Z), (double)$$1.getZ() + $$2.max(Direction.Axis.Z), $$3);
    }

    private void spawnFluidParticle(Level $$0, double $$1, double $$2, double $$3, double $$4, double $$5) {
        $$0.addParticle(ParticleTypes.DRIPPING_HONEY, Mth.lerp($$0.random.nextDouble(), $$1, $$2), $$5, Mth.lerp($$0.random.nextDouble(), $$3, $$4), 0.0, 0.0, 0.0);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(FACING, $$0.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(HONEY_LEVEL, FACING);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new BeehiveBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return $$0.isClientSide ? null : BeehiveBlock.createTickerHelper($$2, BlockEntityType.BEEHIVE, BeehiveBlockEntity::serverTick);
    }

    @Override
    public BlockState playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        if ($$0 instanceof ServerLevel) {
            BlockEntity $$5;
            ServerLevel $$4 = (ServerLevel)$$0;
            if ($$3.preventsBlockDrops() && $$4.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && ($$5 = $$0.getBlockEntity($$1)) instanceof BeehiveBlockEntity) {
                boolean $$8;
                BeehiveBlockEntity $$6 = (BeehiveBlockEntity)$$5;
                int $$7 = $$2.getValue(HONEY_LEVEL);
                boolean bl = $$8 = !$$6.isEmpty();
                if ($$8 || $$7 > 0) {
                    ItemStack $$9 = new ItemStack(this);
                    $$9.applyComponents($$6.collectComponents());
                    $$9.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(HONEY_LEVEL, $$7));
                    ItemEntity $$10 = new ItemEntity($$0, $$1.getX(), $$1.getY(), $$1.getZ(), $$9);
                    $$10.setDefaultPickUpDelay();
                    $$0.addFreshEntity($$10);
                }
            }
        }
        return super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState $$0, LootParams.Builder $$1) {
        BlockEntity $$3;
        Entity $$2 = $$1.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (($$2 instanceof PrimedTnt || $$2 instanceof Creeper || $$2 instanceof WitherSkull || $$2 instanceof WitherBoss || $$2 instanceof MinecartTNT) && ($$3 = $$1.getOptionalParameter(LootContextParams.BLOCK_ENTITY)) instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity $$4 = (BeehiveBlockEntity)$$3;
            $$4.emptyAllLivingFromHive(null, $$0, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
        }
        return super.getDrops($$0, $$1);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        ItemStack $$4 = super.getCloneItemStack($$0, $$1, $$2, $$3);
        if ($$3) {
            $$4.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(HONEY_LEVEL, $$2.getValue(HONEY_LEVEL)));
        }
        return $$4;
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        BlockEntity $$8;
        if ($$1.getBlockState($$5).getBlock() instanceof FireBlock && ($$8 = $$1.getBlockEntity($$3)) instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity $$9 = (BeehiveBlockEntity)$$8;
            $$9.emptyAllLivingFromHive(null, $$0, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation($$0.getValue(FACING)));
    }
}

