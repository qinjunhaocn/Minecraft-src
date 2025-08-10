/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.core;

import javax.annotation.Nullable;

public interface IdMap<T>
extends Iterable<T> {
    public static final int DEFAULT = -1;

    public int getId(T var1);

    @Nullable
    public T byId(int var1);

    default public T byIdOrThrow(int $$0) {
        T $$1 = this.byId($$0);
        if ($$1 == null) {
            throw new IllegalArgumentException("No value with id " + $$0);
        }
        return $$1;
    }

    default public int getIdOrThrow(T $$0) {
        int $$1 = this.getId($$0);
        if ($$1 == -1) {
            throw new IllegalArgumentException("Can't find id for '" + String.valueOf($$0) + "' in map " + String.valueOf(this));
        }
        return $$1;
    }

    public int size();
}

