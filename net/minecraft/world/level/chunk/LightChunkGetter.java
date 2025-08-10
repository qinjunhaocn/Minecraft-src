/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LightChunk;

public interface LightChunkGetter {
    @Nullable
    public LightChunk getChunkForLighting(int var1, int var2);

    default public void onLightUpdate(LightLayer $$0, SectionPos $$1) {
    }

    public BlockGetter getLevel();
}

