/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Unit;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.effects.DamageImmunity;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public interface EnchantmentEffectComponents {
    public static final Codec<DataComponentType<?>> COMPONENT_CODEC = Codec.lazyInitialized(() -> BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE.byNameCodec());
    public static final Codec<DataComponentMap> CODEC = DataComponentMap.makeCodec(COMPONENT_CODEC);
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> DAMAGE_PROTECTION = EnchantmentEffectComponents.register("damage_protection", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));
    public static final DataComponentType<List<ConditionalEffect<DamageImmunity>>> DAMAGE_IMMUNITY = EnchantmentEffectComponents.register("damage_immunity", $$0 -> $$0.persistent(ConditionalEffect.codec(DamageImmunity.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> DAMAGE = EnchantmentEffectComponents.register("damage", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> SMASH_DAMAGE_PER_FALLEN_BLOCK = EnchantmentEffectComponents.register("smash_damage_per_fallen_block", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> KNOCKBACK = EnchantmentEffectComponents.register("knockback", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> ARMOR_EFFECTIVENESS = EnchantmentEffectComponents.register("armor_effectiveness", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));
    public static final DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>> POST_ATTACK = EnchantmentEffectComponents.register("post_attack", $$0 -> $$0.persistent(TargetedConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>> HIT_BLOCK = EnchantmentEffectComponents.register("hit_block", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.HIT_BLOCK).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> ITEM_DAMAGE = EnchantmentEffectComponents.register("item_damage", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf()));
    public static final DataComponentType<List<EnchantmentAttributeEffect>> ATTRIBUTES = EnchantmentEffectComponents.register("attributes", $$0 -> $$0.persistent(EnchantmentAttributeEffect.CODEC.codec().listOf()));
    public static final DataComponentType<List<TargetedConditionalEffect<EnchantmentValueEffect>>> EQUIPMENT_DROPS = EnchantmentEffectComponents.register("equipment_drops", $$0 -> $$0.persistent(TargetedConditionalEffect.equipmentDropsCodec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentLocationBasedEffect>>> LOCATION_CHANGED = EnchantmentEffectComponents.register("location_changed", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentLocationBasedEffect.CODEC, LootContextParamSets.ENCHANTED_LOCATION).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>> TICK = EnchantmentEffectComponents.register("tick", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> AMMO_USE = EnchantmentEffectComponents.register("ammo_use", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> PROJECTILE_PIERCING = EnchantmentEffectComponents.register("projectile_piercing", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>> PROJECTILE_SPAWNED = EnchantmentEffectComponents.register("projectile_spawned", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> PROJECTILE_SPREAD = EnchantmentEffectComponents.register("projectile_spread", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> PROJECTILE_COUNT = EnchantmentEffectComponents.register("projectile_count", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> TRIDENT_RETURN_ACCELERATION = EnchantmentEffectComponents.register("trident_return_acceleration", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> FISHING_TIME_REDUCTION = EnchantmentEffectComponents.register("fishing_time_reduction", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> FISHING_LUCK_BONUS = EnchantmentEffectComponents.register("fishing_luck_bonus", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> BLOCK_EXPERIENCE = EnchantmentEffectComponents.register("block_experience", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> MOB_EXPERIENCE = EnchantmentEffectComponents.register("mob_experience", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> REPAIR_WITH_XP = EnchantmentEffectComponents.register("repair_with_xp", $$0 -> $$0.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf()));
    public static final DataComponentType<EnchantmentValueEffect> CROSSBOW_CHARGE_TIME = EnchantmentEffectComponents.register("crossbow_charge_time", $$0 -> $$0.persistent(EnchantmentValueEffect.CODEC));
    public static final DataComponentType<List<CrossbowItem.ChargingSounds>> CROSSBOW_CHARGING_SOUNDS = EnchantmentEffectComponents.register("crossbow_charging_sounds", $$0 -> $$0.persistent(CrossbowItem.ChargingSounds.CODEC.listOf()));
    public static final DataComponentType<List<Holder<SoundEvent>>> TRIDENT_SOUND = EnchantmentEffectComponents.register("trident_sound", $$0 -> $$0.persistent(SoundEvent.CODEC.listOf()));
    public static final DataComponentType<Unit> PREVENT_EQUIPMENT_DROP = EnchantmentEffectComponents.register("prevent_equipment_drop", $$0 -> $$0.persistent(Unit.CODEC));
    public static final DataComponentType<Unit> PREVENT_ARMOR_CHANGE = EnchantmentEffectComponents.register("prevent_armor_change", $$0 -> $$0.persistent(Unit.CODEC));
    public static final DataComponentType<EnchantmentValueEffect> TRIDENT_SPIN_ATTACK_STRENGTH = EnchantmentEffectComponents.register("trident_spin_attack_strength", $$0 -> $$0.persistent(EnchantmentValueEffect.CODEC));

    public static DataComponentType<?> bootstrap(Registry<DataComponentType<?>> $$0) {
        return DAMAGE_PROTECTION;
    }

    private static <T> DataComponentType<T> register(String $$0, UnaryOperator<DataComponentType.Builder<T>> $$1) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, $$0, ((DataComponentType.Builder)$$1.apply(DataComponentType.builder())).build());
    }
}

