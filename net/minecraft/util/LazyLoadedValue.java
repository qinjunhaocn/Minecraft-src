/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;

@Deprecated
public class LazyLoadedValue<T> {
    private final Supplier<T> factory = Suppliers.memoize($$0::get);

    public LazyLoadedValue(Supplier<T> $$0) {
    }

    public T get() {
        return this.factory.get();
    }
}

