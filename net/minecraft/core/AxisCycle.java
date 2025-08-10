/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.core;

import net.minecraft.core.Direction;

public abstract sealed class AxisCycle
extends Enum<AxisCycle> {
    public static final /* enum */ AxisCycle NONE = new AxisCycle(){

        @Override
        public int cycle(int $$0, int $$1, int $$2, Direction.Axis $$3) {
            return $$3.choose($$0, $$1, $$2);
        }

        @Override
        public double cycle(double $$0, double $$1, double $$2, Direction.Axis $$3) {
            return $$3.choose($$0, $$1, $$2);
        }

        @Override
        public Direction.Axis cycle(Direction.Axis $$0) {
            return $$0;
        }

        @Override
        public AxisCycle inverse() {
            return this;
        }
    };
    public static final /* enum */ AxisCycle FORWARD = new AxisCycle(){

        @Override
        public int cycle(int $$0, int $$1, int $$2, Direction.Axis $$3) {
            return $$3.choose($$2, $$0, $$1);
        }

        @Override
        public double cycle(double $$0, double $$1, double $$2, Direction.Axis $$3) {
            return $$3.choose($$2, $$0, $$1);
        }

        @Override
        public Direction.Axis cycle(Direction.Axis $$0) {
            return AXIS_VALUES[Math.floorMod($$0.ordinal() + 1, 3)];
        }

        @Override
        public AxisCycle inverse() {
            return BACKWARD;
        }
    };
    public static final /* enum */ AxisCycle BACKWARD = new AxisCycle(){

        @Override
        public int cycle(int $$0, int $$1, int $$2, Direction.Axis $$3) {
            return $$3.choose($$1, $$2, $$0);
        }

        @Override
        public double cycle(double $$0, double $$1, double $$2, Direction.Axis $$3) {
            return $$3.choose($$1, $$2, $$0);
        }

        @Override
        public Direction.Axis cycle(Direction.Axis $$0) {
            return AXIS_VALUES[Math.floorMod($$0.ordinal() - 1, 3)];
        }

        @Override
        public AxisCycle inverse() {
            return FORWARD;
        }
    };
    public static final Direction.Axis[] AXIS_VALUES;
    public static final AxisCycle[] VALUES;
    private static final /* synthetic */ AxisCycle[] $VALUES;

    public static AxisCycle[] values() {
        return (AxisCycle[])$VALUES.clone();
    }

    public static AxisCycle valueOf(String $$0) {
        return Enum.valueOf(AxisCycle.class, $$0);
    }

    public abstract int cycle(int var1, int var2, int var3, Direction.Axis var4);

    public abstract double cycle(double var1, double var3, double var5, Direction.Axis var7);

    public abstract Direction.Axis cycle(Direction.Axis var1);

    public abstract AxisCycle inverse();

    public static AxisCycle between(Direction.Axis $$0, Direction.Axis $$1) {
        return VALUES[Math.floorMod($$1.ordinal() - $$0.ordinal(), 3)];
    }

    private static /* synthetic */ AxisCycle[] b() {
        return new AxisCycle[]{NONE, FORWARD, BACKWARD};
    }

    static {
        $VALUES = AxisCycle.b();
        AXIS_VALUES = Direction.Axis.values();
        VALUES = AxisCycle.values();
    }
}

