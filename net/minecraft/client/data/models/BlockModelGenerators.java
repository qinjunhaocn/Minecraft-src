/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.MatchException
 */
package net.minecraft.client.data.models;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quadrant;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.client.renderer.block.model.multipart.CombinedCondition;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.special.BannerSpecialRenderer;
import net.minecraft.client.renderer.special.BedSpecialRenderer;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.special.ConduitSpecialRenderer;
import net.minecraft.client.renderer.special.DecoratedPotSpecialRenderer;
import net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer;
import net.minecraft.client.renderer.special.ShulkerBoxSpecialRenderer;
import net.minecraft.client.renderer.special.SkullSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.DriedGhastBlock;
import net.minecraft.world.level.block.HangingMossBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.MossyCarpetBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.SnifferEggBlock;
import net.minecraft.world.level.block.TestBlock;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.block.entity.vault.VaultState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.CreakingHeartState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.TestBlockMode;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.level.block.state.properties.WallSide;

public class BlockModelGenerators {
    final Consumer<BlockModelDefinitionGenerator> blockStateOutput;
    final ItemModelOutput itemModelOutput;
    final BiConsumer<ResourceLocation, ModelInstance> modelOutput;
    static final List<Block> NON_ORIENTABLE_TRAPDOOR = List.of((Object)Blocks.OAK_TRAPDOOR, (Object)Blocks.DARK_OAK_TRAPDOOR, (Object)Blocks.IRON_TRAPDOOR);
    public static final VariantMutator NOP = $$0 -> $$0;
    public static final VariantMutator UV_LOCK = VariantMutator.UV_LOCK.withValue(true);
    public static final VariantMutator X_ROT_90 = VariantMutator.X_ROT.withValue(Quadrant.R90);
    public static final VariantMutator X_ROT_180 = VariantMutator.X_ROT.withValue(Quadrant.R180);
    public static final VariantMutator X_ROT_270 = VariantMutator.X_ROT.withValue(Quadrant.R270);
    public static final VariantMutator Y_ROT_90 = VariantMutator.Y_ROT.withValue(Quadrant.R90);
    public static final VariantMutator Y_ROT_180 = VariantMutator.Y_ROT.withValue(Quadrant.R180);
    public static final VariantMutator Y_ROT_270 = VariantMutator.Y_ROT.withValue(Quadrant.R270);
    private static final Function<ConditionBuilder, ConditionBuilder> FLOWER_BED_MODEL_1_SEGMENT_CONDITION = $$0 -> $$0;
    private static final Function<ConditionBuilder, ConditionBuilder> FLOWER_BED_MODEL_2_SEGMENT_CONDITION = $$0 -> $$0.a(BlockStateProperties.FLOWER_AMOUNT, Integer.valueOf(2), new Integer[]{3, 4});
    private static final Function<ConditionBuilder, ConditionBuilder> FLOWER_BED_MODEL_3_SEGMENT_CONDITION = $$0 -> $$0.a(BlockStateProperties.FLOWER_AMOUNT, Integer.valueOf(3), new Integer[]{4});
    private static final Function<ConditionBuilder, ConditionBuilder> FLOWER_BED_MODEL_4_SEGMENT_CONDITION = $$0 -> $$0.term(BlockStateProperties.FLOWER_AMOUNT, 4);
    private static final Function<ConditionBuilder, ConditionBuilder> LEAF_LITTER_MODEL_1_SEGMENT_CONDITION = $$0 -> $$0.term(BlockStateProperties.SEGMENT_AMOUNT, 1);
    private static final Function<ConditionBuilder, ConditionBuilder> LEAF_LITTER_MODEL_2_SEGMENT_CONDITION = $$0 -> $$0.a(BlockStateProperties.SEGMENT_AMOUNT, Integer.valueOf(2), new Integer[]{3});
    private static final Function<ConditionBuilder, ConditionBuilder> LEAF_LITTER_MODEL_3_SEGMENT_CONDITION = $$0 -> $$0.term(BlockStateProperties.SEGMENT_AMOUNT, 3);
    private static final Function<ConditionBuilder, ConditionBuilder> LEAF_LITTER_MODEL_4_SEGMENT_CONDITION = $$0 -> $$0.term(BlockStateProperties.SEGMENT_AMOUNT, 4);
    static final Map<Block, BlockStateGeneratorSupplier> FULL_BLOCK_MODEL_CUSTOM_GENERATORS = Map.of((Object)Blocks.STONE, BlockModelGenerators::createMirroredCubeGenerator, (Object)Blocks.DEEPSLATE, BlockModelGenerators::createMirroredColumnGenerator, (Object)Blocks.MUD_BRICKS, BlockModelGenerators::createNorthWestMirroredCubeGenerator);
    private static final PropertyDispatch<VariantMutator> ROTATION_FACING = PropertyDispatch.modify(BlockStateProperties.FACING).select(Direction.DOWN, X_ROT_90).select(Direction.UP, X_ROT_270).select(Direction.NORTH, NOP).select(Direction.SOUTH, Y_ROT_180).select(Direction.WEST, Y_ROT_270).select(Direction.EAST, Y_ROT_90);
    private static final PropertyDispatch<VariantMutator> ROTATIONS_COLUMN_WITH_FACING = PropertyDispatch.modify(BlockStateProperties.FACING).select(Direction.DOWN, X_ROT_180).select(Direction.UP, NOP).select(Direction.NORTH, X_ROT_90).select(Direction.SOUTH, X_ROT_90.then(Y_ROT_180)).select(Direction.WEST, X_ROT_90.then(Y_ROT_270)).select(Direction.EAST, X_ROT_90.then(Y_ROT_90));
    private static final PropertyDispatch<VariantMutator> ROTATION_TORCH = PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, NOP).select(Direction.SOUTH, Y_ROT_90).select(Direction.WEST, Y_ROT_180).select(Direction.NORTH, Y_ROT_270);
    private static final PropertyDispatch<VariantMutator> ROTATION_HORIZONTAL_FACING_ALT = PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.SOUTH, NOP).select(Direction.WEST, Y_ROT_90).select(Direction.NORTH, Y_ROT_180).select(Direction.EAST, Y_ROT_270);
    private static final PropertyDispatch<VariantMutator> ROTATION_HORIZONTAL_FACING = PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, Y_ROT_90).select(Direction.SOUTH, Y_ROT_180).select(Direction.WEST, Y_ROT_270).select(Direction.NORTH, NOP);
    static final Map<Block, TexturedModel> TEXTURED_MODELS = ImmutableMap.builder().put(Blocks.SANDSTONE, TexturedModel.TOP_BOTTOM_WITH_WALL.get(Blocks.SANDSTONE)).put(Blocks.RED_SANDSTONE, TexturedModel.TOP_BOTTOM_WITH_WALL.get(Blocks.RED_SANDSTONE)).put(Blocks.SMOOTH_SANDSTONE, TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.SANDSTONE, "_top"))).put(Blocks.SMOOTH_RED_SANDSTONE, TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.RED_SANDSTONE, "_top"))).put(Blocks.CUT_SANDSTONE, TexturedModel.COLUMN.get(Blocks.SANDSTONE).updateTextures($$0 -> $$0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CUT_SANDSTONE)))).put(Blocks.CUT_RED_SANDSTONE, TexturedModel.COLUMN.get(Blocks.RED_SANDSTONE).updateTextures($$0 -> $$0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CUT_RED_SANDSTONE)))).put(Blocks.QUARTZ_BLOCK, TexturedModel.COLUMN.get(Blocks.QUARTZ_BLOCK)).put(Blocks.SMOOTH_QUARTZ, TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.QUARTZ_BLOCK, "_bottom"))).put(Blocks.BLACKSTONE, TexturedModel.COLUMN_WITH_WALL.get(Blocks.BLACKSTONE)).put(Blocks.DEEPSLATE, TexturedModel.COLUMN_WITH_WALL.get(Blocks.DEEPSLATE)).put(Blocks.CHISELED_QUARTZ_BLOCK, TexturedModel.COLUMN.get(Blocks.CHISELED_QUARTZ_BLOCK).updateTextures($$0 -> $$0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_QUARTZ_BLOCK)))).put(Blocks.CHISELED_SANDSTONE, TexturedModel.COLUMN.get(Blocks.CHISELED_SANDSTONE).updateTextures($$0 -> {
        $$0.put(TextureSlot.END, TextureMapping.getBlockTexture(Blocks.SANDSTONE, "_top"));
        $$0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_SANDSTONE));
    })).put(Blocks.CHISELED_RED_SANDSTONE, TexturedModel.COLUMN.get(Blocks.CHISELED_RED_SANDSTONE).updateTextures($$0 -> {
        $$0.put(TextureSlot.END, TextureMapping.getBlockTexture(Blocks.RED_SANDSTONE, "_top"));
        $$0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_RED_SANDSTONE));
    })).put(Blocks.CHISELED_TUFF_BRICKS, TexturedModel.COLUMN_WITH_WALL.get(Blocks.CHISELED_TUFF_BRICKS)).put(Blocks.CHISELED_TUFF, TexturedModel.COLUMN_WITH_WALL.get(Blocks.CHISELED_TUFF)).build();
    static final Map<BlockFamily.Variant, BiConsumer<BlockFamilyProvider, Block>> SHAPE_CONSUMERS = ImmutableMap.builder().put(BlockFamily.Variant.BUTTON, BlockFamilyProvider::button).put(BlockFamily.Variant.DOOR, BlockFamilyProvider::door).put(BlockFamily.Variant.CHISELED, BlockFamilyProvider::fullBlockVariant).put(BlockFamily.Variant.CRACKED, BlockFamilyProvider::fullBlockVariant).put(BlockFamily.Variant.CUSTOM_FENCE, BlockFamilyProvider::customFence).put(BlockFamily.Variant.FENCE, BlockFamilyProvider::fence).put(BlockFamily.Variant.CUSTOM_FENCE_GATE, BlockFamilyProvider::customFenceGate).put(BlockFamily.Variant.FENCE_GATE, BlockFamilyProvider::fenceGate).put(BlockFamily.Variant.SIGN, BlockFamilyProvider::sign).put(BlockFamily.Variant.SLAB, BlockFamilyProvider::slab).put(BlockFamily.Variant.STAIRS, BlockFamilyProvider::stairs).put(BlockFamily.Variant.PRESSURE_PLATE, BlockFamilyProvider::pressurePlate).put(BlockFamily.Variant.TRAPDOOR, BlockFamilyProvider::trapdoor).put(BlockFamily.Variant.WALL, BlockFamilyProvider::wall).build();
    private static final Map<Direction, VariantMutator> MULTIFACE_GENERATOR = ImmutableMap.of((Object)Direction.NORTH, (Object)NOP, (Object)Direction.EAST, (Object)Y_ROT_90.then(UV_LOCK), (Object)Direction.SOUTH, (Object)Y_ROT_180.then(UV_LOCK), (Object)Direction.WEST, (Object)Y_ROT_270.then(UV_LOCK), (Object)Direction.UP, (Object)X_ROT_270.then(UV_LOCK), (Object)Direction.DOWN, (Object)X_ROT_90.then(UV_LOCK));
    private static final Map<BookSlotModelCacheKey, ResourceLocation> CHISELED_BOOKSHELF_SLOT_MODEL_CACHE = new HashMap<BookSlotModelCacheKey, ResourceLocation>();

    static Variant plainModel(ResourceLocation $$0) {
        return new Variant($$0);
    }

    static MultiVariant variant(Variant $$0) {
        return new MultiVariant(WeightedList.of($$0));
    }

    private static MultiVariant a(Variant ... $$02) {
        return new MultiVariant(WeightedList.of(Arrays.stream($$02).map($$0 -> new Weighted<Variant>((Variant)$$0, 1)).toList()));
    }

    static MultiVariant plainVariant(ResourceLocation $$0) {
        return BlockModelGenerators.variant(BlockModelGenerators.plainModel($$0));
    }

    private static ConditionBuilder condition() {
        return new ConditionBuilder();
    }

    private static Condition a(ConditionBuilder ... $$0) {
        return new CombinedCondition(CombinedCondition.Operation.OR, Stream.of($$0).map(ConditionBuilder::build).toList());
    }

    private static BlockModelDefinitionGenerator createMirroredCubeGenerator(Block $$0, Variant $$1, TextureMapping $$2, BiConsumer<ResourceLocation, ModelInstance> $$3) {
        Variant $$4 = BlockModelGenerators.plainModel(ModelTemplates.CUBE_MIRRORED_ALL.create($$0, $$2, $$3));
        return MultiVariantGenerator.dispatch($$0, BlockModelGenerators.createRotatedVariants($$1, $$4));
    }

    private static BlockModelDefinitionGenerator createNorthWestMirroredCubeGenerator(Block $$0, Variant $$1, TextureMapping $$2, BiConsumer<ResourceLocation, ModelInstance> $$3) {
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_NORTH_WEST_MIRRORED_ALL.create($$0, $$2, $$3));
        return BlockModelGenerators.createSimpleBlock($$0, $$4);
    }

    private static BlockModelDefinitionGenerator createMirroredColumnGenerator(Block $$0, Variant $$1, TextureMapping $$2, BiConsumer<ResourceLocation, ModelInstance> $$3) {
        Variant $$4 = BlockModelGenerators.plainModel(ModelTemplates.CUBE_COLUMN_MIRRORED.create($$0, $$2, $$3));
        return MultiVariantGenerator.dispatch($$0, BlockModelGenerators.createRotatedVariants($$1, $$4)).with(BlockModelGenerators.createRotatedPillar());
    }

    public BlockModelGenerators(Consumer<BlockModelDefinitionGenerator> $$0, ItemModelOutput $$1, BiConsumer<ResourceLocation, ModelInstance> $$2) {
        this.blockStateOutput = $$0;
        this.itemModelOutput = $$1;
        this.modelOutput = $$2;
    }

    private void registerSimpleItemModel(Item $$0, ResourceLocation $$1) {
        this.itemModelOutput.accept($$0, ItemModelUtils.plainModel($$1));
    }

    void registerSimpleItemModel(Block $$0, ResourceLocation $$1) {
        this.itemModelOutput.accept($$0.asItem(), ItemModelUtils.plainModel($$1));
    }

    private void registerSimpleTintedItemModel(Block $$0, ResourceLocation $$1, ItemTintSource $$2) {
        this.itemModelOutput.accept($$0.asItem(), ItemModelUtils.a($$1, $$2));
    }

    private ResourceLocation createFlatItemModel(Item $$0) {
        return ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation($$0), TextureMapping.layer0($$0), this.modelOutput);
    }

    ResourceLocation createFlatItemModelWithBlockTexture(Item $$0, Block $$1) {
        return ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation($$0), TextureMapping.layer0($$1), this.modelOutput);
    }

    private ResourceLocation createFlatItemModelWithBlockTexture(Item $$0, Block $$1, String $$2) {
        return ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation($$0), TextureMapping.layer0(TextureMapping.getBlockTexture($$1, $$2)), this.modelOutput);
    }

    ResourceLocation createFlatItemModelWithBlockTextureAndOverlay(Item $$0, Block $$1, String $$2) {
        ResourceLocation $$3 = TextureMapping.getBlockTexture($$1);
        ResourceLocation $$4 = TextureMapping.getBlockTexture($$1, $$2);
        return ModelTemplates.TWO_LAYERED_ITEM.create(ModelLocationUtils.getModelLocation($$0), TextureMapping.layered($$3, $$4), this.modelOutput);
    }

    void registerSimpleFlatItemModel(Item $$0) {
        this.registerSimpleItemModel($$0, this.createFlatItemModel($$0));
    }

    private void registerSimpleFlatItemModel(Block $$0) {
        Item $$1 = $$0.asItem();
        if ($$1 != Items.AIR) {
            this.registerSimpleItemModel($$1, this.createFlatItemModelWithBlockTexture($$1, $$0));
        }
    }

    private void registerSimpleFlatItemModel(Block $$0, String $$1) {
        Item $$2 = $$0.asItem();
        if ($$2 != Items.AIR) {
            this.registerSimpleItemModel($$2, this.createFlatItemModelWithBlockTexture($$2, $$0, $$1));
        }
    }

    private void registerTwoLayerFlatItemModel(Block $$0, String $$1) {
        Item $$2 = $$0.asItem();
        if ($$2 != Items.AIR) {
            ResourceLocation $$3 = this.createFlatItemModelWithBlockTextureAndOverlay($$2, $$0, $$1);
            this.registerSimpleItemModel($$2, $$3);
        }
    }

    private static MultiVariant createRotatedVariants(Variant $$0) {
        return BlockModelGenerators.a($$0, $$0.with(Y_ROT_90), $$0.with(Y_ROT_180), $$0.with(Y_ROT_270));
    }

    private static MultiVariant createRotatedVariants(Variant $$0, Variant $$1) {
        return BlockModelGenerators.a($$0, $$1, $$0.with(Y_ROT_180), $$1.with(Y_ROT_180));
    }

    private static PropertyDispatch<MultiVariant> createBooleanModelDispatch(BooleanProperty $$0, MultiVariant $$1, MultiVariant $$2) {
        return PropertyDispatch.initial($$0).select(true, $$1).select(false, $$2);
    }

    private void createRotatedMirroredVariantBlock(Block $$0) {
        Variant $$1 = BlockModelGenerators.plainModel(TexturedModel.CUBE.create($$0, this.modelOutput));
        Variant $$2 = BlockModelGenerators.plainModel(TexturedModel.CUBE_MIRRORED.create($$0, this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0, BlockModelGenerators.createRotatedVariants($$1, $$2)));
    }

    private void createRotatedVariantBlock(Block $$0) {
        Variant $$1 = BlockModelGenerators.plainModel(TexturedModel.CUBE.create($$0, this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0, BlockModelGenerators.createRotatedVariants($$1)));
    }

    private void createBrushableBlock(Block $$0) {
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.DUSTED).generate($$1 -> {
            String $$2 = "_" + $$1;
            ResourceLocation $$3 = TextureMapping.getBlockTexture($$0, $$2);
            ResourceLocation $$4 = ModelTemplates.CUBE_ALL.createWithSuffix($$0, $$2, new TextureMapping().put(TextureSlot.ALL, $$3), this.modelOutput);
            return BlockModelGenerators.plainVariant($$4);
        })));
        this.registerSimpleItemModel($$0, ModelLocationUtils.getModelLocation($$0, "_0"));
    }

    static BlockModelDefinitionGenerator createButton(Block $$0, MultiVariant $$1, MultiVariant $$2) {
        return MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.POWERED).select(false, $$1).select(true, $$2)).with(PropertyDispatch.modify(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.FLOOR, Direction.EAST, Y_ROT_90).select(AttachFace.FLOOR, Direction.WEST, Y_ROT_270).select(AttachFace.FLOOR, Direction.SOUTH, Y_ROT_180).select(AttachFace.FLOOR, Direction.NORTH, NOP).select(AttachFace.WALL, Direction.EAST, Y_ROT_90.then(X_ROT_90).then(UV_LOCK)).select(AttachFace.WALL, Direction.WEST, Y_ROT_270.then(X_ROT_90).then(UV_LOCK)).select(AttachFace.WALL, Direction.SOUTH, Y_ROT_180.then(X_ROT_90).then(UV_LOCK)).select(AttachFace.WALL, Direction.NORTH, X_ROT_90.then(UV_LOCK)).select(AttachFace.CEILING, Direction.EAST, Y_ROT_270.then(X_ROT_180)).select(AttachFace.CEILING, Direction.WEST, Y_ROT_90.then(X_ROT_180)).select(AttachFace.CEILING, Direction.SOUTH, X_ROT_180).select(AttachFace.CEILING, Direction.NORTH, Y_ROT_180.then(X_ROT_180)));
    }

    private static BlockModelDefinitionGenerator createDoor(Block $$0, MultiVariant $$1, MultiVariant $$2, MultiVariant $$3, MultiVariant $$4, MultiVariant $$5, MultiVariant $$6, MultiVariant $$7, MultiVariant $$8) {
        return MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.DOUBLE_BLOCK_HALF, BlockStateProperties.DOOR_HINGE, BlockStateProperties.OPEN).select(Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false, $$1).select(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false, $$1.with(Y_ROT_90)).select(Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false, $$1.with(Y_ROT_180)).select(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false, $$1.with(Y_ROT_270)).select(Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false, $$3).select(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false, $$3.with(Y_ROT_90)).select(Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false, $$3.with(Y_ROT_180)).select(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false, $$3.with(Y_ROT_270)).select(Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true, $$2.with(Y_ROT_90)).select(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true, $$2.with(Y_ROT_180)).select(Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true, $$2.with(Y_ROT_270)).select(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true, $$2).select(Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true, $$4.with(Y_ROT_270)).select(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true, $$4).select(Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true, $$4.with(Y_ROT_90)).select(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true, $$4.with(Y_ROT_180)).select(Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false, $$5).select(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false, $$5.with(Y_ROT_90)).select(Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false, $$5.with(Y_ROT_180)).select(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false, $$5.with(Y_ROT_270)).select(Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false, $$7).select(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false, $$7.with(Y_ROT_90)).select(Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false, $$7.with(Y_ROT_180)).select(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false, $$7.with(Y_ROT_270)).select(Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true, $$6.with(Y_ROT_90)).select(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true, $$6.with(Y_ROT_180)).select(Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true, $$6.with(Y_ROT_270)).select(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true, $$6).select(Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true, $$8.with(Y_ROT_270)).select(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true, $$8).select(Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true, $$8.with(Y_ROT_90)).select(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true, $$8.with(Y_ROT_180)));
    }

    static BlockModelDefinitionGenerator createCustomFence(Block $$0, MultiVariant $$1, MultiVariant $$2, MultiVariant $$3, MultiVariant $$4, MultiVariant $$5) {
        return MultiPartGenerator.multiPart($$0).with($$1).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true), $$2).with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, true), $$3).with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, true), $$4).with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, true), $$5);
    }

    static BlockModelDefinitionGenerator createFence(Block $$0, MultiVariant $$1, MultiVariant $$2) {
        return MultiPartGenerator.multiPart($$0).with($$1).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true), $$2.with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, true), $$2.with(Y_ROT_90).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, true), $$2.with(Y_ROT_180).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, true), $$2.with(Y_ROT_270).with(UV_LOCK));
    }

    static BlockModelDefinitionGenerator createWall(Block $$0, MultiVariant $$1, MultiVariant $$2, MultiVariant $$3) {
        return MultiPartGenerator.multiPart($$0).with(BlockModelGenerators.condition().term(BlockStateProperties.UP, true), $$1).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH_WALL, WallSide.LOW), $$2.with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.EAST_WALL, WallSide.LOW), $$2.with(Y_ROT_90).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.LOW), $$2.with(Y_ROT_180).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.WEST_WALL, WallSide.LOW), $$2.with(Y_ROT_270).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH_WALL, WallSide.TALL), $$3.with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.EAST_WALL, WallSide.TALL), $$3.with(Y_ROT_90).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.TALL), $$3.with(Y_ROT_180).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.WEST_WALL, WallSide.TALL), $$3.with(Y_ROT_270).with(UV_LOCK));
    }

    static BlockModelDefinitionGenerator createFenceGate(Block $$0, MultiVariant $$1, MultiVariant $$2, MultiVariant $$3, MultiVariant $$4, boolean $$5) {
        return MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.IN_WALL, BlockStateProperties.OPEN).select(false, false, $$2).select(true, false, $$4).select(false, true, $$1).select(true, true, $$3)).with($$5 ? UV_LOCK : NOP).with(ROTATION_HORIZONTAL_FACING_ALT);
    }

    static BlockModelDefinitionGenerator createStairs(Block $$0, MultiVariant $$1, MultiVariant $$2, MultiVariant $$3) {
        return MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE).select(Direction.EAST, Half.BOTTOM, StairsShape.STRAIGHT, $$2).select(Direction.WEST, Half.BOTTOM, StairsShape.STRAIGHT, $$2.with(Y_ROT_180).with(UV_LOCK)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.STRAIGHT, $$2.with(Y_ROT_90).with(UV_LOCK)).select(Direction.NORTH, Half.BOTTOM, StairsShape.STRAIGHT, $$2.with(Y_ROT_270).with(UV_LOCK)).select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_RIGHT, $$3).select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_RIGHT, $$3.with(Y_ROT_180).with(UV_LOCK)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, $$3.with(Y_ROT_90).with(UV_LOCK)).select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, $$3.with(Y_ROT_270).with(UV_LOCK)).select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_LEFT, $$3.with(Y_ROT_270).with(UV_LOCK)).select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_LEFT, $$3.with(Y_ROT_90).with(UV_LOCK)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_LEFT, $$3).select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_LEFT, $$3.with(Y_ROT_180).with(UV_LOCK)).select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_RIGHT, $$1).select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_RIGHT, $$1.with(Y_ROT_180).with(UV_LOCK)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_RIGHT, $$1.with(Y_ROT_90).with(UV_LOCK)).select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_RIGHT, $$1.with(Y_ROT_270).with(UV_LOCK)).select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_LEFT, $$1.with(Y_ROT_270).with(UV_LOCK)).select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_LEFT, $$1.with(Y_ROT_90).with(UV_LOCK)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_LEFT, $$1).select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_LEFT, $$1.with(Y_ROT_180).with(UV_LOCK)).select(Direction.EAST, Half.TOP, StairsShape.STRAIGHT, $$2.with(X_ROT_180).with(UV_LOCK)).select(Direction.WEST, Half.TOP, StairsShape.STRAIGHT, $$2.with(X_ROT_180).with(Y_ROT_180).with(UV_LOCK)).select(Direction.SOUTH, Half.TOP, StairsShape.STRAIGHT, $$2.with(X_ROT_180).with(Y_ROT_90).with(UV_LOCK)).select(Direction.NORTH, Half.TOP, StairsShape.STRAIGHT, $$2.with(X_ROT_180).with(Y_ROT_270).with(UV_LOCK)).select(Direction.EAST, Half.TOP, StairsShape.OUTER_RIGHT, $$3.with(X_ROT_180).with(Y_ROT_90).with(UV_LOCK)).select(Direction.WEST, Half.TOP, StairsShape.OUTER_RIGHT, $$3.with(X_ROT_180).with(Y_ROT_270).with(UV_LOCK)).select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_RIGHT, $$3.with(X_ROT_180).with(Y_ROT_180).with(UV_LOCK)).select(Direction.NORTH, Half.TOP, StairsShape.OUTER_RIGHT, $$3.with(X_ROT_180).with(UV_LOCK)).select(Direction.EAST, Half.TOP, StairsShape.OUTER_LEFT, $$3.with(X_ROT_180).with(UV_LOCK)).select(Direction.WEST, Half.TOP, StairsShape.OUTER_LEFT, $$3.with(X_ROT_180).with(Y_ROT_180).with(UV_LOCK)).select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_LEFT, $$3.with(X_ROT_180).with(Y_ROT_90).with(UV_LOCK)).select(Direction.NORTH, Half.TOP, StairsShape.OUTER_LEFT, $$3.with(X_ROT_180).with(Y_ROT_270).with(UV_LOCK)).select(Direction.EAST, Half.TOP, StairsShape.INNER_RIGHT, $$1.with(X_ROT_180).with(Y_ROT_90).with(UV_LOCK)).select(Direction.WEST, Half.TOP, StairsShape.INNER_RIGHT, $$1.with(X_ROT_180).with(Y_ROT_270).with(UV_LOCK)).select(Direction.SOUTH, Half.TOP, StairsShape.INNER_RIGHT, $$1.with(X_ROT_180).with(Y_ROT_180).with(UV_LOCK)).select(Direction.NORTH, Half.TOP, StairsShape.INNER_RIGHT, $$1.with(X_ROT_180).with(UV_LOCK)).select(Direction.EAST, Half.TOP, StairsShape.INNER_LEFT, $$1.with(X_ROT_180).with(UV_LOCK)).select(Direction.WEST, Half.TOP, StairsShape.INNER_LEFT, $$1.with(X_ROT_180).with(Y_ROT_180).with(UV_LOCK)).select(Direction.SOUTH, Half.TOP, StairsShape.INNER_LEFT, $$1.with(X_ROT_180).with(Y_ROT_90).with(UV_LOCK)).select(Direction.NORTH, Half.TOP, StairsShape.INNER_LEFT, $$1.with(X_ROT_180).with(Y_ROT_270).with(UV_LOCK)));
    }

    private static BlockModelDefinitionGenerator createOrientableTrapdoor(Block $$0, MultiVariant $$1, MultiVariant $$2, MultiVariant $$3) {
        return MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN).select(Direction.NORTH, Half.BOTTOM, false, $$2).select(Direction.SOUTH, Half.BOTTOM, false, $$2.with(Y_ROT_180)).select(Direction.EAST, Half.BOTTOM, false, $$2.with(Y_ROT_90)).select(Direction.WEST, Half.BOTTOM, false, $$2.with(Y_ROT_270)).select(Direction.NORTH, Half.TOP, false, $$1).select(Direction.SOUTH, Half.TOP, false, $$1.with(Y_ROT_180)).select(Direction.EAST, Half.TOP, false, $$1.with(Y_ROT_90)).select(Direction.WEST, Half.TOP, false, $$1.with(Y_ROT_270)).select(Direction.NORTH, Half.BOTTOM, true, $$3).select(Direction.SOUTH, Half.BOTTOM, true, $$3.with(Y_ROT_180)).select(Direction.EAST, Half.BOTTOM, true, $$3.with(Y_ROT_90)).select(Direction.WEST, Half.BOTTOM, true, $$3.with(Y_ROT_270)).select(Direction.NORTH, Half.TOP, true, $$3.with(X_ROT_180).with(Y_ROT_180)).select(Direction.SOUTH, Half.TOP, true, $$3.with(X_ROT_180)).select(Direction.EAST, Half.TOP, true, $$3.with(X_ROT_180).with(Y_ROT_270)).select(Direction.WEST, Half.TOP, true, $$3.with(X_ROT_180).with(Y_ROT_90)));
    }

    private static BlockModelDefinitionGenerator createTrapdoor(Block $$0, MultiVariant $$1, MultiVariant $$2, MultiVariant $$3) {
        return MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN).select(Direction.NORTH, Half.BOTTOM, false, $$2).select(Direction.SOUTH, Half.BOTTOM, false, $$2).select(Direction.EAST, Half.BOTTOM, false, $$2).select(Direction.WEST, Half.BOTTOM, false, $$2).select(Direction.NORTH, Half.TOP, false, $$1).select(Direction.SOUTH, Half.TOP, false, $$1).select(Direction.EAST, Half.TOP, false, $$1).select(Direction.WEST, Half.TOP, false, $$1).select(Direction.NORTH, Half.BOTTOM, true, $$3).select(Direction.SOUTH, Half.BOTTOM, true, $$3.with(Y_ROT_180)).select(Direction.EAST, Half.BOTTOM, true, $$3.with(Y_ROT_90)).select(Direction.WEST, Half.BOTTOM, true, $$3.with(Y_ROT_270)).select(Direction.NORTH, Half.TOP, true, $$3).select(Direction.SOUTH, Half.TOP, true, $$3.with(Y_ROT_180)).select(Direction.EAST, Half.TOP, true, $$3.with(Y_ROT_90)).select(Direction.WEST, Half.TOP, true, $$3.with(Y_ROT_270)));
    }

    static MultiVariantGenerator createSimpleBlock(Block $$0, MultiVariant $$1) {
        return MultiVariantGenerator.dispatch($$0, $$1);
    }

    private static PropertyDispatch<VariantMutator> createRotatedPillar() {
        return PropertyDispatch.modify(BlockStateProperties.AXIS).select(Direction.Axis.Y, NOP).select(Direction.Axis.Z, X_ROT_90).select(Direction.Axis.X, X_ROT_90.then(Y_ROT_90));
    }

    static BlockModelDefinitionGenerator createPillarBlockUVLocked(Block $$0, TextureMapping $$1, BiConsumer<ResourceLocation, ModelInstance> $$2) {
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_COLUMN_UV_LOCKED_X.create($$0, $$1, $$2));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_COLUMN_UV_LOCKED_Y.create($$0, $$1, $$2));
        MultiVariant $$5 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_COLUMN_UV_LOCKED_Z.create($$0, $$1, $$2));
        return MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.AXIS).select(Direction.Axis.X, $$3).select(Direction.Axis.Y, $$4).select(Direction.Axis.Z, $$5));
    }

    static BlockModelDefinitionGenerator createAxisAlignedPillarBlock(Block $$0, MultiVariant $$1) {
        return MultiVariantGenerator.dispatch($$0, $$1).with(BlockModelGenerators.createRotatedPillar());
    }

    private void createAxisAlignedPillarBlockCustomModel(Block $$0, MultiVariant $$1) {
        this.blockStateOutput.accept(BlockModelGenerators.createAxisAlignedPillarBlock($$0, $$1));
    }

    public void createAxisAlignedPillarBlock(Block $$0, TexturedModel.Provider $$1) {
        MultiVariant $$2 = BlockModelGenerators.plainVariant($$1.create($$0, this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createAxisAlignedPillarBlock($$0, $$2));
    }

    private void createHorizontallyRotatedBlock(Block $$0, TexturedModel.Provider $$1) {
        MultiVariant $$2 = BlockModelGenerators.plainVariant($$1.create($$0, this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0, $$2).with(ROTATION_HORIZONTAL_FACING));
    }

    static BlockModelDefinitionGenerator createRotatedPillarWithHorizontalVariant(Block $$0, MultiVariant $$1, MultiVariant $$2) {
        return MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.AXIS).select(Direction.Axis.Y, $$1).select(Direction.Axis.Z, $$2.with(X_ROT_90)).select(Direction.Axis.X, $$2.with(X_ROT_90).with(Y_ROT_90)));
    }

    private void createRotatedPillarWithHorizontalVariant(Block $$0, TexturedModel.Provider $$1, TexturedModel.Provider $$2) {
        MultiVariant $$3 = BlockModelGenerators.plainVariant($$1.create($$0, this.modelOutput));
        MultiVariant $$4 = BlockModelGenerators.plainVariant($$2.create($$0, this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createRotatedPillarWithHorizontalVariant($$0, $$3, $$4));
    }

    private void createCreakingHeart(Block $$0) {
        MultiVariant $$1 = BlockModelGenerators.plainVariant(TexturedModel.COLUMN_ALT.create($$0, this.modelOutput));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(TexturedModel.COLUMN_HORIZONTAL_ALT.create($$0, this.modelOutput));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(this.createCreakingHeartModel(TexturedModel.COLUMN_ALT, $$0, "_awake"));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(this.createCreakingHeartModel(TexturedModel.COLUMN_HORIZONTAL_ALT, $$0, "_awake"));
        MultiVariant $$5 = BlockModelGenerators.plainVariant(this.createCreakingHeartModel(TexturedModel.COLUMN_ALT, $$0, "_dormant"));
        MultiVariant $$6 = BlockModelGenerators.plainVariant(this.createCreakingHeartModel(TexturedModel.COLUMN_HORIZONTAL_ALT, $$0, "_dormant"));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.AXIS, CreakingHeartBlock.STATE).select(Direction.Axis.Y, CreakingHeartState.UPROOTED, $$1).select(Direction.Axis.Z, CreakingHeartState.UPROOTED, $$2.with(X_ROT_90)).select(Direction.Axis.X, CreakingHeartState.UPROOTED, $$2.with(X_ROT_90).with(Y_ROT_90)).select(Direction.Axis.Y, CreakingHeartState.DORMANT, $$5).select(Direction.Axis.Z, CreakingHeartState.DORMANT, $$6.with(X_ROT_90)).select(Direction.Axis.X, CreakingHeartState.DORMANT, $$6.with(X_ROT_90).with(Y_ROT_90)).select(Direction.Axis.Y, CreakingHeartState.AWAKE, $$3).select(Direction.Axis.Z, CreakingHeartState.AWAKE, $$4.with(X_ROT_90)).select(Direction.Axis.X, CreakingHeartState.AWAKE, $$4.with(X_ROT_90).with(Y_ROT_90))));
    }

    private ResourceLocation createCreakingHeartModel(TexturedModel.Provider $$0, Block $$1, String $$22) {
        return $$0.updateTexture($$2 -> $$2.put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$1, $$22)).put(TextureSlot.END, TextureMapping.getBlockTexture($$1, "_top" + $$22))).createWithSuffix($$1, $$22, this.modelOutput);
    }

    private ResourceLocation createSuffixedVariant(Block $$0, String $$1, ModelTemplate $$2, Function<ResourceLocation, TextureMapping> $$3) {
        return $$2.createWithSuffix($$0, $$1, $$3.apply(TextureMapping.getBlockTexture($$0, $$1)), this.modelOutput);
    }

    static BlockModelDefinitionGenerator createPressurePlate(Block $$0, MultiVariant $$1, MultiVariant $$2) {
        return MultiVariantGenerator.dispatch($$0).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.POWERED, $$2, $$1));
    }

    static BlockModelDefinitionGenerator createSlab(Block $$0, MultiVariant $$1, MultiVariant $$2, MultiVariant $$3) {
        return MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.SLAB_TYPE).select(SlabType.BOTTOM, $$1).select(SlabType.TOP, $$2).select(SlabType.DOUBLE, $$3));
    }

    public void createTrivialCube(Block $$0) {
        this.createTrivialBlock($$0, TexturedModel.CUBE);
    }

    public void createTrivialBlock(Block $$0, TexturedModel.Provider $$1) {
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, BlockModelGenerators.plainVariant($$1.create($$0, this.modelOutput))));
    }

    public void createTintedLeaves(Block $$0, TexturedModel.Provider $$1, int $$2) {
        ResourceLocation $$3 = $$1.create($$0, this.modelOutput);
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, BlockModelGenerators.plainVariant($$3)));
        this.registerSimpleTintedItemModel($$0, $$3, ItemModelUtils.constantTint($$2));
    }

    private void createVine() {
        this.createMultifaceBlockStates(Blocks.VINE);
        ResourceLocation $$0 = this.createFlatItemModelWithBlockTexture(Items.VINE, Blocks.VINE);
        this.registerSimpleTintedItemModel(Blocks.VINE, $$0, ItemModelUtils.constantTint(-12012264));
    }

    private void createItemWithGrassTint(Block $$0) {
        ResourceLocation $$1 = this.createFlatItemModelWithBlockTexture($$0.asItem(), $$0);
        this.registerSimpleTintedItemModel($$0, $$1, new GrassColorSource());
    }

    private BlockFamilyProvider family(Block $$0) {
        TexturedModel $$1 = TEXTURED_MODELS.getOrDefault($$0, TexturedModel.CUBE.get($$0));
        return new BlockFamilyProvider($$1.getMapping()).fullBlock($$0, $$1.getTemplate());
    }

    public void createHangingSign(Block $$0, Block $$1, Block $$2) {
        MultiVariant $$3 = this.createParticleOnlyBlockModel($$1, $$0);
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$1, $$3));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$2, $$3));
        this.registerSimpleFlatItemModel($$1.asItem());
    }

    void createDoor(Block $$0) {
        TextureMapping $$1 = TextureMapping.door($$0);
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_BOTTOM_LEFT.create($$0, $$1, this.modelOutput));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_BOTTOM_LEFT_OPEN.create($$0, $$1, this.modelOutput));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_BOTTOM_RIGHT.create($$0, $$1, this.modelOutput));
        MultiVariant $$5 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN.create($$0, $$1, this.modelOutput));
        MultiVariant $$6 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_TOP_LEFT.create($$0, $$1, this.modelOutput));
        MultiVariant $$7 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_TOP_LEFT_OPEN.create($$0, $$1, this.modelOutput));
        MultiVariant $$8 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_TOP_RIGHT.create($$0, $$1, this.modelOutput));
        MultiVariant $$9 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_TOP_RIGHT_OPEN.create($$0, $$1, this.modelOutput));
        this.registerSimpleFlatItemModel($$0.asItem());
        this.blockStateOutput.accept(BlockModelGenerators.createDoor($$0, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9));
    }

    private void copyDoorModel(Block $$0, Block $$1) {
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_BOTTOM_LEFT.getDefaultModelLocation($$0));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_BOTTOM_LEFT_OPEN.getDefaultModelLocation($$0));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_BOTTOM_RIGHT.getDefaultModelLocation($$0));
        MultiVariant $$5 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN.getDefaultModelLocation($$0));
        MultiVariant $$6 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_TOP_LEFT.getDefaultModelLocation($$0));
        MultiVariant $$7 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_TOP_LEFT_OPEN.getDefaultModelLocation($$0));
        MultiVariant $$8 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_TOP_RIGHT.getDefaultModelLocation($$0));
        MultiVariant $$9 = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_TOP_RIGHT_OPEN.getDefaultModelLocation($$0));
        this.itemModelOutput.copy($$0.asItem(), $$1.asItem());
        this.blockStateOutput.accept(BlockModelGenerators.createDoor($$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9));
    }

    void createOrientableTrapdoor(Block $$0) {
        TextureMapping $$1 = TextureMapping.defaultTexture($$0);
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.ORIENTABLE_TRAPDOOR_TOP.create($$0, $$1, this.modelOutput));
        ResourceLocation $$3 = ModelTemplates.ORIENTABLE_TRAPDOOR_BOTTOM.create($$0, $$1, this.modelOutput);
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.ORIENTABLE_TRAPDOOR_OPEN.create($$0, $$1, this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createOrientableTrapdoor($$0, $$2, BlockModelGenerators.plainVariant($$3), $$4));
        this.registerSimpleItemModel($$0, $$3);
    }

    void createTrapdoor(Block $$0) {
        TextureMapping $$1 = TextureMapping.defaultTexture($$0);
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.TRAPDOOR_TOP.create($$0, $$1, this.modelOutput));
        ResourceLocation $$3 = ModelTemplates.TRAPDOOR_BOTTOM.create($$0, $$1, this.modelOutput);
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.TRAPDOOR_OPEN.create($$0, $$1, this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createTrapdoor($$0, $$2, BlockModelGenerators.plainVariant($$3), $$4));
        this.registerSimpleItemModel($$0, $$3);
    }

    private void copyTrapdoorModel(Block $$0, Block $$1) {
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.TRAPDOOR_TOP.getDefaultModelLocation($$0));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.TRAPDOOR_BOTTOM.getDefaultModelLocation($$0));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.TRAPDOOR_OPEN.getDefaultModelLocation($$0));
        this.itemModelOutput.copy($$0.asItem(), $$1.asItem());
        this.blockStateOutput.accept(BlockModelGenerators.createTrapdoor($$1, $$2, $$3, $$4));
    }

    private void createBigDripLeafBlock() {
        MultiVariant $$0 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF, "_partial_tilt"));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF, "_full_tilt"));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.BIG_DRIPLEAF).with(PropertyDispatch.initial(BlockStateProperties.TILT).select(Tilt.NONE, $$0).select(Tilt.UNSTABLE, $$0).select(Tilt.PARTIAL, $$1).select(Tilt.FULL, $$2)).with(ROTATION_HORIZONTAL_FACING));
    }

    private WoodProvider woodProvider(Block $$0) {
        return new WoodProvider(TextureMapping.logColumn($$0));
    }

    private void createNonTemplateModelBlock(Block $$0) {
        this.createNonTemplateModelBlock($$0, $$0);
    }

    private void createNonTemplateModelBlock(Block $$0, Block $$1) {
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$1))));
    }

    private void createCrossBlockWithDefaultItem(Block $$0, PlantType $$1) {
        this.registerSimpleItemModel($$0.asItem(), $$1.createItemModel(this, $$0));
        this.createCrossBlock($$0, $$1);
    }

    private void createCrossBlockWithDefaultItem(Block $$0, PlantType $$1, TextureMapping $$2) {
        this.registerSimpleFlatItemModel($$0);
        this.createCrossBlock($$0, $$1, $$2);
    }

    private void createCrossBlock(Block $$0, PlantType $$1) {
        TextureMapping $$2 = $$1.getTextureMapping($$0);
        this.createCrossBlock($$0, $$1, $$2);
    }

    private void createCrossBlock(Block $$0, PlantType $$1, TextureMapping $$2) {
        MultiVariant $$3 = BlockModelGenerators.plainVariant($$1.getCross().create($$0, $$2, this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, $$3));
    }

    private void a(Block $$0, PlantType $$1, Property<Integer> $$2, int ... $$32) {
        if ($$2.getPossibleValues().size() != $$32.length) {
            throw new IllegalArgumentException("missing values for property: " + String.valueOf($$2));
        }
        this.registerSimpleFlatItemModel($$0.asItem());
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial($$2).generate($$3 -> {
            String $$4 = "_stage" + $$32[$$3];
            TextureMapping $$5 = TextureMapping.cross(TextureMapping.getBlockTexture($$0, $$4));
            return BlockModelGenerators.plainVariant($$1.getCross().createWithSuffix($$0, $$4, $$5, this.modelOutput));
        })));
    }

    private void createPlantWithDefaultItem(Block $$0, Block $$1, PlantType $$2) {
        this.registerSimpleItemModel($$0.asItem(), $$2.createItemModel(this, $$0));
        this.createPlant($$0, $$1, $$2);
    }

    private void createPlant(Block $$0, Block $$1, PlantType $$2) {
        this.createCrossBlock($$0, $$2);
        TextureMapping $$3 = $$2.getPlantTextureMapping($$0);
        MultiVariant $$4 = BlockModelGenerators.plainVariant($$2.getCrossPot().create($$1, $$3, this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$1, $$4));
    }

    private void createCoralFans(Block $$0, Block $$1) {
        TexturedModel $$2 = TexturedModel.CORAL_FAN.get($$0);
        MultiVariant $$3 = BlockModelGenerators.plainVariant($$2.create($$0, this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, $$3));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.CORAL_WALL_FAN.create($$1, $$2.getMapping(), this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$1, $$4).with(ROTATION_HORIZONTAL_FACING));
        this.registerSimpleFlatItemModel($$0);
    }

    private void createStems(Block $$0, Block $$1) {
        this.registerSimpleFlatItemModel($$0.asItem());
        TextureMapping $$22 = TextureMapping.stem($$0);
        TextureMapping $$3 = TextureMapping.attachedStem($$0, $$1);
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.ATTACHED_STEM.create($$1, $$3, this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$1, $$4).with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.WEST, NOP).select(Direction.SOUTH, Y_ROT_270).select(Direction.NORTH, Y_ROT_90).select(Direction.EAST, Y_ROT_180)));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.AGE_7).generate($$2 -> BlockModelGenerators.plainVariant(ModelTemplates.STEMS[$$2].create($$0, $$22, this.modelOutput)))));
    }

    private void createPitcherPlant() {
        Block $$0 = Blocks.PITCHER_PLANT;
        this.registerSimpleFlatItemModel($$0.asItem());
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0, "_top"));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0, "_bottom"));
        this.createDoubleBlock($$0, $$1, $$2);
    }

    private void createPitcherCrop() {
        Block $$0 = Blocks.PITCHER_CROP;
        this.registerSimpleFlatItemModel($$0.asItem());
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(PitcherCropBlock.AGE, BlockStateProperties.DOUBLE_BLOCK_HALF).generate(($$1, $$2) -> switch ($$2) {
            default -> throw new MatchException(null, null);
            case DoubleBlockHalf.UPPER -> BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0, "_top_stage_" + $$1));
            case DoubleBlockHalf.LOWER -> BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0, "_bottom_stage_" + $$1));
        })));
    }

    private void createCoral(Block $$0, Block $$1, Block $$2, Block $$3, Block $$4, Block $$5, Block $$6, Block $$7) {
        this.createCrossBlockWithDefaultItem($$0, PlantType.NOT_TINTED);
        this.createCrossBlockWithDefaultItem($$1, PlantType.NOT_TINTED);
        this.createTrivialCube($$2);
        this.createTrivialCube($$3);
        this.createCoralFans($$4, $$6);
        this.createCoralFans($$5, $$7);
    }

    private void createDoublePlant(Block $$0, PlantType $$1) {
        MultiVariant $$2 = BlockModelGenerators.plainVariant(this.createSuffixedVariant($$0, "_top", $$1.getCross(), TextureMapping::cross));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(this.createSuffixedVariant($$0, "_bottom", $$1.getCross(), TextureMapping::cross));
        this.createDoubleBlock($$0, $$2, $$3);
    }

    private void createDoublePlantWithDefaultItem(Block $$0, PlantType $$1) {
        this.registerSimpleFlatItemModel($$0, "_top");
        this.createDoublePlant($$0, $$1);
    }

    private void createTintedDoublePlant(Block $$0) {
        ResourceLocation $$1 = this.createFlatItemModelWithBlockTexture($$0.asItem(), $$0, "_top");
        this.registerSimpleTintedItemModel($$0, $$1, new GrassColorSource());
        this.createDoublePlant($$0, PlantType.TINTED);
    }

    private void createSunflower() {
        this.registerSimpleFlatItemModel(Blocks.SUNFLOWER, "_front");
        MultiVariant $$0 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.SUNFLOWER, "_top"));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.SUNFLOWER, "_bottom", PlantType.NOT_TINTED.getCross(), TextureMapping::cross));
        this.createDoubleBlock(Blocks.SUNFLOWER, $$0, $$1);
    }

    private void createTallSeagrass() {
        MultiVariant $$0 = BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.TALL_SEAGRASS, "_top", ModelTemplates.SEAGRASS, TextureMapping::defaultTexture));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.TALL_SEAGRASS, "_bottom", ModelTemplates.SEAGRASS, TextureMapping::defaultTexture));
        this.createDoubleBlock(Blocks.TALL_SEAGRASS, $$0, $$1);
    }

    private void createSmallDripleaf() {
        MultiVariant $$0 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.SMALL_DRIPLEAF, "_top"));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.SMALL_DRIPLEAF, "_bottom"));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SMALL_DRIPLEAF).with(PropertyDispatch.initial(BlockStateProperties.DOUBLE_BLOCK_HALF).select(DoubleBlockHalf.LOWER, $$1).select(DoubleBlockHalf.UPPER, $$0)).with(ROTATION_HORIZONTAL_FACING));
    }

    private void createDoubleBlock(Block $$0, MultiVariant $$1, MultiVariant $$2) {
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.DOUBLE_BLOCK_HALF).select(DoubleBlockHalf.LOWER, $$2).select(DoubleBlockHalf.UPPER, $$1)));
    }

    private void createPassiveRail(Block $$0) {
        TextureMapping $$1 = TextureMapping.rail($$0);
        TextureMapping $$2 = TextureMapping.rail(TextureMapping.getBlockTexture($$0, "_corner"));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.RAIL_FLAT.create($$0, $$1, this.modelOutput));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.RAIL_CURVED.create($$0, $$2, this.modelOutput));
        MultiVariant $$5 = BlockModelGenerators.plainVariant(ModelTemplates.RAIL_RAISED_NE.create($$0, $$1, this.modelOutput));
        MultiVariant $$6 = BlockModelGenerators.plainVariant(ModelTemplates.RAIL_RAISED_SW.create($$0, $$1, this.modelOutput));
        this.registerSimpleFlatItemModel($$0);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.RAIL_SHAPE).select(RailShape.NORTH_SOUTH, $$3).select(RailShape.EAST_WEST, $$3.with(Y_ROT_90)).select(RailShape.ASCENDING_EAST, $$5.with(Y_ROT_90)).select(RailShape.ASCENDING_WEST, $$6.with(Y_ROT_90)).select(RailShape.ASCENDING_NORTH, $$5).select(RailShape.ASCENDING_SOUTH, $$6).select(RailShape.SOUTH_EAST, $$4).select(RailShape.SOUTH_WEST, $$4.with(Y_ROT_90)).select(RailShape.NORTH_WEST, $$4.with(Y_ROT_180)).select(RailShape.NORTH_EAST, $$4.with(Y_ROT_270))));
    }

    private void createActiveRail(Block $$0) {
        MultiVariant $$1 = BlockModelGenerators.plainVariant(this.createSuffixedVariant($$0, "", ModelTemplates.RAIL_FLAT, TextureMapping::rail));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(this.createSuffixedVariant($$0, "", ModelTemplates.RAIL_RAISED_NE, TextureMapping::rail));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(this.createSuffixedVariant($$0, "", ModelTemplates.RAIL_RAISED_SW, TextureMapping::rail));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(this.createSuffixedVariant($$0, "_on", ModelTemplates.RAIL_FLAT, TextureMapping::rail));
        MultiVariant $$5 = BlockModelGenerators.plainVariant(this.createSuffixedVariant($$0, "_on", ModelTemplates.RAIL_RAISED_NE, TextureMapping::rail));
        MultiVariant $$62 = BlockModelGenerators.plainVariant(this.createSuffixedVariant($$0, "_on", ModelTemplates.RAIL_RAISED_SW, TextureMapping::rail));
        this.registerSimpleFlatItemModel($$0);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.POWERED, BlockStateProperties.RAIL_SHAPE_STRAIGHT).generate(($$6, $$7) -> switch ($$7) {
            case RailShape.NORTH_SOUTH -> {
                if ($$6.booleanValue()) {
                    yield $$4;
                }
                yield $$1;
            }
            case RailShape.EAST_WEST -> ($$6 != false ? $$4 : $$1).with(Y_ROT_90);
            case RailShape.ASCENDING_EAST -> ($$6 != false ? $$5 : $$2).with(Y_ROT_90);
            case RailShape.ASCENDING_WEST -> ($$6 != false ? $$62 : $$3).with(Y_ROT_90);
            case RailShape.ASCENDING_NORTH -> {
                if ($$6.booleanValue()) {
                    yield $$5;
                }
                yield $$2;
            }
            case RailShape.ASCENDING_SOUTH -> {
                if ($$6.booleanValue()) {
                    yield $$62;
                }
                yield $$3;
            }
            default -> throw new UnsupportedOperationException("Fix you generator!");
        })));
    }

    private void createAirLikeBlock(Block $$0, Item $$1) {
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.PARTICLE_ONLY.create($$0, TextureMapping.particleFromItem($$1), this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, $$2));
    }

    private void createAirLikeBlock(Block $$0, ResourceLocation $$1) {
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.PARTICLE_ONLY.create($$0, TextureMapping.particle($$1), this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, $$2));
    }

    private MultiVariant createParticleOnlyBlockModel(Block $$0, Block $$1) {
        return BlockModelGenerators.plainVariant(ModelTemplates.PARTICLE_ONLY.create($$0, TextureMapping.particle($$1), this.modelOutput));
    }

    public void createParticleOnlyBlock(Block $$0, Block $$1) {
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, this.createParticleOnlyBlockModel($$0, $$1)));
    }

    private void createParticleOnlyBlock(Block $$0) {
        this.createParticleOnlyBlock($$0, $$0);
    }

    private void createFullAndCarpetBlocks(Block $$0, Block $$1) {
        this.createTrivialCube($$0);
        MultiVariant $$2 = BlockModelGenerators.plainVariant(TexturedModel.CARPET.get($$0).create($$1, this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$1, $$2));
    }

    private void createLeafLitter(Block $$0) {
        MultiVariant $$1 = BlockModelGenerators.plainVariant(TexturedModel.LEAF_LITTER_1.create($$0, this.modelOutput));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(TexturedModel.LEAF_LITTER_2.create($$0, this.modelOutput));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(TexturedModel.LEAF_LITTER_3.create($$0, this.modelOutput));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(TexturedModel.LEAF_LITTER_4.create($$0, this.modelOutput));
        this.registerSimpleFlatItemModel($$0.asItem());
        this.createSegmentedBlock($$0, $$1, LEAF_LITTER_MODEL_1_SEGMENT_CONDITION, $$2, LEAF_LITTER_MODEL_2_SEGMENT_CONDITION, $$3, LEAF_LITTER_MODEL_3_SEGMENT_CONDITION, $$4, LEAF_LITTER_MODEL_4_SEGMENT_CONDITION);
    }

    private void createFlowerBed(Block $$0) {
        MultiVariant $$1 = BlockModelGenerators.plainVariant(TexturedModel.FLOWERBED_1.create($$0, this.modelOutput));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(TexturedModel.FLOWERBED_2.create($$0, this.modelOutput));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(TexturedModel.FLOWERBED_3.create($$0, this.modelOutput));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(TexturedModel.FLOWERBED_4.create($$0, this.modelOutput));
        this.registerSimpleFlatItemModel($$0.asItem());
        this.createSegmentedBlock($$0, $$1, FLOWER_BED_MODEL_1_SEGMENT_CONDITION, $$2, FLOWER_BED_MODEL_2_SEGMENT_CONDITION, $$3, FLOWER_BED_MODEL_3_SEGMENT_CONDITION, $$4, FLOWER_BED_MODEL_4_SEGMENT_CONDITION);
    }

    private void createSegmentedBlock(Block $$0, MultiVariant $$1, Function<ConditionBuilder, ConditionBuilder> $$2, MultiVariant $$3, Function<ConditionBuilder, ConditionBuilder> $$4, MultiVariant $$5, Function<ConditionBuilder, ConditionBuilder> $$6, MultiVariant $$7, Function<ConditionBuilder, ConditionBuilder> $$8) {
        this.blockStateOutput.accept(MultiPartGenerator.multiPart($$0).with($$2.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)), $$1).with($$2.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)), $$1.with(Y_ROT_90)).with($$2.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)), $$1.with(Y_ROT_180)).with($$2.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)), $$1.with(Y_ROT_270)).with($$4.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)), $$3).with($$4.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)), $$3.with(Y_ROT_90)).with($$4.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)), $$3.with(Y_ROT_180)).with($$4.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)), $$3.with(Y_ROT_270)).with($$6.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)), $$5).with($$6.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)), $$5.with(Y_ROT_90)).with($$6.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)), $$5.with(Y_ROT_180)).with($$6.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)), $$5.with(Y_ROT_270)).with($$8.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)), $$7).with($$8.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)), $$7.with(Y_ROT_90)).with($$8.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)), $$7.with(Y_ROT_180)).with($$8.apply(BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)), $$7.with(Y_ROT_270)));
    }

    private void a(TexturedModel.Provider $$0, Block ... $$1) {
        for (Block $$2 : $$1) {
            Variant $$3 = BlockModelGenerators.plainModel($$0.create($$2, this.modelOutput));
            this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$2, BlockModelGenerators.createRotatedVariants($$3)));
        }
    }

    private void b(TexturedModel.Provider $$0, Block ... $$1) {
        for (Block $$2 : $$1) {
            MultiVariant $$3 = BlockModelGenerators.plainVariant($$0.create($$2, this.modelOutput));
            this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$2, $$3).with(ROTATION_HORIZONTAL_FACING_ALT));
        }
    }

    private void createGlassBlocks(Block $$0, Block $$1) {
        this.createTrivialCube($$0);
        TextureMapping $$2 = TextureMapping.pane($$0, $$1);
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_POST.create($$1, $$2, this.modelOutput));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_SIDE.create($$1, $$2, this.modelOutput));
        MultiVariant $$5 = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create($$1, $$2, this.modelOutput));
        MultiVariant $$6 = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create($$1, $$2, this.modelOutput));
        MultiVariant $$7 = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create($$1, $$2, this.modelOutput));
        Item $$8 = $$1.asItem();
        this.registerSimpleItemModel($$8, this.createFlatItemModelWithBlockTexture($$8, $$0));
        this.blockStateOutput.accept(MultiPartGenerator.multiPart($$1).with($$3).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true), $$4).with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, true), $$4.with(Y_ROT_90)).with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, true), $$5).with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, true), $$5.with(Y_ROT_90)).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, false), $$6).with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, false), $$7).with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, false), $$7.with(Y_ROT_90)).with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, false), $$6.with(Y_ROT_270)));
    }

    private void createCommandBlock(Block $$0) {
        TextureMapping $$12 = TextureMapping.commandBlock($$0);
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.COMMAND_BLOCK.create($$0, $$12, this.modelOutput));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(this.createSuffixedVariant($$0, "_conditional", ModelTemplates.COMMAND_BLOCK, $$1 -> $$12.copyAndUpdate(TextureSlot.SIDE, (ResourceLocation)$$1)));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.CONDITIONAL, $$3, $$2)).with(ROTATION_FACING));
    }

    private void createAnvil(Block $$0) {
        MultiVariant $$1 = BlockModelGenerators.plainVariant(TexturedModel.ANVIL.create($$0, this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, $$1).with(ROTATION_HORIZONTAL_FACING_ALT));
    }

    private static MultiVariant createBambooModels(int $$0) {
        String $$12 = "_age" + $$0;
        return new MultiVariant(WeightedList.of(IntStream.range(1, 5).mapToObj($$1 -> new Weighted<Variant>(BlockModelGenerators.plainModel(ModelLocationUtils.getModelLocation(Blocks.BAMBOO, $$1 + $$12)), 1)).collect(Collectors.toList())));
    }

    private void createBamboo() {
        this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.BAMBOO).with(BlockModelGenerators.condition().term(BlockStateProperties.AGE_1, 0), BlockModelGenerators.createBambooModels(0)).with(BlockModelGenerators.condition().term(BlockStateProperties.AGE_1, 1), BlockModelGenerators.createBambooModels(1)).with(BlockModelGenerators.condition().term(BlockStateProperties.BAMBOO_LEAVES, BambooLeaves.SMALL), BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.BAMBOO, "_small_leaves"))).with(BlockModelGenerators.condition().term(BlockStateProperties.BAMBOO_LEAVES, BambooLeaves.LARGE), BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.BAMBOO, "_large_leaves"))));
    }

    private void createBarrel() {
        ResourceLocation $$0 = TextureMapping.getBlockTexture(Blocks.BARREL, "_top_open");
        MultiVariant $$12 = BlockModelGenerators.plainVariant(TexturedModel.CUBE_TOP_BOTTOM.create(Blocks.BARREL, this.modelOutput));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.BARREL).updateTextures($$1 -> $$1.put(TextureSlot.TOP, $$0)).createWithSuffix(Blocks.BARREL, "_open", this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.BARREL).with(PropertyDispatch.initial(BlockStateProperties.OPEN).select(false, $$12).select(true, $$2)).with(ROTATIONS_COLUMN_WITH_FACING));
    }

    private static <T extends Comparable<T>> PropertyDispatch<MultiVariant> createEmptyOrFullDispatch(Property<T> $$0, T $$1, MultiVariant $$2, MultiVariant $$32) {
        return PropertyDispatch.initial($$0).generate($$3 -> {
            boolean $$4 = $$3.compareTo($$1) >= 0;
            return $$4 ? $$2 : $$32;
        });
    }

    private void createBeeNest(Block $$0, Function<Block, TextureMapping> $$1) {
        TextureMapping $$2 = $$1.apply($$0).copyForced(TextureSlot.SIDE, TextureSlot.PARTICLE);
        TextureMapping $$3 = $$2.copyAndUpdate(TextureSlot.FRONT, TextureMapping.getBlockTexture($$0, "_front_honey"));
        ResourceLocation $$4 = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.createWithSuffix($$0, "_empty", $$2, this.modelOutput);
        ResourceLocation $$5 = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.createWithSuffix($$0, "_honey", $$3, this.modelOutput);
        this.itemModelOutput.accept($$0.asItem(), ItemModelUtils.selectBlockItemProperty(BeehiveBlock.HONEY_LEVEL, ItemModelUtils.plainModel($$4), Map.of((Object)5, (Object)ItemModelUtils.plainModel($$5))));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(BlockModelGenerators.createEmptyOrFullDispatch(BeehiveBlock.HONEY_LEVEL, 5, BlockModelGenerators.plainVariant($$5), BlockModelGenerators.plainVariant($$4))).with(ROTATION_HORIZONTAL_FACING));
    }

    private void a(Block $$0, Property<Integer> $$1, int ... $$2) {
        this.registerSimpleFlatItemModel($$0.asItem());
        if ($$1.getPossibleValues().size() != $$2.length) {
            throw new IllegalArgumentException();
        }
        Int2ObjectOpenHashMap $$3 = new Int2ObjectOpenHashMap();
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial($$1).generate(arg_0 -> this.a($$2, (Int2ObjectMap)$$3, $$0, arg_0))));
    }

    private void createBell() {
        MultiVariant $$0 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.BELL, "_floor"));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.BELL, "_ceiling"));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.BELL, "_wall"));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.BELL, "_between_walls"));
        this.registerSimpleFlatItemModel(Items.BELL);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.BELL).with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.BELL_ATTACHMENT).select(Direction.NORTH, BellAttachType.FLOOR, $$0).select(Direction.SOUTH, BellAttachType.FLOOR, $$0.with(Y_ROT_180)).select(Direction.EAST, BellAttachType.FLOOR, $$0.with(Y_ROT_90)).select(Direction.WEST, BellAttachType.FLOOR, $$0.with(Y_ROT_270)).select(Direction.NORTH, BellAttachType.CEILING, $$1).select(Direction.SOUTH, BellAttachType.CEILING, $$1.with(Y_ROT_180)).select(Direction.EAST, BellAttachType.CEILING, $$1.with(Y_ROT_90)).select(Direction.WEST, BellAttachType.CEILING, $$1.with(Y_ROT_270)).select(Direction.NORTH, BellAttachType.SINGLE_WALL, $$2.with(Y_ROT_270)).select(Direction.SOUTH, BellAttachType.SINGLE_WALL, $$2.with(Y_ROT_90)).select(Direction.EAST, BellAttachType.SINGLE_WALL, $$2).select(Direction.WEST, BellAttachType.SINGLE_WALL, $$2.with(Y_ROT_180)).select(Direction.SOUTH, BellAttachType.DOUBLE_WALL, $$3.with(Y_ROT_90)).select(Direction.NORTH, BellAttachType.DOUBLE_WALL, $$3.with(Y_ROT_270)).select(Direction.EAST, BellAttachType.DOUBLE_WALL, $$3).select(Direction.WEST, BellAttachType.DOUBLE_WALL, $$3.with(Y_ROT_180))));
    }

    private void createGrindstone() {
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.GRINDSTONE, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.GRINDSTONE))).with(PropertyDispatch.modify(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.FLOOR, Direction.NORTH, NOP).select(AttachFace.FLOOR, Direction.EAST, Y_ROT_90).select(AttachFace.FLOOR, Direction.SOUTH, Y_ROT_180).select(AttachFace.FLOOR, Direction.WEST, Y_ROT_270).select(AttachFace.WALL, Direction.NORTH, X_ROT_90).select(AttachFace.WALL, Direction.EAST, X_ROT_90.then(Y_ROT_90)).select(AttachFace.WALL, Direction.SOUTH, X_ROT_90.then(Y_ROT_180)).select(AttachFace.WALL, Direction.WEST, X_ROT_90.then(Y_ROT_270)).select(AttachFace.CEILING, Direction.SOUTH, X_ROT_180).select(AttachFace.CEILING, Direction.WEST, X_ROT_180.then(Y_ROT_90)).select(AttachFace.CEILING, Direction.NORTH, X_ROT_180.then(Y_ROT_180)).select(AttachFace.CEILING, Direction.EAST, X_ROT_180.then(Y_ROT_270))));
    }

    private void createFurnace(Block $$0, TexturedModel.Provider $$12) {
        MultiVariant $$2 = BlockModelGenerators.plainVariant($$12.create($$0, this.modelOutput));
        ResourceLocation $$3 = TextureMapping.getBlockTexture($$0, "_front_on");
        MultiVariant $$4 = BlockModelGenerators.plainVariant($$12.get($$0).updateTextures($$1 -> $$1.put(TextureSlot.FRONT, $$3)).createWithSuffix($$0, "_on", this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, $$4, $$2)).with(ROTATION_HORIZONTAL_FACING));
    }

    private void a(Block ... $$0) {
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation("campfire_off"));
        for (Block $$2 : $$0) {
            MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.CAMPFIRE.create($$2, TextureMapping.campfire($$2), this.modelOutput));
            this.registerSimpleFlatItemModel($$2.asItem());
            this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$2).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, $$3, $$1)).with(ROTATION_HORIZONTAL_FACING_ALT));
        }
    }

    private void createAzalea(Block $$0) {
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelTemplates.AZALEA.create($$0, TextureMapping.cubeTop($$0), this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, $$1));
    }

    private void createPottedAzalea(Block $$0) {
        MultiVariant $$2;
        if ($$0 == Blocks.POTTED_FLOWERING_AZALEA) {
            MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelTemplates.POTTED_FLOWERING_AZALEA.create($$0, TextureMapping.pottedAzalea($$0), this.modelOutput));
        } else {
            $$2 = BlockModelGenerators.plainVariant(ModelTemplates.POTTED_AZALEA.create($$0, TextureMapping.pottedAzalea($$0), this.modelOutput));
        }
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, $$2));
    }

    private void createBookshelf() {
        TextureMapping $$0 = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.BOOKSHELF), TextureMapping.getBlockTexture(Blocks.OAK_PLANKS));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_COLUMN.create(Blocks.BOOKSHELF, $$0, this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(Blocks.BOOKSHELF, $$1));
    }

    private void createRedstoneWire() {
        this.registerSimpleFlatItemModel(Items.REDSTONE);
        this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.REDSTONE_WIRE).with(BlockModelGenerators.a(BlockModelGenerators.condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.NONE), BlockModelGenerators.condition().a(BlockStateProperties.NORTH_REDSTONE, (Comparable)((Object)RedstoneSide.SIDE), (Comparable[])new RedstoneSide[]{RedstoneSide.UP}).a(BlockStateProperties.EAST_REDSTONE, (Comparable)((Object)RedstoneSide.SIDE), (Comparable[])new RedstoneSide[]{RedstoneSide.UP}), BlockModelGenerators.condition().a(BlockStateProperties.EAST_REDSTONE, (Comparable)((Object)RedstoneSide.SIDE), (Comparable[])new RedstoneSide[]{RedstoneSide.UP}).a(BlockStateProperties.SOUTH_REDSTONE, (Comparable)((Object)RedstoneSide.SIDE), (Comparable[])new RedstoneSide[]{RedstoneSide.UP}), BlockModelGenerators.condition().a(BlockStateProperties.SOUTH_REDSTONE, (Comparable)((Object)RedstoneSide.SIDE), (Comparable[])new RedstoneSide[]{RedstoneSide.UP}).a(BlockStateProperties.WEST_REDSTONE, (Comparable)((Object)RedstoneSide.SIDE), (Comparable[])new RedstoneSide[]{RedstoneSide.UP}), BlockModelGenerators.condition().a(BlockStateProperties.WEST_REDSTONE, (Comparable)((Object)RedstoneSide.SIDE), (Comparable[])new RedstoneSide[]{RedstoneSide.UP}).a(BlockStateProperties.NORTH_REDSTONE, (Comparable)((Object)RedstoneSide.SIDE), (Comparable[])new RedstoneSide[]{RedstoneSide.UP})), BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_dot"))).with(BlockModelGenerators.condition().a(BlockStateProperties.NORTH_REDSTONE, (Comparable)((Object)RedstoneSide.SIDE), (Comparable[])new RedstoneSide[]{RedstoneSide.UP}), BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side0"))).with(BlockModelGenerators.condition().a(BlockStateProperties.SOUTH_REDSTONE, (Comparable)((Object)RedstoneSide.SIDE), (Comparable[])new RedstoneSide[]{RedstoneSide.UP}), BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side_alt0"))).with(BlockModelGenerators.condition().a(BlockStateProperties.EAST_REDSTONE, (Comparable)((Object)RedstoneSide.SIDE), (Comparable[])new RedstoneSide[]{RedstoneSide.UP}), BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side_alt1")).with(Y_ROT_270)).with(BlockModelGenerators.condition().a(BlockStateProperties.WEST_REDSTONE, (Comparable)((Object)RedstoneSide.SIDE), (Comparable[])new RedstoneSide[]{RedstoneSide.UP}), BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side1")).with(Y_ROT_270)).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.UP), BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up"))).with(BlockModelGenerators.condition().term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.UP), BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up")).with(Y_ROT_90)).with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.UP), BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up")).with(Y_ROT_180)).with(BlockModelGenerators.condition().term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.UP), BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up")).with(Y_ROT_270)));
    }

    private void createComparator() {
        this.registerSimpleFlatItemModel(Items.COMPARATOR);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.COMPARATOR).with(PropertyDispatch.initial(BlockStateProperties.MODE_COMPARATOR, BlockStateProperties.POWERED).select(ComparatorMode.COMPARE, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.COMPARATOR))).select(ComparatorMode.COMPARE, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_on"))).select(ComparatorMode.SUBTRACT, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_subtract"))).select(ComparatorMode.SUBTRACT, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_on_subtract")))).with(ROTATION_HORIZONTAL_FACING_ALT));
    }

    private void createSmoothStoneSlab() {
        TextureMapping $$0 = TextureMapping.cube(Blocks.SMOOTH_STONE);
        TextureMapping $$1 = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.SMOOTH_STONE_SLAB, "_side"), $$0.get(TextureSlot.TOP));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.SLAB_BOTTOM.create(Blocks.SMOOTH_STONE_SLAB, $$1, this.modelOutput));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.SLAB_TOP.create(Blocks.SMOOTH_STONE_SLAB, $$1, this.modelOutput));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_COLUMN.createWithOverride(Blocks.SMOOTH_STONE_SLAB, "_double", $$1, this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createSlab(Blocks.SMOOTH_STONE_SLAB, $$2, $$3, $$4));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(Blocks.SMOOTH_STONE, BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ALL.create(Blocks.SMOOTH_STONE, $$0, this.modelOutput))));
    }

    private void createBrewingStand() {
        this.registerSimpleFlatItemModel(Items.BREWING_STAND);
        this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.BREWING_STAND).with(BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.BREWING_STAND))).with(BlockModelGenerators.condition().term(BlockStateProperties.HAS_BOTTLE_0, true), BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle0"))).with(BlockModelGenerators.condition().term(BlockStateProperties.HAS_BOTTLE_1, true), BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle1"))).with(BlockModelGenerators.condition().term(BlockStateProperties.HAS_BOTTLE_2, true), BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle2"))).with(BlockModelGenerators.condition().term(BlockStateProperties.HAS_BOTTLE_0, false), BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty0"))).with(BlockModelGenerators.condition().term(BlockStateProperties.HAS_BOTTLE_1, false), BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty1"))).with(BlockModelGenerators.condition().term(BlockStateProperties.HAS_BOTTLE_2, false), BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty2"))));
    }

    private void createMushroomBlock(Block $$0) {
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelTemplates.SINGLE_FACE.create($$0, TextureMapping.defaultTexture($$0), this.modelOutput));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation("mushroom_block_inside"));
        this.blockStateOutput.accept(MultiPartGenerator.multiPart($$0).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true), $$1).with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, true), $$1.with(Y_ROT_90).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, true), $$1.with(Y_ROT_180).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, true), $$1.with(Y_ROT_270).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.UP, true), $$1.with(X_ROT_270).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.DOWN, true), $$1.with(X_ROT_90).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, false), $$2).with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, false), $$2.with(Y_ROT_90)).with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, false), $$2.with(Y_ROT_180)).with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, false), $$2.with(Y_ROT_270)).with(BlockModelGenerators.condition().term(BlockStateProperties.UP, false), $$2.with(X_ROT_270)).with(BlockModelGenerators.condition().term(BlockStateProperties.DOWN, false), $$2.with(X_ROT_90)));
        this.registerSimpleItemModel($$0, TexturedModel.CUBE.createWithSuffix($$0, "_inventory", this.modelOutput));
    }

    private void createCakeBlock() {
        this.registerSimpleFlatItemModel(Items.CAKE);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.CAKE).with(PropertyDispatch.initial(BlockStateProperties.BITES).select(0, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.CAKE))).select(1, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice1"))).select(2, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice2"))).select(3, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice3"))).select(4, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice4"))).select(5, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice5"))).select(6, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice6")))));
    }

    private void createCartographyTable() {
        TextureMapping $$0 = new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureSlot.DOWN, TextureMapping.getBlockTexture(Blocks.DARK_OAK_PLANKS)).put(TextureSlot.UP, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_top")).put(TextureSlot.NORTH, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureSlot.EAST, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side1")).put(TextureSlot.WEST, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side2"));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(Blocks.CARTOGRAPHY_TABLE, BlockModelGenerators.plainVariant(ModelTemplates.CUBE.create(Blocks.CARTOGRAPHY_TABLE, $$0, this.modelOutput))));
    }

    private void createSmithingTable() {
        TextureMapping $$0 = new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(TextureSlot.DOWN, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_bottom")).put(TextureSlot.UP, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_top")).put(TextureSlot.NORTH, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(TextureSlot.EAST, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_side")).put(TextureSlot.WEST, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_side"));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(Blocks.SMITHING_TABLE, BlockModelGenerators.plainVariant(ModelTemplates.CUBE.create(Blocks.SMITHING_TABLE, $$0, this.modelOutput))));
    }

    private void createCraftingTableLike(Block $$0, Block $$1, BiFunction<Block, Block, TextureMapping> $$2) {
        TextureMapping $$3 = $$2.apply($$0, $$1);
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, BlockModelGenerators.plainVariant(ModelTemplates.CUBE.create($$0, $$3, this.modelOutput))));
    }

    public void createGenericCube(Block $$0) {
        TextureMapping $$1 = new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture($$0, "_particle")).put(TextureSlot.DOWN, TextureMapping.getBlockTexture($$0, "_down")).put(TextureSlot.UP, TextureMapping.getBlockTexture($$0, "_up")).put(TextureSlot.NORTH, TextureMapping.getBlockTexture($$0, "_north")).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture($$0, "_south")).put(TextureSlot.EAST, TextureMapping.getBlockTexture($$0, "_east")).put(TextureSlot.WEST, TextureMapping.getBlockTexture($$0, "_west"));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, BlockModelGenerators.plainVariant(ModelTemplates.CUBE.create($$0, $$1, this.modelOutput))));
    }

    private void createPumpkins() {
        TextureMapping $$0 = TextureMapping.column(Blocks.PUMPKIN);
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(Blocks.PUMPKIN, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.PUMPKIN))));
        this.createPumpkinVariant(Blocks.CARVED_PUMPKIN, $$0);
        this.createPumpkinVariant(Blocks.JACK_O_LANTERN, $$0);
    }

    private void createPumpkinVariant(Block $$0, TextureMapping $$1) {
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ORIENTABLE.create($$0, $$1.copyAndUpdate(TextureSlot.FRONT, TextureMapping.getBlockTexture($$0)), this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0, $$2).with(ROTATION_HORIZONTAL_FACING));
    }

    private void createCauldrons() {
        this.registerSimpleFlatItemModel(Items.CAULDRON);
        this.createNonTemplateModelBlock(Blocks.CAULDRON);
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(Blocks.LAVA_CAULDRON, BlockModelGenerators.plainVariant(ModelTemplates.CAULDRON_FULL.create(Blocks.LAVA_CAULDRON, TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.LAVA, "_still")), this.modelOutput))));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.WATER_CAULDRON).with(PropertyDispatch.initial(LayeredCauldronBlock.LEVEL).select(1, BlockModelGenerators.plainVariant(ModelTemplates.CAULDRON_LEVEL1.createWithSuffix(Blocks.WATER_CAULDRON, "_level1", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput))).select(2, BlockModelGenerators.plainVariant(ModelTemplates.CAULDRON_LEVEL2.createWithSuffix(Blocks.WATER_CAULDRON, "_level2", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput))).select(3, BlockModelGenerators.plainVariant(ModelTemplates.CAULDRON_FULL.createWithSuffix(Blocks.WATER_CAULDRON, "_full", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput)))));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.POWDER_SNOW_CAULDRON).with(PropertyDispatch.initial(LayeredCauldronBlock.LEVEL).select(1, BlockModelGenerators.plainVariant(ModelTemplates.CAULDRON_LEVEL1.createWithSuffix(Blocks.POWDER_SNOW_CAULDRON, "_level1", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)), this.modelOutput))).select(2, BlockModelGenerators.plainVariant(ModelTemplates.CAULDRON_LEVEL2.createWithSuffix(Blocks.POWDER_SNOW_CAULDRON, "_level2", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)), this.modelOutput))).select(3, BlockModelGenerators.plainVariant(ModelTemplates.CAULDRON_FULL.createWithSuffix(Blocks.POWDER_SNOW_CAULDRON, "_full", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)), this.modelOutput)))));
    }

    private void createChorusFlower() {
        TextureMapping $$0 = TextureMapping.defaultTexture(Blocks.CHORUS_FLOWER);
        MultiVariant $$12 = BlockModelGenerators.plainVariant(ModelTemplates.CHORUS_FLOWER.create(Blocks.CHORUS_FLOWER, $$0, this.modelOutput));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.CHORUS_FLOWER, "_dead", ModelTemplates.CHORUS_FLOWER, $$1 -> $$0.copyAndUpdate(TextureSlot.TEXTURE, (ResourceLocation)$$1)));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.CHORUS_FLOWER).with(BlockModelGenerators.createEmptyOrFullDispatch(BlockStateProperties.AGE_5, 5, $$2, $$12)));
    }

    private void createCrafterBlock() {
        MultiVariant $$0 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.CRAFTER));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.CRAFTER, "_triggered"));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.CRAFTER, "_crafting"));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.CRAFTER, "_crafting_triggered"));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.CRAFTER).with(PropertyDispatch.initial(BlockStateProperties.TRIGGERED, CrafterBlock.CRAFTING).select(false, false, $$0).select(true, true, $$3).select(true, false, $$1).select(false, true, $$2)).with(PropertyDispatch.modify(BlockStateProperties.ORIENTATION).generate(BlockModelGenerators::applyRotation)));
    }

    private void createDispenserBlock(Block $$0) {
        TextureMapping $$1 = new TextureMapping().put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FURNACE, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.FURNACE, "_side")).put(TextureSlot.FRONT, TextureMapping.getBlockTexture($$0, "_front"));
        TextureMapping $$2 = new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.FURNACE, "_top")).put(TextureSlot.FRONT, TextureMapping.getBlockTexture($$0, "_front_vertical"));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ORIENTABLE.create($$0, $$1, this.modelOutput));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ORIENTABLE_VERTICAL.create($$0, $$2, this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.FACING).select(Direction.DOWN, $$4.with(X_ROT_180)).select(Direction.UP, $$4).select(Direction.NORTH, $$3).select(Direction.EAST, $$3.with(Y_ROT_90)).select(Direction.SOUTH, $$3.with(Y_ROT_180)).select(Direction.WEST, $$3.with(Y_ROT_270))));
    }

    private void createEndPortalFrame() {
        MultiVariant $$0 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.END_PORTAL_FRAME));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.END_PORTAL_FRAME, "_filled"));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.END_PORTAL_FRAME).with(PropertyDispatch.initial(BlockStateProperties.EYE).select(false, $$0).select(true, $$1)).with(ROTATION_HORIZONTAL_FACING_ALT));
    }

    private void createChorusPlant() {
        MultiVariant $$0 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_side"));
        Variant $$1 = BlockModelGenerators.plainModel(ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside"));
        Variant $$2 = BlockModelGenerators.plainModel(ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside1"));
        Variant $$3 = BlockModelGenerators.plainModel(ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside2"));
        Variant $$4 = BlockModelGenerators.plainModel(ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside3"));
        Variant $$5 = $$1.with(UV_LOCK);
        Variant $$6 = $$2.with(UV_LOCK);
        Variant $$7 = $$3.with(UV_LOCK);
        Variant $$8 = $$4.with(UV_LOCK);
        this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.CHORUS_PLANT).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true), $$0).with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, true), $$0.with(Y_ROT_90).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, true), $$0.with(Y_ROT_180).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, true), $$0.with(Y_ROT_270).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.UP, true), $$0.with(X_ROT_270).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.DOWN, true), $$0.with(X_ROT_90).with(UV_LOCK)).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, false), new MultiVariant(WeightedList.a(new Weighted<Variant>($$1, 2), new Weighted<Variant>($$2, 1), new Weighted<Variant>($$3, 1), new Weighted<Variant>($$4, 1)))).with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, false), new MultiVariant(WeightedList.a(new Weighted<Variant>($$6.with(Y_ROT_90), 1), new Weighted<Variant>($$7.with(Y_ROT_90), 1), new Weighted<Variant>($$8.with(Y_ROT_90), 1), new Weighted<Variant>($$5.with(Y_ROT_90), 2)))).with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, false), new MultiVariant(WeightedList.a(new Weighted<Variant>($$7.with(Y_ROT_180), 1), new Weighted<Variant>($$8.with(Y_ROT_180), 1), new Weighted<Variant>($$5.with(Y_ROT_180), 2), new Weighted<Variant>($$6.with(Y_ROT_180), 1)))).with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, false), new MultiVariant(WeightedList.a(new Weighted<Variant>($$8.with(Y_ROT_270), 1), new Weighted<Variant>($$5.with(Y_ROT_270), 2), new Weighted<Variant>($$6.with(Y_ROT_270), 1), new Weighted<Variant>($$7.with(Y_ROT_270), 1)))).with(BlockModelGenerators.condition().term(BlockStateProperties.UP, false), new MultiVariant(WeightedList.a(new Weighted<Variant>($$5.with(X_ROT_270), 2), new Weighted<Variant>($$8.with(X_ROT_270), 1), new Weighted<Variant>($$6.with(X_ROT_270), 1), new Weighted<Variant>($$7.with(X_ROT_270), 1)))).with(BlockModelGenerators.condition().term(BlockStateProperties.DOWN, false), new MultiVariant(WeightedList.a(new Weighted<Variant>($$8.with(X_ROT_90), 1), new Weighted<Variant>($$7.with(X_ROT_90), 1), new Weighted<Variant>($$6.with(X_ROT_90), 1), new Weighted<Variant>($$5.with(X_ROT_90), 2)))));
    }

    private void createComposter() {
        this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.COMPOSTER).with(BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER))).with(BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 1), BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents1"))).with(BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 2), BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents2"))).with(BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 3), BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents3"))).with(BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 4), BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents4"))).with(BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 5), BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents5"))).with(BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 6), BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents6"))).with(BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 7), BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents7"))).with(BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 8), BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents_ready"))));
    }

    private void createCopperBulb(Block $$0) {
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ALL.create($$0, TextureMapping.cube($$0), this.modelOutput));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(this.createSuffixedVariant($$0, "_powered", ModelTemplates.CUBE_ALL, TextureMapping::cube));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(this.createSuffixedVariant($$0, "_lit", ModelTemplates.CUBE_ALL, TextureMapping::cube));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(this.createSuffixedVariant($$0, "_lit_powered", ModelTemplates.CUBE_ALL, TextureMapping::cube));
        this.blockStateOutput.accept(BlockModelGenerators.createCopperBulb($$0, $$1, $$3, $$2, $$4));
    }

    private static BlockModelDefinitionGenerator createCopperBulb(Block $$0, MultiVariant $$1, MultiVariant $$2, MultiVariant $$3, MultiVariant $$42) {
        return MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.LIT, BlockStateProperties.POWERED).generate(($$4, $$5) -> {
            if ($$4.booleanValue()) {
                return $$5 != false ? $$42 : $$2;
            }
            return $$5 != false ? $$3 : $$1;
        }));
    }

    private void copyCopperBulbModel(Block $$0, Block $$1) {
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0, "_powered"));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0, "_lit"));
        MultiVariant $$5 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0, "_lit_powered"));
        this.itemModelOutput.copy($$0.asItem(), $$1.asItem());
        this.blockStateOutput.accept(BlockModelGenerators.createCopperBulb($$1, $$2, $$4, $$3, $$5));
    }

    private void createAmethystCluster(Block $$0) {
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelTemplates.CROSS.create($$0, TextureMapping.cross($$0), this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0, $$1).with(ROTATIONS_COLUMN_WITH_FACING));
    }

    private void createAmethystClusters() {
        this.createAmethystCluster(Blocks.SMALL_AMETHYST_BUD);
        this.createAmethystCluster(Blocks.MEDIUM_AMETHYST_BUD);
        this.createAmethystCluster(Blocks.LARGE_AMETHYST_BUD);
        this.createAmethystCluster(Blocks.AMETHYST_CLUSTER);
    }

    private void createPointedDripstone() {
        PropertyDispatch.C2<MultiVariant, Direction, DripstoneThickness> $$0 = PropertyDispatch.initial(BlockStateProperties.VERTICAL_DIRECTION, BlockStateProperties.DRIPSTONE_THICKNESS);
        for (DripstoneThickness $$1 : DripstoneThickness.values()) {
            $$0.select(Direction.UP, $$1, this.createPointedDripstoneVariant(Direction.UP, $$1));
        }
        for (DripstoneThickness $$2 : DripstoneThickness.values()) {
            $$0.select(Direction.DOWN, $$2, this.createPointedDripstoneVariant(Direction.DOWN, $$2));
        }
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.POINTED_DRIPSTONE).with($$0));
    }

    private MultiVariant createPointedDripstoneVariant(Direction $$0, DripstoneThickness $$1) {
        String $$2 = "_" + $$0.getSerializedName() + "_" + $$1.getSerializedName();
        TextureMapping $$3 = TextureMapping.cross(TextureMapping.getBlockTexture(Blocks.POINTED_DRIPSTONE, $$2));
        return BlockModelGenerators.plainVariant(ModelTemplates.POINTED_DRIPSTONE.createWithSuffix(Blocks.POINTED_DRIPSTONE, $$2, $$3, this.modelOutput));
    }

    private void createNyliumBlock(Block $$0) {
        TextureMapping $$1 = new TextureMapping().put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.NETHERRACK)).put(TextureSlot.TOP, TextureMapping.getBlockTexture($$0)).put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, "_side"));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, BlockModelGenerators.plainVariant(ModelTemplates.CUBE_BOTTOM_TOP.create($$0, $$1, this.modelOutput))));
    }

    private void createDaylightDetector() {
        ResourceLocation $$0 = TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_side");
        TextureMapping $$1 = new TextureMapping().put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_top")).put(TextureSlot.SIDE, $$0);
        TextureMapping $$2 = new TextureMapping().put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_inverted_top")).put(TextureSlot.SIDE, $$0);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.DAYLIGHT_DETECTOR).with(PropertyDispatch.initial(BlockStateProperties.INVERTED).select(false, BlockModelGenerators.plainVariant(ModelTemplates.DAYLIGHT_DETECTOR.create(Blocks.DAYLIGHT_DETECTOR, $$1, this.modelOutput))).select(true, BlockModelGenerators.plainVariant(ModelTemplates.DAYLIGHT_DETECTOR.create(ModelLocationUtils.getModelLocation(Blocks.DAYLIGHT_DETECTOR, "_inverted"), $$2, this.modelOutput)))));
    }

    private void createRotatableColumn(Block $$0) {
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0))).with(ROTATIONS_COLUMN_WITH_FACING));
    }

    private void createLightningRod() {
        Block $$0 = Blocks.LIGHTNING_ROD;
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0, "_on"));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.POWERED, $$1, $$2)).with(ROTATIONS_COLUMN_WITH_FACING));
    }

    private void createFarmland() {
        TextureMapping $$0 = new TextureMapping().put(TextureSlot.DIRT, TextureMapping.getBlockTexture(Blocks.DIRT)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FARMLAND));
        TextureMapping $$1 = new TextureMapping().put(TextureSlot.DIRT, TextureMapping.getBlockTexture(Blocks.DIRT)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FARMLAND, "_moist"));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.FARMLAND.create(Blocks.FARMLAND, $$0, this.modelOutput));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.FARMLAND.create(TextureMapping.getBlockTexture(Blocks.FARMLAND, "_moist"), $$1, this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.FARMLAND).with(BlockModelGenerators.createEmptyOrFullDispatch(BlockStateProperties.MOISTURE, 7, $$3, $$2)));
    }

    private MultiVariant createFloorFireModels(Block $$0) {
        return BlockModelGenerators.a(BlockModelGenerators.plainModel(ModelTemplates.FIRE_FLOOR.create(ModelLocationUtils.getModelLocation($$0, "_floor0"), TextureMapping.fire0($$0), this.modelOutput)), BlockModelGenerators.plainModel(ModelTemplates.FIRE_FLOOR.create(ModelLocationUtils.getModelLocation($$0, "_floor1"), TextureMapping.fire1($$0), this.modelOutput)));
    }

    private MultiVariant createSideFireModels(Block $$0) {
        return BlockModelGenerators.a(BlockModelGenerators.plainModel(ModelTemplates.FIRE_SIDE.create(ModelLocationUtils.getModelLocation($$0, "_side0"), TextureMapping.fire0($$0), this.modelOutput)), BlockModelGenerators.plainModel(ModelTemplates.FIRE_SIDE.create(ModelLocationUtils.getModelLocation($$0, "_side1"), TextureMapping.fire1($$0), this.modelOutput)), BlockModelGenerators.plainModel(ModelTemplates.FIRE_SIDE_ALT.create(ModelLocationUtils.getModelLocation($$0, "_side_alt0"), TextureMapping.fire0($$0), this.modelOutput)), BlockModelGenerators.plainModel(ModelTemplates.FIRE_SIDE_ALT.create(ModelLocationUtils.getModelLocation($$0, "_side_alt1"), TextureMapping.fire1($$0), this.modelOutput)));
    }

    private MultiVariant createTopFireModels(Block $$0) {
        return BlockModelGenerators.a(BlockModelGenerators.plainModel(ModelTemplates.FIRE_UP.create(ModelLocationUtils.getModelLocation($$0, "_up0"), TextureMapping.fire0($$0), this.modelOutput)), BlockModelGenerators.plainModel(ModelTemplates.FIRE_UP.create(ModelLocationUtils.getModelLocation($$0, "_up1"), TextureMapping.fire1($$0), this.modelOutput)), BlockModelGenerators.plainModel(ModelTemplates.FIRE_UP_ALT.create(ModelLocationUtils.getModelLocation($$0, "_up_alt0"), TextureMapping.fire0($$0), this.modelOutput)), BlockModelGenerators.plainModel(ModelTemplates.FIRE_UP_ALT.create(ModelLocationUtils.getModelLocation($$0, "_up_alt1"), TextureMapping.fire1($$0), this.modelOutput)));
    }

    private void createFire() {
        ConditionBuilder $$0 = BlockModelGenerators.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false).term(BlockStateProperties.UP, false);
        MultiVariant $$1 = this.createFloorFireModels(Blocks.FIRE);
        MultiVariant $$2 = this.createSideFireModels(Blocks.FIRE);
        MultiVariant $$3 = this.createTopFireModels(Blocks.FIRE);
        this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.FIRE).with($$0, $$1).with(BlockModelGenerators.a(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true), $$0), $$2).with(BlockModelGenerators.a(BlockModelGenerators.condition().term(BlockStateProperties.EAST, true), $$0), $$2.with(Y_ROT_90)).with(BlockModelGenerators.a(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, true), $$0), $$2.with(Y_ROT_180)).with(BlockModelGenerators.a(BlockModelGenerators.condition().term(BlockStateProperties.WEST, true), $$0), $$2.with(Y_ROT_270)).with(BlockModelGenerators.condition().term(BlockStateProperties.UP, true), $$3));
    }

    private void createSoulFire() {
        MultiVariant $$0 = this.createFloorFireModels(Blocks.SOUL_FIRE);
        MultiVariant $$1 = this.createSideFireModels(Blocks.SOUL_FIRE);
        this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.SOUL_FIRE).with($$0).with($$1).with($$1.with(Y_ROT_90)).with($$1.with(Y_ROT_180)).with($$1.with(Y_ROT_270)));
    }

    private void createLantern(Block $$0) {
        MultiVariant $$1 = BlockModelGenerators.plainVariant(TexturedModel.LANTERN.create($$0, this.modelOutput));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(TexturedModel.HANGING_LANTERN.create($$0, this.modelOutput));
        this.registerSimpleFlatItemModel($$0.asItem());
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.HANGING, $$2, $$1)));
    }

    private void createMuddyMangroveRoots() {
        TextureMapping $$0 = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.MUDDY_MANGROVE_ROOTS, "_side"), TextureMapping.getBlockTexture(Blocks.MUDDY_MANGROVE_ROOTS, "_top"));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_COLUMN.create(Blocks.MUDDY_MANGROVE_ROOTS, $$0, this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createAxisAlignedPillarBlock(Blocks.MUDDY_MANGROVE_ROOTS, $$1));
    }

    private void createMangrovePropagule() {
        this.registerSimpleFlatItemModel(Items.MANGROVE_PROPAGULE);
        Block $$0 = Blocks.MANGROVE_PROPAGULE;
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.MANGROVE_PROPAGULE).with(PropertyDispatch.initial(MangrovePropaguleBlock.HANGING, MangrovePropaguleBlock.AGE).generate(($$2, $$3) -> $$2 != false ? BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0, "_hanging_" + $$3)) : $$1)));
    }

    private void createFrostedIce() {
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.FROSTED_ICE).with(PropertyDispatch.initial(BlockStateProperties.AGE_3).select(0, BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.FROSTED_ICE, "_0", ModelTemplates.CUBE_ALL, TextureMapping::cube))).select(1, BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.FROSTED_ICE, "_1", ModelTemplates.CUBE_ALL, TextureMapping::cube))).select(2, BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.FROSTED_ICE, "_2", ModelTemplates.CUBE_ALL, TextureMapping::cube))).select(3, BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.FROSTED_ICE, "_3", ModelTemplates.CUBE_ALL, TextureMapping::cube)))));
    }

    private void createGrassBlocks() {
        ResourceLocation $$0 = TextureMapping.getBlockTexture(Blocks.DIRT);
        TextureMapping $$12 = new TextureMapping().put(TextureSlot.BOTTOM, $$0).copyForced(TextureSlot.BOTTOM, TextureSlot.PARTICLE).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.GRASS_BLOCK, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.GRASS_BLOCK, "_snow"));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.GRASS_BLOCK, "_snow", $$12, this.modelOutput));
        ResourceLocation $$3 = ModelLocationUtils.getModelLocation(Blocks.GRASS_BLOCK);
        this.createGrassLikeBlock(Blocks.GRASS_BLOCK, BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel($$3)), $$2);
        this.registerSimpleTintedItemModel(Blocks.GRASS_BLOCK, $$3, new GrassColorSource());
        MultiVariant $$4 = BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel(TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.MYCELIUM).updateTextures($$1 -> $$1.put(TextureSlot.BOTTOM, $$0)).create(Blocks.MYCELIUM, this.modelOutput)));
        this.createGrassLikeBlock(Blocks.MYCELIUM, $$4, $$2);
        MultiVariant $$5 = BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel(TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.PODZOL).updateTextures($$1 -> $$1.put(TextureSlot.BOTTOM, $$0)).create(Blocks.PODZOL, this.modelOutput)));
        this.createGrassLikeBlock(Blocks.PODZOL, $$5, $$2);
    }

    private void createGrassLikeBlock(Block $$0, MultiVariant $$1, MultiVariant $$2) {
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.SNOWY).select(true, $$2).select(false, $$1)));
    }

    private void createCocoa() {
        this.registerSimpleFlatItemModel(Items.COCOA_BEANS);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.COCOA).with(PropertyDispatch.initial(BlockStateProperties.AGE_2).select(0, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage0"))).select(1, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage1"))).select(2, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage2")))).with(ROTATION_HORIZONTAL_FACING_ALT));
    }

    private void createDirtPath() {
        Variant $$0 = BlockModelGenerators.plainModel(ModelLocationUtils.getModelLocation(Blocks.DIRT_PATH));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.DIRT_PATH, BlockModelGenerators.createRotatedVariants($$0)));
    }

    private void createWeightedPressurePlate(Block $$0, Block $$1) {
        TextureMapping $$2 = TextureMapping.defaultTexture($$1);
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.PRESSURE_PLATE_UP.create($$0, $$2, this.modelOutput));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.PRESSURE_PLATE_DOWN.create($$0, $$2, this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(BlockModelGenerators.createEmptyOrFullDispatch(BlockStateProperties.POWER, 1, $$4, $$3)));
    }

    private void createHopper() {
        MultiVariant $$0 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.HOPPER));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.HOPPER, "_side"));
        this.registerSimpleFlatItemModel(Items.HOPPER);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.HOPPER).with(PropertyDispatch.initial(BlockStateProperties.FACING_HOPPER).select(Direction.DOWN, $$0).select(Direction.NORTH, $$1).select(Direction.EAST, $$1.with(Y_ROT_90)).select(Direction.SOUTH, $$1.with(Y_ROT_180)).select(Direction.WEST, $$1.with(Y_ROT_270))));
    }

    private void copyModel(Block $$0, Block $$1) {
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$1, $$2));
        this.itemModelOutput.copy($$0.asItem(), $$1.asItem());
    }

    private void createIronBars() {
        MultiVariant $$0 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_post_ends"));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_post"));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_cap"));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_cap_alt"));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_side"));
        MultiVariant $$5 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_side_alt"));
        this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.IRON_BARS).with($$0).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), $$1).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), $$2).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, true).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), $$2.with(Y_ROT_90)).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, true).term(BlockStateProperties.WEST, false), $$3).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, true), $$3.with(Y_ROT_90)).with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true), $$4).with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, true), $$4.with(Y_ROT_90)).with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, true), $$5).with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, true), $$5.with(Y_ROT_90)));
        this.registerSimpleFlatItemModel(Blocks.IRON_BARS);
    }

    private void createNonTemplateHorizontalBlock(Block $$0) {
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0))).with(ROTATION_HORIZONTAL_FACING));
    }

    private void createLever() {
        MultiVariant $$0 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.LEVER));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.LEVER, "_on"));
        this.registerSimpleFlatItemModel(Blocks.LEVER);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.LEVER).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.POWERED, $$0, $$1)).with(PropertyDispatch.modify(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.CEILING, Direction.NORTH, X_ROT_180.then(Y_ROT_180)).select(AttachFace.CEILING, Direction.EAST, X_ROT_180.then(Y_ROT_270)).select(AttachFace.CEILING, Direction.SOUTH, X_ROT_180).select(AttachFace.CEILING, Direction.WEST, X_ROT_180.then(Y_ROT_90)).select(AttachFace.FLOOR, Direction.NORTH, NOP).select(AttachFace.FLOOR, Direction.EAST, Y_ROT_90).select(AttachFace.FLOOR, Direction.SOUTH, Y_ROT_180).select(AttachFace.FLOOR, Direction.WEST, Y_ROT_270).select(AttachFace.WALL, Direction.NORTH, X_ROT_90).select(AttachFace.WALL, Direction.EAST, X_ROT_90.then(Y_ROT_90)).select(AttachFace.WALL, Direction.SOUTH, X_ROT_90.then(Y_ROT_180)).select(AttachFace.WALL, Direction.WEST, X_ROT_90.then(Y_ROT_270))));
    }

    private void createLilyPad() {
        ResourceLocation $$0 = this.createFlatItemModelWithBlockTexture(Items.LILY_PAD, Blocks.LILY_PAD);
        this.registerSimpleTintedItemModel(Blocks.LILY_PAD, $$0, ItemModelUtils.constantTint(-9321636));
        Variant $$1 = BlockModelGenerators.plainModel(ModelLocationUtils.getModelLocation(Blocks.LILY_PAD));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.LILY_PAD, BlockModelGenerators.createRotatedVariants($$1)));
    }

    private void createFrogspawnBlock() {
        this.registerSimpleFlatItemModel(Blocks.FROGSPAWN);
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(Blocks.FROGSPAWN, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.FROGSPAWN))));
    }

    private void createNetherPortalBlock() {
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.NETHER_PORTAL).with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_AXIS).select(Direction.Axis.X, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.NETHER_PORTAL, "_ns"))).select(Direction.Axis.Z, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.NETHER_PORTAL, "_ew")))));
    }

    private void createNetherrack() {
        Variant $$0 = BlockModelGenerators.plainModel(TexturedModel.CUBE.create(Blocks.NETHERRACK, this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.NETHERRACK, BlockModelGenerators.a($$0, $$0.with(X_ROT_90), $$0.with(X_ROT_180), $$0.with(X_ROT_270), $$0.with(Y_ROT_90), $$0.with(Y_ROT_90.then(X_ROT_90)), $$0.with(Y_ROT_90.then(X_ROT_180)), $$0.with(Y_ROT_90.then(X_ROT_270)), $$0.with(Y_ROT_180), $$0.with(Y_ROT_180.then(X_ROT_90)), $$0.with(Y_ROT_180.then(X_ROT_180)), $$0.with(Y_ROT_180.then(X_ROT_270)), $$0.with(Y_ROT_270), $$0.with(Y_ROT_270.then(X_ROT_90)), $$0.with(Y_ROT_270.then(X_ROT_180)), $$0.with(Y_ROT_270.then(X_ROT_270)))));
    }

    private void createObserver() {
        MultiVariant $$0 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.OBSERVER));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.OBSERVER, "_on"));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.OBSERVER).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.POWERED, $$1, $$0)).with(ROTATION_FACING));
    }

    private void createPistons() {
        TextureMapping $$0 = new TextureMapping().put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.PISTON, "_bottom")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
        ResourceLocation $$1 = TextureMapping.getBlockTexture(Blocks.PISTON, "_top_sticky");
        ResourceLocation $$2 = TextureMapping.getBlockTexture(Blocks.PISTON, "_top");
        TextureMapping $$3 = $$0.copyAndUpdate(TextureSlot.PLATFORM, $$1);
        TextureMapping $$4 = $$0.copyAndUpdate(TextureSlot.PLATFORM, $$2);
        MultiVariant $$5 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.PISTON, "_base"));
        this.createPistonVariant(Blocks.PISTON, $$5, $$4);
        this.createPistonVariant(Blocks.STICKY_PISTON, $$5, $$3);
        ResourceLocation $$6 = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.PISTON, "_inventory", $$0.copyAndUpdate(TextureSlot.TOP, $$2), this.modelOutput);
        ResourceLocation $$7 = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.STICKY_PISTON, "_inventory", $$0.copyAndUpdate(TextureSlot.TOP, $$1), this.modelOutput);
        this.registerSimpleItemModel(Blocks.PISTON, $$6);
        this.registerSimpleItemModel(Blocks.STICKY_PISTON, $$7);
    }

    private void createPistonVariant(Block $$0, MultiVariant $$1, TextureMapping $$2) {
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.PISTON.create($$0, $$2, this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.EXTENDED, $$1, $$3)).with(ROTATION_FACING));
    }

    private void createPistonHeads() {
        TextureMapping $$0 = new TextureMapping().put(TextureSlot.UNSTICKY, TextureMapping.getBlockTexture(Blocks.PISTON, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
        TextureMapping $$1 = $$0.copyAndUpdate(TextureSlot.PLATFORM, TextureMapping.getBlockTexture(Blocks.PISTON, "_top_sticky"));
        TextureMapping $$2 = $$0.copyAndUpdate(TextureSlot.PLATFORM, TextureMapping.getBlockTexture(Blocks.PISTON, "_top"));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.PISTON_HEAD).with(PropertyDispatch.initial(BlockStateProperties.SHORT, BlockStateProperties.PISTON_TYPE).select(false, PistonType.DEFAULT, BlockModelGenerators.plainVariant(ModelTemplates.PISTON_HEAD.createWithSuffix(Blocks.PISTON, "_head", $$2, this.modelOutput))).select(false, PistonType.STICKY, BlockModelGenerators.plainVariant(ModelTemplates.PISTON_HEAD.createWithSuffix(Blocks.PISTON, "_head_sticky", $$1, this.modelOutput))).select(true, PistonType.DEFAULT, BlockModelGenerators.plainVariant(ModelTemplates.PISTON_HEAD_SHORT.createWithSuffix(Blocks.PISTON, "_head_short", $$2, this.modelOutput))).select(true, PistonType.STICKY, BlockModelGenerators.plainVariant(ModelTemplates.PISTON_HEAD_SHORT.createWithSuffix(Blocks.PISTON, "_head_short_sticky", $$1, this.modelOutput)))).with(ROTATION_FACING));
    }

    private void createTrialSpawner() {
        Block $$0 = Blocks.TRIAL_SPAWNER;
        TextureMapping $$1 = TextureMapping.trialSpawner($$0, "_side_inactive", "_top_inactive");
        TextureMapping $$2 = TextureMapping.trialSpawner($$0, "_side_active", "_top_active");
        TextureMapping $$3 = TextureMapping.trialSpawner($$0, "_side_active", "_top_ejecting_reward");
        TextureMapping $$4 = TextureMapping.trialSpawner($$0, "_side_inactive_ominous", "_top_inactive_ominous");
        TextureMapping $$5 = TextureMapping.trialSpawner($$0, "_side_active_ominous", "_top_active_ominous");
        TextureMapping $$62 = TextureMapping.trialSpawner($$0, "_side_active_ominous", "_top_ejecting_reward_ominous");
        ResourceLocation $$72 = ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.create($$0, $$1, this.modelOutput);
        MultiVariant $$8 = BlockModelGenerators.plainVariant($$72);
        MultiVariant $$9 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix($$0, "_active", $$2, this.modelOutput));
        MultiVariant $$10 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix($$0, "_ejecting_reward", $$3, this.modelOutput));
        MultiVariant $$11 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix($$0, "_inactive_ominous", $$4, this.modelOutput));
        MultiVariant $$12 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix($$0, "_active_ominous", $$5, this.modelOutput));
        MultiVariant $$13 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix($$0, "_ejecting_reward_ominous", $$62, this.modelOutput));
        this.registerSimpleItemModel($$0, $$72);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.TRIAL_SPAWNER_STATE, BlockStateProperties.OMINOUS).generate(($$6, $$7) -> switch ($$6) {
            default -> throw new MatchException(null, null);
            case TrialSpawnerState.INACTIVE, TrialSpawnerState.COOLDOWN -> {
                if ($$7.booleanValue()) {
                    yield $$11;
                }
                yield $$8;
            }
            case TrialSpawnerState.WAITING_FOR_PLAYERS, TrialSpawnerState.ACTIVE, TrialSpawnerState.WAITING_FOR_REWARD_EJECTION -> {
                if ($$7.booleanValue()) {
                    yield $$12;
                }
                yield $$9;
            }
            case TrialSpawnerState.EJECTING_REWARD -> $$7 != false ? $$13 : $$10;
        })));
    }

    private void createVault() {
        Block $$0 = Blocks.VAULT;
        TextureMapping $$1 = TextureMapping.vault($$0, "_front_off", "_side_off", "_top", "_bottom");
        TextureMapping $$2 = TextureMapping.vault($$0, "_front_on", "_side_on", "_top", "_bottom");
        TextureMapping $$3 = TextureMapping.vault($$0, "_front_ejecting", "_side_on", "_top", "_bottom");
        TextureMapping $$4 = TextureMapping.vault($$0, "_front_ejecting", "_side_on", "_top_ejecting", "_bottom");
        ResourceLocation $$5 = ModelTemplates.VAULT.create($$0, $$1, this.modelOutput);
        MultiVariant $$6 = BlockModelGenerators.plainVariant($$5);
        MultiVariant $$7 = BlockModelGenerators.plainVariant(ModelTemplates.VAULT.createWithSuffix($$0, "_active", $$2, this.modelOutput));
        MultiVariant $$82 = BlockModelGenerators.plainVariant(ModelTemplates.VAULT.createWithSuffix($$0, "_unlocking", $$3, this.modelOutput));
        MultiVariant $$92 = BlockModelGenerators.plainVariant(ModelTemplates.VAULT.createWithSuffix($$0, "_ejecting_reward", $$4, this.modelOutput));
        TextureMapping $$10 = TextureMapping.vault($$0, "_front_off_ominous", "_side_off_ominous", "_top_ominous", "_bottom_ominous");
        TextureMapping $$11 = TextureMapping.vault($$0, "_front_on_ominous", "_side_on_ominous", "_top_ominous", "_bottom_ominous");
        TextureMapping $$12 = TextureMapping.vault($$0, "_front_ejecting_ominous", "_side_on_ominous", "_top_ominous", "_bottom_ominous");
        TextureMapping $$13 = TextureMapping.vault($$0, "_front_ejecting_ominous", "_side_on_ominous", "_top_ejecting_ominous", "_bottom_ominous");
        MultiVariant $$14 = BlockModelGenerators.plainVariant(ModelTemplates.VAULT.createWithSuffix($$0, "_ominous", $$10, this.modelOutput));
        MultiVariant $$15 = BlockModelGenerators.plainVariant(ModelTemplates.VAULT.createWithSuffix($$0, "_active_ominous", $$11, this.modelOutput));
        MultiVariant $$16 = BlockModelGenerators.plainVariant(ModelTemplates.VAULT.createWithSuffix($$0, "_unlocking_ominous", $$12, this.modelOutput));
        MultiVariant $$17 = BlockModelGenerators.plainVariant(ModelTemplates.VAULT.createWithSuffix($$0, "_ejecting_reward_ominous", $$13, this.modelOutput));
        this.registerSimpleItemModel($$0, $$5);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(VaultBlock.STATE, VaultBlock.OMINOUS).generate(($$8, $$9) -> switch ($$8) {
            default -> throw new MatchException(null, null);
            case VaultState.INACTIVE -> {
                if ($$9.booleanValue()) {
                    yield $$14;
                }
                yield $$6;
            }
            case VaultState.ACTIVE -> {
                if ($$9.booleanValue()) {
                    yield $$15;
                }
                yield $$7;
            }
            case VaultState.UNLOCKING -> {
                if ($$9.booleanValue()) {
                    yield $$16;
                }
                yield $$82;
            }
            case VaultState.EJECTING -> $$9 != false ? $$17 : $$92;
        })).with(ROTATION_HORIZONTAL_FACING));
    }

    private void createSculkSensor() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.SCULK_SENSOR, "_inactive");
        MultiVariant $$1 = BlockModelGenerators.plainVariant($$0);
        MultiVariant $$22 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.SCULK_SENSOR, "_active"));
        this.registerSimpleItemModel(Blocks.SCULK_SENSOR, $$0);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SCULK_SENSOR).with(PropertyDispatch.initial(BlockStateProperties.SCULK_SENSOR_PHASE).generate($$2 -> $$2 == SculkSensorPhase.ACTIVE || $$2 == SculkSensorPhase.COOLDOWN ? $$22 : $$1)));
    }

    private void createCalibratedSculkSensor() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.CALIBRATED_SCULK_SENSOR, "_inactive");
        MultiVariant $$1 = BlockModelGenerators.plainVariant($$0);
        MultiVariant $$22 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.CALIBRATED_SCULK_SENSOR, "_active"));
        this.registerSimpleItemModel(Blocks.CALIBRATED_SCULK_SENSOR, $$0);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.CALIBRATED_SCULK_SENSOR).with(PropertyDispatch.initial(BlockStateProperties.SCULK_SENSOR_PHASE).generate($$2 -> $$2 == SculkSensorPhase.ACTIVE || $$2 == SculkSensorPhase.COOLDOWN ? $$22 : $$1)).with(ROTATION_HORIZONTAL_FACING));
    }

    private void createSculkShrieker() {
        ResourceLocation $$0 = ModelTemplates.SCULK_SHRIEKER.create(Blocks.SCULK_SHRIEKER, TextureMapping.sculkShrieker(false), this.modelOutput);
        MultiVariant $$1 = BlockModelGenerators.plainVariant($$0);
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.SCULK_SHRIEKER.createWithSuffix(Blocks.SCULK_SHRIEKER, "_can_summon", TextureMapping.sculkShrieker(true), this.modelOutput));
        this.registerSimpleItemModel(Blocks.SCULK_SHRIEKER, $$0);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SCULK_SHRIEKER).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.CAN_SUMMON, $$2, $$1)));
    }

    private void createScaffolding() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.SCAFFOLDING, "_stable");
        MultiVariant $$1 = BlockModelGenerators.plainVariant($$0);
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.SCAFFOLDING, "_unstable"));
        this.registerSimpleItemModel(Blocks.SCAFFOLDING, $$0);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SCAFFOLDING).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.BOTTOM, $$2, $$1)));
    }

    private void createCaveVines() {
        MultiVariant $$0 = BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.CAVE_VINES, "", ModelTemplates.CROSS, TextureMapping::cross));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.CAVE_VINES, "_lit", ModelTemplates.CROSS, TextureMapping::cross));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.CAVE_VINES).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.BERRIES, $$1, $$0)));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.CAVE_VINES_PLANT, "", ModelTemplates.CROSS, TextureMapping::cross));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.CAVE_VINES_PLANT, "_lit", ModelTemplates.CROSS, TextureMapping::cross));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.CAVE_VINES_PLANT).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.BERRIES, $$3, $$2)));
    }

    private void createRedstoneLamp() {
        MultiVariant $$0 = BlockModelGenerators.plainVariant(TexturedModel.CUBE.create(Blocks.REDSTONE_LAMP, this.modelOutput));
        MultiVariant $$1 = BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.REDSTONE_LAMP, "_on", ModelTemplates.CUBE_ALL, TextureMapping::cube));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.REDSTONE_LAMP).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, $$1, $$0)));
    }

    private void createNormalTorch(Block $$0, Block $$1) {
        TextureMapping $$2 = TextureMapping.torch($$0);
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, BlockModelGenerators.plainVariant(ModelTemplates.TORCH.create($$0, $$2, this.modelOutput))));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$1, BlockModelGenerators.plainVariant(ModelTemplates.WALL_TORCH.create($$1, $$2, this.modelOutput))).with(ROTATION_TORCH));
        this.registerSimpleFlatItemModel($$0);
    }

    private void createRedstoneTorch() {
        TextureMapping $$0 = TextureMapping.torch(Blocks.REDSTONE_TORCH);
        TextureMapping $$1 = TextureMapping.torch(TextureMapping.getBlockTexture(Blocks.REDSTONE_TORCH, "_off"));
        MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.REDSTONE_TORCH.create(Blocks.REDSTONE_TORCH, $$0, this.modelOutput));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.TORCH_UNLIT.createWithSuffix(Blocks.REDSTONE_TORCH, "_off", $$1, this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.REDSTONE_TORCH).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, $$2, $$3)));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.REDSTONE_WALL_TORCH.create(Blocks.REDSTONE_WALL_TORCH, $$0, this.modelOutput));
        MultiVariant $$5 = BlockModelGenerators.plainVariant(ModelTemplates.WALL_TORCH_UNLIT.createWithSuffix(Blocks.REDSTONE_WALL_TORCH, "_off", $$1, this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.REDSTONE_WALL_TORCH).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, $$4, $$5)).with(ROTATION_TORCH));
        this.registerSimpleFlatItemModel(Blocks.REDSTONE_TORCH);
    }

    private void createRepeater() {
        this.registerSimpleFlatItemModel(Items.REPEATER);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.REPEATER).with(PropertyDispatch.initial(BlockStateProperties.DELAY, BlockStateProperties.LOCKED, BlockStateProperties.POWERED).generate((Function3<Integer, Boolean, Boolean, MultiVariant>)((Function3)($$0, $$1, $$2) -> {
            StringBuilder $$3 = new StringBuilder();
            $$3.append('_').append($$0).append("tick");
            if ($$2.booleanValue()) {
                $$3.append("_on");
            }
            if ($$1.booleanValue()) {
                $$3.append("_locked");
            }
            return BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(Blocks.REPEATER, $$3.toString()));
        }))).with(ROTATION_HORIZONTAL_FACING_ALT));
    }

    private void createSeaPickle() {
        this.registerSimpleFlatItemModel(Items.SEA_PICKLE);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SEA_PICKLE).with(PropertyDispatch.initial(BlockStateProperties.PICKLES, BlockStateProperties.WATERLOGGED).select(1, false, BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel(ModelLocationUtils.decorateBlockModelLocation("dead_sea_pickle")))).select(2, false, BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel(ModelLocationUtils.decorateBlockModelLocation("two_dead_sea_pickles")))).select(3, false, BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel(ModelLocationUtils.decorateBlockModelLocation("three_dead_sea_pickles")))).select(4, false, BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel(ModelLocationUtils.decorateBlockModelLocation("four_dead_sea_pickles")))).select(1, true, BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel(ModelLocationUtils.decorateBlockModelLocation("sea_pickle")))).select(2, true, BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel(ModelLocationUtils.decorateBlockModelLocation("two_sea_pickles")))).select(3, true, BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel(ModelLocationUtils.decorateBlockModelLocation("three_sea_pickles")))).select(4, true, BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel(ModelLocationUtils.decorateBlockModelLocation("four_sea_pickles"))))));
    }

    private void createSnowBlocks() {
        TextureMapping $$0 = TextureMapping.cube(Blocks.SNOW);
        MultiVariant $$12 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ALL.create(Blocks.SNOW_BLOCK, $$0, this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SNOW).with(PropertyDispatch.initial(BlockStateProperties.LAYERS).generate($$1 -> $$1 < 8 ? BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.SNOW, "_height" + $$1 * 2)) : $$12)));
        this.registerSimpleItemModel(Blocks.SNOW, ModelLocationUtils.getModelLocation(Blocks.SNOW, "_height2"));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(Blocks.SNOW_BLOCK, $$12));
    }

    private void createStonecutter() {
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.STONECUTTER, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.STONECUTTER))).with(ROTATION_HORIZONTAL_FACING));
    }

    private void createStructureBlock() {
        ResourceLocation $$02 = TexturedModel.CUBE.create(Blocks.STRUCTURE_BLOCK, this.modelOutput);
        this.registerSimpleItemModel(Blocks.STRUCTURE_BLOCK, $$02);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.STRUCTURE_BLOCK).with(PropertyDispatch.initial(BlockStateProperties.STRUCTUREBLOCK_MODE).generate($$0 -> BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.STRUCTURE_BLOCK, "_" + $$0.getSerializedName(), ModelTemplates.CUBE_ALL, TextureMapping::cube)))));
    }

    private void createTestBlock() {
        HashMap<TestBlockMode, ResourceLocation> $$0 = new HashMap<TestBlockMode, ResourceLocation>();
        for (TestBlockMode $$12 : TestBlockMode.values()) {
            $$0.put($$12, this.createSuffixedVariant(Blocks.TEST_BLOCK, "_" + $$12.getSerializedName(), ModelTemplates.CUBE_ALL, TextureMapping::cube));
        }
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.TEST_BLOCK).with(PropertyDispatch.initial(BlockStateProperties.TEST_BLOCK_MODE).generate($$1 -> BlockModelGenerators.plainVariant((ResourceLocation)$$0.get($$1)))));
        this.itemModelOutput.accept(Items.TEST_BLOCK, ItemModelUtils.selectBlockItemProperty(TestBlock.MODE, ItemModelUtils.plainModel((ResourceLocation)$$0.get(TestBlockMode.START)), Map.of((Object)TestBlockMode.FAIL, (Object)ItemModelUtils.plainModel((ResourceLocation)$$0.get(TestBlockMode.FAIL)), (Object)TestBlockMode.LOG, (Object)ItemModelUtils.plainModel((ResourceLocation)$$0.get(TestBlockMode.LOG)), (Object)TestBlockMode.ACCEPT, (Object)ItemModelUtils.plainModel((ResourceLocation)$$0.get(TestBlockMode.ACCEPT)))));
    }

    private void createSweetBerryBush() {
        this.registerSimpleFlatItemModel(Items.SWEET_BERRIES);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SWEET_BERRY_BUSH).with(PropertyDispatch.initial(BlockStateProperties.AGE_3).generate($$0 -> BlockModelGenerators.plainVariant(this.createSuffixedVariant(Blocks.SWEET_BERRY_BUSH, "_stage" + $$0, ModelTemplates.CROSS, TextureMapping::cross)))));
    }

    private void createTripwire() {
        this.registerSimpleFlatItemModel(Items.STRING);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.TRIPWIRE).with(PropertyDispatch.initial(BlockStateProperties.ATTACHED, BlockStateProperties.EAST, BlockStateProperties.NORTH, BlockStateProperties.SOUTH, BlockStateProperties.WEST).select(false, false, false, false, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns"))).select(false, true, false, false, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n")).with(Y_ROT_90)).select(false, false, true, false, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n"))).select(false, false, false, true, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n")).with(Y_ROT_180)).select(false, false, false, false, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n")).with(Y_ROT_270)).select(false, true, true, false, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne"))).select(false, true, false, true, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(Y_ROT_90)).select(false, false, false, true, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(Y_ROT_180)).select(false, false, true, false, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(Y_ROT_270)).select(false, false, true, true, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns"))).select(false, true, false, false, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns")).with(Y_ROT_90)).select(false, true, true, true, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse"))).select(false, true, false, true, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(Y_ROT_90)).select(false, false, true, true, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(Y_ROT_180)).select(false, true, true, false, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(Y_ROT_270)).select(false, true, true, true, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nsew"))).select(true, false, false, false, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns"))).select(true, false, true, false, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n"))).select(true, false, false, true, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(Y_ROT_180)).select(true, true, false, false, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(Y_ROT_90)).select(true, false, false, false, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(Y_ROT_270)).select(true, true, true, false, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne"))).select(true, true, false, true, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(Y_ROT_90)).select(true, false, false, true, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(Y_ROT_180)).select(true, false, true, false, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(Y_ROT_270)).select(true, false, true, true, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns"))).select(true, true, false, false, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns")).with(Y_ROT_90)).select(true, true, true, true, false, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse"))).select(true, true, false, true, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(Y_ROT_90)).select(true, false, true, true, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(Y_ROT_180)).select(true, true, true, false, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(Y_ROT_270)).select(true, true, true, true, true, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nsew")))));
    }

    private void createTripwireHook() {
        this.registerSimpleFlatItemModel(Blocks.TRIPWIRE_HOOK);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.TRIPWIRE_HOOK).with(PropertyDispatch.initial(BlockStateProperties.ATTACHED, BlockStateProperties.POWERED).generate(($$0, $$1) -> BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE_HOOK, ($$0 != false ? "_attached" : "") + ($$1 != false ? "_on" : ""))))).with(ROTATION_HORIZONTAL_FACING));
    }

    private Variant createTurtleEggModel(int $$0, String $$1, TextureMapping $$2) {
        return switch ($$0) {
            case 1 -> BlockModelGenerators.plainModel(ModelTemplates.TURTLE_EGG.create(ModelLocationUtils.decorateBlockModelLocation($$1 + "turtle_egg"), $$2, this.modelOutput));
            case 2 -> BlockModelGenerators.plainModel(ModelTemplates.TWO_TURTLE_EGGS.create(ModelLocationUtils.decorateBlockModelLocation("two_" + $$1 + "turtle_eggs"), $$2, this.modelOutput));
            case 3 -> BlockModelGenerators.plainModel(ModelTemplates.THREE_TURTLE_EGGS.create(ModelLocationUtils.decorateBlockModelLocation("three_" + $$1 + "turtle_eggs"), $$2, this.modelOutput));
            case 4 -> BlockModelGenerators.plainModel(ModelTemplates.FOUR_TURTLE_EGGS.create(ModelLocationUtils.decorateBlockModelLocation("four_" + $$1 + "turtle_eggs"), $$2, this.modelOutput));
            default -> throw new UnsupportedOperationException();
        };
    }

    private Variant createTurtleEggModel(int $$0, int $$1) {
        return switch ($$1) {
            case 0 -> this.createTurtleEggModel($$0, "", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG)));
            case 1 -> this.createTurtleEggModel($$0, "slightly_cracked_", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG, "_slightly_cracked")));
            case 2 -> this.createTurtleEggModel($$0, "very_cracked_", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG, "_very_cracked")));
            default -> throw new UnsupportedOperationException();
        };
    }

    private void createTurtleEgg() {
        this.registerSimpleFlatItemModel(Items.TURTLE_EGG);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.TURTLE_EGG).with(PropertyDispatch.initial(BlockStateProperties.EGGS, BlockStateProperties.HATCH).generate(($$0, $$1) -> BlockModelGenerators.createRotatedVariants(this.createTurtleEggModel((int)$$0, (int)$$1)))));
    }

    private void createDriedGhastBlock() {
        ResourceLocation $$02 = ModelLocationUtils.getModelLocation(Blocks.DRIED_GHAST, "_hydration_0");
        this.registerSimpleItemModel(Blocks.DRIED_GHAST, $$02);
        Function<Integer, ResourceLocation> $$12 = $$0 -> {
            String $$1 = switch ($$0) {
                case 1 -> "_hydration_1";
                case 2 -> "_hydration_2";
                case 3 -> "_hydration_3";
                default -> "_hydration_0";
            };
            TextureMapping $$2 = TextureMapping.driedGhast($$1);
            return ModelTemplates.DRIED_GHAST.createWithSuffix(Blocks.DRIED_GHAST, $$1, $$2, this.modelOutput);
        };
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.DRIED_GHAST).with(PropertyDispatch.initial(DriedGhastBlock.HYDRATION_LEVEL).generate($$1 -> BlockModelGenerators.plainVariant((ResourceLocation)$$12.apply((Integer)$$1)))).with(ROTATION_HORIZONTAL_FACING));
    }

    private void createSnifferEgg() {
        this.registerSimpleFlatItemModel(Items.SNIFFER_EGG);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SNIFFER_EGG).with(PropertyDispatch.initial(SnifferEggBlock.HATCH).generate($$0 -> {
            String $$1 = switch ($$0) {
                case 1 -> "_slightly_cracked";
                case 2 -> "_very_cracked";
                default -> "_not_cracked";
            };
            TextureMapping $$2 = TextureMapping.snifferEgg($$1);
            return BlockModelGenerators.plainVariant(ModelTemplates.SNIFFER_EGG.createWithSuffix(Blocks.SNIFFER_EGG, $$1, $$2, this.modelOutput));
        })));
    }

    private void createMultiface(Block $$0) {
        this.registerSimpleFlatItemModel($$0);
        this.createMultifaceBlockStates($$0);
    }

    private void createMultiface(Block $$0, Item $$1) {
        this.registerSimpleFlatItemModel($$1);
        this.createMultifaceBlockStates($$0);
    }

    private static <T extends Property<?>> Map<T, VariantMutator> selectMultifaceProperties(StateHolder<?, ?> $$0, Function<Direction, T> $$1) {
        ImmutableMap.Builder $$2 = ImmutableMap.builderWithExpectedSize(MULTIFACE_GENERATOR.size());
        MULTIFACE_GENERATOR.forEach(($$3, $$4) -> {
            Property $$5 = (Property)$$1.apply((Direction)$$3);
            if ($$0.hasProperty($$5)) {
                $$2.put($$5, $$4);
            }
        });
        return $$2.build();
    }

    private void createMultifaceBlockStates(Block $$0) {
        Map<Property, VariantMutator> $$12 = BlockModelGenerators.selectMultifaceProperties($$0.defaultBlockState(), MultifaceBlock::getFaceProperty);
        ConditionBuilder $$22 = BlockModelGenerators.condition();
        $$12.forEach(($$1, $$2) -> $$22.term($$1, false));
        MultiVariant $$32 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0));
        MultiPartGenerator $$42 = MultiPartGenerator.multiPart($$0);
        $$12.forEach(($$3, $$4) -> {
            $$42.with(BlockModelGenerators.condition().term($$3, true), $$32.with((VariantMutator)$$4));
            $$42.with($$22, $$32.with((VariantMutator)$$4));
        });
        this.blockStateOutput.accept($$42);
    }

    private void createMossyCarpet(Block $$0) {
        Map<Property, VariantMutator> $$12 = BlockModelGenerators.selectMultifaceProperties($$0.defaultBlockState(), MossyCarpetBlock::getPropertyForFace);
        ConditionBuilder $$22 = BlockModelGenerators.condition().term(MossyCarpetBlock.BASE, false);
        $$12.forEach(($$1, $$2) -> $$22.term($$1, WallSide.NONE));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(TexturedModel.CARPET.create($$0, this.modelOutput));
        MultiVariant $$42 = BlockModelGenerators.plainVariant(TexturedModel.MOSSY_CARPET_SIDE.get($$0).updateTextures($$1 -> $$1.put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, "_side_tall"))).createWithSuffix($$0, "_side_tall", this.modelOutput));
        MultiVariant $$52 = BlockModelGenerators.plainVariant(TexturedModel.MOSSY_CARPET_SIDE.get($$0).updateTextures($$1 -> $$1.put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, "_side_small"))).createWithSuffix($$0, "_side_small", this.modelOutput));
        MultiPartGenerator $$6 = MultiPartGenerator.multiPart($$0);
        $$6.with(BlockModelGenerators.condition().term(MossyCarpetBlock.BASE, true), $$3);
        $$6.with($$22, $$3);
        $$12.forEach(($$4, $$5) -> {
            $$6.with(BlockModelGenerators.condition().term($$4, WallSide.TALL), $$42.with((VariantMutator)$$5));
            $$6.with(BlockModelGenerators.condition().term($$4, WallSide.LOW), $$52.with((VariantMutator)$$5));
            $$6.with($$22, $$42.with((VariantMutator)$$5));
        });
        this.blockStateOutput.accept($$6);
    }

    private void createHangingMoss(Block $$0) {
        this.registerSimpleFlatItemModel($$0);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(HangingMossBlock.TIP).generate($$1 -> {
            String $$2 = $$1 != false ? "_tip" : "";
            TextureMapping $$3 = TextureMapping.cross(TextureMapping.getBlockTexture($$0, $$2));
            return BlockModelGenerators.plainVariant(PlantType.NOT_TINTED.getCross().createWithSuffix($$0, $$2, $$3, this.modelOutput));
        })));
    }

    private void createSculkCatalyst() {
        ResourceLocation $$0 = TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_bottom");
        TextureMapping $$1 = new TextureMapping().put(TextureSlot.BOTTOM, $$0).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_side"));
        TextureMapping $$22 = new TextureMapping().put(TextureSlot.BOTTOM, $$0).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_top_bloom")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_side_bloom"));
        ResourceLocation $$3 = ModelTemplates.CUBE_BOTTOM_TOP.create(Blocks.SCULK_CATALYST, $$1, this.modelOutput);
        MultiVariant $$4 = BlockModelGenerators.plainVariant($$3);
        MultiVariant $$5 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.SCULK_CATALYST, "_bloom", $$22, this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SCULK_CATALYST).with(PropertyDispatch.initial(BlockStateProperties.BLOOM).generate($$2 -> $$2 != false ? $$5 : $$4)));
        this.registerSimpleItemModel(Blocks.SCULK_CATALYST, $$3);
    }

    private void createChiseledBookshelf() {
        Block $$0 = Blocks.CHISELED_BOOKSHELF;
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0));
        MultiPartGenerator $$22 = MultiPartGenerator.multiPart($$0);
        List.of((Object)Pair.of((Object)Direction.NORTH, (Object)NOP), (Object)Pair.of((Object)Direction.EAST, (Object)Y_ROT_90), (Object)Pair.of((Object)Direction.SOUTH, (Object)Y_ROT_180), (Object)Pair.of((Object)Direction.WEST, (Object)Y_ROT_270)).forEach($$2 -> {
            Direction $$3 = (Direction)$$2.getFirst();
            VariantMutator $$4 = (VariantMutator)$$2.getSecond();
            Condition $$5 = BlockModelGenerators.condition().term(BlockStateProperties.HORIZONTAL_FACING, $$3).build();
            $$22.with($$5, $$1.with($$4).with(UV_LOCK));
            this.addSlotStateAndRotationVariants($$22, $$5, $$4);
        });
        this.blockStateOutput.accept($$22);
        this.registerSimpleItemModel($$0, ModelLocationUtils.getModelLocation($$0, "_inventory"));
        CHISELED_BOOKSHELF_SLOT_MODEL_CACHE.clear();
    }

    private void addSlotStateAndRotationVariants(MultiPartGenerator $$0, Condition $$1, VariantMutator $$2) {
        List.of((Object)Pair.of((Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED, (Object)ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_LEFT), (Object)Pair.of((Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED, (Object)ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_MID), (Object)Pair.of((Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED, (Object)ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_RIGHT), (Object)Pair.of((Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED, (Object)ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT), (Object)Pair.of((Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED, (Object)ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_MID), (Object)Pair.of((Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED, (Object)ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT)).forEach($$3 -> {
            BooleanProperty $$4 = (BooleanProperty)$$3.getFirst();
            ModelTemplate $$5 = (ModelTemplate)$$3.getSecond();
            this.addBookSlotModel($$0, $$1, $$2, $$4, $$5, true);
            this.addBookSlotModel($$0, $$1, $$2, $$4, $$5, false);
        });
    }

    private void addBookSlotModel(MultiPartGenerator $$0, Condition $$1, VariantMutator $$2, BooleanProperty $$32, ModelTemplate $$4, boolean $$5) {
        String $$6 = $$5 ? "_occupied" : "_empty";
        TextureMapping $$7 = new TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(Blocks.CHISELED_BOOKSHELF, $$6));
        BookSlotModelCacheKey $$8 = new BookSlotModelCacheKey($$4, $$6);
        MultiVariant $$9 = BlockModelGenerators.plainVariant(CHISELED_BOOKSHELF_SLOT_MODEL_CACHE.computeIfAbsent($$8, $$3 -> $$4.createWithSuffix(Blocks.CHISELED_BOOKSHELF, $$6, $$7, this.modelOutput)));
        $$0.with(new CombinedCondition(CombinedCondition.Operation.AND, List.of((Object)$$1, (Object)BlockModelGenerators.condition().term($$32, $$5).build())), $$9.with($$2));
    }

    private void createMagmaBlock() {
        MultiVariant $$0 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ALL.create(Blocks.MAGMA_BLOCK, TextureMapping.cube(ModelLocationUtils.decorateBlockModelLocation("magma")), this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(Blocks.MAGMA_BLOCK, $$0));
    }

    private void createShulkerBox(Block $$0, @Nullable DyeColor $$1) {
        this.createParticleOnlyBlock($$0);
        Item $$2 = $$0.asItem();
        ResourceLocation $$3 = ModelTemplates.SHULKER_BOX_INVENTORY.create($$2, TextureMapping.particle($$0), this.modelOutput);
        ItemModel.Unbaked $$4 = $$1 != null ? ItemModelUtils.specialModel($$3, new ShulkerBoxSpecialRenderer.Unbaked($$1)) : ItemModelUtils.specialModel($$3, new ShulkerBoxSpecialRenderer.Unbaked());
        this.itemModelOutput.accept($$2, $$4);
    }

    private void createGrowingPlant(Block $$0, Block $$1, PlantType $$2) {
        this.createCrossBlock($$0, $$2);
        this.createCrossBlock($$1, $$2);
    }

    private void createInfestedStone() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.STONE);
        Variant $$1 = BlockModelGenerators.plainModel($$0);
        Variant $$2 = BlockModelGenerators.plainModel(ModelLocationUtils.getModelLocation(Blocks.STONE, "_mirrored"));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.INFESTED_STONE, BlockModelGenerators.createRotatedVariants($$1, $$2)));
        this.registerSimpleItemModel(Blocks.INFESTED_STONE, $$0);
    }

    private void createInfestedDeepslate() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.DEEPSLATE);
        Variant $$1 = BlockModelGenerators.plainModel($$0);
        Variant $$2 = BlockModelGenerators.plainModel(ModelLocationUtils.getModelLocation(Blocks.DEEPSLATE, "_mirrored"));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.INFESTED_DEEPSLATE, BlockModelGenerators.createRotatedVariants($$1, $$2)).with(BlockModelGenerators.createRotatedPillar()));
        this.registerSimpleItemModel(Blocks.INFESTED_DEEPSLATE, $$0);
    }

    private void createNetherRoots(Block $$0, Block $$1) {
        this.createCrossBlockWithDefaultItem($$0, PlantType.NOT_TINTED);
        TextureMapping $$2 = TextureMapping.plant(TextureMapping.getBlockTexture($$0, "_pot"));
        MultiVariant $$3 = BlockModelGenerators.plainVariant(PlantType.NOT_TINTED.getCrossPot().create($$1, $$2, this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$1, $$3));
    }

    private void createRespawnAnchor() {
        ResourceLocation $$0 = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_bottom");
        ResourceLocation $$12 = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_top_off");
        ResourceLocation $$2 = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_top");
        ResourceLocation[] $$3 = new ResourceLocation[5];
        for (int $$4 = 0; $$4 < 5; ++$$4) {
            TextureMapping $$5 = new TextureMapping().put(TextureSlot.BOTTOM, $$0).put(TextureSlot.TOP, $$4 == 0 ? $$12 : $$2).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_side" + $$4));
            $$3[$$4] = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.RESPAWN_ANCHOR, "_" + $$4, $$5, this.modelOutput);
        }
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.RESPAWN_ANCHOR).with(PropertyDispatch.initial(BlockStateProperties.RESPAWN_ANCHOR_CHARGES).generate($$1 -> BlockModelGenerators.plainVariant($$3[$$1]))));
        this.registerSimpleItemModel(Blocks.RESPAWN_ANCHOR, $$3[0]);
    }

    private static VariantMutator applyRotation(FrontAndTop $$0) {
        return switch ($$0) {
            default -> throw new MatchException(null, null);
            case FrontAndTop.DOWN_NORTH -> X_ROT_90;
            case FrontAndTop.DOWN_SOUTH -> X_ROT_90.then(Y_ROT_180);
            case FrontAndTop.DOWN_WEST -> X_ROT_90.then(Y_ROT_270);
            case FrontAndTop.DOWN_EAST -> X_ROT_90.then(Y_ROT_90);
            case FrontAndTop.UP_NORTH -> X_ROT_270.then(Y_ROT_180);
            case FrontAndTop.UP_SOUTH -> X_ROT_270;
            case FrontAndTop.UP_WEST -> X_ROT_270.then(Y_ROT_90);
            case FrontAndTop.UP_EAST -> X_ROT_270.then(Y_ROT_270);
            case FrontAndTop.NORTH_UP -> NOP;
            case FrontAndTop.SOUTH_UP -> Y_ROT_180;
            case FrontAndTop.WEST_UP -> Y_ROT_270;
            case FrontAndTop.EAST_UP -> Y_ROT_90;
        };
    }

    private void createJigsaw() {
        ResourceLocation $$0 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_top");
        ResourceLocation $$1 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_bottom");
        ResourceLocation $$2 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_side");
        ResourceLocation $$3 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_lock");
        TextureMapping $$4 = new TextureMapping().put(TextureSlot.DOWN, $$2).put(TextureSlot.WEST, $$2).put(TextureSlot.EAST, $$2).put(TextureSlot.PARTICLE, $$0).put(TextureSlot.NORTH, $$0).put(TextureSlot.SOUTH, $$1).put(TextureSlot.UP, $$3);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.JIGSAW, BlockModelGenerators.plainVariant(ModelTemplates.CUBE_DIRECTIONAL.create(Blocks.JIGSAW, $$4, this.modelOutput))).with(PropertyDispatch.modify(BlockStateProperties.ORIENTATION).generate(BlockModelGenerators::applyRotation)));
    }

    private void createPetrifiedOakSlab() {
        Block $$0 = Blocks.OAK_PLANKS;
        MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation($$0));
        TextureMapping $$2 = TextureMapping.cube($$0);
        Block $$3 = Blocks.PETRIFIED_OAK_SLAB;
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.SLAB_BOTTOM.create($$3, $$2, this.modelOutput));
        MultiVariant $$5 = BlockModelGenerators.plainVariant(ModelTemplates.SLAB_TOP.create($$3, $$2, this.modelOutput));
        this.blockStateOutput.accept(BlockModelGenerators.createSlab($$3, $$4, $$5, $$1));
    }

    private void createHead(Block $$0, Block $$1, SkullBlock.Type $$2, ResourceLocation $$3) {
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation("skull"));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, $$4));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$1, $$4));
        if ($$2 == SkullBlock.Types.PLAYER) {
            this.itemModelOutput.accept($$0.asItem(), ItemModelUtils.specialModel($$3, new PlayerHeadSpecialRenderer.Unbaked()));
        } else {
            this.itemModelOutput.accept($$0.asItem(), ItemModelUtils.specialModel($$3, new SkullSpecialRenderer.Unbaked($$2)));
        }
    }

    private void createHeads() {
        ResourceLocation $$0 = ModelLocationUtils.decorateItemModelLocation("template_skull");
        this.createHead(Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, SkullBlock.Types.CREEPER, $$0);
        this.createHead(Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, SkullBlock.Types.PLAYER, $$0);
        this.createHead(Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, SkullBlock.Types.ZOMBIE, $$0);
        this.createHead(Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, SkullBlock.Types.SKELETON, $$0);
        this.createHead(Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, SkullBlock.Types.WITHER_SKELETON, $$0);
        this.createHead(Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD, SkullBlock.Types.PIGLIN, $$0);
        this.createHead(Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, SkullBlock.Types.DRAGON, ModelLocationUtils.getModelLocation(Items.DRAGON_HEAD));
    }

    private void createBanner(Block $$0, Block $$1, DyeColor $$2) {
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation("banner"));
        ResourceLocation $$4 = ModelLocationUtils.decorateItemModelLocation("template_banner");
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, $$3));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$1, $$3));
        Item $$5 = $$0.asItem();
        this.itemModelOutput.accept($$5, ItemModelUtils.specialModel($$4, new BannerSpecialRenderer.Unbaked($$2)));
    }

    private void createBanners() {
        this.createBanner(Blocks.WHITE_BANNER, Blocks.WHITE_WALL_BANNER, DyeColor.WHITE);
        this.createBanner(Blocks.ORANGE_BANNER, Blocks.ORANGE_WALL_BANNER, DyeColor.ORANGE);
        this.createBanner(Blocks.MAGENTA_BANNER, Blocks.MAGENTA_WALL_BANNER, DyeColor.MAGENTA);
        this.createBanner(Blocks.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, DyeColor.LIGHT_BLUE);
        this.createBanner(Blocks.YELLOW_BANNER, Blocks.YELLOW_WALL_BANNER, DyeColor.YELLOW);
        this.createBanner(Blocks.LIME_BANNER, Blocks.LIME_WALL_BANNER, DyeColor.LIME);
        this.createBanner(Blocks.PINK_BANNER, Blocks.PINK_WALL_BANNER, DyeColor.PINK);
        this.createBanner(Blocks.GRAY_BANNER, Blocks.GRAY_WALL_BANNER, DyeColor.GRAY);
        this.createBanner(Blocks.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, DyeColor.LIGHT_GRAY);
        this.createBanner(Blocks.CYAN_BANNER, Blocks.CYAN_WALL_BANNER, DyeColor.CYAN);
        this.createBanner(Blocks.PURPLE_BANNER, Blocks.PURPLE_WALL_BANNER, DyeColor.PURPLE);
        this.createBanner(Blocks.BLUE_BANNER, Blocks.BLUE_WALL_BANNER, DyeColor.BLUE);
        this.createBanner(Blocks.BROWN_BANNER, Blocks.BROWN_WALL_BANNER, DyeColor.BROWN);
        this.createBanner(Blocks.GREEN_BANNER, Blocks.GREEN_WALL_BANNER, DyeColor.GREEN);
        this.createBanner(Blocks.RED_BANNER, Blocks.RED_WALL_BANNER, DyeColor.RED);
        this.createBanner(Blocks.BLACK_BANNER, Blocks.BLACK_WALL_BANNER, DyeColor.BLACK);
    }

    private void createChest(Block $$0, Block $$1, ResourceLocation $$2, boolean $$3) {
        this.createParticleOnlyBlock($$0, $$1);
        Item $$4 = $$0.asItem();
        ResourceLocation $$5 = ModelTemplates.CHEST_INVENTORY.create($$4, TextureMapping.particle($$1), this.modelOutput);
        ItemModel.Unbaked $$6 = ItemModelUtils.specialModel($$5, new ChestSpecialRenderer.Unbaked($$2));
        if ($$3) {
            ItemModel.Unbaked $$7 = ItemModelUtils.specialModel($$5, new ChestSpecialRenderer.Unbaked(ChestSpecialRenderer.GIFT_CHEST_TEXTURE));
            this.itemModelOutput.accept($$4, ItemModelUtils.isXmas($$7, $$6));
        } else {
            this.itemModelOutput.accept($$4, $$6);
        }
    }

    private void createChests() {
        this.createChest(Blocks.CHEST, Blocks.OAK_PLANKS, ChestSpecialRenderer.NORMAL_CHEST_TEXTURE, true);
        this.createChest(Blocks.TRAPPED_CHEST, Blocks.OAK_PLANKS, ChestSpecialRenderer.TRAPPED_CHEST_TEXTURE, true);
        this.createChest(Blocks.ENDER_CHEST, Blocks.OBSIDIAN, ChestSpecialRenderer.ENDER_CHEST_TEXTURE, false);
    }

    private void createBed(Block $$0, Block $$1, DyeColor $$2) {
        MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation("bed"));
        this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, $$3));
        Item $$4 = $$0.asItem();
        ResourceLocation $$5 = ModelTemplates.BED_INVENTORY.create(ModelLocationUtils.getModelLocation($$4), TextureMapping.particle($$1), this.modelOutput);
        this.itemModelOutput.accept($$4, ItemModelUtils.specialModel($$5, new BedSpecialRenderer.Unbaked($$2)));
    }

    private void createBeds() {
        this.createBed(Blocks.WHITE_BED, Blocks.WHITE_WOOL, DyeColor.WHITE);
        this.createBed(Blocks.ORANGE_BED, Blocks.ORANGE_WOOL, DyeColor.ORANGE);
        this.createBed(Blocks.MAGENTA_BED, Blocks.MAGENTA_WOOL, DyeColor.MAGENTA);
        this.createBed(Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL, DyeColor.LIGHT_BLUE);
        this.createBed(Blocks.YELLOW_BED, Blocks.YELLOW_WOOL, DyeColor.YELLOW);
        this.createBed(Blocks.LIME_BED, Blocks.LIME_WOOL, DyeColor.LIME);
        this.createBed(Blocks.PINK_BED, Blocks.PINK_WOOL, DyeColor.PINK);
        this.createBed(Blocks.GRAY_BED, Blocks.GRAY_WOOL, DyeColor.GRAY);
        this.createBed(Blocks.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL, DyeColor.LIGHT_GRAY);
        this.createBed(Blocks.CYAN_BED, Blocks.CYAN_WOOL, DyeColor.CYAN);
        this.createBed(Blocks.PURPLE_BED, Blocks.PURPLE_WOOL, DyeColor.PURPLE);
        this.createBed(Blocks.BLUE_BED, Blocks.BLUE_WOOL, DyeColor.BLUE);
        this.createBed(Blocks.BROWN_BED, Blocks.BROWN_WOOL, DyeColor.BROWN);
        this.createBed(Blocks.GREEN_BED, Blocks.GREEN_WOOL, DyeColor.GREEN);
        this.createBed(Blocks.RED_BED, Blocks.RED_WOOL, DyeColor.RED);
        this.createBed(Blocks.BLACK_BED, Blocks.BLACK_WOOL, DyeColor.BLACK);
    }

    private void generateSimpleSpecialItemModel(Block $$0, SpecialModelRenderer.Unbaked $$1) {
        Item $$2 = $$0.asItem();
        ResourceLocation $$3 = ModelLocationUtils.getModelLocation($$2);
        this.itemModelOutput.accept($$2, ItemModelUtils.specialModel($$3, $$1));
    }

    public void run() {
        BlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateModel).forEach($$0 -> this.family($$0.getBaseBlock()).generateFor((BlockFamily)$$0));
        this.family(Blocks.CUT_COPPER).generateFor(BlockFamilies.CUT_COPPER).donateModelTo(Blocks.CUT_COPPER, Blocks.WAXED_CUT_COPPER).donateModelTo(Blocks.CHISELED_COPPER, Blocks.WAXED_CHISELED_COPPER).generateFor(BlockFamilies.WAXED_CUT_COPPER);
        this.family(Blocks.EXPOSED_CUT_COPPER).generateFor(BlockFamilies.EXPOSED_CUT_COPPER).donateModelTo(Blocks.EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER).donateModelTo(Blocks.EXPOSED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_CHISELED_COPPER).generateFor(BlockFamilies.WAXED_EXPOSED_CUT_COPPER);
        this.family(Blocks.WEATHERED_CUT_COPPER).generateFor(BlockFamilies.WEATHERED_CUT_COPPER).donateModelTo(Blocks.WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER).donateModelTo(Blocks.WEATHERED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_CHISELED_COPPER).generateFor(BlockFamilies.WAXED_WEATHERED_CUT_COPPER);
        this.family(Blocks.OXIDIZED_CUT_COPPER).generateFor(BlockFamilies.OXIDIZED_CUT_COPPER).donateModelTo(Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER).donateModelTo(Blocks.OXIDIZED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_CHISELED_COPPER).generateFor(BlockFamilies.WAXED_OXIDIZED_CUT_COPPER);
        this.createCopperBulb(Blocks.COPPER_BULB);
        this.createCopperBulb(Blocks.EXPOSED_COPPER_BULB);
        this.createCopperBulb(Blocks.WEATHERED_COPPER_BULB);
        this.createCopperBulb(Blocks.OXIDIZED_COPPER_BULB);
        this.copyCopperBulbModel(Blocks.COPPER_BULB, Blocks.WAXED_COPPER_BULB);
        this.copyCopperBulbModel(Blocks.EXPOSED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB);
        this.copyCopperBulbModel(Blocks.WEATHERED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB);
        this.copyCopperBulbModel(Blocks.OXIDIZED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB);
        this.createNonTemplateModelBlock(Blocks.AIR);
        this.createNonTemplateModelBlock(Blocks.CAVE_AIR, Blocks.AIR);
        this.createNonTemplateModelBlock(Blocks.VOID_AIR, Blocks.AIR);
        this.createNonTemplateModelBlock(Blocks.BEACON);
        this.createNonTemplateModelBlock(Blocks.CACTUS);
        this.createNonTemplateModelBlock(Blocks.BUBBLE_COLUMN, Blocks.WATER);
        this.createNonTemplateModelBlock(Blocks.DRAGON_EGG);
        this.createNonTemplateModelBlock(Blocks.DRIED_KELP_BLOCK);
        this.createNonTemplateModelBlock(Blocks.ENCHANTING_TABLE);
        this.createNonTemplateModelBlock(Blocks.FLOWER_POT);
        this.registerSimpleFlatItemModel(Items.FLOWER_POT);
        this.createNonTemplateModelBlock(Blocks.HONEY_BLOCK);
        this.createNonTemplateModelBlock(Blocks.WATER);
        this.createNonTemplateModelBlock(Blocks.LAVA);
        this.createNonTemplateModelBlock(Blocks.SLIME_BLOCK);
        this.registerSimpleFlatItemModel(Items.CHAIN);
        this.createCandleAndCandleCake(Blocks.WHITE_CANDLE, Blocks.WHITE_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.ORANGE_CANDLE, Blocks.ORANGE_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.MAGENTA_CANDLE, Blocks.MAGENTA_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.LIGHT_BLUE_CANDLE, Blocks.LIGHT_BLUE_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.YELLOW_CANDLE, Blocks.YELLOW_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.LIME_CANDLE, Blocks.LIME_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.PINK_CANDLE, Blocks.PINK_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.GRAY_CANDLE, Blocks.GRAY_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.LIGHT_GRAY_CANDLE, Blocks.LIGHT_GRAY_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.CYAN_CANDLE, Blocks.CYAN_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.PURPLE_CANDLE, Blocks.PURPLE_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.BLUE_CANDLE, Blocks.BLUE_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.BROWN_CANDLE, Blocks.BROWN_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.GREEN_CANDLE, Blocks.GREEN_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.RED_CANDLE, Blocks.RED_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.BLACK_CANDLE, Blocks.BLACK_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.CANDLE, Blocks.CANDLE_CAKE);
        this.createNonTemplateModelBlock(Blocks.POTTED_BAMBOO);
        this.createNonTemplateModelBlock(Blocks.POTTED_CACTUS);
        this.createNonTemplateModelBlock(Blocks.POWDER_SNOW);
        this.createNonTemplateModelBlock(Blocks.SPORE_BLOSSOM);
        this.createAzalea(Blocks.AZALEA);
        this.createAzalea(Blocks.FLOWERING_AZALEA);
        this.createPottedAzalea(Blocks.POTTED_AZALEA);
        this.createPottedAzalea(Blocks.POTTED_FLOWERING_AZALEA);
        this.createCaveVines();
        this.createFullAndCarpetBlocks(Blocks.MOSS_BLOCK, Blocks.MOSS_CARPET);
        this.createMossyCarpet(Blocks.PALE_MOSS_CARPET);
        this.createHangingMoss(Blocks.PALE_HANGING_MOSS);
        this.createTrivialCube(Blocks.PALE_MOSS_BLOCK);
        this.createFlowerBed(Blocks.PINK_PETALS);
        this.createFlowerBed(Blocks.WILDFLOWERS);
        this.createLeafLitter(Blocks.LEAF_LITTER);
        this.createCrossBlock(Blocks.FIREFLY_BUSH, PlantType.EMISSIVE_NOT_TINTED);
        this.registerSimpleFlatItemModel(Items.FIREFLY_BUSH);
        this.createAirLikeBlock(Blocks.BARRIER, Items.BARRIER);
        this.registerSimpleFlatItemModel(Items.BARRIER);
        this.createLightBlock();
        this.createAirLikeBlock(Blocks.STRUCTURE_VOID, Items.STRUCTURE_VOID);
        this.registerSimpleFlatItemModel(Items.STRUCTURE_VOID);
        this.createAirLikeBlock(Blocks.MOVING_PISTON, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
        this.createTrivialCube(Blocks.COAL_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_COAL_ORE);
        this.createTrivialCube(Blocks.COAL_BLOCK);
        this.createTrivialCube(Blocks.DIAMOND_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_DIAMOND_ORE);
        this.createTrivialCube(Blocks.DIAMOND_BLOCK);
        this.createTrivialCube(Blocks.EMERALD_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_EMERALD_ORE);
        this.createTrivialCube(Blocks.EMERALD_BLOCK);
        this.createTrivialCube(Blocks.GOLD_ORE);
        this.createTrivialCube(Blocks.NETHER_GOLD_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_GOLD_ORE);
        this.createTrivialCube(Blocks.GOLD_BLOCK);
        this.createTrivialCube(Blocks.IRON_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_IRON_ORE);
        this.createTrivialCube(Blocks.IRON_BLOCK);
        this.createTrivialBlock(Blocks.ANCIENT_DEBRIS, TexturedModel.COLUMN);
        this.createTrivialCube(Blocks.NETHERITE_BLOCK);
        this.createTrivialCube(Blocks.LAPIS_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_LAPIS_ORE);
        this.createTrivialCube(Blocks.LAPIS_BLOCK);
        this.createTrivialCube(Blocks.RESIN_BLOCK);
        this.createTrivialCube(Blocks.NETHER_QUARTZ_ORE);
        this.createTrivialCube(Blocks.REDSTONE_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_REDSTONE_ORE);
        this.createTrivialCube(Blocks.REDSTONE_BLOCK);
        this.createTrivialCube(Blocks.GILDED_BLACKSTONE);
        this.createTrivialCube(Blocks.BLUE_ICE);
        this.createTrivialCube(Blocks.CLAY);
        this.createTrivialCube(Blocks.COARSE_DIRT);
        this.createTrivialCube(Blocks.CRYING_OBSIDIAN);
        this.createTrivialCube(Blocks.END_STONE);
        this.createTrivialCube(Blocks.GLOWSTONE);
        this.createTrivialCube(Blocks.GRAVEL);
        this.createTrivialCube(Blocks.HONEYCOMB_BLOCK);
        this.createTrivialCube(Blocks.ICE);
        this.createTrivialBlock(Blocks.JUKEBOX, TexturedModel.CUBE_TOP);
        this.createTrivialBlock(Blocks.LODESTONE, TexturedModel.COLUMN);
        this.createTrivialBlock(Blocks.MELON, TexturedModel.COLUMN);
        this.createNonTemplateModelBlock(Blocks.MANGROVE_ROOTS);
        this.createNonTemplateModelBlock(Blocks.POTTED_MANGROVE_PROPAGULE);
        this.createTrivialCube(Blocks.NETHER_WART_BLOCK);
        this.createTrivialCube(Blocks.NOTE_BLOCK);
        this.createTrivialCube(Blocks.PACKED_ICE);
        this.createTrivialCube(Blocks.OBSIDIAN);
        this.createTrivialCube(Blocks.QUARTZ_BRICKS);
        this.createTrivialCube(Blocks.SEA_LANTERN);
        this.createTrivialCube(Blocks.SHROOMLIGHT);
        this.createTrivialCube(Blocks.SOUL_SAND);
        this.createTrivialCube(Blocks.SOUL_SOIL);
        this.createTrivialBlock(Blocks.SPAWNER, TexturedModel.CUBE_INNER_FACES);
        this.createCreakingHeart(Blocks.CREAKING_HEART);
        this.createTrivialCube(Blocks.SPONGE);
        this.createTrivialBlock(Blocks.SEAGRASS, TexturedModel.SEAGRASS);
        this.registerSimpleFlatItemModel(Items.SEAGRASS);
        this.createTrivialBlock(Blocks.TNT, TexturedModel.CUBE_TOP_BOTTOM);
        this.createTrivialBlock(Blocks.TARGET, TexturedModel.COLUMN);
        this.createTrivialCube(Blocks.WARPED_WART_BLOCK);
        this.createTrivialCube(Blocks.WET_SPONGE);
        this.createTrivialCube(Blocks.AMETHYST_BLOCK);
        this.createTrivialCube(Blocks.BUDDING_AMETHYST);
        this.createTrivialCube(Blocks.CALCITE);
        this.createTrivialCube(Blocks.DRIPSTONE_BLOCK);
        this.createTrivialCube(Blocks.RAW_IRON_BLOCK);
        this.createTrivialCube(Blocks.RAW_COPPER_BLOCK);
        this.createTrivialCube(Blocks.RAW_GOLD_BLOCK);
        this.createRotatedMirroredVariantBlock(Blocks.SCULK);
        this.createNonTemplateModelBlock(Blocks.HEAVY_CORE);
        this.createPetrifiedOakSlab();
        this.createTrivialCube(Blocks.COPPER_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_COPPER_ORE);
        this.createTrivialCube(Blocks.COPPER_BLOCK);
        this.createTrivialCube(Blocks.EXPOSED_COPPER);
        this.createTrivialCube(Blocks.WEATHERED_COPPER);
        this.createTrivialCube(Blocks.OXIDIZED_COPPER);
        this.copyModel(Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK);
        this.copyModel(Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER);
        this.copyModel(Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER);
        this.copyModel(Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER);
        this.createDoor(Blocks.COPPER_DOOR);
        this.createDoor(Blocks.EXPOSED_COPPER_DOOR);
        this.createDoor(Blocks.WEATHERED_COPPER_DOOR);
        this.createDoor(Blocks.OXIDIZED_COPPER_DOOR);
        this.copyDoorModel(Blocks.COPPER_DOOR, Blocks.WAXED_COPPER_DOOR);
        this.copyDoorModel(Blocks.EXPOSED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR);
        this.copyDoorModel(Blocks.WEATHERED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR);
        this.copyDoorModel(Blocks.OXIDIZED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR);
        this.createTrapdoor(Blocks.COPPER_TRAPDOOR);
        this.createTrapdoor(Blocks.EXPOSED_COPPER_TRAPDOOR);
        this.createTrapdoor(Blocks.WEATHERED_COPPER_TRAPDOOR);
        this.createTrapdoor(Blocks.OXIDIZED_COPPER_TRAPDOOR);
        this.copyTrapdoorModel(Blocks.COPPER_TRAPDOOR, Blocks.WAXED_COPPER_TRAPDOOR);
        this.copyTrapdoorModel(Blocks.EXPOSED_COPPER_TRAPDOOR, Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
        this.copyTrapdoorModel(Blocks.WEATHERED_COPPER_TRAPDOOR, Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
        this.copyTrapdoorModel(Blocks.OXIDIZED_COPPER_TRAPDOOR, Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);
        this.createTrivialCube(Blocks.COPPER_GRATE);
        this.createTrivialCube(Blocks.EXPOSED_COPPER_GRATE);
        this.createTrivialCube(Blocks.WEATHERED_COPPER_GRATE);
        this.createTrivialCube(Blocks.OXIDIZED_COPPER_GRATE);
        this.copyModel(Blocks.COPPER_GRATE, Blocks.WAXED_COPPER_GRATE);
        this.copyModel(Blocks.EXPOSED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER_GRATE);
        this.copyModel(Blocks.WEATHERED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER_GRATE);
        this.copyModel(Blocks.OXIDIZED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER_GRATE);
        this.createWeightedPressurePlate(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.GOLD_BLOCK);
        this.createWeightedPressurePlate(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.IRON_BLOCK);
        this.createAmethystClusters();
        this.createBookshelf();
        this.createChiseledBookshelf();
        this.createBrewingStand();
        this.createCakeBlock();
        this.a(Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);
        this.createCartographyTable();
        this.createCauldrons();
        this.createChorusFlower();
        this.createChorusPlant();
        this.createComposter();
        this.createDaylightDetector();
        this.createEndPortalFrame();
        this.createRotatableColumn(Blocks.END_ROD);
        this.createLightningRod();
        this.createFarmland();
        this.createFire();
        this.createSoulFire();
        this.createFrostedIce();
        this.createGrassBlocks();
        this.createCocoa();
        this.createDirtPath();
        this.createGrindstone();
        this.createHopper();
        this.createIronBars();
        this.createLever();
        this.createLilyPad();
        this.createNetherPortalBlock();
        this.createNetherrack();
        this.createObserver();
        this.createPistons();
        this.createPistonHeads();
        this.createScaffolding();
        this.createRedstoneTorch();
        this.createRedstoneLamp();
        this.createRepeater();
        this.createSeaPickle();
        this.createSmithingTable();
        this.createSnowBlocks();
        this.createStonecutter();
        this.createStructureBlock();
        this.createSweetBerryBush();
        this.createTestBlock();
        this.createTrivialCube(Blocks.TEST_INSTANCE_BLOCK);
        this.createTripwire();
        this.createTripwireHook();
        this.createTurtleEgg();
        this.createSnifferEgg();
        this.createDriedGhastBlock();
        this.createVine();
        this.createMultiface(Blocks.GLOW_LICHEN);
        this.createMultiface(Blocks.SCULK_VEIN);
        this.createMultiface(Blocks.RESIN_CLUMP, Items.RESIN_CLUMP);
        this.createMagmaBlock();
        this.createJigsaw();
        this.createSculkSensor();
        this.createCalibratedSculkSensor();
        this.createSculkShrieker();
        this.createFrogspawnBlock();
        this.createMangrovePropagule();
        this.createMuddyMangroveRoots();
        this.createTrialSpawner();
        this.createVault();
        this.createNonTemplateHorizontalBlock(Blocks.LADDER);
        this.registerSimpleFlatItemModel(Blocks.LADDER);
        this.createNonTemplateHorizontalBlock(Blocks.LECTERN);
        this.createBigDripLeafBlock();
        this.createNonTemplateHorizontalBlock(Blocks.BIG_DRIPLEAF_STEM);
        this.createNormalTorch(Blocks.TORCH, Blocks.WALL_TORCH);
        this.createNormalTorch(Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH);
        this.createCraftingTableLike(Blocks.CRAFTING_TABLE, Blocks.OAK_PLANKS, TextureMapping::craftingTable);
        this.createCraftingTableLike(Blocks.FLETCHING_TABLE, Blocks.BIRCH_PLANKS, TextureMapping::fletchingTable);
        this.createNyliumBlock(Blocks.CRIMSON_NYLIUM);
        this.createNyliumBlock(Blocks.WARPED_NYLIUM);
        this.createDispenserBlock(Blocks.DISPENSER);
        this.createDispenserBlock(Blocks.DROPPER);
        this.createCrafterBlock();
        this.createLantern(Blocks.LANTERN);
        this.createLantern(Blocks.SOUL_LANTERN);
        this.createAxisAlignedPillarBlockCustomModel(Blocks.CHAIN, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.CHAIN)));
        this.createAxisAlignedPillarBlock(Blocks.BASALT, TexturedModel.COLUMN);
        this.createAxisAlignedPillarBlock(Blocks.POLISHED_BASALT, TexturedModel.COLUMN);
        this.createTrivialCube(Blocks.SMOOTH_BASALT);
        this.createAxisAlignedPillarBlock(Blocks.BONE_BLOCK, TexturedModel.COLUMN);
        this.createRotatedVariantBlock(Blocks.DIRT);
        this.createRotatedVariantBlock(Blocks.ROOTED_DIRT);
        this.createRotatedVariantBlock(Blocks.SAND);
        this.createBrushableBlock(Blocks.SUSPICIOUS_SAND);
        this.createBrushableBlock(Blocks.SUSPICIOUS_GRAVEL);
        this.createRotatedVariantBlock(Blocks.RED_SAND);
        this.createRotatedMirroredVariantBlock(Blocks.BEDROCK);
        this.createTrivialBlock(Blocks.REINFORCED_DEEPSLATE, TexturedModel.CUBE_TOP_BOTTOM);
        this.createRotatedPillarWithHorizontalVariant(Blocks.HAY_BLOCK, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
        this.createRotatedPillarWithHorizontalVariant(Blocks.PURPUR_PILLAR, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);
        this.createRotatedPillarWithHorizontalVariant(Blocks.QUARTZ_PILLAR, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);
        this.createRotatedPillarWithHorizontalVariant(Blocks.OCHRE_FROGLIGHT, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
        this.createRotatedPillarWithHorizontalVariant(Blocks.VERDANT_FROGLIGHT, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
        this.createRotatedPillarWithHorizontalVariant(Blocks.PEARLESCENT_FROGLIGHT, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
        this.createHorizontallyRotatedBlock(Blocks.LOOM, TexturedModel.ORIENTABLE);
        this.createPumpkins();
        this.createBeeNest(Blocks.BEE_NEST, TextureMapping::orientableCube);
        this.createBeeNest(Blocks.BEEHIVE, TextureMapping::orientableCubeSameEnds);
        this.a(Blocks.BEETROOTS, BlockStateProperties.AGE_3, 0, 1, 2, 3);
        this.a(Blocks.CARROTS, BlockStateProperties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
        this.a(Blocks.NETHER_WART, BlockStateProperties.AGE_3, 0, 1, 1, 2);
        this.a(Blocks.POTATOES, BlockStateProperties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
        this.a(Blocks.WHEAT, BlockStateProperties.AGE_7, 0, 1, 2, 3, 4, 5, 6, 7);
        this.a(Blocks.TORCHFLOWER_CROP, PlantType.NOT_TINTED, BlockStateProperties.AGE_1, 0, 1);
        this.createPitcherCrop();
        this.createPitcherPlant();
        this.createBanners();
        this.createBeds();
        this.createHeads();
        this.createChests();
        this.createShulkerBox(Blocks.SHULKER_BOX, null);
        this.createShulkerBox(Blocks.WHITE_SHULKER_BOX, DyeColor.WHITE);
        this.createShulkerBox(Blocks.ORANGE_SHULKER_BOX, DyeColor.ORANGE);
        this.createShulkerBox(Blocks.MAGENTA_SHULKER_BOX, DyeColor.MAGENTA);
        this.createShulkerBox(Blocks.LIGHT_BLUE_SHULKER_BOX, DyeColor.LIGHT_BLUE);
        this.createShulkerBox(Blocks.YELLOW_SHULKER_BOX, DyeColor.YELLOW);
        this.createShulkerBox(Blocks.LIME_SHULKER_BOX, DyeColor.LIME);
        this.createShulkerBox(Blocks.PINK_SHULKER_BOX, DyeColor.PINK);
        this.createShulkerBox(Blocks.GRAY_SHULKER_BOX, DyeColor.GRAY);
        this.createShulkerBox(Blocks.LIGHT_GRAY_SHULKER_BOX, DyeColor.LIGHT_GRAY);
        this.createShulkerBox(Blocks.CYAN_SHULKER_BOX, DyeColor.CYAN);
        this.createShulkerBox(Blocks.PURPLE_SHULKER_BOX, DyeColor.PURPLE);
        this.createShulkerBox(Blocks.BLUE_SHULKER_BOX, DyeColor.BLUE);
        this.createShulkerBox(Blocks.BROWN_SHULKER_BOX, DyeColor.BROWN);
        this.createShulkerBox(Blocks.GREEN_SHULKER_BOX, DyeColor.GREEN);
        this.createShulkerBox(Blocks.RED_SHULKER_BOX, DyeColor.RED);
        this.createShulkerBox(Blocks.BLACK_SHULKER_BOX, DyeColor.BLACK);
        this.createParticleOnlyBlock(Blocks.CONDUIT);
        this.generateSimpleSpecialItemModel(Blocks.CONDUIT, new ConduitSpecialRenderer.Unbaked());
        this.createParticleOnlyBlock(Blocks.DECORATED_POT, Blocks.TERRACOTTA);
        this.generateSimpleSpecialItemModel(Blocks.DECORATED_POT, new DecoratedPotSpecialRenderer.Unbaked());
        this.createParticleOnlyBlock(Blocks.END_PORTAL, Blocks.OBSIDIAN);
        this.createParticleOnlyBlock(Blocks.END_GATEWAY, Blocks.OBSIDIAN);
        this.createTrivialCube(Blocks.AZALEA_LEAVES);
        this.createTrivialCube(Blocks.FLOWERING_AZALEA_LEAVES);
        this.createTrivialCube(Blocks.WHITE_CONCRETE);
        this.createTrivialCube(Blocks.ORANGE_CONCRETE);
        this.createTrivialCube(Blocks.MAGENTA_CONCRETE);
        this.createTrivialCube(Blocks.LIGHT_BLUE_CONCRETE);
        this.createTrivialCube(Blocks.YELLOW_CONCRETE);
        this.createTrivialCube(Blocks.LIME_CONCRETE);
        this.createTrivialCube(Blocks.PINK_CONCRETE);
        this.createTrivialCube(Blocks.GRAY_CONCRETE);
        this.createTrivialCube(Blocks.LIGHT_GRAY_CONCRETE);
        this.createTrivialCube(Blocks.CYAN_CONCRETE);
        this.createTrivialCube(Blocks.PURPLE_CONCRETE);
        this.createTrivialCube(Blocks.BLUE_CONCRETE);
        this.createTrivialCube(Blocks.BROWN_CONCRETE);
        this.createTrivialCube(Blocks.GREEN_CONCRETE);
        this.createTrivialCube(Blocks.RED_CONCRETE);
        this.createTrivialCube(Blocks.BLACK_CONCRETE);
        this.a(TexturedModel.CUBE, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER);
        this.createTrivialCube(Blocks.TERRACOTTA);
        this.createTrivialCube(Blocks.WHITE_TERRACOTTA);
        this.createTrivialCube(Blocks.ORANGE_TERRACOTTA);
        this.createTrivialCube(Blocks.MAGENTA_TERRACOTTA);
        this.createTrivialCube(Blocks.LIGHT_BLUE_TERRACOTTA);
        this.createTrivialCube(Blocks.YELLOW_TERRACOTTA);
        this.createTrivialCube(Blocks.LIME_TERRACOTTA);
        this.createTrivialCube(Blocks.PINK_TERRACOTTA);
        this.createTrivialCube(Blocks.GRAY_TERRACOTTA);
        this.createTrivialCube(Blocks.LIGHT_GRAY_TERRACOTTA);
        this.createTrivialCube(Blocks.CYAN_TERRACOTTA);
        this.createTrivialCube(Blocks.PURPLE_TERRACOTTA);
        this.createTrivialCube(Blocks.BLUE_TERRACOTTA);
        this.createTrivialCube(Blocks.BROWN_TERRACOTTA);
        this.createTrivialCube(Blocks.GREEN_TERRACOTTA);
        this.createTrivialCube(Blocks.RED_TERRACOTTA);
        this.createTrivialCube(Blocks.BLACK_TERRACOTTA);
        this.createTrivialCube(Blocks.TINTED_GLASS);
        this.createGlassBlocks(Blocks.GLASS, Blocks.GLASS_PANE);
        this.createGlassBlocks(Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.MAGENTA_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.PINK_STAINED_GLASS, Blocks.PINK_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.GRAY_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.CYAN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE);
        this.b(TexturedModel.GLAZED_TERRACOTTA, Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA);
        this.createFullAndCarpetBlocks(Blocks.WHITE_WOOL, Blocks.WHITE_CARPET);
        this.createFullAndCarpetBlocks(Blocks.ORANGE_WOOL, Blocks.ORANGE_CARPET);
        this.createFullAndCarpetBlocks(Blocks.MAGENTA_WOOL, Blocks.MAGENTA_CARPET);
        this.createFullAndCarpetBlocks(Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_BLUE_CARPET);
        this.createFullAndCarpetBlocks(Blocks.YELLOW_WOOL, Blocks.YELLOW_CARPET);
        this.createFullAndCarpetBlocks(Blocks.LIME_WOOL, Blocks.LIME_CARPET);
        this.createFullAndCarpetBlocks(Blocks.PINK_WOOL, Blocks.PINK_CARPET);
        this.createFullAndCarpetBlocks(Blocks.GRAY_WOOL, Blocks.GRAY_CARPET);
        this.createFullAndCarpetBlocks(Blocks.LIGHT_GRAY_WOOL, Blocks.LIGHT_GRAY_CARPET);
        this.createFullAndCarpetBlocks(Blocks.CYAN_WOOL, Blocks.CYAN_CARPET);
        this.createFullAndCarpetBlocks(Blocks.PURPLE_WOOL, Blocks.PURPLE_CARPET);
        this.createFullAndCarpetBlocks(Blocks.BLUE_WOOL, Blocks.BLUE_CARPET);
        this.createFullAndCarpetBlocks(Blocks.BROWN_WOOL, Blocks.BROWN_CARPET);
        this.createFullAndCarpetBlocks(Blocks.GREEN_WOOL, Blocks.GREEN_CARPET);
        this.createFullAndCarpetBlocks(Blocks.RED_WOOL, Blocks.RED_CARPET);
        this.createFullAndCarpetBlocks(Blocks.BLACK_WOOL, Blocks.BLACK_CARPET);
        this.createTrivialCube(Blocks.MUD);
        this.createTrivialCube(Blocks.PACKED_MUD);
        this.createPlant(Blocks.FERN, Blocks.POTTED_FERN, PlantType.TINTED);
        this.createItemWithGrassTint(Blocks.FERN);
        this.createPlantWithDefaultItem(Blocks.DANDELION, Blocks.POTTED_DANDELION, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.POPPY, Blocks.POTTED_POPPY, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.OPEN_EYEBLOSSOM, Blocks.POTTED_OPEN_EYEBLOSSOM, PlantType.EMISSIVE_NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.CLOSED_EYEBLOSSOM, Blocks.POTTED_CLOSED_EYEBLOSSOM, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.BLUE_ORCHID, Blocks.POTTED_BLUE_ORCHID, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.ALLIUM, Blocks.POTTED_ALLIUM, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.AZURE_BLUET, Blocks.POTTED_AZURE_BLUET, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.RED_TULIP, Blocks.POTTED_RED_TULIP, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.ORANGE_TULIP, Blocks.POTTED_ORANGE_TULIP, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.WHITE_TULIP, Blocks.POTTED_WHITE_TULIP, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.PINK_TULIP, Blocks.POTTED_PINK_TULIP, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.OXEYE_DAISY, Blocks.POTTED_OXEYE_DAISY, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.CORNFLOWER, Blocks.POTTED_CORNFLOWER, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.LILY_OF_THE_VALLEY, Blocks.POTTED_LILY_OF_THE_VALLEY, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.WITHER_ROSE, Blocks.POTTED_WITHER_ROSE, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.RED_MUSHROOM, Blocks.POTTED_RED_MUSHROOM, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.BROWN_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.DEAD_BUSH, Blocks.POTTED_DEAD_BUSH, PlantType.NOT_TINTED);
        this.createPlantWithDefaultItem(Blocks.TORCHFLOWER, Blocks.POTTED_TORCHFLOWER, PlantType.NOT_TINTED);
        this.createPointedDripstone();
        this.createMushroomBlock(Blocks.BROWN_MUSHROOM_BLOCK);
        this.createMushroomBlock(Blocks.RED_MUSHROOM_BLOCK);
        this.createMushroomBlock(Blocks.MUSHROOM_STEM);
        this.createCrossBlock(Blocks.SHORT_GRASS, PlantType.TINTED);
        this.createItemWithGrassTint(Blocks.SHORT_GRASS);
        this.createCrossBlockWithDefaultItem(Blocks.SHORT_DRY_GRASS, PlantType.NOT_TINTED);
        this.createCrossBlockWithDefaultItem(Blocks.TALL_DRY_GRASS, PlantType.NOT_TINTED);
        this.createCrossBlock(Blocks.BUSH, PlantType.TINTED);
        this.createItemWithGrassTint(Blocks.BUSH);
        this.createCrossBlock(Blocks.SUGAR_CANE, PlantType.TINTED);
        this.registerSimpleFlatItemModel(Items.SUGAR_CANE);
        this.createGrowingPlant(Blocks.KELP, Blocks.KELP_PLANT, PlantType.NOT_TINTED);
        this.registerSimpleFlatItemModel(Items.KELP);
        this.createCrossBlock(Blocks.HANGING_ROOTS, PlantType.NOT_TINTED);
        this.createGrowingPlant(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT, PlantType.NOT_TINTED);
        this.createGrowingPlant(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT, PlantType.NOT_TINTED);
        this.registerSimpleFlatItemModel(Blocks.WEEPING_VINES, "_plant");
        this.registerSimpleFlatItemModel(Blocks.TWISTING_VINES, "_plant");
        this.createCrossBlockWithDefaultItem(Blocks.BAMBOO_SAPLING, PlantType.TINTED, TextureMapping.cross(TextureMapping.getBlockTexture(Blocks.BAMBOO, "_stage0")));
        this.createBamboo();
        this.createCrossBlockWithDefaultItem(Blocks.CACTUS_FLOWER, PlantType.NOT_TINTED);
        this.createCrossBlockWithDefaultItem(Blocks.COBWEB, PlantType.NOT_TINTED);
        this.createDoublePlantWithDefaultItem(Blocks.LILAC, PlantType.NOT_TINTED);
        this.createDoublePlantWithDefaultItem(Blocks.ROSE_BUSH, PlantType.NOT_TINTED);
        this.createDoublePlantWithDefaultItem(Blocks.PEONY, PlantType.NOT_TINTED);
        this.createTintedDoublePlant(Blocks.TALL_GRASS);
        this.createTintedDoublePlant(Blocks.LARGE_FERN);
        this.createSunflower();
        this.createTallSeagrass();
        this.createSmallDripleaf();
        this.createCoral(Blocks.TUBE_CORAL, Blocks.DEAD_TUBE_CORAL, Blocks.TUBE_CORAL_BLOCK, Blocks.DEAD_TUBE_CORAL_BLOCK, Blocks.TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_FAN, Blocks.TUBE_CORAL_WALL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN);
        this.createCoral(Blocks.BRAIN_CORAL, Blocks.DEAD_BRAIN_CORAL, Blocks.BRAIN_CORAL_BLOCK, Blocks.DEAD_BRAIN_CORAL_BLOCK, Blocks.BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN);
        this.createCoral(Blocks.BUBBLE_CORAL, Blocks.DEAD_BUBBLE_CORAL, Blocks.BUBBLE_CORAL_BLOCK, Blocks.DEAD_BUBBLE_CORAL_BLOCK, Blocks.BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN);
        this.createCoral(Blocks.FIRE_CORAL, Blocks.DEAD_FIRE_CORAL, Blocks.FIRE_CORAL_BLOCK, Blocks.DEAD_FIRE_CORAL_BLOCK, Blocks.FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_FAN, Blocks.FIRE_CORAL_WALL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN);
        this.createCoral(Blocks.HORN_CORAL, Blocks.DEAD_HORN_CORAL, Blocks.HORN_CORAL_BLOCK, Blocks.DEAD_HORN_CORAL_BLOCK, Blocks.HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_FAN, Blocks.HORN_CORAL_WALL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN);
        this.createStems(Blocks.MELON_STEM, Blocks.ATTACHED_MELON_STEM);
        this.createStems(Blocks.PUMPKIN_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
        this.woodProvider(Blocks.MANGROVE_LOG).logWithHorizontal(Blocks.MANGROVE_LOG).wood(Blocks.MANGROVE_WOOD);
        this.woodProvider(Blocks.STRIPPED_MANGROVE_LOG).logWithHorizontal(Blocks.STRIPPED_MANGROVE_LOG).wood(Blocks.STRIPPED_MANGROVE_WOOD);
        this.createHangingSign(Blocks.STRIPPED_MANGROVE_LOG, Blocks.MANGROVE_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN);
        this.createTintedLeaves(Blocks.MANGROVE_LEAVES, TexturedModel.LEAVES, -7158200);
        this.woodProvider(Blocks.ACACIA_LOG).logWithHorizontal(Blocks.ACACIA_LOG).wood(Blocks.ACACIA_WOOD);
        this.woodProvider(Blocks.STRIPPED_ACACIA_LOG).logWithHorizontal(Blocks.STRIPPED_ACACIA_LOG).wood(Blocks.STRIPPED_ACACIA_WOOD);
        this.createHangingSign(Blocks.STRIPPED_ACACIA_LOG, Blocks.ACACIA_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN);
        this.createPlantWithDefaultItem(Blocks.ACACIA_SAPLING, Blocks.POTTED_ACACIA_SAPLING, PlantType.NOT_TINTED);
        this.createTintedLeaves(Blocks.ACACIA_LEAVES, TexturedModel.LEAVES, -12012264);
        this.woodProvider(Blocks.CHERRY_LOG).logUVLocked(Blocks.CHERRY_LOG).wood(Blocks.CHERRY_WOOD);
        this.woodProvider(Blocks.STRIPPED_CHERRY_LOG).logUVLocked(Blocks.STRIPPED_CHERRY_LOG).wood(Blocks.STRIPPED_CHERRY_WOOD);
        this.createHangingSign(Blocks.STRIPPED_CHERRY_LOG, Blocks.CHERRY_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN);
        this.createPlantWithDefaultItem(Blocks.CHERRY_SAPLING, Blocks.POTTED_CHERRY_SAPLING, PlantType.NOT_TINTED);
        this.createTrivialBlock(Blocks.CHERRY_LEAVES, TexturedModel.LEAVES);
        this.woodProvider(Blocks.BIRCH_LOG).logWithHorizontal(Blocks.BIRCH_LOG).wood(Blocks.BIRCH_WOOD);
        this.woodProvider(Blocks.STRIPPED_BIRCH_LOG).logWithHorizontal(Blocks.STRIPPED_BIRCH_LOG).wood(Blocks.STRIPPED_BIRCH_WOOD);
        this.createHangingSign(Blocks.STRIPPED_BIRCH_LOG, Blocks.BIRCH_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN);
        this.createPlantWithDefaultItem(Blocks.BIRCH_SAPLING, Blocks.POTTED_BIRCH_SAPLING, PlantType.NOT_TINTED);
        this.createTintedLeaves(Blocks.BIRCH_LEAVES, TexturedModel.LEAVES, -8345771);
        this.woodProvider(Blocks.OAK_LOG).logWithHorizontal(Blocks.OAK_LOG).wood(Blocks.OAK_WOOD);
        this.woodProvider(Blocks.STRIPPED_OAK_LOG).logWithHorizontal(Blocks.STRIPPED_OAK_LOG).wood(Blocks.STRIPPED_OAK_WOOD);
        this.createHangingSign(Blocks.STRIPPED_OAK_LOG, Blocks.OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN);
        this.createPlantWithDefaultItem(Blocks.OAK_SAPLING, Blocks.POTTED_OAK_SAPLING, PlantType.NOT_TINTED);
        this.createTintedLeaves(Blocks.OAK_LEAVES, TexturedModel.LEAVES, -12012264);
        this.woodProvider(Blocks.SPRUCE_LOG).logWithHorizontal(Blocks.SPRUCE_LOG).wood(Blocks.SPRUCE_WOOD);
        this.woodProvider(Blocks.STRIPPED_SPRUCE_LOG).logWithHorizontal(Blocks.STRIPPED_SPRUCE_LOG).wood(Blocks.STRIPPED_SPRUCE_WOOD);
        this.createHangingSign(Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN);
        this.createPlantWithDefaultItem(Blocks.SPRUCE_SAPLING, Blocks.POTTED_SPRUCE_SAPLING, PlantType.NOT_TINTED);
        this.createTintedLeaves(Blocks.SPRUCE_LEAVES, TexturedModel.LEAVES, -10380959);
        this.woodProvider(Blocks.DARK_OAK_LOG).logWithHorizontal(Blocks.DARK_OAK_LOG).wood(Blocks.DARK_OAK_WOOD);
        this.woodProvider(Blocks.STRIPPED_DARK_OAK_LOG).logWithHorizontal(Blocks.STRIPPED_DARK_OAK_LOG).wood(Blocks.STRIPPED_DARK_OAK_WOOD);
        this.createHangingSign(Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN);
        this.createPlantWithDefaultItem(Blocks.DARK_OAK_SAPLING, Blocks.POTTED_DARK_OAK_SAPLING, PlantType.NOT_TINTED);
        this.createTintedLeaves(Blocks.DARK_OAK_LEAVES, TexturedModel.LEAVES, -12012264);
        this.woodProvider(Blocks.PALE_OAK_LOG).logWithHorizontal(Blocks.PALE_OAK_LOG).wood(Blocks.PALE_OAK_WOOD);
        this.woodProvider(Blocks.STRIPPED_PALE_OAK_LOG).logWithHorizontal(Blocks.STRIPPED_PALE_OAK_LOG).wood(Blocks.STRIPPED_PALE_OAK_WOOD);
        this.createHangingSign(Blocks.STRIPPED_PALE_OAK_LOG, Blocks.PALE_OAK_HANGING_SIGN, Blocks.PALE_OAK_WALL_HANGING_SIGN);
        this.createPlantWithDefaultItem(Blocks.PALE_OAK_SAPLING, Blocks.POTTED_PALE_OAK_SAPLING, PlantType.NOT_TINTED);
        this.createTrivialBlock(Blocks.PALE_OAK_LEAVES, TexturedModel.LEAVES);
        this.woodProvider(Blocks.JUNGLE_LOG).logWithHorizontal(Blocks.JUNGLE_LOG).wood(Blocks.JUNGLE_WOOD);
        this.woodProvider(Blocks.STRIPPED_JUNGLE_LOG).logWithHorizontal(Blocks.STRIPPED_JUNGLE_LOG).wood(Blocks.STRIPPED_JUNGLE_WOOD);
        this.createHangingSign(Blocks.STRIPPED_JUNGLE_LOG, Blocks.JUNGLE_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN);
        this.createPlantWithDefaultItem(Blocks.JUNGLE_SAPLING, Blocks.POTTED_JUNGLE_SAPLING, PlantType.NOT_TINTED);
        this.createTintedLeaves(Blocks.JUNGLE_LEAVES, TexturedModel.LEAVES, -12012264);
        this.woodProvider(Blocks.CRIMSON_STEM).log(Blocks.CRIMSON_STEM).wood(Blocks.CRIMSON_HYPHAE);
        this.woodProvider(Blocks.STRIPPED_CRIMSON_STEM).log(Blocks.STRIPPED_CRIMSON_STEM).wood(Blocks.STRIPPED_CRIMSON_HYPHAE);
        this.createHangingSign(Blocks.STRIPPED_CRIMSON_STEM, Blocks.CRIMSON_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN);
        this.createPlantWithDefaultItem(Blocks.CRIMSON_FUNGUS, Blocks.POTTED_CRIMSON_FUNGUS, PlantType.NOT_TINTED);
        this.createNetherRoots(Blocks.CRIMSON_ROOTS, Blocks.POTTED_CRIMSON_ROOTS);
        this.woodProvider(Blocks.WARPED_STEM).log(Blocks.WARPED_STEM).wood(Blocks.WARPED_HYPHAE);
        this.woodProvider(Blocks.STRIPPED_WARPED_STEM).log(Blocks.STRIPPED_WARPED_STEM).wood(Blocks.STRIPPED_WARPED_HYPHAE);
        this.createHangingSign(Blocks.STRIPPED_WARPED_STEM, Blocks.WARPED_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN);
        this.createPlantWithDefaultItem(Blocks.WARPED_FUNGUS, Blocks.POTTED_WARPED_FUNGUS, PlantType.NOT_TINTED);
        this.createNetherRoots(Blocks.WARPED_ROOTS, Blocks.POTTED_WARPED_ROOTS);
        this.woodProvider(Blocks.BAMBOO_BLOCK).logUVLocked(Blocks.BAMBOO_BLOCK);
        this.woodProvider(Blocks.STRIPPED_BAMBOO_BLOCK).logUVLocked(Blocks.STRIPPED_BAMBOO_BLOCK);
        this.createHangingSign(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
        this.createCrossBlock(Blocks.NETHER_SPROUTS, PlantType.NOT_TINTED);
        this.registerSimpleFlatItemModel(Items.NETHER_SPROUTS);
        this.createDoor(Blocks.IRON_DOOR);
        this.createTrapdoor(Blocks.IRON_TRAPDOOR);
        this.createSmoothStoneSlab();
        this.createPassiveRail(Blocks.RAIL);
        this.createActiveRail(Blocks.POWERED_RAIL);
        this.createActiveRail(Blocks.DETECTOR_RAIL);
        this.createActiveRail(Blocks.ACTIVATOR_RAIL);
        this.createComparator();
        this.createCommandBlock(Blocks.COMMAND_BLOCK);
        this.createCommandBlock(Blocks.REPEATING_COMMAND_BLOCK);
        this.createCommandBlock(Blocks.CHAIN_COMMAND_BLOCK);
        this.createAnvil(Blocks.ANVIL);
        this.createAnvil(Blocks.CHIPPED_ANVIL);
        this.createAnvil(Blocks.DAMAGED_ANVIL);
        this.createBarrel();
        this.createBell();
        this.createFurnace(Blocks.FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
        this.createFurnace(Blocks.BLAST_FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
        this.createFurnace(Blocks.SMOKER, TexturedModel.ORIENTABLE);
        this.createRedstoneWire();
        this.createRespawnAnchor();
        this.createSculkCatalyst();
        this.copyModel(Blocks.CHISELED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS);
        this.copyModel(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE);
        this.copyModel(Blocks.CRACKED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
        this.copyModel(Blocks.MOSSY_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS);
        this.createInfestedStone();
        this.copyModel(Blocks.STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS);
        this.createInfestedDeepslate();
    }

    private void createLightBlock() {
        ItemModel.Unbaked $$0 = ItemModelUtils.plainModel(this.createFlatItemModel(Items.LIGHT));
        HashMap<Integer, ItemModel.Unbaked> $$1 = new HashMap<Integer, ItemModel.Unbaked>(16);
        PropertyDispatch.C1<MultiVariant, Integer> $$2 = PropertyDispatch.initial(BlockStateProperties.LEVEL);
        for (int $$3 = 0; $$3 <= 15; ++$$3) {
            String $$4 = String.format(Locale.ROOT, "_%02d", $$3);
            ResourceLocation $$5 = TextureMapping.getItemTexture(Items.LIGHT, $$4);
            $$2.select($$3, BlockModelGenerators.plainVariant(ModelTemplates.PARTICLE_ONLY.createWithSuffix(Blocks.LIGHT, $$4, TextureMapping.particle($$5), this.modelOutput)));
            ItemModel.Unbaked $$6 = ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(Items.LIGHT, $$4), TextureMapping.layer0($$5), this.modelOutput));
            $$1.put($$3, $$6);
        }
        this.itemModelOutput.accept(Items.LIGHT, ItemModelUtils.selectBlockItemProperty(LightBlock.LEVEL, $$0, $$1));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.LIGHT).with($$2));
    }

    private void createCandleAndCandleCake(Block $$0, Block $$1) {
        this.registerSimpleFlatItemModel($$0.asItem());
        TextureMapping $$2 = TextureMapping.cube(TextureMapping.getBlockTexture($$0));
        TextureMapping $$3 = TextureMapping.cube(TextureMapping.getBlockTexture($$0, "_lit"));
        MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.CANDLE.createWithSuffix($$0, "_one_candle", $$2, this.modelOutput));
        MultiVariant $$5 = BlockModelGenerators.plainVariant(ModelTemplates.TWO_CANDLES.createWithSuffix($$0, "_two_candles", $$2, this.modelOutput));
        MultiVariant $$6 = BlockModelGenerators.plainVariant(ModelTemplates.THREE_CANDLES.createWithSuffix($$0, "_three_candles", $$2, this.modelOutput));
        MultiVariant $$7 = BlockModelGenerators.plainVariant(ModelTemplates.FOUR_CANDLES.createWithSuffix($$0, "_four_candles", $$2, this.modelOutput));
        MultiVariant $$8 = BlockModelGenerators.plainVariant(ModelTemplates.CANDLE.createWithSuffix($$0, "_one_candle_lit", $$3, this.modelOutput));
        MultiVariant $$9 = BlockModelGenerators.plainVariant(ModelTemplates.TWO_CANDLES.createWithSuffix($$0, "_two_candles_lit", $$3, this.modelOutput));
        MultiVariant $$10 = BlockModelGenerators.plainVariant(ModelTemplates.THREE_CANDLES.createWithSuffix($$0, "_three_candles_lit", $$3, this.modelOutput));
        MultiVariant $$11 = BlockModelGenerators.plainVariant(ModelTemplates.FOUR_CANDLES.createWithSuffix($$0, "_four_candles_lit", $$3, this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$0).with(PropertyDispatch.initial(BlockStateProperties.CANDLES, BlockStateProperties.LIT).select(1, false, $$4).select(2, false, $$5).select(3, false, $$6).select(4, false, $$7).select(1, true, $$8).select(2, true, $$9).select(3, true, $$10).select(4, true, $$11)));
        MultiVariant $$12 = BlockModelGenerators.plainVariant(ModelTemplates.CANDLE_CAKE.create($$1, TextureMapping.candleCake($$0, false), this.modelOutput));
        MultiVariant $$13 = BlockModelGenerators.plainVariant(ModelTemplates.CANDLE_CAKE.createWithSuffix($$1, "_lit", TextureMapping.candleCake($$0, true), this.modelOutput));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch($$1).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, $$13, $$12)));
    }

    private /* synthetic */ MultiVariant a(int[] $$0, Int2ObjectMap $$12, Block $$2, Integer $$3) {
        int $$4 = $$0[$$3];
        return BlockModelGenerators.plainVariant((ResourceLocation)$$12.computeIfAbsent($$4, $$1 -> this.createSuffixedVariant($$2, "_stage" + $$1, ModelTemplates.CROP, TextureMapping::crop)));
    }

    class BlockFamilyProvider {
        private final TextureMapping mapping;
        private final Map<ModelTemplate, ResourceLocation> models = new HashMap<ModelTemplate, ResourceLocation>();
        @Nullable
        private BlockFamily family;
        @Nullable
        private Variant fullBlock;
        private final Set<Block> skipGeneratingModelsFor = new HashSet<Block>();

        public BlockFamilyProvider(TextureMapping $$0) {
            this.mapping = $$0;
        }

        public BlockFamilyProvider fullBlock(Block $$0, ModelTemplate $$1) {
            this.fullBlock = BlockModelGenerators.plainModel($$1.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            if (FULL_BLOCK_MODEL_CUSTOM_GENERATORS.containsKey($$0)) {
                BlockModelGenerators.this.blockStateOutput.accept(FULL_BLOCK_MODEL_CUSTOM_GENERATORS.get($$0).create($$0, this.fullBlock, this.mapping, BlockModelGenerators.this.modelOutput));
            } else {
                BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, BlockModelGenerators.variant(this.fullBlock)));
            }
            return this;
        }

        public BlockFamilyProvider donateModelTo(Block $$0, Block $$1) {
            ResourceLocation $$2 = ModelLocationUtils.getModelLocation($$0);
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$1, BlockModelGenerators.plainVariant($$2)));
            BlockModelGenerators.this.itemModelOutput.copy($$0.asItem(), $$1.asItem());
            this.skipGeneratingModelsFor.add($$1);
            return this;
        }

        public BlockFamilyProvider button(Block $$0) {
            MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelTemplates.BUTTON.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.BUTTON_PRESSED.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createButton($$0, $$1, $$2));
            ResourceLocation $$3 = ModelTemplates.BUTTON_INVENTORY.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.registerSimpleItemModel($$0, $$3);
            return this;
        }

        public BlockFamilyProvider wall(Block $$0) {
            MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelTemplates.WALL_POST.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.WALL_LOW_SIDE.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.WALL_TALL_SIDE.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createWall($$0, $$1, $$2, $$3));
            ResourceLocation $$4 = ModelTemplates.WALL_INVENTORY.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.registerSimpleItemModel($$0, $$4);
            return this;
        }

        public BlockFamilyProvider customFence(Block $$0) {
            TextureMapping $$1 = TextureMapping.customParticle($$0);
            MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_POST.create($$0, $$1, BlockModelGenerators.this.modelOutput));
            MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_SIDE_NORTH.create($$0, $$1, BlockModelGenerators.this.modelOutput));
            MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_SIDE_EAST.create($$0, $$1, BlockModelGenerators.this.modelOutput));
            MultiVariant $$5 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_SIDE_SOUTH.create($$0, $$1, BlockModelGenerators.this.modelOutput));
            MultiVariant $$6 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_SIDE_WEST.create($$0, $$1, BlockModelGenerators.this.modelOutput));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createCustomFence($$0, $$2, $$3, $$4, $$5, $$6));
            ResourceLocation $$7 = ModelTemplates.CUSTOM_FENCE_INVENTORY.create($$0, $$1, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.registerSimpleItemModel($$0, $$7);
            return this;
        }

        public BlockFamilyProvider fence(Block $$0) {
            MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_POST.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_SIDE.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createFence($$0, $$1, $$2));
            ResourceLocation $$3 = ModelTemplates.FENCE_INVENTORY.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.registerSimpleItemModel($$0, $$3);
            return this;
        }

        public BlockFamilyProvider customFenceGate(Block $$0) {
            TextureMapping $$1 = TextureMapping.customParticle($$0);
            MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_GATE_OPEN.create($$0, $$1, BlockModelGenerators.this.modelOutput));
            MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_GATE_CLOSED.create($$0, $$1, BlockModelGenerators.this.modelOutput));
            MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_GATE_WALL_OPEN.create($$0, $$1, BlockModelGenerators.this.modelOutput));
            MultiVariant $$5 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_GATE_WALL_CLOSED.create($$0, $$1, BlockModelGenerators.this.modelOutput));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createFenceGate($$0, $$2, $$3, $$4, $$5, false));
            return this;
        }

        public BlockFamilyProvider fenceGate(Block $$0) {
            MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_GATE_OPEN.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_GATE_CLOSED.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            MultiVariant $$3 = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_GATE_WALL_OPEN.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            MultiVariant $$4 = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_GATE_WALL_CLOSED.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createFenceGate($$0, $$1, $$2, $$3, $$4, true));
            return this;
        }

        public BlockFamilyProvider pressurePlate(Block $$0) {
            MultiVariant $$1 = BlockModelGenerators.plainVariant(ModelTemplates.PRESSURE_PLATE_UP.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.PRESSURE_PLATE_DOWN.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createPressurePlate($$0, $$1, $$2));
            return this;
        }

        public BlockFamilyProvider sign(Block $$0) {
            if (this.family == null) {
                throw new IllegalStateException("Family not defined");
            }
            Block $$1 = this.family.getVariants().get((Object)BlockFamily.Variant.WALL_SIGN);
            MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.PARTICLE_ONLY.create($$0, this.mapping, BlockModelGenerators.this.modelOutput));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, $$2));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$1, $$2));
            BlockModelGenerators.this.registerSimpleFlatItemModel($$0.asItem());
            return this;
        }

        public BlockFamilyProvider slab(Block $$0) {
            if (this.fullBlock == null) {
                throw new IllegalStateException("Full block not generated yet");
            }
            ResourceLocation $$1 = this.getOrCreateModel(ModelTemplates.SLAB_BOTTOM, $$0);
            MultiVariant $$2 = BlockModelGenerators.plainVariant(this.getOrCreateModel(ModelTemplates.SLAB_TOP, $$0));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSlab($$0, BlockModelGenerators.plainVariant($$1), $$2, BlockModelGenerators.variant(this.fullBlock)));
            BlockModelGenerators.this.registerSimpleItemModel($$0, $$1);
            return this;
        }

        public BlockFamilyProvider stairs(Block $$0) {
            MultiVariant $$1 = BlockModelGenerators.plainVariant(this.getOrCreateModel(ModelTemplates.STAIRS_INNER, $$0));
            ResourceLocation $$2 = this.getOrCreateModel(ModelTemplates.STAIRS_STRAIGHT, $$0);
            MultiVariant $$3 = BlockModelGenerators.plainVariant(this.getOrCreateModel(ModelTemplates.STAIRS_OUTER, $$0));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createStairs($$0, $$1, BlockModelGenerators.plainVariant($$2), $$3));
            BlockModelGenerators.this.registerSimpleItemModel($$0, $$2);
            return this;
        }

        private BlockFamilyProvider fullBlockVariant(Block $$0) {
            TexturedModel $$1 = TEXTURED_MODELS.getOrDefault($$0, TexturedModel.CUBE.get($$0));
            MultiVariant $$2 = BlockModelGenerators.plainVariant($$1.create($$0, BlockModelGenerators.this.modelOutput));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock($$0, $$2));
            return this;
        }

        private BlockFamilyProvider door(Block $$0) {
            BlockModelGenerators.this.createDoor($$0);
            return this;
        }

        private void trapdoor(Block $$0) {
            if (NON_ORIENTABLE_TRAPDOOR.contains($$0)) {
                BlockModelGenerators.this.createTrapdoor($$0);
            } else {
                BlockModelGenerators.this.createOrientableTrapdoor($$0);
            }
        }

        private ResourceLocation getOrCreateModel(ModelTemplate $$0, Block $$12) {
            return this.models.computeIfAbsent($$0, $$1 -> $$1.create($$12, this.mapping, BlockModelGenerators.this.modelOutput));
        }

        public BlockFamilyProvider generateFor(BlockFamily $$02) {
            this.family = $$02;
            $$02.getVariants().forEach(($$0, $$1) -> {
                if (this.skipGeneratingModelsFor.contains($$1)) {
                    return;
                }
                BiConsumer<BlockFamilyProvider, Block> $$2 = SHAPE_CONSUMERS.get($$0);
                if ($$2 != null) {
                    $$2.accept(this, (Block)$$1);
                }
            });
            return this;
        }
    }

    class WoodProvider {
        private final TextureMapping logMapping;

        public WoodProvider(TextureMapping $$0) {
            this.logMapping = $$0;
        }

        public WoodProvider wood(Block $$0) {
            TextureMapping $$1 = this.logMapping.copyAndUpdate(TextureSlot.END, this.logMapping.get(TextureSlot.SIDE));
            ResourceLocation $$2 = ModelTemplates.CUBE_COLUMN.create($$0, $$1, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createAxisAlignedPillarBlock($$0, BlockModelGenerators.plainVariant($$2)));
            BlockModelGenerators.this.registerSimpleItemModel($$0, $$2);
            return this;
        }

        public WoodProvider log(Block $$0) {
            ResourceLocation $$1 = ModelTemplates.CUBE_COLUMN.create($$0, this.logMapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createAxisAlignedPillarBlock($$0, BlockModelGenerators.plainVariant($$1)));
            BlockModelGenerators.this.registerSimpleItemModel($$0, $$1);
            return this;
        }

        public WoodProvider logWithHorizontal(Block $$0) {
            ResourceLocation $$1 = ModelTemplates.CUBE_COLUMN.create($$0, this.logMapping, BlockModelGenerators.this.modelOutput);
            MultiVariant $$2 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_COLUMN_HORIZONTAL.create($$0, this.logMapping, BlockModelGenerators.this.modelOutput));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createRotatedPillarWithHorizontalVariant($$0, BlockModelGenerators.plainVariant($$1), $$2));
            BlockModelGenerators.this.registerSimpleItemModel($$0, $$1);
            return this;
        }

        public WoodProvider logUVLocked(Block $$0) {
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createPillarBlockUVLocked($$0, this.logMapping, BlockModelGenerators.this.modelOutput));
            BlockModelGenerators.this.registerSimpleItemModel($$0, ModelTemplates.CUBE_COLUMN.create($$0, this.logMapping, BlockModelGenerators.this.modelOutput));
            return this;
        }
    }

    static final class PlantType
    extends Enum<PlantType> {
        public static final /* enum */ PlantType TINTED = new PlantType(ModelTemplates.TINTED_CROSS, ModelTemplates.TINTED_FLOWER_POT_CROSS, false);
        public static final /* enum */ PlantType NOT_TINTED = new PlantType(ModelTemplates.CROSS, ModelTemplates.FLOWER_POT_CROSS, false);
        public static final /* enum */ PlantType EMISSIVE_NOT_TINTED = new PlantType(ModelTemplates.CROSS_EMISSIVE, ModelTemplates.FLOWER_POT_CROSS_EMISSIVE, true);
        private final ModelTemplate blockTemplate;
        private final ModelTemplate flowerPotTemplate;
        private final boolean isEmissive;
        private static final /* synthetic */ PlantType[] $VALUES;

        public static PlantType[] values() {
            return (PlantType[])$VALUES.clone();
        }

        public static PlantType valueOf(String $$0) {
            return Enum.valueOf(PlantType.class, $$0);
        }

        private PlantType(ModelTemplate $$0, ModelTemplate $$1, boolean $$2) {
            this.blockTemplate = $$0;
            this.flowerPotTemplate = $$1;
            this.isEmissive = $$2;
        }

        public ModelTemplate getCross() {
            return this.blockTemplate;
        }

        public ModelTemplate getCrossPot() {
            return this.flowerPotTemplate;
        }

        public ResourceLocation createItemModel(BlockModelGenerators $$0, Block $$1) {
            Item $$2 = $$1.asItem();
            if (this.isEmissive) {
                return $$0.createFlatItemModelWithBlockTextureAndOverlay($$2, $$1, "_emissive");
            }
            return $$0.createFlatItemModelWithBlockTexture($$2, $$1);
        }

        public TextureMapping getTextureMapping(Block $$0) {
            return this.isEmissive ? TextureMapping.crossEmissive($$0) : TextureMapping.cross($$0);
        }

        public TextureMapping getPlantTextureMapping(Block $$0) {
            return this.isEmissive ? TextureMapping.plantEmissive($$0) : TextureMapping.plant($$0);
        }

        private static /* synthetic */ PlantType[] c() {
            return new PlantType[]{TINTED, NOT_TINTED, EMISSIVE_NOT_TINTED};
        }

        static {
            $VALUES = PlantType.c();
        }
    }

    record BookSlotModelCacheKey(ModelTemplate template, String modelSuffix) {
    }

    @FunctionalInterface
    static interface BlockStateGeneratorSupplier {
        public BlockModelDefinitionGenerator create(Block var1, Variant var2, TextureMapping var3, BiConsumer<ResourceLocation, ModelInstance> var4);
    }
}

