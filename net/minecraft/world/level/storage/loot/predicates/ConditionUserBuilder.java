/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.storage.loot.predicates;

import java.util.function.Function;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public interface ConditionUserBuilder<T extends ConditionUserBuilder<T>> {
    public T when(LootItemCondition.Builder var1);

    default public <E> T when(Iterable<E> $$0, Function<E, LootItemCondition.Builder> $$1) {
        T $$2 = this.unwrap();
        for (E $$3 : $$0) {
            $$2 = $$2.when($$1.apply($$3));
        }
        return $$2;
    }

    public T unwrap();
}

