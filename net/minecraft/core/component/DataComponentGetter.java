/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.core.component;

import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;

public interface DataComponentGetter {
    @Nullable
    public <T> T get(DataComponentType<? extends T> var1);

    default public <T> T getOrDefault(DataComponentType<? extends T> $$0, T $$1) {
        T $$2 = this.get($$0);
        return $$2 != null ? $$2 : $$1;
    }

    @Nullable
    default public <T> TypedDataComponent<T> getTyped(DataComponentType<T> $$0) {
        T $$1 = this.get($$0);
        return $$1 != null ? new TypedDataComponent<T>($$0, $$1) : null;
    }
}

