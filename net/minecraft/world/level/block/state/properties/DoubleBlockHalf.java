/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public final class DoubleBlockHalf
extends Enum<DoubleBlockHalf>
implements StringRepresentable {
    public static final /* enum */ DoubleBlockHalf UPPER = new DoubleBlockHalf(Direction.DOWN);
    public static final /* enum */ DoubleBlockHalf LOWER = new DoubleBlockHalf(Direction.UP);
    private final Direction directionToOther;
    private static final /* synthetic */ DoubleBlockHalf[] $VALUES;

    public static DoubleBlockHalf[] values() {
        return (DoubleBlockHalf[])$VALUES.clone();
    }

    public static DoubleBlockHalf valueOf(String $$0) {
        return Enum.valueOf(DoubleBlockHalf.class, $$0);
    }

    private DoubleBlockHalf(Direction $$0) {
        this.directionToOther = $$0;
    }

    public Direction getDirectionToOther() {
        return this.directionToOther;
    }

    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return this == UPPER ? "upper" : "lower";
    }

    public DoubleBlockHalf getOtherHalf() {
        return this == UPPER ? LOWER : UPPER;
    }

    private static /* synthetic */ DoubleBlockHalf[] d() {
        return new DoubleBlockHalf[]{UPPER, LOWER};
    }

    static {
        $VALUES = DoubleBlockHalf.d();
    }
}

