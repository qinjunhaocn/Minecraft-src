/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  java.lang.MatchException
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ArrayVoxelShape
extends VoxelShape {
    private final DoubleList xs;
    private final DoubleList ys;
    private final DoubleList zs;

    protected ArrayVoxelShape(DiscreteVoxelShape $$0, double[] $$1, double[] $$2, double[] $$3) {
        this($$0, (DoubleList)DoubleArrayList.wrap((double[])Arrays.copyOf($$1, $$0.getXSize() + 1)), (DoubleList)DoubleArrayList.wrap((double[])Arrays.copyOf($$2, $$0.getYSize() + 1)), (DoubleList)DoubleArrayList.wrap((double[])Arrays.copyOf($$3, $$0.getZSize() + 1)));
    }

    ArrayVoxelShape(DiscreteVoxelShape $$0, DoubleList $$1, DoubleList $$2, DoubleList $$3) {
        super($$0);
        int $$4 = $$0.getXSize() + 1;
        int $$5 = $$0.getYSize() + 1;
        int $$6 = $$0.getZSize() + 1;
        if ($$4 != $$1.size() || $$5 != $$2.size() || $$6 != $$3.size()) {
            throw Util.pauseInIde(new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape."));
        }
        this.xs = $$1;
        this.ys = $$2;
        this.zs = $$3;
    }

    @Override
    public DoubleList getCoords(Direction.Axis $$0) {
        return switch ($$0) {
            default -> throw new MatchException(null, null);
            case Direction.Axis.X -> this.xs;
            case Direction.Axis.Y -> this.ys;
            case Direction.Axis.Z -> this.zs;
        };
    }
}

