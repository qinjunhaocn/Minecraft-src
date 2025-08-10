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
package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class CompositeLootItemCondition
implements LootItemCondition {
    protected final List<LootItemCondition> terms;
    private final Predicate<LootContext> composedPredicate;

    protected CompositeLootItemCondition(List<LootItemCondition> $$0, Predicate<LootContext> $$1) {
        this.terms = $$0;
        this.composedPredicate = $$1;
    }

    protected static <T extends CompositeLootItemCondition> MapCodec<T> createCodec(Function<List<LootItemCondition>, T> $$0) {
        return RecordCodecBuilder.mapCodec($$1 -> $$1.group((App)LootItemCondition.DIRECT_CODEC.listOf().fieldOf("terms").forGetter($$0 -> $$0.terms)).apply((Applicative)$$1, $$0));
    }

    protected static <T extends CompositeLootItemCondition> Codec<T> createInlineCodec(Function<List<LootItemCondition>, T> $$02) {
        return LootItemCondition.DIRECT_CODEC.listOf().xmap($$02, $$0 -> $$0.terms);
    }

    @Override
    public final boolean test(LootContext $$0) {
        return this.composedPredicate.test($$0);
    }

    @Override
    public void validate(ValidationContext $$0) {
        LootItemCondition.super.validate($$0);
        for (int $$1 = 0; $$1 < this.terms.size(); ++$$1) {
            this.terms.get($$1).validate($$0.forChild(new ProblemReporter.IndexedFieldPathElement("terms", $$1)));
        }
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static abstract class Builder
    implements LootItemCondition.Builder {
        private final ImmutableList.Builder<LootItemCondition> terms = ImmutableList.builder();

        protected Builder(LootItemCondition.Builder ... $$0) {
            for (LootItemCondition.Builder $$1 : $$0) {
                this.terms.add((Object)$$1.build());
            }
        }

        public void addTerm(LootItemCondition.Builder $$0) {
            this.terms.add((Object)$$0.build());
        }

        @Override
        public LootItemCondition build() {
            return this.create((List<LootItemCondition>)((Object)this.terms.build()));
        }

        protected abstract LootItemCondition create(List<LootItemCondition> var1);
    }
}

