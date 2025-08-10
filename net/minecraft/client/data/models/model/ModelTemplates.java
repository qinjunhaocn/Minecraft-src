/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.data.models.model;

import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

public class ModelTemplates {
    public static final ModelTemplate CUBE = ModelTemplates.a("cube", TextureSlot.PARTICLE, TextureSlot.NORTH, TextureSlot.SOUTH, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.UP, TextureSlot.DOWN);
    public static final ModelTemplate CUBE_DIRECTIONAL = ModelTemplates.a("cube_directional", TextureSlot.PARTICLE, TextureSlot.NORTH, TextureSlot.SOUTH, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.UP, TextureSlot.DOWN);
    public static final ModelTemplate CUBE_ALL = ModelTemplates.a("cube_all", TextureSlot.ALL);
    public static final ModelTemplate CUBE_ALL_INNER_FACES = ModelTemplates.a("cube_all_inner_faces", TextureSlot.ALL);
    public static final ModelTemplate CUBE_MIRRORED_ALL = ModelTemplates.b("cube_mirrored_all", "_mirrored", TextureSlot.ALL);
    public static final ModelTemplate CUBE_NORTH_WEST_MIRRORED_ALL = ModelTemplates.b("cube_north_west_mirrored_all", "_north_west_mirrored", TextureSlot.ALL);
    public static final ModelTemplate CUBE_COLUMN_UV_LOCKED_X = ModelTemplates.b("cube_column_uv_locked_x", "_x", TextureSlot.END, TextureSlot.SIDE);
    public static final ModelTemplate CUBE_COLUMN_UV_LOCKED_Y = ModelTemplates.b("cube_column_uv_locked_y", "_y", TextureSlot.END, TextureSlot.SIDE);
    public static final ModelTemplate CUBE_COLUMN_UV_LOCKED_Z = ModelTemplates.b("cube_column_uv_locked_z", "_z", TextureSlot.END, TextureSlot.SIDE);
    public static final ModelTemplate CUBE_COLUMN = ModelTemplates.a("cube_column", TextureSlot.END, TextureSlot.SIDE);
    public static final ModelTemplate CUBE_COLUMN_HORIZONTAL = ModelTemplates.b("cube_column_horizontal", "_horizontal", TextureSlot.END, TextureSlot.SIDE);
    public static final ModelTemplate CUBE_COLUMN_MIRRORED = ModelTemplates.b("cube_column_mirrored", "_mirrored", TextureSlot.END, TextureSlot.SIDE);
    public static final ModelTemplate CUBE_TOP = ModelTemplates.a("cube_top", TextureSlot.TOP, TextureSlot.SIDE);
    public static final ModelTemplate CUBE_BOTTOM_TOP = ModelTemplates.a("cube_bottom_top", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate CUBE_BOTTOM_TOP_INNER_FACES = ModelTemplates.a("cube_bottom_top_inner_faces", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate CUBE_ORIENTABLE = ModelTemplates.a("orientable", TextureSlot.TOP, TextureSlot.FRONT, TextureSlot.SIDE);
    public static final ModelTemplate CUBE_ORIENTABLE_TOP_BOTTOM = ModelTemplates.a("orientable_with_bottom", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.FRONT);
    public static final ModelTemplate CUBE_ORIENTABLE_VERTICAL = ModelTemplates.b("orientable_vertical", "_vertical", TextureSlot.FRONT, TextureSlot.SIDE);
    public static final ModelTemplate BUTTON = ModelTemplates.a("button", TextureSlot.TEXTURE);
    public static final ModelTemplate BUTTON_PRESSED = ModelTemplates.b("button_pressed", "_pressed", TextureSlot.TEXTURE);
    public static final ModelTemplate BUTTON_INVENTORY = ModelTemplates.b("button_inventory", "_inventory", TextureSlot.TEXTURE);
    public static final ModelTemplate DOOR_BOTTOM_LEFT = ModelTemplates.b("door_bottom_left", "_bottom_left", TextureSlot.TOP, TextureSlot.BOTTOM);
    public static final ModelTemplate DOOR_BOTTOM_LEFT_OPEN = ModelTemplates.b("door_bottom_left_open", "_bottom_left_open", TextureSlot.TOP, TextureSlot.BOTTOM);
    public static final ModelTemplate DOOR_BOTTOM_RIGHT = ModelTemplates.b("door_bottom_right", "_bottom_right", TextureSlot.TOP, TextureSlot.BOTTOM);
    public static final ModelTemplate DOOR_BOTTOM_RIGHT_OPEN = ModelTemplates.b("door_bottom_right_open", "_bottom_right_open", TextureSlot.TOP, TextureSlot.BOTTOM);
    public static final ModelTemplate DOOR_TOP_LEFT = ModelTemplates.b("door_top_left", "_top_left", TextureSlot.TOP, TextureSlot.BOTTOM);
    public static final ModelTemplate DOOR_TOP_LEFT_OPEN = ModelTemplates.b("door_top_left_open", "_top_left_open", TextureSlot.TOP, TextureSlot.BOTTOM);
    public static final ModelTemplate DOOR_TOP_RIGHT = ModelTemplates.b("door_top_right", "_top_right", TextureSlot.TOP, TextureSlot.BOTTOM);
    public static final ModelTemplate DOOR_TOP_RIGHT_OPEN = ModelTemplates.b("door_top_right_open", "_top_right_open", TextureSlot.TOP, TextureSlot.BOTTOM);
    public static final ModelTemplate CUSTOM_FENCE_POST = ModelTemplates.b("custom_fence_post", "_post", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
    public static final ModelTemplate CUSTOM_FENCE_SIDE_NORTH = ModelTemplates.b("custom_fence_side_north", "_side_north", TextureSlot.TEXTURE);
    public static final ModelTemplate CUSTOM_FENCE_SIDE_EAST = ModelTemplates.b("custom_fence_side_east", "_side_east", TextureSlot.TEXTURE);
    public static final ModelTemplate CUSTOM_FENCE_SIDE_SOUTH = ModelTemplates.b("custom_fence_side_south", "_side_south", TextureSlot.TEXTURE);
    public static final ModelTemplate CUSTOM_FENCE_SIDE_WEST = ModelTemplates.b("custom_fence_side_west", "_side_west", TextureSlot.TEXTURE);
    public static final ModelTemplate CUSTOM_FENCE_INVENTORY = ModelTemplates.b("custom_fence_inventory", "_inventory", TextureSlot.TEXTURE);
    public static final ModelTemplate FENCE_POST = ModelTemplates.b("fence_post", "_post", TextureSlot.TEXTURE);
    public static final ModelTemplate FENCE_SIDE = ModelTemplates.b("fence_side", "_side", TextureSlot.TEXTURE);
    public static final ModelTemplate FENCE_INVENTORY = ModelTemplates.b("fence_inventory", "_inventory", TextureSlot.TEXTURE);
    public static final ModelTemplate WALL_POST = ModelTemplates.b("template_wall_post", "_post", TextureSlot.WALL);
    public static final ModelTemplate WALL_LOW_SIDE = ModelTemplates.b("template_wall_side", "_side", TextureSlot.WALL);
    public static final ModelTemplate WALL_TALL_SIDE = ModelTemplates.b("template_wall_side_tall", "_side_tall", TextureSlot.WALL);
    public static final ModelTemplate WALL_INVENTORY = ModelTemplates.b("wall_inventory", "_inventory", TextureSlot.WALL);
    public static final ModelTemplate CUSTOM_FENCE_GATE_CLOSED = ModelTemplates.a("template_custom_fence_gate", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
    public static final ModelTemplate CUSTOM_FENCE_GATE_OPEN = ModelTemplates.b("template_custom_fence_gate_open", "_open", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
    public static final ModelTemplate CUSTOM_FENCE_GATE_WALL_CLOSED = ModelTemplates.b("template_custom_fence_gate_wall", "_wall", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
    public static final ModelTemplate CUSTOM_FENCE_GATE_WALL_OPEN = ModelTemplates.b("template_custom_fence_gate_wall_open", "_wall_open", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
    public static final ModelTemplate FENCE_GATE_CLOSED = ModelTemplates.a("template_fence_gate", TextureSlot.TEXTURE);
    public static final ModelTemplate FENCE_GATE_OPEN = ModelTemplates.b("template_fence_gate_open", "_open", TextureSlot.TEXTURE);
    public static final ModelTemplate FENCE_GATE_WALL_CLOSED = ModelTemplates.b("template_fence_gate_wall", "_wall", TextureSlot.TEXTURE);
    public static final ModelTemplate FENCE_GATE_WALL_OPEN = ModelTemplates.b("template_fence_gate_wall_open", "_wall_open", TextureSlot.TEXTURE);
    public static final ModelTemplate PRESSURE_PLATE_UP = ModelTemplates.a("pressure_plate_up", TextureSlot.TEXTURE);
    public static final ModelTemplate PRESSURE_PLATE_DOWN = ModelTemplates.b("pressure_plate_down", "_down", TextureSlot.TEXTURE);
    public static final ModelTemplate PARTICLE_ONLY = ModelTemplates.a(TextureSlot.PARTICLE);
    public static final ModelTemplate SLAB_BOTTOM = ModelTemplates.a("slab", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
    public static final ModelTemplate SLAB_TOP = ModelTemplates.b("slab_top", "_top", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
    public static final ModelTemplate LEAVES = ModelTemplates.a("leaves", TextureSlot.ALL);
    public static final ModelTemplate STAIRS_STRAIGHT = ModelTemplates.a("stairs", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
    public static final ModelTemplate STAIRS_INNER = ModelTemplates.b("inner_stairs", "_inner", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
    public static final ModelTemplate STAIRS_OUTER = ModelTemplates.b("outer_stairs", "_outer", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
    public static final ModelTemplate TRAPDOOR_TOP = ModelTemplates.b("template_trapdoor_top", "_top", TextureSlot.TEXTURE);
    public static final ModelTemplate TRAPDOOR_BOTTOM = ModelTemplates.b("template_trapdoor_bottom", "_bottom", TextureSlot.TEXTURE);
    public static final ModelTemplate TRAPDOOR_OPEN = ModelTemplates.b("template_trapdoor_open", "_open", TextureSlot.TEXTURE);
    public static final ModelTemplate ORIENTABLE_TRAPDOOR_TOP = ModelTemplates.b("template_orientable_trapdoor_top", "_top", TextureSlot.TEXTURE);
    public static final ModelTemplate ORIENTABLE_TRAPDOOR_BOTTOM = ModelTemplates.b("template_orientable_trapdoor_bottom", "_bottom", TextureSlot.TEXTURE);
    public static final ModelTemplate ORIENTABLE_TRAPDOOR_OPEN = ModelTemplates.b("template_orientable_trapdoor_open", "_open", TextureSlot.TEXTURE);
    public static final ModelTemplate POINTED_DRIPSTONE = ModelTemplates.a("pointed_dripstone", TextureSlot.CROSS);
    public static final ModelTemplate CROSS = ModelTemplates.a("cross", TextureSlot.CROSS);
    public static final ModelTemplate TINTED_CROSS = ModelTemplates.a("tinted_cross", TextureSlot.CROSS);
    public static final ModelTemplate CROSS_EMISSIVE = ModelTemplates.a("cross_emissive", TextureSlot.CROSS, TextureSlot.CROSS_EMISSIVE);
    public static final ModelTemplate FLOWER_POT_CROSS = ModelTemplates.a("flower_pot_cross", TextureSlot.PLANT);
    public static final ModelTemplate TINTED_FLOWER_POT_CROSS = ModelTemplates.a("tinted_flower_pot_cross", TextureSlot.PLANT);
    public static final ModelTemplate FLOWER_POT_CROSS_EMISSIVE = ModelTemplates.a("flower_pot_cross_emissive", TextureSlot.PLANT, TextureSlot.CROSS_EMISSIVE);
    public static final ModelTemplate RAIL_FLAT = ModelTemplates.a("rail_flat", TextureSlot.RAIL);
    public static final ModelTemplate RAIL_CURVED = ModelTemplates.b("rail_curved", "_corner", TextureSlot.RAIL);
    public static final ModelTemplate RAIL_RAISED_NE = ModelTemplates.b("template_rail_raised_ne", "_raised_ne", TextureSlot.RAIL);
    public static final ModelTemplate RAIL_RAISED_SW = ModelTemplates.b("template_rail_raised_sw", "_raised_sw", TextureSlot.RAIL);
    public static final ModelTemplate CARPET = ModelTemplates.a("carpet", TextureSlot.WOOL);
    public static final ModelTemplate MOSSY_CARPET_SIDE = ModelTemplates.a("mossy_carpet_side", TextureSlot.SIDE);
    public static final ModelTemplate FLOWERBED_1 = ModelTemplates.b("flowerbed_1", "_1", TextureSlot.FLOWERBED, TextureSlot.STEM);
    public static final ModelTemplate FLOWERBED_2 = ModelTemplates.b("flowerbed_2", "_2", TextureSlot.FLOWERBED, TextureSlot.STEM);
    public static final ModelTemplate FLOWERBED_3 = ModelTemplates.b("flowerbed_3", "_3", TextureSlot.FLOWERBED, TextureSlot.STEM);
    public static final ModelTemplate FLOWERBED_4 = ModelTemplates.b("flowerbed_4", "_4", TextureSlot.FLOWERBED, TextureSlot.STEM);
    public static final ModelTemplate LEAF_LITTER_1 = ModelTemplates.b("template_leaf_litter_1", "_1", TextureSlot.TEXTURE);
    public static final ModelTemplate LEAF_LITTER_2 = ModelTemplates.b("template_leaf_litter_2", "_2", TextureSlot.TEXTURE);
    public static final ModelTemplate LEAF_LITTER_3 = ModelTemplates.b("template_leaf_litter_3", "_3", TextureSlot.TEXTURE);
    public static final ModelTemplate LEAF_LITTER_4 = ModelTemplates.b("template_leaf_litter_4", "_4", TextureSlot.TEXTURE);
    public static final ModelTemplate CORAL_FAN = ModelTemplates.a("coral_fan", TextureSlot.FAN);
    public static final ModelTemplate CORAL_WALL_FAN = ModelTemplates.a("coral_wall_fan", TextureSlot.FAN);
    public static final ModelTemplate GLAZED_TERRACOTTA = ModelTemplates.a("template_glazed_terracotta", TextureSlot.PATTERN);
    public static final ModelTemplate CHORUS_FLOWER = ModelTemplates.a("template_chorus_flower", TextureSlot.TEXTURE);
    public static final ModelTemplate DAYLIGHT_DETECTOR = ModelTemplates.a("template_daylight_detector", TextureSlot.TOP, TextureSlot.SIDE);
    public static final ModelTemplate STAINED_GLASS_PANE_NOSIDE = ModelTemplates.b("template_glass_pane_noside", "_noside", TextureSlot.PANE);
    public static final ModelTemplate STAINED_GLASS_PANE_NOSIDE_ALT = ModelTemplates.b("template_glass_pane_noside_alt", "_noside_alt", TextureSlot.PANE);
    public static final ModelTemplate STAINED_GLASS_PANE_POST = ModelTemplates.b("template_glass_pane_post", "_post", TextureSlot.PANE, TextureSlot.EDGE);
    public static final ModelTemplate STAINED_GLASS_PANE_SIDE = ModelTemplates.b("template_glass_pane_side", "_side", TextureSlot.PANE, TextureSlot.EDGE);
    public static final ModelTemplate STAINED_GLASS_PANE_SIDE_ALT = ModelTemplates.b("template_glass_pane_side_alt", "_side_alt", TextureSlot.PANE, TextureSlot.EDGE);
    public static final ModelTemplate COMMAND_BLOCK = ModelTemplates.a("template_command_block", TextureSlot.FRONT, TextureSlot.BACK, TextureSlot.SIDE);
    public static final ModelTemplate CHISELED_BOOKSHELF_SLOT_TOP_LEFT = ModelTemplates.b("template_chiseled_bookshelf_slot_top_left", "_slot_top_left", TextureSlot.TEXTURE);
    public static final ModelTemplate CHISELED_BOOKSHELF_SLOT_TOP_MID = ModelTemplates.b("template_chiseled_bookshelf_slot_top_mid", "_slot_top_mid", TextureSlot.TEXTURE);
    public static final ModelTemplate CHISELED_BOOKSHELF_SLOT_TOP_RIGHT = ModelTemplates.b("template_chiseled_bookshelf_slot_top_right", "_slot_top_right", TextureSlot.TEXTURE);
    public static final ModelTemplate CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT = ModelTemplates.b("template_chiseled_bookshelf_slot_bottom_left", "_slot_bottom_left", TextureSlot.TEXTURE);
    public static final ModelTemplate CHISELED_BOOKSHELF_SLOT_BOTTOM_MID = ModelTemplates.b("template_chiseled_bookshelf_slot_bottom_mid", "_slot_bottom_mid", TextureSlot.TEXTURE);
    public static final ModelTemplate CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT = ModelTemplates.b("template_chiseled_bookshelf_slot_bottom_right", "_slot_bottom_right", TextureSlot.TEXTURE);
    public static final ModelTemplate ANVIL = ModelTemplates.a("template_anvil", TextureSlot.TOP);
    public static final ModelTemplate[] STEMS = (ModelTemplate[])IntStream.range(0, 8).mapToObj($$0 -> ModelTemplates.b("stem_growth" + $$0, "_stage" + $$0, TextureSlot.STEM)).toArray(ModelTemplate[]::new);
    public static final ModelTemplate ATTACHED_STEM = ModelTemplates.a("stem_fruit", TextureSlot.STEM, TextureSlot.UPPER_STEM);
    public static final ModelTemplate CROP = ModelTemplates.a("crop", TextureSlot.CROP);
    public static final ModelTemplate FARMLAND = ModelTemplates.a("template_farmland", TextureSlot.DIRT, TextureSlot.TOP);
    public static final ModelTemplate FIRE_FLOOR = ModelTemplates.a("template_fire_floor", TextureSlot.FIRE);
    public static final ModelTemplate FIRE_SIDE = ModelTemplates.a("template_fire_side", TextureSlot.FIRE);
    public static final ModelTemplate FIRE_SIDE_ALT = ModelTemplates.a("template_fire_side_alt", TextureSlot.FIRE);
    public static final ModelTemplate FIRE_UP = ModelTemplates.a("template_fire_up", TextureSlot.FIRE);
    public static final ModelTemplate FIRE_UP_ALT = ModelTemplates.a("template_fire_up_alt", TextureSlot.FIRE);
    public static final ModelTemplate CAMPFIRE = ModelTemplates.a("template_campfire", TextureSlot.FIRE, TextureSlot.LIT_LOG);
    public static final ModelTemplate LANTERN = ModelTemplates.a("template_lantern", TextureSlot.LANTERN);
    public static final ModelTemplate HANGING_LANTERN = ModelTemplates.b("template_hanging_lantern", "_hanging", TextureSlot.LANTERN);
    public static final ModelTemplate TORCH = ModelTemplates.a("template_torch", TextureSlot.TORCH);
    public static final ModelTemplate TORCH_UNLIT = ModelTemplates.a("template_torch_unlit", TextureSlot.TORCH);
    public static final ModelTemplate WALL_TORCH = ModelTemplates.a("template_torch_wall", TextureSlot.TORCH);
    public static final ModelTemplate WALL_TORCH_UNLIT = ModelTemplates.a("template_torch_wall_unlit", TextureSlot.TORCH);
    public static final ModelTemplate REDSTONE_TORCH = ModelTemplates.a("template_redstone_torch", TextureSlot.TORCH);
    public static final ModelTemplate REDSTONE_WALL_TORCH = ModelTemplates.a("template_redstone_torch_wall", TextureSlot.TORCH);
    public static final ModelTemplate PISTON = ModelTemplates.a("template_piston", TextureSlot.PLATFORM, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate PISTON_HEAD = ModelTemplates.a("template_piston_head", TextureSlot.PLATFORM, TextureSlot.SIDE, TextureSlot.UNSTICKY);
    public static final ModelTemplate PISTON_HEAD_SHORT = ModelTemplates.a("template_piston_head_short", TextureSlot.PLATFORM, TextureSlot.SIDE, TextureSlot.UNSTICKY);
    public static final ModelTemplate SEAGRASS = ModelTemplates.a("template_seagrass", TextureSlot.TEXTURE);
    public static final ModelTemplate TURTLE_EGG = ModelTemplates.a("template_turtle_egg", TextureSlot.ALL);
    public static final ModelTemplate DRIED_GHAST = ModelTemplates.a("dried_ghast", TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.NORTH, TextureSlot.SOUTH, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.TENTACLES);
    public static final ModelTemplate TWO_TURTLE_EGGS = ModelTemplates.a("template_two_turtle_eggs", TextureSlot.ALL);
    public static final ModelTemplate THREE_TURTLE_EGGS = ModelTemplates.a("template_three_turtle_eggs", TextureSlot.ALL);
    public static final ModelTemplate FOUR_TURTLE_EGGS = ModelTemplates.a("template_four_turtle_eggs", TextureSlot.ALL);
    public static final ModelTemplate SINGLE_FACE = ModelTemplates.a("template_single_face", TextureSlot.TEXTURE);
    public static final ModelTemplate CAULDRON_LEVEL1 = ModelTemplates.a("template_cauldron_level1", TextureSlot.CONTENT, TextureSlot.INSIDE, TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate CAULDRON_LEVEL2 = ModelTemplates.a("template_cauldron_level2", TextureSlot.CONTENT, TextureSlot.INSIDE, TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate CAULDRON_FULL = ModelTemplates.a("template_cauldron_full", TextureSlot.CONTENT, TextureSlot.INSIDE, TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate AZALEA = ModelTemplates.a("template_azalea", TextureSlot.TOP, TextureSlot.SIDE);
    public static final ModelTemplate POTTED_AZALEA = ModelTemplates.a("template_potted_azalea_bush", TextureSlot.PLANT, TextureSlot.TOP, TextureSlot.SIDE);
    public static final ModelTemplate POTTED_FLOWERING_AZALEA = ModelTemplates.a("template_potted_azalea_bush", TextureSlot.PLANT, TextureSlot.TOP, TextureSlot.SIDE);
    public static final ModelTemplate SNIFFER_EGG = ModelTemplates.a("sniffer_egg", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.NORTH, TextureSlot.SOUTH, TextureSlot.EAST, TextureSlot.WEST);
    public static final ModelTemplate FLAT_ITEM = ModelTemplates.b("generated", TextureSlot.LAYER0);
    public static final ModelTemplate MUSIC_DISC = ModelTemplates.b("template_music_disc", TextureSlot.LAYER0);
    public static final ModelTemplate FLAT_HANDHELD_ITEM = ModelTemplates.b("handheld", TextureSlot.LAYER0);
    public static final ModelTemplate FLAT_HANDHELD_ROD_ITEM = ModelTemplates.b("handheld_rod", TextureSlot.LAYER0);
    public static final ModelTemplate TWO_LAYERED_ITEM = ModelTemplates.b("generated", TextureSlot.LAYER0, TextureSlot.LAYER1);
    public static final ModelTemplate THREE_LAYERED_ITEM = ModelTemplates.b("generated", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2);
    public static final ModelTemplate SHULKER_BOX_INVENTORY = ModelTemplates.b("template_shulker_box", TextureSlot.PARTICLE);
    public static final ModelTemplate BED_INVENTORY = ModelTemplates.b("template_bed", TextureSlot.PARTICLE);
    public static final ModelTemplate CHEST_INVENTORY = ModelTemplates.b("template_chest", TextureSlot.PARTICLE);
    public static final ModelTemplate BUNDLE_OPEN_FRONT_INVENTORY = ModelTemplates.a("template_bundle_open_front", "_open_front", TextureSlot.LAYER0);
    public static final ModelTemplate BUNDLE_OPEN_BACK_INVENTORY = ModelTemplates.a("template_bundle_open_back", "_open_back", TextureSlot.LAYER0);
    public static final ModelTemplate BOW = ModelTemplates.b("bow", TextureSlot.LAYER0);
    public static final ModelTemplate CROSSBOW = ModelTemplates.b("crossbow", TextureSlot.LAYER0);
    public static final ModelTemplate CANDLE = ModelTemplates.a("template_candle", TextureSlot.ALL, TextureSlot.PARTICLE);
    public static final ModelTemplate TWO_CANDLES = ModelTemplates.a("template_two_candles", TextureSlot.ALL, TextureSlot.PARTICLE);
    public static final ModelTemplate THREE_CANDLES = ModelTemplates.a("template_three_candles", TextureSlot.ALL, TextureSlot.PARTICLE);
    public static final ModelTemplate FOUR_CANDLES = ModelTemplates.a("template_four_candles", TextureSlot.ALL, TextureSlot.PARTICLE);
    public static final ModelTemplate CANDLE_CAKE = ModelTemplates.a("template_cake_with_candle", TextureSlot.CANDLE, TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.PARTICLE);
    public static final ModelTemplate SCULK_SHRIEKER = ModelTemplates.a("template_sculk_shrieker", TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.PARTICLE, TextureSlot.INNER_TOP);
    public static final ModelTemplate VAULT = ModelTemplates.a("template_vault", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.FRONT);
    public static final ModelTemplate FLAT_HANDHELD_MACE_ITEM = ModelTemplates.b("handheld_mace", TextureSlot.LAYER0);

    private static ModelTemplate a(TextureSlot ... $$0) {
        return new ModelTemplate(Optional.empty(), Optional.empty(), $$0);
    }

    private static ModelTemplate a(String $$0, TextureSlot ... $$1) {
        return new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("block/" + $$0)), Optional.empty(), $$1);
    }

    private static ModelTemplate b(String $$0, TextureSlot ... $$1) {
        return new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("item/" + $$0)), Optional.empty(), $$1);
    }

    private static ModelTemplate a(String $$0, String $$1, TextureSlot ... $$2) {
        return new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("item/" + $$0)), Optional.of($$1), $$2);
    }

    private static ModelTemplate b(String $$0, String $$1, TextureSlot ... $$2) {
        return new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("block/" + $$0)), Optional.of($$1), $$2);
    }
}

