/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.equipment.trim;

import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ProvidesTrimMaterial;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public class TrimMaterials {
    public static final ResourceKey<TrimMaterial> QUARTZ = TrimMaterials.registryKey("quartz");
    public static final ResourceKey<TrimMaterial> IRON = TrimMaterials.registryKey("iron");
    public static final ResourceKey<TrimMaterial> NETHERITE = TrimMaterials.registryKey("netherite");
    public static final ResourceKey<TrimMaterial> REDSTONE = TrimMaterials.registryKey("redstone");
    public static final ResourceKey<TrimMaterial> COPPER = TrimMaterials.registryKey("copper");
    public static final ResourceKey<TrimMaterial> GOLD = TrimMaterials.registryKey("gold");
    public static final ResourceKey<TrimMaterial> EMERALD = TrimMaterials.registryKey("emerald");
    public static final ResourceKey<TrimMaterial> DIAMOND = TrimMaterials.registryKey("diamond");
    public static final ResourceKey<TrimMaterial> LAPIS = TrimMaterials.registryKey("lapis");
    public static final ResourceKey<TrimMaterial> AMETHYST = TrimMaterials.registryKey("amethyst");
    public static final ResourceKey<TrimMaterial> RESIN = TrimMaterials.registryKey("resin");

    public static void bootstrap(BootstrapContext<TrimMaterial> $$0) {
        TrimMaterials.register($$0, QUARTZ, Style.EMPTY.withColor(14931140), MaterialAssetGroup.QUARTZ);
        TrimMaterials.register($$0, IRON, Style.EMPTY.withColor(0xECECEC), MaterialAssetGroup.IRON);
        TrimMaterials.register($$0, NETHERITE, Style.EMPTY.withColor(6445145), MaterialAssetGroup.NETHERITE);
        TrimMaterials.register($$0, REDSTONE, Style.EMPTY.withColor(9901575), MaterialAssetGroup.REDSTONE);
        TrimMaterials.register($$0, COPPER, Style.EMPTY.withColor(11823181), MaterialAssetGroup.COPPER);
        TrimMaterials.register($$0, GOLD, Style.EMPTY.withColor(14594349), MaterialAssetGroup.GOLD);
        TrimMaterials.register($$0, EMERALD, Style.EMPTY.withColor(1155126), MaterialAssetGroup.EMERALD);
        TrimMaterials.register($$0, DIAMOND, Style.EMPTY.withColor(7269586), MaterialAssetGroup.DIAMOND);
        TrimMaterials.register($$0, LAPIS, Style.EMPTY.withColor(4288151), MaterialAssetGroup.LAPIS);
        TrimMaterials.register($$0, AMETHYST, Style.EMPTY.withColor(10116294), MaterialAssetGroup.AMETHYST);
        TrimMaterials.register($$0, RESIN, Style.EMPTY.withColor(16545810), MaterialAssetGroup.RESIN);
    }

    public static Optional<Holder<TrimMaterial>> getFromIngredient(HolderLookup.Provider $$0, ItemStack $$1) {
        ProvidesTrimMaterial $$2 = $$1.get(DataComponents.PROVIDES_TRIM_MATERIAL);
        return $$2 != null ? $$2.unwrap($$0) : Optional.empty();
    }

    private static void register(BootstrapContext<TrimMaterial> $$0, ResourceKey<TrimMaterial> $$1, Style $$2, MaterialAssetGroup $$3) {
        MutableComponent $$4 = Component.translatable(Util.makeDescriptionId("trim_material", $$1.location())).withStyle($$2);
        $$0.register($$1, new TrimMaterial($$3, $$4));
    }

    private static ResourceKey<TrimMaterial> registryKey(String $$0) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, ResourceLocation.withDefaultNamespace($$0));
    }
}

