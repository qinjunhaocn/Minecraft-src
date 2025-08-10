/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.ticks;

import com.mojang.serialization.Codec;

public final class TickPriority
extends Enum<TickPriority> {
    public static final /* enum */ TickPriority EXTREMELY_HIGH = new TickPriority(-3);
    public static final /* enum */ TickPriority VERY_HIGH = new TickPriority(-2);
    public static final /* enum */ TickPriority HIGH = new TickPriority(-1);
    public static final /* enum */ TickPriority NORMAL = new TickPriority(0);
    public static final /* enum */ TickPriority LOW = new TickPriority(1);
    public static final /* enum */ TickPriority VERY_LOW = new TickPriority(2);
    public static final /* enum */ TickPriority EXTREMELY_LOW = new TickPriority(3);
    public static final Codec<TickPriority> CODEC;
    private final int value;
    private static final /* synthetic */ TickPriority[] $VALUES;

    public static TickPriority[] values() {
        return (TickPriority[])$VALUES.clone();
    }

    public static TickPriority valueOf(String $$0) {
        return Enum.valueOf(TickPriority.class, $$0);
    }

    private TickPriority(int $$0) {
        this.value = $$0;
    }

    public static TickPriority byValue(int $$0) {
        for (TickPriority $$1 : TickPriority.values()) {
            if ($$1.value != $$0) continue;
            return $$1;
        }
        if ($$0 < TickPriority.EXTREMELY_HIGH.value) {
            return EXTREMELY_HIGH;
        }
        return EXTREMELY_LOW;
    }

    public int getValue() {
        return this.value;
    }

    private static /* synthetic */ TickPriority[] b() {
        return new TickPriority[]{EXTREMELY_HIGH, VERY_HIGH, HIGH, NORMAL, LOW, VERY_LOW, EXTREMELY_LOW};
    }

    static {
        $VALUES = TickPriority.b();
        CODEC = Codec.INT.xmap(TickPriority::byValue, TickPriority::getValue);
    }
}

