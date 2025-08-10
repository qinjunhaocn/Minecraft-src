/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.inventory;

import net.minecraft.world.inventory.ContainerData;

public abstract class DataSlot {
    private int prevValue;

    public static DataSlot forContainer(final ContainerData $$0, final int $$1) {
        return new DataSlot(){

            @Override
            public int get() {
                return $$0.get($$1);
            }

            @Override
            public void set(int $$02) {
                $$0.set($$1, $$02);
            }
        };
    }

    public static DataSlot a(final int[] $$0, final int $$1) {
        return new DataSlot(){

            @Override
            public int get() {
                return $$0[$$1];
            }

            @Override
            public void set(int $$02) {
                $$0[$$1] = $$02;
            }
        };
    }

    public static DataSlot standalone() {
        return new DataSlot(){
            private int value;

            @Override
            public int get() {
                return this.value;
            }

            @Override
            public void set(int $$0) {
                this.value = $$0;
            }
        };
    }

    public abstract int get();

    public abstract void set(int var1);

    public boolean checkAndClearUpdateFlag() {
        int $$0 = this.get();
        boolean $$1 = $$0 != this.prevValue;
        this.prevValue = $$0;
        return $$1;
    }
}

