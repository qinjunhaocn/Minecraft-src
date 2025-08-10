/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.TreePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBedBlock;
import net.minecraft.world.level.block.LeafLitterBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomBooleanFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.DualNoiseProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.NoiseProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.NoiseThresholdProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.Fluids;

public class VegetationFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> BAMBOO_NO_PODZOL = FeatureUtils.createKey("bamboo_no_podzol");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BAMBOO_SOME_PODZOL = FeatureUtils.createKey("bamboo_some_podzol");
    public static final ResourceKey<ConfiguredFeature<?, ?>> VINES = FeatureUtils.createKey("vines");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_BROWN_MUSHROOM = FeatureUtils.createKey("patch_brown_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_RED_MUSHROOM = FeatureUtils.createKey("patch_red_mushroom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_SUNFLOWER = FeatureUtils.createKey("patch_sunflower");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_PUMPKIN = FeatureUtils.createKey("patch_pumpkin");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_BERRY_BUSH = FeatureUtils.createKey("patch_berry_bush");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_TAIGA_GRASS = FeatureUtils.createKey("patch_taiga_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_GRASS = FeatureUtils.createKey("patch_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_GRASS_MEADOW = FeatureUtils.createKey("patch_grass_meadow");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_GRASS_JUNGLE = FeatureUtils.createKey("patch_grass_jungle");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SINGLE_PIECE_OF_GRASS = FeatureUtils.createKey("single_piece_of_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_DEAD_BUSH = FeatureUtils.createKey("patch_dead_bush");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_DRY_GRASS = FeatureUtils.createKey("patch_dry_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_MELON = FeatureUtils.createKey("patch_melon");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_WATERLILY = FeatureUtils.createKey("patch_waterlily");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_TALL_GRASS = FeatureUtils.createKey("patch_tall_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_LARGE_FERN = FeatureUtils.createKey("patch_large_fern");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_BUSH = FeatureUtils.createKey("patch_bush");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_LEAF_LITTER = FeatureUtils.createKey("patch_leaf_litter");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_FIREFLY_BUSH = FeatureUtils.createKey("patch_firefly_bush");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_CACTUS = FeatureUtils.createKey("patch_cactus");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PATCH_SUGAR_CANE = FeatureUtils.createKey("patch_sugar_cane");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_DEFAULT = FeatureUtils.createKey("flower_default");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_FLOWER_FOREST = FeatureUtils.createKey("flower_flower_forest");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_SWAMP = FeatureUtils.createKey("flower_swamp");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_PLAIN = FeatureUtils.createKey("flower_plain");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_MEADOW = FeatureUtils.createKey("flower_meadow");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_CHERRY = FeatureUtils.createKey("flower_cherry");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_PALE_GARDEN = FeatureUtils.createKey("flower_pale_garden");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILDFLOWERS_BIRCH_FOREST = FeatureUtils.createKey("wildflowers_birch_forest");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILDFLOWERS_MEADOW = FeatureUtils.createKey("wildflowers_meadow");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_FLOWERS = FeatureUtils.createKey("forest_flowers");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PALE_FOREST_FLOWERS = FeatureUtils.createKey("pale_forest_flowers");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DARK_FOREST_VEGETATION = FeatureUtils.createKey("dark_forest_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PALE_GARDEN_VEGETATION = FeatureUtils.createKey("pale_garden_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PALE_MOSS_VEGETATION = FeatureUtils.createKey("pale_moss_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PALE_MOSS_PATCH = FeatureUtils.createKey("pale_moss_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PALE_MOSS_PATCH_BONEMEAL = FeatureUtils.createKey("pale_moss_patch_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_FLOWER_FOREST = FeatureUtils.createKey("trees_flower_forest");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEADOW_TREES = FeatureUtils.createKey("meadow_trees");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_TAIGA = FeatureUtils.createKey("trees_taiga");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_BADLANDS = FeatureUtils.createKey("trees_badlands");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_GROVE = FeatureUtils.createKey("trees_grove");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_SAVANNA = FeatureUtils.createKey("trees_savanna");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_SNOWY = FeatureUtils.createKey("trees_snowy");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_BIRCH = FeatureUtils.createKey("trees_birch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIRCH_TALL = FeatureUtils.createKey("birch_tall");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_WINDSWEPT_HILLS = FeatureUtils.createKey("trees_windswept_hills");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_WATER = FeatureUtils.createKey("trees_water");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_BIRCH_AND_OAK_LEAF_LITTER = FeatureUtils.createKey("trees_birch_and_oak_leaf_litter");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_PLAINS = FeatureUtils.createKey("trees_plains");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_SPARSE_JUNGLE = FeatureUtils.createKey("trees_sparse_jungle");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_OLD_GROWTH_SPRUCE_TAIGA = FeatureUtils.createKey("trees_old_growth_spruce_taiga");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_OLD_GROWTH_PINE_TAIGA = FeatureUtils.createKey("trees_old_growth_pine_taiga");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREES_JUNGLE = FeatureUtils.createKey("trees_jungle");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BAMBOO_VEGETATION = FeatureUtils.createKey("bamboo_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUSHROOM_ISLAND_VEGETATION = FeatureUtils.createKey("mushroom_island_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MANGROVE_VEGETATION = FeatureUtils.createKey("mangrove_vegetation");
    private static final float FALLEN_TREE_ONE_IN_CHANCE = 80.0f;

    private static RandomPatchConfiguration grassPatch(BlockStateProvider $$0, int $$1) {
        return FeatureUtils.simpleRandomPatchConfiguration($$1, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration($$0)));
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> $$0) {
        HolderGetter<ConfiguredFeature<?, ?>> $$1 = $$0.lookup(Registries.CONFIGURED_FEATURE);
        Holder.Reference<ConfiguredFeature<?, ?>> $$2 = $$1.getOrThrow(TreeFeatures.HUGE_BROWN_MUSHROOM);
        Holder.Reference<ConfiguredFeature<?, ?>> $$3 = $$1.getOrThrow(TreeFeatures.HUGE_RED_MUSHROOM);
        Holder.Reference<ConfiguredFeature<?, ?>> $$4 = $$1.getOrThrow(TreeFeatures.FANCY_OAK_BEES_005);
        Holder.Reference<ConfiguredFeature<?, ?>> $$5 = $$1.getOrThrow(TreeFeatures.OAK_BEES_005);
        Holder.Reference<ConfiguredFeature<?, ?>> $$6 = $$1.getOrThrow(PATCH_GRASS_JUNGLE);
        HolderGetter<PlacedFeature> $$7 = $$0.lookup(Registries.PLACED_FEATURE);
        Holder.Reference<PlacedFeature> $$8 = $$7.getOrThrow(TreePlacements.PALE_OAK_CHECKED);
        Holder.Reference<PlacedFeature> $$9 = $$7.getOrThrow(TreePlacements.PALE_OAK_CREAKING_CHECKED);
        Holder.Reference<PlacedFeature> $$10 = $$7.getOrThrow(TreePlacements.FANCY_OAK_CHECKED);
        Holder.Reference<PlacedFeature> $$11 = $$7.getOrThrow(TreePlacements.BIRCH_BEES_002);
        Holder.Reference<PlacedFeature> $$12 = $$7.getOrThrow(TreePlacements.FANCY_OAK_BEES_002);
        Holder.Reference<PlacedFeature> $$13 = $$7.getOrThrow(TreePlacements.FANCY_OAK_BEES);
        Holder.Reference<PlacedFeature> $$14 = $$7.getOrThrow(TreePlacements.PINE_CHECKED);
        Holder.Reference<PlacedFeature> $$15 = $$7.getOrThrow(TreePlacements.SPRUCE_CHECKED);
        Holder.Reference<PlacedFeature> $$16 = $$7.getOrThrow(TreePlacements.PINE_ON_SNOW);
        Holder.Reference<PlacedFeature> $$17 = $$7.getOrThrow(TreePlacements.ACACIA_CHECKED);
        Holder.Reference<PlacedFeature> $$18 = $$7.getOrThrow(TreePlacements.SUPER_BIRCH_BEES_0002);
        Holder.Reference<PlacedFeature> $$19 = $$7.getOrThrow(TreePlacements.BIRCH_BEES_0002_PLACED);
        Holder.Reference<PlacedFeature> $$20 = $$7.getOrThrow(TreePlacements.BIRCH_BEES_0002_LEAF_LITTER);
        Holder.Reference<PlacedFeature> $$21 = $$7.getOrThrow(TreePlacements.FANCY_OAK_BEES_0002_LEAF_LITTER);
        Holder.Reference<PlacedFeature> $$22 = $$7.getOrThrow(TreePlacements.JUNGLE_BUSH);
        Holder.Reference<PlacedFeature> $$23 = $$7.getOrThrow(TreePlacements.MEGA_SPRUCE_CHECKED);
        Holder.Reference<PlacedFeature> $$24 = $$7.getOrThrow(TreePlacements.MEGA_PINE_CHECKED);
        Holder.Reference<PlacedFeature> $$25 = $$7.getOrThrow(TreePlacements.MEGA_JUNGLE_TREE_CHECKED);
        Holder.Reference<PlacedFeature> $$26 = $$7.getOrThrow(TreePlacements.TALL_MANGROVE_CHECKED);
        Holder.Reference<PlacedFeature> $$27 = $$7.getOrThrow(TreePlacements.OAK_CHECKED);
        Holder.Reference<PlacedFeature> $$28 = $$7.getOrThrow(TreePlacements.OAK_BEES_002);
        Holder.Reference<PlacedFeature> $$29 = $$7.getOrThrow(TreePlacements.SUPER_BIRCH_BEES);
        Holder.Reference<PlacedFeature> $$30 = $$7.getOrThrow(TreePlacements.SPRUCE_ON_SNOW);
        Holder.Reference<PlacedFeature> $$31 = $$7.getOrThrow(TreePlacements.OAK_BEES_0002_LEAF_LITTER);
        Holder.Reference<PlacedFeature> $$32 = $$7.getOrThrow(TreePlacements.JUNGLE_TREE_CHECKED);
        Holder.Reference<PlacedFeature> $$33 = $$7.getOrThrow(TreePlacements.MANGROVE_CHECKED);
        Holder.Reference<PlacedFeature> $$34 = $$7.getOrThrow(TreePlacements.OAK_LEAF_LITTER);
        Holder.Reference<PlacedFeature> $$35 = $$7.getOrThrow(TreePlacements.DARK_OAK_LEAF_LITTER);
        Holder.Reference<PlacedFeature> $$36 = $$7.getOrThrow(TreePlacements.BIRCH_LEAF_LITTER);
        Holder.Reference<PlacedFeature> $$37 = $$7.getOrThrow(TreePlacements.FANCY_OAK_LEAF_LITTER);
        Holder.Reference<PlacedFeature> $$38 = $$7.getOrThrow(TreePlacements.FALLEN_OAK_TREE);
        Holder.Reference<PlacedFeature> $$39 = $$7.getOrThrow(TreePlacements.FALLEN_BIRCH_TREE);
        Holder.Reference<PlacedFeature> $$40 = $$7.getOrThrow(TreePlacements.FALLEN_SUPER_BIRCH_TREE);
        Holder.Reference<PlacedFeature> $$41 = $$7.getOrThrow(TreePlacements.FALLEN_JUNGLE_TREE);
        Holder.Reference<PlacedFeature> $$42 = $$7.getOrThrow(TreePlacements.FALLEN_SPRUCE_TREE);
        FeatureUtils.register($$0, BAMBOO_NO_PODZOL, Feature.BAMBOO, new ProbabilityFeatureConfiguration(0.0f));
        FeatureUtils.register($$0, BAMBOO_SOME_PODZOL, Feature.BAMBOO, new ProbabilityFeatureConfiguration(0.2f));
        FeatureUtils.register($$0, VINES, Feature.VINES);
        FeatureUtils.register($$0, PATCH_BROWN_MUSHROOM, Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.BROWN_MUSHROOM))));
        FeatureUtils.register($$0, PATCH_RED_MUSHROOM, Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.RED_MUSHROOM))));
        FeatureUtils.register($$0, PATCH_SUNFLOWER, Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SUNFLOWER))));
        FeatureUtils.register($$0, PATCH_PUMPKIN, Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.PUMPKIN)), List.of((Object)Blocks.GRASS_BLOCK)));
        FeatureUtils.register($$0, PATCH_BERRY_BUSH, Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple((BlockState)Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3))), List.of((Object)Blocks.GRASS_BLOCK)));
        FeatureUtils.register($$0, PATCH_TAIGA_GRASS, Feature.RANDOM_PATCH, VegetationFeatures.grassPatch(new WeightedStateProvider(WeightedList.builder().add(Blocks.SHORT_GRASS.defaultBlockState(), 1).add(Blocks.FERN.defaultBlockState(), 4)), 32));
        FeatureUtils.register($$0, PATCH_GRASS, Feature.RANDOM_PATCH, VegetationFeatures.grassPatch(BlockStateProvider.simple(Blocks.SHORT_GRASS), 32));
        FeatureUtils.register($$0, PATCH_GRASS_MEADOW, Feature.RANDOM_PATCH, VegetationFeatures.grassPatch(BlockStateProvider.simple(Blocks.SHORT_GRASS), 16));
        FeatureUtils.register($$0, PATCH_LEAF_LITTER, Feature.RANDOM_PATCH, FeatureUtils.simpleRandomPatchConfiguration(32, PlacementUtils.filtered(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(VegetationFeatures.leafLitterPatchBuilder(1, 3))), BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.a(Direction.DOWN.getUnitVec3i(), Blocks.GRASS_BLOCK)))));
        FeatureUtils.register($$0, PATCH_GRASS_JUNGLE, Feature.RANDOM_PATCH, new RandomPatchConfiguration(32, 7, 3, PlacementUtils.filtered(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(WeightedList.builder().add(Blocks.SHORT_GRASS.defaultBlockState(), 3).add(Blocks.FERN.defaultBlockState(), 1))), BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.not(BlockPredicate.a(Direction.DOWN.getUnitVec3i(), Blocks.PODZOL))))));
        FeatureUtils.register($$0, SINGLE_PIECE_OF_GRASS, Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SHORT_GRASS.defaultBlockState())));
        FeatureUtils.register($$0, PATCH_DEAD_BUSH, Feature.RANDOM_PATCH, VegetationFeatures.grassPatch(BlockStateProvider.simple(Blocks.DEAD_BUSH), 4));
        FeatureUtils.register($$0, PATCH_DRY_GRASS, Feature.RANDOM_PATCH, VegetationFeatures.grassPatch(new WeightedStateProvider(WeightedList.builder().add(Blocks.SHORT_DRY_GRASS.defaultBlockState(), 1).add(Blocks.TALL_DRY_GRASS.defaultBlockState(), 1)), 64));
        FeatureUtils.register($$0, PATCH_MELON, Feature.RANDOM_PATCH, new RandomPatchConfiguration(64, 7, 3, PlacementUtils.filtered(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.MELON)), BlockPredicate.a(BlockPredicate.replaceable(), BlockPredicate.noFluid(), BlockPredicate.a(Direction.DOWN.getUnitVec3i(), Blocks.GRASS_BLOCK)))));
        FeatureUtils.register($$0, PATCH_WATERLILY, Feature.RANDOM_PATCH, new RandomPatchConfiguration(10, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.LILY_PAD)))));
        FeatureUtils.register($$0, PATCH_TALL_GRASS, Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.TALL_GRASS))));
        FeatureUtils.register($$0, PATCH_LARGE_FERN, Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.LARGE_FERN))));
        FeatureUtils.register($$0, PATCH_BUSH, Feature.RANDOM_PATCH, new RandomPatchConfiguration(24, 5, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.BUSH)))));
        FeatureUtils.register($$0, PATCH_CACTUS, Feature.RANDOM_PATCH, FeatureUtils.simpleRandomPatchConfiguration(10, PlacementUtils.a(Feature.BLOCK_COLUMN, new BlockColumnConfiguration(List.of((Object)((Object)BlockColumnConfiguration.layer(BiasedToBottomInt.of(1, 3), BlockStateProvider.simple(Blocks.CACTUS))), (Object)((Object)BlockColumnConfiguration.layer(new WeightedListInt(WeightedList.builder().add(ConstantInt.of(0), 3).add(ConstantInt.of(1), 1).build()), BlockStateProvider.simple(Blocks.CACTUS_FLOWER)))), Direction.UP, BlockPredicate.ONLY_IN_AIR_PREDICATE, false), BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.wouldSurvive(Blocks.CACTUS.defaultBlockState(), BlockPos.ZERO))))));
        FeatureUtils.register($$0, PATCH_SUGAR_CANE, Feature.RANDOM_PATCH, new RandomPatchConfiguration(20, 4, 0, PlacementUtils.a(Feature.BLOCK_COLUMN, BlockColumnConfiguration.simple(BiasedToBottomInt.of(2, 4), BlockStateProvider.simple(Blocks.SUGAR_CANE)), VegetationFeatures.nearWaterPredicate(Blocks.SUGAR_CANE))));
        FeatureUtils.register($$0, PATCH_FIREFLY_BUSH, Feature.RANDOM_PATCH, new RandomPatchConfiguration(20, 4, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.FIREFLY_BUSH)))));
        FeatureUtils.register($$0, FLOWER_DEFAULT, Feature.FLOWER, VegetationFeatures.grassPatch(new WeightedStateProvider(WeightedList.builder().add(Blocks.POPPY.defaultBlockState(), 2).add(Blocks.DANDELION.defaultBlockState(), 1)), 64));
        FeatureUtils.register($$0, FLOWER_FLOWER_FOREST, Feature.FLOWER, new RandomPatchConfiguration(96, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new NoiseProvider(2345L, new NormalNoise.NoiseParameters(0, 1.0, new double[0]), 0.020833334f, List.of((Object[])new BlockState[]{Blocks.DANDELION.defaultBlockState(), Blocks.POPPY.defaultBlockState(), Blocks.ALLIUM.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.RED_TULIP.defaultBlockState(), Blocks.ORANGE_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState(), Blocks.LILY_OF_THE_VALLEY.defaultBlockState()}))))));
        FeatureUtils.register($$0, FLOWER_SWAMP, Feature.FLOWER, new RandomPatchConfiguration(64, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.BLUE_ORCHID)))));
        FeatureUtils.register($$0, FLOWER_PLAIN, Feature.FLOWER, new RandomPatchConfiguration(64, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new NoiseThresholdProvider(2345L, new NormalNoise.NoiseParameters(0, 1.0, new double[0]), 0.005f, -0.8f, 0.33333334f, Blocks.DANDELION.defaultBlockState(), List.of((Object)Blocks.ORANGE_TULIP.defaultBlockState(), (Object)Blocks.RED_TULIP.defaultBlockState(), (Object)Blocks.PINK_TULIP.defaultBlockState(), (Object)Blocks.WHITE_TULIP.defaultBlockState()), List.of((Object)Blocks.POPPY.defaultBlockState(), (Object)Blocks.AZURE_BLUET.defaultBlockState(), (Object)Blocks.OXEYE_DAISY.defaultBlockState(), (Object)Blocks.CORNFLOWER.defaultBlockState()))))));
        FeatureUtils.register($$0, FLOWER_MEADOW, Feature.FLOWER, new RandomPatchConfiguration(96, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new DualNoiseProvider(new InclusiveRange<Integer>(1, 3), new NormalNoise.NoiseParameters(-10, 1.0, new double[0]), 1.0f, 2345L, new NormalNoise.NoiseParameters(-3, 1.0, new double[0]), 1.0f, List.of((Object)Blocks.TALL_GRASS.defaultBlockState(), (Object)Blocks.ALLIUM.defaultBlockState(), (Object)Blocks.POPPY.defaultBlockState(), (Object)Blocks.AZURE_BLUET.defaultBlockState(), (Object)Blocks.DANDELION.defaultBlockState(), (Object)Blocks.CORNFLOWER.defaultBlockState(), (Object)Blocks.OXEYE_DAISY.defaultBlockState(), (Object)Blocks.SHORT_GRASS.defaultBlockState()))))));
        FeatureUtils.register($$0, FLOWER_CHERRY, Feature.FLOWER, new RandomPatchConfiguration(96, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(VegetationFeatures.flowerBedPatchBuilder(Blocks.PINK_PETALS))))));
        FeatureUtils.register($$0, WILDFLOWERS_BIRCH_FOREST, Feature.FLOWER, new RandomPatchConfiguration(64, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(VegetationFeatures.flowerBedPatchBuilder(Blocks.WILDFLOWERS))))));
        FeatureUtils.register($$0, WILDFLOWERS_MEADOW, Feature.FLOWER, new RandomPatchConfiguration(8, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(VegetationFeatures.flowerBedPatchBuilder(Blocks.WILDFLOWERS))))));
        FeatureUtils.register($$0, FLOWER_PALE_GARDEN, Feature.FLOWER, new RandomPatchConfiguration(1, 0, 0, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.CLOSED_EYEBLOSSOM), true))));
        FeatureUtils.register($$0, FOREST_FLOWERS, Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.a(PlacementUtils.a(Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.LILAC))), new PlacementModifier[0]), PlacementUtils.a(Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.ROSE_BUSH))), new PlacementModifier[0]), PlacementUtils.a(Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.PEONY))), new PlacementModifier[0]), PlacementUtils.a(Feature.NO_BONEMEAL_FLOWER, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.LILY_OF_THE_VALLEY))), new PlacementModifier[0]))));
        FeatureUtils.register($$0, PALE_FOREST_FLOWERS, Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.CLOSED_EYEBLOSSOM), true)));
        FeatureUtils.register($$0, DARK_FOREST_VEGETATION, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature(PlacementUtils.a($$2, new PlacementModifier[0]), 0.025f), (Object)new WeightedPlacedFeature(PlacementUtils.a($$3, new PlacementModifier[0]), 0.05f), (Object)new WeightedPlacedFeature($$35, 0.6666667f), (Object)new WeightedPlacedFeature($$39, 0.0025f), (Object)new WeightedPlacedFeature($$36, 0.2f), (Object)new WeightedPlacedFeature($$38, 0.0125f), (Object)new WeightedPlacedFeature($$37, 0.1f)), $$34));
        FeatureUtils.register($$0, PALE_GARDEN_VEGETATION, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$9, 0.1f), (Object)new WeightedPlacedFeature($$8, 0.9f)), $$8));
        FeatureUtils.register($$0, PALE_MOSS_VEGETATION, Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(WeightedList.builder().add(Blocks.PALE_MOSS_CARPET.defaultBlockState(), 25).add(Blocks.SHORT_GRASS.defaultBlockState(), 25).add(Blocks.TALL_GRASS.defaultBlockState(), 10))));
        FeatureUtils.register($$0, PALE_MOSS_PATCH, Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(BlockTags.MOSS_REPLACEABLE, BlockStateProvider.simple(Blocks.PALE_MOSS_BLOCK), PlacementUtils.a($$1.getOrThrow(PALE_MOSS_VEGETATION), new PlacementModifier[0]), CaveSurface.FLOOR, ConstantInt.of(1), 0.0f, 5, 0.3f, UniformInt.of(2, 4), 0.75f));
        FeatureUtils.register($$0, PALE_MOSS_PATCH_BONEMEAL, Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(BlockTags.MOSS_REPLACEABLE, BlockStateProvider.simple(Blocks.PALE_MOSS_BLOCK), PlacementUtils.a($$1.getOrThrow(PALE_MOSS_VEGETATION), new PlacementModifier[0]), CaveSurface.FLOOR, ConstantInt.of(1), 0.0f, 5, 0.6f, UniformInt.of(1, 2), 0.75f));
        FeatureUtils.register($$0, TREES_FLOWER_FOREST, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$39, 0.0025f), (Object)new WeightedPlacedFeature($$11, 0.2f), (Object)new WeightedPlacedFeature($$12, 0.1f)), $$28));
        FeatureUtils.register($$0, MEADOW_TREES, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$13, 0.5f)), $$29));
        FeatureUtils.register($$0, TREES_TAIGA, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$14, 0.33333334f), (Object)new WeightedPlacedFeature($$42, 0.0125f)), $$15));
        FeatureUtils.register($$0, TREES_BADLANDS, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$38, 0.0125f)), $$34));
        FeatureUtils.register($$0, TREES_GROVE, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$16, 0.33333334f)), $$30));
        FeatureUtils.register($$0, TREES_SAVANNA, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$17, 0.8f), (Object)new WeightedPlacedFeature($$38, 0.0125f)), $$27));
        FeatureUtils.register($$0, TREES_SNOWY, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$42, 0.0125f)), $$15));
        FeatureUtils.register($$0, TREES_BIRCH, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$39, 0.0125f)), $$19));
        FeatureUtils.register($$0, BIRCH_TALL, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$40, 0.00625f), (Object)new WeightedPlacedFeature($$18, 0.5f), (Object)new WeightedPlacedFeature($$39, 0.0125f)), $$19));
        FeatureUtils.register($$0, TREES_WINDSWEPT_HILLS, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$42, 0.008325f), (Object)new WeightedPlacedFeature($$15, 0.666f), (Object)new WeightedPlacedFeature($$10, 0.1f), (Object)new WeightedPlacedFeature($$38, 0.0125f)), $$27));
        FeatureUtils.register($$0, TREES_WATER, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$10, 0.1f)), $$27));
        FeatureUtils.register($$0, TREES_BIRCH_AND_OAK_LEAF_LITTER, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$39, 0.0025f), (Object)new WeightedPlacedFeature($$20, 0.2f), (Object)new WeightedPlacedFeature($$21, 0.1f), (Object)new WeightedPlacedFeature($$38, 0.0125f)), $$31));
        FeatureUtils.register($$0, TREES_PLAINS, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature(PlacementUtils.a($$4, new PlacementModifier[0]), 0.33333334f), (Object)new WeightedPlacedFeature($$38, 0.0125f)), PlacementUtils.a($$5, new PlacementModifier[0])));
        FeatureUtils.register($$0, TREES_SPARSE_JUNGLE, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$10, 0.1f), (Object)new WeightedPlacedFeature($$22, 0.5f), (Object)new WeightedPlacedFeature($$41, 0.0125f)), $$32));
        FeatureUtils.register($$0, TREES_OLD_GROWTH_SPRUCE_TAIGA, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$23, 0.33333334f), (Object)new WeightedPlacedFeature($$14, 0.33333334f), (Object)new WeightedPlacedFeature($$42, 0.0125f)), $$15));
        FeatureUtils.register($$0, TREES_OLD_GROWTH_PINE_TAIGA, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$23, 0.025641026f), (Object)new WeightedPlacedFeature($$24, 0.30769232f), (Object)new WeightedPlacedFeature($$14, 0.33333334f), (Object)new WeightedPlacedFeature($$42, 0.0125f)), $$15));
        FeatureUtils.register($$0, TREES_JUNGLE, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$10, 0.1f), (Object)new WeightedPlacedFeature($$22, 0.5f), (Object)new WeightedPlacedFeature($$25, 0.33333334f), (Object)new WeightedPlacedFeature($$41, 0.0125f)), $$32));
        FeatureUtils.register($$0, BAMBOO_VEGETATION, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$10, 0.05f), (Object)new WeightedPlacedFeature($$22, 0.15f), (Object)new WeightedPlacedFeature($$25, 0.7f)), PlacementUtils.a($$6, new PlacementModifier[0])));
        FeatureUtils.register($$0, MUSHROOM_ISLAND_VEGETATION, Feature.RANDOM_BOOLEAN_SELECTOR, new RandomBooleanFeatureConfiguration(PlacementUtils.a($$3, new PlacementModifier[0]), PlacementUtils.a($$2, new PlacementModifier[0])));
        FeatureUtils.register($$0, MANGROVE_VEGETATION, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of((Object)new WeightedPlacedFeature($$26, 0.85f)), $$33));
    }

    private static WeightedList.Builder<BlockState> flowerBedPatchBuilder(Block $$0) {
        return VegetationFeatures.segmentedBlockPatchBuilder($$0, 1, 4, FlowerBedBlock.AMOUNT, FlowerBedBlock.FACING);
    }

    public static WeightedList.Builder<BlockState> leafLitterPatchBuilder(int $$0, int $$1) {
        return VegetationFeatures.segmentedBlockPatchBuilder(Blocks.LEAF_LITTER, $$0, $$1, LeafLitterBlock.AMOUNT, LeafLitterBlock.FACING);
    }

    private static WeightedList.Builder<BlockState> segmentedBlockPatchBuilder(Block $$0, int $$1, int $$2, IntegerProperty $$3, EnumProperty<Direction> $$4) {
        WeightedList.Builder<BlockState> $$5 = WeightedList.builder();
        for (int $$6 = $$1; $$6 <= $$2; ++$$6) {
            for (Direction $$7 : Direction.Plane.HORIZONTAL) {
                $$5.add((BlockState)((BlockState)$$0.defaultBlockState().setValue($$3, $$6)).setValue($$4, $$7), 1);
            }
        }
        return $$5;
    }

    public static BlockPredicateFilter nearWaterPredicate(Block $$0) {
        return BlockPredicateFilter.forPredicate(BlockPredicate.a(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.wouldSurvive($$0.defaultBlockState(), BlockPos.ZERO), BlockPredicate.b(BlockPredicate.a((Vec3i)new BlockPos(1, -1, 0), Fluids.WATER, Fluids.FLOWING_WATER), BlockPredicate.a((Vec3i)new BlockPos(-1, -1, 0), Fluids.WATER, Fluids.FLOWING_WATER), BlockPredicate.a((Vec3i)new BlockPos(0, -1, 1), Fluids.WATER, Fluids.FLOWING_WATER), BlockPredicate.a((Vec3i)new BlockPos(0, -1, -1), Fluids.WATER, Fluids.FLOWING_WATER))));
    }
}

