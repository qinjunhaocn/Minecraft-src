/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.gui.navigation;

import net.minecraft.client.gui.navigation.ScreenDirection;

public final class ScreenAxis
extends Enum<ScreenAxis> {
    public static final /* enum */ ScreenAxis HORIZONTAL = new ScreenAxis();
    public static final /* enum */ ScreenAxis VERTICAL = new ScreenAxis();
    private static final /* synthetic */ ScreenAxis[] $VALUES;

    public static ScreenAxis[] values() {
        return (ScreenAxis[])$VALUES.clone();
    }

    public static ScreenAxis valueOf(String $$0) {
        return Enum.valueOf(ScreenAxis.class, $$0);
    }

    public ScreenAxis orthogonal() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> VERTICAL;
            case 1 -> HORIZONTAL;
        };
    }

    public ScreenDirection getPositive() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> ScreenDirection.RIGHT;
            case 1 -> ScreenDirection.DOWN;
        };
    }

    public ScreenDirection getNegative() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> ScreenDirection.LEFT;
            case 1 -> ScreenDirection.UP;
        };
    }

    public ScreenDirection getDirection(boolean $$0) {
        return $$0 ? this.getPositive() : this.getNegative();
    }

    private static /* synthetic */ ScreenAxis[] d() {
        return new ScreenAxis[]{HORIZONTAL, VERTICAL};
    }

    static {
        $VALUES = ScreenAxis.d();
    }
}

