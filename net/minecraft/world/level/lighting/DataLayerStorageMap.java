/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 */
package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.DataLayer;

public abstract class DataLayerStorageMap<M extends DataLayerStorageMap<M>> {
    private static final int CACHE_SIZE = 2;
    private final long[] lastSectionKeys = new long[2];
    private final DataLayer[] lastSections = new DataLayer[2];
    private boolean cacheEnabled;
    protected final Long2ObjectOpenHashMap<DataLayer> map;

    protected DataLayerStorageMap(Long2ObjectOpenHashMap<DataLayer> $$0) {
        this.map = $$0;
        this.clearCache();
        this.cacheEnabled = true;
    }

    public abstract M copy();

    public DataLayer copyDataLayer(long $$0) {
        DataLayer $$1 = ((DataLayer)this.map.get($$0)).copy();
        this.map.put($$0, (Object)$$1);
        this.clearCache();
        return $$1;
    }

    public boolean hasLayer(long $$0) {
        return this.map.containsKey($$0);
    }

    @Nullable
    public DataLayer getLayer(long $$0) {
        DataLayer $$2;
        if (this.cacheEnabled) {
            for (int $$1 = 0; $$1 < 2; ++$$1) {
                if ($$0 != this.lastSectionKeys[$$1]) continue;
                return this.lastSections[$$1];
            }
        }
        if (($$2 = (DataLayer)this.map.get($$0)) != null) {
            if (this.cacheEnabled) {
                for (int $$3 = 1; $$3 > 0; --$$3) {
                    this.lastSectionKeys[$$3] = this.lastSectionKeys[$$3 - 1];
                    this.lastSections[$$3] = this.lastSections[$$3 - 1];
                }
                this.lastSectionKeys[0] = $$0;
                this.lastSections[0] = $$2;
            }
            return $$2;
        }
        return null;
    }

    @Nullable
    public DataLayer removeLayer(long $$0) {
        return (DataLayer)this.map.remove($$0);
    }

    public void setLayer(long $$0, DataLayer $$1) {
        this.map.put($$0, (Object)$$1);
    }

    public void clearCache() {
        for (int $$0 = 0; $$0 < 2; ++$$0) {
            this.lastSectionKeys[$$0] = Long.MAX_VALUE;
            this.lastSections[$$0] = null;
        }
    }

    public void disableCache() {
        this.cacheEnabled = false;
    }
}

