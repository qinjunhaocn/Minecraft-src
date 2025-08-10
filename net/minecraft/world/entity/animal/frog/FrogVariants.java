/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.animal.frog;

import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.TemperatureVariants;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.entity.variant.BiomeCheck;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.biome.Biome;

public interface FrogVariants {
    public static final ResourceKey<FrogVariant> TEMPERATE = FrogVariants.createKey(TemperatureVariants.TEMPERATE);
    public static final ResourceKey<FrogVariant> WARM = FrogVariants.createKey(TemperatureVariants.WARM);
    public static final ResourceKey<FrogVariant> COLD = FrogVariants.createKey(TemperatureVariants.COLD);

    private static ResourceKey<FrogVariant> createKey(ResourceLocation $$0) {
        return ResourceKey.create(Registries.FROG_VARIANT, $$0);
    }

    public static void bootstrap(BootstrapContext<FrogVariant> $$0) {
        FrogVariants.register($$0, TEMPERATE, "entity/frog/temperate_frog", SpawnPrioritySelectors.fallback(0));
        FrogVariants.register($$0, WARM, "entity/frog/warm_frog", BiomeTags.SPAWNS_WARM_VARIANT_FROGS);
        FrogVariants.register($$0, COLD, "entity/frog/cold_frog", BiomeTags.SPAWNS_COLD_VARIANT_FROGS);
    }

    private static void register(BootstrapContext<FrogVariant> $$0, ResourceKey<FrogVariant> $$1, String $$2, TagKey<Biome> $$3) {
        HolderSet.Named<Biome> $$4 = $$0.lookup(Registries.BIOME).getOrThrow($$3);
        FrogVariants.register($$0, $$1, $$2, SpawnPrioritySelectors.single(new BiomeCheck($$4), 1));
    }

    private static void register(BootstrapContext<FrogVariant> $$0, ResourceKey<FrogVariant> $$1, String $$2, SpawnPrioritySelectors $$3) {
        $$0.register($$1, new FrogVariant(new ClientAsset(ResourceLocation.withDefaultNamespace($$2)), $$3));
    }
}

