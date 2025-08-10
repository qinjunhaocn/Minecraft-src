/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.multiplayer;

import java.util.function.Function;
import javax.annotation.Nullable;

public class CacheSlot<C extends Cleaner<C>, D> {
    private final Function<C, D> operation;
    @Nullable
    private C context;
    @Nullable
    private D value;

    public CacheSlot(Function<C, D> $$0) {
        this.operation = $$0;
    }

    public D compute(C $$0) {
        if ($$0 == this.context && this.value != null) {
            return this.value;
        }
        D $$1 = this.operation.apply($$0);
        this.value = $$1;
        this.context = $$0;
        $$0.registerForCleaning(this);
        return $$1;
    }

    public void clear() {
        this.value = null;
        this.context = null;
    }

    @FunctionalInterface
    public static interface Cleaner<C extends Cleaner<C>> {
        public void registerForCleaning(CacheSlot<C, ?> var1);
    }
}

