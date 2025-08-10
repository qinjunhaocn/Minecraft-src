/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.EnumMap;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;

public final class ChunkSectionsToRender
extends Record {
    private final EnumMap<ChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>> drawsPerLayer;
    private final int maxIndicesRequired;
    private final GpuBufferSlice[] dynamicTransforms;

    public ChunkSectionsToRender(EnumMap<ChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>> $$0, int $$1, GpuBufferSlice[] $$2) {
        this.drawsPerLayer = $$0;
        this.maxIndicesRequired = $$1;
        this.dynamicTransforms = $$2;
    }

    public void renderGroup(ChunkSectionLayerGroup $$0) {
        RenderSystem.AutoStorageIndexBuffer $$1 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        GpuBuffer $$2 = this.maxIndicesRequired == 0 ? null : $$1.getBuffer(this.maxIndicesRequired);
        VertexFormat.IndexType $$3 = this.maxIndicesRequired == 0 ? null : $$1.type();
        ChunkSectionLayer[] $$4 = $$0.b();
        Minecraft $$5 = Minecraft.getInstance();
        boolean $$6 = false;
        RenderTarget $$7 = $$0.outputTarget();
        try (RenderPass $$8 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Section layers for " + $$0.label(), $$7.getColorTextureView(), OptionalInt.empty(), $$7.getDepthTextureView(), OptionalDouble.empty());){
            RenderSystem.bindDefaultUniforms($$8);
            $$8.bindSampler("Sampler2", $$5.gameRenderer.lightTexture().getTextureView());
            for (ChunkSectionLayer $$9 : $$4) {
                List $$10 = this.drawsPerLayer.get((Object)$$9);
                if ($$10.isEmpty()) continue;
                if ($$9 == ChunkSectionLayer.TRANSLUCENT) {
                    $$10 = $$10.reversed();
                }
                $$8.setPipeline($$6 ? RenderPipelines.WIREFRAME : $$9.pipeline());
                $$8.bindSampler("Sampler0", $$9.textureView());
                $$8.drawMultipleIndexed($$10, $$2, $$3, List.of((Object)"DynamicTransforms"), this.dynamicTransforms);
            }
        }
    }

    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ChunkSectionsToRender.class, "drawsPerLayer;maxIndicesRequired;dynamicTransforms", "drawsPerLayer", "maxIndicesRequired", "dynamicTransforms"}, this);
    }

    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ChunkSectionsToRender.class, "drawsPerLayer;maxIndicesRequired;dynamicTransforms", "drawsPerLayer", "maxIndicesRequired", "dynamicTransforms"}, this);
    }

    public final boolean equals(Object $$0) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ChunkSectionsToRender.class, "drawsPerLayer;maxIndicesRequired;dynamicTransforms", "drawsPerLayer", "maxIndicesRequired", "dynamicTransforms"}, this, $$0);
    }

    public EnumMap<ChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>> drawsPerLayer() {
        return this.drawsPerLayer;
    }

    public int maxIndicesRequired() {
        return this.maxIndicesRequired;
    }

    public GpuBufferSlice[] c() {
        return this.dynamicTransforms;
    }
}

