/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.level;

public final class FullChunkStatus
extends Enum<FullChunkStatus> {
    public static final /* enum */ FullChunkStatus INACCESSIBLE = new FullChunkStatus();
    public static final /* enum */ FullChunkStatus FULL = new FullChunkStatus();
    public static final /* enum */ FullChunkStatus BLOCK_TICKING = new FullChunkStatus();
    public static final /* enum */ FullChunkStatus ENTITY_TICKING = new FullChunkStatus();
    private static final /* synthetic */ FullChunkStatus[] $VALUES;

    public static FullChunkStatus[] values() {
        return (FullChunkStatus[])$VALUES.clone();
    }

    public static FullChunkStatus valueOf(String $$0) {
        return Enum.valueOf(FullChunkStatus.class, $$0);
    }

    public boolean isOrAfter(FullChunkStatus $$0) {
        return this.ordinal() >= $$0.ordinal();
    }

    private static /* synthetic */ FullChunkStatus[] a() {
        return new FullChunkStatus[]{INACCESSIBLE, FULL, BLOCK_TICKING, ENTITY_TICKING};
    }

    static {
        $VALUES = FullChunkStatus.a();
    }
}

