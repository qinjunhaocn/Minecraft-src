/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.codec;

@FunctionalInterface
public interface StreamEncoder<O, T> {
    public void encode(O var1, T var2);
}

