/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.inventory;

import net.minecraft.world.inventory.ContainerData;

public class SimpleContainerData
implements ContainerData {
    private final int[] ints;

    public SimpleContainerData(int $$0) {
        this.ints = new int[$$0];
    }

    @Override
    public int get(int $$0) {
        return this.ints[$$0];
    }

    @Override
    public void set(int $$0, int $$1) {
        this.ints[$$0] = $$1;
    }

    @Override
    public int getCount() {
        return this.ints.length;
    }
}

