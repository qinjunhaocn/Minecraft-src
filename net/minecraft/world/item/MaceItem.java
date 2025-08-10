/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MaceItem
extends Item {
    private static final int DEFAULT_ATTACK_DAMAGE = 5;
    private static final float DEFAULT_ATTACK_SPEED = -3.4f;
    public static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5f;
    private static final float SMASH_ATTACK_HEAVY_THRESHOLD = 5.0f;
    public static final float SMASH_ATTACK_KNOCKBACK_RADIUS = 3.5f;
    private static final float SMASH_ATTACK_KNOCKBACK_POWER = 0.7f;

    public MaceItem(Item.Properties $$0) {
        super($$0);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 5.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -3.4f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(), 1.0f, 2, false);
    }

    @Override
    public void hurtEnemy(ItemStack $$0, LivingEntity $$1, LivingEntity $$2) {
        if (MaceItem.canSmashAttack($$2)) {
            ServerLevel $$3 = (ServerLevel)$$2.level();
            $$2.setDeltaMovement($$2.getDeltaMovement().with(Direction.Axis.Y, 0.01f));
            if ($$2 instanceof ServerPlayer) {
                ServerPlayer $$4 = (ServerPlayer)$$2;
                $$4.currentImpulseImpactPos = this.calculateImpactPosition($$4);
                $$4.setIgnoreFallDamageFromCurrentImpulse(true);
                $$4.connection.send(new ClientboundSetEntityMotionPacket($$4));
            }
            if ($$1.onGround()) {
                if ($$2 instanceof ServerPlayer) {
                    ServerPlayer $$5 = (ServerPlayer)$$2;
                    $$5.setSpawnExtraParticlesOnFall(true);
                }
                SoundEvent $$6 = $$2.fallDistance > 5.0 ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
                $$3.playSound(null, $$2.getX(), $$2.getY(), $$2.getZ(), $$6, $$2.getSoundSource(), 1.0f, 1.0f);
            } else {
                $$3.playSound(null, $$2.getX(), $$2.getY(), $$2.getZ(), SoundEvents.MACE_SMASH_AIR, $$2.getSoundSource(), 1.0f, 1.0f);
            }
            MaceItem.knockback($$3, $$2, $$1);
        }
    }

    private Vec3 calculateImpactPosition(ServerPlayer $$0) {
        if ($$0.isIgnoringFallDamageFromCurrentImpulse() && $$0.currentImpulseImpactPos != null && $$0.currentImpulseImpactPos.y <= $$0.position().y) {
            return $$0.currentImpulseImpactPos;
        }
        return $$0.position();
    }

    @Override
    public void postHurtEnemy(ItemStack $$0, LivingEntity $$1, LivingEntity $$2) {
        if (MaceItem.canSmashAttack($$2)) {
            $$2.resetFallDistance();
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public float getAttackDamageBonus(Entity $$0, float $$1, DamageSource $$2) {
        double $$10;
        void $$4;
        Entity entity = $$2.getDirectEntity();
        if (!(entity instanceof LivingEntity)) {
            return 0.0f;
        }
        LivingEntity $$3 = (LivingEntity)entity;
        if (!MaceItem.canSmashAttack((LivingEntity)$$4)) {
            return 0.0f;
        }
        double $$5 = 3.0;
        double $$6 = 8.0;
        double $$7 = $$4.fallDistance;
        if ($$7 <= 3.0) {
            double $$8 = 4.0 * $$7;
        } else if ($$7 <= 8.0) {
            double $$9 = 12.0 + 2.0 * ($$7 - 3.0);
        } else {
            $$10 = 22.0 + $$7 - 8.0;
        }
        Level level = $$4.level();
        if (level instanceof ServerLevel) {
            ServerLevel $$11 = (ServerLevel)level;
            return (float)($$10 + (double)EnchantmentHelper.modifyFallBasedDamage($$11, $$4.getWeaponItem(), $$0, $$2, 0.0f) * $$7);
        }
        return (float)$$10;
    }

    private static void knockback(Level $$0, Entity $$1, Entity $$22) {
        $$0.levelEvent(2013, $$22.getOnPos(), 750);
        $$0.getEntitiesOfClass(LivingEntity.class, $$22.getBoundingBox().inflate(3.5), MaceItem.knockbackPredicate($$1, $$22)).forEach($$2 -> {
            Vec3 $$3 = $$2.position().subtract($$22.position());
            double $$4 = MaceItem.getKnockbackPower($$1, $$2, $$3);
            Vec3 $$5 = $$3.normalize().scale($$4);
            if ($$4 > 0.0) {
                $$2.push($$5.x, 0.7f, $$5.z);
                if ($$2 instanceof ServerPlayer) {
                    ServerPlayer $$6 = (ServerPlayer)$$2;
                    $$6.connection.send(new ClientboundSetEntityMotionPacket($$6));
                }
            }
        });
    }

    private static Predicate<LivingEntity> knockbackPredicate(Entity $$0, Entity $$1) {
        return arg_0 -> MaceItem.lambda$knockbackPredicate$1($$0, $$1, arg_0);
    }

    private static double getKnockbackPower(Entity $$0, LivingEntity $$1, Vec3 $$2) {
        return (3.5 - $$2.length()) * (double)0.7f * (double)($$0.fallDistance > 5.0 ? 2 : 1) * (1.0 - $$1.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
    }

    public static boolean canSmashAttack(LivingEntity $$0) {
        return $$0.fallDistance > 1.5 && !$$0.isFallFlying();
    }

    @Override
    @Nullable
    public DamageSource getDamageSource(LivingEntity $$0) {
        if (MaceItem.canSmashAttack($$0)) {
            return $$0.damageSources().mace($$0);
        }
        return super.getDamageSource($$0);
    }

    /*
     * Unable to fully structure code
     */
    private static /* synthetic */ boolean lambda$knockbackPredicate$1(Entity $$0, Entity $$1, LivingEntity $$2) {
        $$3 = $$2.isSpectator() == false;
        $$4 = $$2 != $$0 && $$2 != $$1;
        v0 = $$5 = $$0.isAlliedTo($$2) == false;
        if (!($$2 instanceof TamableAnimal)) ** GOTO lbl-1000
        $$6 = (TamableAnimal)$$2;
        if (!($$1 instanceof LivingEntity)) ** GOTO lbl-1000
        $$7 = (LivingEntity)$$1;
        if ($$6.isTame() && $$6.isOwnedBy($$7)) {
            v1 = true;
        } else lbl-1000:
        // 3 sources

        {
            v1 = false;
        }
        $$8 = v1 == false;
        $$11 = $$2 instanceof ArmorStand == false || ($$9 = (ArmorStand)$$2).isMarker() == false;
        $$12 = $$1.distanceToSqr($$2) <= Math.pow(3.5, 2.0);
        return $$3 != false && $$4 != false && $$5 != false && $$8 != false && $$11 != false && $$12 != false;
    }
}

