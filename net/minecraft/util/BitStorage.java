/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util;

import java.util.function.IntConsumer;

public interface BitStorage {
    public int getAndSet(int var1, int var2);

    public void set(int var1, int var2);

    public int get(int var1);

    public long[] a();

    public int getSize();

    public int getBits();

    public void getAll(IntConsumer var1);

    public void a(int[] var1);

    public BitStorage copy();
}

