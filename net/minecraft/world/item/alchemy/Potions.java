/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.alchemy;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

public class Potions {
    public static final Holder<Potion> WATER = Potions.register("water", new Potion("water", new MobEffectInstance[0]));
    public static final Holder<Potion> MUNDANE = Potions.register("mundane", new Potion("mundane", new MobEffectInstance[0]));
    public static final Holder<Potion> THICK = Potions.register("thick", new Potion("thick", new MobEffectInstance[0]));
    public static final Holder<Potion> AWKWARD = Potions.register("awkward", new Potion("awkward", new MobEffectInstance[0]));
    public static final Holder<Potion> NIGHT_VISION = Potions.register("night_vision", new Potion("night_vision", new MobEffectInstance(MobEffects.NIGHT_VISION, 3600)));
    public static final Holder<Potion> LONG_NIGHT_VISION = Potions.register("long_night_vision", new Potion("night_vision", new MobEffectInstance(MobEffects.NIGHT_VISION, 9600)));
    public static final Holder<Potion> INVISIBILITY = Potions.register("invisibility", new Potion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, 3600)));
    public static final Holder<Potion> LONG_INVISIBILITY = Potions.register("long_invisibility", new Potion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, 9600)));
    public static final Holder<Potion> LEAPING = Potions.register("leaping", new Potion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, 3600)));
    public static final Holder<Potion> LONG_LEAPING = Potions.register("long_leaping", new Potion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, 9600)));
    public static final Holder<Potion> STRONG_LEAPING = Potions.register("strong_leaping", new Potion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, 1800, 1)));
    public static final Holder<Potion> FIRE_RESISTANCE = Potions.register("fire_resistance", new Potion("fire_resistance", new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600)));
    public static final Holder<Potion> LONG_FIRE_RESISTANCE = Potions.register("long_fire_resistance", new Potion("fire_resistance", new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 9600)));
    public static final Holder<Potion> SWIFTNESS = Potions.register("swiftness", new Potion("swiftness", new MobEffectInstance(MobEffects.SPEED, 3600)));
    public static final Holder<Potion> LONG_SWIFTNESS = Potions.register("long_swiftness", new Potion("swiftness", new MobEffectInstance(MobEffects.SPEED, 9600)));
    public static final Holder<Potion> STRONG_SWIFTNESS = Potions.register("strong_swiftness", new Potion("swiftness", new MobEffectInstance(MobEffects.SPEED, 1800, 1)));
    public static final Holder<Potion> SLOWNESS = Potions.register("slowness", new Potion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, 1800)));
    public static final Holder<Potion> LONG_SLOWNESS = Potions.register("long_slowness", new Potion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, 4800)));
    public static final Holder<Potion> STRONG_SLOWNESS = Potions.register("strong_slowness", new Potion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, 400, 3)));
    public static final Holder<Potion> TURTLE_MASTER = Potions.register("turtle_master", new Potion("turtle_master", new MobEffectInstance(MobEffects.SLOWNESS, 400, 3), new MobEffectInstance(MobEffects.RESISTANCE, 400, 2)));
    public static final Holder<Potion> LONG_TURTLE_MASTER = Potions.register("long_turtle_master", new Potion("turtle_master", new MobEffectInstance(MobEffects.SLOWNESS, 800, 3), new MobEffectInstance(MobEffects.RESISTANCE, 800, 2)));
    public static final Holder<Potion> STRONG_TURTLE_MASTER = Potions.register("strong_turtle_master", new Potion("turtle_master", new MobEffectInstance(MobEffects.SLOWNESS, 400, 5), new MobEffectInstance(MobEffects.RESISTANCE, 400, 3)));
    public static final Holder<Potion> WATER_BREATHING = Potions.register("water_breathing", new Potion("water_breathing", new MobEffectInstance(MobEffects.WATER_BREATHING, 3600)));
    public static final Holder<Potion> LONG_WATER_BREATHING = Potions.register("long_water_breathing", new Potion("water_breathing", new MobEffectInstance(MobEffects.WATER_BREATHING, 9600)));
    public static final Holder<Potion> HEALING = Potions.register("healing", new Potion("healing", new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1)));
    public static final Holder<Potion> STRONG_HEALING = Potions.register("strong_healing", new Potion("healing", new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 1)));
    public static final Holder<Potion> HARMING = Potions.register("harming", new Potion("harming", new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1)));
    public static final Holder<Potion> STRONG_HARMING = Potions.register("strong_harming", new Potion("harming", new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, 1)));
    public static final Holder<Potion> POISON = Potions.register("poison", new Potion("poison", new MobEffectInstance(MobEffects.POISON, 900)));
    public static final Holder<Potion> LONG_POISON = Potions.register("long_poison", new Potion("poison", new MobEffectInstance(MobEffects.POISON, 1800)));
    public static final Holder<Potion> STRONG_POISON = Potions.register("strong_poison", new Potion("poison", new MobEffectInstance(MobEffects.POISON, 432, 1)));
    public static final Holder<Potion> REGENERATION = Potions.register("regeneration", new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 900)));
    public static final Holder<Potion> LONG_REGENERATION = Potions.register("long_regeneration", new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 1800)));
    public static final Holder<Potion> STRONG_REGENERATION = Potions.register("strong_regeneration", new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 450, 1)));
    public static final Holder<Potion> STRENGTH = Potions.register("strength", new Potion("strength", new MobEffectInstance(MobEffects.STRENGTH, 3600)));
    public static final Holder<Potion> LONG_STRENGTH = Potions.register("long_strength", new Potion("strength", new MobEffectInstance(MobEffects.STRENGTH, 9600)));
    public static final Holder<Potion> STRONG_STRENGTH = Potions.register("strong_strength", new Potion("strength", new MobEffectInstance(MobEffects.STRENGTH, 1800, 1)));
    public static final Holder<Potion> WEAKNESS = Potions.register("weakness", new Potion("weakness", new MobEffectInstance(MobEffects.WEAKNESS, 1800)));
    public static final Holder<Potion> LONG_WEAKNESS = Potions.register("long_weakness", new Potion("weakness", new MobEffectInstance(MobEffects.WEAKNESS, 4800)));
    public static final Holder<Potion> LUCK = Potions.register("luck", new Potion("luck", new MobEffectInstance(MobEffects.LUCK, 6000)));
    public static final Holder<Potion> SLOW_FALLING = Potions.register("slow_falling", new Potion("slow_falling", new MobEffectInstance(MobEffects.SLOW_FALLING, 1800)));
    public static final Holder<Potion> LONG_SLOW_FALLING = Potions.register("long_slow_falling", new Potion("slow_falling", new MobEffectInstance(MobEffects.SLOW_FALLING, 4800)));
    public static final Holder<Potion> WIND_CHARGED = Potions.register("wind_charged", new Potion("wind_charged", new MobEffectInstance(MobEffects.WIND_CHARGED, 3600)));
    public static final Holder<Potion> WEAVING = Potions.register("weaving", new Potion("weaving", new MobEffectInstance(MobEffects.WEAVING, 3600)));
    public static final Holder<Potion> OOZING = Potions.register("oozing", new Potion("oozing", new MobEffectInstance(MobEffects.OOZING, 3600)));
    public static final Holder<Potion> INFESTED = Potions.register("infested", new Potion("infested", new MobEffectInstance(MobEffects.INFESTED, 3600)));

    private static Holder<Potion> register(String $$0, Potion $$1) {
        return Registry.registerForHolder(BuiltInRegistries.POTION, ResourceLocation.withDefaultNamespace($$0), $$1);
    }

    public static Holder<Potion> bootstrap(Registry<Potion> $$0) {
        return WATER;
    }
}

