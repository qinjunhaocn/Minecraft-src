/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

public class RamTarget
extends Behavior<Goat> {
    public static final int TIME_OUT_DURATION = 200;
    public static final float RAM_SPEED_FORCE_FACTOR = 1.65f;
    private final Function<Goat, UniformInt> getTimeBetweenRams;
    private final TargetingConditions ramTargeting;
    private final float speed;
    private final ToDoubleFunction<Goat> getKnockbackForce;
    private Vec3 ramDirection;
    private final Function<Goat, SoundEvent> getImpactSound;
    private final Function<Goat, SoundEvent> getHornBreakSound;

    public RamTarget(Function<Goat, UniformInt> $$0, TargetingConditions $$1, float $$2, ToDoubleFunction<Goat> $$3, Function<Goat, SoundEvent> $$4, Function<Goat, SoundEvent> $$5) {
        super(ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_PRESENT), 200);
        this.getTimeBetweenRams = $$0;
        this.ramTargeting = $$1;
        this.speed = $$2;
        this.getKnockbackForce = $$3;
        this.getImpactSound = $$4;
        this.getHornBreakSound = $$5;
        this.ramDirection = Vec3.ZERO;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel $$0, Goat $$1) {
        return $$1.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, Goat $$1, long $$2) {
        return $$1.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
    }

    @Override
    protected void start(ServerLevel $$0, Goat $$1, long $$2) {
        BlockPos $$3 = $$1.blockPosition();
        Brain<Goat> $$4 = $$1.getBrain();
        Vec3 $$5 = $$4.getMemory(MemoryModuleType.RAM_TARGET).get();
        this.ramDirection = new Vec3((double)$$3.getX() - $$5.x(), 0.0, (double)$$3.getZ() - $$5.z()).normalize();
        $$4.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget($$5, this.speed, 0));
    }

    @Override
    protected void tick(ServerLevel $$0, Goat $$1, long $$2) {
        List<LivingEntity> $$3 = $$0.getNearbyEntities(LivingEntity.class, this.ramTargeting, $$1, $$1.getBoundingBox());
        Brain<Goat> $$4 = $$1.getBrain();
        if (!$$3.isEmpty()) {
            float $$7;
            DamageSource $$6;
            LivingEntity $$5 = $$3.get(0);
            if ($$5.hurtServer($$0, $$6 = $$0.damageSources().noAggroMobAttack($$1), $$7 = (float)$$1.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                EnchantmentHelper.doPostAttackEffects($$0, $$5, $$6);
            }
            int $$8 = $$1.hasEffect(MobEffects.SPEED) ? $$1.getEffect(MobEffects.SPEED).getAmplifier() + 1 : 0;
            int $$9 = $$1.hasEffect(MobEffects.SLOWNESS) ? $$1.getEffect(MobEffects.SLOWNESS).getAmplifier() + 1 : 0;
            float $$10 = 0.25f * (float)($$8 - $$9);
            float $$11 = Mth.clamp($$1.getSpeed() * 1.65f, 0.2f, 3.0f) + $$10;
            DamageSource $$12 = $$0.damageSources().mobAttack($$1);
            float $$13 = $$5.applyItemBlocking($$0, $$12, $$7);
            float $$14 = $$13 > 0.0f ? 0.5f : 1.0f;
            $$5.knockback((double)($$14 * $$11) * this.getKnockbackForce.applyAsDouble($$1), this.ramDirection.x(), this.ramDirection.z());
            this.finishRam($$0, $$1);
            $$0.playSound(null, $$1, this.getImpactSound.apply($$1), SoundSource.NEUTRAL, 1.0f, 1.0f);
        } else if (this.hasRammedHornBreakingBlock($$0, $$1)) {
            $$0.playSound(null, $$1, this.getImpactSound.apply($$1), SoundSource.NEUTRAL, 1.0f, 1.0f);
            boolean $$15 = $$1.dropHorn();
            if ($$15) {
                $$0.playSound(null, $$1, this.getHornBreakSound.apply($$1), SoundSource.NEUTRAL, 1.0f, 1.0f);
            }
            this.finishRam($$0, $$1);
        } else {
            boolean $$18;
            Optional<WalkTarget> $$16 = $$4.getMemory(MemoryModuleType.WALK_TARGET);
            Optional<Vec3> $$17 = $$4.getMemory(MemoryModuleType.RAM_TARGET);
            boolean bl = $$18 = $$16.isEmpty() || $$17.isEmpty() || $$16.get().getTarget().currentPosition().closerThan($$17.get(), 0.25);
            if ($$18) {
                this.finishRam($$0, $$1);
            }
        }
    }

    private boolean hasRammedHornBreakingBlock(ServerLevel $$0, Goat $$1) {
        Vec3 $$2 = $$1.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize();
        BlockPos $$3 = BlockPos.containing($$1.position().add($$2));
        return $$0.getBlockState($$3).is(BlockTags.SNAPS_GOAT_HORN) || $$0.getBlockState($$3.above()).is(BlockTags.SNAPS_GOAT_HORN);
    }

    protected void finishRam(ServerLevel $$0, Goat $$1) {
        $$0.broadcastEntityEvent($$1, (byte)59);
        $$1.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, this.getTimeBetweenRams.apply($$1).sample($$0.random));
        $$1.getBrain().eraseMemory(MemoryModuleType.RAM_TARGET);
    }

    @Override
    protected /* synthetic */ void start(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        this.start(serverLevel, (Goat)livingEntity, l);
    }
}

