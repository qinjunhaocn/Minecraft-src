/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArraySet
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntSet
 */
package net.minecraft.world.level.chunk.storage;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.BitSet;

public class RegionBitmap {
    private final BitSet used = new BitSet();

    public void force(int $$0, int $$1) {
        this.used.set($$0, $$0 + $$1);
    }

    public void free(int $$0, int $$1) {
        this.used.clear($$0, $$0 + $$1);
    }

    public int allocate(int $$0) {
        int $$1 = 0;
        while (true) {
            int $$2;
            int $$3;
            if (($$3 = this.used.nextSetBit($$2 = this.used.nextClearBit($$1))) == -1 || $$3 - $$2 >= $$0) {
                this.force($$2, $$0);
                return $$2;
            }
            $$1 = $$3;
        }
    }

    @VisibleForTesting
    public IntSet getUsed() {
        return (IntSet)this.used.stream().collect(IntArraySet::new, IntCollection::add, IntCollection::addAll);
    }
}

