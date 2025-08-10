/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public interface ChunkProgressListener {
    public void updateSpawnPos(ChunkPos var1);

    public void onStatusChange(ChunkPos var1, @Nullable ChunkStatus var2);

    public void start();

    public void stop();

    public static int calculateDiameter(int $$0) {
        return 2 * $$0 + 1;
    }
}

