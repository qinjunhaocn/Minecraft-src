/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.resource;

import com.mojang.blaze3d.resource.ResourceDescriptor;

public interface GraphicsResourceAllocator {
    public static final GraphicsResourceAllocator UNPOOLED = new GraphicsResourceAllocator(){

        @Override
        public <T> T acquire(ResourceDescriptor<T> $$0) {
            T $$1 = $$0.allocate();
            $$0.prepare($$1);
            return $$1;
        }

        @Override
        public <T> void release(ResourceDescriptor<T> $$0, T $$1) {
            $$0.free($$1);
        }
    };

    public <T> T acquire(ResourceDescriptor<T> var1);

    public <T> void release(ResourceDescriptor<T> var1, T var2);
}

