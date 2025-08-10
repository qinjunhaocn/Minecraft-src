/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 */
package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LightEngine;

public abstract class LayerLightSectionStorage<M extends DataLayerStorageMap<M>> {
    private final LightLayer layer;
    protected final LightChunkGetter chunkSource;
    protected final Long2ByteMap sectionStates = new Long2ByteOpenHashMap();
    private final LongSet columnsWithSources = new LongOpenHashSet();
    protected volatile M visibleSectionData;
    protected final M updatingSectionData;
    protected final LongSet changedSections = new LongOpenHashSet();
    protected final LongSet sectionsAffectedByLightUpdates = new LongOpenHashSet();
    protected final Long2ObjectMap<DataLayer> queuedSections = Long2ObjectMaps.synchronize((Long2ObjectMap)new Long2ObjectOpenHashMap());
    private final LongSet columnsToRetainQueuedDataFor = new LongOpenHashSet();
    private final LongSet toRemove = new LongOpenHashSet();
    protected volatile boolean hasInconsistencies;

    protected LayerLightSectionStorage(LightLayer $$0, LightChunkGetter $$1, M $$2) {
        this.layer = $$0;
        this.chunkSource = $$1;
        this.updatingSectionData = $$2;
        this.visibleSectionData = ((DataLayerStorageMap)$$2).copy();
        ((DataLayerStorageMap)this.visibleSectionData).disableCache();
        this.sectionStates.defaultReturnValue((byte)0);
    }

    protected boolean storingLightForSection(long $$0) {
        return this.getDataLayer($$0, true) != null;
    }

    @Nullable
    protected DataLayer getDataLayer(long $$0, boolean $$1) {
        return this.getDataLayer($$1 ? this.updatingSectionData : this.visibleSectionData, $$0);
    }

    @Nullable
    protected DataLayer getDataLayer(M $$0, long $$1) {
        return ((DataLayerStorageMap)$$0).getLayer($$1);
    }

    @Nullable
    protected DataLayer getDataLayerToWrite(long $$0) {
        DataLayer $$1 = ((DataLayerStorageMap)this.updatingSectionData).getLayer($$0);
        if ($$1 == null) {
            return null;
        }
        if (this.changedSections.add($$0)) {
            $$1 = $$1.copy();
            ((DataLayerStorageMap)this.updatingSectionData).setLayer($$0, $$1);
            ((DataLayerStorageMap)this.updatingSectionData).clearCache();
        }
        return $$1;
    }

    @Nullable
    public DataLayer getDataLayerData(long $$0) {
        DataLayer $$1 = (DataLayer)this.queuedSections.get($$0);
        if ($$1 != null) {
            return $$1;
        }
        return this.getDataLayer($$0, false);
    }

    protected abstract int getLightValue(long var1);

    protected int getStoredLevel(long $$0) {
        long $$1 = SectionPos.blockToSection($$0);
        DataLayer $$2 = this.getDataLayer($$1, true);
        return $$2.get(SectionPos.sectionRelative(BlockPos.getX($$0)), SectionPos.sectionRelative(BlockPos.getY($$0)), SectionPos.sectionRelative(BlockPos.getZ($$0)));
    }

    protected void setStoredLevel(long $$0, int $$1) {
        DataLayer $$4;
        long $$2 = SectionPos.blockToSection($$0);
        if (this.changedSections.add($$2)) {
            DataLayer $$3 = ((DataLayerStorageMap)this.updatingSectionData).copyDataLayer($$2);
        } else {
            $$4 = this.getDataLayer($$2, true);
        }
        $$4.set(SectionPos.sectionRelative(BlockPos.getX($$0)), SectionPos.sectionRelative(BlockPos.getY($$0)), SectionPos.sectionRelative(BlockPos.getZ($$0)), $$1);
        SectionPos.aroundAndAtBlockPos($$0, arg_0 -> ((LongSet)this.sectionsAffectedByLightUpdates).add(arg_0));
    }

