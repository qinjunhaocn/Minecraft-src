/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer;

public class RunningTrimmedMean {
    private final long[] values;
    private int count;
    private int cursor;

    public RunningTrimmedMean(int $$0) {
        this.values = new long[$$0];
    }

    public long registerValueAndGetMean(long $$0) {
        if (this.count < this.values.length) {
            ++this.count;
        }
        this.values[this.cursor] = $$0;
        this.cursor = (this.cursor + 1) % this.values.length;
        long $$1 = Long.MAX_VALUE;
        long $$2 = Long.MIN_VALUE;
        long $$3 = 0L;
        for (int $$4 = 0; $$4 < this.count; ++$$4) {
            long $$5 = this.values[$$4];
            $$3 += $$5;
            $$1 = Math.min($$1, $$5);
            $$2 = Math.max($$2, $$5);
        }
        if (this.count > 2) {
            return ($$3 -= $$1 + $$2) / (long)(this.count - 2);
        }
        if ($$3 > 0L) {
            return (long)this.count / $$3;
        }
        return 0L;
    }
}

