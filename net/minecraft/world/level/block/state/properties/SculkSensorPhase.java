/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class SculkSensorPhase
extends Enum<SculkSensorPhase>
implements StringRepresentable {
    public static final /* enum */ SculkSensorPhase INACTIVE = new SculkSensorPhase("inactive");
    public static final /* enum */ SculkSensorPhase ACTIVE = new SculkSensorPhase("active");
    public static final /* enum */ SculkSensorPhase COOLDOWN = new SculkSensorPhase("cooldown");
    private final String name;
    private static final /* synthetic */ SculkSensorPhase[] $VALUES;

    public static SculkSensorPhase[] values() {
        return (SculkSensorPhase[])$VALUES.clone();
    }

    public static SculkSensorPhase valueOf(String $$0) {
        return Enum.valueOf(SculkSensorPhase.class, $$0);
    }

    private SculkSensorPhase(String $$0) {
        this.name = $$0;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ SculkSensorPhase[] a() {
        return new SculkSensorPhase[]{INACTIVE, ACTIVE, COOLDOWN};
    }

    static {
        $VALUES = SculkSensorPhase.a();
    }
}

