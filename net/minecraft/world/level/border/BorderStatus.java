/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.border;

public final class BorderStatus
extends Enum<BorderStatus> {
    public static final /* enum */ BorderStatus GROWING = new BorderStatus(4259712);
    public static final /* enum */ BorderStatus SHRINKING = new BorderStatus(0xFF3030);
    public static final /* enum */ BorderStatus STATIONARY = new BorderStatus(2138367);
    private final int color;
    private static final /* synthetic */ BorderStatus[] $VALUES;

    public static BorderStatus[] values() {
        return (BorderStatus[])$VALUES.clone();
    }

    public static BorderStatus valueOf(String $$0) {
        return Enum.valueOf(BorderStatus.class, $$0);
    }

    private BorderStatus(int $$0) {
        this.color = $$0;
    }

    public int getColor() {
        return this.color;
    }

    private static /* synthetic */ BorderStatus[] b() {
        return new BorderStatus[]{GROWING, SHRINKING, STATIONARY};
    }

    static {
        $VALUES = BorderStatus.b();
    }
}

