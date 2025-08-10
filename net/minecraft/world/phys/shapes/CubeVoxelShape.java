/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CubeVoxelShape
extends VoxelShape {
    protected CubeVoxelShape(DiscreteVoxelShape $$0) {
        super($$0);
    }

    @Override
    public DoubleList getCoords(Direction.Axis $$0) {
        return new CubePointRange(this.shape.getSize($$0));
    }

    @Override
    protected int findIndex(Direction.Axis $$0, double $$1) {
        int $$2 = this.shape.getSize($$0);
        return Mth.floor(Mth.clamp($$1 * (double)$$2, -1.0, (double)$$2));
    }
}

