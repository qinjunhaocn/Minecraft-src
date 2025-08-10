/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.entries;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItem
extends LootPoolSingletonContainer {
    public static final MapCodec<LootItem> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Item.CODEC.fieldOf("name").forGetter($$0 -> $$0.item)).and(LootItem.singletonFields($$02)).apply((Applicative)$$02, LootItem::new));
    private final Holder<Item> item;

    private LootItem(Holder<Item> $$0, int $$1, int $$2, List<LootItemCondition> $$3, List<LootItemFunction> $$4) {
        super($$1, $$2, $$3, $$4);
        this.item = $$0;
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.ITEM;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> $$0, LootContext $$1) {
        $$0.accept(new ItemStack(this.item));
    }

    public static LootPoolSingletonContainer.Builder<?> lootTableItem(ItemLike $$0) {
        return LootItem.simpleBuilder(($$1, $$2, $$3, $$4) -> new LootItem($$0.asItem().builtInRegistryHolder(), $$1, $$2, $$3, $$4));
    }
}

