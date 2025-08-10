/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.debugchart;

public final class RemoteDebugSampleType
extends Enum<RemoteDebugSampleType> {
    public static final /* enum */ RemoteDebugSampleType TICK_TIME = new RemoteDebugSampleType();
    private static final /* synthetic */ RemoteDebugSampleType[] $VALUES;

    public static RemoteDebugSampleType[] values() {
        return (RemoteDebugSampleType[])$VALUES.clone();
    }

    public static RemoteDebugSampleType valueOf(String $$0) {
        return Enum.valueOf(RemoteDebugSampleType.class, $$0);
    }

    private static /* synthetic */ RemoteDebugSampleType[] a() {
        return new RemoteDebugSampleType[]{TICK_TIME};
    }

    static {
        $VALUES = RemoteDebugSampleType.a();
    }
}

