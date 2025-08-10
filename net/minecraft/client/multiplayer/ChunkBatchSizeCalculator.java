/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.multiplayer;

import net.minecraft.Util;
import net.minecraft.util.Mth;

public class ChunkBatchSizeCalculator {
    private static final int MAX_OLD_SAMPLES_WEIGHT = 49;
    private static final int CLAMP_COEFFICIENT = 3;
    private double aggregatedNanosPerChunk = 2000000.0;
    private int oldSamplesWeight = 1;
    private volatile long chunkBatchStartTime = Util.getNanos();

    public void onBatchStart() {
        this.chunkBatchStartTime = Util.getNanos();
    }

    public void onBatchFinished(int $$0) {
        if ($$0 > 0) {
            double $$1 = Util.getNanos() - this.chunkBatchStartTime;
            double $$2 = $$1 / (double)$$0;
            double $$3 = Mth.clamp($$2, this.aggregatedNanosPerChunk / 3.0, this.aggregatedNanosPerChunk * 3.0);
            this.aggregatedNanosPerChunk = (this.aggregatedNanosPerChunk * (double)this.oldSamplesWeight + $$3) / (double)(this.oldSamplesWeight + 1);
            this.oldSamplesWeight = Math.min(49, this.oldSamplesWeight + 1);
        }
    }

    public float getDesiredChunksPerTick() {
        return (float)(7000000.0 / this.aggregatedNanosPerChunk);
    }
}

