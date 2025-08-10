/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.core;

public interface HolderOwner<T> {
    default public boolean canSerializeIn(HolderOwner<T> $$0) {
        return $$0 == this;
    }
}

