/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.worldgen.placement;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceRelativeThresholdFilter;

public class CavePlacements {
    public static final ResourceKey<PlacedFeature> MONSTER_ROOM = PlacementUtils.createKey("monster_room");
    public static final ResourceKey<PlacedFeature> MONSTER_ROOM_DEEP = PlacementUtils.createKey("monster_room_deep");
    public static final ResourceKey<PlacedFeature> FOSSIL_UPPER = PlacementUtils.createKey("fossil_upper");
    public static final ResourceKey<PlacedFeature> FOSSIL_LOWER = PlacementUtils.createKey("fossil_lower");
    public static final ResourceKey<PlacedFeature> DRIPSTONE_CLUSTER = PlacementUtils.createKey("dripstone_cluster");
    public static final ResourceKey<PlacedFeature> LARGE_DRIPSTONE = PlacementUtils.createKey("large_dripstone");
    public static final ResourceKey<PlacedFeature> POINTED_DRIPSTONE = PlacementUtils.createKey("pointed_dripstone");
    public static final ResourceKey<PlacedFeature> UNDERWATER_MAGMA = PlacementUtils.createKey("underwater_magma");
    public static final ResourceKey<PlacedFeature> GLOW_LICHEN = PlacementUtils.createKey("glow_lichen");
    public static final ResourceKey<PlacedFeature> ROOTED_AZALEA_TREE = PlacementUtils.createKey("rooted_azalea_tree");
    public static final ResourceKey<PlacedFeature> CAVE_VINES = PlacementUtils.createKey("cave_vines");
    public static final ResourceKey<PlacedFeature> LUSH_CAVES_VEGETATION = PlacementUtils.createKey("lush_caves_vegetation");
    public static final ResourceKey<PlacedFeature> LUSH_CAVES_CLAY = PlacementUtils.createKey("lush_caves_clay");
    public static final ResourceKey<PlacedFeature> LUSH_CAVES_CEILING_VEGETATION = PlacementUtils.createKey("lush_caves_ceiling_vegetation");
    public static final ResourceKey<PlacedFeature> SPORE_BLOSSOM = PlacementUtils.createKey("spore_blossom");
    public static final ResourceKey<PlacedFeature> CLASSIC_VINES = PlacementUtils.createKey("classic_vines_cave_feature");
    public static final ResourceKey<PlacedFeature> AMETHYST_GEODE = PlacementUtils.createKey("amethyst_geode");
    public static final ResourceKey<PlacedFeature> SCULK_PATCH_DEEP_DARK = PlacementUtils.createKey("sculk_patch_deep_dark");
    public static final ResourceKey<PlacedFeature> SCULK_PATCH_ANCIENT_CITY = PlacementUtils.createKey("sculk_patch_ancient_city");
    public static final ResourceKey<PlacedFeature> SCULK_VEIN = PlacementUtils.createKey("sculk_vein");

