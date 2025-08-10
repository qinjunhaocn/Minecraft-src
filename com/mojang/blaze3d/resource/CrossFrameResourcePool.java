/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.resource;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.ResourceDescriptor;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

public class CrossFrameResourcePool
implements GraphicsResourceAllocator,
AutoCloseable {
    private final int framesToKeepResource;
    private final Deque<ResourceEntry<?>> pool = new ArrayDeque();

    public CrossFrameResourcePool(int $$0) {
        this.framesToKeepResource = $$0;
    }

    public void endFrame() {
        Iterator<ResourceEntry<?>> $$0 = this.pool.iterator();
        while ($$0.hasNext()) {
            ResourceEntry<?> $$1 = $$0.next();
            if ($$1.framesToLive-- != 0) continue;
            $$1.close();
            $$0.remove();
        }
    }

    @Override
    public <T> T acquire(ResourceDescriptor<T> $$0) {
        T $$1 = this.acquireWithoutPreparing($$0);
        $$0.prepare($$1);
        return $$1;
    }

    private <T> T acquireWithoutPreparing(ResourceDescriptor<T> $$0) {
        Iterator<ResourceEntry<?>> $$1 = this.pool.iterator();
        while ($$1.hasNext()) {
            ResourceEntry<?> $$2 = $$1.next();
            if (!$$0.canUsePhysicalResource($$2.descriptor)) continue;
            $$1.remove();
            return $$2.value;
        }
        return $$0.allocate();
    }

    @Override
    public <T> void release(ResourceDescriptor<T> $$0, T $$1) {
        this.pool.addFirst(new ResourceEntry<T>($$0, $$1, this.framesToKeepResource));
    }

    public void clear() {
        this.pool.forEach(ResourceEntry::close);
        this.pool.clear();
    }

    @Override
    public void close() {
        this.clear();
    }

    @VisibleForTesting
    protected Collection<ResourceEntry<?>> entries() {
        return this.pool;
    }

    @VisibleForTesting
    protected static final class ResourceEntry<T>
    implements AutoCloseable {
        final ResourceDescriptor<T> descriptor;
        final T value;
        int framesToLive;

        ResourceEntry(ResourceDescriptor<T> $$0, T $$1, int $$2) {
            this.descriptor = $$0;
            this.value = $$1;
            this.framesToLive = $$2;
        }

        @Override
        public void close() {
            this.descriptor.free(this.value);
        }
    }
}

