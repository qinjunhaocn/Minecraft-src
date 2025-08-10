/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.entries;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class TagEntry
extends LootPoolSingletonContainer {
    public static final MapCodec<TagEntry> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)TagKey.codec(Registries.ITEM).fieldOf("name").forGetter($$0 -> $$0.tag), (App)Codec.BOOL.fieldOf("expand").forGetter($$0 -> $$0.expand)).and(TagEntry.singletonFields($$02)).apply((Applicative)$$02, TagEntry::new));
    private final TagKey<Item> tag;
    private final boolean expand;

    private TagEntry(TagKey<Item> $$0, boolean $$1, int $$2, int $$3, List<LootItemCondition> $$4, List<LootItemFunction> $$5) {
        super($$2, $$3, $$4, $$5);
        this.tag = $$0;
        this.expand = $$1;
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.TAG;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> $$0, LootContext $$12) {
        BuiltInRegistries.ITEM.getTagOrEmpty(this.tag).forEach($$1 -> $$0.accept(new ItemStack((Holder<Item>)$$1)));
    }

    private boolean expandTag(LootContext $$0, Consumer<LootPoolEntry> $$1) {
        if (this.canRun($$0)) {
            for (final Holder<Item> $$2 : BuiltInRegistries.ITEM.getTagOrEmpty(this.tag)) {
                $$1.accept(new LootPoolSingletonContainer.EntryBase(this){

                    @Override
                    public void createItemStack(Consumer<ItemStack> $$0, LootContext $$1) {
                        $$0.accept(new ItemStack($$2));
                    }
                });
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean expand(LootContext $$0, Consumer<LootPoolEntry> $$1) {
        if (this.expand) {
            return this.expandTag($$0, $$1);
        }
        return super.expand($$0, $$1);
    }

    public static LootPoolSingletonContainer.Builder<?> tagContents(TagKey<Item> $$0) {
        return TagEntry.simpleBuilder(($$1, $$2, $$3, $$4) -> new TagEntry($$0, false, $$1, $$2, $$3, $$4));
    }

    public static LootPoolSingletonContainer.Builder<?> expandTag(TagKey<Item> $$0) {
        return TagEntry.simpleBuilder(($$1, $$2, $$3, $$4) -> new TagEntry($$0, true, $$1, $$2, $$3, $$4));
    }
}

