/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public interface LevelHeightAccessor {
    public int getHeight();

    public int getMinY();

    default public int getMaxY() {
        return this.getMinY() + this.getHeight() - 1;
    }

    default public int getSectionsCount() {
        return this.getMaxSectionY() - this.getMinSectionY() + 1;
    }

    default public int getMinSectionY() {
        return SectionPos.blockToSectionCoord(this.getMinY());
    }

    default public int getMaxSectionY() {
        return SectionPos.blockToSectionCoord(this.getMaxY());
    }

    default public boolean isInsideBuildHeight(int $$0) {
        return $$0 >= this.getMinY() && $$0 <= this.getMaxY();
    }

    default public boolean isOutsideBuildHeight(BlockPos $$0) {
        return this.isOutsideBuildHeight($$0.getY());
    }

    default public boolean isOutsideBuildHeight(int $$0) {
        return $$0 < this.getMinY() || $$0 > this.getMaxY();
    }

    default public int getSectionIndex(int $$0) {
        return this.getSectionIndexFromSectionY(SectionPos.blockToSectionCoord($$0));
    }

    default public int getSectionIndexFromSectionY(int $$0) {
        return $$0 - this.getMinSectionY();
    }

    default public int getSectionYFromSectionIndex(int $$0) {
        return $$0 + this.getMinSectionY();
    }

    public static LevelHeightAccessor create(final int $$0, final int $$1) {
        return new LevelHeightAccessor(){

            @Override
            public int getHeight() {
                return $$1;
            }

            @Override
            public int getMinY() {
                return $$0;
            }
        };
    }
}

