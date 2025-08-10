/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.level.progress;

import net.minecraft.server.level.progress.ChunkProgressListener;

public interface ChunkProgressListenerFactory {
    public ChunkProgressListener create(int var1);
}

