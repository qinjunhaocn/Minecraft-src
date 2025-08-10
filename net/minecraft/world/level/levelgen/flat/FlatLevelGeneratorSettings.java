/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.flat;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.slf4j.Logger;

public class FlatLevelGeneratorSettings {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<FlatLevelGeneratorSettings> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)RegistryCodecs.homogeneousList(Registries.STRUCTURE_SET).lenientOptionalFieldOf("structure_overrides").forGetter($$0 -> $$0.structureOverrides), (App)FlatLayerInfo.CODEC.listOf().fieldOf("layers").forGetter(FlatLevelGeneratorSettings::getLayersInfo), (App)Codec.BOOL.fieldOf("lakes").orElse((Object)false).forGetter($$0 -> $$0.addLakes), (App)Codec.BOOL.fieldOf("features").orElse((Object)false).forGetter($$0 -> $$0.decoration), (App)Biome.CODEC.lenientOptionalFieldOf("biome").orElseGet(Optional::empty).forGetter($$0 -> Optional.of($$0.biome)), RegistryOps.retrieveElement(Biomes.PLAINS), RegistryOps.retrieveElement(MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND), RegistryOps.retrieveElement(MiscOverworldPlacements.LAKE_LAVA_SURFACE)).apply((Applicative)$$02, FlatLevelGeneratorSettings::new)).comapFlatMap(FlatLevelGeneratorSettings::validateHeight, Function.identity()).stable();
    private final Optional<HolderSet<StructureSet>> structureOverrides;
    private final List<FlatLayerInfo> layersInfo = Lists.newArrayList();
    private final Holder<Biome> biome;
    private final List<BlockState> layers;
    private boolean voidGen;
    private boolean decoration;
    private boolean addLakes;
    private final List<Holder<PlacedFeature>> lakes;

    private static DataResult<FlatLevelGeneratorSettings> validateHeight(FlatLevelGeneratorSettings $$0) {
        int $$1 = $$0.layersInfo.stream().mapToInt(FlatLayerInfo::getHeight).sum();
        if ($$1 > DimensionType.Y_SIZE) {
            return DataResult.error(() -> "Sum of layer heights is > " + DimensionType.Y_SIZE, (Object)$$0);
        }
        return DataResult.success((Object)$$0);
    }

    private FlatLevelGeneratorSettings(Optional<HolderSet<StructureSet>> $$0, List<FlatLayerInfo> $$1, boolean $$2, boolean $$3, Optional<Holder<Biome>> $$4, Holder.Reference<Biome> $$5, Holder<PlacedFeature> $$6, Holder<PlacedFeature> $$7) {
        this($$0, FlatLevelGeneratorSettings.getBiome($$4, $$5), List.of($$6, $$7));
        if ($$2) {
            this.setAddLakes();
        }
        if ($$3) {
            this.setDecoration();
        }
        this.layersInfo.addAll($$1);
        this.updateLayers();
    }

    private static Holder<Biome> getBiome(Optional<? extends Holder<Biome>> $$0, Holder<Biome> $$1) {
        if ($$0.isEmpty()) {
            LOGGER.error("Unknown biome, defaulting to plains");
            return $$1;
        }
        return $$0.get();
    }

    public FlatLevelGeneratorSettings(Optional<HolderSet<StructureSet>> $$0, Holder<Biome> $$1, List<Holder<PlacedFeature>> $$2) {
        this.structureOverrides = $$0;
        this.biome = $$1;
        this.layers = Lists.newArrayList();
        this.lakes = $$2;
    }

    public FlatLevelGeneratorSettings withBiomeAndLayers(List<FlatLayerInfo> $$0, Optional<HolderSet<StructureSet>> $$1, Holder<Biome> $$2) {
        FlatLevelGeneratorSettings $$3 = new FlatLevelGeneratorSettings($$1, $$2, this.lakes);
        for (FlatLayerInfo $$4 : $$0) {
            $$3.layersInfo.add(new FlatLayerInfo($$4.getHeight(), $$4.getBlockState().getBlock()));
            $$3.updateLayers();
        }
        if (this.decoration) {
            $$3.setDecoration();
        }
        if (this.addLakes) {
            $$3.setAddLakes();
        }
        return $$3;
    }

    public void setDecoration() {
        this.decoration = true;
    }

    public void setAddLakes() {
        this.addLakes = true;
    }

    public BiomeGenerationSettings adjustGenerationSettings(Holder<Biome> $$0) {
        boolean $$4;
        if (!$$0.equals(this.biome)) {
            return $$0.value().getGenerationSettings();
        }
        BiomeGenerationSettings $$1 = this.getBiome().value().getGenerationSettings();
        BiomeGenerationSettings.PlainBuilder $$2 = new BiomeGenerationSettings.PlainBuilder();
        if (this.addLakes) {
            for (Holder<PlacedFeature> $$3 : this.lakes) {
                $$2.addFeature(GenerationStep.Decoration.LAKES, $$3);
            }
        }
        boolean bl = $$4 = (!this.voidGen || $$0.is(Biomes.THE_VOID)) && this.decoration;
        if ($$4) {
            List<HolderSet<PlacedFeature>> $$5 = $$1.features();
            for (int $$6 = 0; $$6 < $$5.size(); ++$$6) {
                if ($$6 == GenerationStep.Decoration.UNDERGROUND_STRUCTURES.ordinal() || $$6 == GenerationStep.Decoration.SURFACE_STRUCTURES.ordinal() || this.addLakes && $$6 == GenerationStep.Decoration.LAKES.ordinal()) continue;
                HolderSet<PlacedFeature> $$7 = $$5.get($$6);
                for (Holder holder : $$7) {
                    $$2.addFeature($$6, (Holder<PlacedFeature>)holder);
                }
            }
        }
        List<BlockState> $$9 = this.getLayers();
        for (int $$10 = 0; $$10 < $$9.size(); ++$$10) {
            BlockState $$11 = $$9.get($$10);
            if (Heightmap.Types.MOTION_BLOCKING.isOpaque().test($$11)) continue;
            $$9.set($$10, null);
            $$2.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, PlacementUtils.a(Feature.FILL_LAYER, new LayerConfiguration($$10, $$11), new PlacementModifier[0]));
        }
        return $$2.build();
    }

    public Optional<HolderSet<StructureSet>> structureOverrides() {
        return this.structureOverrides;
    }

    public Holder<Biome> getBiome() {
        return this.biome;
    }

    public List<FlatLayerInfo> getLayersInfo() {
        return this.layersInfo;
    }

    public List<BlockState> getLayers() {
        return this.layers;
    }

    public void updateLayers() {
        this.layers.clear();
        for (FlatLayerInfo $$02 : this.layersInfo) {
            for (int $$1 = 0; $$1 < $$02.getHeight(); ++$$1) {
                this.layers.add($$02.getBlockState());
            }
        }
        this.voidGen = this.layers.stream().allMatch($$0 -> $$0.is(Blocks.AIR));
    }

    public static FlatLevelGeneratorSettings getDefault(HolderGetter<Biome> $$0, HolderGetter<StructureSet> $$1, HolderGetter<PlacedFeature> $$2) {
        HolderSet.Direct $$3 = HolderSet.a($$1.getOrThrow(BuiltinStructureSets.STRONGHOLDS), $$1.getOrThrow(BuiltinStructureSets.VILLAGES));
        FlatLevelGeneratorSettings $$4 = new FlatLevelGeneratorSettings(Optional.of($$3), FlatLevelGeneratorSettings.getDefaultBiome($$0), FlatLevelGeneratorSettings.createLakesList($$2));
        $$4.getLayersInfo().add(new FlatLayerInfo(1, Blocks.BEDROCK));
        $$4.getLayersInfo().add(new FlatLayerInfo(2, Blocks.DIRT));
        $$4.getLayersInfo().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
        $$4.updateLayers();
        return $$4;
    }

    public static Holder<Biome> getDefaultBiome(HolderGetter<Biome> $$0) {
        return $$0.getOrThrow(Biomes.PLAINS);
    }

    public static List<Holder<PlacedFeature>> createLakesList(HolderGetter<PlacedFeature> $$0) {
        return List.of($$0.getOrThrow(MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND), $$0.getOrThrow(MiscOverworldPlacements.LAKE_LAVA_SURFACE));
    }
}

