/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.phys.shapes;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public final class SubShape
extends DiscreteVoxelShape {
    private final DiscreteVoxelShape parent;
    private final int startX;
    private final int startY;
    private final int startZ;
    private final int endX;
    private final int endY;
    private final int endZ;

    protected SubShape(DiscreteVoxelShape $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6) {
        super($$4 - $$1, $$5 - $$2, $$6 - $$3);
        this.parent = $$0;
        this.startX = $$1;
        this.startY = $$2;
        this.startZ = $$3;
        this.endX = $$4;
        this.endY = $$5;
        this.endZ = $$6;
    }

    @Override
    public boolean isFull(int $$0, int $$1, int $$2) {
        return this.parent.isFull(this.startX + $$0, this.startY + $$1, this.startZ + $$2);
    }

    @Override
    public void fill(int $$0, int $$1, int $$2) {
        this.parent.fill(this.startX + $$0, this.startY + $$1, this.startZ + $$2);
    }

    @Override
    public int firstFull(Direction.Axis $$0) {
        return this.clampToShape($$0, this.parent.firstFull($$0));
    }

    @Override
    public int lastFull(Direction.Axis $$0) {
        return this.clampToShape($$0, this.parent.lastFull($$0));
    }

    private int clampToShape(Direction.Axis $$0, int $$1) {
        int $$2 = $$0.choose(this.startX, this.startY, this.startZ);
        int $$3 = $$0.choose(this.endX, this.endY, this.endZ);
        return Mth.clamp($$1, $$2, $$3) - $$2;
    }
}

