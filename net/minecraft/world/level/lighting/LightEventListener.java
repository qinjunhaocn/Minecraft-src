/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;

public interface LightEventListener {
    public void checkBlock(BlockPos var1);

    public boolean hasLightWork();

    public int runLightUpdates();

    default public void updateSectionStatus(BlockPos $$0, boolean $$1) {
        this.updateSectionStatus(SectionPos.of($$0), $$1);
    }

    public void updateSectionStatus(SectionPos var1, boolean var2);

    public void setLightEnabled(ChunkPos var1, boolean var2);

    public void propagateLightSources(ChunkPos var1);
}

