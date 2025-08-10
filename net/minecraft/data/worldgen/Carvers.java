/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

public class Carvers {
    public static final ResourceKey<ConfiguredWorldCarver<?>> CAVE = Carvers.createKey("cave");
    public static final ResourceKey<ConfiguredWorldCarver<?>> CAVE_EXTRA_UNDERGROUND = Carvers.createKey("cave_extra_underground");
    public static final ResourceKey<ConfiguredWorldCarver<?>> CANYON = Carvers.createKey("canyon");
    public static final ResourceKey<ConfiguredWorldCarver<?>> NETHER_CAVE = Carvers.createKey("nether_cave");

    private static ResourceKey<ConfiguredWorldCarver<?>> createKey(String $$0) {
        return ResourceKey.create(Registries.CONFIGURED_CARVER, ResourceLocation.withDefaultNamespace($$0));
    }

    public static void bootstrap(BootstrapContext<ConfiguredWorldCarver<?>> $$0) {
        HolderGetter<Block> $$1 = $$0.lookup(Registries.BLOCK);
        $$0.register(CAVE, WorldCarver.CAVE.configured(new CaveCarverConfiguration(0.15f, UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)), UniformFloat.of(0.1f, 0.9f), VerticalAnchor.aboveBottom(8), CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()), $$1.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES), UniformFloat.of(0.7f, 1.4f), UniformFloat.of(0.8f, 1.3f), UniformFloat.of(-1.0f, -0.4f))));
        $$0.register(CAVE_EXTRA_UNDERGROUND, WorldCarver.CAVE.configured(new CaveCarverConfiguration(0.07f, UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(47)), UniformFloat.of(0.1f, 0.9f), VerticalAnchor.aboveBottom(8), CarverDebugSettings.of(false, Blocks.OAK_BUTTON.defaultBlockState()), $$1.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES), UniformFloat.of(0.7f, 1.4f), UniformFloat.of(0.8f, 1.3f), UniformFloat.of(-1.0f, -0.4f))));
        $$0.register(CANYON, WorldCarver.CANYON.configured(new CanyonCarverConfiguration(0.01f, UniformHeight.of(VerticalAnchor.absolute(10), VerticalAnchor.absolute(67)), ConstantFloat.of(3.0f), VerticalAnchor.aboveBottom(8), CarverDebugSettings.of(false, Blocks.WARPED_BUTTON.defaultBlockState()), $$1.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES), UniformFloat.of(-0.125f, 0.125f), new CanyonCarverConfiguration.CanyonShapeConfiguration(UniformFloat.of(0.75f, 1.0f), TrapezoidFloat.of(0.0f, 6.0f, 2.0f), 3, UniformFloat.of(0.75f, 1.0f), 1.0f, 0.0f))));
        $$0.register(NETHER_CAVE, WorldCarver.NETHER_CAVE.configured(new CaveCarverConfiguration(0.2f, UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.belowTop(1)), ConstantFloat.of(0.5f), VerticalAnchor.aboveBottom(10), $$1.getOrThrow(BlockTags.NETHER_CARVER_REPLACEABLES), ConstantFloat.of(1.0f), ConstantFloat.of(1.0f), ConstantFloat.of(-0.7f))));
    }
}

