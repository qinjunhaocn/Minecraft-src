/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.framegraph;

import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.ResourceDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class FrameGraphBuilder {
    private final List<InternalVirtualResource<?>> internalResources = new ArrayList();
    private final List<ExternalResource<?>> externalResources = new ArrayList();
    private final List<Pass> passes = new ArrayList<Pass>();

    public FramePass addPass(String $$0) {
        Pass $$1 = new Pass(this.passes.size(), $$0);
        this.passes.add($$1);
        return $$1;
    }

    public <T> ResourceHandle<T> importExternal(String $$0, T $$1) {
        ExternalResource<T> $$2 = new ExternalResource<T>($$0, null, $$1);
        this.externalResources.add($$2);
        return $$2.handle;
    }

    public <T> ResourceHandle<T> createInternal(String $$0, ResourceDescriptor<T> $$1) {
        return this.createInternalResource((String)$$0, $$1, null).handle;
    }

    <T> InternalVirtualResource<T> createInternalResource(String $$0, ResourceDescriptor<T> $$1, @Nullable Pass $$2) {
        int $$3 = this.internalResources.size();
        InternalVirtualResource<T> $$4 = new InternalVirtualResource<T>($$3, $$0, $$2, $$1);
        this.internalResources.add($$4);
        return $$4;
    }

    public void execute(GraphicsResourceAllocator $$0) {
        this.execute($$0, Inspector.NONE);
    }

    public void execute(GraphicsResourceAllocator $$0, Inspector $$1) {
        BitSet $$2 = this.identifyPassesToKeep();
        ArrayList<Pass> $$3 = new ArrayList<Pass>($$2.cardinality());
        BitSet $$4 = new BitSet(this.passes.size());
        for (Pass $$5 : this.passes) {
            this.resolvePassOrder($$5, $$2, $$4, $$3);
        }
        this.assignResourceLifetimes($$3);
        for (Pass $$6 : $$3) {
            for (InternalVirtualResource<?> $$7 : $$6.resourcesToAcquire) {
                $$1.acquireResource($$7.name);
                $$7.acquire($$0);
            }
            $$1.beforeExecutePass($$6.name);
            $$6.task.run();
            $$1.afterExecutePass($$6.name);
            int $$8 = $$6.resourcesToRelease.nextSetBit(0);
            while ($$8 >= 0) {
                InternalVirtualResource<?> $$9 = this.internalResources.get($$8);
                $$1.releaseResource($$9.name);
                $$9.release($$0);
                $$8 = $$6.resourcesToRelease.nextSetBit($$8 + 1);
            }
        }
    }

    private BitSet identifyPassesToKeep() {
        ArrayDeque<Pass> $$0 = new ArrayDeque<Pass>(this.passes.size());
        BitSet $$1 = new BitSet(this.passes.size());
        for (VirtualResource virtualResource : this.externalResources) {
            Pass $$3 = virtualResource.handle.createdBy;
            if ($$3 == null) continue;
            this.discoverAllRequiredPasses($$3, $$1, $$0);
        }
        for (Pass pass : this.passes) {
            if (!pass.disableCulling) continue;
            this.discoverAllRequiredPasses(pass, $$1, $$0);
        }
        return $$1;
    }

    private void discoverAllRequiredPasses(Pass $$0, BitSet $$1, Deque<Pass> $$2) {
        $$2.add($$0);
        while (!$$2.isEmpty()) {
            Pass $$3 = $$2.poll();
            if ($$1.get($$3.id)) continue;
            $$1.set($$3.id);
            int $$4 = $$3.requiredPassIds.nextSetBit(0);
            while ($$4 >= 0) {
                $$2.add(this.passes.get($$4));
                $$4 = $$3.requiredPassIds.nextSetBit($$4 + 1);
            }
        }
    }

    private void resolvePassOrder(Pass $$02, BitSet $$1, BitSet $$2, List<Pass> $$3) {
        if ($$2.get($$02.id)) {
            String $$4 = $$2.stream().mapToObj($$0 -> this.passes.get((int)$$0).name).collect(Collectors.joining(", "));
            throw new IllegalStateException("Frame graph cycle detected between " + $$4);
        }
        if (!$$1.get($$02.id)) {
            return;
        }
        $$2.set($$02.id);
        $$1.clear($$02.id);
        int $$5 = $$02.requiredPassIds.nextSetBit(0);
        while ($$5 >= 0) {
            this.resolvePassOrder(this.passes.get($$5), $$1, $$2, $$3);
            $$5 = $$02.requiredPassIds.nextSetBit($$5 + 1);
        }
        for (Handle<?> $$6 : $$02.writesFrom) {
            int $$7 = $$6.readBy.nextSetBit(0);
            while ($$7 >= 0) {
                if ($$7 != $$02.id) {
                    this.resolvePassOrder(this.passes.get($$7), $$1, $$2, $$3);
                }
                $$7 = $$6.readBy.nextSetBit($$7 + 1);
            }
        }
        $$3.add($$02);
        $$2.clear($$02.id);
    }

    private void assignResourceLifetimes(Collection<Pass> $$0) {
        Pass[] $$1 = new Pass[this.internalResources.size()];
        for (Pass $$2 : $$0) {
            int $$3 = $$2.requiredResourceIds.nextSetBit(0);
            while ($$3 >= 0) {
                InternalVirtualResource<?> $$4 = this.internalResources.get($$3);
                Pass $$5 = $$1[$$3];
                $$1[$$3] = $$2;
                if ($$5 == null) {
                    $$2.resourcesToAcquire.add($$4);
                } else {
                    $$5.resourcesToRelease.clear($$3);
                }
                $$2.resourcesToRelease.set($$3);
                $$3 = $$2.requiredResourceIds.nextSetBit($$3 + 1);
            }
        }
    }

    class Pass
    implements FramePass {
        final int id;
        final String name;
        final List<Handle<?>> writesFrom = new ArrayList();
        final BitSet requiredResourceIds = new BitSet();
        final BitSet requiredPassIds = new BitSet();
        Runnable task = () -> {};
        final List<InternalVirtualResource<?>> resourcesToAcquire = new ArrayList();
        final BitSet resourcesToRelease = new BitSet();
        boolean disableCulling;

        public Pass(int $$0, String $$1) {
            this.id = $$0;
            this.name = $$1;
        }

        private <T> void markResourceRequired(Handle<T> $$0) {
            VirtualResource virtualResource = $$0.holder;
            if (virtualResource instanceof InternalVirtualResource) {
                InternalVirtualResource $$1 = (InternalVirtualResource)virtualResource;
                this.requiredResourceIds.set($$1.id);
            }
        }

        private void markPassRequired(Pass $$0) {
            this.requiredPassIds.set($$0.id);
        }

        @Override
        public <T> ResourceHandle<T> createsInternal(String $$0, ResourceDescriptor<T> $$1) {
            InternalVirtualResource<T> $$2 = FrameGraphBuilder.this.createInternalResource($$0, $$1, this);
            this.requiredResourceIds.set($$2.id);
            return $$2.handle;
        }

        @Override
        public <T> void reads(ResourceHandle<T> $$0) {
            this._reads((Handle)$$0);
        }

        private <T> void _reads(Handle<T> $$0) {
            this.markResourceRequired($$0);
            if ($$0.createdBy != null) {
                this.markPassRequired($$0.createdBy);
            }
            $$0.readBy.set(this.id);
        }

        @Override
        public <T> ResourceHandle<T> readsAndWrites(ResourceHandle<T> $$0) {
            return this._readsAndWrites((Handle)$$0);
        }

        @Override
        public void requires(FramePass $$0) {
            this.requiredPassIds.set(((Pass)$$0).id);
        }

        @Override
        public void disableCulling() {
            this.disableCulling = true;
        }

        private <T> Handle<T> _readsAndWrites(Handle<T> $$0) {
            this.writesFrom.add($$0);
            this._reads($$0);
            return $$0.writeAndAlias(this);
        }

        @Override
        public void executes(Runnable $$0) {
            this.task = $$0;
        }

        public String toString() {
            return this.name;
        }
    }

    static class ExternalResource<T>
    extends VirtualResource<T> {
        private final T resource;

        public ExternalResource(String $$0, @Nullable Pass $$1, T $$2) {
            super($$0, $$1);
            this.resource = $$2;
        }

        @Override
        public T get() {
            return this.resource;
        }
    }

    static class Handle<T>
    implements ResourceHandle<T> {
        final VirtualResource<T> holder;
        private final int version;
        @Nullable
        final Pass createdBy;
        final BitSet readBy = new BitSet();
        @Nullable
        private Handle<T> aliasedBy;

        Handle(VirtualResource<T> $$0, int $$1, @Nullable Pass $$2) {
            this.holder = $$0;
            this.version = $$1;
            this.createdBy = $$2;
        }

        @Override
        public T get() {
            return this.holder.get();
        }

        Handle<T> writeAndAlias(Pass $$0) {
            if (this.holder.handle != this) {
                throw new IllegalStateException("Handle " + String.valueOf(this) + " is no longer valid, as its contents were moved into " + String.valueOf(this.aliasedBy));
            }
            Handle<T> $$1 = new Handle<T>(this.holder, this.version + 1, $$0);
            this.holder.handle = $$1;
            this.aliasedBy = $$1;
            return $$1;
        }

        public String toString() {
            if (this.createdBy != null) {
                return String.valueOf(this.holder) + "#" + this.version + " (from " + String.valueOf(this.createdBy) + ")";
            }
            return String.valueOf(this.holder) + "#" + this.version;
        }
    }

    static class InternalVirtualResource<T>
    extends VirtualResource<T> {
        final int id;
        private final ResourceDescriptor<T> descriptor;
        @Nullable
        private T physicalResource;

        public InternalVirtualResource(int $$0, String $$1, @Nullable Pass $$2, ResourceDescriptor<T> $$3) {
            super($$1, $$2);
            this.id = $$0;
            this.descriptor = $$3;
        }

        @Override
        public T get() {
            return Objects.requireNonNull(this.physicalResource, "Resource is not currently available");
        }

        public void acquire(GraphicsResourceAllocator $$0) {
            if (this.physicalResource != null) {
                throw new IllegalStateException("Tried to acquire physical resource, but it was already assigned");
            }
            this.physicalResource = $$0.acquire(this.descriptor);
        }

        public void release(GraphicsResourceAllocator $$0) {
            if (this.physicalResource == null) {
                throw new IllegalStateException("Tried to release physical resource that was not allocated");
            }
            $$0.release(this.descriptor, this.physicalResource);
            this.physicalResource = null;
        }
    }

    public static interface Inspector {
        public static final Inspector NONE = new Inspector(){};

        default public void acquireResource(String $$0) {
        }

        default public void releaseResource(String $$0) {
        }

        default public void beforeExecutePass(String $$0) {
        }

        default public void afterExecutePass(String $$0) {
        }
    }

    static abstract class VirtualResource<T> {
        public final String name;
        public Handle<T> handle;

        public VirtualResource(String $$0, @Nullable Pass $$1) {
            this.name = $$0;
            this.handle = new Handle(this, 0, $$1);
        }

        public abstract T get();

        public String toString() {
            return this.name;
        }
    }
}

