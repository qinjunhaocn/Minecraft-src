/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CampfireBlock
extends BaseEntityBlock
implements SimpleWaterloggedBlock {
    public static final MapCodec<CampfireBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.BOOL.fieldOf("spawn_particles").forGetter($$0 -> $$0.spawnParticles), (App)Codec.intRange((int)0, (int)1000).fieldOf("fire_damage").forGetter($$0 -> $$0.fireDamage), CampfireBlock.propertiesCodec()).apply((Applicative)$$02, CampfireBlock::new));
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty SIGNAL_FIRE = BlockStateProperties.SIGNAL_FIRE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.column(16.0, 0.0, 7.0);
    private static final VoxelShape SHAPE_VIRTUAL_POST = Block.column(4.0, 0.0, 16.0);
    private static final int SMOKE_DISTANCE = 5;
    private final boolean spawnParticles;
    private final int fireDamage;

    public MapCodec<CampfireBlock> codec() {
        return CODEC;
    }

    public CampfireBlock(boolean $$0, int $$1, BlockBehaviour.Properties $$2) {
        super($$2);
        this.spawnParticles = $$0;
        this.fireDamage = $$1;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true)).setValue(SIGNAL_FIRE, false)).setValue(WATERLOGGED, false)).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack $$0, BlockState $$1, Level $$2, BlockPos $$3, Player $$4, InteractionHand $$5, BlockHitResult $$6) {
        BlockEntity $$7 = $$2.getBlockEntity($$3);
        if ($$7 instanceof CampfireBlockEntity) {
            CampfireBlockEntity $$8 = (CampfireBlockEntity)$$7;
            ItemStack $$9 = $$4.getItemInHand($$5);
            if ($$2.recipeAccess().propertySet(RecipePropertySet.CAMPFIRE_INPUT).test($$9)) {
                ServerLevel $$10;
                if ($$2 instanceof ServerLevel && $$8.placeFood($$10 = (ServerLevel)$$2, $$4, $$9)) {
                    $$4.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return InteractionResult.SUCCESS_SERVER;
                }
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        if ($$0.getValue(LIT).booleanValue() && $$3 instanceof LivingEntity) {
            $$3.hurt($$1.damageSources().campfire(), this.fireDamage);
        }
        super.entityInside($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockPos $$2;
        Level $$1 = $$0.getLevel();
        boolean $$3 = $$1.getFluidState($$2 = $$0.getClickedPos()).getType() == Fluids.WATER;
        return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, $$3)).setValue(SIGNAL_FIRE, this.isSmokeSource($$1.getBlockState($$2.below())))).setValue(LIT, !$$3)).setValue(FACING, $$0.getHorizontalDirection());
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$2.scheduleTick($$3, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        if ($$4 == Direction.DOWN) {
            return (BlockState)$$0.setValue(SIGNAL_FIRE, this.isSmokeSource($$6));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    private boolean isSmokeSource(BlockState $$0) {
        return $$0.is(Blocks.HAY_BLOCK);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.getValue(LIT).booleanValue()) {
            return;
        }
        if ($$3.nextInt(10) == 0) {
            $$1.playLocalSound((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5f + $$3.nextFloat(), $$3.nextFloat() * 0.7f + 0.6f, false);
        }
        if (this.spawnParticles && $$3.nextInt(5) == 0) {
            for (int $$4 = 0; $$4 < $$3.nextInt(1) + 1; ++$$4) {
                $$1.addParticle(ParticleTypes.LAVA, (double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, $$3.nextFloat() / 2.0f, 5.0E-5, $$3.nextFloat() / 2.0f);
            }
        }
    }

    public static void dowse(@Nullable Entity $$0, LevelAccessor $$1, BlockPos $$2, BlockState $$3) {
        if ($$1.isClientSide()) {
            for (int $$4 = 0; $$4 < 20; ++$$4) {
                CampfireBlock.makeParticles((Level)$$1, $$2, $$3.getValue(SIGNAL_FIRE), true);
            }
        }
        $$1.gameEvent($$0, GameEvent.BLOCK_CHANGE, $$2);
    }

    @Override
    public boolean placeLiquid(LevelAccessor $$0, BlockPos $$1, BlockState $$2, FluidState $$3) {
        if (!$$2.getValue(BlockStateProperties.WATERLOGGED).booleanValue() && $$3.getType() == Fluids.WATER) {
            boolean $$4 = $$2.getValue(LIT);
            if ($$4) {
                if (!$$0.isClientSide()) {
                    $$0.playSound(null, $$1, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                CampfireBlock.dowse(null, $$0, $$1, $$2);
            }
            $$0.setBlock($$1, (BlockState)((BlockState)$$2.setValue(WATERLOGGED, true)).setValue(LIT, false), 3);
            $$0.scheduleTick($$1, $$3.getType(), $$3.getType().getTickDelay($$0));
            return true;
        }
        return false;
    }

    @Override
    protected void onProjectileHit(Level $$0, BlockState $$1, BlockHitResult $$2, Projectile $$3) {
        BlockPos $$4 = $$2.getBlockPos();
        if ($$0 instanceof ServerLevel) {
            ServerLevel $$5 = (ServerLevel)$$0;
            if ($$3.isOnFire() && $$3.mayInteract($$5, $$4) && !$$1.getValue(LIT).booleanValue() && !$$1.getValue(WATERLOGGED).booleanValue()) {
                $$0.setBlock($$4, (BlockState)$$1.setValue(BlockStateProperties.LIT, true), 11);
            }
        }
    }

    public static void makeParticles(Level $$0, BlockPos $$1, boolean $$2, boolean $$3) {
        RandomSource $$4 = $$0.getRandom();
        SimpleParticleType $$5 = $$2 ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
        $$0.addAlwaysVisibleParticle($$5, true, (double)$$1.getX() + 0.5 + $$4.nextDouble() / 3.0 * (double)($$4.nextBoolean() ? 1 : -1), (double)$$1.getY() + $$4.nextDouble() + $$4.nextDouble(), (double)$$1.getZ() + 0.5 + $$4.nextDouble() / 3.0 * (double)($$4.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        if ($$3) {
            $$0.addParticle(ParticleTypes.SMOKE, (double)$$1.getX() + 0.5 + $$4.nextDouble() / 4.0 * (double)($$4.nextBoolean() ? 1 : -1), (double)$$1.getY() + 0.4, (double)$$1.getZ() + 0.5 + $$4.nextDouble() / 4.0 * (double)($$4.nextBoolean() ? 1 : -1), 0.0, 0.005, 0.0);
        }
    }

    public static boolean isSmokeyPos(Level $$0, BlockPos $$1) {
        for (int $$2 = 1; $$2 <= 5; ++$$2) {
            BlockPos $$3 = $$1.below($$2);
            BlockState $$4 = $$0.getBlockState($$3);
            if (CampfireBlock.isLitCampfire($$4)) {
                return true;
            }
            boolean $$5 = Shapes.joinIsNotEmpty(SHAPE_VIRTUAL_POST, $$4.getCollisionShape($$0, $$1, CollisionContext.empty()), BooleanOp.AND);
            if (!$$5) continue;
            BlockState $$6 = $$0.getBlockState($$3.below());
            return CampfireBlock.isLitCampfire($$6);
        }
        return false;
    }

    public static boolean isLitCampfire(BlockState $$0) {
        return $$0.hasProperty(LIT) && $$0.is(BlockTags.CAMPFIRES) && $$0.getValue(LIT) != false;
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation($$0.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(LIT, SIGNAL_FIRE, WATERLOGGED, FACING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new CampfireBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$22) {
        if ($$0 instanceof ServerLevel) {
            ServerLevel $$32 = (ServerLevel)$$0;
            if ($$1.getValue(LIT).booleanValue()) {
                RecipeManager.CachedCheck $$42 = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
                return CampfireBlock.createTickerHelper($$22, BlockEntityType.CAMPFIRE, ($$2, $$3, $$4, $$5) -> CampfireBlockEntity.cookTick($$32, $$3, $$4, $$5, $$42));
            }
            return CampfireBlock.createTickerHelper($$22, BlockEntityType.CAMPFIRE, CampfireBlockEntity::cooldownTick);
        }
        if ($$1.getValue(LIT).booleanValue()) {
            return CampfireBlock.createTickerHelper($$22, BlockEntityType.CAMPFIRE, CampfireBlockEntity::particleTick);
        }
        return null;
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }

    public static boolean canLight(BlockState $$02) {
        return $$02.is(BlockTags.CAMPFIRES, $$0 -> $$0.hasProperty(WATERLOGGED) && $$0.hasProperty(LIT)) && $$02.getValue(WATERLOGGED) == false && $$02.getValue(LIT) == false;
    }
}

