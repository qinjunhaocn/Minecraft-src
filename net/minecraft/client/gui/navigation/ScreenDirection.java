/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntComparator
 *  java.lang.MatchException
 */
package net.minecraft.client.gui.navigation;

import it.unimi.dsi.fastutil.ints.IntComparator;
import net.minecraft.client.gui.navigation.ScreenAxis;

public final class ScreenDirection
extends Enum<ScreenDirection> {
    public static final /* enum */ ScreenDirection UP = new ScreenDirection();
    public static final /* enum */ ScreenDirection DOWN = new ScreenDirection();
    public static final /* enum */ ScreenDirection LEFT = new ScreenDirection();
    public static final /* enum */ ScreenDirection RIGHT = new ScreenDirection();
    private final IntComparator coordinateValueComparator = ($$0, $$1) -> $$0 == $$1 ? 0 : (this.isBefore($$0, $$1) ? -1 : 1);
    private static final /* synthetic */ ScreenDirection[] $VALUES;

    public static ScreenDirection[] values() {
        return (ScreenDirection[])$VALUES.clone();
    }

    public static ScreenDirection valueOf(String $$0) {
        return Enum.valueOf(ScreenDirection.class, $$0);
    }

    public ScreenAxis getAxis() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0, 1 -> ScreenAxis.VERTICAL;
            case 2, 3 -> ScreenAxis.HORIZONTAL;
        };
    }

    public ScreenDirection getOpposite() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> DOWN;
            case 1 -> UP;
            case 2 -> RIGHT;
            case 3 -> LEFT;
        };
    }

    public boolean isPositive() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0, 2 -> false;
            case 1, 3 -> true;
        };
    }

    public boolean isAfter(int $$0, int $$1) {
        if (this.isPositive()) {
            return $$0 > $$1;
        }
        return $$1 > $$0;
    }

    public boolean isBefore(int $$0, int $$1) {
        if (this.isPositive()) {
            return $$0 < $$1;
        }
        return $$1 < $$0;
    }

    public IntComparator coordinateValueComparator() {
        return this.coordinateValueComparator;
    }

    private static /* synthetic */ ScreenDirection[] e() {
        return new ScreenDirection[]{UP, DOWN, LEFT, RIGHT};
    }

    static {
        $VALUES = ScreenDirection.e();
    }
}

