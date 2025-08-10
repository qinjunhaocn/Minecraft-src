/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.data.models.blockstates;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.block.model.multipart.KeyValueCondition;
import net.minecraft.world.level.block.state.properties.Property;

public class ConditionBuilder {
    private final ImmutableMap.Builder<String, KeyValueCondition.Terms> terms = ImmutableMap.builder();

    private <T extends Comparable<T>> void putValue(Property<T> $$0, KeyValueCondition.Terms $$1) {
        this.terms.put($$0.getName(), $$1);
    }

    public final <T extends Comparable<T>> ConditionBuilder term(Property<T> $$0, T $$1) {
        this.putValue($$0, new KeyValueCondition.Terms(List.of((Object)((Object)new KeyValueCondition.Term($$0.getName($$1), false)))));
        return this;
    }

    @SafeVarargs
    public final <T extends Comparable<T>> ConditionBuilder a(Property<T> $$02, T $$1, T ... $$2) {
        List $$3 = Stream.concat(Stream.of($$1), Stream.of($$2)).map($$02::getName).sorted().distinct().map($$0 -> new KeyValueCondition.Term((String)$$0, false)).toList();
        this.putValue($$02, new KeyValueCondition.Terms($$3));
        return this;
    }

    public final <T extends Comparable<T>> ConditionBuilder negatedTerm(Property<T> $$0, T $$1) {
        this.putValue($$0, new KeyValueCondition.Terms(List.of((Object)((Object)new KeyValueCondition.Term($$0.getName($$1), true)))));
        return this;
    }

    public Condition build() {
        return new KeyValueCondition(this.terms.buildOrThrow());
    }
}

