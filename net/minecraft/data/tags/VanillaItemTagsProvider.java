/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.tags;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BlockItemTagsProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class VanillaItemTagsProvider
extends IntrinsicHolderTagsProvider<Item> {
    public VanillaItemTagsProvider(PackOutput $$02, CompletableFuture<HolderLookup.Provider> $$1) {
        super($$02, Registries.ITEM, $$1, (T $$0) -> $$0.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider $$02) {
        new BlockItemTagsProvider(){

            @Override
            protected TagAppender<Block, Block> tag(TagKey<Block> $$0, TagKey<Item> $$1) {
                return new BlockToItemConverter(VanillaItemTagsProvider.this.tag($$1));
            }
        }.run();
        this.tag(ItemTags.BANNERS).a((Item[])new Item[]{Items.WHITE_BANNER, Items.ORANGE_BANNER, Items.MAGENTA_BANNER, Items.LIGHT_BLUE_BANNER, Items.YELLOW_BANNER, Items.LIME_BANNER, Items.PINK_BANNER, Items.GRAY_BANNER, Items.LIGHT_GRAY_BANNER, Items.CYAN_BANNER, Items.PURPLE_BANNER, Items.BLUE_BANNER, Items.BROWN_BANNER, Items.GREEN_BANNER, Items.RED_BANNER, Items.BLACK_BANNER});
        this.tag(ItemTags.BOATS).a((Item[])new Item[]{Items.OAK_BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT, Items.PALE_OAK_BOAT, Items.MANGROVE_BOAT, Items.BAMBOO_RAFT, Items.CHERRY_BOAT}).addTag(ItemTags.CHEST_BOATS);
        this.tag(ItemTags.BUNDLES).a((Item[])new Item[]{Items.BUNDLE, Items.BLACK_BUNDLE, Items.BLUE_BUNDLE, Items.BROWN_BUNDLE, Items.CYAN_BUNDLE, Items.GRAY_BUNDLE, Items.GREEN_BUNDLE, Items.LIGHT_BLUE_BUNDLE, Items.LIGHT_GRAY_BUNDLE, Items.LIME_BUNDLE, Items.MAGENTA_BUNDLE, Items.ORANGE_BUNDLE, Items.PINK_BUNDLE, Items.PURPLE_BUNDLE, Items.RED_BUNDLE, Items.YELLOW_BUNDLE, Items.WHITE_BUNDLE});
        this.tag(ItemTags.CHEST_BOATS).a((Item[])new Item[]{Items.OAK_CHEST_BOAT, Items.SPRUCE_CHEST_BOAT, Items.BIRCH_CHEST_BOAT, Items.JUNGLE_CHEST_BOAT, Items.ACACIA_CHEST_BOAT, Items.DARK_OAK_CHEST_BOAT, Items.PALE_OAK_CHEST_BOAT, Items.MANGROVE_CHEST_BOAT, Items.BAMBOO_CHEST_RAFT, Items.CHERRY_CHEST_BOAT});
        this.tag(ItemTags.EGGS).a((Item[])new Item[]{Items.EGG, Items.BLUE_EGG, Items.BROWN_EGG});
        this.tag(ItemTags.FISHES).a((Item[])new Item[]{Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH});
        this.tag(ItemTags.CREEPER_DROP_MUSIC_DISCS).a((Item[])new Item[]{Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT});
        this.tag(ItemTags.COALS).a((Item[])new Item[]{Items.COAL, Items.CHARCOAL});
        this.tag(ItemTags.ARROWS).a((Item[])new Item[]{Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW});
        this.tag(ItemTags.LECTERN_BOOKS).a((Item[])new Item[]{Items.WRITTEN_BOOK, Items.WRITABLE_BOOK});
        this.tag(ItemTags.BEACON_PAYMENT_ITEMS).a((Item[])new Item[]{Items.NETHERITE_INGOT, Items.EMERALD, Items.DIAMOND, Items.GOLD_INGOT, Items.IRON_INGOT});
        this.tag(ItemTags.PIGLIN_REPELLENTS).add(Items.SOUL_TORCH).add(Items.SOUL_LANTERN).add(Items.SOUL_CAMPFIRE);
        this.tag(ItemTags.PIGLIN_LOVED).addTag(ItemTags.GOLD_ORES).a((Item[])new Item[]{Items.GOLD_BLOCK, Items.GILDED_BLACKSTONE, Items.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT, Items.BELL, Items.CLOCK, Items.GOLDEN_CARROT, Items.GLISTERING_MELON_SLICE, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR, Items.GOLDEN_SWORD, Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.RAW_GOLD, Items.RAW_GOLD_BLOCK});
        this.tag(ItemTags.IGNORED_BY_PIGLIN_BABIES).add(Items.LEATHER);
        this.tag(ItemTags.PIGLIN_FOOD).a((Item[])new Item[]{Items.PORKCHOP, Items.COOKED_PORKCHOP});
        this.tag(ItemTags.PIGLIN_SAFE_ARMOR).a((Item[])new Item[]{Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS});
        this.tag(ItemTags.FOX_FOOD).a((Item[])new Item[]{Items.SWEET_BERRIES, Items.GLOW_BERRIES});
        this.tag(ItemTags.DUPLICATES_ALLAYS).add(Items.AMETHYST_SHARD);
        this.tag(ItemTags.BREWING_FUEL).add(Items.BLAZE_POWDER);
        this.tag(ItemTags.NON_FLAMMABLE_WOOD).a((Item[])new Item[]{Items.WARPED_STEM, Items.STRIPPED_WARPED_STEM, Items.WARPED_HYPHAE, Items.STRIPPED_WARPED_HYPHAE, Items.CRIMSON_STEM, Items.STRIPPED_CRIMSON_STEM, Items.CRIMSON_HYPHAE, Items.STRIPPED_CRIMSON_HYPHAE, Items.CRIMSON_PLANKS, Items.WARPED_PLANKS, Items.CRIMSON_SLAB, Items.WARPED_SLAB, Items.CRIMSON_PRESSURE_PLATE, Items.WARPED_PRESSURE_PLATE, Items.CRIMSON_FENCE, Items.WARPED_FENCE, Items.CRIMSON_TRAPDOOR, Items.WARPED_TRAPDOOR, Items.CRIMSON_FENCE_GATE, Items.WARPED_FENCE_GATE, Items.CRIMSON_STAIRS, Items.WARPED_STAIRS, Items.CRIMSON_BUTTON, Items.WARPED_BUTTON, Items.CRIMSON_DOOR, Items.WARPED_DOOR, Items.CRIMSON_SIGN, Items.WARPED_SIGN, Items.WARPED_HANGING_SIGN, Items.CRIMSON_HANGING_SIGN});
        this.tag(ItemTags.WOODEN_TOOL_MATERIALS).addTag(ItemTags.PLANKS);
        this.tag(ItemTags.STONE_TOOL_MATERIALS).a((Item[])new Item[]{Items.COBBLESTONE, Items.BLACKSTONE, Items.COBBLED_DEEPSLATE});
        this.tag(ItemTags.IRON_TOOL_MATERIALS).add(Items.IRON_INGOT);
        this.tag(ItemTags.GOLD_TOOL_MATERIALS).add(Items.GOLD_INGOT);
        this.tag(ItemTags.DIAMOND_TOOL_MATERIALS).add(Items.DIAMOND);
        this.tag(ItemTags.NETHERITE_TOOL_MATERIALS).add(Items.NETHERITE_INGOT);
        this.tag(ItemTags.REPAIRS_LEATHER_ARMOR).add(Items.LEATHER);
        this.tag(ItemTags.REPAIRS_CHAIN_ARMOR).add(Items.IRON_INGOT);
        this.tag(ItemTags.REPAIRS_IRON_ARMOR).add(Items.IRON_INGOT);
        this.tag(ItemTags.REPAIRS_GOLD_ARMOR).add(Items.GOLD_INGOT);
        this.tag(ItemTags.REPAIRS_DIAMOND_ARMOR).add(Items.DIAMOND);
        this.tag(ItemTags.REPAIRS_NETHERITE_ARMOR).add(Items.NETHERITE_INGOT);
        this.tag(ItemTags.REPAIRS_TURTLE_HELMET).add(Items.TURTLE_SCUTE);
        this.tag(ItemTags.REPAIRS_WOLF_ARMOR).add(Items.ARMADILLO_SCUTE);
        this.tag(ItemTags.STONE_CRAFTING_MATERIALS).a((Item[])new Item[]{Items.COBBLESTONE, Items.BLACKSTONE, Items.COBBLED_DEEPSLATE});
        this.tag(ItemTags.FREEZE_IMMUNE_WEARABLES).a((Item[])new Item[]{Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_HORSE_ARMOR});
        this.tag(ItemTags.AXOLOTL_FOOD).add(Items.TROPICAL_FISH_BUCKET);
        this.tag(ItemTags.CLUSTER_MAX_HARVESTABLES).a((Item[])new Item[]{Items.DIAMOND_PICKAXE, Items.GOLDEN_PICKAXE, Items.IRON_PICKAXE, Items.NETHERITE_PICKAXE, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE});
        this.tag(ItemTags.COMPASSES).add(Items.COMPASS).add(Items.RECOVERY_COMPASS);
        this.tag(ItemTags.CREEPER_IGNITERS).add(Items.FLINT_AND_STEEL).add(Items.FIRE_CHARGE);
        this.tag(ItemTags.SWORDS).add(Items.DIAMOND_SWORD).add(Items.STONE_SWORD).add(Items.GOLDEN_SWORD).add(Items.NETHERITE_SWORD).add(Items.WOODEN_SWORD).add(Items.IRON_SWORD);
        this.tag(ItemTags.AXES).add(Items.DIAMOND_AXE).add(Items.STONE_AXE).add(Items.GOLDEN_AXE).add(Items.NETHERITE_AXE).add(Items.WOODEN_AXE).add(Items.IRON_AXE);
        this.tag(ItemTags.PICKAXES).add(Items.DIAMOND_PICKAXE).add(Items.STONE_PICKAXE).add(Items.GOLDEN_PICKAXE).add(Items.NETHERITE_PICKAXE).add(Items.WOODEN_PICKAXE).add(Items.IRON_PICKAXE);
        this.tag(ItemTags.SHOVELS).add(Items.DIAMOND_SHOVEL).add(Items.STONE_SHOVEL).add(Items.GOLDEN_SHOVEL).add(Items.NETHERITE_SHOVEL).add(Items.WOODEN_SHOVEL).add(Items.IRON_SHOVEL);
        this.tag(ItemTags.HOES).add(Items.DIAMOND_HOE).add(Items.STONE_HOE).add(Items.GOLDEN_HOE).add(Items.NETHERITE_HOE).add(Items.WOODEN_HOE).add(Items.IRON_HOE);
        this.tag(ItemTags.BREAKS_DECORATED_POTS).addTag(ItemTags.SWORDS).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES).add(Items.TRIDENT).add(Items.MACE);
        this.tag(ItemTags.SKELETON_PREFERRED_WEAPONS).add(Items.BOW);
        this.tag(ItemTags.DROWNED_PREFERRED_WEAPONS).add(Items.TRIDENT);
        this.tag(ItemTags.PIGLIN_PREFERRED_WEAPONS).add(Items.CROSSBOW);
        this.tag(ItemTags.PILLAGER_PREFERRED_WEAPONS).add(Items.CROSSBOW);
        this.tag(ItemTags.WITHER_SKELETON_DISLIKED_WEAPONS).add(Items.BOW).add(Items.CROSSBOW);
        this.tag(ItemTags.DECORATED_POT_SHERDS).a((Item[])new Item[]{Items.ANGLER_POTTERY_SHERD, Items.ARCHER_POTTERY_SHERD, Items.ARMS_UP_POTTERY_SHERD, Items.BLADE_POTTERY_SHERD, Items.BREWER_POTTERY_SHERD, Items.BURN_POTTERY_SHERD, Items.DANGER_POTTERY_SHERD, Items.EXPLORER_POTTERY_SHERD, Items.FRIEND_POTTERY_SHERD, Items.HEART_POTTERY_SHERD, Items.HEARTBREAK_POTTERY_SHERD, Items.HOWL_POTTERY_SHERD, Items.MINER_POTTERY_SHERD, Items.MOURNER_POTTERY_SHERD, Items.PLENTY_POTTERY_SHERD, Items.PRIZE_POTTERY_SHERD, Items.SHEAF_POTTERY_SHERD, Items.SHELTER_POTTERY_SHERD, Items.SKULL_POTTERY_SHERD, Items.SNORT_POTTERY_SHERD, Items.FLOW_POTTERY_SHERD, Items.GUSTER_POTTERY_SHERD, Items.SCRAPE_POTTERY_SHERD});
        this.tag(ItemTags.DECORATED_POT_INGREDIENTS).add(Items.BRICK).addTag(ItemTags.DECORATED_POT_SHERDS);
        this.tag(ItemTags.FOOT_ARMOR).a((Item[])new Item[]{Items.LEATHER_BOOTS, Items.CHAINMAIL_BOOTS, Items.GOLDEN_BOOTS, Items.IRON_BOOTS, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS});
        this.tag(ItemTags.LEG_ARMOR).a((Item[])new Item[]{Items.LEATHER_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS});
        this.tag(ItemTags.CHEST_ARMOR).a((Item[])new Item[]{Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE});
        this.tag(ItemTags.HEAD_ARMOR).a((Item[])new Item[]{Items.LEATHER_HELMET, Items.CHAINMAIL_HELMET, Items.GOLDEN_HELMET, Items.IRON_HELMET, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET, Items.TURTLE_HELMET});
        this.tag(ItemTags.SKULLS).a((Item[])new Item[]{Items.PLAYER_HEAD, Items.CREEPER_HEAD, Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.DRAGON_HEAD, Items.PIGLIN_HEAD});
        this.tag(ItemTags.TRIMMABLE_ARMOR).addTag(ItemTags.FOOT_ARMOR).addTag(ItemTags.LEG_ARMOR).addTag(ItemTags.CHEST_ARMOR).addTag(ItemTags.HEAD_ARMOR);
        this.tag(ItemTags.TRIM_MATERIALS).addAll($$02.lookupOrThrow(Registries.ITEM).listElements().filter($$0 -> ((Item)$$0.value()).components().has(DataComponents.PROVIDES_TRIM_MATERIAL)).sorted(Comparator.comparing($$0 -> $$0.key().location())).map(Holder.Reference::value));
        this.tag(ItemTags.BOOKSHELF_BOOKS).a((Item[])new Item[]{Items.BOOK, Items.WRITTEN_BOOK, Items.ENCHANTED_BOOK, Items.WRITABLE_BOOK, Items.KNOWLEDGE_BOOK});
        this.tag(ItemTags.NOTE_BLOCK_TOP_INSTRUMENTS).a((Item[])new Item[]{Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, Items.CREEPER_HEAD, Items.DRAGON_HEAD, Items.WITHER_SKELETON_SKULL, Items.PIGLIN_HEAD, Items.PLAYER_HEAD});
        this.tag(ItemTags.SNIFFER_FOOD).add(Items.TORCHFLOWER_SEEDS);
        this.tag(ItemTags.VILLAGER_PLANTABLE_SEEDS).a((Item[])new Item[]{Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD});
        this.tag(ItemTags.VILLAGER_PICKS_UP).addTag(ItemTags.VILLAGER_PLANTABLE_SEEDS).a((Item[])new Item[]{Items.BREAD, Items.WHEAT, Items.BEETROOT});
        this.tag(ItemTags.BOOK_CLONING_TARGET).add(Items.WRITABLE_BOOK);
        this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).addTag(ItemTags.FOOT_ARMOR);
        this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE).addTag(ItemTags.LEG_ARMOR);
        this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).addTag(ItemTags.CHEST_ARMOR);
        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).addTag(ItemTags.HEAD_ARMOR);
        this.tag(ItemTags.ARMOR_ENCHANTABLE).addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE).addTag(ItemTags.LEG_ARMOR_ENCHANTABLE).addTag(ItemTags.CHEST_ARMOR_ENCHANTABLE).addTag(ItemTags.HEAD_ARMOR_ENCHANTABLE);
        this.tag(ItemTags.SWORD_ENCHANTABLE).addTag(ItemTags.SWORDS);
        this.tag(ItemTags.FIRE_ASPECT_ENCHANTABLE).addTag(ItemTags.SWORD_ENCHANTABLE).add(Items.MACE);
        this.tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).addTag(ItemTags.SWORDS).addTag(ItemTags.AXES);
        this.tag(ItemTags.WEAPON_ENCHANTABLE).addTag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(Items.MACE);
        this.tag(ItemTags.MACE_ENCHANTABLE).add(Items.MACE);
        this.tag(ItemTags.MINING_ENCHANTABLE).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES).add(Items.SHEARS);
        this.tag(ItemTags.MINING_LOOT_ENCHANTABLE).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES);
        this.tag(ItemTags.FISHING_ENCHANTABLE).add(Items.FISHING_ROD);
        this.tag(ItemTags.TRIDENT_ENCHANTABLE).add(Items.TRIDENT);
        this.tag(ItemTags.DURABILITY_ENCHANTABLE).addTag(ItemTags.FOOT_ARMOR).addTag(ItemTags.LEG_ARMOR).addTag(ItemTags.CHEST_ARMOR).addTag(ItemTags.HEAD_ARMOR).add(Items.ELYTRA).add(Items.SHIELD).addTag(ItemTags.SWORDS).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES).add(Items.BOW).add(Items.CROSSBOW).add(Items.TRIDENT).add(Items.FLINT_AND_STEEL).add(Items.SHEARS).add(Items.BRUSH).add(Items.FISHING_ROD).a((Item[])new Item[]{Items.CARROT_ON_A_STICK, Items.WARPED_FUNGUS_ON_A_STICK}).add(Items.MACE);
        this.tag(ItemTags.BOW_ENCHANTABLE).add(Items.BOW);
        this.tag(ItemTags.EQUIPPABLE_ENCHANTABLE).addTag(ItemTags.FOOT_ARMOR).addTag(ItemTags.LEG_ARMOR).addTag(ItemTags.CHEST_ARMOR).addTag(ItemTags.HEAD_ARMOR).add(Items.ELYTRA).addTag(ItemTags.SKULLS).add(Items.CARVED_PUMPKIN);
        this.tag(ItemTags.CROSSBOW_ENCHANTABLE).add(Items.CROSSBOW);
        this.tag(ItemTags.VANISHING_ENCHANTABLE).addTag(ItemTags.DURABILITY_ENCHANTABLE).add(Items.COMPASS).add(Items.CARVED_PUMPKIN).addTag(ItemTags.SKULLS);
        this.tag(ItemTags.DYEABLE).a((Item[])new Item[]{Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR, Items.WOLF_ARMOR});
        this.tag(ItemTags.FURNACE_MINECART_FUEL).a((Item[])new Item[]{Items.COAL, Items.CHARCOAL});
        this.tag(ItemTags.MEAT).a((Item[])new Item[]{Items.BEEF, Items.CHICKEN, Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_MUTTON, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT, Items.MUTTON, Items.PORKCHOP, Items.RABBIT, Items.ROTTEN_FLESH});
        this.tag(ItemTags.WOLF_FOOD).addTag(ItemTags.MEAT).a((Item[])new Item[]{Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.RABBIT_STEW});
        this.tag(ItemTags.OCELOT_FOOD).a((Item[])new Item[]{Items.COD, Items.SALMON});
        this.tag(ItemTags.CAT_FOOD).a((Item[])new Item[]{Items.COD, Items.SALMON});
        this.tag(ItemTags.HORSE_FOOD).a((Item[])new Item[]{Items.WHEAT, Items.SUGAR, Items.HAY_BLOCK, Items.APPLE, Items.CARROT, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE});
        this.tag(ItemTags.HORSE_TEMPT_ITEMS).a((Item[])new Item[]{Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE});
        this.tag(ItemTags.HARNESSES).a((Item[])new Item[]{Items.WHITE_HARNESS, Items.ORANGE_HARNESS, Items.MAGENTA_HARNESS, Items.LIGHT_BLUE_HARNESS, Items.YELLOW_HARNESS, Items.LIME_HARNESS, Items.PINK_HARNESS, Items.GRAY_HARNESS, Items.LIGHT_GRAY_HARNESS, Items.CYAN_HARNESS, Items.PURPLE_HARNESS, Items.BLUE_HARNESS, Items.BROWN_HARNESS, Items.GREEN_HARNESS, Items.RED_HARNESS, Items.BLACK_HARNESS});
        this.tag(ItemTags.HAPPY_GHAST_FOOD).add(Items.SNOWBALL);
        this.tag(ItemTags.HAPPY_GHAST_TEMPT_ITEMS).addTag(ItemTags.HAPPY_GHAST_FOOD).addTag(ItemTags.HARNESSES);
        this.tag(ItemTags.CAMEL_FOOD).add(Items.CACTUS);
        this.tag(ItemTags.ARMADILLO_FOOD).add(Items.SPIDER_EYE);
        this.tag(ItemTags.CHICKEN_FOOD).a((Item[])new Item[]{Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD});
        this.tag(ItemTags.FROG_FOOD).add(Items.SLIME_BALL);
        this.tag(ItemTags.HOGLIN_FOOD).add(Items.CRIMSON_FUNGUS);
        this.tag(ItemTags.LLAMA_FOOD).a((Item[])new Item[]{Items.WHEAT, Items.HAY_BLOCK});
        this.tag(ItemTags.LLAMA_TEMPT_ITEMS).add(Items.HAY_BLOCK);
        this.tag(ItemTags.PANDA_FOOD).add(Items.BAMBOO);
        this.tag(ItemTags.PANDA_EATS_FROM_GROUND).addTag(ItemTags.PANDA_FOOD).add(Items.CAKE);
        this.tag(ItemTags.PIG_FOOD).a((Item[])new Item[]{Items.CARROT, Items.POTATO, Items.BEETROOT});
        this.tag(ItemTags.RABBIT_FOOD).a((Item[])new Item[]{Items.CARROT, Items.GOLDEN_CARROT, Items.DANDELION});
        this.tag(ItemTags.STRIDER_FOOD).add(Items.WARPED_FUNGUS);
        this.tag(ItemTags.STRIDER_TEMPT_ITEMS).addTag(ItemTags.STRIDER_FOOD).add(Items.WARPED_FUNGUS_ON_A_STICK);
        this.tag(ItemTags.TURTLE_FOOD).add(Items.SEAGRASS);
        this.tag(ItemTags.PARROT_FOOD).a((Item[])new Item[]{Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD});
        this.tag(ItemTags.PARROT_POISONOUS_FOOD).add(Items.COOKIE);
        this.tag(ItemTags.COW_FOOD).add(Items.WHEAT);
        this.tag(ItemTags.SHEEP_FOOD).add(Items.WHEAT);
        this.tag(ItemTags.GOAT_FOOD).add(Items.WHEAT);
        this.tag(ItemTags.MAP_INVISIBILITY_EQUIPMENT).add(Items.CARVED_PUMPKIN);
        this.tag(ItemTags.GAZE_DISGUISE_EQUIPMENT).add(Items.CARVED_PUMPKIN);
    }

    static class BlockToItemConverter
    implements TagAppender<Block, Block> {
        private final TagAppender<Item, Item> itemAppender;

        public BlockToItemConverter(TagAppender<Item, Item> $$0) {
            this.itemAppender = $$0;
        }

        @Override
        public TagAppender<Block, Block> add(Block $$0) {
            this.itemAppender.add(Objects.requireNonNull($$0.asItem()));
            return this;
        }

        @Override
        public TagAppender<Block, Block> addOptional(Block $$0) {
            this.itemAppender.addOptional(Objects.requireNonNull($$0.asItem()));
            return this;
        }

        private static TagKey<Item> blockTagToItemTag(TagKey<Block> $$0) {
            return TagKey.create(Registries.ITEM, $$0.location());
        }

        @Override
        public TagAppender<Block, Block> addTag(TagKey<Block> $$0) {
            this.itemAppender.addTag(BlockToItemConverter.blockTagToItemTag($$0));
            return this;
        }

        @Override
        public TagAppender<Block, Block> addOptionalTag(TagKey<Block> $$0) {
            this.itemAppender.addOptionalTag(BlockToItemConverter.blockTagToItemTag($$0));
            return this;
        }
    }
}

