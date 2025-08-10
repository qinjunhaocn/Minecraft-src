/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.material;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public abstract class WaterFluid
extends FlowingFluid {
    @Override
    public Fluid getFlowing() {
        return Fluids.FLOWING_WATER;
    }

    @Override
    public Fluid getSource() {
        return Fluids.WATER;
    }

    @Override
    public Item getBucket() {
        return Items.WATER_BUCKET;
    }

    @Override
    public void animateTick(Level $$0, BlockPos $$1, FluidState $$2, RandomSource $$3) {
        if (!$$2.isSource() && !$$2.getValue(FALLING).booleanValue()) {
            if ($$3.nextInt(64) == 0) {
                $$0.playLocalSound((double)$$1.getX() + 0.5, (double)$$1.getY() + 0.5, (double)$$1.getZ() + 0.5, SoundEvents.WATER_AMBIENT, SoundSource.AMBIENT, $$3.nextFloat() * 0.25f + 0.75f, $$3.nextFloat() + 0.5f, false);
            }
        } else if ($$3.nextInt(10) == 0) {
            $$0.addParticle(ParticleTypes.UNDERWATER, (double)$$1.getX() + $$3.nextDouble(), (double)$$1.getY() + $$3.nextDouble(), (double)$$1.getZ() + $$3.nextDouble(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    @Nullable
    public ParticleOptions getDripParticle() {
        return ParticleTypes.DRIPPING_WATER;
    }

    @Override
    protected boolean canConvertToSource(ServerLevel $$0) {
        return $$0.getGameRules().getBoolean(GameRules.RULE_WATER_SOURCE_CONVERSION);
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor $$0, BlockPos $$1, BlockState $$2) {
        BlockEntity $$3 = $$2.hasBlockEntity() ? $$0.getBlockEntity($$1) : null;
        Block.dropResources($$2, $$0, $$1, $$3);
    }

    @Override
    protected void entityInside(Level $$0, BlockPos $$1, Entity $$2, InsideBlockEffectApplier $$3) {
        $$3.apply(InsideBlockEffectType.EXTINGUISH);
    }

    @Override
    public int getSlopeFindDistance(LevelReader $$0) {
        return 4;
    }

    @Override
    public BlockState createLegacyBlock(FluidState $$0) {
        return (BlockState)Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, WaterFluid.getLegacyLevel($$0));
    }

    @Override
    public boolean isSame(Fluid $$0) {
        return $$0 == Fluids.WATER || $$0 == Fluids.FLOWING_WATER;
    }

    @Override
    public int getDropOff(LevelReader $$0) {
        return 1;
    }

    @Override
    public int getTickDelay(LevelReader $$0) {
        return 5;
    }

    @Override
    public boolean canBeReplacedWith(FluidState $$0, BlockGetter $$1, BlockPos $$2, Fluid $$3, Direction $$4) {
        return $$4 == Direction.DOWN && !$$3.is(FluidTags.WATER);
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0f;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    public static class Flowing
    extends WaterFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> $$0) {
            super.createFluidStateDefinition($$0);
            $$0.a(LEVEL);
        }

        @Override
        public int getAmount(FluidState $$0) {
            return $$0.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState $$0) {
            return false;
        }
    }

    public static class Source
    extends WaterFluid {
        @Override
        public int getAmount(FluidState $$0) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState $$0) {
            return true;
        }
    }
}

