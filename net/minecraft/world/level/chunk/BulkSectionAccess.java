/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 */
package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class BulkSectionAccess
implements AutoCloseable {
    private final LevelAccessor level;
    private final Long2ObjectMap<LevelChunkSection> acquiredSections = new Long2ObjectOpenHashMap();
    @Nullable
    private LevelChunkSection lastSection;
    private long lastSectionKey;

    public BulkSectionAccess(LevelAccessor $$0) {
        this.level = $$0;
    }

    @Nullable
    public LevelChunkSection getSection(BlockPos $$0) {
        int $$1 = this.level.getSectionIndex($$0.getY());
        if ($$1 < 0 || $$1 >= this.level.getSectionsCount()) {
            return null;
        }
        long $$22 = SectionPos.asLong($$0);
        if (this.lastSection == null || this.lastSectionKey != $$22) {
            this.lastSection = (LevelChunkSection)this.acquiredSections.computeIfAbsent($$22, $$2 -> {
                ChunkAccess $$3 = this.level.getChunk(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()));
                LevelChunkSection $$4 = $$3.getSection($$1);
                $$4.acquire();
                return $$4;
            });
            this.lastSectionKey = $$22;
        }
        return this.lastSection;
    }

    public BlockState getBlockState(BlockPos $$0) {
        LevelChunkSection $$1 = this.getSection($$0);
        if ($$1 == null) {
            return Blocks.AIR.defaultBlockState();
        }
        int $$2 = SectionPos.sectionRelative($$0.getX());
        int $$3 = SectionPos.sectionRelative($$0.getY());
        int $$4 = SectionPos.sectionRelative($$0.getZ());
        return $$1.getBlockState($$2, $$3, $$4);
    }

    @Override
    public void close() {
        for (LevelChunkSection $$0 : this.acquiredSections.values()) {
            $$0.release();
        }
    }
}

