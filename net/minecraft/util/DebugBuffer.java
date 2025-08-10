/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class DebugBuffer<T> {
    private final AtomicReferenceArray<T> data;
    private final AtomicInteger index;

    public DebugBuffer(int $$0) {
        this.data = new AtomicReferenceArray($$0);
        this.index = new AtomicInteger(0);
    }

    public void push(T $$0) {
        int $$3;
        int $$2;
        int $$1 = this.data.length();
        while (!this.index.compareAndSet($$2 = this.index.get(), $$3 = ($$2 + 1) % $$1)) {
        }
        this.data.set($$3, $$0);
    }

    public List<T> dump() {
        int $$0 = this.index.get();
        ImmutableList.Builder $$1 = ImmutableList.builder();
        for (int $$2 = 0; $$2 < this.data.length(); ++$$2) {
            int $$3 = Math.floorMod($$0 - $$2, this.data.length());
            T $$4 = this.data.get($$3);
            if ($$4 == null) continue;
            $$1.add($$4);
        }
        return $$1.build();
    }
}

