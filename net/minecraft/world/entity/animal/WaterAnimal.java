/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;

public abstract class WaterAnimal
extends PathfinderMob {
    public static final int AMBIENT_SOUND_INTERVAL = 120;

    protected WaterAnimal(EntityType<? extends WaterAnimal> $$0, Level $$1) {
        super((EntityType<? extends PathfinderMob>)$$0, $$1);
        this.setPathfindingMalus(PathType.WATER, 0.0f);
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader $$0) {
        return $$0.isUnobstructed(this);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    protected int getBaseExperienceReward(ServerLevel $$0) {
        return 1 + this.random.nextInt(3);
    }

    protected void handleAirSupply(ServerLevel $$0, int $$1) {
        if (this.isAlive() && !this.isInWater()) {
            this.setAirSupply($$1 - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurtServer($$0, this.damageSources().drown(), 2.0f);
            }
        } else {
            this.setAirSupply(300);
        }
    }

    @Override
    public void baseTick() {
        int $$0 = this.getAirSupply();
        super.baseTick();
        Level level = this.level();
        if (level instanceof ServerLevel) {
            ServerLevel $$1 = (ServerLevel)level;
            this.handleAirSupply($$1, $$0);
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    public static boolean checkSurfaceWaterAnimalSpawnRules(EntityType<? extends WaterAnimal> $$0, LevelAccessor $$1, EntitySpawnReason $$2, BlockPos $$3, RandomSource $$4) {
        int $$5 = $$1.getSeaLevel();
        int $$6 = $$5 - 13;
        return $$3.getY() >= $$6 && $$3.getY() <= $$5 && $$1.getFluidState($$3.below()).is(FluidTags.WATER) && $$1.getBlockState($$3.above()).is(Blocks.WATER);
    }
}