    protected void markSectionAndNeighborsAsAffected(long $$0) {
        int $$1 = SectionPos.x($$0);
        int $$2 = SectionPos.y($$0);
        int $$3 = SectionPos.z($$0);
        for (int $$4 = -1; $$4 <= 1; ++$$4) {
            for (int $$5 = -1; $$5 <= 1; ++$$5) {
                for (int $$6 = -1; $$6 <= 1; ++$$6) {
                    this.sectionsAffectedByLightUpdates.add(SectionPos.asLong($$1 + $$5, $$2 + $$6, $$3 + $$4));
                }
            }
        }
    }

    protected DataLayer createDataLayer(long $$0) {
        DataLayer $$1 = (DataLayer)this.queuedSections.get($$0);
        if ($$1 != null) {
            return $$1;
        }
        return new DataLayer();
    }

    protected boolean hasInconsistencies() {
        return this.hasInconsistencies;
    }

    protected void markNewInconsistencies(LightEngine<M, ?> $$0) {
        if (!this.hasInconsistencies) {
            return;
        }
        this.hasInconsistencies = false;
        LongIterator longIterator = this.toRemove.iterator();
        while (longIterator.hasNext()) {
            long $$1 = (Long)longIterator.next();
            DataLayer $$2 = (DataLayer)this.queuedSections.remove($$1);
            DataLayer $$3 = ((DataLayerStorageMap)this.updatingSectionData).removeLayer($$1);
            if (!this.columnsToRetainQueuedDataFor.contains(SectionPos.getZeroNode($$1))) continue;
            if ($$2 != null) {
                this.queuedSections.put($$1, (Object)$$2);
                continue;
            }
            if ($$3 == null) continue;
            this.queuedSections.put($$1, (Object)$$3);
        }
        ((DataLayerStorageMap)this.updatingSectionData).clearCache();
        longIterator = this.toRemove.iterator();
        while (longIterator.hasNext()) {
            long $$4 = (Long)longIterator.next();
            this.onNodeRemoved($$4);
            this.changedSections.add($$4);
        }
        this.toRemove.clear();
        ObjectIterator $$5 = Long2ObjectMaps.fastIterator(this.queuedSections);
        while ($$5.hasNext()) {
            Long2ObjectMap.Entry $$6 = (Long2ObjectMap.Entry)$$5.next();
            long $$7 = $$6.getLongKey();
            if (!this.storingLightForSection($$7)) continue;
            DataLayer $$8 = (DataLayer)$$6.getValue();
            if (((DataLayerStorageMap)this.updatingSectionData).getLayer($$7) != $$8) {
                ((DataLayerStorageMap)this.updatingSectionData).setLayer($$7, $$8);
                this.changedSections.add($$7);
            }
            $$5.remove();
        }
        ((DataLayerStorageMap)this.updatingSectionData).clearCache();
    }

    protected void onNodeAdded(long $$0) {
    }

    protected void onNodeRemoved(long $$0) {
    }

    protected void setLightEnabled(long $$0, boolean $$1) {
        if ($$1) {
            this.columnsWithSources.add($$0);
        } else {
            this.columnsWithSources.remove($$0);
        }
    }

    protected boolean lightOnInSection(long $$0) {
        long $$1 = SectionPos.getZeroNode($$0);
        return this.columnsWithSources.contains($$1);
    }

    protected boolean lightOnInColumn(long $$0) {
        return this.columnsWithSources.contains($$0);
    }

    public void retainData(long $$0, boolean $$1) {
        if ($$1) {
            this.columnsToRetainQueuedDataFor.add($$0);
        } else {
            this.columnsToRetainQueuedDataFor.remove($$0);
        }
    }

    protected void queueSectionData(long $$0, @Nullable DataLayer $$1) {
        if ($$1 != null) {
            this.queuedSections.put($$0, (Object)$$1);
            this.hasInconsistencies = true;
        } else {
            this.queuedSections.remove($$0);
        }
    }

    protected void updateSectionStatus(long $$0, boolean $$1) {
        byte $$3;
        byte $$2 = this.sectionStates.get($$0);
        if ($$2 == ($$3 = SectionState.hasData($$2, !$$1))) {
            return;
        }
        this.putSectionState($$0, $$3);
        int $$4 = $$1 ? -1 : 1;
        for (int $$5 = -1; $$5 <= 1; ++$$5) {
            for (int $$6 = -1; $$6 <= 1; ++$$6) {
                for (int $$7 = -1; $$7 <= 1; ++$$7) {
                    if ($$5 == 0 && $$6 == 0 && $$7 == 0) continue;
                    long $$8 = SectionPos.offset($$0, $$5, $$6, $$7);
                    byte $$9 = this.sectionStates.get($$8);
                    this.putSectionState($$8, SectionState.neighborCount($$9, SectionState.neighborCount($$9) + $$4));
                }
            }
        }
    }

