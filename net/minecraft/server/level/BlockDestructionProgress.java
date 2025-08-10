/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.level;

import net.minecraft.core.BlockPos;

public class BlockDestructionProgress
implements Comparable<BlockDestructionProgress> {
    private final int id;
    private final BlockPos pos;
    private int progress;
    private int updatedRenderTick;

    public BlockDestructionProgress(int $$0, BlockPos $$1) {
        this.id = $$0;
        this.pos = $$1;
    }

    public int getId() {
        return this.id;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setProgress(int $$0) {
        if ($$0 > 10) {
            $$0 = 10;
        }
        this.progress = $$0;
    }

    public int getProgress() {
        return this.progress;
    }

    public void updateTick(int $$0) {
        this.updatedRenderTick = $$0;
    }

    public int getUpdatedRenderTick() {
        return this.updatedRenderTick;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || this.getClass() != $$0.getClass()) {
            return false;
        }
        BlockDestructionProgress $$1 = (BlockDestructionProgress)$$0;
        return this.id == $$1.id;
    }

    public int hashCode() {
        return Integer.hashCode(this.id);
    }

    @Override
    public int compareTo(BlockDestructionProgress $$0) {
        if (this.progress != $$0.progress) {
            return Integer.compare(this.progress, $$0.progress);
        }
        return Integer.compare(this.id, $$0.id);
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((BlockDestructionProgress)object);
    }
}

