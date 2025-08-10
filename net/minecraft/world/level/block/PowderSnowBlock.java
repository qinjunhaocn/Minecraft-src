/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PowderSnowBlock
extends Block
implements BucketPickup {
    public static final MapCodec<PowderSnowBlock> CODEC = PowderSnowBlock.simpleCodec(PowderSnowBlock::new);
    private static final float HORIZONTAL_PARTICLE_MOMENTUM_FACTOR = 0.083333336f;
    private static final float IN_BLOCK_HORIZONTAL_SPEED_MULTIPLIER = 0.9f;
    private static final float IN_BLOCK_VERTICAL_SPEED_MULTIPLIER = 1.5f;
    private static final float NUM_BLOCKS_TO_FALL_INTO_BLOCK = 2.5f;
    private static final VoxelShape FALLING_COLLISION_SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.9f, 1.0);
    private static final double MINIMUM_FALL_DISTANCE_FOR_SOUND = 4.0;
    private static final double MINIMUM_FALL_DISTANCE_FOR_BIG_SOUND = 7.0;

    public MapCodec<PowderSnowBlock> codec() {
        return CODEC;
    }

    public PowderSnowBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected boolean skipRendering(BlockState $$0, BlockState $$1, Direction $$2) {
        if ($$1.is(this)) {
            return true;
        }
        return super.skipRendering($$0, $$1, $$2);
    }

    @Override
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$22, Entity $$3, InsideBlockEffectApplier $$4) {
        if (!($$3 instanceof LivingEntity) || $$3.getInBlockState().is(this)) {
            $$3.makeStuckInBlock($$0, new Vec3(0.9f, 1.5, 0.9f));
            if ($$1.isClientSide) {
                boolean $$6;
                RandomSource $$5 = $$1.getRandom();
                boolean bl = $$6 = $$3.xOld != $$3.getX() || $$3.zOld != $$3.getZ();
                if ($$6 && $$5.nextBoolean()) {
                    $$1.addParticle(ParticleTypes.SNOWFLAKE, $$3.getX(), $$22.getY() + 1, $$3.getZ(), Mth.randomBetween($$5, -1.0f, 1.0f) * 0.083333336f, 0.05f, Mth.randomBetween($$5, -1.0f, 1.0f) * 0.083333336f);
                }
            }
        }
        BlockPos $$7 = $$22.immutable();
        $$4.runBefore(InsideBlockEffectType.EXTINGUISH, $$2 -> {
            if ($$1 instanceof ServerLevel) {
                ServerLevel $$3 = (ServerLevel)$$1;
                if ($$2.isOnFire() && ($$3.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || $$2 instanceof Player) && $$2.mayInteract($$3, $$7)) {
                    $$1.destroyBlock($$7, false);
                }
            }
        });
        $$4.apply(InsideBlockEffectType.FREEZE);
        $$4.apply(InsideBlockEffectType.EXTINGUISH);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void fallOn(Level $$0, BlockState $$1, BlockPos $$2, Entity $$3, double $$4) {
        void $$6;
        if ($$4 < 4.0 || !($$3 instanceof LivingEntity)) {
            return;
        }
        LivingEntity $$5 = (LivingEntity)$$3;
        LivingEntity.Fallsounds $$7 = $$6.getFallSounds();
        SoundEvent $$8 = $$4 < 7.0 ? $$7.small() : $$7.big();
        $$3.playSound($$8, 1.0f, 1.0f);
    }

    @Override
    protected VoxelShape getEntityInsideCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, Entity $$3) {
        VoxelShape $$4 = this.getCollisionShape($$0, $$1, $$2, CollisionContext.of($$3));
        return $$4.isEmpty() ? Shapes.block() : $$4;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        EntityCollisionContext $$4;
        Entity $$5;
        if (!$$3.isPlacement() && $$3 instanceof EntityCollisionContext && ($$5 = ($$4 = (EntityCollisionContext)$$3).getEntity()) != null) {
            if ($$5.fallDistance > 2.5) {
                return FALLING_COLLISION_SHAPE;
            }
            boolean $$6 = $$5 instanceof FallingBlockEntity;
            if ($$6 || PowderSnowBlock.canEntityWalkOnPowderSnow($$5) && $$3.isAbove(Shapes.block(), $$2, false) && !$$3.isDescending()) {
                return super.getCollisionShape($$0, $$1, $$2, $$3);
            }
        }
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getVisualShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return Shapes.empty();
    }

    public static boolean canEntityWalkOnPowderSnow(Entity $$0) {
        if ($$0.getType().is(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)) {
            return true;
        }
        if ($$0 instanceof LivingEntity) {
            return ((LivingEntity)$$0).getItemBySlot(EquipmentSlot.FEET).is(Items.LEATHER_BOOTS);
        }
        return false;
    }

    @Override
    public ItemStack pickupBlock(@Nullable LivingEntity $$0, LevelAccessor $$1, BlockPos $$2, BlockState $$3) {
        $$1.setBlock($$2, Blocks.AIR.defaultBlockState(), 11);
        if (!$$1.isClientSide()) {
            $$1.levelEvent(2001, $$2, Block.getId($$3));
        }
        return new ItemStack(Items.POWDER_SNOW_BUCKET);
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL_POWDER_SNOW);
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return true;
    }
}

