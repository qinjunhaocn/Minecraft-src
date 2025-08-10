/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class RedstoneSide
extends Enum<RedstoneSide>
implements StringRepresentable {
    public static final /* enum */ RedstoneSide UP = new RedstoneSide("up");
    public static final /* enum */ RedstoneSide SIDE = new RedstoneSide("side");
    public static final /* enum */ RedstoneSide NONE = new RedstoneSide("none");
    private final String name;
    private static final /* synthetic */ RedstoneSide[] $VALUES;

    public static RedstoneSide[] values() {
        return (RedstoneSide[])$VALUES.clone();
    }

    public static RedstoneSide valueOf(String $$0) {
        return Enum.valueOf(RedstoneSide.class, $$0);
    }

    private RedstoneSide(String $$0) {
        this.name = $$0;
    }

    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public boolean isConnected() {
        return this != NONE;
    }

    private static /* synthetic */ RedstoneSide[] b() {
        return new RedstoneSide[]{UP, SIDE, NONE};
    }

    static {
        $VALUES = RedstoneSide.b();
    }
}

