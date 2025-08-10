/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseFireBlock
extends Block {
    private static final int SECONDS_ON_FIRE = 8;
    private static final int MIN_FIRE_TICKS_TO_ADD = 1;
    private static final int MAX_FIRE_TICKS_TO_ADD = 3;
    private final float fireDamage;
    protected static final VoxelShape SHAPE = Block.column(16.0, 0.0, 1.0);

    public BaseFireBlock(BlockBehaviour.Properties $$0, float $$1) {
        super($$0);
        this.fireDamage = $$1;
    }

    protected abstract MapCodec<? extends BaseFireBlock> codec();

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return BaseFireBlock.getState($$0.getLevel(), $$0.getClickedPos());
    }

    public static BlockState getState(BlockGetter $$0, BlockPos $$1) {
        BlockPos $$2 = $$1.below();
        BlockState $$3 = $$0.getBlockState($$2);
        if (SoulFireBlock.canSurviveOnBlock($$3)) {
            return Blocks.SOUL_FIRE.defaultBlockState();
        }
        return ((FireBlock)Blocks.FIRE).getStateForPlacement($$0, $$1);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        block12: {
            block11: {
                BlockPos $$4;
                BlockState $$5;
                if ($$3.nextInt(24) == 0) {
                    $$1.playLocalSound((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0f + $$3.nextFloat(), $$3.nextFloat() * 0.7f + 0.3f, false);
                }
                if (!this.canBurn($$5 = $$1.getBlockState($$4 = $$2.below())) && !$$5.isFaceSturdy($$1, $$4, Direction.UP)) break block11;
                for (int $$6 = 0; $$6 < 3; ++$$6) {
                    double $$7 = (double)$$2.getX() + $$3.nextDouble();
                    double $$8 = (double)$$2.getY() + $$3.nextDouble() * 0.5 + 0.5;
                    double $$9 = (double)$$2.getZ() + $$3.nextDouble();
                    $$1.addParticle(ParticleTypes.LARGE_SMOKE, $$7, $$8, $$9, 0.0, 0.0, 0.0);
                }
                break block12;
            }
            if (this.canBurn($$1.getBlockState($$2.west()))) {
                for (int $$10 = 0; $$10 < 2; ++$$10) {
                    double $$11 = (double)$$2.getX() + $$3.nextDouble() * (double)0.1f;
                    double $$12 = (double)$$2.getY() + $$3.nextDouble();
                    double $$13 = (double)$$2.getZ() + $$3.nextDouble();
                    $$1.addParticle(ParticleTypes.LARGE_SMOKE, $$11, $$12, $$13, 0.0, 0.0, 0.0);
                }
            }
            if (this.canBurn($$1.getBlockState($$2.east()))) {
                for (int $$14 = 0; $$14 < 2; ++$$14) {
                    double $$15 = (double)($$2.getX() + 1) - $$3.nextDouble() * (double)0.1f;
                    double $$16 = (double)$$2.getY() + $$3.nextDouble();
                    double $$17 = (double)$$2.getZ() + $$3.nextDouble();
                    $$1.addParticle(ParticleTypes.LARGE_SMOKE, $$15, $$16, $$17, 0.0, 0.0, 0.0);
                }
            }
            if (this.canBurn($$1.getBlockState($$2.north()))) {
                for (int $$18 = 0; $$18 < 2; ++$$18) {
                    double $$19 = (double)$$2.getX() + $$3.nextDouble();
                    double $$20 = (double)$$2.getY() + $$3.nextDouble();
                    double $$21 = (double)$$2.getZ() + $$3.nextDouble() * (double)0.1f;
                    $$1.addParticle(ParticleTypes.LARGE_SMOKE, $$19, $$20, $$21, 0.0, 0.0, 0.0);
                }
            }
            if (this.canBurn($$1.getBlockState($$2.south()))) {
                for (int $$22 = 0; $$22 < 2; ++$$22) {
                    double $$23 = (double)$$2.getX() + $$3.nextDouble();
                    double $$24 = (double)$$2.getY() + $$3.nextDouble();
                    double $$25 = (double)($$2.getZ() + 1) - $$3.nextDouble() * (double)0.1f;
                    $$1.addParticle(ParticleTypes.LARGE_SMOKE, $$23, $$24, $$25, 0.0, 0.0, 0.0);
                }
            }
            if (!this.canBurn($$1.getBlockState($$2.above()))) break block12;
            for (int $$26 = 0; $$26 < 2; ++$$26) {
                double $$27 = (double)$$2.getX() + $$3.nextDouble();
                double $$28 = (double)($$2.getY() + 1) - $$3.nextDouble() * (double)0.1f;
                double $$29 = (double)$$2.getZ() + $$3.nextDouble();
                $$1.addParticle(ParticleTypes.LARGE_SMOKE, $$27, $$28, $$29, 0.0, 0.0, 0.0);
            }
        }
    }

    protected abstract boolean canBurn(BlockState var1);

    @Override
    protected void entityInside(BlockState $$02, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        $$4.apply(InsideBlockEffectType.FIRE_IGNITE);
        $$4.runAfter(InsideBlockEffectType.FIRE_IGNITE, $$0 -> $$0.hurt($$0.level().damageSources().inFire(), this.fireDamage));
    }

    public static void fireIgnite(Entity $$0) {
        if (!$$0.fireImmune()) {
            if ($$0.getRemainingFireTicks() < 0) {
                $$0.setRemainingFireTicks($$0.getRemainingFireTicks() + 1);
            } else if ($$0 instanceof ServerPlayer) {
                int $$1 = $$0.level().getRandom().nextInt(1, 3);
                $$0.setRemainingFireTicks($$0.getRemainingFireTicks() + $$1);
            }
            if ($$0.getRemainingFireTicks() >= 0) {
                $$0.igniteForSeconds(8.0f);
            }
        }
    }

    @Override
    protected void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        Optional<PortalShape> $$5;
        if ($$3.is($$0.getBlock())) {
            return;
        }
        if (BaseFireBlock.inPortalDimension($$1) && ($$5 = PortalShape.findEmptyPortalShape($$1, $$2, Direction.Axis.X)).isPresent()) {
            $$5.get().createPortalBlocks($$1);
            return;
        }
        if (!$$0.canSurvive($$1, $$2)) {
            $$1.removeBlock($$2, false);
        }
    }

    private static boolean inPortalDimension(Level $$0) {
        return $$0.dimension() == Level.OVERWORLD || $$0.dimension() == Level.NETHER;
    }

    @Override
    protected void spawnDestroyParticles(Level $$0, Player $$1, BlockPos $$2, BlockState $$3) {
    }

    @Override
    public BlockState playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        if (!$$0.isClientSide()) {
            $$0.levelEvent(null, 1009, $$1, 0);
        }
        return super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    public static boolean canBePlacedAt(Level $$0, BlockPos $$1, Direction $$2) {
        BlockState $$3 = $$0.getBlockState($$1);
        if (!$$3.isAir()) {
            return false;
        }
        return BaseFireBlock.getState($$0, $$1).canSurvive($$0, $$1) || BaseFireBlock.isPortal($$0, $$1, $$2);
    }

    private static boolean isPortal(Level $$0, BlockPos $$1, Direction $$2) {
        if (!BaseFireBlock.inPortalDimension($$0)) {
            return false;
        }
        BlockPos.MutableBlockPos $$3 = $$1.mutable();
        boolean $$4 = false;
        for (Direction $$5 : Direction.values()) {
            if (!$$0.getBlockState($$3.set($$1).move($$5)).is(Blocks.OBSIDIAN)) continue;
            $$4 = true;
            break;
        }
        if (!$$4) {
            return false;
        }
        Direction.Axis $$6 = $$2.getAxis().isHorizontal() ? $$2.getCounterClockWise().getAxis() : Direction.Plane.HORIZONTAL.getRandomAxis($$0.random);
        return PortalShape.findEmptyPortalShape($$0, $$1, $$6).isPresent();
    }
}

