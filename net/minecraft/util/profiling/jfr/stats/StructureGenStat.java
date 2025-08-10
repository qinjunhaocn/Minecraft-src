/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.util.profiling.jfr.stats.TimedStat;
import net.minecraft.world.level.ChunkPos;

public record StructureGenStat(Duration duration, ChunkPos chunkPos, String structureName, String level, boolean success) implements TimedStat
{
    public static StructureGenStat from(RecordedEvent $$0) {
        return new StructureGenStat($$0.getDuration(), new ChunkPos($$0.getInt("chunkPosX"), $$0.getInt("chunkPosX")), $$0.getString("structure"), $$0.getString("level"), $$0.getBoolean("success"));
    }
}

