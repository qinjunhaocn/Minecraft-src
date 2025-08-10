/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.worldselection;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.minecraft.client.gui.screens.worldselection.InitialWorldCreationOptions;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;

public record WorldCreationContext(WorldOptions options, Registry<LevelStem> datapackDimensions, WorldDimensions selectedDimensions, LayeredRegistryAccess<RegistryLayer> worldgenRegistries, ReloadableServerResources dataPackResources, WorldDataConfiguration dataConfiguration, InitialWorldCreationOptions initialWorldCreationOptions) {
    public WorldCreationContext(WorldGenSettings $$0, LayeredRegistryAccess<RegistryLayer> $$1, ReloadableServerResources $$2, WorldDataConfiguration $$3) {
        this($$0.options(), $$0.dimensions(), $$1, $$2, $$3, new InitialWorldCreationOptions(WorldCreationUiState.SelectedGameMode.SURVIVAL, Set.of(), null));
    }

    public WorldCreationContext(WorldOptions $$0, WorldDimensions $$1, LayeredRegistryAccess<RegistryLayer> $$2, ReloadableServerResources $$3, WorldDataConfiguration $$4, InitialWorldCreationOptions $$5) {
        this($$0, (Registry<LevelStem>)$$2.getLayer(RegistryLayer.DIMENSIONS).lookupOrThrow(Registries.LEVEL_STEM), $$1, $$2.a(RegistryLayer.DIMENSIONS, new RegistryAccess.Frozen[0]), $$3, $$4, $$5);
    }

    public WorldCreationContext withSettings(WorldOptions $$0, WorldDimensions $$1) {
        return new WorldCreationContext($$0, this.datapackDimensions, $$1, this.worldgenRegistries, this.dataPackResources, this.dataConfiguration, this.initialWorldCreationOptions);
    }

    public WorldCreationContext withOptions(OptionsModifier $$0) {
        return new WorldCreationContext((WorldOptions)$$0.apply(this.options), this.datapackDimensions, this.selectedDimensions, this.worldgenRegistries, this.dataPackResources, this.dataConfiguration, this.initialWorldCreationOptions);
    }

    public WorldCreationContext withDimensions(DimensionsUpdater $$0) {
        return new WorldCreationContext(this.options, this.datapackDimensions, (WorldDimensions)((Object)$$0.apply(this.worldgenLoadContext(), this.selectedDimensions)), this.worldgenRegistries, this.dataPackResources, this.dataConfiguration, this.initialWorldCreationOptions);
    }

    public RegistryAccess.Frozen worldgenLoadContext() {
        return this.worldgenRegistries.compositeAccess();
    }

    public void validate() {
        for (LevelStem $$0 : this.datapackDimensions()) {
            $$0.generator().validate();
        }
    }

    public static interface OptionsModifier
    extends UnaryOperator<WorldOptions> {
    }

    @FunctionalInterface
    public static interface DimensionsUpdater
    extends BiFunction<RegistryAccess.Frozen, WorldDimensions, WorldDimensions> {
    }
}

