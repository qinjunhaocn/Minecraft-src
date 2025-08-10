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
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class CompositeEntryBase
extends LootPoolEntryContainer {
    public static final ProblemReporter.Problem NO_CHILDREN_PROBLEM = new ProblemReporter.Problem(){

        @Override
        public String description() {
            return "Empty children list";
        }
    };
    protected final List<LootPoolEntryContainer> children;
    private final ComposableEntryContainer composedChildren;

    protected CompositeEntryBase(List<LootPoolEntryContainer> $$0, List<LootItemCondition> $$1) {
        super($$1);
        this.children = $$0;
        this.composedChildren = this.compose($$0);
    }

    @Override
    public void validate(ValidationContext $$0) {
        super.validate($$0);
        if (this.children.isEmpty()) {
            $$0.reportProblem(NO_CHILDREN_PROBLEM);
        }
        for (int $$1 = 0; $$1 < this.children.size(); ++$$1) {
            this.children.get($$1).validate($$0.forChild(new ProblemReporter.IndexedFieldPathElement("children", $$1)));
        }
    }

    protected abstract ComposableEntryContainer compose(List<? extends ComposableEntryContainer> var1);

    @Override
    public final boolean expand(LootContext $$0, Consumer<LootPoolEntry> $$1) {
        if (!this.canRun($$0)) {
            return false;
        }
        return this.composedChildren.expand($$0, $$1);
    }

    public static <T extends CompositeEntryBase> MapCodec<T> createCodec(CompositeEntryConstructor<T> $$0) {
        return RecordCodecBuilder.mapCodec($$1 -> $$1.group((App)LootPoolEntries.CODEC.listOf().optionalFieldOf("children", (Object)List.of()).forGetter($$0 -> $$0.children)).and(CompositeEntryBase.commonFields($$1).t1()).apply((Applicative)$$1, $$0::create));
    }

    @FunctionalInterface
    public static interface CompositeEntryConstructor<T extends CompositeEntryBase> {
        public T create(List<LootPoolEntryContainer> var1, List<LootItemCondition> var2);
    }
}

