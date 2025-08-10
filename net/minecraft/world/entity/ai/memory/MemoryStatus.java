/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.memory;

public final class MemoryStatus
extends Enum<MemoryStatus> {
    public static final /* enum */ MemoryStatus VALUE_PRESENT = new MemoryStatus();
    public static final /* enum */ MemoryStatus VALUE_ABSENT = new MemoryStatus();
    public static final /* enum */ MemoryStatus REGISTERED = new MemoryStatus();
    private static final /* synthetic */ MemoryStatus[] $VALUES;

    public static MemoryStatus[] values() {
        return (MemoryStatus[])$VALUES.clone();
    }

    public static MemoryStatus valueOf(String $$0) {
        return Enum.valueOf(MemoryStatus.class, $$0);
    }

    private static /* synthetic */ MemoryStatus[] a() {
        return new MemoryStatus[]{VALUE_PRESENT, VALUE_ABSENT, REGISTERED};
    }

    static {
        $VALUES = MemoryStatus.a();
    }
}

