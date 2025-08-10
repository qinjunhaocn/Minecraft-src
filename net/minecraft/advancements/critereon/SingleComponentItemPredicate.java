/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.advancements.critereon;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.predicates.DataComponentPredicate;

public interface SingleComponentItemPredicate<T>
extends DataComponentPredicate {
    @Override
    default public boolean matches(DataComponentGetter $$0) {
        T $$1 = $$0.get(this.componentType());
        return $$1 != null && this.matches($$1);
    }

    public DataComponentType<T> componentType();

    public boolean matches(T var1);
}

