/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.resource;

public interface ResourceDescriptor<T> {
    public T allocate();

    default public void prepare(T $$0) {
    }

    public void free(T var1);

    default public boolean canUsePhysicalResource(ResourceDescriptor<?> $$0) {
        return this.equals($$0);
    }
}

