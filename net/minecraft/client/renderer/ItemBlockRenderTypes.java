/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class ItemBlockRenderTypes {
    private static final Map<Block, ChunkSectionLayer> TYPE_BY_BLOCK = Util.make(Maps.newHashMap(), $$0 -> {
        ChunkSectionLayer $$1 = ChunkSectionLayer.TRIPWIRE;
        $$0.put(Blocks.TRIPWIRE, $$1);
        ChunkSectionLayer $$2 = ChunkSectionLayer.CUTOUT_MIPPED;
        $$0.put(Blocks.GRASS_BLOCK, $$2);
        $$0.put(Blocks.IRON_BARS, $$2);
        $$0.put(Blocks.GLASS_PANE, $$2);
        $$0.put(Blocks.TRIPWIRE_HOOK, $$2);
        $$0.put(Blocks.HOPPER, $$2);
        $$0.put(Blocks.CHAIN, $$2);
        $$0.put(Blocks.JUNGLE_LEAVES, $$2);
        $$0.put(Blocks.OAK_LEAVES, $$2);
        $$0.put(Blocks.SPRUCE_LEAVES, $$2);
        $$0.put(Blocks.ACACIA_LEAVES, $$2);
        $$0.put(Blocks.CHERRY_LEAVES, $$2);
        $$0.put(Blocks.BIRCH_LEAVES, $$2);
        $$0.put(Blocks.DARK_OAK_LEAVES, $$2);
        $$0.put(Blocks.PALE_OAK_LEAVES, $$2);
        $$0.put(Blocks.AZALEA_LEAVES, $$2);
        $$0.put(Blocks.FLOWERING_AZALEA_LEAVES, $$2);
        $$0.put(Blocks.MANGROVE_ROOTS, $$2);
        $$0.put(Blocks.MANGROVE_LEAVES, $$2);
        ChunkSectionLayer $$3 = ChunkSectionLayer.CUTOUT;
        $$0.put(Blocks.OAK_SAPLING, $$3);
        $$0.put(Blocks.SPRUCE_SAPLING, $$3);
        $$0.put(Blocks.BIRCH_SAPLING, $$3);
        $$0.put(Blocks.JUNGLE_SAPLING, $$3);
        $$0.put(Blocks.ACACIA_SAPLING, $$3);
        $$0.put(Blocks.CHERRY_SAPLING, $$3);
        $$0.put(Blocks.DARK_OAK_SAPLING, $$3);
        $$0.put(Blocks.PALE_OAK_SAPLING, $$3);
        $$0.put(Blocks.GLASS, $$3);
        $$0.put(Blocks.WHITE_BED, $$3);
        $$0.put(Blocks.ORANGE_BED, $$3);
        $$0.put(Blocks.MAGENTA_BED, $$3);
        $$0.put(Blocks.LIGHT_BLUE_BED, $$3);
        $$0.put(Blocks.YELLOW_BED, $$3);
        $$0.put(Blocks.LIME_BED, $$3);
        $$0.put(Blocks.PINK_BED, $$3);
        $$0.put(Blocks.GRAY_BED, $$3);
        $$0.put(Blocks.LIGHT_GRAY_BED, $$3);
        $$0.put(Blocks.CYAN_BED, $$3);
        $$0.put(Blocks.PURPLE_BED, $$3);
        $$0.put(Blocks.BLUE_BED, $$3);
        $$0.put(Blocks.BROWN_BED, $$3);
        $$0.put(Blocks.GREEN_BED, $$3);
        $$0.put(Blocks.RED_BED, $$3);
        $$0.put(Blocks.BLACK_BED, $$3);
        $$0.put(Blocks.POWERED_RAIL, $$3);
        $$0.put(Blocks.DETECTOR_RAIL, $$3);
        $$0.put(Blocks.COBWEB, $$3);
        $$0.put(Blocks.SHORT_GRASS, $$3);
        $$0.put(Blocks.FERN, $$3);
        $$0.put(Blocks.BUSH, $$3);
        $$0.put(Blocks.DEAD_BUSH, $$3);
        $$0.put(Blocks.SHORT_DRY_GRASS, $$3);
        $$0.put(Blocks.TALL_DRY_GRASS, $$3);
        $$0.put(Blocks.SEAGRASS, $$3);
        $$0.put(Blocks.TALL_SEAGRASS, $$3);
        $$0.put(Blocks.DANDELION, $$3);
        $$0.put(Blocks.OPEN_EYEBLOSSOM, $$3);
        $$0.put(Blocks.CLOSED_EYEBLOSSOM, $$3);
        $$0.put(Blocks.POPPY, $$3);
        $$0.put(Blocks.BLUE_ORCHID, $$3);
        $$0.put(Blocks.ALLIUM, $$3);
        $$0.put(Blocks.AZURE_BLUET, $$3);
        $$0.put(Blocks.RED_TULIP, $$3);
        $$0.put(Blocks.ORANGE_TULIP, $$3);
        $$0.put(Blocks.WHITE_TULIP, $$3);
        $$0.put(Blocks.PINK_TULIP, $$3);
        $$0.put(Blocks.OXEYE_DAISY, $$3);
        $$0.put(Blocks.CORNFLOWER, $$3);
        $$0.put(Blocks.WITHER_ROSE, $$3);
        $$0.put(Blocks.LILY_OF_THE_VALLEY, $$3);
        $$0.put(Blocks.BROWN_MUSHROOM, $$3);
        $$0.put(Blocks.RED_MUSHROOM, $$3);
        $$0.put(Blocks.TORCH, $$3);
        $$0.put(Blocks.WALL_TORCH, $$3);
        $$0.put(Blocks.SOUL_TORCH, $$3);
        $$0.put(Blocks.SOUL_WALL_TORCH, $$3);
        $$0.put(Blocks.FIRE, $$3);
        $$0.put(Blocks.SOUL_FIRE, $$3);
        $$0.put(Blocks.SPAWNER, $$3);
        $$0.put(Blocks.TRIAL_SPAWNER, $$3);
        $$0.put(Blocks.VAULT, $$3);
        $$0.put(Blocks.REDSTONE_WIRE, $$3);
        $$0.put(Blocks.WHEAT, $$3);
        $$0.put(Blocks.OAK_DOOR, $$3);
        $$0.put(Blocks.LADDER, $$3);
        $$0.put(Blocks.RAIL, $$3);
        $$0.put(Blocks.IRON_DOOR, $$3);
        $$0.put(Blocks.REDSTONE_TORCH, $$3);
        $$0.put(Blocks.REDSTONE_WALL_TORCH, $$3);
        $$0.put(Blocks.CACTUS, $$3);
        $$0.put(Blocks.SUGAR_CANE, $$3);
        $$0.put(Blocks.REPEATER, $$3);
        $$0.put(Blocks.OAK_TRAPDOOR, $$3);
        $$0.put(Blocks.SPRUCE_TRAPDOOR, $$3);
        $$0.put(Blocks.BIRCH_TRAPDOOR, $$3);
        $$0.put(Blocks.JUNGLE_TRAPDOOR, $$3);
        $$0.put(Blocks.ACACIA_TRAPDOOR, $$3);
        $$0.put(Blocks.CHERRY_TRAPDOOR, $$3);
        $$0.put(Blocks.DARK_OAK_TRAPDOOR, $$3);
        $$0.put(Blocks.PALE_OAK_TRAPDOOR, $$3);
        $$0.put(Blocks.CRIMSON_TRAPDOOR, $$3);
        $$0.put(Blocks.WARPED_TRAPDOOR, $$3);
        $$0.put(Blocks.MANGROVE_TRAPDOOR, $$3);
        $$0.put(Blocks.BAMBOO_TRAPDOOR, $$3);
        $$0.put(Blocks.COPPER_TRAPDOOR, $$3);
        $$0.put(Blocks.EXPOSED_COPPER_TRAPDOOR, $$3);
        $$0.put(Blocks.WEATHERED_COPPER_TRAPDOOR, $$3);
        $$0.put(Blocks.OXIDIZED_COPPER_TRAPDOOR, $$3);
        $$0.put(Blocks.WAXED_COPPER_TRAPDOOR, $$3);
        $$0.put(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, $$3);
        $$0.put(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, $$3);
        $$0.put(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR, $$3);
        $$0.put(Blocks.ATTACHED_PUMPKIN_STEM, $$3);
        $$0.put(Blocks.ATTACHED_MELON_STEM, $$3);
        $$0.put(Blocks.PUMPKIN_STEM, $$3);
        $$0.put(Blocks.MELON_STEM, $$3);
        $$0.put(Blocks.VINE, $$3);
        $$0.put(Blocks.PALE_MOSS_CARPET, $$3);
        $$0.put(Blocks.PALE_HANGING_MOSS, $$3);
        $$0.put(Blocks.GLOW_LICHEN, $$3);
        $$0.put(Blocks.RESIN_CLUMP, $$3);
        $$0.put(Blocks.LILY_PAD, $$3);
        $$0.put(Blocks.NETHER_WART, $$3);
        $$0.put(Blocks.BREWING_STAND, $$3);
        $$0.put(Blocks.COCOA, $$3);
        $$0.put(Blocks.BEACON, $$3);
        $$0.put(Blocks.FLOWER_POT, $$3);
        $$0.put(Blocks.POTTED_OAK_SAPLING, $$3);
        $$0.put(Blocks.POTTED_SPRUCE_SAPLING, $$3);
        $$0.put(Blocks.POTTED_BIRCH_SAPLING, $$3);
        $$0.put(Blocks.POTTED_JUNGLE_SAPLING, $$3);
        $$0.put(Blocks.POTTED_ACACIA_SAPLING, $$3);
        $$0.put(Blocks.POTTED_CHERRY_SAPLING, $$3);
        $$0.put(Blocks.POTTED_DARK_OAK_SAPLING, $$3);
        $$0.put(Blocks.POTTED_PALE_OAK_SAPLING, $$3);
        $$0.put(Blocks.POTTED_MANGROVE_PROPAGULE, $$3);
        $$0.put(Blocks.POTTED_FERN, $$3);
        $$0.put(Blocks.POTTED_DANDELION, $$3);
        $$0.put(Blocks.POTTED_POPPY, $$3);
        $$0.put(Blocks.POTTED_OPEN_EYEBLOSSOM, $$3);
        $$0.put(Blocks.POTTED_CLOSED_EYEBLOSSOM, $$3);
        $$0.put(Blocks.POTTED_BLUE_ORCHID, $$3);
        $$0.put(Blocks.POTTED_ALLIUM, $$3);
        $$0.put(Blocks.POTTED_AZURE_BLUET, $$3);
        $$0.put(Blocks.POTTED_RED_TULIP, $$3);
        $$0.put(Blocks.POTTED_ORANGE_TULIP, $$3);
        $$0.put(Blocks.POTTED_WHITE_TULIP, $$3);
        $$0.put(Blocks.POTTED_PINK_TULIP, $$3);
        $$0.put(Blocks.POTTED_OXEYE_DAISY, $$3);
        $$0.put(Blocks.POTTED_CORNFLOWER, $$3);
        $$0.put(Blocks.POTTED_LILY_OF_THE_VALLEY, $$3);
        $$0.put(Blocks.POTTED_WITHER_ROSE, $$3);
        $$0.put(Blocks.POTTED_RED_MUSHROOM, $$3);
        $$0.put(Blocks.POTTED_BROWN_MUSHROOM, $$3);
        $$0.put(Blocks.POTTED_DEAD_BUSH, $$3);
        $$0.put(Blocks.POTTED_CACTUS, $$3);
        $$0.put(Blocks.POTTED_AZALEA, $$3);
        $$0.put(Blocks.POTTED_FLOWERING_AZALEA, $$3);
        $$0.put(Blocks.POTTED_TORCHFLOWER, $$3);
        $$0.put(Blocks.CARROTS, $$3);
        $$0.put(Blocks.POTATOES, $$3);
        $$0.put(Blocks.COMPARATOR, $$3);
        $$0.put(Blocks.ACTIVATOR_RAIL, $$3);
        $$0.put(Blocks.IRON_TRAPDOOR, $$3);
        $$0.put(Blocks.SUNFLOWER, $$3);
        $$0.put(Blocks.LILAC, $$3);
        $$0.put(Blocks.ROSE_BUSH, $$3);
        $$0.put(Blocks.PEONY, $$3);
        $$0.put(Blocks.TALL_GRASS, $$3);
        $$0.put(Blocks.LARGE_FERN, $$3);
        $$0.put(Blocks.SPRUCE_DOOR, $$3);
        $$0.put(Blocks.BIRCH_DOOR, $$3);
        $$0.put(Blocks.JUNGLE_DOOR, $$3);
        $$0.put(Blocks.ACACIA_DOOR, $$3);
        $$0.put(Blocks.CHERRY_DOOR, $$3);
        $$0.put(Blocks.DARK_OAK_DOOR, $$3);
        $$0.put(Blocks.PALE_OAK_DOOR, $$3);
        $$0.put(Blocks.MANGROVE_DOOR, $$3);
        $$0.put(Blocks.BAMBOO_DOOR, $$3);
        $$0.put(Blocks.COPPER_DOOR, $$3);
        $$0.put(Blocks.EXPOSED_COPPER_DOOR, $$3);
        $$0.put(Blocks.WEATHERED_COPPER_DOOR, $$3);
        $$0.put(Blocks.OXIDIZED_COPPER_DOOR, $$3);
        $$0.put(Blocks.WAXED_COPPER_DOOR, $$3);
        $$0.put(Blocks.WAXED_EXPOSED_COPPER_DOOR, $$3);
        $$0.put(Blocks.WAXED_WEATHERED_COPPER_DOOR, $$3);
        $$0.put(Blocks.WAXED_OXIDIZED_COPPER_DOOR, $$3);
        $$0.put(Blocks.END_ROD, $$3);
        $$0.put(Blocks.CHORUS_PLANT, $$3);
        $$0.put(Blocks.CHORUS_FLOWER, $$3);
        $$0.put(Blocks.TORCHFLOWER, $$3);
        $$0.put(Blocks.TORCHFLOWER_CROP, $$3);
        $$0.put(Blocks.PITCHER_PLANT, $$3);
        $$0.put(Blocks.PITCHER_CROP, $$3);
        $$0.put(Blocks.BEETROOTS, $$3);
        $$0.put(Blocks.KELP, $$3);
        $$0.put(Blocks.KELP_PLANT, $$3);
        $$0.put(Blocks.TURTLE_EGG, $$3);
        $$0.put(Blocks.DEAD_TUBE_CORAL, $$3);
        $$0.put(Blocks.DEAD_BRAIN_CORAL, $$3);
        $$0.put(Blocks.DEAD_BUBBLE_CORAL, $$3);
        $$0.put(Blocks.DEAD_FIRE_CORAL, $$3);
        $$0.put(Blocks.DEAD_HORN_CORAL, $$3);
        $$0.put(Blocks.TUBE_CORAL, $$3);
        $$0.put(Blocks.BRAIN_CORAL, $$3);
        $$0.put(Blocks.BUBBLE_CORAL, $$3);
        $$0.put(Blocks.FIRE_CORAL, $$3);
        $$0.put(Blocks.HORN_CORAL, $$3);
        $$0.put(Blocks.DEAD_TUBE_CORAL_FAN, $$3);
        $$0.put(Blocks.DEAD_BRAIN_CORAL_FAN, $$3);
        $$0.put(Blocks.DEAD_BUBBLE_CORAL_FAN, $$3);
        $$0.put(Blocks.DEAD_FIRE_CORAL_FAN, $$3);
        $$0.put(Blocks.DEAD_HORN_CORAL_FAN, $$3);
        $$0.put(Blocks.TUBE_CORAL_FAN, $$3);
        $$0.put(Blocks.BRAIN_CORAL_FAN, $$3);
        $$0.put(Blocks.BUBBLE_CORAL_FAN, $$3);
        $$0.put(Blocks.FIRE_CORAL_FAN, $$3);
        $$0.put(Blocks.HORN_CORAL_FAN, $$3);
        $$0.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, $$3);
        $$0.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, $$3);
        $$0.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, $$3);
        $$0.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, $$3);
        $$0.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, $$3);
        $$0.put(Blocks.TUBE_CORAL_WALL_FAN, $$3);
        $$0.put(Blocks.BRAIN_CORAL_WALL_FAN, $$3);
        $$0.put(Blocks.BUBBLE_CORAL_WALL_FAN, $$3);
        $$0.put(Blocks.FIRE_CORAL_WALL_FAN, $$3);
        $$0.put(Blocks.HORN_CORAL_WALL_FAN, $$3);
        $$0.put(Blocks.SEA_PICKLE, $$3);
        $$0.put(Blocks.CONDUIT, $$3);
        $$0.put(Blocks.BAMBOO_SAPLING, $$3);
        $$0.put(Blocks.BAMBOO, $$3);
        $$0.put(Blocks.POTTED_BAMBOO, $$3);
        $$0.put(Blocks.SCAFFOLDING, $$3);
        $$0.put(Blocks.STONECUTTER, $$3);
        $$0.put(Blocks.LANTERN, $$3);
        $$0.put(Blocks.SOUL_LANTERN, $$3);
        $$0.put(Blocks.CAMPFIRE, $$3);
        $$0.put(Blocks.SOUL_CAMPFIRE, $$3);
        $$0.put(Blocks.SWEET_BERRY_BUSH, $$3);
        $$0.put(Blocks.WEEPING_VINES, $$3);
        $$0.put(Blocks.WEEPING_VINES_PLANT, $$3);
        $$0.put(Blocks.TWISTING_VINES, $$3);
        $$0.put(Blocks.TWISTING_VINES_PLANT, $$3);
        $$0.put(Blocks.NETHER_SPROUTS, $$3);
        $$0.put(Blocks.CRIMSON_FUNGUS, $$3);
        $$0.put(Blocks.WARPED_FUNGUS, $$3);
        $$0.put(Blocks.CRIMSON_ROOTS, $$3);
        $$0.put(Blocks.WARPED_ROOTS, $$3);
        $$0.put(Blocks.POTTED_CRIMSON_FUNGUS, $$3);
        $$0.put(Blocks.POTTED_WARPED_FUNGUS, $$3);
        $$0.put(Blocks.POTTED_CRIMSON_ROOTS, $$3);
        $$0.put(Blocks.POTTED_WARPED_ROOTS, $$3);
        $$0.put(Blocks.CRIMSON_DOOR, $$3);
        $$0.put(Blocks.WARPED_DOOR, $$3);
        $$0.put(Blocks.POINTED_DRIPSTONE, $$3);
        $$0.put(Blocks.SMALL_AMETHYST_BUD, $$3);
        $$0.put(Blocks.MEDIUM_AMETHYST_BUD, $$3);
        $$0.put(Blocks.LARGE_AMETHYST_BUD, $$3);
        $$0.put(Blocks.AMETHYST_CLUSTER, $$3);
        $$0.put(Blocks.LIGHTNING_ROD, $$3);
        $$0.put(Blocks.CAVE_VINES, $$3);
        $$0.put(Blocks.CAVE_VINES_PLANT, $$3);
        $$0.put(Blocks.SPORE_BLOSSOM, $$3);
        $$0.put(Blocks.FLOWERING_AZALEA, $$3);
        $$0.put(Blocks.AZALEA, $$3);
        $$0.put(Blocks.PINK_PETALS, $$3);
        $$0.put(Blocks.WILDFLOWERS, $$3);
        $$0.put(Blocks.LEAF_LITTER, $$3);
        $$0.put(Blocks.BIG_DRIPLEAF, $$3);
        $$0.put(Blocks.BIG_DRIPLEAF_STEM, $$3);
        $$0.put(Blocks.SMALL_DRIPLEAF, $$3);
        $$0.put(Blocks.HANGING_ROOTS, $$3);
        $$0.put(Blocks.SCULK_SENSOR, $$3);
        $$0.put(Blocks.CALIBRATED_SCULK_SENSOR, $$3);
        $$0.put(Blocks.SCULK_VEIN, $$3);
        $$0.put(Blocks.SCULK_SHRIEKER, $$3);
        $$0.put(Blocks.MANGROVE_PROPAGULE, $$3);
        $$0.put(Blocks.FROGSPAWN, $$3);
        $$0.put(Blocks.COPPER_GRATE, $$3);
        $$0.put(Blocks.EXPOSED_COPPER_GRATE, $$3);
        $$0.put(Blocks.WEATHERED_COPPER_GRATE, $$3);
        $$0.put(Blocks.OXIDIZED_COPPER_GRATE, $$3);
        $$0.put(Blocks.WAXED_COPPER_GRATE, $$3);
        $$0.put(Blocks.WAXED_EXPOSED_COPPER_GRATE, $$3);
        $$0.put(Blocks.WAXED_WEATHERED_COPPER_GRATE, $$3);
        $$0.put(Blocks.WAXED_OXIDIZED_COPPER_GRATE, $$3);
        $$0.put(Blocks.FIREFLY_BUSH, $$3);
        $$0.put(Blocks.CACTUS_FLOWER, $$3);
        ChunkSectionLayer $$4 = ChunkSectionLayer.TRANSLUCENT;
        $$0.put(Blocks.ICE, $$4);
        $$0.put(Blocks.NETHER_PORTAL, $$4);
        $$0.put(Blocks.WHITE_STAINED_GLASS, $$4);
        $$0.put(Blocks.ORANGE_STAINED_GLASS, $$4);
        $$0.put(Blocks.MAGENTA_STAINED_GLASS, $$4);
        $$0.put(Blocks.LIGHT_BLUE_STAINED_GLASS, $$4);
        $$0.put(Blocks.YELLOW_STAINED_GLASS, $$4);
        $$0.put(Blocks.LIME_STAINED_GLASS, $$4);
        $$0.put(Blocks.PINK_STAINED_GLASS, $$4);
        $$0.put(Blocks.GRAY_STAINED_GLASS, $$4);
        $$0.put(Blocks.LIGHT_GRAY_STAINED_GLASS, $$4);
        $$0.put(Blocks.CYAN_STAINED_GLASS, $$4);
        $$0.put(Blocks.PURPLE_STAINED_GLASS, $$4);
        $$0.put(Blocks.BLUE_STAINED_GLASS, $$4);
        $$0.put(Blocks.BROWN_STAINED_GLASS, $$4);
        $$0.put(Blocks.GREEN_STAINED_GLASS, $$4);
        $$0.put(Blocks.RED_STAINED_GLASS, $$4);
        $$0.put(Blocks.BLACK_STAINED_GLASS, $$4);
        $$0.put(Blocks.WHITE_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.ORANGE_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.MAGENTA_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.YELLOW_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.LIME_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.PINK_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.GRAY_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.CYAN_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.PURPLE_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.BLUE_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.BROWN_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.GREEN_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.RED_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.BLACK_STAINED_GLASS_PANE, $$4);
        $$0.put(Blocks.SLIME_BLOCK, $$4);
        $$0.put(Blocks.HONEY_BLOCK, $$4);
        $$0.put(Blocks.FROSTED_ICE, $$4);
        $$0.put(Blocks.BUBBLE_COLUMN, $$4);
        $$0.put(Blocks.TINTED_GLASS, $$4);
    });
    private static final Map<Fluid, ChunkSectionLayer> LAYER_BY_FLUID = Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put(Fluids.FLOWING_WATER, ChunkSectionLayer.TRANSLUCENT);
        $$0.put(Fluids.WATER, ChunkSectionLayer.TRANSLUCENT);
    });
    private static boolean renderCutout;

    public static ChunkSectionLayer getChunkRenderType(BlockState $$0) {
        Block $$1 = $$0.getBlock();
        if ($$1 instanceof LeavesBlock) {
            return renderCutout ? ChunkSectionLayer.CUTOUT_MIPPED : ChunkSectionLayer.SOLID;
        }
        ChunkSectionLayer $$2 = TYPE_BY_BLOCK.get($$1);
        if ($$2 != null) {
            return $$2;
        }
        return ChunkSectionLayer.SOLID;
    }

    public static RenderType getMovingBlockRenderType(BlockState $$0) {
        Block $$1 = $$0.getBlock();
        if ($$1 instanceof LeavesBlock) {
            return renderCutout ? RenderType.cutoutMipped() : RenderType.solid();
        }
        ChunkSectionLayer $$2 = TYPE_BY_BLOCK.get($$1);
        if ($$2 != null) {
            return switch ($$2) {
                default -> throw new MatchException(null, null);
                case ChunkSectionLayer.SOLID -> RenderType.solid();
                case ChunkSectionLayer.CUTOUT_MIPPED -> RenderType.cutoutMipped();
                case ChunkSectionLayer.CUTOUT -> RenderType.cutout();
                case ChunkSectionLayer.TRANSLUCENT -> RenderType.translucentMovingBlock();
                case ChunkSectionLayer.TRIPWIRE -> RenderType.tripwire();
            };
        }
        return RenderType.solid();
    }

    public static RenderType getRenderType(BlockState $$0) {
        ChunkSectionLayer $$1 = ItemBlockRenderTypes.getChunkRenderType($$0);
        if ($$1 == ChunkSectionLayer.TRANSLUCENT) {
            return Sheets.translucentItemSheet();
        }
        return Sheets.cutoutBlockSheet();
    }

    public static RenderType getRenderType(ItemStack $$0) {
        Item $$1 = $$0.getItem();
        if ($$1 instanceof BlockItem) {
            BlockItem $$2 = (BlockItem)$$1;
            Block $$3 = $$2.getBlock();
            return ItemBlockRenderTypes.getRenderType($$3.defaultBlockState());
        }
        return Sheets.translucentItemSheet();
    }

    public static ChunkSectionLayer getRenderLayer(FluidState $$0) {
        ChunkSectionLayer $$1 = LAYER_BY_FLUID.get($$0.getType());
        if ($$1 != null) {
            return $$1;
        }
        return ChunkSectionLayer.SOLID;
    }

    public static void setFancy(boolean $$0) {
        renderCutout = $$0;
    }
}

