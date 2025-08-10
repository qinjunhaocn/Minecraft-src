/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.worldselection;

import java.util.Map;
import java.util.Optional;
import net.minecraft.client.gui.screens.CreateBuffetWorldScreen;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public interface PresetEditor {
    public static final Map<Optional<ResourceKey<WorldPreset>>, PresetEditor> EDITORS = Map.of(Optional.of(WorldPresets.FLAT), ($$0, $$12) -> {
        ChunkGenerator $$2 = $$12.selectedDimensions().overworld();
        RegistryAccess.Frozen $$3 = $$12.worldgenLoadContext();
        HolderLookup.RegistryLookup $$4 = $$3.lookupOrThrow(Registries.BIOME);
        HolderLookup.RegistryLookup $$5 = $$3.lookupOrThrow(Registries.STRUCTURE_SET);
        HolderLookup.RegistryLookup $$6 = $$3.lookupOrThrow(Registries.PLACED_FEATURE);
        return new CreateFlatWorldScreen($$0, $$1 -> $$0.getUiState().updateDimensions(PresetEditor.flatWorldConfigurator($$1)), $$2 instanceof FlatLevelSource ? ((FlatLevelSource)$$2).settings() : FlatLevelGeneratorSettings.getDefault($$4, $$5, $$6));
    }, Optional.of(WorldPresets.SINGLE_BIOME_SURFACE), ($$0, $$12) -> new CreateBuffetWorldScreen($$0, $$12, $$1 -> $$0.getUiState().updateDimensions(PresetEditor.fixedBiomeConfigurator($$1))));

    public Screen createEditScreen(CreateWorldScreen var1, WorldCreationContext var2);

    public static WorldCreationContext.DimensionsUpdater flatWorldConfigurator(FlatLevelGeneratorSettings $$0) {
        return ($$1, $$2) -> {
            FlatLevelSource $$3 = new FlatLevelSource($$0);
            return $$2.replaceOverworldGenerator((HolderLookup.Provider)$$1, $$3);
        };
    }

    private static WorldCreationContext.DimensionsUpdater fixedBiomeConfigurator(Holder<Biome> $$0) {
        return ($$1, $$2) -> {
            HolderLookup.RegistryLookup $$3 = $$1.lookupOrThrow(Registries.NOISE_SETTINGS);
            Holder.Reference $$4 = $$3.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
            FixedBiomeSource $$5 = new FixedBiomeSource($$0);
            NoiseBasedChunkGenerator $$6 = new NoiseBasedChunkGenerator((BiomeSource)$$5, $$4);
            return $$2.replaceOverworldGenerator((HolderLookup.Provider)$$1, $$6);
        };
    }
}

