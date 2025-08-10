/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.data.models.model;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class TexturedModel {
    public static final Provider CUBE = TexturedModel.createDefault(TextureMapping::cube, ModelTemplates.CUBE_ALL);
    public static final Provider CUBE_INNER_FACES = TexturedModel.createDefault(TextureMapping::cube, ModelTemplates.CUBE_ALL_INNER_FACES);
    public static final Provider CUBE_MIRRORED = TexturedModel.createDefault(TextureMapping::cube, ModelTemplates.CUBE_MIRRORED_ALL);
    public static final Provider COLUMN = TexturedModel.createDefault(TextureMapping::column, ModelTemplates.CUBE_COLUMN);
    public static final Provider COLUMN_HORIZONTAL = TexturedModel.createDefault(TextureMapping::column, ModelTemplates.CUBE_COLUMN_HORIZONTAL);
    public static final Provider CUBE_TOP_BOTTOM = TexturedModel.createDefault(TextureMapping::cubeBottomTop, ModelTemplates.CUBE_BOTTOM_TOP);
    public static final Provider CUBE_TOP = TexturedModel.createDefault(TextureMapping::cubeTop, ModelTemplates.CUBE_TOP);
    public static final Provider ORIENTABLE_ONLY_TOP = TexturedModel.createDefault(TextureMapping::orientableCubeOnlyTop, ModelTemplates.CUBE_ORIENTABLE);
    public static final Provider ORIENTABLE = TexturedModel.createDefault(TextureMapping::orientableCube, ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM);
    public static final Provider CARPET = TexturedModel.createDefault(TextureMapping::wool, ModelTemplates.CARPET);
    public static final Provider MOSSY_CARPET_SIDE = TexturedModel.createDefault(TextureMapping::side, ModelTemplates.MOSSY_CARPET_SIDE);
    public static final Provider FLOWERBED_1 = TexturedModel.createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_1);
    public static final Provider FLOWERBED_2 = TexturedModel.createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_2);
    public static final Provider FLOWERBED_3 = TexturedModel.createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_3);
    public static final Provider FLOWERBED_4 = TexturedModel.createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_4);
    public static final Provider LEAF_LITTER_1 = TexturedModel.createDefault(TextureMapping::defaultTexture, ModelTemplates.LEAF_LITTER_1);
    public static final Provider LEAF_LITTER_2 = TexturedModel.createDefault(TextureMapping::defaultTexture, ModelTemplates.LEAF_LITTER_2);
    public static final Provider LEAF_LITTER_3 = TexturedModel.createDefault(TextureMapping::defaultTexture, ModelTemplates.LEAF_LITTER_3);
    public static final Provider LEAF_LITTER_4 = TexturedModel.createDefault(TextureMapping::defaultTexture, ModelTemplates.LEAF_LITTER_4);
    public static final Provider GLAZED_TERRACOTTA = TexturedModel.createDefault(TextureMapping::pattern, ModelTemplates.GLAZED_TERRACOTTA);
    public static final Provider CORAL_FAN = TexturedModel.createDefault(TextureMapping::fan, ModelTemplates.CORAL_FAN);
    public static final Provider ANVIL = TexturedModel.createDefault(TextureMapping::top, ModelTemplates.ANVIL);
    public static final Provider LEAVES = TexturedModel.createDefault(TextureMapping::cube, ModelTemplates.LEAVES);
    public static final Provider LANTERN = TexturedModel.createDefault(TextureMapping::lantern, ModelTemplates.LANTERN);
    public static final Provider HANGING_LANTERN = TexturedModel.createDefault(TextureMapping::lantern, ModelTemplates.HANGING_LANTERN);
    public static final Provider SEAGRASS = TexturedModel.createDefault(TextureMapping::defaultTexture, ModelTemplates.SEAGRASS);
    public static final Provider COLUMN_ALT = TexturedModel.createDefault(TextureMapping::logColumn, ModelTemplates.CUBE_COLUMN);
    public static final Provider COLUMN_HORIZONTAL_ALT = TexturedModel.createDefault(TextureMapping::logColumn, ModelTemplates.CUBE_COLUMN_HORIZONTAL);
    public static final Provider TOP_BOTTOM_WITH_WALL = TexturedModel.createDefault(TextureMapping::cubeBottomTopWithWall, ModelTemplates.CUBE_BOTTOM_TOP);
    public static final Provider COLUMN_WITH_WALL = TexturedModel.createDefault(TextureMapping::columnWithWall, ModelTemplates.CUBE_COLUMN);
    private final TextureMapping mapping;
    private final ModelTemplate template;

    private TexturedModel(TextureMapping $$0, ModelTemplate $$1) {
        this.mapping = $$0;
        this.template = $$1;
    }

    public ModelTemplate getTemplate() {
        return this.template;
    }

    public TextureMapping getMapping() {
        return this.mapping;
    }

    public TexturedModel updateTextures(Consumer<TextureMapping> $$0) {
        $$0.accept(this.mapping);
        return this;
    }

    public ResourceLocation create(Block $$0, BiConsumer<ResourceLocation, ModelInstance> $$1) {
        return this.template.create($$0, this.mapping, $$1);
    }

    public ResourceLocation createWithSuffix(Block $$0, String $$1, BiConsumer<ResourceLocation, ModelInstance> $$2) {
        return this.template.createWithSuffix($$0, $$1, this.mapping, $$2);
    }

    private static Provider createDefault(Function<Block, TextureMapping> $$0, ModelTemplate $$1) {
        return $$2 -> new TexturedModel((TextureMapping)$$0.apply($$2), $$1);
    }

    public static TexturedModel createAllSame(ResourceLocation $$0) {
        return new TexturedModel(TextureMapping.cube($$0), ModelTemplates.CUBE_ALL);
    }

    @FunctionalInterface
    public static interface Provider {
        public TexturedModel get(Block var1);

        default public ResourceLocation create(Block $$0, BiConsumer<ResourceLocation, ModelInstance> $$1) {
            return this.get($$0).create($$0, $$1);
        }

        default public ResourceLocation createWithSuffix(Block $$0, String $$1, BiConsumer<ResourceLocation, ModelInstance> $$2) {
            return this.get($$0).createWithSuffix($$0, $$1, $$2);
        }

        default public Provider updateTexture(Consumer<TextureMapping> $$0) {
            return $$1 -> this.get($$1).updateTextures($$0);
        }
    }
}

