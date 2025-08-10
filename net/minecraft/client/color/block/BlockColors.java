/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.color.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;

public class BlockColors {
    private static final int DEFAULT = -1;
    public static final int LILY_PAD_IN_WORLD = -14647248;
    public static final int LILY_PAD_DEFAULT = -9321636;
    private final IdMapper<BlockColor> blockColors = new IdMapper(32);
    private final Map<Block, Set<Property<?>>> coloringStates = Maps.newHashMap();

    public static BlockColors createDefault() {
        BlockColors $$02 = new BlockColors();
        $$02.a((BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2, int $$3) -> {
            if ($$1 == null || $$2 == null) {
                return GrassColor.getDefaultColor();
            }
            return BiomeColors.getAverageGrassColor($$1, $$0.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? $$2.below() : $$2);
        }, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
        $$02.a(DoublePlantBlock.HALF, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
        $$02.a((BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2, int $$3) -> {
            if ($$1 == null || $$2 == null) {
                return GrassColor.getDefaultColor();
            }
            return BiomeColors.getAverageGrassColor($$1, $$2);
        }, Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.SHORT_GRASS, Blocks.POTTED_FERN, Blocks.BUSH);
        $$02.a((BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2, int $$3) -> {
            if ($$3 != 0) {
                if ($$1 == null || $$2 == null) {
                    return GrassColor.getDefaultColor();
                }
                return BiomeColors.getAverageGrassColor($$1, $$2);
            }
            return -1;
        }, Blocks.PINK_PETALS, Blocks.WILDFLOWERS);
        $$02.a((BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2, int $$3) -> -10380959, Blocks.SPRUCE_LEAVES);
        $$02.a((BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2, int $$3) -> -8345771, Blocks.BIRCH_LEAVES);
        $$02.a((BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2, int $$3) -> {
            if ($$1 == null || $$2 == null) {
                return -12012264;
            }
            return BiomeColors.getAverageFoliageColor($$1, $$2);
        }, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE, Blocks.MANGROVE_LEAVES);
        $$02.a((BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2, int $$3) -> {
            if ($$1 == null || $$2 == null) {
                return -10732494;
            }
            return BiomeColors.getAverageDryFoliageColor($$1, $$2);
        }, Blocks.LEAF_LITTER);
        $$02.a((BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2, int $$3) -> {
            if ($$1 == null || $$2 == null) {
                return -1;
            }
            return BiomeColors.getAverageWaterColor($$1, $$2);
        }, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.WATER_CAULDRON);
        $$02.a((BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2, int $$3) -> RedStoneWireBlock.getColorForPower($$0.getValue(RedStoneWireBlock.POWER)), Blocks.REDSTONE_WIRE);
        $$02.a(RedStoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
        $$02.a((BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2, int $$3) -> {
            if ($$1 == null || $$2 == null) {
                return -1;
            }
            return BiomeColors.getAverageGrassColor($$1, $$2);
        }, Blocks.SUGAR_CANE);
        $$02.a((BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2, int $$3) -> -2046180, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
        $$02.a((BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2, int $$3) -> {
            int $$4 = $$0.getValue(StemBlock.AGE);
            return ARGB.color($$4 * 32, 255 - $$4 * 8, $$4 * 4);
        }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
        $$02.a(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
        $$02.a((BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2, int $$3) -> {
            if ($$1 == null || $$2 == null) {
                return -9321636;
            }
            return -14647248;
        }, Blocks.LILY_PAD);
        return $$02;
    }

    public int getColor(BlockState $$0, Level $$1, BlockPos $$2) {
        BlockColor $$3 = this.blockColors.byId(BuiltInRegistries.BLOCK.getId($$0.getBlock()));
        if ($$3 != null) {
            return $$3.getColor($$0, null, null, 0);
        }
        MapColor $$4 = $$0.getMapColor($$1, $$2);
        return $$4 != null ? $$4.col : -1;
    }

    public int getColor(BlockState $$0, @Nullable BlockAndTintGetter $$1, @Nullable BlockPos $$2, int $$3) {
        BlockColor $$4 = this.blockColors.byId(BuiltInRegistries.BLOCK.getId($$0.getBlock()));
        return $$4 == null ? -1 : $$4.getColor($$0, $$1, $$2, $$3);
    }

    public void a(BlockColor $$0, Block ... $$1) {
        for (Block $$2 : $$1) {
            this.blockColors.addMapping($$0, BuiltInRegistries.BLOCK.getId($$2));
        }
    }

    private void a(Set<Property<?>> $$0, Block ... $$1) {
        for (Block $$2 : $$1) {
            this.coloringStates.put($$2, $$0);
        }
    }

    private void a(Property<?> $$0, Block ... $$1) {
        this.a(ImmutableSet.of($$0), $$1);
    }

    public Set<Property<?>> getColoringProperties(Block $$0) {
        return this.coloringStates.getOrDefault($$0, ImmutableSet.of());
    }
}

