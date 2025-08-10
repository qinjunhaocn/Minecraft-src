/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps
 *  java.util.SequencedMap
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import java.util.HashMap;
import java.util.Map;
import java.util.SequencedMap;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;

public interface MultiBufferSource {
    public static BufferSource immediate(ByteBufferBuilder $$0) {
        return MultiBufferSource.immediateWithBuffers((SequencedMap<RenderType, ByteBufferBuilder>)Object2ObjectSortedMaps.emptyMap(), $$0);
    }

    public static BufferSource immediateWithBuffers(SequencedMap<RenderType, ByteBufferBuilder> $$0, ByteBufferBuilder $$1) {
        return new BufferSource($$1, $$0);
    }

    public VertexConsumer getBuffer(RenderType var1);

    public static class BufferSource
    implements MultiBufferSource {
        protected final ByteBufferBuilder sharedBuffer;
        protected final SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers;
        protected final Map<RenderType, BufferBuilder> startedBuilders = new HashMap<RenderType, BufferBuilder>();
        @Nullable
        protected RenderType lastSharedType;

        protected BufferSource(ByteBufferBuilder $$0, SequencedMap<RenderType, ByteBufferBuilder> $$1) {
            this.sharedBuffer = $$0;
            this.fixedBuffers = $$1;
        }

        @Override
        public VertexConsumer getBuffer(RenderType $$0) {
            BufferBuilder $$1 = this.startedBuilders.get($$0);
            if ($$1 != null && !$$0.canConsolidateConsecutiveGeometry()) {
                this.endBatch($$0, $$1);
                $$1 = null;
            }
            if ($$1 != null) {
                return $$1;
            }
            ByteBufferBuilder $$2 = (ByteBufferBuilder)this.fixedBuffers.get((Object)$$0);
            if ($$2 != null) {
                $$1 = new BufferBuilder($$2, $$0.mode(), $$0.format());
            } else {
                if (this.lastSharedType != null) {
                    this.endBatch(this.lastSharedType);
                }
                $$1 = new BufferBuilder(this.sharedBuffer, $$0.mode(), $$0.format());
                this.lastSharedType = $$0;
            }
            this.startedBuilders.put($$0, $$1);
            return $$1;
        }

        public void endLastBatch() {
            if (this.lastSharedType != null) {
                this.endBatch(this.lastSharedType);
                this.lastSharedType = null;
            }
        }

        public void endBatch() {
            this.endLastBatch();
            for (RenderType $$0 : this.fixedBuffers.keySet()) {
                this.endBatch($$0);
            }
        }

        public void endBatch(RenderType $$0) {
            BufferBuilder $$1 = this.startedBuilders.remove($$0);
            if ($$1 != null) {
                this.endBatch($$0, $$1);
            }
        }

        private void endBatch(RenderType $$0, BufferBuilder $$1) {
            MeshData $$2 = $$1.build();
            if ($$2 != null) {
                if ($$0.sortOnUpload()) {
                    ByteBufferBuilder $$3 = (ByteBufferBuilder)this.fixedBuffers.getOrDefault((Object)$$0, (Object)this.sharedBuffer);
                    $$2.sortQuads($$3, RenderSystem.getProjectionType().vertexSorting());
                }
                $$0.draw($$2);
            }
            if ($$0.equals(this.lastSharedType)) {
                this.lastSharedType = null;
            }
        }
    }
}

