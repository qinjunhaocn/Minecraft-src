/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class SlabType
extends Enum<SlabType>
implements StringRepresentable {
    public static final /* enum */ SlabType TOP = new SlabType("top");
    public static final /* enum */ SlabType BOTTOM = new SlabType("bottom");
    public static final /* enum */ SlabType DOUBLE = new SlabType("double");
    private final String name;
    private static final /* synthetic */ SlabType[] $VALUES;

    public static SlabType[] values() {
        return (SlabType[])$VALUES.clone();
    }

    public static SlabType valueOf(String $$0) {
        return Enum.valueOf(SlabType.class, $$0);
    }

    private SlabType(String $$0) {
        this.name = $$0;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ SlabType[] a() {
        return new SlabType[]{TOP, BOTTOM, DOUBLE};
    }

    static {
        $VALUES = SlabType.a();
    }
}

