/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.client;

import net.minecraft.Util;

public class UploadStatus {
    private volatile long bytesWritten;
    private volatile long totalBytes;
    private long previousTimeSnapshot = Util.getMillis();
    private long previousBytesWritten;
    private long bytesPerSecond;

    public void setTotalBytes(long $$0) {
        this.totalBytes = $$0;
    }

    public long getTotalBytes() {
        return this.totalBytes;
    }

    public long getBytesWritten() {
        return this.bytesWritten;
    }

    public void onWrite(long $$0) {
        this.bytesWritten += $$0;
    }

    public boolean uploadStarted() {
        return this.bytesWritten != 0L;
    }

    public boolean uploadCompleted() {
        return this.bytesWritten == this.getTotalBytes();
    }

    public double getPercentage() {
        return Math.min((double)this.getBytesWritten() / (double)this.getTotalBytes(), 1.0);
    }

    public void refreshBytesPerSecond() {
        long $$0 = Util.getMillis();
        long $$1 = $$0 - this.previousTimeSnapshot;
        if ($$1 < 1000L) {
            return;
        }
        long $$2 = this.bytesWritten;
        this.bytesPerSecond = 1000L * ($$2 - this.previousBytesWritten) / $$1;
        this.previousBytesWritten = $$2;
        this.previousTimeSnapshot = $$0;
    }

    public long getBytesPerSecond() {
        return this.bytesPerSecond;
    }
}

