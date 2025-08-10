/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.codec;

@FunctionalInterface
public interface StreamMemberEncoder<O, T> {
    public void encode(T var1, O var2);
}

