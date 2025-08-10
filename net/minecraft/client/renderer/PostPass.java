/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.UniformValue;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.MemoryStack;

public class PostPass
implements AutoCloseable {
    private static final int UBO_SIZE_PER_SAMPLER = new Std140SizeCalculator().putVec2().get();
    private final String name;
    private final RenderPipeline pipeline;
    private final ResourceLocation outputTargetId;
    private final Map<String, GpuBuffer> customUniforms = new HashMap<String, GpuBuffer>();
    private final MappableRingBuffer infoUbo;
    private final List<Input> inputs;

    public PostPass(RenderPipeline $$0, ResourceLocation $$1, Map<String, List<UniformValue>> $$2, List<Input> $$3) {
        this.pipeline = $$0;
        this.name = $$0.getLocation().toString();
        this.outputTargetId = $$1;
        this.inputs = $$3;
        for (Map.Entry<String, List<UniformValue>> $$4 : $$2.entrySet()) {
            List<UniformValue> $$5 = $$4.getValue();
            if ($$5.isEmpty()) continue;
            Std140SizeCalculator $$6 = new Std140SizeCalculator();
            for (UniformValue $$7 : $$5) {
                $$7.addSize($$6);
            }
            int $$8 = $$6.get();
            MemoryStack $$9 = MemoryStack.stackPush();
            try {
                Std140Builder $$10 = Std140Builder.onStack($$9, $$8);
                for (UniformValue $$11 : $$5) {
                    $$11.writeTo($$10);
                }
                this.customUniforms.put($$4.getKey(), RenderSystem.getDevice().createBuffer(() -> this.name + " / " + (String)$$4.getKey(), 128, $$10.get()));
            } finally {
                if ($$9 == null) continue;
                $$9.close();
            }
        }
        this.infoUbo = new MappableRingBuffer(() -> this.name + " SamplerInfo", 130, ($$3.size() + 1) * UBO_SIZE_PER_SAMPLER);
    }

    public void addToFrame(FrameGraphBuilder $$0, Map<ResourceLocation, ResourceHandle<RenderTarget>> $$12, GpuBufferSlice $$22) {
        FramePass $$3 = $$0.addPass(this.name);
        for (Input $$4 : this.inputs) {
            $$4.addToPass($$3, $$12);
        }
        ResourceHandle $$5 = $$12.computeIfPresent(this.outputTargetId, ($$1, $$2) -> $$3.readsAndWrites($$2));
        if ($$5 == null) {
            throw new IllegalStateException("Missing handle for target " + String.valueOf(this.outputTargetId));
        }
        $$3.executes(() -> {
            RenderTarget $$3 = (RenderTarget)$$5.get();
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix($$22, ProjectionType.ORTHOGRAPHIC);
            CommandEncoder $$4 = RenderSystem.getDevice().createCommandEncoder();
            List $$5 = this.inputs.stream().map($$1 -> Pair.of((Object)$$1.samplerName(), (Object)$$1.texture($$12))).toList();
            try (GpuBuffer.MappedView $$6 = $$4.mapBuffer(this.infoUbo.currentBuffer(), false, true);){
                Std140Builder $$7 = Std140Builder.intoBuffer($$6.data());
                $$7.putVec2($$3.width, $$3.height);
                for (Pair $$8 : $$5) {
                    $$7.putVec2(((GpuTextureView)$$8.getSecond()).getWidth(0), ((GpuTextureView)$$8.getSecond()).getHeight(0));
                }
            }
            GpuBuffer $$9 = RenderSystem.getQuadVertexBuffer();
            RenderSystem.AutoStorageIndexBuffer $$10 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
            GpuBuffer $$11 = $$10.getBuffer(6);
            try (RenderPass $$122 = $$4.createRenderPass(() -> "Post pass " + this.name, $$3.getColorTextureView(), OptionalInt.empty(), $$3.useDepth ? $$3.getDepthTextureView() : null, OptionalDouble.empty());){
                $$122.setPipeline(this.pipeline);
                RenderSystem.bindDefaultUniforms($$122);
                $$122.setUniform("SamplerInfo", this.infoUbo.currentBuffer());
                for (Map.Entry<String, GpuBuffer> $$13 : this.customUniforms.entrySet()) {
                    $$122.setUniform($$13.getKey(), $$13.getValue());
                }
                $$122.setVertexBuffer(0, $$9);
                $$122.setIndexBuffer($$11, $$10.type());
                for (Pair $$14 : $$5) {
                    $$122.bindSampler((String)$$14.getFirst() + "Sampler", (GpuTextureView)$$14.getSecond());
                }
                $$122.drawIndexed(0, 0, 6, 1);
            }
            this.infoUbo.rotate();
            RenderSystem.restoreProjectionMatrix();
            for (Input $$15 : this.inputs) {
                $$15.cleanup($$12);
            }
        });
    }

    @Override
    public void close() {
        for (GpuBuffer $$0 : this.customUniforms.values()) {
            $$0.close();
        }
        this.infoUbo.close();
    }

    public static interface Input {
        public void addToPass(FramePass var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2);

        default public void cleanup(Map<ResourceLocation, ResourceHandle<RenderTarget>> $$0) {
        }

        public GpuTextureView texture(Map<ResourceLocation, ResourceHandle<RenderTarget>> var1);

        public String samplerName();
    }

    public record TargetInput(String samplerName, ResourceLocation targetId, boolean depthBuffer, boolean bilinear) implements Input
    {
        private ResourceHandle<RenderTarget> getHandle(Map<ResourceLocation, ResourceHandle<RenderTarget>> $$0) {
            ResourceHandle<RenderTarget> $$1 = $$0.get(this.targetId);
            if ($$1 == null) {
                throw new IllegalStateException("Missing handle for target " + String.valueOf(this.targetId));
            }
            return $$1;
        }

        @Override
        public void addToPass(FramePass $$0, Map<ResourceLocation, ResourceHandle<RenderTarget>> $$1) {
            $$0.reads(this.getHandle($$1));
        }

        @Override
        public void cleanup(Map<ResourceLocation, ResourceHandle<RenderTarget>> $$0) {
            if (this.bilinear) {
                this.getHandle($$0).get().setFilterMode(FilterMode.NEAREST);
            }
        }

        @Override
        public GpuTextureView texture(Map<ResourceLocation, ResourceHandle<RenderTarget>> $$0) {
            GpuTextureView $$3;
            ResourceHandle<RenderTarget> $$1 = this.getHandle($$0);
            RenderTarget $$2 = $$1.get();
            $$2.setFilterMode(this.bilinear ? FilterMode.LINEAR : FilterMode.NEAREST);
            GpuTextureView gpuTextureView = $$3 = this.depthBuffer ? $$2.getDepthTextureView() : $$2.getColorTextureView();
            if ($$3 == null) {
                throw new IllegalStateException("Missing " + (this.depthBuffer ? "depth" : "color") + "texture for target " + String.valueOf(this.targetId));
            }
            return $$3;
        }
    }

    public record TextureInput(String samplerName, AbstractTexture texture, int width, int height) implements Input
    {
        @Override
        public void addToPass(FramePass $$0, Map<ResourceLocation, ResourceHandle<RenderTarget>> $$1) {
        }

        @Override
        public GpuTextureView texture(Map<ResourceLocation, ResourceHandle<RenderTarget>> $$0) {
            return this.texture.getTextureView();
        }
    }
}

