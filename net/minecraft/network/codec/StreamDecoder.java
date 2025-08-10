/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.codec;

@FunctionalInterface
public interface StreamDecoder<I, T> {
    public T decode(I var1);
}

