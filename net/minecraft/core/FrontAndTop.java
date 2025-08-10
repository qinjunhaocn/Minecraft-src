/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.core;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public final class FrontAndTop
extends Enum<FrontAndTop>
implements StringRepresentable {
    public static final /* enum */ FrontAndTop DOWN_EAST = new FrontAndTop("down_east", Direction.DOWN, Direction.EAST);
    public static final /* enum */ FrontAndTop DOWN_NORTH = new FrontAndTop("down_north", Direction.DOWN, Direction.NORTH);
    public static final /* enum */ FrontAndTop DOWN_SOUTH = new FrontAndTop("down_south", Direction.DOWN, Direction.SOUTH);
    public static final /* enum */ FrontAndTop DOWN_WEST = new FrontAndTop("down_west", Direction.DOWN, Direction.WEST);
    public static final /* enum */ FrontAndTop UP_EAST = new FrontAndTop("up_east", Direction.UP, Direction.EAST);
    public static final /* enum */ FrontAndTop UP_NORTH = new FrontAndTop("up_north", Direction.UP, Direction.NORTH);
    public static final /* enum */ FrontAndTop UP_SOUTH = new FrontAndTop("up_south", Direction.UP, Direction.SOUTH);
    public static final /* enum */ FrontAndTop UP_WEST = new FrontAndTop("up_west", Direction.UP, Direction.WEST);
    public static final /* enum */ FrontAndTop WEST_UP = new FrontAndTop("west_up", Direction.WEST, Direction.UP);
    public static final /* enum */ FrontAndTop EAST_UP = new FrontAndTop("east_up", Direction.EAST, Direction.UP);
    public static final /* enum */ FrontAndTop NORTH_UP = new FrontAndTop("north_up", Direction.NORTH, Direction.UP);
    public static final /* enum */ FrontAndTop SOUTH_UP = new FrontAndTop("south_up", Direction.SOUTH, Direction.UP);
    private static final int NUM_DIRECTIONS;
    private static final FrontAndTop[] BY_TOP_FRONT;
    private final String name;
    private final Direction top;
    private final Direction front;
    private static final /* synthetic */ FrontAndTop[] $VALUES;

    public static FrontAndTop[] values() {
        return (FrontAndTop[])$VALUES.clone();
    }

    public static FrontAndTop valueOf(String $$0) {
        return Enum.valueOf(FrontAndTop.class, $$0);
    }

    private static int lookupKey(Direction $$0, Direction $$1) {
        return $$0.ordinal() * NUM_DIRECTIONS + $$1.ordinal();
    }

    private FrontAndTop(String $$0, Direction $$1, Direction $$2) {
        this.name = $$0;
        this.front = $$1;
        this.top = $$2;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public static FrontAndTop fromFrontAndTop(Direction $$0, Direction $$1) {
        return BY_TOP_FRONT[FrontAndTop.lookupKey($$0, $$1)];
    }

    public Direction front() {
        return this.front;
    }

    public Direction top() {
        return this.top;
    }

    private static /* synthetic */ FrontAndTop[] d() {
        return new FrontAndTop[]{DOWN_EAST, DOWN_NORTH, DOWN_SOUTH, DOWN_WEST, UP_EAST, UP_NORTH, UP_SOUTH, UP_WEST, WEST_UP, EAST_UP, NORTH_UP, SOUTH_UP};
    }

    static {
        $VALUES = FrontAndTop.d();
        NUM_DIRECTIONS = Direction.values().length;
        BY_TOP_FRONT = Util.make(new FrontAndTop[NUM_DIRECTIONS * NUM_DIRECTIONS], $$0 -> {
            FrontAndTop[] frontAndTopArray = FrontAndTop.values();
            int n = frontAndTopArray.length;
            for (int i = 0; i < n; ++i) {
                FrontAndTop $$1;
                $$0[FrontAndTop.lookupKey((Direction)$$1.front, (Direction)$$1.top)] = $$1 = frontAndTopArray[i];
            }
        });
    }
}

