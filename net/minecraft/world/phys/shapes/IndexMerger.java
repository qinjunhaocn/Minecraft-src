/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface IndexMerger {
    public DoubleList getList();

    public boolean forMergedIndexes(IndexConsumer var1);

    public int size();

    public static interface IndexConsumer {
        public boolean merge(int var1, int var2, int var3);
    }
}

