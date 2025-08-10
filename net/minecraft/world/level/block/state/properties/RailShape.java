/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class RailShape
extends Enum<RailShape>
implements StringRepresentable {
    public static final /* enum */ RailShape NORTH_SOUTH = new RailShape("north_south");
    public static final /* enum */ RailShape EAST_WEST = new RailShape("east_west");
    public static final /* enum */ RailShape ASCENDING_EAST = new RailShape("ascending_east");
    public static final /* enum */ RailShape ASCENDING_WEST = new RailShape("ascending_west");
    public static final /* enum */ RailShape ASCENDING_NORTH = new RailShape("ascending_north");
    public static final /* enum */ RailShape ASCENDING_SOUTH = new RailShape("ascending_south");
    public static final /* enum */ RailShape SOUTH_EAST = new RailShape("south_east");
    public static final /* enum */ RailShape SOUTH_WEST = new RailShape("south_west");
    public static final /* enum */ RailShape NORTH_WEST = new RailShape("north_west");
    public static final /* enum */ RailShape NORTH_EAST = new RailShape("north_east");
    private final String name;
    private static final /* synthetic */ RailShape[] $VALUES;

    public static RailShape[] values() {
        return (RailShape[])$VALUES.clone();
    }

    public static RailShape valueOf(String $$0) {
        return Enum.valueOf(RailShape.class, $$0);
    }

    private RailShape(String $$0) {
        this.name = $$0;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public boolean isSlope() {
        return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ RailShape[] d() {
        return new RailShape[]{NORTH_SOUTH, EAST_WEST, ASCENDING_EAST, ASCENDING_WEST, ASCENDING_NORTH, ASCENDING_SOUTH, SOUTH_EAST, SOUTH_WEST, NORTH_WEST, NORTH_EAST};
    }

    static {
        $VALUES = RailShape.d();
    }
}

