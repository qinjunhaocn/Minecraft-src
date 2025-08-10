/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.data.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class EquipmentAssetProvider
implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public EquipmentAssetProvider(PackOutput $$0) {
        this.pathProvider = $$0.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
    }

    private static void bootstrap(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> $$0) {
        $$0.accept(EquipmentAssets.LEATHER, EquipmentClientInfo.builder().addHumanoidLayers(ResourceLocation.withDefaultNamespace("leather"), true).addHumanoidLayers(ResourceLocation.withDefaultNamespace("leather_overlay"), false).a(EquipmentClientInfo.LayerType.HORSE_BODY, EquipmentClientInfo.Layer.leatherDyeable(ResourceLocation.withDefaultNamespace("leather"), true)).build());
        $$0.accept(EquipmentAssets.CHAINMAIL, EquipmentAssetProvider.onlyHumanoid("chainmail"));
        $$0.accept(EquipmentAssets.IRON, EquipmentAssetProvider.humanoidAndHorse("iron"));
        $$0.accept(EquipmentAssets.GOLD, EquipmentAssetProvider.humanoidAndHorse("gold"));
        $$0.accept(EquipmentAssets.DIAMOND, EquipmentAssetProvider.humanoidAndHorse("diamond"));
        $$0.accept(EquipmentAssets.TURTLE_SCUTE, EquipmentClientInfo.builder().addMainHumanoidLayer(ResourceLocation.withDefaultNamespace("turtle_scute"), false).build());
        $$0.accept(EquipmentAssets.NETHERITE, EquipmentAssetProvider.onlyHumanoid("netherite"));
        $$0.accept(EquipmentAssets.ARMADILLO_SCUTE, EquipmentClientInfo.builder().a(EquipmentClientInfo.LayerType.WOLF_BODY, EquipmentClientInfo.Layer.onlyIfDyed(ResourceLocation.withDefaultNamespace("armadillo_scute"), false)).a(EquipmentClientInfo.LayerType.WOLF_BODY, EquipmentClientInfo.Layer.onlyIfDyed(ResourceLocation.withDefaultNamespace("armadillo_scute_overlay"), true)).build());
        $$0.accept(EquipmentAssets.ELYTRA, EquipmentClientInfo.builder().a(EquipmentClientInfo.LayerType.WINGS, new EquipmentClientInfo.Layer(ResourceLocation.withDefaultNamespace("elytra"), Optional.empty(), true)).build());
        EquipmentClientInfo.Layer $$1 = new EquipmentClientInfo.Layer(ResourceLocation.withDefaultNamespace("saddle"));
        $$0.accept(EquipmentAssets.SADDLE, EquipmentClientInfo.builder().a(EquipmentClientInfo.LayerType.PIG_SADDLE, $$1).a(EquipmentClientInfo.LayerType.STRIDER_SADDLE, $$1).a(EquipmentClientInfo.LayerType.CAMEL_SADDLE, $$1).a(EquipmentClientInfo.LayerType.HORSE_SADDLE, $$1).a(EquipmentClientInfo.LayerType.DONKEY_SADDLE, $$1).a(EquipmentClientInfo.LayerType.MULE_SADDLE, $$1).a(EquipmentClientInfo.LayerType.SKELETON_HORSE_SADDLE, $$1).a(EquipmentClientInfo.LayerType.ZOMBIE_HORSE_SADDLE, $$1).build());
        for (Map.Entry<DyeColor, ResourceKey<EquipmentAsset>> $$2 : EquipmentAssets.HARNESSES.entrySet()) {
            DyeColor $$3 = $$2.getKey();
            ResourceKey<EquipmentAsset> $$4 = $$2.getValue();
            $$0.accept($$4, EquipmentClientInfo.builder().a(EquipmentClientInfo.LayerType.HAPPY_GHAST_BODY, EquipmentClientInfo.Layer.onlyIfDyed(ResourceLocation.withDefaultNamespace($$3.getSerializedName() + "_harness"), false)).build());
        }
        for (Map.Entry<DyeColor, ResourceKey<EquipmentAsset>> $$5 : EquipmentAssets.CARPETS.entrySet()) {
            DyeColor $$6 = $$5.getKey();
            ResourceKey<EquipmentAsset> $$7 = $$5.getValue();
            $$0.accept($$7, EquipmentClientInfo.builder().a(EquipmentClientInfo.LayerType.LLAMA_BODY, new EquipmentClientInfo.Layer(ResourceLocation.withDefaultNamespace($$6.getSerializedName()))).build());
        }
        $$0.accept(EquipmentAssets.TRADER_LLAMA, EquipmentClientInfo.builder().a(EquipmentClientInfo.LayerType.LLAMA_BODY, new EquipmentClientInfo.Layer(ResourceLocation.withDefaultNamespace("trader_llama"))).build());
    }

    private static EquipmentClientInfo onlyHumanoid(String $$0) {
        return EquipmentClientInfo.builder().addHumanoidLayers(ResourceLocation.withDefaultNamespace($$0)).build();
    }

    private static EquipmentClientInfo humanoidAndHorse(String $$0) {
        return EquipmentClientInfo.builder().addHumanoidLayers(ResourceLocation.withDefaultNamespace($$0)).a(EquipmentClientInfo.LayerType.HORSE_BODY, EquipmentClientInfo.Layer.leatherDyeable(ResourceLocation.withDefaultNamespace($$0), false)).build();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$0) {
        HashMap $$12 = new HashMap();
        EquipmentAssetProvider.bootstrap(($$1, $$2) -> {
            if ($$12.putIfAbsent($$1, $$2) != null) {
                throw new IllegalStateException("Tried to register equipment asset twice for id: " + String.valueOf($$1));
            }
        });
        return DataProvider.saveAll($$0, EquipmentClientInfo.CODEC, this.pathProvider::json, $$12);
    }

    @Override
    public String getName() {
        return "Equipment Asset Definitions";
    }
}

