/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.storage.loot.functions;

import java.util.Arrays;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public interface FunctionUserBuilder<T extends FunctionUserBuilder<T>> {
    public T apply(LootItemFunction.Builder var1);

    default public <E> T apply(Iterable<E> $$0, Function<E, LootItemFunction.Builder> $$1) {
        T $$2 = this.unwrap();
        for (E $$3 : $$0) {
            $$2 = $$2.apply($$1.apply($$3));
        }
        return $$2;
    }

    default public <E> T a(E[] $$0, Function<E, LootItemFunction.Builder> $$1) {
        return this.apply(Arrays.asList($$0), $$1);
    }

    public T unwrap();
}

