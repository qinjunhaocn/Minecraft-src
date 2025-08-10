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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class DynamicLoot
extends LootPoolSingletonContainer {
    public static final MapCodec<DynamicLoot> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)ResourceLocation.CODEC.fieldOf("name").forGetter($$0 -> $$0.name)).and(DynamicLoot.singletonFields($$02)).apply((Applicative)$$02, DynamicLoot::new));
    private final ResourceLocation name;

    private DynamicLoot(ResourceLocation $$0, int $$1, int $$2, List<LootItemCondition> $$3, List<LootItemFunction> $$4) {
        super($$1, $$2, $$3, $$4);
        this.name = $$0;
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.DYNAMIC;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> $$0, LootContext $$1) {
        $$1.addDynamicDrops(this.name, $$0);
    }

    public static LootPoolSingletonContainer.Builder<?> dynamicEntry(ResourceLocation $$0) {
        return DynamicLoot.simpleBuilder(($$1, $$2, $$3, $$4) -> new DynamicLoot($$0, $$1, $$2, $$3, $$4));
    }
}

