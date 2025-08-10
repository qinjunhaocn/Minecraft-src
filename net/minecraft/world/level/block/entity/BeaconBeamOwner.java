/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity;

import java.util.List;

public interface BeaconBeamOwner {
    public List<Section> getBeamSections();

    public static class Section {
        private final int color;
        private int height;

        public Section(int $$0) {
            this.color = $$0;
            this.height = 1;
        }

        public void increaseHeight() {
            ++this.height;
        }

        public int getColor() {
            return this.color;
        }

        public int getHeight() {
            return this.height;
        }
    }
}

