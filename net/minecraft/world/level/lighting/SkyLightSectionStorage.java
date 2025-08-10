/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 */
package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;

public class SkyLightSectionStorage
extends LayerLightSectionStorage<SkyDataLayerStorageMap> {
    protected SkyLightSectionStorage(LightChunkGetter $$0) {
        super(LightLayer.SKY, $$0, new SkyDataLayerStorageMap((Long2ObjectOpenHashMap<DataLayer>)new Long2ObjectOpenHashMap(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
    }

    @Override
    protected int getLightValue(long $$0) {
        return this.getLightValue($$0, false);
    }

    protected int getLightValue(long $$0, boolean $$1) {
        long $$2 = SectionPos.blockToSection($$0);
        int $$3 = SectionPos.y($$2);
        SkyDataLayerStorageMap $$4 = $$1 ? (SkyDataLayerStorageMap)this.updatingSectionData : (SkyDataLayerStorageMap)this.visibleSectionData;
        int $$5 = $$4.topSections.get(SectionPos.getZeroNode($$2));
        if ($$5 == $$4.currentLowestY || $$3 >= $$5) {
            if ($$1 && !this.lightOnInSection($$2)) {
                return 0;
            }
            return 15;
        }
        DataLayer $$6 = this.getDataLayer($$4, $$2);
        if ($$6 == null) {
            $$0 = BlockPos.getFlatIndex($$0);
            while ($$6 == null) {
                if (++$$3 >= $$5) {
                    return 15;
                }
                $$2 = SectionPos.offset($$2, Direction.UP);
                $$6 = this.getDataLayer($$4, $$2);
            }
        }
        return $$6.get(SectionPos.sectionRelative(BlockPos.getX($$0)), SectionPos.sectionRelative(BlockPos.getY($$0)), SectionPos.sectionRelative(BlockPos.getZ($$0)));
    }

    @Override
    protected void onNodeAdded(long $$0) {
        long $$2;
        int $$3;
        int $$1 = SectionPos.y($$0);
        if (((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY > $$1) {
            ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY = $$1;
            ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.defaultReturnValue(((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY);
        }
        if (($$3 = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get($$2 = SectionPos.getZeroNode($$0))) < $$1 + 1) {
            ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.put($$2, $$1 + 1);
        }
    }

    @Override
    protected void onNodeRemoved(long $$0) {
        long $$1 = SectionPos.getZeroNode($$0);
        int $$2 = SectionPos.y($$0);
        if (((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get($$1) == $$2 + 1) {
            long $$3 = $$0;
            while (!this.storingLightForSection($$3) && this.hasLightDataAtOrBelow($$2)) {
                --$$2;
                $$3 = SectionPos.offset($$3, Direction.DOWN);
            }
            if (this.storingLightForSection($$3)) {
                ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.put($$1, $$2 + 1);
            } else {
                ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.remove($$1);
            }
        }
    }

    @Override
    protected DataLayer createDataLayer(long $$0) {
        DataLayer $$4;
        DataLayer $$1 = (DataLayer)this.queuedSections.get($$0);
        if ($$1 != null) {
            return $$1;
        }
        int $$2 = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(SectionPos.getZeroNode($$0));
        if ($$2 == ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY || SectionPos.y($$0) >= $$2) {
            if (this.lightOnInSection($$0)) {
                return new DataLayer(15);
            }
            return new DataLayer();
        }
        long $$3 = SectionPos.offset($$0, Direction.UP);
        while (($$4 = this.getDataLayer($$3, true)) == null) {
            $$3 = SectionPos.offset($$3, Direction.UP);
        }
        return SkyLightSectionStorage.repeatFirstLayer($$4);
    }

    private static DataLayer repeatFirstLayer(DataLayer $$0) {
        if ($$0.isDefinitelyHomogenous()) {
            return $$0.copy();
        }
        byte[] $$1 = $$0.a();
        byte[] $$2 = new byte[2048];
        for (int $$3 = 0; $$3 < 16; ++$$3) {
            System.arraycopy($$1, 0, $$2, $$3 * 128, 128);
        }
        return new DataLayer($$2);
    }

    protected boolean hasLightDataAtOrBelow(int $$0) {
        return $$0 >= ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY;
    }

    protected boolean isAboveData(long $$0) {
        long $$1 = SectionPos.getZeroNode($$0);
        int $$2 = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get($$1);
        return $$2 == ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY || SectionPos.y($$0) >= $$2;
    }

    protected int getTopSectionY(long $$0) {
        return ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get($$0);
    }

    protected int getBottomSectionY() {
        return ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY;
    }

    protected static final class SkyDataLayerStorageMap
    extends DataLayerStorageMap<SkyDataLayerStorageMap> {
        int currentLowestY;
        final Long2IntOpenHashMap topSections;

        public SkyDataLayerStorageMap(Long2ObjectOpenHashMap<DataLayer> $$0, Long2IntOpenHashMap $$1, int $$2) {
            super($$0);
            this.topSections = $$1;
            $$1.defaultReturnValue($$2);
            this.currentLowestY = $$2;
        }

        @Override
        public SkyDataLayerStorageMap copy() {
            return new SkyDataLayerStorageMap((Long2ObjectOpenHashMap<DataLayer>)this.map.clone(), this.topSections.clone(), this.currentLowestY);
        }

        @Override
        public /* synthetic */ DataLayerStorageMap copy() {
            return this.copy();
        }
    }
}

