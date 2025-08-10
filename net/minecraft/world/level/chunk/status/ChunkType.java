/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.chunk.status;

public final class ChunkType
extends Enum<ChunkType> {
    public static final /* enum */ ChunkType PROTOCHUNK = new ChunkType();
    public static final /* enum */ ChunkType LEVELCHUNK = new ChunkType();
    private static final /* synthetic */ ChunkType[] $VALUES;

    public static ChunkType[] values() {
        return (ChunkType[])$VALUES.clone();
    }

    public static ChunkType valueOf(String $$0) {
        return Enum.valueOf(ChunkType.class, $$0);
    }

    private static /* synthetic */ ChunkType[] a() {
        return new ChunkType[]{PROTOCHUNK, LEVELCHUNK};
    }

    static {
        $VALUES = ChunkType.a();
    }
}

