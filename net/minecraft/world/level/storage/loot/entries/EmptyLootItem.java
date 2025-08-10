/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.entries;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EmptyLootItem
extends LootPoolSingletonContainer {
    public static final MapCodec<EmptyLootItem> CODEC = RecordCodecBuilder.mapCodec($$0 -> EmptyLootItem.singletonFields($$0).apply((Applicative)$$0, EmptyLootItem::new));

    private EmptyLootItem(int $$0, int $$1, List<LootItemCondition> $$2, List<LootItemFunction> $$3) {
        super($$0, $$1, $$2, $$3);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.EMPTY;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> $$0, LootContext $$1) {
    }

    public static LootPoolSingletonContainer.Builder<?> emptyItem() {
        return EmptyLootItem.simpleBuilder(EmptyLootItem::new);
    }
}

