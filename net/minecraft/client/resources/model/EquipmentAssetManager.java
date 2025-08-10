/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.model;

import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class EquipmentAssetManager
extends SimpleJsonResourceReloadListener<EquipmentClientInfo> {
    public static final EquipmentClientInfo MISSING = new EquipmentClientInfo(Map.of());
    private static final FileToIdConverter ASSET_LISTER = FileToIdConverter.json("equipment");
    private Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> equipmentAssets = Map.of();

    public EquipmentAssetManager() {
        super(EquipmentClientInfo.CODEC, ASSET_LISTER);
    }

    @Override
    protected void apply(Map<ResourceLocation, EquipmentClientInfo> $$02, ResourceManager $$1, ProfilerFiller $$2) {
        this.equipmentAssets = (Map)$$02.entrySet().stream().collect(Collectors.toUnmodifiableMap($$0 -> ResourceKey.create(EquipmentAssets.ROOT_ID, (ResourceLocation)$$0.getKey()), Map.Entry::getValue));
    }

    public EquipmentClientInfo get(ResourceKey<EquipmentAsset> $$0) {
        return this.equipmentAssets.getOrDefault($$0, MISSING);
    }
}

