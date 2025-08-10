/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.debugchart;

public final class TpsDebugDimensions
extends Enum<TpsDebugDimensions> {
    public static final /* enum */ TpsDebugDimensions FULL_TICK = new TpsDebugDimensions();
    public static final /* enum */ TpsDebugDimensions TICK_SERVER_METHOD = new TpsDebugDimensions();
    public static final /* enum */ TpsDebugDimensions SCHEDULED_TASKS = new TpsDebugDimensions();
    public static final /* enum */ TpsDebugDimensions IDLE = new TpsDebugDimensions();
    private static final /* synthetic */ TpsDebugDimensions[] $VALUES;

    public static TpsDebugDimensions[] values() {
        return (TpsDebugDimensions[])$VALUES.clone();
    }

    public static TpsDebugDimensions valueOf(String $$0) {
        return Enum.valueOf(TpsDebugDimensions.class, $$0);
    }

    private static /* synthetic */ TpsDebugDimensions[] a() {
        return new TpsDebugDimensions[]{FULL_TICK, TICK_SERVER_METHOD, SCHEDULED_TASKS, IDLE};
    }

    static {
        $VALUES = TpsDebugDimensions.a();
    }
}

