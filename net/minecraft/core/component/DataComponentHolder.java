/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.core.component;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;

public interface DataComponentHolder
extends DataComponentGetter {
    public DataComponentMap getComponents();

    @Override
    @Nullable
    default public <T> T get(DataComponentType<? extends T> $$0) {
        return this.getComponents().get($$0);
    }

    default public <T> Stream<T> getAllOfType(Class<? extends T> $$02) {
        return this.getComponents().stream().map(TypedDataComponent::value).filter($$1 -> $$02.isAssignableFrom($$1.getClass())).map($$0 -> $$0);
    }

    @Override
    default public <T> T getOrDefault(DataComponentType<? extends T> $$0, T $$1) {
        return this.getComponents().getOrDefault($$0, $$1);
    }

    default public boolean has(DataComponentType<?> $$0) {
        return this.getComponents().has($$0);
    }
}

