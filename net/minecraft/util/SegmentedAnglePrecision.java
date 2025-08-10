/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util;

import net.minecraft.core.Direction;

public class SegmentedAnglePrecision {
    private final int mask;
    private final int precision;
    private final float degreeToAngle;
    private final float angleToDegree;

    public SegmentedAnglePrecision(int $$0) {
        if ($$0 < 2) {
            throw new IllegalArgumentException("Precision cannot be less than 2 bits");
        }
        if ($$0 > 30) {
            throw new IllegalArgumentException("Precision cannot be greater than 30 bits");
        }
        int $$1 = 1 << $$0;
        this.mask = $$1 - 1;
        this.precision = $$0;
        this.degreeToAngle = (float)$$1 / 360.0f;
        this.angleToDegree = 360.0f / (float)$$1;
    }

    public boolean isSameAxis(int $$0, int $$1) {
        int $$2 = this.getMask() >> 1;
        return ($$0 & $$2) == ($$1 & $$2);
    }

    public int fromDirection(Direction $$0) {
        if ($$0.getAxis().isVertical()) {
            return 0;
        }
        int $$1 = $$0.get2DDataValue();
        return $$1 << this.precision - 2;
    }

    public int fromDegreesWithTurns(float $$0) {
        return Math.round($$0 * this.degreeToAngle);
    }

    public int fromDegrees(float $$0) {
        return this.normalize(this.fromDegreesWithTurns($$0));
    }

    public float toDegreesWithTurns(int $$0) {
        return (float)$$0 * this.angleToDegree;
    }

    public float toDegrees(int $$0) {
        float $$1 = this.toDegreesWithTurns(this.normalize($$0));
        return $$1 >= 180.0f ? $$1 - 360.0f : $$1;
    }

    public int normalize(int $$0) {
        return $$0 & this.mask;
    }

    public int getMask() {
        return this.mask;
    }
}

