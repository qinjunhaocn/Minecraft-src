/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model.geom;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModelLayers {
    private static final String DEFAULT_LAYER = "main";
    private static final Set<ModelLayerLocation> ALL_MODELS = Sets.newHashSet();
    public static final ModelLayerLocation ACACIA_BOAT = ModelLayers.register("boat/acacia");
    public static final ModelLayerLocation ACACIA_CHEST_BOAT = ModelLayers.register("chest_boat/acacia");
    public static final ModelLayerLocation ALLAY = ModelLayers.register("allay");
    public static final ModelLayerLocation ARMADILLO = ModelLayers.register("armadillo");
    public static final ModelLayerLocation ARMADILLO_BABY = ModelLayers.register("armadillo_baby");
    public static final ModelLayerLocation ARMOR_STAND = ModelLayers.register("armor_stand");
    public static final ModelLayerLocation ARMOR_STAND_INNER_ARMOR = ModelLayers.registerInnerArmor("armor_stand");
    public static final ModelLayerLocation ARMOR_STAND_OUTER_ARMOR = ModelLayers.registerOuterArmor("armor_stand");
    public static final ModelLayerLocation ARMOR_STAND_SMALL = ModelLayers.register("armor_stand_small");
    public static final ModelLayerLocation ARMOR_STAND_SMALL_INNER_ARMOR = ModelLayers.registerInnerArmor("armor_stand_small");
    public static final ModelLayerLocation ARMOR_STAND_SMALL_OUTER_ARMOR = ModelLayers.registerOuterArmor("armor_stand_small");
    public static final ModelLayerLocation ARROW = ModelLayers.register("arrow");
    public static final ModelLayerLocation AXOLOTL = ModelLayers.register("axolotl");
    public static final ModelLayerLocation AXOLOTL_BABY = ModelLayers.register("axolotl_baby");
    public static final ModelLayerLocation BAMBOO_CHEST_RAFT = ModelLayers.register("chest_boat/bamboo");
    public static final ModelLayerLocation BAMBOO_RAFT = ModelLayers.register("boat/bamboo");
    public static final ModelLayerLocation STANDING_BANNER = ModelLayers.register("standing_banner");
    public static final ModelLayerLocation STANDING_BANNER_FLAG = ModelLayers.register("standing_banner", "flag");
    public static final ModelLayerLocation WALL_BANNER = ModelLayers.register("wall_banner");
    public static final ModelLayerLocation WALL_BANNER_FLAG = ModelLayers.register("wall_banner", "flag");
    public static final ModelLayerLocation BAT = ModelLayers.register("bat");
    public static final ModelLayerLocation BED_FOOT = ModelLayers.register("bed_foot");
    public static final ModelLayerLocation BED_HEAD = ModelLayers.register("bed_head");
    public static final ModelLayerLocation BEE = ModelLayers.register("bee");
    public static final ModelLayerLocation BEE_BABY = ModelLayers.register("bee_baby");
    public static final ModelLayerLocation BEE_STINGER = ModelLayers.register("bee_stinger");
    public static final ModelLayerLocation BELL = ModelLayers.register("bell");
    public static final ModelLayerLocation BIRCH_BOAT = ModelLayers.register("boat/birch");
    public static final ModelLayerLocation BIRCH_CHEST_BOAT = ModelLayers.register("chest_boat/birch");
    public static final ModelLayerLocation BLAZE = ModelLayers.register("blaze");
    public static final ModelLayerLocation BOAT_WATER_PATCH = ModelLayers.register("boat", "water_patch");
    public static final ModelLayerLocation BOGGED = ModelLayers.register("bogged");
    public static final ModelLayerLocation BOGGED_INNER_ARMOR = ModelLayers.registerInnerArmor("bogged");
    public static final ModelLayerLocation BOGGED_OUTER_ARMOR = ModelLayers.registerOuterArmor("bogged");
    public static final ModelLayerLocation BOGGED_OUTER_LAYER = ModelLayers.register("bogged", "outer");
    public static final ModelLayerLocation BOOK = ModelLayers.register("book");
    public static final ModelLayerLocation BREEZE = ModelLayers.register("breeze");
    public static final ModelLayerLocation BREEZE_WIND = ModelLayers.register("breeze_wind");
    public static final ModelLayerLocation CAMEL = ModelLayers.register("camel");
    public static final ModelLayerLocation CAMEL_BABY = ModelLayers.register("camel_baby");
    public static final ModelLayerLocation CAMEL_SADDLE = ModelLayers.register("camel", "saddle");
    public static final ModelLayerLocation CAMEL_BABY_SADDLE = ModelLayers.register("camel_baby", "saddle");
    public static final ModelLayerLocation CAT = ModelLayers.register("cat");
    public static final ModelLayerLocation CAT_BABY = ModelLayers.register("cat_baby");
    public static final ModelLayerLocation CAT_BABY_COLLAR = ModelLayers.register("cat_baby", "collar");
    public static final ModelLayerLocation CAT_COLLAR = ModelLayers.register("cat", "collar");
    public static final ModelLayerLocation CAVE_SPIDER = ModelLayers.register("cave_spider");
    public static final ModelLayerLocation CHERRY_BOAT = ModelLayers.register("boat/cherry");
    public static final ModelLayerLocation CHERRY_CHEST_BOAT = ModelLayers.register("chest_boat/cherry");
    public static final ModelLayerLocation CHEST = ModelLayers.register("chest");
    public static final ModelLayerLocation CHEST_MINECART = ModelLayers.register("chest_minecart");
    public static final ModelLayerLocation CHICKEN = ModelLayers.register("chicken");
    public static final ModelLayerLocation CHICKEN_BABY = ModelLayers.register("chicken_baby");
    public static final ModelLayerLocation COD = ModelLayers.register("cod");
    public static final ModelLayerLocation COLD_CHICKEN = ModelLayers.register("cold_chicken");
    public static final ModelLayerLocation COLD_CHICKEN_BABY = ModelLayers.register("cold_chicken_baby");
    public static final ModelLayerLocation COLD_COW = ModelLayers.register("cold_cow");
    public static final ModelLayerLocation COLD_COW_BABY = ModelLayers.register("cold_cow_baby");
    public static final ModelLayerLocation COLD_PIG = ModelLayers.register("cold_pig");
    public static final ModelLayerLocation COLD_PIG_BABY = ModelLayers.register("cold_pig_baby");
    public static final ModelLayerLocation COMMAND_BLOCK_MINECART = ModelLayers.register("command_block_minecart");
    public static final ModelLayerLocation CONDUIT_CAGE = ModelLayers.register("conduit", "cage");
    public static final ModelLayerLocation CONDUIT_EYE = ModelLayers.register("conduit", "eye");
    public static final ModelLayerLocation CONDUIT_SHELL = ModelLayers.register("conduit", "shell");
    public static final ModelLayerLocation CONDUIT_WIND = ModelLayers.register("conduit", "wind");
    public static final ModelLayerLocation COW = ModelLayers.register("cow");
    public static final ModelLayerLocation COW_BABY = ModelLayers.register("cow_baby");
    public static final ModelLayerLocation CREAKING = ModelLayers.register("creaking");
    public static final ModelLayerLocation CREEPER = ModelLayers.register("creeper");
    public static final ModelLayerLocation CREEPER_ARMOR = ModelLayers.register("creeper", "armor");
    public static final ModelLayerLocation CREEPER_HEAD = ModelLayers.register("creeper_head");
    public static final ModelLayerLocation DARK_OAK_BOAT = ModelLayers.register("boat/dark_oak");
    public static final ModelLayerLocation DARK_OAK_CHEST_BOAT = ModelLayers.register("chest_boat/dark_oak");
    public static final ModelLayerLocation DECORATED_POT_BASE = ModelLayers.register("decorated_pot_base");
    public static final ModelLayerLocation DECORATED_POT_SIDES = ModelLayers.register("decorated_pot_sides");
    public static final ModelLayerLocation DOLPHIN = ModelLayers.register("dolphin");
    public static final ModelLayerLocation DOLPHIN_BABY = ModelLayers.register("dolphin_baby");
    public static final ModelLayerLocation DONKEY = ModelLayers.register("donkey");
    public static final ModelLayerLocation DONKEY_BABY = ModelLayers.register("donkey_baby");
    public static final ModelLayerLocation DONKEY_SADDLE = ModelLayers.register("donkey", "saddle");
    public static final ModelLayerLocation DONKEY_BABY_SADDLE = ModelLayers.register("donkey_baby", "saddle");
    public static final ModelLayerLocation DOUBLE_CHEST_LEFT = ModelLayers.register("double_chest_left");
    public static final ModelLayerLocation DOUBLE_CHEST_RIGHT = ModelLayers.register("double_chest_right");
    public static final ModelLayerLocation DRAGON_SKULL = ModelLayers.register("dragon_skull");
    public static final ModelLayerLocation DROWNED = ModelLayers.register("drowned");
    public static final ModelLayerLocation DROWNED_BABY = ModelLayers.register("drowned_baby");
    public static final ModelLayerLocation DROWNED_BABY_INNER_ARMOR = ModelLayers.registerInnerArmor("drowned_baby");
    public static final ModelLayerLocation DROWNED_BABY_OUTER_ARMOR = ModelLayers.registerOuterArmor("drowned_baby");
    public static final ModelLayerLocation DROWNED_BABY_OUTER_LAYER = ModelLayers.register("drowned_baby", "outer");
    public static final ModelLayerLocation DROWNED_INNER_ARMOR = ModelLayers.registerInnerArmor("drowned");
    public static final ModelLayerLocation DROWNED_OUTER_ARMOR = ModelLayers.registerOuterArmor("drowned");
    public static final ModelLayerLocation DROWNED_OUTER_LAYER = ModelLayers.register("drowned", "outer");
    public static final ModelLayerLocation ELDER_GUARDIAN = ModelLayers.register("elder_guardian");
    public static final ModelLayerLocation ELYTRA = ModelLayers.register("elytra");
    public static final ModelLayerLocation ELYTRA_BABY = ModelLayers.register("elytra_baby");
    public static final ModelLayerLocation ENDERMAN = ModelLayers.register("enderman");
    public static final ModelLayerLocation ENDERMITE = ModelLayers.register("endermite");
    public static final ModelLayerLocation ENDER_DRAGON = ModelLayers.register("ender_dragon");
    public static final ModelLayerLocation END_CRYSTAL = ModelLayers.register("end_crystal");
    public static final ModelLayerLocation EVOKER = ModelLayers.register("evoker");
    public static final ModelLayerLocation EVOKER_FANGS = ModelLayers.register("evoker_fangs");
    public static final ModelLayerLocation FOX = ModelLayers.register("fox");
    public static final ModelLayerLocation FOX_BABY = ModelLayers.register("fox_baby");
    public static final ModelLayerLocation FROG = ModelLayers.register("frog");
    public static final ModelLayerLocation FURNACE_MINECART = ModelLayers.register("furnace_minecart");
    public static final ModelLayerLocation GHAST = ModelLayers.register("ghast");
    public static final ModelLayerLocation GIANT = ModelLayers.register("giant");
    public static final ModelLayerLocation GIANT_INNER_ARMOR = ModelLayers.registerInnerArmor("giant");
    public static final ModelLayerLocation GIANT_OUTER_ARMOR = ModelLayers.registerOuterArmor("giant");
    public static final ModelLayerLocation GLOW_SQUID = ModelLayers.register("glow_squid");
    public static final ModelLayerLocation GLOW_SQUID_BABY = ModelLayers.register("glow_squid_baby");
    public static final ModelLayerLocation GOAT = ModelLayers.register("goat");
    public static final ModelLayerLocation GOAT_BABY = ModelLayers.register("goat_baby");
    public static final ModelLayerLocation GUARDIAN = ModelLayers.register("guardian");
    public static final ModelLayerLocation HAPPY_GHAST = ModelLayers.register("happy_ghast");
    public static final ModelLayerLocation HAPPY_GHAST_BABY = ModelLayers.register("happy_ghast_baby");
    public static final ModelLayerLocation HAPPY_GHAST_HARNESS = ModelLayers.register("happy_ghast_harness");
    public static final ModelLayerLocation HAPPY_GHAST_BABY_HARNESS = ModelLayers.register("happy_ghast_baby_harness");
    public static final ModelLayerLocation HAPPY_GHAST_ROPES = ModelLayers.register("happy_ghast_ropes");
    public static final ModelLayerLocation HAPPY_GHAST_BABY_ROPES = ModelLayers.register("happy_ghast_baby_ropes");
    public static final ModelLayerLocation HOGLIN = ModelLayers.register("hoglin");
    public static final ModelLayerLocation HOGLIN_BABY = ModelLayers.register("hoglin_baby");
    public static final ModelLayerLocation HOPPER_MINECART = ModelLayers.register("hopper_minecart");
    public static final ModelLayerLocation HORSE = ModelLayers.register("horse");
    public static final ModelLayerLocation HORSE_ARMOR = ModelLayers.register("horse_armor");
    public static final ModelLayerLocation HORSE_SADDLE = ModelLayers.register("horse", "saddle");
    public static final ModelLayerLocation HORSE_BABY = ModelLayers.register("horse_baby");
    public static final ModelLayerLocation HORSE_BABY_ARMOR = ModelLayers.register("horse_armor_baby");
    public static final ModelLayerLocation HORSE_BABY_SADDLE = ModelLayers.register("horse_baby", "saddle");
    public static final ModelLayerLocation HUSK = ModelLayers.register("husk");
    public static final ModelLayerLocation HUSK_BABY = ModelLayers.register("husk_baby");
    public static final ModelLayerLocation HUSK_BABY_INNER_ARMOR = ModelLayers.registerInnerArmor("husk_baby");
    public static final ModelLayerLocation HUSK_BABY_OUTER_ARMOR = ModelLayers.registerOuterArmor("husk_baby");
    public static final ModelLayerLocation HUSK_INNER_ARMOR = ModelLayers.registerInnerArmor("husk");
    public static final ModelLayerLocation HUSK_OUTER_ARMOR = ModelLayers.registerOuterArmor("husk");
    public static final ModelLayerLocation ILLUSIONER = ModelLayers.register("illusioner");
    public static final ModelLayerLocation IRON_GOLEM = ModelLayers.register("iron_golem");
    public static final ModelLayerLocation JUNGLE_BOAT = ModelLayers.register("boat/jungle");
    public static final ModelLayerLocation JUNGLE_CHEST_BOAT = ModelLayers.register("chest_boat/jungle");
    public static final ModelLayerLocation LEASH_KNOT = ModelLayers.register("leash_knot");
    public static final ModelLayerLocation LLAMA = ModelLayers.register("llama");
    public static final ModelLayerLocation LLAMA_BABY = ModelLayers.register("llama_baby");
    public static final ModelLayerLocation LLAMA_BABY_DECOR = ModelLayers.register("llama_baby", "decor");
    public static final ModelLayerLocation LLAMA_DECOR = ModelLayers.register("llama", "decor");
    public static final ModelLayerLocation LLAMA_SPIT = ModelLayers.register("llama_spit");
    public static final ModelLayerLocation MAGMA_CUBE = ModelLayers.register("magma_cube");
    public static final ModelLayerLocation MANGROVE_BOAT = ModelLayers.register("boat/mangrove");
    public static final ModelLayerLocation MANGROVE_CHEST_BOAT = ModelLayers.register("chest_boat/mangrove");
    public static final ModelLayerLocation MINECART = ModelLayers.register("minecart");
    public static final ModelLayerLocation MOOSHROOM = ModelLayers.register("mooshroom");
    public static final ModelLayerLocation MOOSHROOM_BABY = ModelLayers.register("mooshroom_baby");
    public static final ModelLayerLocation MULE = ModelLayers.register("mule");
    public static final ModelLayerLocation MULE_BABY = ModelLayers.register("mule_baby");
    public static final ModelLayerLocation MULE_SADDLE = ModelLayers.register("mule", "saddle");
    public static final ModelLayerLocation MULE_BABY_SADDLE = ModelLayers.register("mule_baby", "saddle");
    public static final ModelLayerLocation OAK_BOAT = ModelLayers.register("boat/oak");
    public static final ModelLayerLocation OAK_CHEST_BOAT = ModelLayers.register("chest_boat/oak");
    public static final ModelLayerLocation OCELOT = ModelLayers.register("ocelot");
    public static final ModelLayerLocation OCELOT_BABY = ModelLayers.register("ocelot_baby");
    public static final ModelLayerLocation PALE_OAK_BOAT = ModelLayers.register("boat/pale_oak");
    public static final ModelLayerLocation PALE_OAK_CHEST_BOAT = ModelLayers.register("chest_boat/pale_oak");
    public static final ModelLayerLocation PANDA = ModelLayers.register("panda");
    public static final ModelLayerLocation PANDA_BABY = ModelLayers.register("panda_baby");
    public static final ModelLayerLocation PARROT = ModelLayers.register("parrot");
    public static final ModelLayerLocation PHANTOM = ModelLayers.register("phantom");
    public static final ModelLayerLocation PIG = ModelLayers.register("pig");
    public static final ModelLayerLocation PIGLIN = ModelLayers.register("piglin");
    public static final ModelLayerLocation PIGLIN_BABY = ModelLayers.register("piglin_baby");
    public static final ModelLayerLocation PIGLIN_BABY_INNER_ARMOR = ModelLayers.registerInnerArmor("piglin_baby");
    public static final ModelLayerLocation PIGLIN_BABY_OUTER_ARMOR = ModelLayers.registerOuterArmor("piglin_baby");
    public static final ModelLayerLocation PIGLIN_BRUTE = ModelLayers.register("piglin_brute");
    public static final ModelLayerLocation PIGLIN_BRUTE_INNER_ARMOR = ModelLayers.registerInnerArmor("piglin_brute");
    public static final ModelLayerLocation PIGLIN_BRUTE_OUTER_ARMOR = ModelLayers.registerOuterArmor("piglin_brute");
    public static final ModelLayerLocation PIGLIN_HEAD = ModelLayers.register("piglin_head");
    public static final ModelLayerLocation PIGLIN_INNER_ARMOR = ModelLayers.registerInnerArmor("piglin");
    public static final ModelLayerLocation PIGLIN_OUTER_ARMOR = ModelLayers.registerOuterArmor("piglin");
    public static final ModelLayerLocation PIG_BABY = ModelLayers.register("pig_baby");
    public static final ModelLayerLocation PIG_BABY_SADDLE = ModelLayers.register("pig_baby", "saddle");
    public static final ModelLayerLocation PIG_SADDLE = ModelLayers.register("pig", "saddle");
    public static final ModelLayerLocation PILLAGER = ModelLayers.register("pillager");
    public static final ModelLayerLocation PLAYER = ModelLayers.register("player");
    public static final ModelLayerLocation PLAYER_CAPE = ModelLayers.register("player", "cape");
    public static final ModelLayerLocation PLAYER_EARS = ModelLayers.register("player", "ears");
    public static final ModelLayerLocation PLAYER_HEAD = ModelLayers.register("player_head");
    public static final ModelLayerLocation PLAYER_INNER_ARMOR = ModelLayers.registerInnerArmor("player");
    public static final ModelLayerLocation PLAYER_OUTER_ARMOR = ModelLayers.registerOuterArmor("player");
    public static final ModelLayerLocation PLAYER_SLIM = ModelLayers.register("player_slim");
    public static final ModelLayerLocation PLAYER_SLIM_INNER_ARMOR = ModelLayers.registerInnerArmor("player_slim");
    public static final ModelLayerLocation PLAYER_SLIM_OUTER_ARMOR = ModelLayers.registerOuterArmor("player_slim");
    public static final ModelLayerLocation PLAYER_SPIN_ATTACK = ModelLayers.register("spin_attack");
    public static final ModelLayerLocation POLAR_BEAR = ModelLayers.register("polar_bear");
    public static final ModelLayerLocation POLAR_BEAR_BABY = ModelLayers.register("polar_bear_baby");
    public static final ModelLayerLocation PUFFERFISH_BIG = ModelLayers.register("pufferfish_big");
    public static final ModelLayerLocation PUFFERFISH_MEDIUM = ModelLayers.register("pufferfish_medium");
    public static final ModelLayerLocation PUFFERFISH_SMALL = ModelLayers.register("pufferfish_small");
    public static final ModelLayerLocation RABBIT = ModelLayers.register("rabbit");
    public static final ModelLayerLocation RABBIT_BABY = ModelLayers.register("rabbit_baby");
    public static final ModelLayerLocation RAVAGER = ModelLayers.register("ravager");
    public static final ModelLayerLocation SALMON = ModelLayers.register("salmon");
    public static final ModelLayerLocation SALMON_LARGE = ModelLayers.register("salmon_large");
    public static final ModelLayerLocation SALMON_SMALL = ModelLayers.register("salmon_small");
    public static final ModelLayerLocation SHEEP = ModelLayers.register("sheep");
    public static final ModelLayerLocation SHEEP_BABY = ModelLayers.register("sheep_baby");
    public static final ModelLayerLocation SHEEP_BABY_WOOL = ModelLayers.register("sheep_baby", "wool");
    public static final ModelLayerLocation SHEEP_WOOL = ModelLayers.register("sheep", "wool");
    public static final ModelLayerLocation SHEEP_WOOL_UNDERCOAT = ModelLayers.register("sheep", "wool_undercoat");
    public static final ModelLayerLocation SHEEP_BABY_WOOL_UNDERCOAT = ModelLayers.register("sheep_baby", "wool_undercoat");
    public static final ModelLayerLocation SHIELD = ModelLayers.register("shield");
    public static final ModelLayerLocation SHULKER = ModelLayers.register("shulker");
    public static final ModelLayerLocation SHULKER_BOX = ModelLayers.register("shulker_box");
    public static final ModelLayerLocation SHULKER_BULLET = ModelLayers.register("shulker_bullet");
    public static final ModelLayerLocation SILVERFISH = ModelLayers.register("silverfish");
    public static final ModelLayerLocation SKELETON = ModelLayers.register("skeleton");
    public static final ModelLayerLocation SKELETON_HORSE = ModelLayers.register("skeleton_horse");
    public static final ModelLayerLocation SKELETON_HORSE_BABY = ModelLayers.register("skeleton_horse_baby");
    public static final ModelLayerLocation SKELETON_HORSE_SADDLE = ModelLayers.register("skeleton_horse", "saddle");
    public static final ModelLayerLocation SKELETON_HORSE_BABY_SADDLE = ModelLayers.register("skeleton_horse_baby", "saddle");
    public static final ModelLayerLocation SKELETON_INNER_ARMOR = ModelLayers.registerInnerArmor("skeleton");
    public static final ModelLayerLocation SKELETON_OUTER_ARMOR = ModelLayers.registerOuterArmor("skeleton");
    public static final ModelLayerLocation SKELETON_SKULL = ModelLayers.register("skeleton_skull");
    public static final ModelLayerLocation SLIME = ModelLayers.register("slime");
    public static final ModelLayerLocation SLIME_OUTER = ModelLayers.register("slime", "outer");
    public static final ModelLayerLocation SNIFFER = ModelLayers.register("sniffer");
    public static final ModelLayerLocation SNIFFER_BABY = ModelLayers.register("sniffer_baby");
    public static final ModelLayerLocation SNOW_GOLEM = ModelLayers.register("snow_golem");
    public static final ModelLayerLocation SPAWNER_MINECART = ModelLayers.register("spawner_minecart");
    public static final ModelLayerLocation SPIDER = ModelLayers.register("spider");
    public static final ModelLayerLocation SPRUCE_BOAT = ModelLayers.register("boat/spruce");
    public static final ModelLayerLocation SPRUCE_CHEST_BOAT = ModelLayers.register("chest_boat/spruce");
    public static final ModelLayerLocation SQUID = ModelLayers.register("squid");
    public static final ModelLayerLocation SQUID_BABY = ModelLayers.register("squid_baby");
    public static final ModelLayerLocation STRAY = ModelLayers.register("stray");
    public static final ModelLayerLocation STRAY_INNER_ARMOR = ModelLayers.registerInnerArmor("stray");
    public static final ModelLayerLocation STRAY_OUTER_ARMOR = ModelLayers.registerOuterArmor("stray");
    public static final ModelLayerLocation STRAY_OUTER_LAYER = ModelLayers.register("stray", "outer");
    public static final ModelLayerLocation STRIDER = ModelLayers.register("strider");
    public static final ModelLayerLocation STRIDER_SADDLE = ModelLayers.register("strider", "saddle");
    public static final ModelLayerLocation STRIDER_BABY = ModelLayers.register("strider_baby");
    public static final ModelLayerLocation STRIDER_BABY_SADDLE = ModelLayers.register("strider_baby", "saddle");
    public static final ModelLayerLocation TADPOLE = ModelLayers.register("tadpole");
    public static final ModelLayerLocation TNT_MINECART = ModelLayers.register("tnt_minecart");
    public static final ModelLayerLocation TRADER_LLAMA = ModelLayers.register("trader_llama");
    public static final ModelLayerLocation TRADER_LLAMA_BABY = ModelLayers.register("trader_llama_baby");
    public static final ModelLayerLocation TRIDENT = ModelLayers.register("trident");
    public static final ModelLayerLocation TROPICAL_FISH_LARGE = ModelLayers.register("tropical_fish_large");
    public static final ModelLayerLocation TROPICAL_FISH_LARGE_PATTERN = ModelLayers.register("tropical_fish_large", "pattern");
    public static final ModelLayerLocation TROPICAL_FISH_SMALL = ModelLayers.register("tropical_fish_small");
    public static final ModelLayerLocation TROPICAL_FISH_SMALL_PATTERN = ModelLayers.register("tropical_fish_small", "pattern");
    public static final ModelLayerLocation TURTLE = ModelLayers.register("turtle");
    public static final ModelLayerLocation TURTLE_BABY = ModelLayers.register("turtle_baby");
    public static final ModelLayerLocation VEX = ModelLayers.register("vex");
    public static final ModelLayerLocation VILLAGER = ModelLayers.register("villager");
    public static final ModelLayerLocation VILLAGER_BABY = ModelLayers.register("villager_baby");
    public static final ModelLayerLocation VINDICATOR = ModelLayers.register("vindicator");
    public static final ModelLayerLocation WANDERING_TRADER = ModelLayers.register("wandering_trader");
    public static final ModelLayerLocation WARDEN = ModelLayers.register("warden");
    public static final ModelLayerLocation WARM_COW = ModelLayers.register("warm_cow");
    public static final ModelLayerLocation WARM_COW_BABY = ModelLayers.register("warm_cow_baby");
    public static final ModelLayerLocation WIND_CHARGE = ModelLayers.register("wind_charge");
    public static final ModelLayerLocation WITCH = ModelLayers.register("witch");
    public static final ModelLayerLocation WITHER = ModelLayers.register("wither");
    public static final ModelLayerLocation WITHER_ARMOR = ModelLayers.register("wither", "armor");
    public static final ModelLayerLocation WITHER_SKELETON = ModelLayers.register("wither_skeleton");
    public static final ModelLayerLocation WITHER_SKELETON_INNER_ARMOR = ModelLayers.registerInnerArmor("wither_skeleton");
    public static final ModelLayerLocation WITHER_SKELETON_OUTER_ARMOR = ModelLayers.registerOuterArmor("wither_skeleton");
    public static final ModelLayerLocation WITHER_SKELETON_SKULL = ModelLayers.register("wither_skeleton_skull");
    public static final ModelLayerLocation WITHER_SKULL = ModelLayers.register("wither_skull");
    public static final ModelLayerLocation WOLF = ModelLayers.register("wolf");
    public static final ModelLayerLocation WOLF_ARMOR = ModelLayers.register("wolf_armor");
    public static final ModelLayerLocation WOLF_BABY = ModelLayers.register("wolf_baby");
    public static final ModelLayerLocation WOLF_BABY_ARMOR = ModelLayers.register("wolf_baby_armor");
    public static final ModelLayerLocation ZOGLIN = ModelLayers.register("zoglin");
    public static final ModelLayerLocation ZOGLIN_BABY = ModelLayers.register("zoglin_baby");
    public static final ModelLayerLocation ZOMBIE = ModelLayers.register("zombie");
    public static final ModelLayerLocation ZOMBIE_BABY = ModelLayers.register("zombie_baby");
    public static final ModelLayerLocation ZOMBIE_BABY_INNER_ARMOR = ModelLayers.registerInnerArmor("zombie_baby");
    public static final ModelLayerLocation ZOMBIE_BABY_OUTER_ARMOR = ModelLayers.registerOuterArmor("zombie_baby");
    public static final ModelLayerLocation ZOMBIE_HEAD = ModelLayers.register("zombie_head");
    public static final ModelLayerLocation ZOMBIE_HORSE = ModelLayers.register("zombie_horse");
    public static final ModelLayerLocation ZOMBIE_HORSE_BABY = ModelLayers.register("zombie_horse_baby");
    public static final ModelLayerLocation ZOMBIE_HORSE_SADDLE = ModelLayers.register("zombie_horse", "saddle");
    public static final ModelLayerLocation ZOMBIE_HORSE_BABY_SADDLE = ModelLayers.register("zombie_horse_baby", "saddle");
    public static final ModelLayerLocation ZOMBIE_INNER_ARMOR = ModelLayers.registerInnerArmor("zombie");
    public static final ModelLayerLocation ZOMBIE_OUTER_ARMOR = ModelLayers.registerOuterArmor("zombie");
    public static final ModelLayerLocation ZOMBIE_VILLAGER = ModelLayers.register("zombie_villager");
    public static final ModelLayerLocation ZOMBIE_VILLAGER_BABY = ModelLayers.register("zombie_villager_baby");
    public static final ModelLayerLocation ZOMBIE_VILLAGER_BABY_INNER_ARMOR = ModelLayers.registerInnerArmor("zombie_villager_baby");
    public static final ModelLayerLocation ZOMBIE_VILLAGER_BABY_OUTER_ARMOR = ModelLayers.registerOuterArmor("zombie_villager_baby");
    public static final ModelLayerLocation ZOMBIE_VILLAGER_INNER_ARMOR = ModelLayers.registerInnerArmor("zombie_villager");
    public static final ModelLayerLocation ZOMBIE_VILLAGER_OUTER_ARMOR = ModelLayers.registerOuterArmor("zombie_villager");
    public static final ModelLayerLocation ZOMBIFIED_PIGLIN = ModelLayers.register("zombified_piglin");
    public static final ModelLayerLocation ZOMBIFIED_PIGLIN_BABY = ModelLayers.register("zombified_piglin_baby");
    public static final ModelLayerLocation ZOMBIFIED_PIGLIN_BABY_INNER_ARMOR = ModelLayers.registerInnerArmor("zombified_piglin_baby");
    public static final ModelLayerLocation ZOMBIFIED_PIGLIN_BABY_OUTER_ARMOR = ModelLayers.registerOuterArmor("zombified_piglin_baby");
    public static final ModelLayerLocation ZOMBIFIED_PIGLIN_INNER_ARMOR = ModelLayers.registerInnerArmor("zombified_piglin");
    public static final ModelLayerLocation ZOMBIFIED_PIGLIN_OUTER_ARMOR = ModelLayers.registerOuterArmor("zombified_piglin");

    private static ModelLayerLocation register(String $$0) {
        return ModelLayers.register($$0, DEFAULT_LAYER);
    }

    private static ModelLayerLocation register(String $$0, String $$1) {
        ModelLayerLocation $$2 = ModelLayers.createLocation($$0, $$1);
        if (!ALL_MODELS.add($$2)) {
            throw new IllegalStateException("Duplicate registration for " + String.valueOf((Object)$$2));
        }
        return $$2;
    }

    private static ModelLayerLocation createLocation(String $$0, String $$1) {
        return new ModelLayerLocation(ResourceLocation.withDefaultNamespace($$0), $$1);
    }

    private static ModelLayerLocation registerInnerArmor(String $$0) {
        return ModelLayers.register($$0, "inner_armor");
    }

    private static ModelLayerLocation registerOuterArmor(String $$0) {
        return ModelLayers.register($$0, "outer_armor");
    }

    public static ModelLayerLocation createStandingSignModelName(WoodType $$0) {
        return ModelLayers.createLocation("sign/standing/" + $$0.name(), DEFAULT_LAYER);
    }

    public static ModelLayerLocation createWallSignModelName(WoodType $$0) {
        return ModelLayers.createLocation("sign/wall/" + $$0.name(), DEFAULT_LAYER);
    }

    public static ModelLayerLocation createHangingSignModelName(WoodType $$0, HangingSignRenderer.AttachmentType $$1) {
        return ModelLayers.createLocation("hanging_sign/" + $$0.name() + "/" + $$1.getSerializedName(), DEFAULT_LAYER);
    }

    public static Stream<ModelLayerLocation> getKnownLocations() {
        return ALL_MODELS.stream();
    }
}

