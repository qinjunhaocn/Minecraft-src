/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.damagesource;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;

public interface DamageTypes {
    public static final ResourceKey<DamageType> IN_FIRE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("in_fire"));
    public static final ResourceKey<DamageType> CAMPFIRE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("campfire"));
    public static final ResourceKey<DamageType> LIGHTNING_BOLT = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("lightning_bolt"));
    public static final ResourceKey<DamageType> ON_FIRE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("on_fire"));
    public static final ResourceKey<DamageType> LAVA = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("lava"));
    public static final ResourceKey<DamageType> HOT_FLOOR = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("hot_floor"));
    public static final ResourceKey<DamageType> IN_WALL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("in_wall"));
    public static final ResourceKey<DamageType> CRAMMING = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("cramming"));
    public static final ResourceKey<DamageType> DROWN = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("drown"));
    public static final ResourceKey<DamageType> STARVE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("starve"));
    public static final ResourceKey<DamageType> CACTUS = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("cactus"));
    public static final ResourceKey<DamageType> FALL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("fall"));
    public static final ResourceKey<DamageType> ENDER_PEARL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("ender_pearl"));
    public static final ResourceKey<DamageType> FLY_INTO_WALL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("fly_into_wall"));
    public static final ResourceKey<DamageType> FELL_OUT_OF_WORLD = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("out_of_world"));
    public static final ResourceKey<DamageType> GENERIC = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("generic"));
    public static final ResourceKey<DamageType> MAGIC = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("magic"));
    public static final ResourceKey<DamageType> WITHER = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("wither"));
    public static final ResourceKey<DamageType> DRAGON_BREATH = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("dragon_breath"));
    public static final ResourceKey<DamageType> DRY_OUT = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("dry_out"));
    public static final ResourceKey<DamageType> SWEET_BERRY_BUSH = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("sweet_berry_bush"));
    public static final ResourceKey<DamageType> FREEZE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("freeze"));
    public static final ResourceKey<DamageType> STALAGMITE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("stalagmite"));
    public static final ResourceKey<DamageType> FALLING_BLOCK = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("falling_block"));
    public static final ResourceKey<DamageType> FALLING_ANVIL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("falling_anvil"));
    public static final ResourceKey<DamageType> FALLING_STALACTITE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("falling_stalactite"));
    public static final ResourceKey<DamageType> STING = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("sting"));
    public static final ResourceKey<DamageType> MOB_ATTACK = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("mob_attack"));
    public static final ResourceKey<DamageType> MOB_ATTACK_NO_AGGRO = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("mob_attack_no_aggro"));
    public static final ResourceKey<DamageType> PLAYER_ATTACK = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("player_attack"));
    public static final ResourceKey<DamageType> ARROW = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("arrow"));
    public static final ResourceKey<DamageType> TRIDENT = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("trident"));
    public static final ResourceKey<DamageType> MOB_PROJECTILE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("mob_projectile"));
    public static final ResourceKey<DamageType> SPIT = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("spit"));
    public static final ResourceKey<DamageType> WIND_CHARGE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("wind_charge"));
    public static final ResourceKey<DamageType> FIREWORKS = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("fireworks"));
    public static final ResourceKey<DamageType> FIREBALL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("fireball"));
    public static final ResourceKey<DamageType> UNATTRIBUTED_FIREBALL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("unattributed_fireball"));
    public static final ResourceKey<DamageType> WITHER_SKULL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("wither_skull"));
    public static final ResourceKey<DamageType> THROWN = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("thrown"));
    public static final ResourceKey<DamageType> INDIRECT_MAGIC = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("indirect_magic"));
    public static final ResourceKey<DamageType> THORNS = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("thorns"));
    public static final ResourceKey<DamageType> EXPLOSION = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("explosion"));
    public static final ResourceKey<DamageType> PLAYER_EXPLOSION = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("player_explosion"));
    public static final ResourceKey<DamageType> SONIC_BOOM = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("sonic_boom"));
    public static final ResourceKey<DamageType> BAD_RESPAWN_POINT = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("bad_respawn_point"));
    public static final ResourceKey<DamageType> OUTSIDE_BORDER = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("outside_border"));
    public static final ResourceKey<DamageType> GENERIC_KILL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("generic_kill"));
    public static final ResourceKey<DamageType> MACE_SMASH = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("mace_smash"));

    public static void bootstrap(BootstrapContext<DamageType> $$0) {
        $$0.register(IN_FIRE, new DamageType("inFire", 0.1f, DamageEffects.BURNING));
        $$0.register(CAMPFIRE, new DamageType("inFire", 0.1f, DamageEffects.BURNING));
        $$0.register(LIGHTNING_BOLT, new DamageType("lightningBolt", 0.1f));
        $$0.register(ON_FIRE, new DamageType("onFire", 0.0f, DamageEffects.BURNING));
        $$0.register(LAVA, new DamageType("lava", 0.1f, DamageEffects.BURNING));
        $$0.register(HOT_FLOOR, new DamageType("hotFloor", 0.1f, DamageEffects.BURNING));
        $$0.register(IN_WALL, new DamageType("inWall", 0.0f));
        $$0.register(CRAMMING, new DamageType("cramming", 0.0f));
        $$0.register(DROWN, new DamageType("drown", 0.0f, DamageEffects.DROWNING));
        $$0.register(STARVE, new DamageType("starve", 0.0f));
        $$0.register(CACTUS, new DamageType("cactus", 0.1f));
        $$0.register(FALL, new DamageType("fall", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0f, DamageEffects.HURT, DeathMessageType.FALL_VARIANTS));
        $$0.register(ENDER_PEARL, new DamageType("fall", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0f, DamageEffects.HURT, DeathMessageType.FALL_VARIANTS));
        $$0.register(FLY_INTO_WALL, new DamageType("flyIntoWall", 0.0f));
        $$0.register(FELL_OUT_OF_WORLD, new DamageType("outOfWorld", 0.0f));
        $$0.register(GENERIC, new DamageType("generic", 0.0f));
        $$0.register(MAGIC, new DamageType("magic", 0.0f));
        $$0.register(WITHER, new DamageType("wither", 0.0f));
        $$0.register(DRAGON_BREATH, new DamageType("dragonBreath", 0.0f));
        $$0.register(DRY_OUT, new DamageType("dryout", 0.1f));
        $$0.register(SWEET_BERRY_BUSH, new DamageType("sweetBerryBush", 0.1f, DamageEffects.POKING));
        $$0.register(FREEZE, new DamageType("freeze", 0.0f, DamageEffects.FREEZING));
        $$0.register(STALAGMITE, new DamageType("stalagmite", 0.0f));
        $$0.register(FALLING_BLOCK, new DamageType("fallingBlock", 0.1f));
        $$0.register(FALLING_ANVIL, new DamageType("anvil", 0.1f));
        $$0.register(FALLING_STALACTITE, new DamageType("fallingStalactite", 0.1f));
        $$0.register(STING, new DamageType("sting", 0.1f));
        $$0.register(MOB_ATTACK, new DamageType("mob", 0.1f));
        $$0.register(MOB_ATTACK_NO_AGGRO, new DamageType("mob", 0.1f));
        $$0.register(PLAYER_ATTACK, new DamageType("player", 0.1f));
        $$0.register(ARROW, new DamageType("arrow", 0.1f));
        $$0.register(TRIDENT, new DamageType("trident", 0.1f));
        $$0.register(MOB_PROJECTILE, new DamageType("mob", 0.1f));
        $$0.register(SPIT, new DamageType("mob", 0.1f));
        $$0.register(FIREWORKS, new DamageType("fireworks", 0.1f));
        $$0.register(UNATTRIBUTED_FIREBALL, new DamageType("onFire", 0.1f, DamageEffects.BURNING));
        $$0.register(FIREBALL, new DamageType("fireball", 0.1f, DamageEffects.BURNING));
        $$0.register(WITHER_SKULL, new DamageType("witherSkull", 0.1f));
        $$0.register(THROWN, new DamageType("thrown", 0.1f));
        $$0.register(INDIRECT_MAGIC, new DamageType("indirectMagic", 0.0f));
        $$0.register(THORNS, new DamageType("thorns", 0.1f, DamageEffects.THORNS));
        $$0.register(EXPLOSION, new DamageType("explosion", DamageScaling.ALWAYS, 0.1f));
        $$0.register(PLAYER_EXPLOSION, new DamageType("explosion.player", DamageScaling.ALWAYS, 0.1f));
        $$0.register(SONIC_BOOM, new DamageType("sonic_boom", DamageScaling.ALWAYS, 0.0f));
        $$0.register(BAD_RESPAWN_POINT, new DamageType("badRespawnPoint", DamageScaling.ALWAYS, 0.1f, DamageEffects.HURT, DeathMessageType.INTENTIONAL_GAME_DESIGN));
        $$0.register(OUTSIDE_BORDER, new DamageType("outsideBorder", 0.0f));
        $$0.register(GENERIC_KILL, new DamageType("genericKill", 0.0f));
        $$0.register(WIND_CHARGE, new DamageType("mob", 0.1f));
        $$0.register(MACE_SMASH, new DamageType("mace_smash", 0.1f));
    }
}

