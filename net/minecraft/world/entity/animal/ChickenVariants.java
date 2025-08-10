/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.animal;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.ChickenVariant;
import net.minecraft.world.entity.animal.TemperatureVariants;
import net.minecraft.world.entity.variant.BiomeCheck;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.biome.Biome;

public class ChickenVariants {
    public static final ResourceKey<ChickenVariant> TEMPERATE = ChickenVariants.createKey(TemperatureVariants.TEMPERATE);
    public static final ResourceKey<ChickenVariant> WARM = ChickenVariants.createKey(TemperatureVariants.WARM);
    public static final ResourceKey<ChickenVariant> COLD = ChickenVariants.createKey(TemperatureVariants.COLD);
    public static final ResourceKey<ChickenVariant> DEFAULT = TEMPERATE;

    private static ResourceKey<ChickenVariant> createKey(ResourceLocation $$0) {
        return ResourceKey.create(Registries.CHICKEN_VARIANT, $$0);
    }

    public static void bootstrap(BootstrapContext<ChickenVariant> $$0) {
        ChickenVariants.register($$0, TEMPERATE, ChickenVariant.ModelType.NORMAL, "temperate_chicken", SpawnPrioritySelectors.fallback(0));
        ChickenVariants.register($$0, WARM, ChickenVariant.ModelType.NORMAL, "warm_chicken", BiomeTags.SPAWNS_WARM_VARIANT_FARM_ANIMALS);
        ChickenVariants.register($$0, COLD, ChickenVariant.ModelType.COLD, "cold_chicken", BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS);
    }

    private static void register(BootstrapContext<ChickenVariant> $$0, ResourceKey<ChickenVariant> $$1, ChickenVariant.ModelType $$2, String $$3, TagKey<Biome> $$4) {
        HolderSet.Named<Biome> $$5 = $$0.lookup(Registries.BIOME).getOrThrow($$4);
        ChickenVariants.register($$0, $$1, $$2, $$3, SpawnPrioritySelectors.single(new BiomeCheck($$5), 1));
    }

    private static void register(BootstrapContext<ChickenVariant> $$0, ResourceKey<ChickenVariant> $$1, ChickenVariant.ModelType $$2, String $$3, SpawnPrioritySelectors $$4) {
        ResourceLocation $$5 = ResourceLocation.withDefaultNamespace("entity/chicken/" + $$3);
        $$0.register($$1, new ChickenVariant(new ModelAndTexture<ChickenVariant.ModelType>($$2, $$5), $$4));
    }
}

