/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.AbsorptionMobEffect;
import net.minecraft.world.effect.BadOmenMobEffect;
import net.minecraft.world.effect.HealOrHarmMobEffect;
import net.minecraft.world.effect.HungerMobEffect;
import net.minecraft.world.effect.InfestedMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.OozingMobEffect;
import net.minecraft.world.effect.PoisonMobEffect;
import net.minecraft.world.effect.RaidOmenMobEffect;
import net.minecraft.world.effect.RegenerationMobEffect;
import net.minecraft.world.effect.SaturationMobEffect;
import net.minecraft.world.effect.WeavingMobEffect;
import net.minecraft.world.effect.WindChargedMobEffect;
import net.minecraft.world.effect.WitherMobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MobEffects {
    private static final int DARKNESS_EFFECT_FACTOR_PADDING_DURATION_TICKS = 22;
    public static final Holder<MobEffect> SPEED = MobEffects.register("speed", new MobEffect(MobEffectCategory.BENEFICIAL, 3402751).addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.withDefaultNamespace("effect.speed"), 0.2f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder<MobEffect> SLOWNESS = MobEffects.register("slowness", new MobEffect(MobEffectCategory.HARMFUL, 9154528).addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.withDefaultNamespace("effect.slowness"), -0.15f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder<MobEffect> HASTE = MobEffects.register("haste", new MobEffect(MobEffectCategory.BENEFICIAL, 14270531).addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.withDefaultNamespace("effect.haste"), 0.1f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder<MobEffect> MINING_FATIGUE = MobEffects.register("mining_fatigue", new MobEffect(MobEffectCategory.HARMFUL, 4866583).addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.withDefaultNamespace("effect.mining_fatigue"), -0.1f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder<MobEffect> STRENGTH = MobEffects.register("strength", new MobEffect(MobEffectCategory.BENEFICIAL, 16762624).addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.withDefaultNamespace("effect.strength"), 3.0, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> INSTANT_HEALTH = MobEffects.register("instant_health", new HealOrHarmMobEffect(MobEffectCategory.BENEFICIAL, 16262179, false));
    public static final Holder<MobEffect> INSTANT_DAMAGE = MobEffects.register("instant_damage", new HealOrHarmMobEffect(MobEffectCategory.HARMFUL, 11101546, true));
    public static final Holder<MobEffect> JUMP_BOOST = MobEffects.register("jump_boost", new MobEffect(MobEffectCategory.BENEFICIAL, 16646020).addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, ResourceLocation.withDefaultNamespace("effect.jump_boost"), 1.0, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> NAUSEA = MobEffects.register("nausea", new MobEffect(MobEffectCategory.HARMFUL, 5578058).setBlendDuration(150, 20, 60));
    public static final Holder<MobEffect> REGENERATION = MobEffects.register("regeneration", new RegenerationMobEffect(MobEffectCategory.BENEFICIAL, 13458603));
    public static final Holder<MobEffect> RESISTANCE = MobEffects.register("resistance", new MobEffect(MobEffectCategory.BENEFICIAL, 9520880));
    public static final Holder<MobEffect> FIRE_RESISTANCE = MobEffects.register("fire_resistance", new MobEffect(MobEffectCategory.BENEFICIAL, 0xFF9900));
    public static final Holder<MobEffect> WATER_BREATHING = MobEffects.register("water_breathing", new MobEffect(MobEffectCategory.BENEFICIAL, 10017472));
    public static final Holder<MobEffect> INVISIBILITY = MobEffects.register("invisibility", new MobEffect(MobEffectCategory.BENEFICIAL, 0xF6F6F6).addAttributeModifier(Attributes.WAYPOINT_TRANSMIT_RANGE, ResourceLocation.withDefaultNamespace("effect.waypoint_transmit_range_hide"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder<MobEffect> BLINDNESS = MobEffects.register("blindness", new MobEffect(MobEffectCategory.HARMFUL, 2039587));
    public static final Holder<MobEffect> NIGHT_VISION = MobEffects.register("night_vision", new MobEffect(MobEffectCategory.BENEFICIAL, 12779366));
    public static final Holder<MobEffect> HUNGER = MobEffects.register("hunger", new HungerMobEffect(MobEffectCategory.HARMFUL, 5797459));
    public static final Holder<MobEffect> WEAKNESS = MobEffects.register("weakness", new MobEffect(MobEffectCategory.HARMFUL, 0x484D48).addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.withDefaultNamespace("effect.weakness"), -4.0, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> POISON = MobEffects.register("poison", new PoisonMobEffect(MobEffectCategory.HARMFUL, 8889187));
    public static final Holder<MobEffect> WITHER = MobEffects.register("wither", new WitherMobEffect(MobEffectCategory.HARMFUL, 7561558));
    public static final Holder<MobEffect> HEALTH_BOOST = MobEffects.register("health_boost", new MobEffect(MobEffectCategory.BENEFICIAL, 16284963).addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.withDefaultNamespace("effect.health_boost"), 4.0, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> ABSORPTION = MobEffects.register("absorption", new AbsorptionMobEffect(MobEffectCategory.BENEFICIAL, 0x2552A5).addAttributeModifier(Attributes.MAX_ABSORPTION, ResourceLocation.withDefaultNamespace("effect.absorption"), 4.0, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> SATURATION = MobEffects.register("saturation", new SaturationMobEffect(MobEffectCategory.BENEFICIAL, 16262179));
    public static final Holder<MobEffect> GLOWING = MobEffects.register("glowing", new MobEffect(MobEffectCategory.NEUTRAL, 9740385));
    public static final Holder<MobEffect> LEVITATION = MobEffects.register("levitation", new MobEffect(MobEffectCategory.HARMFUL, 0xCEFFFF));
    public static final Holder<MobEffect> LUCK = MobEffects.register("luck", new MobEffect(MobEffectCategory.BENEFICIAL, 5882118).addAttributeModifier(Attributes.LUCK, ResourceLocation.withDefaultNamespace("effect.luck"), 1.0, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> UNLUCK = MobEffects.register("unluck", new MobEffect(MobEffectCategory.HARMFUL, 12624973).addAttributeModifier(Attributes.LUCK, ResourceLocation.withDefaultNamespace("effect.unluck"), -1.0, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> SLOW_FALLING = MobEffects.register("slow_falling", new MobEffect(MobEffectCategory.BENEFICIAL, 15978425));
    public static final Holder<MobEffect> CONDUIT_POWER = MobEffects.register("conduit_power", new MobEffect(MobEffectCategory.BENEFICIAL, 1950417));
    public static final Holder<MobEffect> DOLPHINS_GRACE = MobEffects.register("dolphins_grace", new MobEffect(MobEffectCategory.BENEFICIAL, 8954814));
    public static final Holder<MobEffect> BAD_OMEN = MobEffects.register("bad_omen", new BadOmenMobEffect(MobEffectCategory.NEUTRAL, 745784).withSoundOnAdded(SoundEvents.APPLY_EFFECT_BAD_OMEN));
    public static final Holder<MobEffect> HERO_OF_THE_VILLAGE = MobEffects.register("hero_of_the_village", new MobEffect(MobEffectCategory.BENEFICIAL, 0x44FF44));
    public static final Holder<MobEffect> DARKNESS = MobEffects.register("darkness", new MobEffect(MobEffectCategory.HARMFUL, 2696993).setBlendDuration(22));
    public static final Holder<MobEffect> TRIAL_OMEN = MobEffects.register("trial_omen", new MobEffect(MobEffectCategory.NEUTRAL, 0x16A6A6, ParticleTypes.TRIAL_OMEN).withSoundOnAdded(SoundEvents.APPLY_EFFECT_TRIAL_OMEN));
    public static final Holder<MobEffect> RAID_OMEN = MobEffects.register("raid_omen", new RaidOmenMobEffect(MobEffectCategory.NEUTRAL, 14565464, ParticleTypes.RAID_OMEN).withSoundOnAdded(SoundEvents.APPLY_EFFECT_RAID_OMEN));
    public static final Holder<MobEffect> WIND_CHARGED = MobEffects.register("wind_charged", new WindChargedMobEffect(MobEffectCategory.HARMFUL, 12438015));
    public static final Holder<MobEffect> WEAVING = MobEffects.register("weaving", new WeavingMobEffect(MobEffectCategory.HARMFUL, 7891290, $$0 -> Mth.randomBetweenInclusive($$0, 2, 3)));
    public static final Holder<MobEffect> OOZING = MobEffects.register("oozing", new OozingMobEffect(MobEffectCategory.HARMFUL, 10092451, $$0 -> 2));
    public static final Holder<MobEffect> INFESTED = MobEffects.register("infested", new InfestedMobEffect(MobEffectCategory.HARMFUL, 9214860, 0.1f, $$0 -> Mth.randomBetweenInclusive($$0, 1, 2)));

    private static Holder<MobEffect> register(String $$0, MobEffect $$1) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, ResourceLocation.withDefaultNamespace($$0), $$1);
    }

    public static Holder<MobEffect> bootstrap(Registry<MobEffect> $$0) {
        return SPEED;
    }
}

