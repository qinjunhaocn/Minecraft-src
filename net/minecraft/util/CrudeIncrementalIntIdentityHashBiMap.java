/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.IdMap;
import net.minecraft.util.Mth;

public class CrudeIncrementalIntIdentityHashBiMap<K>
implements IdMap<K> {
    private static final int NOT_FOUND = -1;
    private static final Object EMPTY_SLOT = null;
    private static final float LOADFACTOR = 0.8f;
    private K[] keys;
    private int[] values;
    private K[] byId;
    private int nextId;
    private int size;

    private CrudeIncrementalIntIdentityHashBiMap(int $$0) {
        this.keys = new Object[$$0];
        this.values = new int[$$0];
        this.byId = new Object[$$0];
    }

    private CrudeIncrementalIntIdentityHashBiMap(K[] $$0, int[] $$1, K[] $$2, int $$3, int $$4) {
        this.keys = $$0;
        this.values = $$1;
        this.byId = $$2;
        this.nextId = $$3;
        this.size = $$4;
    }

    public static <A> CrudeIncrementalIntIdentityHashBiMap<A> create(int $$0) {
        return new CrudeIncrementalIntIdentityHashBiMap((int)((float)$$0 / 0.8f));
    }

    @Override
    public int getId(@Nullable K $$0) {
        return this.getValue(this.indexOf($$0, this.hash($$0)));
    }

    @Override
    @Nullable
    public K byId(int $$0) {
        if ($$0 < 0 || $$0 >= this.byId.length) {
            return null;
        }
        return this.byId[$$0];
    }

    private int getValue(int $$0) {
        if ($$0 == -1) {
            return -1;
        }
        return this.values[$$0];
    }

    public boolean contains(K $$0) {
        return this.getId($$0) != -1;
    }

    public boolean contains(int $$0) {
        return this.byId($$0) != null;
    }

    public int add(K $$0) {
        int $$1 = this.nextId();
        this.addMapping($$0, $$1);
        return $$1;
    }

    private int nextId() {
        while (this.nextId < this.byId.length && this.byId[this.nextId] != null) {
            ++this.nextId;
        }
        return this.nextId;
    }

    private void grow(int $$0) {
        K[] $$1 = this.keys;
        int[] $$2 = this.values;
        CrudeIncrementalIntIdentityHashBiMap<K> $$3 = new CrudeIncrementalIntIdentityHashBiMap<K>($$0);
        for (int $$4 = 0; $$4 < $$1.length; ++$$4) {
            if ($$1[$$4] == null) continue;
            $$3.addMapping($$1[$$4], $$2[$$4]);
        }
        this.keys = $$3.keys;
        this.values = $$3.values;
        this.byId = $$3.byId;
        this.nextId = $$3.nextId;
        this.size = $$3.size;
    }

    public void addMapping(K $$0, int $$1) {
        int $$2 = Math.max($$1, this.size + 1);
        if ((float)$$2 >= (float)this.keys.length * 0.8f) {
            int $$3;
            for ($$3 = this.keys.length << 1; $$3 < $$1; $$3 <<= 1) {
            }
            this.grow($$3);
        }
        int $$4 = this.findEmpty(this.hash($$0));
        this.keys[$$4] = $$0;
        this.values[$$4] = $$1;
        this.byId[$$1] = $$0;
        ++this.size;
        if ($$1 == this.nextId) {
            ++this.nextId;
        }
    }

    private int hash(@Nullable K $$0) {
        return (Mth.murmurHash3Mixer(System.identityHashCode($$0)) & Integer.MAX_VALUE) % this.keys.length;
    }

    private int indexOf(@Nullable K $$0, int $$1) {
        for (int $$2 = $$1; $$2 < this.keys.length; ++$$2) {
            if (this.keys[$$2] == $$0) {
                return $$2;
            }
            if (this.keys[$$2] != EMPTY_SLOT) continue;
            return -1;
        }
        for (int $$3 = 0; $$3 < $$1; ++$$3) {
            if (this.keys[$$3] == $$0) {
                return $$3;
            }
            if (this.keys[$$3] != EMPTY_SLOT) continue;
            return -1;
        }
        return -1;
    }

    private int findEmpty(int $$0) {
        for (int $$1 = $$0; $$1 < this.keys.length; ++$$1) {
            if (this.keys[$$1] != EMPTY_SLOT) continue;
            return $$1;
        }
        for (int $$2 = 0; $$2 < $$0; ++$$2) {
            if (this.keys[$$2] != EMPTY_SLOT) continue;
            return $$2;
        }
        throw new RuntimeException("Overflowed :(");
    }

    @Override
    public Iterator<K> iterator() {
        return Iterators.filter(Iterators.forArray(this.byId), Predicates.notNull());
    }

    public void clear() {
        Arrays.fill(this.keys, null);
        Arrays.fill(this.byId, null);
        this.nextId = 0;
        this.size = 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    public CrudeIncrementalIntIdentityHashBiMap<K> copy() {
        return new CrudeIncrementalIntIdentityHashBiMap<Object>((Object[])this.keys.clone(), (int[])this.values.clone(), (Object[])this.byId.clone(), this.nextId, this.size);
    }
}

