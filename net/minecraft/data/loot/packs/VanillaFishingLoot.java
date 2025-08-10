/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.loot.packs;

import java.util.function.BiConsumer;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.FishingHookPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public record VanillaFishingLoot(HolderLookup.Provider registries) implements LootTableSubProvider
{
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> $$0) {
        HolderGetter $$1 = this.registries.lookupOrThrow(Registries.BIOME);
        $$0.accept(BuiltInLootTables.FISHING, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)NestedLootTable.lootTableReference(BuiltInLootTables.FISHING_JUNK).setWeight(10)).setQuality(-2)).add((LootPoolEntryContainer.Builder<?>)((LootPoolEntryContainer.Builder)((LootPoolSingletonContainer.Builder)NestedLootTable.lootTableReference(BuiltInLootTables.FISHING_TREASURE).setWeight(5)).setQuality(2)).when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(FishingHookPredicate.inOpenWater(true))))).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)NestedLootTable.lootTableReference(BuiltInLootTables.FISHING_FISH).setWeight(85)).setQuality(-1))));
        $$0.accept(BuiltInLootTables.FISHING_FISH, VanillaFishingLoot.fishingFishLootTable());
        $$0.accept(BuiltInLootTables.FISHING_JUNK, LootTable.lootTable().withPool(LootPool.lootPool().add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Blocks.LILY_PAD).setWeight(17)).add((LootPoolEntryContainer.Builder<?>)((Object)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.LEATHER_BOOTS).setWeight(10)).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.0f, 0.9f))))).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.LEATHER).setWeight(10)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.BONE).setWeight(10)).add((LootPoolEntryContainer.Builder<?>)((Object)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.POTION).setWeight(10)).apply(SetPotionFunction.setPotion(Potions.WATER)))).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.STRING).setWeight(5)).add((LootPoolEntryContainer.Builder<?>)((Object)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.FISHING_ROD).setWeight(2)).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.0f, 0.9f))))).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.BOWL).setWeight(10)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.STICK).setWeight(5)).add((LootPoolEntryContainer.Builder<?>)((Object)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.INK_SAC).setWeight(1)).apply(SetItemCountFunction.setCount(ConstantValue.exactly(10.0f))))).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Blocks.TRIPWIRE_HOOK).setWeight(10)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(10)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Blocks.BAMBOO).when(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiomes(HolderSet.a($$1.getOrThrow(Biomes.JUNGLE), $$1.getOrThrow(Biomes.SPARSE_JUNGLE), $$1.getOrThrow(Biomes.BAMBOO_JUNGLE)))))).setWeight(10))));
        $$0.accept(BuiltInLootTables.FISHING_TREASURE, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.NAME_TAG)).add(LootItem.lootTableItem(Items.SADDLE)).add((LootPoolEntryContainer.Builder<?>)((Object)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.BOW).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.0f, 0.25f)))).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, ConstantValue.exactly(30.0f))))).add((LootPoolEntryContainer.Builder<?>)((Object)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.FISHING_ROD).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.0f, 0.25f)))).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, ConstantValue.exactly(30.0f))))).add((LootPoolEntryContainer.Builder<?>)((Object)LootItem.lootTableItem(Items.BOOK).apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, ConstantValue.exactly(30.0f))))).add(LootItem.lootTableItem(Items.NAUTILUS_SHELL))));
    }

    public static LootTable.Builder fishingFishLootTable() {
        return LootTable.lootTable().withPool(LootPool.lootPool().add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.COD).setWeight(60)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.SALMON).setWeight(25)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.TROPICAL_FISH).setWeight(2)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.PUFFERFISH).setWeight(13)));
    }
}

