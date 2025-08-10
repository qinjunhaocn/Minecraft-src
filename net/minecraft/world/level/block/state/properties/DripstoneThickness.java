/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class DripstoneThickness
extends Enum<DripstoneThickness>
implements StringRepresentable {
    public static final /* enum */ DripstoneThickness TIP_MERGE = new DripstoneThickness("tip_merge");
    public static final /* enum */ DripstoneThickness TIP = new DripstoneThickness("tip");
    public static final /* enum */ DripstoneThickness FRUSTUM = new DripstoneThickness("frustum");
    public static final /* enum */ DripstoneThickness MIDDLE = new DripstoneThickness("middle");
    public static final /* enum */ DripstoneThickness BASE = new DripstoneThickness("base");
    private final String name;
    private static final /* synthetic */ DripstoneThickness[] $VALUES;

    public static DripstoneThickness[] values() {
        return (DripstoneThickness[])$VALUES.clone();
    }

    public static DripstoneThickness valueOf(String $$0) {
        return Enum.valueOf(DripstoneThickness.class, $$0);
    }

    private DripstoneThickness(String $$0) {
        this.name = $$0;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ DripstoneThickness[] a() {
        return new DripstoneThickness[]{TIP_MERGE, TIP, FRUSTUM, MIDDLE, BASE};
    }

    static {
        $VALUES = DripstoneThickness.a();
    }
}

