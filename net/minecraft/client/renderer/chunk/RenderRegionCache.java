/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 */
package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCopy;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class RenderRegionCache {
    private final Long2ObjectMap<SectionCopy> sectionCopyCache = new Long2ObjectOpenHashMap();

    public RenderSectionRegion createRegion(Level $$0, long $$1) {
        int $$2 = SectionPos.x($$1);
        int $$3 = SectionPos.y($$1);
        int $$4 = SectionPos.z($$1);
        int $$5 = $$2 - 1;
        int $$6 = $$3 - 1;
        int $$7 = $$4 - 1;
        int $$8 = $$2 + 1;
        int $$9 = $$3 + 1;
        int $$10 = $$4 + 1;
        SectionCopy[] $$11 = new SectionCopy[27];
        for (int $$12 = $$7; $$12 <= $$10; ++$$12) {
            for (int $$13 = $$6; $$13 <= $$9; ++$$13) {
                for (int $$14 = $$5; $$14 <= $$8; ++$$14) {
                    int $$15 = RenderSectionRegion.index($$5, $$6, $$7, $$14, $$13, $$12);
                    $$11[$$15] = this.getSectionDataCopy($$0, $$14, $$13, $$12);
                }
            }
        }
        return new RenderSectionRegion($$0, $$5, $$6, $$7, $$11);
    }

    private SectionCopy getSectionDataCopy(Level $$0, int $$1, int $$2, int $$3) {
        return (SectionCopy)this.sectionCopyCache.computeIfAbsent(SectionPos.asLong($$1, $$2, $$3), $$4 -> {
            LevelChunk $$5 = $$0.getChunk($$1, $$3);
            return new SectionCopy($$5, $$5.getSectionIndexFromSectionY($$2));
        });
    }
}

