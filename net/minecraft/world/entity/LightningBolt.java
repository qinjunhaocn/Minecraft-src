/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LightningBolt
extends Entity {
    private static final int START_LIFE = 2;
    private static final double DAMAGE_RADIUS = 3.0;
    private static final double DETECTION_RADIUS = 15.0;
    private int life = 2;
    public long seed;
    private int flashes;
    private boolean visualOnly;
    @Nullable
    private ServerPlayer cause;
    private final Set<Entity> hitEntities = Sets.newHashSet();
    private int blocksSetOnFire;

    public LightningBolt(EntityType<? extends LightningBolt> $$0, Level $$1) {
        super($$0, $$1);
        this.seed = this.random.nextLong();
        this.flashes = this.random.nextInt(3) + 1;
    }

    public void setVisualOnly(boolean $$0) {
        this.visualOnly = $$0;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.WEATHER;
    }

    @Nullable
    public ServerPlayer getCause() {
        return this.cause;
    }

    public void setCause(@Nullable ServerPlayer $$0) {
        this.cause = $$0;
    }

    private void powerLightningRod() {
        BlockPos $$0 = this.getStrikePosition();
        BlockState $$1 = this.level().getBlockState($$0);
        if ($$1.is(Blocks.LIGHTNING_ROD)) {
            ((LightningRodBlock)$$1.getBlock()).onLightningStrike($$1, this.level(), $$0);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.life == 2) {
            if (this.level().isClientSide()) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0f, 0.8f + this.random.nextFloat() * 0.2f, false);
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0f, 0.5f + this.random.nextFloat() * 0.2f, false);
            } else {
                Difficulty $$02 = this.level().getDifficulty();
                if ($$02 == Difficulty.NORMAL || $$02 == Difficulty.HARD) {
                    this.spawnFire(4);
                }
                this.powerLightningRod();
                LightningBolt.clearCopperOnLightningStrike(this.level(), this.getStrikePosition());
                this.gameEvent(GameEvent.LIGHTNING_STRIKE);
            }
        }
        --this.life;
        if (this.life < 0) {
            if (this.flashes == 0) {
                if (this.level() instanceof ServerLevel) {
                    List<Entity> $$1 = this.level().getEntities(this, new AABB(this.getX() - 15.0, this.getY() - 15.0, this.getZ() - 15.0, this.getX() + 15.0, this.getY() + 6.0 + 15.0, this.getZ() + 15.0), $$0 -> $$0.isAlive() && !this.hitEntities.contains($$0));
                    for (ServerPlayer $$2 : ((ServerLevel)this.level()).getPlayers($$0 -> $$0.distanceTo(this) < 256.0f)) {
                        CriteriaTriggers.LIGHTNING_STRIKE.trigger($$2, this, $$1);
                    }
                }
                this.discard();
            } else if (this.life < -this.random.nextInt(10)) {
                --this.flashes;
                this.life = 1;
                this.seed = this.random.nextLong();
                this.spawnFire(0);
            }
        }
        if (this.life >= 0) {
            if (!(this.level() instanceof ServerLevel)) {
                this.level().setSkyFlashTime(2);
            } else if (!this.visualOnly) {
                List<Entity> $$3 = this.level().getEntities(this, new AABB(this.getX() - 3.0, this.getY() - 3.0, this.getZ() - 3.0, this.getX() + 3.0, this.getY() + 6.0 + 3.0, this.getZ() + 3.0), Entity::isAlive);
                for (Entity $$4 : $$3) {
                    $$4.thunderHit((ServerLevel)this.level(), this);
                }
                this.hitEntities.addAll($$3);
                if (this.cause != null) {
                    CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.cause, $$3);
                }
            }
        }
    }

    private BlockPos getStrikePosition() {
        Vec3 $$0 = this.position();
        return BlockPos.containing($$0.x, $$0.y - 1.0E-6, $$0.z);
    }

    private void spawnFire(int $$0) {
        ServerLevel $$1;
        Level level;
        if (this.visualOnly || !((level = this.level()) instanceof ServerLevel) || !($$1 = (ServerLevel)level).getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            return;
        }
        BlockPos $$3 = this.blockPosition();
        BlockState $$4 = BaseFireBlock.getState(this.level(), $$3);
        if (this.level().getBlockState($$3).isAir() && $$4.canSurvive(this.level(), $$3)) {
            this.level().setBlockAndUpdate($$3, $$4);
            ++this.blocksSetOnFire;
        }
        for (int $$5 = 0; $$5 < $$0; ++$$5) {
            BlockPos $$6 = $$3.offset(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
            $$4 = BaseFireBlock.getState(this.level(), $$6);
            if (!this.level().getBlockState($$6).isAir() || !$$4.canSurvive(this.level(), $$6)) continue;
            this.level().setBlockAndUpdate($$6, $$4);
            ++this.blocksSetOnFire;
        }
    }

    private static void clearCopperOnLightningStrike(Level $$0, BlockPos $$1) {
        BlockState $$6;
        BlockPos $$5;
        BlockState $$2 = $$0.getBlockState($$1);
        if ($$2.is(Blocks.LIGHTNING_ROD)) {
            BlockPos $$3 = $$1.relative(((Direction)$$2.getValue(LightningRodBlock.FACING)).getOpposite());
            BlockState $$4 = $$0.getBlockState($$3);
        } else {
            $$5 = $$1;
            $$6 = $$2;
        }
        if (!($$6.getBlock() instanceof WeatheringCopper)) {
            return;
        }
        $$0.setBlockAndUpdate($$5, WeatheringCopper.getFirst($$0.getBlockState($$5)));
        BlockPos.MutableBlockPos $$7 = $$1.mutable();
        int $$8 = $$0.random.nextInt(3) + 3;
        for (int $$9 = 0; $$9 < $$8; ++$$9) {
            int $$10 = $$0.random.nextInt(8) + 1;
            LightningBolt.randomWalkCleaningCopper($$0, $$5, $$7, $$10);
        }
    }

    private static void randomWalkCleaningCopper(Level $$0, BlockPos $$1, BlockPos.MutableBlockPos $$2, int $$3) {
        Optional<BlockPos> $$5;
        $$2.set($$1);
        for (int $$4 = 0; $$4 < $$3 && !($$5 = LightningBolt.randomStepCleaningCopper($$0, $$2)).isEmpty(); ++$$4) {
            $$2.set($$5.get());
        }
    }

    private static Optional<BlockPos> randomStepCleaningCopper(Level $$0, BlockPos $$1) {
        for (BlockPos $$22 : BlockPos.randomInCube($$0.random, 10, $$1, 1)) {
            BlockState $$3 = $$0.getBlockState($$22);
            if (!($$3.getBlock() instanceof WeatheringCopper)) continue;
            WeatheringCopper.getPrevious($$3).ifPresent($$2 -> $$0.setBlockAndUpdate($$22, (BlockState)$$2));
            $$0.levelEvent(3002, $$22, -1);
            return Optional.of($$22);
        }
        return Optional.empty();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        double $$1 = 64.0 * LightningBolt.getViewScale();
        return $$0 < $$1 * $$1;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder $$0) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput $$0) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput $$0) {
    }

    public int getBlocksSetOnFire() {
        return this.blocksSetOnFire;
    }

    public Stream<Entity> getHitEntities() {
        return this.hitEntities.stream().filter(Entity::isAlive);
    }

    @Override
    public final boolean hurtServer(ServerLevel $$0, DamageSource $$1, float $$2) {
        return false;
    }
}

