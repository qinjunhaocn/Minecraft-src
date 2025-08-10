/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.entries;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class NestedLootTable
extends LootPoolSingletonContainer {
    public static final MapCodec<NestedLootTable> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.either(LootTable.KEY_CODEC, LootTable.DIRECT_CODEC).fieldOf("value").forGetter($$0 -> $$0.contents)).and(NestedLootTable.singletonFields($$02)).apply((Applicative)$$02, NestedLootTable::new));
    public static final ProblemReporter.PathElement INLINE_LOOT_TABLE_PATH_ELEMENT = new ProblemReporter.PathElement(){

        @Override
        public String get() {
            return "->{inline}";
        }
    };
    private final Either<ResourceKey<LootTable>, LootTable> contents;

    private NestedLootTable(Either<ResourceKey<LootTable>, LootTable> $$0, int $$1, int $$2, List<LootItemCondition> $$3, List<LootItemFunction> $$4) {
        super($$1, $$2, $$3, $$4);
        this.contents = $$0;
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.LOOT_TABLE;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> $$02, LootContext $$12) {
        ((LootTable)this.contents.map($$1 -> $$12.getResolver().get($$1).map(Holder::value).orElse(LootTable.EMPTY), $$0 -> $$0)).getRandomItemsRaw($$12, $$02);
    }

    @Override
    public void validate(ValidationContext $$0) {
        Optional $$12 = this.contents.left();
        if ($$12.isPresent()) {
            ResourceKey $$2 = (ResourceKey)$$12.get();
            if (!$$0.allowsReferences()) {
                $$0.reportProblem(new ValidationContext.ReferenceNotAllowedProblem($$2));
                return;
            }
            if ($$0.hasVisitedElement($$2)) {
                $$0.reportProblem(new ValidationContext.RecursiveReferenceProblem($$2));
                return;
            }
        }
        super.validate($$0);
        this.contents.ifLeft($$1 -> $$0.resolver().get($$1).ifPresentOrElse($$2 -> ((LootTable)$$2.value()).validate($$0.enterElement(new ProblemReporter.ElementReferencePathElement((ResourceKey<?>)$$1), (ResourceKey<?>)$$1)), () -> $$0.reportProblem(new ValidationContext.MissingReferenceProblem((ResourceKey<?>)$$1)))).ifRight($$1 -> $$1.validate($$0.forChild(INLINE_LOOT_TABLE_PATH_ELEMENT)));
    }

    public static LootPoolSingletonContainer.Builder<?> lootTableReference(ResourceKey<LootTable> $$0) {
        return NestedLootTable.simpleBuilder(($$1, $$2, $$3, $$4) -> new NestedLootTable((Either<ResourceKey<LootTable>, LootTable>)Either.left((Object)$$0), $$1, $$2, $$3, $$4));
    }

    public static LootPoolSingletonContainer.Builder<?> inlineLootTable(LootTable $$0) {
        return NestedLootTable.simpleBuilder(($$1, $$2, $$3, $$4) -> new NestedLootTable((Either<ResourceKey<LootTable>, LootTable>)Either.right((Object)$$0), $$1, $$2, $$3, $$4));
    }
}

