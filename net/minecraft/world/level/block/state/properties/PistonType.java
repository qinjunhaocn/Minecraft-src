/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class PistonType
extends Enum<PistonType>
implements StringRepresentable {
    public static final /* enum */ PistonType DEFAULT = new PistonType("normal");
    public static final /* enum */ PistonType STICKY = new PistonType("sticky");
    private final String name;
    private static final /* synthetic */ PistonType[] $VALUES;

    public static PistonType[] values() {
        return (PistonType[])$VALUES.clone();
    }

    public static PistonType valueOf(String $$0) {
        return Enum.valueOf(PistonType.class, $$0);
    }

    private PistonType(String $$0) {
        this.name = $$0;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ PistonType[] a() {
        return new PistonType[]{DEFAULT, STICKY};
    }

    static {
        $VALUES = PistonType.a();
    }
}

