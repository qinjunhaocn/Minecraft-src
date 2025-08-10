/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.loot.packs;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.packs.VanillaArchaeologyLoot;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.data.loot.packs.VanillaChestLoot;
import net.minecraft.data.loot.packs.VanillaEntityLoot;
import net.minecraft.data.loot.packs.VanillaEquipmentLoot;
import net.minecraft.data.loot.packs.VanillaFishingLoot;
import net.minecraft.data.loot.packs.VanillaGiftLoot;
import net.minecraft.data.loot.packs.VanillaPiglinBarterLoot;
import net.minecraft.data.loot.packs.VanillaShearingLoot;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class VanillaLootTableProvider {
    public static LootTableProvider create(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1) {
        return new LootTableProvider($$0, BuiltInLootTables.all(), List.of((Object)((Object)new LootTableProvider.SubProviderEntry(VanillaFishingLoot::new, LootContextParamSets.FISHING)), (Object)((Object)new LootTableProvider.SubProviderEntry(VanillaChestLoot::new, LootContextParamSets.CHEST)), (Object)((Object)new LootTableProvider.SubProviderEntry(VanillaEntityLoot::new, LootContextParamSets.ENTITY)), (Object)((Object)new LootTableProvider.SubProviderEntry(VanillaEquipmentLoot::new, LootContextParamSets.EQUIPMENT)), (Object)((Object)new LootTableProvider.SubProviderEntry(VanillaBlockLoot::new, LootContextParamSets.BLOCK)), (Object)((Object)new LootTableProvider.SubProviderEntry(VanillaPiglinBarterLoot::new, LootContextParamSets.PIGLIN_BARTER)), (Object)((Object)new LootTableProvider.SubProviderEntry(VanillaGiftLoot::new, LootContextParamSets.GIFT)), (Object)((Object)new LootTableProvider.SubProviderEntry(VanillaArchaeologyLoot::new, LootContextParamSets.ARCHAEOLOGY)), (Object)((Object)new LootTableProvider.SubProviderEntry(VanillaShearingLoot::new, LootContextParamSets.SHEARING))), $$1);
    }
}

