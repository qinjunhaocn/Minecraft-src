/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;

public class References {
    public static final DSL.TypeReference LEVEL = References.reference("level");
    public static final DSL.TypeReference LIGHTWEIGHT_LEVEL = References.reference("lightweight_level");
    public static final DSL.TypeReference PLAYER = References.reference("player");
    public static final DSL.TypeReference CHUNK = References.reference("chunk");
    public static final DSL.TypeReference HOTBAR = References.reference("hotbar");
    public static final DSL.TypeReference OPTIONS = References.reference("options");
    public static final DSL.TypeReference STRUCTURE = References.reference("structure");
    public static final DSL.TypeReference STATS = References.reference("stats");
    public static final DSL.TypeReference SAVED_DATA_COMMAND_STORAGE = References.reference("saved_data/command_storage");
    public static final DSL.TypeReference SAVED_DATA_TICKETS = References.reference("saved_data/tickets");
    public static final DSL.TypeReference SAVED_DATA_MAP_DATA = References.reference("saved_data/map_data");
    public static final DSL.TypeReference SAVED_DATA_MAP_INDEX = References.reference("saved_data/idcounts");
    public static final DSL.TypeReference SAVED_DATA_RAIDS = References.reference("saved_data/raids");
    public static final DSL.TypeReference SAVED_DATA_RANDOM_SEQUENCES = References.reference("saved_data/random_sequences");
    public static final DSL.TypeReference SAVED_DATA_STRUCTURE_FEATURE_INDICES = References.reference("saved_data/structure_feature_indices");
    public static final DSL.TypeReference SAVED_DATA_SCOREBOARD = References.reference("saved_data/scoreboard");
    public static final DSL.TypeReference ADVANCEMENTS = References.reference("advancements");
    public static final DSL.TypeReference POI_CHUNK = References.reference("poi_chunk");
    public static final DSL.TypeReference ENTITY_CHUNK = References.reference("entity_chunk");
    public static final DSL.TypeReference BLOCK_ENTITY = References.reference("block_entity");
    public static final DSL.TypeReference ITEM_STACK = References.reference("item_stack");
    public static final DSL.TypeReference BLOCK_STATE = References.reference("block_state");
    public static final DSL.TypeReference FLAT_BLOCK_STATE = References.reference("flat_block_state");
    public static final DSL.TypeReference DATA_COMPONENTS = References.reference("data_components");
    public static final DSL.TypeReference VILLAGER_TRADE = References.reference("villager_trade");
    public static final DSL.TypeReference PARTICLE = References.reference("particle");
    public static final DSL.TypeReference TEXT_COMPONENT = References.reference("text_component");
    public static final DSL.TypeReference ENTITY_EQUIPMENT = References.reference("entity_equipment");
    public static final DSL.TypeReference ENTITY_NAME = References.reference("entity_name");
    public static final DSL.TypeReference ENTITY_TREE = References.reference("entity_tree");
    public static final DSL.TypeReference ENTITY = References.reference("entity");
    public static final DSL.TypeReference BLOCK_NAME = References.reference("block_name");
    public static final DSL.TypeReference ITEM_NAME = References.reference("item_name");
    public static final DSL.TypeReference GAME_EVENT_NAME = References.reference("game_event_name");
    public static final DSL.TypeReference UNTAGGED_SPAWNER = References.reference("untagged_spawner");
    public static final DSL.TypeReference STRUCTURE_FEATURE = References.reference("structure_feature");
    public static final DSL.TypeReference OBJECTIVE = References.reference("objective");
    public static final DSL.TypeReference TEAM = References.reference("team");
    public static final DSL.TypeReference RECIPE = References.reference("recipe");
    public static final DSL.TypeReference BIOME = References.reference("biome");
    public static final DSL.TypeReference MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = References.reference("multi_noise_biome_source_parameter_list");
    public static final DSL.TypeReference WORLD_GEN_SETTINGS = References.reference("world_gen_settings");

    public static DSL.TypeReference reference(final String $$0) {
        return new DSL.TypeReference(){

            public String typeName() {
                return $$0;
            }

            public String toString() {
                return "@" + $$0;
            }
        };
    }
}

