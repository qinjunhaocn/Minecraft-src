/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class SkyRenderer
implements AutoCloseable {
    private static final ResourceLocation SUN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");
    private static final ResourceLocation MOON_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
    public static final ResourceLocation END_SKY_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png");
    private static final float SKY_DISC_RADIUS = 512.0f;
    private static final int SKY_VERTICES = 10;
    private static final int STAR_COUNT = 1500;
    private static final int END_SKY_QUAD_COUNT = 6;
    private final GpuBuffer starBuffer;
    private final RenderSystem.AutoStorageIndexBuffer starIndices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
    private final GpuBuffer topSkyBuffer;
    private final GpuBuffer bottomSkyBuffer;
    private final GpuBuffer endSkyBuffer;
    private int starIndexCount;

    public SkyRenderer() {
        this.starBuffer = this.buildStars();
        this.endSkyBuffer = SkyRenderer.buildEndSky();
        try (ByteBufferBuilder $$0 = ByteBufferBuilder.exactlySized(10 * DefaultVertexFormat.POSITION.getVertexSize());){
            BufferBuilder $$1 = new BufferBuilder($$0, VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
            this.buildSkyDisc($$1, 16.0f);
            try (MeshData $$2 = $$1.buildOrThrow();){
                this.topSkyBuffer = RenderSystem.getDevice().createBuffer(() -> "Top sky vertex buffer", 32, $$2.vertexBuffer());
            }
            $$1 = new BufferBuilder($$0, VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
            this.buildSkyDisc($$1, -16.0f);
            try (MeshData $$3 = $$1.buildOrThrow();){
                this.bottomSkyBuffer = RenderSystem.getDevice().createBuffer(() -> "Bottom sky vertex buffer", 32, $$3.vertexBuffer());
            }
        }
    }

    private GpuBuffer buildStars() {
        RandomSource $$0 = RandomSource.create(10842L);
        float $$1 = 100.0f;
        try (ByteBufferBuilder $$2 = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION.getVertexSize() * 1500 * 4);){
            BufferBuilder $$3 = new BufferBuilder($$2, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            for (int $$4 = 0; $$4 < 1500; ++$$4) {
                float $$5 = $$0.nextFloat() * 2.0f - 1.0f;
                float $$6 = $$0.nextFloat() * 2.0f - 1.0f;
                float $$7 = $$0.nextFloat() * 2.0f - 1.0f;
                float $$8 = 0.15f + $$0.nextFloat() * 0.1f;
                float $$9 = Mth.lengthSquared($$5, $$6, $$7);
                if ($$9 <= 0.010000001f || $$9 >= 1.0f) continue;
                Vector3f $$10 = new Vector3f($$5, $$6, $$7).normalize(100.0f);
                float $$11 = (float)($$0.nextDouble() * 3.1415927410125732 * 2.0);
                Matrix3f $$12 = new Matrix3f().rotateTowards((Vector3fc)new Vector3f((Vector3fc)$$10).negate(), (Vector3fc)new Vector3f(0.0f, 1.0f, 0.0f)).rotateZ(-$$11);
                $$3.addVertex(new Vector3f($$8, -$$8, 0.0f).mul((Matrix3fc)$$12).add((Vector3fc)$$10));
                $$3.addVertex(new Vector3f($$8, $$8, 0.0f).mul((Matrix3fc)$$12).add((Vector3fc)$$10));
                $$3.addVertex(new Vector3f(-$$8, $$8, 0.0f).mul((Matrix3fc)$$12).add((Vector3fc)$$10));
                $$3.addVertex(new Vector3f(-$$8, -$$8, 0.0f).mul((Matrix3fc)$$12).add((Vector3fc)$$10));
            }
            MeshData $$13 = $$3.buildOrThrow();
            try {
                this.starIndexCount = $$13.drawState().indexCount();
                GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Stars vertex buffer", 40, $$13.vertexBuffer());
                if ($$13 != null) {
                    $$13.close();
                }
                return gpuBuffer;
            } catch (Throwable throwable) {
                if ($$13 != null) {
                    try {
                        $$13.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }

    private void buildSkyDisc(VertexConsumer $$0, float $$1) {
        float $$2 = Math.signum($$1) * 512.0f;
        $$0.addVertex(0.0f, $$1, 0.0f);
        for (int $$3 = -180; $$3 <= 180; $$3 += 45) {
            $$0.addVertex($$2 * Mth.cos((float)$$3 * ((float)Math.PI / 180)), $$1, 512.0f * Mth.sin((float)$$3 * ((float)Math.PI / 180)));
        }
    }

    public void renderSkyDisc(float $$0, float $$1, float $$2) {
        GpuBufferSlice $$3 = RenderSystem.getDynamicUniforms().writeTransform((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)new Vector4f($$0, $$1, $$2, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 0.0f);
        GpuTextureView $$4 = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        GpuTextureView $$5 = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
        try (RenderPass $$6 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Sky disc", $$4, OptionalInt.empty(), $$5, OptionalDouble.empty());){
            $$6.setPipeline(RenderPipelines.SKY);
            RenderSystem.bindDefaultUniforms($$6);
            $$6.setUniform("DynamicTransforms", $$3);
            $$6.setVertexBuffer(0, this.topSkyBuffer);
            $$6.draw(0, 10);
        }
    }

    public void renderDarkDisc() {
        Matrix4fStack $$0 = RenderSystem.getModelViewStack();
        $$0.pushMatrix();
        $$0.translate(0.0f, 12.0f, 0.0f);
        GpuBufferSlice $$1 = RenderSystem.getDynamicUniforms().writeTransform((Matrix4fc)$$0, (Vector4fc)new Vector4f(0.0f, 0.0f, 0.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 0.0f);
        GpuTextureView $$2 = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        GpuTextureView $$3 = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
        try (RenderPass $$4 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Sky dark", $$2, OptionalInt.empty(), $$3, OptionalDouble.empty());){
            $$4.setPipeline(RenderPipelines.SKY);
            RenderSystem.bindDefaultUniforms($$4);
            $$4.setUniform("DynamicTransforms", $$1);
            $$4.setVertexBuffer(0, this.bottomSkyBuffer);
            $$4.draw(0, 10);
        }
        $$0.popMatrix();
    }

    public void renderSunMoonAndStars(PoseStack $$0, MultiBufferSource.BufferSource $$1, float $$2, int $$3, float $$4, float $$5) {
        $$0.pushPose();
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-90.0f));
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$2 * 360.0f));
        this.renderSun($$4, $$1, $$0);
        this.renderMoon($$3, $$4, $$1, $$0);
        $$1.endBatch();
        if ($$5 > 0.0f) {
            this.renderStars($$5, $$0);
        }
        $$0.popPose();
    }

    private void renderSun(float $$0, MultiBufferSource $$1, PoseStack $$2) {
        float $$3 = 30.0f;
        float $$4 = 100.0f;
        VertexConsumer $$5 = $$1.getBuffer(RenderType.celestial(SUN_LOCATION));
        int $$6 = ARGB.white($$0);
        Matrix4f $$7 = $$2.last().pose();
        $$5.addVertex($$7, -30.0f, 100.0f, -30.0f).setUv(0.0f, 0.0f).setColor($$6);
        $$5.addVertex($$7, 30.0f, 100.0f, -30.0f).setUv(1.0f, 0.0f).setColor($$6);
        $$5.addVertex($$7, 30.0f, 100.0f, 30.0f).setUv(1.0f, 1.0f).setColor($$6);
        $$5.addVertex($$7, -30.0f, 100.0f, 30.0f).setUv(0.0f, 1.0f).setColor($$6);
    }

    private void renderMoon(int $$0, float $$1, MultiBufferSource $$2, PoseStack $$3) {
        float $$4 = 20.0f;
        int $$5 = $$0 % 4;
        int $$6 = $$0 / 4 % 2;
        float $$7 = (float)($$5 + 0) / 4.0f;
        float $$8 = (float)($$6 + 0) / 2.0f;
        float $$9 = (float)($$5 + 1) / 4.0f;
        float $$10 = (float)($$6 + 1) / 2.0f;
        float $$11 = 100.0f;
        VertexConsumer $$12 = $$2.getBuffer(RenderType.celestial(MOON_LOCATION));
        int $$13 = ARGB.white($$1);
        Matrix4f $$14 = $$3.last().pose();
        $$12.addVertex($$14, -20.0f, -100.0f, 20.0f).setUv($$9, $$10).setColor($$13);
        $$12.addVertex($$14, 20.0f, -100.0f, 20.0f).setUv($$7, $$10).setColor($$13);
        $$12.addVertex($$14, 20.0f, -100.0f, -20.0f).setUv($$7, $$8).setColor($$13);
        $$12.addVertex($$14, -20.0f, -100.0f, -20.0f).setUv($$9, $$8).setColor($$13);
    }

    private void renderStars(float $$0, PoseStack $$1) {
        Matrix4fStack $$2 = RenderSystem.getModelViewStack();
        $$2.pushMatrix();
        $$2.mul((Matrix4fc)$$1.last().pose());
        RenderPipeline $$3 = RenderPipelines.STARS;
        GpuTextureView $$4 = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        GpuTextureView $$5 = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
        GpuBuffer $$6 = this.starIndices.getBuffer(this.starIndexCount);
        GpuBufferSlice $$7 = RenderSystem.getDynamicUniforms().writeTransform((Matrix4fc)$$2, (Vector4fc)new Vector4f($$0, $$0, $$0, $$0), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 0.0f);
        try (RenderPass $$8 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Stars", $$4, OptionalInt.empty(), $$5, OptionalDouble.empty());){
            $$8.setPipeline($$3);
            RenderSystem.bindDefaultUniforms($$8);
            $$8.setUniform("DynamicTransforms", $$7);
            $$8.setVertexBuffer(0, this.starBuffer);
            $$8.setIndexBuffer($$6, this.starIndices.type());
            $$8.drawIndexed(0, 0, this.starIndexCount, 1);
        }
        $$2.popMatrix();
    }

    public void renderSunriseAndSunset(PoseStack $$0, MultiBufferSource.BufferSource $$1, float $$2, int $$3) {
        $$0.pushPose();
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90.0f));
        float $$4 = Mth.sin($$2) < 0.0f ? 180.0f : 0.0f;
        $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees($$4));
        $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(90.0f));
        Matrix4f $$5 = $$0.last().pose();
        VertexConsumer $$6 = $$1.getBuffer(RenderType.sunriseSunset());
        float $$7 = ARGB.alphaFloat($$3);
        $$6.addVertex($$5, 0.0f, 100.0f, 0.0f).setColor($$3);
        int $$8 = ARGB.transparent($$3);
        int $$9 = 16;
        for (int $$10 = 0; $$10 <= 16; ++$$10) {
            float $$11 = (float)$$10 * ((float)Math.PI * 2) / 16.0f;
            float $$12 = Mth.sin($$11);
            float $$13 = Mth.cos($$11);
            $$6.addVertex($$5, $$12 * 120.0f, $$13 * 120.0f, -$$13 * 40.0f * $$7).setColor($$8);
        }
        $$0.popPose();
    }

    private static GpuBuffer buildEndSky() {
        try (ByteBufferBuilder $$0 = ByteBufferBuilder.exactlySized(24 * DefaultVertexFormat.POSITION_TEX_COLOR.getVertexSize());){
            BufferBuilder $$1 = new BufferBuilder($$0, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            for (int $$2 = 0; $$2 < 6; ++$$2) {
                Matrix4f $$3 = new Matrix4f();
                switch ($$2) {
                    case 1: {
                        $$3.rotationX(1.5707964f);
                        break;
                    }
                    case 2: {
                        $$3.rotationX(-1.5707964f);
                        break;
                    }
                    case 3: {
                        $$3.rotationX((float)Math.PI);
                        break;
                    }
                    case 4: {
                        $$3.rotationZ(1.5707964f);
                        break;
                    }
                    case 5: {
                        $$3.rotationZ(-1.5707964f);
                    }
                }
                $$1.addVertex($$3, -100.0f, -100.0f, -100.0f).setUv(0.0f, 0.0f).setColor(-14145496);
                $$1.addVertex($$3, -100.0f, -100.0f, 100.0f).setUv(0.0f, 16.0f).setColor(-14145496);
                $$1.addVertex($$3, 100.0f, -100.0f, 100.0f).setUv(16.0f, 16.0f).setColor(-14145496);
                $$1.addVertex($$3, 100.0f, -100.0f, -100.0f).setUv(16.0f, 0.0f).setColor(-14145496);
            }
            MeshData $$4 = $$1.buildOrThrow();
            try {
                GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "End sky vertex buffer", 40, $$4.vertexBuffer());
                if ($$4 != null) {
                    $$4.close();
                }
                return gpuBuffer;
            } catch (Throwable throwable) {
                if ($$4 != null) {
                    try {
                        $$4.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }

    public void renderEndSky() {
        TextureManager $$0 = Minecraft.getInstance().getTextureManager();
        AbstractTexture $$1 = $$0.getTexture(END_SKY_LOCATION);
        $$1.setUseMipmaps(false);
        RenderSystem.AutoStorageIndexBuffer $$2 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        GpuBuffer $$3 = $$2.getBuffer(36);
        GpuTextureView $$4 = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        GpuTextureView $$5 = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
        GpuBufferSlice $$6 = RenderSystem.getDynamicUniforms().writeTransform((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 0.0f);
        try (RenderPass $$7 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "End sky", $$4, OptionalInt.empty(), $$5, OptionalDouble.empty());){
            $$7.setPipeline(RenderPipelines.END_SKY);
            RenderSystem.bindDefaultUniforms($$7);
            $$7.setUniform("DynamicTransforms", $$6);
            $$7.bindSampler("Sampler0", $$1.getTextureView());
            $$7.setVertexBuffer(0, this.endSkyBuffer);
            $$7.setIndexBuffer($$3, $$2.type());
            $$7.drawIndexed(0, 0, 36, 1);
        }
    }

    @Override
    public void close() {
        this.starBuffer.close();
        this.topSkyBuffer.close();
        this.bottomSkyBuffer.close();
        this.endSkyBuffer.close();
    }
}

