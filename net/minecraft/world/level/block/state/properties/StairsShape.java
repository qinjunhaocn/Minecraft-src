/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class StairsShape
extends Enum<StairsShape>
implements StringRepresentable {
    public static final /* enum */ StairsShape STRAIGHT = new StairsShape("straight");
    public static final /* enum */ StairsShape INNER_LEFT = new StairsShape("inner_left");
    public static final /* enum */ StairsShape INNER_RIGHT = new StairsShape("inner_right");
    public static final /* enum */ StairsShape OUTER_LEFT = new StairsShape("outer_left");
    public static final /* enum */ StairsShape OUTER_RIGHT = new StairsShape("outer_right");
    private final String name;
    private static final /* synthetic */ StairsShape[] $VALUES;

    public static StairsShape[] values() {
        return (StairsShape[])$VALUES.clone();
    }

    public static StairsShape valueOf(String $$0) {
        return Enum.valueOf(StairsShape.class, $$0);
    }

    private StairsShape(String $$0) {
        this.name = $$0;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ StairsShape[] a() {
        return new StairsShape[]{STRAIGHT, INNER_LEFT, INNER_RIGHT, OUTER_LEFT, OUTER_RIGHT};
    }

    static {
        $VALUES = StairsShape.a();
    }
}

