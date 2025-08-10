/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.BambooFeature;
import net.minecraft.world.level.levelgen.feature.BasaltColumnsFeature;
import net.minecraft.world.level.levelgen.feature.BasaltPillarFeature;
import net.minecraft.world.level.levelgen.feature.BlockBlobFeature;
import net.minecraft.world.level.levelgen.feature.BlockColumnFeature;
import net.minecraft.world.level.levelgen.feature.BlockPileFeature;
import net.minecraft.world.level.levelgen.feature.BlueIceFeature;
import net.minecraft.world.level.levelgen.feature.BonusChestFeature;
import net.minecraft.world.level.levelgen.feature.ChorusPlantFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.CoralClawFeature;
import net.minecraft.world.level.levelgen.feature.CoralMushroomFeature;
import net.minecraft.world.level.levelgen.feature.CoralTreeFeature;
import net.minecraft.world.level.levelgen.feature.DeltaFeature;
import net.minecraft.world.level.levelgen.feature.DesertWellFeature;
import net.minecraft.world.level.levelgen.feature.DiskFeature;
import net.minecraft.world.level.levelgen.feature.DripstoneClusterFeature;
import net.minecraft.world.level.levelgen.feature.EndGatewayFeature;
import net.minecraft.world.level.levelgen.feature.EndIslandFeature;
import net.minecraft.world.level.levelgen.feature.EndPlatformFeature;
import net.minecraft.world.level.levelgen.feature.FallenTreeFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.FillLayerFeature;
import net.minecraft.world.level.levelgen.feature.FossilFeature;
import net.minecraft.world.level.levelgen.feature.FossilFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.GeodeFeature;
import net.minecraft.world.level.levelgen.feature.GlowstoneFeature;
import net.minecraft.world.level.levelgen.feature.HugeBrownMushroomFeature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.HugeFungusFeature;
import net.minecraft.world.level.levelgen.feature.HugeRedMushroomFeature;
import net.minecraft.world.level.levelgen.feature.IceSpikeFeature;
import net.minecraft.world.level.levelgen.feature.IcebergFeature;
import net.minecraft.world.level.levelgen.feature.KelpFeature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.LargeDripstoneFeature;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.MultifaceGrowthFeature;
import net.minecraft.world.level.levelgen.feature.NetherForestVegetationFeature;
import net.minecraft.world.level.levelgen.feature.NoOpFeature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.PointedDripstoneFeature;
import net.minecraft.world.level.levelgen.feature.RandomBooleanSelectorFeature;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.ReplaceBlobsFeature;
import net.minecraft.world.level.levelgen.feature.ReplaceBlockFeature;
import net.minecraft.world.level.levelgen.feature.RootSystemFeature;
import net.minecraft.world.level.levelgen.feature.ScatteredOreFeature;
import net.minecraft.world.level.levelgen.feature.SculkPatchFeature;
import net.minecraft.world.level.levelgen.feature.SeaPickleFeature;
import net.minecraft.world.level.levelgen.feature.SeagrassFeature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.SimpleRandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.SpringFeature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.TwistingVinesFeature;
import net.minecraft.world.level.levelgen.feature.UnderwaterMagmaFeature;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.VinesFeature;
import net.minecraft.world.level.levelgen.feature.VoidStartPlatformFeature;
import net.minecraft.world.level.levelgen.feature.WaterloggedVegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.WeepingVinesFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DeltaFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FallenTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomBooleanFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SculkPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TwistingVinesConfig;
import net.minecraft.world.level.levelgen.feature.configurations.UnderwaterMagmaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public abstract class Feature<FC extends FeatureConfiguration> {
    public static final Feature<NoneFeatureConfiguration> NO_OP = Feature.register("no_op", new NoOpFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<TreeConfiguration> TREE = Feature.register("tree", new TreeFeature(TreeConfiguration.CODEC));
    public static final Feature<FallenTreeConfiguration> FALLEN_TREE = Feature.register("fallen_tree", new FallenTreeFeature(FallenTreeConfiguration.CODEC));
    public static final Feature<RandomPatchConfiguration> FLOWER = Feature.register("flower", new RandomPatchFeature(RandomPatchConfiguration.CODEC));
    public static final Feature<RandomPatchConfiguration> NO_BONEMEAL_FLOWER = Feature.register("no_bonemeal_flower", new RandomPatchFeature(RandomPatchConfiguration.CODEC));
    public static final Feature<RandomPatchConfiguration> RANDOM_PATCH = Feature.register("random_patch", new RandomPatchFeature(RandomPatchConfiguration.CODEC));
    public static final Feature<BlockPileConfiguration> BLOCK_PILE = Feature.register("block_pile", new BlockPileFeature(BlockPileConfiguration.CODEC));
    public static final Feature<SpringConfiguration> SPRING = Feature.register("spring_feature", new SpringFeature(SpringConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> CHORUS_PLANT = Feature.register("chorus_plant", new ChorusPlantFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<ReplaceBlockConfiguration> REPLACE_SINGLE_BLOCK = Feature.register("replace_single_block", new ReplaceBlockFeature(ReplaceBlockConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> VOID_START_PLATFORM = Feature.register("void_start_platform", new VoidStartPlatformFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> DESERT_WELL = Feature.register("desert_well", new DesertWellFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<FossilFeatureConfiguration> FOSSIL = Feature.register("fossil", new FossilFeature(FossilFeatureConfiguration.CODEC));
    public static final Feature<HugeMushroomFeatureConfiguration> HUGE_RED_MUSHROOM = Feature.register("huge_red_mushroom", new HugeRedMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));
    public static final Feature<HugeMushroomFeatureConfiguration> HUGE_BROWN_MUSHROOM = Feature.register("huge_brown_mushroom", new HugeBrownMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> ICE_SPIKE = Feature.register("ice_spike", new IceSpikeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> GLOWSTONE_BLOB = Feature.register("glowstone_blob", new GlowstoneFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> FREEZE_TOP_LAYER = Feature.register("freeze_top_layer", new SnowAndFreezeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> VINES = Feature.register("vines", new VinesFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<BlockColumnConfiguration> BLOCK_COLUMN = Feature.register("block_column", new BlockColumnFeature(BlockColumnConfiguration.CODEC));
    public static final Feature<VegetationPatchConfiguration> VEGETATION_PATCH = Feature.register("vegetation_patch", new VegetationPatchFeature(VegetationPatchConfiguration.CODEC));
    public static final Feature<VegetationPatchConfiguration> WATERLOGGED_VEGETATION_PATCH = Feature.register("waterlogged_vegetation_patch", new WaterloggedVegetationPatchFeature(VegetationPatchConfiguration.CODEC));
    public static final Feature<RootSystemConfiguration> ROOT_SYSTEM = Feature.register("root_system", new RootSystemFeature(RootSystemConfiguration.CODEC));
    public static final Feature<MultifaceGrowthConfiguration> MULTIFACE_GROWTH = Feature.register("multiface_growth", new MultifaceGrowthFeature(MultifaceGrowthConfiguration.CODEC));
    public static final Feature<UnderwaterMagmaConfiguration> UNDERWATER_MAGMA = Feature.register("underwater_magma", new UnderwaterMagmaFeature(UnderwaterMagmaConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> MONSTER_ROOM = Feature.register("monster_room", new MonsterRoomFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> BLUE_ICE = Feature.register("blue_ice", new BlueIceFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<BlockStateConfiguration> ICEBERG = Feature.register("iceberg", new IcebergFeature(BlockStateConfiguration.CODEC));
    public static final Feature<BlockStateConfiguration> FOREST_ROCK = Feature.register("forest_rock", new BlockBlobFeature(BlockStateConfiguration.CODEC));
    public static final Feature<DiskConfiguration> DISK = Feature.register("disk", new DiskFeature(DiskConfiguration.CODEC));
    public static final Feature<LakeFeature.Configuration> LAKE = Feature.register("lake", new LakeFeature(LakeFeature.Configuration.CODEC));
    public static final Feature<OreConfiguration> ORE = Feature.register("ore", new OreFeature(OreConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> END_PLATFORM = Feature.register("end_platform", new EndPlatformFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<SpikeConfiguration> END_SPIKE = Feature.register("end_spike", new SpikeFeature(SpikeConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> END_ISLAND = Feature.register("end_island", new EndIslandFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<EndGatewayConfiguration> END_GATEWAY = Feature.register("end_gateway", new EndGatewayFeature(EndGatewayConfiguration.CODEC));
    public static final SeagrassFeature SEAGRASS = Feature.register("seagrass", new SeagrassFeature(ProbabilityFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> KELP = Feature.register("kelp", new KelpFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> CORAL_TREE = Feature.register("coral_tree", new CoralTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> CORAL_MUSHROOM = Feature.register("coral_mushroom", new CoralMushroomFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> CORAL_CLAW = Feature.register("coral_claw", new CoralClawFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<CountConfiguration> SEA_PICKLE = Feature.register("sea_pickle", new SeaPickleFeature(CountConfiguration.CODEC));
    public static final Feature<SimpleBlockConfiguration> SIMPLE_BLOCK = Feature.register("simple_block", new SimpleBlockFeature(SimpleBlockConfiguration.CODEC));
    public static final Feature<ProbabilityFeatureConfiguration> BAMBOO = Feature.register("bamboo", new BambooFeature(ProbabilityFeatureConfiguration.CODEC));
    public static final Feature<HugeFungusConfiguration> HUGE_FUNGUS = Feature.register("huge_fungus", new HugeFungusFeature(HugeFungusConfiguration.CODEC));
    public static final Feature<NetherForestVegetationConfig> NETHER_FOREST_VEGETATION = Feature.register("nether_forest_vegetation", new NetherForestVegetationFeature(NetherForestVegetationConfig.CODEC));
    public static final Feature<NoneFeatureConfiguration> WEEPING_VINES = Feature.register("weeping_vines", new WeepingVinesFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<TwistingVinesConfig> TWISTING_VINES = Feature.register("twisting_vines", new TwistingVinesFeature(TwistingVinesConfig.CODEC));
    public static final Feature<ColumnFeatureConfiguration> BASALT_COLUMNS = Feature.register("basalt_columns", new BasaltColumnsFeature(ColumnFeatureConfiguration.CODEC));
    public static final Feature<DeltaFeatureConfiguration> DELTA_FEATURE = Feature.register("delta_feature", new DeltaFeature(DeltaFeatureConfiguration.CODEC));
    public static final Feature<ReplaceSphereConfiguration> REPLACE_BLOBS = Feature.register("netherrack_replace_blobs", new ReplaceBlobsFeature(ReplaceSphereConfiguration.CODEC));
    public static final Feature<LayerConfiguration> FILL_LAYER = Feature.register("fill_layer", new FillLayerFeature(LayerConfiguration.CODEC));
    public static final BonusChestFeature BONUS_CHEST = Feature.register("bonus_chest", new BonusChestFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<NoneFeatureConfiguration> BASALT_PILLAR = Feature.register("basalt_pillar", new BasaltPillarFeature(NoneFeatureConfiguration.CODEC));
    public static final Feature<OreConfiguration> SCATTERED_ORE = Feature.register("scattered_ore", new ScatteredOreFeature(OreConfiguration.CODEC));
    public static final Feature<RandomFeatureConfiguration> RANDOM_SELECTOR = Feature.register("random_selector", new RandomSelectorFeature(RandomFeatureConfiguration.CODEC));
    public static final Feature<SimpleRandomFeatureConfiguration> SIMPLE_RANDOM_SELECTOR = Feature.register("simple_random_selector", new SimpleRandomSelectorFeature(SimpleRandomFeatureConfiguration.CODEC));
    public static final Feature<RandomBooleanFeatureConfiguration> RANDOM_BOOLEAN_SELECTOR = Feature.register("random_boolean_selector", new RandomBooleanSelectorFeature(RandomBooleanFeatureConfiguration.CODEC));
    public static final Feature<GeodeConfiguration> GEODE = Feature.register("geode", new GeodeFeature(GeodeConfiguration.CODEC));
    public static final Feature<DripstoneClusterConfiguration> DRIPSTONE_CLUSTER = Feature.register("dripstone_cluster", new DripstoneClusterFeature(DripstoneClusterConfiguration.CODEC));
    public static final Feature<LargeDripstoneConfiguration> LARGE_DRIPSTONE = Feature.register("large_dripstone", new LargeDripstoneFeature(LargeDripstoneConfiguration.CODEC));
    public static final Feature<PointedDripstoneConfiguration> POINTED_DRIPSTONE = Feature.register("pointed_dripstone", new PointedDripstoneFeature(PointedDripstoneConfiguration.CODEC));
    public static final Feature<SculkPatchConfiguration> SCULK_PATCH = Feature.register("sculk_patch", new SculkPatchFeature(SculkPatchConfiguration.CODEC));
    private final MapCodec<ConfiguredFeature<FC, Feature<FC>>> configuredCodec;

    private static <C extends FeatureConfiguration, F extends Feature<C>> F register(String $$0, F $$1) {
        return (F)Registry.register(BuiltInRegistries.FEATURE, $$0, $$1);
    }

    public Feature(Codec<FC> $$02) {
        this.configuredCodec = $$02.fieldOf("config").xmap($$0 -> new ConfiguredFeature<FeatureConfiguration, Feature>(this, (FeatureConfiguration)$$0), ConfiguredFeature::config);
    }

    public MapCodec<ConfiguredFeature<FC, Feature<FC>>> configuredCodec() {
        return this.configuredCodec;
    }

    protected void setBlock(LevelWriter $$0, BlockPos $$1, BlockState $$2) {
        $$0.setBlock($$1, $$2, 3);
    }

    public static Predicate<BlockState> isReplaceable(TagKey<Block> $$0) {
        return $$1 -> !$$1.is($$0);
    }

    protected void safeSetBlock(WorldGenLevel $$0, BlockPos $$1, BlockState $$2, Predicate<BlockState> $$3) {
        if ($$3.test($$0.getBlockState($$1))) {
            $$0.setBlock($$1, $$2, 2);
        }
    }

    public abstract boolean place(FeaturePlaceContext<FC> var1);

    public boolean place(FC $$0, WorldGenLevel $$1, ChunkGenerator $$2, RandomSource $$3, BlockPos $$4) {
        if ($$1.ensureCanWrite($$4)) {
            return this.place(new FeaturePlaceContext<FC>(Optional.empty(), $$1, $$2, $$3, $$4, $$0));
        }
        return false;
    }

    protected static boolean isStone(BlockState $$0) {
        return $$0.is(BlockTags.BASE_STONE_OVERWORLD);
    }

    public static boolean isDirt(BlockState $$0) {
        return $$0.is(BlockTags.DIRT);
    }

    public static boolean isGrassOrDirt(LevelSimulatedReader $$0, BlockPos $$1) {
        return $$0.isStateAtPosition($$1, Feature::isDirt);
    }

    public static boolean checkNeighbors(Function<BlockPos, BlockState> $$0, BlockPos $$1, Predicate<BlockState> $$2) {
        BlockPos.MutableBlockPos $$3 = new BlockPos.MutableBlockPos();
        for (Direction $$4 : Direction.values()) {
            $$3.setWithOffset((Vec3i)$$1, $$4);
            if (!$$2.test($$0.apply($$3))) continue;
            return true;
        }
        return false;
    }

    public static boolean isAdjacentToAir(Function<BlockPos, BlockState> $$0, BlockPos $$1) {
        return Feature.checkNeighbors($$0, $$1, BlockBehaviour.BlockStateBase::isAir);
    }

    protected void markAboveForPostProcessing(WorldGenLevel $$0, BlockPos $$1) {
        BlockPos.MutableBlockPos $$2 = $$1.mutable();
        for (int $$3 = 0; $$3 < 2; ++$$3) {
            $$2.move(Direction.UP);
            if ($$0.getBlockState($$2).isAir()) {
                return;
            }
            $$0.getChunk($$2).markPosForPostprocessing($$2);
        }
    }
}

