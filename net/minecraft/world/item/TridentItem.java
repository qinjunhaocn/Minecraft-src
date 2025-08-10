/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TridentItem
extends Item
implements ProjectileItem {
    public static final int THROW_THRESHOLD_TIME = 10;
    public static final float BASE_DAMAGE = 8.0f;
    public static final float PROJECTILE_SHOOT_POWER = 2.5f;

    public TridentItem(Item.Properties $$0) {
        super($$0);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 8.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.9f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(), 1.0f, 2, false);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack $$0) {
        return ItemUseAnimation.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack $$0, LivingEntity $$1) {
        return 72000;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public boolean releaseUsing(ItemStack $$0, Level $$1, LivingEntity $$2, int $$3) {
        void $$5;
        if (!($$2 instanceof Player)) {
            return false;
        }
        Player $$4 = (Player)$$2;
        int $$6 = this.getUseDuration($$0, $$2) - $$3;
        if ($$6 < 10) {
            return false;
        }
        float $$7 = EnchantmentHelper.getTridentSpinAttackStrength($$0, (LivingEntity)$$5);
        if ($$7 > 0.0f && !$$5.isInWaterOrRain()) {
            return false;
        }
        if ($$0.nextDamageWillBreak()) {
            return false;
        }
        Holder<SoundEvent> $$8 = EnchantmentHelper.pickHighestLevel($$0, EnchantmentEffectComponents.TRIDENT_SOUND).orElse(SoundEvents.TRIDENT_THROW);
        $$5.awardStat(Stats.ITEM_USED.get(this));
        if ($$1 instanceof ServerLevel) {
            ServerLevel $$9 = (ServerLevel)$$1;
            $$0.hurtWithoutBreaking(1, (Player)$$5);
            if ($$7 == 0.0f) {
                ItemStack $$10 = $$0.consumeAndReturn(1, (LivingEntity)$$5);
                ThrownTrident $$11 = Projectile.spawnProjectileFromRotation(ThrownTrident::new, $$9, $$10, (LivingEntity)$$5, 0.0f, 2.5f, 1.0f);
                if ($$5.hasInfiniteMaterials()) {
                    $$11.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
                $$1.playSound(null, $$11, $$8.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
                return true;
            }
        }
        if ($$7 > 0.0f) {
            float $$12 = $$5.getYRot();
            float $$13 = $$5.getXRot();
            float $$14 = -Mth.sin($$12 * ((float)Math.PI / 180)) * Mth.cos($$13 * ((float)Math.PI / 180));
            float $$15 = -Mth.sin($$13 * ((float)Math.PI / 180));
            float $$16 = Mth.cos($$12 * ((float)Math.PI / 180)) * Mth.cos($$13 * ((float)Math.PI / 180));
            float $$17 = Mth.sqrt($$14 * $$14 + $$15 * $$15 + $$16 * $$16);
            $$5.push($$14 *= $$7 / $$17, $$15 *= $$7 / $$17, $$16 *= $$7 / $$17);
            $$5.startAutoSpinAttack(20, 8.0f, $$0);
            if ($$5.onGround()) {
                float $$18 = 1.1999999f;
                $$5.move(MoverType.SELF, new Vec3(0.0, 1.1999999284744263, 0.0));
            }
            $$1.playSound(null, (Entity)$$5, $$8.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult use(Level $$0, Player $$1, InteractionHand $$2) {
        ItemStack $$3 = $$1.getItemInHand($$2);
        if ($$3.nextDamageWillBreak()) {
            return InteractionResult.FAIL;
        }
        if (EnchantmentHelper.getTridentSpinAttackStrength($$3, $$1) > 0.0f && !$$1.isInWaterOrRain()) {
            return InteractionResult.FAIL;
        }
        $$1.startUsingItem($$2);
        return InteractionResult.CONSUME;
    }

    @Override
    public Projectile asProjectile(Level $$0, Position $$1, ItemStack $$2, Direction $$3) {
        ThrownTrident $$4 = new ThrownTrident($$0, $$1.x(), $$1.y(), $$1.z(), $$2.copyWithCount(1));
        $$4.pickup = AbstractArrow.Pickup.ALLOWED;
        return $$4;
    }
}

