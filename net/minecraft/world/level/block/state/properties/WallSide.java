/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class WallSide
extends Enum<WallSide>
implements StringRepresentable {
    public static final /* enum */ WallSide NONE = new WallSide("none");
    public static final /* enum */ WallSide LOW = new WallSide("low");
    public static final /* enum */ WallSide TALL = new WallSide("tall");
    private final String name;
    private static final /* synthetic */ WallSide[] $VALUES;

    public static WallSide[] values() {
        return (WallSide[])$VALUES.clone();
    }

    public static WallSide valueOf(String $$0) {
        return Enum.valueOf(WallSide.class, $$0);
    }

    private WallSide(String $$0) {
        this.name = $$0;
    }

    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ WallSide[] a() {
        return new WallSide[]{NONE, LOW, TALL};
    }

    static {
        $VALUES = WallSide.a();
    }
}

