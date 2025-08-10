/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.AbstractDoubleList
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class CubePointRange
extends AbstractDoubleList {
    private final int parts;

    public CubePointRange(int $$0) {
        if ($$0 <= 0) {
            throw new IllegalArgumentException("Need at least 1 part");
        }
        this.parts = $$0;
    }

    public double getDouble(int $$0) {
        return (double)$$0 / (double)this.parts;
    }

    public int size() {
        return this.parts + 1;
    }
}