    protected void putSectionState(long $$0, byte $$1) {
        if ($$1 != 0) {
            if (this.sectionStates.put($$0, $$1) == 0) {
                this.initializeSection($$0);
            }
        } else if (this.sectionStates.remove($$0) != 0) {
            this.removeSection($$0);
        }
    }

    private void initializeSection(long $$0) {
        if (!this.toRemove.remove($$0)) {
            ((DataLayerStorageMap)this.updatingSectionData).setLayer($$0, this.createDataLayer($$0));
            this.changedSections.add($$0);
            this.onNodeAdded($$0);
            this.markSectionAndNeighborsAsAffected($$0);
            this.hasInconsistencies = true;
        }
    }

    private void removeSection(long $$0) {
        this.toRemove.add($$0);
        this.hasInconsistencies = true;
    }

    protected void swapSectionMap() {
        if (!this.changedSections.isEmpty()) {
            Object $$0 = ((DataLayerStorageMap)this.updatingSectionData).copy();
            ((DataLayerStorageMap)$$0).disableCache();
            this.visibleSectionData = $$0;
            this.changedSections.clear();
        }
        if (!this.sectionsAffectedByLightUpdates.isEmpty()) {
            LongIterator $$1 = this.sectionsAffectedByLightUpdates.iterator();
            while ($$1.hasNext()) {
                long $$2 = $$1.nextLong();
                this.chunkSource.onLightUpdate(this.layer, SectionPos.of($$2));
            }
            this.sectionsAffectedByLightUpdates.clear();
        }
    }

    public SectionType getDebugSectionType(long $$0) {
        return SectionState.type(this.sectionStates.get($$0));
    }

    protected static class SectionState {
        public static final byte EMPTY = 0;
        private static final int MIN_NEIGHBORS = 0;
        private static final int MAX_NEIGHBORS = 26;
        private static final byte HAS_DATA_BIT = 32;
        private static final byte NEIGHBOR_COUNT_BITS = 31;

        protected SectionState() {
        }

        public static byte hasData(byte $$0, boolean $$1) {
            return (byte)($$1 ? $$0 | 0x20 : $$0 & 0xFFFFFFDF);
        }

        public static byte neighborCount(byte $$0, int $$1) {
            if ($$1 < 0 || $$1 > 26) {
                throw new IllegalArgumentException("Neighbor count was not within range [0; 26]");
            }
            return (byte)($$0 & 0xFFFFFFE0 | $$1 & 0x1F);
        }

        public static boolean hasData(byte $$0) {
            return ($$0 & 0x20) != 0;
        }

        public static int neighborCount(byte $$0) {
            return $$0 & 0x1F;
        }

        public static SectionType type(byte $$0) {
            if ($$0 == 0) {
                return SectionType.EMPTY;
            }
            if (SectionState.hasData($$0)) {
                return SectionType.LIGHT_AND_DATA;
            }
            return SectionType.LIGHT_ONLY;
        }
    }

    public static final class SectionType
    extends Enum<SectionType> {
        public static final /* enum */ SectionType EMPTY = new SectionType("2");
        public static final /* enum */ SectionType LIGHT_ONLY = new SectionType("1");
        public static final /* enum */ SectionType LIGHT_AND_DATA = new SectionType("0");
        private final String display;
        private static final /* synthetic */ SectionType[] $VALUES;

        public static SectionType[] values() {
            return (SectionType[])$VALUES.clone();
        }

        public static SectionType valueOf(String $$0) {
            return Enum.valueOf(SectionType.class, $$0);
        }

        private SectionType(String $$0) {
            this.display = $$0;
        }

        public String display() {
            return this.display;
        }

        private static /* synthetic */ SectionType[] b() {
            return new SectionType[]{EMPTY, LIGHT_ONLY, LIGHT_AND_DATA};
        }

        static {
            $VALUES = SectionType.b();
        }
    }
}

