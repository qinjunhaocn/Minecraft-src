/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.core;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public final class Direction8
extends Enum<Direction8> {
    public static final /* enum */ Direction8 NORTH = new Direction8(Direction.NORTH);
    public static final /* enum */ Direction8 NORTH_EAST = new Direction8(Direction.NORTH, Direction.EAST);
    public static final /* enum */ Direction8 EAST = new Direction8(Direction.EAST);
    public static final /* enum */ Direction8 SOUTH_EAST = new Direction8(Direction.SOUTH, Direction.EAST);
    public static final /* enum */ Direction8 SOUTH = new Direction8(Direction.SOUTH);
    public static final /* enum */ Direction8 SOUTH_WEST = new Direction8(Direction.SOUTH, Direction.WEST);
    public static final /* enum */ Direction8 WEST = new Direction8(Direction.WEST);
    public static final /* enum */ Direction8 NORTH_WEST = new Direction8(Direction.NORTH, Direction.WEST);
    private final Set<Direction> directions;
    private final Vec3i step;
    private static final /* synthetic */ Direction8[] $VALUES;

    public static Direction8[] values() {
        return (Direction8[])$VALUES.clone();
    }

    public static Direction8 valueOf(String $$0) {
        return Enum.valueOf(Direction8.class, $$0);
    }

    private Direction8(Direction ... $$0) {
        this.directions = Sets.immutableEnumSet(Arrays.asList($$0));
        this.step = new Vec3i(0, 0, 0);
        for (Direction $$1 : $$0) {
            this.step.setX(this.step.getX() + $$1.getStepX()).setY(this.step.getY() + $$1.getStepY()).setZ(this.step.getZ() + $$1.getStepZ());
        }
    }

    public Set<Direction> getDirections() {
        return this.directions;
    }

    public int getStepX() {
        return this.step.getX();
    }

    public int getStepZ() {
        return this.step.getZ();
    }

    private static /* synthetic */ Direction8[] d() {
        return new Direction8[]{NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST};
    }

    static {
        $VALUES = Direction8.d();
    }
}