    public static void bootstrap(BootstrapContext<PlacedFeature> $$0) {
        HolderGetter<ConfiguredFeature<?, ?>> $$1 = $$0.lookup(Registries.CONFIGURED_FEATURE);
        Holder.Reference<ConfiguredFeature<?, ?>> $$2 = $$1.getOrThrow(CaveFeatures.MONSTER_ROOM);
        Holder.Reference<ConfiguredFeature<?, ?>> $$3 = $$1.getOrThrow(CaveFeatures.FOSSIL_COAL);
        Holder.Reference<ConfiguredFeature<?, ?>> $$4 = $$1.getOrThrow(CaveFeatures.FOSSIL_DIAMONDS);
        Holder.Reference<ConfiguredFeature<?, ?>> $$5 = $$1.getOrThrow(CaveFeatures.DRIPSTONE_CLUSTER);
        Holder.Reference<ConfiguredFeature<?, ?>> $$6 = $$1.getOrThrow(CaveFeatures.LARGE_DRIPSTONE);
        Holder.Reference<ConfiguredFeature<?, ?>> $$7 = $$1.getOrThrow(CaveFeatures.POINTED_DRIPSTONE);
        Holder.Reference<ConfiguredFeature<?, ?>> $$8 = $$1.getOrThrow(CaveFeatures.UNDERWATER_MAGMA);
        Holder.Reference<ConfiguredFeature<?, ?>> $$9 = $$1.getOrThrow(CaveFeatures.GLOW_LICHEN);
        Holder.Reference<ConfiguredFeature<?, ?>> $$10 = $$1.getOrThrow(CaveFeatures.ROOTED_AZALEA_TREE);
        Holder.Reference<ConfiguredFeature<?, ?>> $$11 = $$1.getOrThrow(CaveFeatures.CAVE_VINE);
        Holder.Reference<ConfiguredFeature<?, ?>> $$12 = $$1.getOrThrow(CaveFeatures.MOSS_PATCH);
        Holder.Reference<ConfiguredFeature<?, ?>> $$13 = $$1.getOrThrow(CaveFeatures.LUSH_CAVES_CLAY);
        Holder.Reference<ConfiguredFeature<?, ?>> $$14 = $$1.getOrThrow(CaveFeatures.MOSS_PATCH_CEILING);
        Holder.Reference<ConfiguredFeature<?, ?>> $$15 = $$1.getOrThrow(CaveFeatures.SPORE_BLOSSOM);
        Holder.Reference<ConfiguredFeature<?, ?>> $$16 = $$1.getOrThrow(VegetationFeatures.VINES);
        Holder.Reference<ConfiguredFeature<?, ?>> $$17 = $$1.getOrThrow(CaveFeatures.AMETHYST_GEODE);
        Holder.Reference<ConfiguredFeature<?, ?>> $$18 = $$1.getOrThrow(CaveFeatures.SCULK_PATCH_DEEP_DARK);
        Holder.Reference<ConfiguredFeature<?, ?>> $$19 = $$1.getOrThrow(CaveFeatures.SCULK_PATCH_ANCIENT_CITY);
        Holder.Reference<ConfiguredFeature<?, ?>> $$20 = $$1.getOrThrow(CaveFeatures.SCULK_VEIN);
        PlacementUtils.a($$0, MONSTER_ROOM, $$2, CountPlacement.of(10), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.top()), BiomeFilter.biome());
        PlacementUtils.a($$0, MONSTER_ROOM_DEEP, $$2, CountPlacement.of(4), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(-1)), BiomeFilter.biome());
        PlacementUtils.a($$0, FOSSIL_UPPER, $$3, RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.top()), BiomeFilter.biome());
        PlacementUtils.a($$0, FOSSIL_LOWER, $$4, RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(-8)), BiomeFilter.biome());
        PlacementUtils.a($$0, DRIPSTONE_CLUSTER, $$5, CountPlacement.of(UniformInt.of(48, 96)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome());
        PlacementUtils.a($$0, LARGE_DRIPSTONE, $$6, CountPlacement.of(UniformInt.of(10, 48)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome());
        PlacementUtils.a($$0, POINTED_DRIPSTONE, $$7, CountPlacement.of(UniformInt.of(192, 256)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, CountPlacement.of(UniformInt.of(1, 5)), RandomOffsetPlacement.of(ClampedNormalInt.of(0.0f, 3.0f, -10, 10), ClampedNormalInt.of(0.0f, 0.6f, -2, 2)), BiomeFilter.biome());
        PlacementUtils.a($$0, UNDERWATER_MAGMA, $$8, CountPlacement.of(UniformInt.of(44, 52)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -2), BiomeFilter.biome());
        PlacementUtils.a($$0, GLOW_LICHEN, $$9, CountPlacement.of(UniformInt.of(104, 157)), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, InSquarePlacement.spread(), SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -13), BiomeFilter.biome());
        PlacementUtils.a($$0, ROOTED_AZALEA_TREE, $$10, CountPlacement.of(UniformInt.of(1, 2)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome());
        PlacementUtils.a($$0, CAVE_VINES, $$11, CountPlacement.of(188), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.hasSturdyFace(Direction.DOWN), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome());
        PlacementUtils.a($$0, LUSH_CAVES_VEGETATION, $$12, CountPlacement.of(125), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());
        PlacementUtils.a($$0, LUSH_CAVES_CLAY, $$13, CountPlacement.of(62), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BiomeFilter.biome());
        PlacementUtils.a($$0, LUSH_CAVES_CEILING_VEGETATION, $$14, CountPlacement.of(125), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome());
        PlacementUtils.a($$0, SPORE_BLOSSOM, $$15, CountPlacement.of(25), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome());
        PlacementUtils.a($$0, CLASSIC_VINES, $$16, CountPlacement.of(256), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome());
        PlacementUtils.a($$0, AMETHYST_GEODE, $$17, RarityFilter.onAverageOnceEvery(24), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(30)), BiomeFilter.biome());
        PlacementUtils.a($$0, SCULK_PATCH_DEEP_DARK, $$18, CountPlacement.of(ConstantInt.of(256)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome());
        PlacementUtils.a($$0, SCULK_PATCH_ANCIENT_CITY, $$19, new PlacementModifier[0]);
        PlacementUtils.a($$0, SCULK_VEIN, $$20, CountPlacement.of(UniformInt.of(204, 250)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome());
    }
}

