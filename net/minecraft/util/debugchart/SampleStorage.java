/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.debugchart;

public interface SampleStorage {
    public int capacity();

    public int size();

    public long get(int var1);

    public long get(int var1, int var2);

    public void reset();
}

