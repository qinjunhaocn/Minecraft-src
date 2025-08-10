/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.biome.EndBiomes;
import net.minecraft.data.worldgen.biome.NetherBiomes;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public abstract class BiomeData {
    public static void bootstrap(BootstrapContext<Biome> $$0) {
        HolderGetter<PlacedFeature> $$1 = $$0.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> $$2 = $$0.lookup(Registries.CONFIGURED_CARVER);
        $$0.register(Biomes.THE_VOID, OverworldBiomes.theVoid($$1, $$2));
        $$0.register(Biomes.PLAINS, OverworldBiomes.plains($$1, $$2, false, false, false));
        $$0.register(Biomes.SUNFLOWER_PLAINS, OverworldBiomes.plains($$1, $$2, true, false, false));
        $$0.register(Biomes.SNOWY_PLAINS, OverworldBiomes.plains($$1, $$2, false, true, false));
        $$0.register(Biomes.ICE_SPIKES, OverworldBiomes.plains($$1, $$2, false, true, true));
        $$0.register(Biomes.DESERT, OverworldBiomes.desert($$1, $$2));
        $$0.register(Biomes.SWAMP, OverworldBiomes.swamp($$1, $$2));
        $$0.register(Biomes.MANGROVE_SWAMP, OverworldBiomes.mangroveSwamp($$1, $$2));
        $$0.register(Biomes.FOREST, OverworldBiomes.forest($$1, $$2, false, false, false));
        $$0.register(Biomes.FLOWER_FOREST, OverworldBiomes.forest($$1, $$2, false, false, true));
        $$0.register(Biomes.BIRCH_FOREST, OverworldBiomes.forest($$1, $$2, true, false, false));
        $$0.register(Biomes.DARK_FOREST, OverworldBiomes.darkForest($$1, $$2, false));
        $$0.register(Biomes.PALE_GARDEN, OverworldBiomes.darkForest($$1, $$2, true));
        $$0.register(Biomes.OLD_GROWTH_BIRCH_FOREST, OverworldBiomes.forest($$1, $$2, true, true, false));
        $$0.register(Biomes.OLD_GROWTH_PINE_TAIGA, OverworldBiomes.oldGrowthTaiga($$1, $$2, false));
        $$0.register(Biomes.OLD_GROWTH_SPRUCE_TAIGA, OverworldBiomes.oldGrowthTaiga($$1, $$2, true));
        $$0.register(Biomes.TAIGA, OverworldBiomes.taiga($$1, $$2, false));
        $$0.register(Biomes.SNOWY_TAIGA, OverworldBiomes.taiga($$1, $$2, true));
        $$0.register(Biomes.SAVANNA, OverworldBiomes.savanna($$1, $$2, false, false));
        $$0.register(Biomes.SAVANNA_PLATEAU, OverworldBiomes.savanna($$1, $$2, false, true));
        $$0.register(Biomes.WINDSWEPT_HILLS, OverworldBiomes.windsweptHills($$1, $$2, false));
        $$0.register(Biomes.WINDSWEPT_GRAVELLY_HILLS, OverworldBiomes.windsweptHills($$1, $$2, false));
        $$0.register(Biomes.WINDSWEPT_FOREST, OverworldBiomes.windsweptHills($$1, $$2, true));
        $$0.register(Biomes.WINDSWEPT_SAVANNA, OverworldBiomes.savanna($$1, $$2, true, false));
        $$0.register(Biomes.JUNGLE, OverworldBiomes.jungle($$1, $$2));
        $$0.register(Biomes.SPARSE_JUNGLE, OverworldBiomes.sparseJungle($$1, $$2));
        $$0.register(Biomes.BAMBOO_JUNGLE, OverworldBiomes.bambooJungle($$1, $$2));
        $$0.register(Biomes.BADLANDS, OverworldBiomes.badlands($$1, $$2, false));
        $$0.register(Biomes.ERODED_BADLANDS, OverworldBiomes.badlands($$1, $$2, false));
        $$0.register(Biomes.WOODED_BADLANDS, OverworldBiomes.badlands($$1, $$2, true));
        $$0.register(Biomes.MEADOW, OverworldBiomes.meadowOrCherryGrove($$1, $$2, false));
        $$0.register(Biomes.CHERRY_GROVE, OverworldBiomes.meadowOrCherryGrove($$1, $$2, true));
        $$0.register(Biomes.GROVE, OverworldBiomes.grove($$1, $$2));
        $$0.register(Biomes.SNOWY_SLOPES, OverworldBiomes.snowySlopes($$1, $$2));
        $$0.register(Biomes.FROZEN_PEAKS, OverworldBiomes.frozenPeaks($$1, $$2));
        $$0.register(Biomes.JAGGED_PEAKS, OverworldBiomes.jaggedPeaks($$1, $$2));
        $$0.register(Biomes.STONY_PEAKS, OverworldBiomes.stonyPeaks($$1, $$2));
        $$0.register(Biomes.RIVER, OverworldBiomes.river($$1, $$2, false));
        $$0.register(Biomes.FROZEN_RIVER, OverworldBiomes.river($$1, $$2, true));
        $$0.register(Biomes.BEACH, OverworldBiomes.beach($$1, $$2, false, false));
        $$0.register(Biomes.SNOWY_BEACH, OverworldBiomes.beach($$1, $$2, true, false));
        $$0.register(Biomes.STONY_SHORE, OverworldBiomes.beach($$1, $$2, false, true));
        $$0.register(Biomes.WARM_OCEAN, OverworldBiomes.warmOcean($$1, $$2));
        $$0.register(Biomes.LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean($$1, $$2, false));
        $$0.register(Biomes.DEEP_LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean($$1, $$2, true));
        $$0.register(Biomes.OCEAN, OverworldBiomes.ocean($$1, $$2, false));
        $$0.register(Biomes.DEEP_OCEAN, OverworldBiomes.ocean($$1, $$2, true));
        $$0.register(Biomes.COLD_OCEAN, OverworldBiomes.coldOcean($$1, $$2, false));
        $$0.register(Biomes.DEEP_COLD_OCEAN, OverworldBiomes.coldOcean($$1, $$2, true));
        $$0.register(Biomes.FROZEN_OCEAN, OverworldBiomes.frozenOcean($$1, $$2, false));
        $$0.register(Biomes.DEEP_FROZEN_OCEAN, OverworldBiomes.frozenOcean($$1, $$2, true));
        $$0.register(Biomes.MUSHROOM_FIELDS, OverworldBiomes.mushroomFields($$1, $$2));
        $$0.register(Biomes.DRIPSTONE_CAVES, OverworldBiomes.dripstoneCaves($$1, $$2));
        $$0.register(Biomes.LUSH_CAVES, OverworldBiomes.lushCaves($$1, $$2));
        $$0.register(Biomes.DEEP_DARK, OverworldBiomes.deepDark($$1, $$2));
        $$0.register(Biomes.NETHER_WASTES, NetherBiomes.netherWastes($$1, $$2));
        $$0.register(Biomes.WARPED_FOREST, NetherBiomes.warpedForest($$1, $$2));
        $$0.register(Biomes.CRIMSON_FOREST, NetherBiomes.crimsonForest($$1, $$2));
        $$0.register(Biomes.SOUL_SAND_VALLEY, NetherBiomes.soulSandValley($$1, $$2));
        $$0.register(Biomes.BASALT_DELTAS, NetherBiomes.basaltDeltas($$1, $$2));
        $$0.register(Biomes.THE_END, EndBiomes.theEnd($$1, $$2));
        $$0.register(Biomes.END_HIGHLANDS, EndBiomes.endHighlands($$1, $$2));
        $$0.register(Biomes.END_MIDLANDS, EndBiomes.endMidlands($$1, $$2));
        $$0.register(Biomes.SMALL_END_ISLANDS, EndBiomes.smallEndIslands($$1, $$2));
        $$0.register(Biomes.END_BARRENS, EndBiomes.endBarrens($$1, $$2));
    }
}

