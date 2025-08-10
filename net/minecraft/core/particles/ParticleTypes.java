/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ParticleTypes {
    public static final SimpleParticleType ANGRY_VILLAGER = ParticleTypes.register("angry_villager", false);
    public static final ParticleType<BlockParticleOption> BLOCK = ParticleTypes.register("block", false, BlockParticleOption::codec, BlockParticleOption::streamCodec);
    public static final ParticleType<BlockParticleOption> BLOCK_MARKER = ParticleTypes.register("block_marker", true, BlockParticleOption::codec, BlockParticleOption::streamCodec);
    public static final SimpleParticleType BUBBLE = ParticleTypes.register("bubble", false);
    public static final SimpleParticleType CLOUD = ParticleTypes.register("cloud", false);
    public static final SimpleParticleType CRIT = ParticleTypes.register("crit", false);
    public static final SimpleParticleType DAMAGE_INDICATOR = ParticleTypes.register("damage_indicator", true);
    public static final SimpleParticleType DRAGON_BREATH = ParticleTypes.register("dragon_breath", false);
    public static final SimpleParticleType DRIPPING_LAVA = ParticleTypes.register("dripping_lava", false);
    public static final SimpleParticleType FALLING_LAVA = ParticleTypes.register("falling_lava", false);
    public static final SimpleParticleType LANDING_LAVA = ParticleTypes.register("landing_lava", false);
    public static final SimpleParticleType DRIPPING_WATER = ParticleTypes.register("dripping_water", false);
    public static final SimpleParticleType FALLING_WATER = ParticleTypes.register("falling_water", false);
    public static final ParticleType<DustParticleOptions> DUST = ParticleTypes.register("dust", false, $$0 -> DustParticleOptions.CODEC, $$0 -> DustParticleOptions.STREAM_CODEC);
    public static final ParticleType<DustColorTransitionOptions> DUST_COLOR_TRANSITION = ParticleTypes.register("dust_color_transition", false, $$0 -> DustColorTransitionOptions.CODEC, $$0 -> DustColorTransitionOptions.STREAM_CODEC);
    public static final SimpleParticleType EFFECT = ParticleTypes.register("effect", false);
    public static final SimpleParticleType ELDER_GUARDIAN = ParticleTypes.register("elder_guardian", true);
    public static final SimpleParticleType ENCHANTED_HIT = ParticleTypes.register("enchanted_hit", false);
    public static final SimpleParticleType ENCHANT = ParticleTypes.register("enchant", false);
    public static final SimpleParticleType END_ROD = ParticleTypes.register("end_rod", false);
    public static final ParticleType<ColorParticleOption> ENTITY_EFFECT = ParticleTypes.register("entity_effect", false, ColorParticleOption::codec, ColorParticleOption::streamCodec);
    public static final SimpleParticleType EXPLOSION_EMITTER = ParticleTypes.register("explosion_emitter", true);
    public static final SimpleParticleType EXPLOSION = ParticleTypes.register("explosion", true);
    public static final SimpleParticleType GUST = ParticleTypes.register("gust", true);
    public static final SimpleParticleType SMALL_GUST = ParticleTypes.register("small_gust", false);
    public static final SimpleParticleType GUST_EMITTER_LARGE = ParticleTypes.register("gust_emitter_large", true);
    public static final SimpleParticleType GUST_EMITTER_SMALL = ParticleTypes.register("gust_emitter_small", true);
    public static final SimpleParticleType SONIC_BOOM = ParticleTypes.register("sonic_boom", true);
    public static final ParticleType<BlockParticleOption> FALLING_DUST = ParticleTypes.register("falling_dust", false, BlockParticleOption::codec, BlockParticleOption::streamCodec);
    public static final SimpleParticleType FIREWORK = ParticleTypes.register("firework", false);
    public static final SimpleParticleType FISHING = ParticleTypes.register("fishing", false);
    public static final SimpleParticleType FLAME = ParticleTypes.register("flame", false);
    public static final SimpleParticleType INFESTED = ParticleTypes.register("infested", false);
    public static final SimpleParticleType CHERRY_LEAVES = ParticleTypes.register("cherry_leaves", false);
    public static final SimpleParticleType PALE_OAK_LEAVES = ParticleTypes.register("pale_oak_leaves", false);
    public static final ParticleType<ColorParticleOption> TINTED_LEAVES = ParticleTypes.register("tinted_leaves", false, ColorParticleOption::codec, ColorParticleOption::streamCodec);
    public static final SimpleParticleType SCULK_SOUL = ParticleTypes.register("sculk_soul", false);
    public static final ParticleType<SculkChargeParticleOptions> SCULK_CHARGE = ParticleTypes.register("sculk_charge", true, $$0 -> SculkChargeParticleOptions.CODEC, $$0 -> SculkChargeParticleOptions.STREAM_CODEC);
    public static final SimpleParticleType SCULK_CHARGE_POP = ParticleTypes.register("sculk_charge_pop", true);
    public static final SimpleParticleType SOUL_FIRE_FLAME = ParticleTypes.register("soul_fire_flame", false);
    public static final SimpleParticleType SOUL = ParticleTypes.register("soul", false);
    public static final SimpleParticleType FLASH = ParticleTypes.register("flash", false);
    public static final SimpleParticleType HAPPY_VILLAGER = ParticleTypes.register("happy_villager", false);
    public static final SimpleParticleType COMPOSTER = ParticleTypes.register("composter", false);
    public static final SimpleParticleType HEART = ParticleTypes.register("heart", false);
    public static final SimpleParticleType INSTANT_EFFECT = ParticleTypes.register("instant_effect", false);
    public static final ParticleType<ItemParticleOption> ITEM = ParticleTypes.register("item", false, ItemParticleOption::codec, ItemParticleOption::streamCodec);
    public static final ParticleType<VibrationParticleOption> VIBRATION = ParticleTypes.register("vibration", true, $$0 -> VibrationParticleOption.CODEC, $$0 -> VibrationParticleOption.STREAM_CODEC);
    public static final ParticleType<TrailParticleOption> TRAIL = ParticleTypes.register("trail", false, $$0 -> TrailParticleOption.CODEC, $$0 -> TrailParticleOption.STREAM_CODEC);
    public static final SimpleParticleType ITEM_SLIME = ParticleTypes.register("item_slime", false);
    public static final SimpleParticleType ITEM_COBWEB = ParticleTypes.register("item_cobweb", false);
    public static final SimpleParticleType ITEM_SNOWBALL = ParticleTypes.register("item_snowball", false);
    public static final SimpleParticleType LARGE_SMOKE = ParticleTypes.register("large_smoke", false);
    public static final SimpleParticleType LAVA = ParticleTypes.register("lava", false);
    public static final SimpleParticleType MYCELIUM = ParticleTypes.register("mycelium", false);
    public static final SimpleParticleType NOTE = ParticleTypes.register("note", false);
    public static final SimpleParticleType POOF = ParticleTypes.register("poof", true);
    public static final SimpleParticleType PORTAL = ParticleTypes.register("portal", false);
    public static final SimpleParticleType RAIN = ParticleTypes.register("rain", false);
    public static final SimpleParticleType SMOKE = ParticleTypes.register("smoke", false);
    public static final SimpleParticleType WHITE_SMOKE = ParticleTypes.register("white_smoke", false);
    public static final SimpleParticleType SNEEZE = ParticleTypes.register("sneeze", false);
    public static final SimpleParticleType SPIT = ParticleTypes.register("spit", true);
    public static final SimpleParticleType SQUID_INK = ParticleTypes.register("squid_ink", true);
    public static final SimpleParticleType SWEEP_ATTACK = ParticleTypes.register("sweep_attack", true);
    public static final SimpleParticleType TOTEM_OF_UNDYING = ParticleTypes.register("totem_of_undying", false);
    public static final SimpleParticleType UNDERWATER = ParticleTypes.register("underwater", false);
    public static final SimpleParticleType SPLASH = ParticleTypes.register("splash", false);
    public static final SimpleParticleType WITCH = ParticleTypes.register("witch", false);
    public static final SimpleParticleType BUBBLE_POP = ParticleTypes.register("bubble_pop", false);
    public static final SimpleParticleType CURRENT_DOWN = ParticleTypes.register("current_down", false);
    public static final SimpleParticleType BUBBLE_COLUMN_UP = ParticleTypes.register("bubble_column_up", false);
    public static final SimpleParticleType NAUTILUS = ParticleTypes.register("nautilus", false);
    public static final SimpleParticleType DOLPHIN = ParticleTypes.register("dolphin", false);
    public static final SimpleParticleType CAMPFIRE_COSY_SMOKE = ParticleTypes.register("campfire_cosy_smoke", true);
    public static final SimpleParticleType CAMPFIRE_SIGNAL_SMOKE = ParticleTypes.register("campfire_signal_smoke", true);
    public static final SimpleParticleType DRIPPING_HONEY = ParticleTypes.register("dripping_honey", false);
    public static final SimpleParticleType FALLING_HONEY = ParticleTypes.register("falling_honey", false);
    public static final SimpleParticleType LANDING_HONEY = ParticleTypes.register("landing_honey", false);
    public static final SimpleParticleType FALLING_NECTAR = ParticleTypes.register("falling_nectar", false);
    public static final SimpleParticleType FALLING_SPORE_BLOSSOM = ParticleTypes.register("falling_spore_blossom", false);
    public static final SimpleParticleType ASH = ParticleTypes.register("ash", false);
    public static final SimpleParticleType CRIMSON_SPORE = ParticleTypes.register("crimson_spore", false);
    public static final SimpleParticleType WARPED_SPORE = ParticleTypes.register("warped_spore", false);
    public static final SimpleParticleType SPORE_BLOSSOM_AIR = ParticleTypes.register("spore_blossom_air", false);
    public static final SimpleParticleType DRIPPING_OBSIDIAN_TEAR = ParticleTypes.register("dripping_obsidian_tear", false);
    public static final SimpleParticleType FALLING_OBSIDIAN_TEAR = ParticleTypes.register("falling_obsidian_tear", false);
    public static final SimpleParticleType LANDING_OBSIDIAN_TEAR = ParticleTypes.register("landing_obsidian_tear", false);
    public static final SimpleParticleType REVERSE_PORTAL = ParticleTypes.register("reverse_portal", false);
    public static final SimpleParticleType WHITE_ASH = ParticleTypes.register("white_ash", false);
    public static final SimpleParticleType SMALL_FLAME = ParticleTypes.register("small_flame", false);
    public static final SimpleParticleType SNOWFLAKE = ParticleTypes.register("snowflake", false);
    public static final SimpleParticleType DRIPPING_DRIPSTONE_LAVA = ParticleTypes.register("dripping_dripstone_lava", false);
    public static final SimpleParticleType FALLING_DRIPSTONE_LAVA = ParticleTypes.register("falling_dripstone_lava", false);
    public static final SimpleParticleType DRIPPING_DRIPSTONE_WATER = ParticleTypes.register("dripping_dripstone_water", false);
    public static final SimpleParticleType FALLING_DRIPSTONE_WATER = ParticleTypes.register("falling_dripstone_water", false);
    public static final SimpleParticleType GLOW_SQUID_INK = ParticleTypes.register("glow_squid_ink", true);
    public static final SimpleParticleType GLOW = ParticleTypes.register("glow", true);
    public static final SimpleParticleType WAX_ON = ParticleTypes.register("wax_on", true);
    public static final SimpleParticleType WAX_OFF = ParticleTypes.register("wax_off", true);
    public static final SimpleParticleType ELECTRIC_SPARK = ParticleTypes.register("electric_spark", true);
    public static final SimpleParticleType SCRAPE = ParticleTypes.register("scrape", true);
    public static final ParticleType<ShriekParticleOption> SHRIEK = ParticleTypes.register("shriek", false, $$0 -> ShriekParticleOption.CODEC, $$0 -> ShriekParticleOption.STREAM_CODEC);
    public static final SimpleParticleType EGG_CRACK = ParticleTypes.register("egg_crack", false);
    public static final SimpleParticleType DUST_PLUME = ParticleTypes.register("dust_plume", false);
    public static final SimpleParticleType TRIAL_SPAWNER_DETECTED_PLAYER = ParticleTypes.register("trial_spawner_detection", true);
    public static final SimpleParticleType TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS = ParticleTypes.register("trial_spawner_detection_ominous", true);
    public static final SimpleParticleType VAULT_CONNECTION = ParticleTypes.register("vault_connection", true);
    public static final ParticleType<BlockParticleOption> DUST_PILLAR = ParticleTypes.register("dust_pillar", false, BlockParticleOption::codec, BlockParticleOption::streamCodec);
    public static final SimpleParticleType OMINOUS_SPAWNING = ParticleTypes.register("ominous_spawning", true);
    public static final SimpleParticleType RAID_OMEN = ParticleTypes.register("raid_omen", false);
    public static final SimpleParticleType TRIAL_OMEN = ParticleTypes.register("trial_omen", false);
    public static final ParticleType<BlockParticleOption> BLOCK_CRUMBLE = ParticleTypes.register("block_crumble", false, BlockParticleOption::codec, BlockParticleOption::streamCodec);
    public static final SimpleParticleType FIREFLY = ParticleTypes.register("firefly", false);
    public static final Codec<ParticleOptions> CODEC = BuiltInRegistries.PARTICLE_TYPE.byNameCodec().dispatch("type", ParticleOptions::getType, ParticleType::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleOptions> STREAM_CODEC = ByteBufCodecs.registry(Registries.PARTICLE_TYPE).dispatch(ParticleOptions::getType, ParticleType::streamCodec);

    private static SimpleParticleType register(String $$0, boolean $$1) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, $$0, new SimpleParticleType($$1));
    }

    private static <T extends ParticleOptions> ParticleType<T> register(String $$0, boolean $$1, final Function<ParticleType<T>, MapCodec<T>> $$2, final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> $$3) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, $$0, new ParticleType<T>($$1){

            @Override
            public MapCodec<T> codec() {
                return (MapCodec)$$2.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return (StreamCodec)$$3.apply(this);
            }
        });
    }
}

