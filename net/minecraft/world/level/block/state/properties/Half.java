/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class Half
extends Enum<Half>
implements StringRepresentable {
    public static final /* enum */ Half TOP = new Half("top");
    public static final /* enum */ Half BOTTOM = new Half("bottom");
    private final String name;
    private static final /* synthetic */ Half[] $VALUES;

    public static Half[] values() {
        return (Half[])$VALUES.clone();
    }

    public static Half valueOf(String $$0) {
        return Enum.valueOf(Half.class, $$0);
    }

    private Half(String $$0) {
        this.name = $$0;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ Half[] a() {
        return new Half[]{TOP, BOTTOM};
    }

    static {
        $VALUES = Half.a();
    }
}

