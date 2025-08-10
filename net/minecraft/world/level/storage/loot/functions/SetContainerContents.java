/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulator;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulators;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerContents
extends LootItemConditionalFunction {
    public static final MapCodec<SetContainerContents> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetContainerContents.commonFields($$02).and($$02.group((App)ContainerComponentManipulators.CODEC.fieldOf("component").forGetter($$0 -> $$0.component), (App)LootPoolEntries.CODEC.listOf().fieldOf("entries").forGetter($$0 -> $$0.entries))).apply((Applicative)$$02, SetContainerContents::new));
    private final ContainerComponentManipulator<?> component;
    private final List<LootPoolEntryContainer> entries;

    SetContainerContents(List<LootItemCondition> $$0, ContainerComponentManipulator<?> $$1, List<LootPoolEntryContainer> $$2) {
        super($$0);
        this.component = $$1;
        this.entries = List.copyOf($$2);
    }

    public LootItemFunctionType<SetContainerContents> getType() {
        return LootItemFunctions.SET_CONTENTS;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        if ($$0.isEmpty()) {
            return $$0;
        }
        Stream.Builder $$2 = Stream.builder();
        this.entries.forEach($$22 -> $$22.expand($$1, $$2 -> $$2.createItemStack(LootTable.createStackSplitter($$1.getLevel(), $$2::add), $$1)));
        this.component.setContents($$0, $$2.build());
        return $$0;
    }

    @Override
    public void validate(ValidationContext $$0) {
        super.validate($$0);
        for (int $$1 = 0; $$1 < this.entries.size(); ++$$1) {
            this.entries.get($$1).validate($$0.forChild(new ProblemReporter.IndexedFieldPathElement("entries", $$1)));
        }
    }

    public static Builder setContents(ContainerComponentManipulator<?> $$0) {
        return new Builder($$0);
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();
        private final ContainerComponentManipulator<?> component;

        public Builder(ContainerComponentManipulator<?> $$0) {
            this.component = $$0;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder withEntry(LootPoolEntryContainer.Builder<?> $$0) {
            this.entries.add((Object)$$0.build());
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetContainerContents(this.getConditions(), this.component, (List<LootPoolEntryContainer>)((Object)this.entries.build()));
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }
}

