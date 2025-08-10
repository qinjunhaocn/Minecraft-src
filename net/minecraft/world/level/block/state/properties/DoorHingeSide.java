/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class DoorHingeSide
extends Enum<DoorHingeSide>
implements StringRepresentable {
    public static final /* enum */ DoorHingeSide LEFT = new DoorHingeSide();
    public static final /* enum */ DoorHingeSide RIGHT = new DoorHingeSide();
    private static final /* synthetic */ DoorHingeSide[] $VALUES;

    public static DoorHingeSide[] values() {
        return (DoorHingeSide[])$VALUES.clone();
    }

    public static DoorHingeSide valueOf(String $$0) {
        return Enum.valueOf(DoorHingeSide.class, $$0);
    }

    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return this == LEFT ? "left" : "right";
    }

    private static /* synthetic */ DoorHingeSide[] a() {
        return new DoorHingeSide[]{LEFT, RIGHT};
    }

    static {
        $VALUES = DoorHingeSide.a();
    }
}

