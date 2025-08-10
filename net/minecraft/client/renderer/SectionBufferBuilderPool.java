/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import org.slf4j.Logger;

public class SectionBufferBuilderPool {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Queue<SectionBufferBuilderPack> freeBuffers;
    private volatile int freeBufferCount;

    private SectionBufferBuilderPool(List<SectionBufferBuilderPack> $$0) {
        this.freeBuffers = Queues.newArrayDeque($$0);
        this.freeBufferCount = this.freeBuffers.size();
    }

    public static SectionBufferBuilderPool allocate(int $$0) {
        int $$1 = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / SectionBufferBuilderPack.TOTAL_BUFFERS_SIZE);
        int $$2 = Math.max(1, Math.min($$0, $$1));
        ArrayList<SectionBufferBuilderPack> $$3 = new ArrayList<SectionBufferBuilderPack>($$2);
        try {
            for (int $$4 = 0; $$4 < $$2; ++$$4) {
                $$3.add(new SectionBufferBuilderPack());
            }
        } catch (OutOfMemoryError $$5) {
            LOGGER.warn("Allocated only {}/{} buffers", (Object)$$3.size(), (Object)$$2);
            int $$6 = Math.min($$3.size() * 2 / 3, $$3.size() - 1);
            for (int $$7 = 0; $$7 < $$6; ++$$7) {
                ((SectionBufferBuilderPack)$$3.remove($$3.size() - 1)).close();
            }
        }
        return new SectionBufferBuilderPool($$3);
    }

    @Nullable
    public SectionBufferBuilderPack acquire() {
        SectionBufferBuilderPack $$0 = this.freeBuffers.poll();
        if ($$0 != null) {
            this.freeBufferCount = this.freeBuffers.size();
            return $$0;
        }
        return null;
    }

    public void release(SectionBufferBuilderPack $$0) {
        this.freeBuffers.add($$0);
        this.freeBufferCount = this.freeBuffers.size();
    }

    public boolean isEmpty() {
        return this.freeBuffers.isEmpty();
    }

    public int getFreeBufferCount() {
        return this.freeBufferCount;
    }
}

