/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.Optionull;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkCatalystBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class SculkCatalystBlockEntity
extends BlockEntity
implements GameEventListener.Provider<CatalystListener> {
    private final CatalystListener catalystListener;

    public SculkCatalystBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.SCULK_CATALYST, $$0, $$1);
        this.catalystListener = new CatalystListener($$1, new BlockPositionSource($$0));
    }

    public static void serverTick(Level $$0, BlockPos $$1, BlockState $$2, SculkCatalystBlockEntity $$3) {
        $$3.catalystListener.getSculkSpreader().updateCursors($$0, $$1, $$0.getRandom(), true);
    }

    @Override
    protected void loadAdditional(ValueInput $$0) {
        super.loadAdditional($$0);
        this.catalystListener.sculkSpreader.load($$0);
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        this.catalystListener.sculkSpreader.save($$0);
        super.saveAdditional($$0);
    }

    @Override
    public CatalystListener getListener() {
        return this.catalystListener;
    }

    @Override
    public /* synthetic */ GameEventListener getListener() {
        return this.getListener();
    }

    public static class CatalystListener
    implements GameEventListener {
        public static final int PULSE_TICKS = 8;
        final SculkSpreader sculkSpreader;
        private final BlockState blockState;
        private final PositionSource positionSource;

        public CatalystListener(BlockState $$0, PositionSource $$1) {
            this.blockState = $$0;
            this.positionSource = $$1;
            this.sculkSpreader = SculkSpreader.createLevelSpreader();
        }

        @Override
        public PositionSource getListenerSource() {
            return this.positionSource;
        }

        @Override
        public int getListenerRadius() {
            return 8;
        }

        @Override
        public GameEventListener.DeliveryMode getDeliveryMode() {
            return GameEventListener.DeliveryMode.BY_DISTANCE;
        }

        @Override
        public boolean handleGameEvent(ServerLevel $$0, Holder<GameEvent> $$12, GameEvent.Context $$2, Vec3 $$3) {
            Entity entity;
            if ($$12.is(GameEvent.ENTITY_DIE) && (entity = $$2.sourceEntity()) instanceof LivingEntity) {
                LivingEntity $$4 = (LivingEntity)entity;
                if (!$$4.wasExperienceConsumed()) {
                    DamageSource $$5 = $$4.getLastDamageSource();
                    int $$6 = $$4.getExperienceReward($$0, Optionull.map($$5, DamageSource::getEntity));
                    if ($$4.shouldDropExperience() && $$6 > 0) {
                        this.sculkSpreader.addCursors(BlockPos.containing($$3.relative(Direction.UP, 0.5)), $$6);
                        this.tryAwardItSpreadsAdvancement($$0, $$4);
                    }
                    $$4.skipDropExperience();
                    this.positionSource.getPosition($$0).ifPresent($$1 -> this.bloom($$0, BlockPos.containing($$1), this.blockState, $$0.getRandom()));
                }
                return true;
            }
            return false;
        }

        @VisibleForTesting
        public SculkSpreader getSculkSpreader() {
            return this.sculkSpreader;
        }

        private void bloom(ServerLevel $$0, BlockPos $$1, BlockState $$2, RandomSource $$3) {
            $$0.setBlock($$1, (BlockState)$$2.setValue(SculkCatalystBlock.PULSE, true), 3);
            $$0.scheduleTick($$1, $$2.getBlock(), 8);
            $$0.sendParticles(ParticleTypes.SCULK_SOUL, (double)$$1.getX() + 0.5, (double)$$1.getY() + 1.15, (double)$$1.getZ() + 0.5, 2, 0.2, 0.0, 0.2, 0.0);
            $$0.playSound(null, $$1, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 2.0f, 0.6f + $$3.nextFloat() * 0.4f);
        }

        private void tryAwardItSpreadsAdvancement(Level $$0, LivingEntity $$1) {
            LivingEntity $$2 = $$1.getLastHurtByMob();
            if ($$2 instanceof ServerPlayer) {
                ServerPlayer $$3 = (ServerPlayer)$$2;
                DamageSource $$4 = $$1.getLastDamageSource() == null ? $$0.damageSources().playerAttack($$3) : $$1.getLastDamageSource();
                CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.trigger($$3, $$1, $$4);
            }
        }
    }
}

