/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class Tilt
extends Enum<Tilt>
implements StringRepresentable {
    public static final /* enum */ Tilt NONE = new Tilt("none", true);
    public static final /* enum */ Tilt UNSTABLE = new Tilt("unstable", false);
    public static final /* enum */ Tilt PARTIAL = new Tilt("partial", true);
    public static final /* enum */ Tilt FULL = new Tilt("full", true);
    private final String name;
    private final boolean causesVibration;
    private static final /* synthetic */ Tilt[] $VALUES;

    public static Tilt[] values() {
        return (Tilt[])$VALUES.clone();
    }

    public static Tilt valueOf(String $$0) {
        return Enum.valueOf(Tilt.class, $$0);
    }

    private Tilt(String $$0, boolean $$1) {
        this.name = $$0;
        this.causesVibration = $$1;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public boolean causesVibration() {
        return this.causesVibration;
    }

    private static /* synthetic */ Tilt[] b() {
        return new Tilt[]{NONE, UNSTABLE, PARTIAL, FULL};
    }

    static {
        $VALUES = Tilt.b();
    }
}

