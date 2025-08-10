/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class SpellcasterIllager
extends AbstractIllager {
    private static final EntityDataAccessor<Byte> DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(SpellcasterIllager.class, EntityDataSerializers.BYTE);
    private static final int DEFAULT_SPELLCASTING_TICKS = 0;
    protected int spellCastingTickCount = 0;
    private IllagerSpell currentSpell = IllagerSpell.NONE;

    protected SpellcasterIllager(EntityType<? extends SpellcasterIllager> $$0, Level $$1) {
        super((EntityType<? extends AbstractIllager>)$$0, $$1);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder $$0) {
        super.defineSynchedData($$0);
        $$0.define(DATA_SPELL_CASTING_ID, (byte)0);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput $$0) {
        super.readAdditionalSaveData($$0);
        this.spellCastingTickCount = $$0.getIntOr("SpellTicks", 0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("SpellTicks", this.spellCastingTickCount);
    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return AbstractIllager.IllagerArmPose.SPELLCASTING;
        }
        if (this.isCelebrating()) {
            return AbstractIllager.IllagerArmPose.CELEBRATING;
        }
        return AbstractIllager.IllagerArmPose.CROSSED;
    }

    public boolean isCastingSpell() {
        if (this.level().isClientSide) {
            return this.entityData.get(DATA_SPELL_CASTING_ID) > 0;
        }
        return this.spellCastingTickCount > 0;
    }

    public void setIsCastingSpell(IllagerSpell $$0) {
        this.currentSpell = $$0;
        this.entityData.set(DATA_SPELL_CASTING_ID, (byte)$$0.id);
    }

    protected IllagerSpell getCurrentSpell() {
        if (!this.level().isClientSide) {
            return this.currentSpell;
        }
        return IllagerSpell.byId(this.entityData.get(DATA_SPELL_CASTING_ID).byteValue());
    }

    @Override
    protected void customServerAiStep(ServerLevel $$0) {
        super.customServerAiStep($$0);
        if (this.spellCastingTickCount > 0) {
            --this.spellCastingTickCount;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.isCastingSpell()) {
            IllagerSpell $$0 = this.getCurrentSpell();
            float $$1 = (float)$$0.spellColor[0];
            float $$2 = (float)$$0.spellColor[1];
            float $$3 = (float)$$0.spellColor[2];
            float $$4 = this.yBodyRot * ((float)Math.PI / 180) + Mth.cos((float)this.tickCount * 0.6662f) * 0.25f;
            float $$5 = Mth.cos($$4);
            float $$6 = Mth.sin($$4);
            double $$7 = 0.6 * (double)this.getScale();
            double $$8 = 1.8 * (double)this.getScale();
            this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, $$1, $$2, $$3), this.getX() + (double)$$5 * $$7, this.getY() + $$8, this.getZ() + (double)$$6 * $$7, 0.0, 0.0, 0.0);
            this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, $$1, $$2, $$3), this.getX() - (double)$$5 * $$7, this.getY() + $$8, this.getZ() - (double)$$6 * $$7, 0.0, 0.0, 0.0);
        }
    }

    protected int getSpellCastingTime() {
        return this.spellCastingTickCount;
    }

    protected abstract SoundEvent getCastingSoundEvent();

    protected static final class IllagerSpell
    extends Enum<IllagerSpell> {
        public static final /* enum */ IllagerSpell NONE = new IllagerSpell(0, 0.0, 0.0, 0.0);
        public static final /* enum */ IllagerSpell SUMMON_VEX = new IllagerSpell(1, 0.7, 0.7, 0.8);
        public static final /* enum */ IllagerSpell FANGS = new IllagerSpell(2, 0.4, 0.3, 0.35);
        public static final /* enum */ IllagerSpell WOLOLO = new IllagerSpell(3, 0.7, 0.5, 0.2);
        public static final /* enum */ IllagerSpell DISAPPEAR = new IllagerSpell(4, 0.3, 0.3, 0.8);
        public static final /* enum */ IllagerSpell BLINDNESS = new IllagerSpell(5, 0.1, 0.1, 0.2);
        private static final IntFunction<IllagerSpell> BY_ID;
        final int id;
        final double[] spellColor;
        private static final /* synthetic */ IllagerSpell[] $VALUES;

        public static IllagerSpell[] values() {
            return (IllagerSpell[])$VALUES.clone();
        }

        public static IllagerSpell valueOf(String $$0) {
            return Enum.valueOf(IllagerSpell.class, $$0);
        }

        private IllagerSpell(int $$0, double $$1, double $$2, double $$3) {
            this.id = $$0;
            this.spellColor = new double[]{$$1, $$2, $$3};
        }

        public static IllagerSpell byId(int $$0) {
            return BY_ID.apply($$0);
        }

        private static /* synthetic */ IllagerSpell[] a() {
            return new IllagerSpell[]{NONE, SUMMON_VEX, FANGS, WOLOLO, DISAPPEAR, BLINDNESS};
        }

        static {
            $VALUES = IllagerSpell.a();
            BY_ID = ByIdMap.a($$0 -> $$0.id, IllagerSpell.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        }
    }

    protected abstract class SpellcasterUseSpellGoal
    extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        protected SpellcasterUseSpellGoal() {
        }

        @Override
        public boolean canUse() {
            LivingEntity $$0 = SpellcasterIllager.this.getTarget();
            if ($$0 == null || !$$0.isAlive()) {
                return false;
            }
            if (SpellcasterIllager.this.isCastingSpell()) {
                return false;
            }
            return SpellcasterIllager.this.tickCount >= this.nextAttackTickCount;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity $$0 = SpellcasterIllager.this.getTarget();
            return $$0 != null && $$0.isAlive() && this.attackWarmupDelay > 0;
        }

        @Override
        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            SpellcasterIllager.this.spellCastingTickCount = this.getCastingTime();
            this.nextAttackTickCount = SpellcasterIllager.this.tickCount + this.getCastingInterval();
            SoundEvent $$0 = this.getSpellPrepareSound();
            if ($$0 != null) {
                SpellcasterIllager.this.playSound($$0, 1.0f, 1.0f);
            }
            SpellcasterIllager.this.setIsCastingSpell(this.getSpell());
        }

        @Override
        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                SpellcasterIllager.this.playSound(SpellcasterIllager.this.getCastingSoundEvent(), 1.0f, 1.0f);
            }
        }

        protected abstract void performSpellCasting();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        protected abstract IllagerSpell getSpell();
    }

    protected class SpellcasterCastingSpellGoal
    extends Goal {
        public SpellcasterCastingSpellGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return SpellcasterIllager.this.getSpellCastingTime() > 0;
        }

        @Override
        public void start() {
            super.start();
            SpellcasterIllager.this.navigation.stop();
        }

        @Override
        public void stop() {
            super.stop();
            SpellcasterIllager.this.setIsCastingSpell(IllagerSpell.NONE);
        }

        @Override
        public void tick() {
            if (SpellcasterIllager.this.getTarget() != null) {
                SpellcasterIllager.this.getLookControl().setLookAt(SpellcasterIllager.this.getTarget(), SpellcasterIllager.this.getMaxHeadYRot(), SpellcasterIllager.this.getMaxHeadXRot());
            }
        }
    }
}

