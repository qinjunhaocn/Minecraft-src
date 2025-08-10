/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.level;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;

public record ColumnPos(int x, int z) {
    private static final long COORD_BITS = 32L;
    private static final long COORD_MASK = 0xFFFFFFFFL;

    public ChunkPos toChunkPos() {
        return new ChunkPos(SectionPos.blockToSectionCoord(this.x), SectionPos.blockToSectionCoord(this.z));
    }

    public long toLong() {
        return ColumnPos.asLong(this.x, this.z);
    }

    public static long asLong(int $$0, int $$1) {
        return (long)$$0 & 0xFFFFFFFFL | ((long)$$1 & 0xFFFFFFFFL) << 32;
    }

    public static int getX(long $$0) {
        return (int)($$0 & 0xFFFFFFFFL);
    }

    public static int getZ(long $$0) {
        return (int)($$0 >>> 32 & 0xFFFFFFFFL);
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    public int hashCode() {
        return ChunkPos.hash(this.x, this.z);
    }
}

