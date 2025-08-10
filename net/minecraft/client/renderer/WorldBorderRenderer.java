/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class WorldBorderRenderer {
    public static final ResourceLocation FORCEFIELD_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/forcefield.png");
    private boolean needsRebuild = true;
    private double lastMinX;
    private double lastMinZ;
    private double lastBorderMinX;
    private double lastBorderMaxX;
    private double lastBorderMinZ;
    private double lastBorderMaxZ;
    private final GpuBuffer worldBorderBuffer = RenderSystem.getDevice().createBuffer(() -> "World border vertex buffer", 40, 16 * DefaultVertexFormat.POSITION_TEX.getVertexSize());
    private final RenderSystem.AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);

    private void rebuildWorldBorderBuffer(WorldBorder $$0, double $$1, double $$2, double $$3, float $$4, float $$5, float $$6) {
        try (ByteBufferBuilder $$7 = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION_TEX.getVertexSize() * 4 * 4);){
            double $$8 = $$0.getMinX();
            double $$9 = $$0.getMaxX();
            double $$10 = $$0.getMinZ();
            double $$11 = $$0.getMaxZ();
            double $$12 = Math.max((double)Mth.floor($$2 - $$1), $$10);
            double $$13 = Math.min((double)Mth.ceil($$2 + $$1), $$11);
            float $$14 = (float)(Mth.floor($$12) & 1) * 0.5f;
            float $$15 = (float)($$13 - $$12) / 2.0f;
            double $$16 = Math.max((double)Mth.floor($$3 - $$1), $$8);
            double $$17 = Math.min((double)Mth.ceil($$3 + $$1), $$9);
            float $$18 = (float)(Mth.floor($$16) & 1) * 0.5f;
            float $$19 = (float)($$17 - $$16) / 2.0f;
            BufferBuilder $$20 = new BufferBuilder($$7, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            $$20.addVertex(0.0f, -$$4, (float)($$11 - $$12)).setUv($$18, $$5);
            $$20.addVertex((float)($$17 - $$16), -$$4, (float)($$11 - $$12)).setUv($$19 + $$18, $$5);
            $$20.addVertex((float)($$17 - $$16), $$4, (float)($$11 - $$12)).setUv($$19 + $$18, $$6);
            $$20.addVertex(0.0f, $$4, (float)($$11 - $$12)).setUv($$18, $$6);
            $$20.addVertex(0.0f, -$$4, 0.0f).setUv($$14, $$5);
            $$20.addVertex(0.0f, -$$4, (float)($$13 - $$12)).setUv($$15 + $$14, $$5);
            $$20.addVertex(0.0f, $$4, (float)($$13 - $$12)).setUv($$15 + $$14, $$6);
            $$20.addVertex(0.0f, $$4, 0.0f).setUv($$14, $$6);
            $$20.addVertex((float)($$17 - $$16), -$$4, 0.0f).setUv($$18, $$5);
            $$20.addVertex(0.0f, -$$4, 0.0f).setUv($$19 + $$18, $$5);
            $$20.addVertex(0.0f, $$4, 0.0f).setUv($$19 + $$18, $$6);
            $$20.addVertex((float)($$17 - $$16), $$4, 0.0f).setUv($$18, $$6);
            $$20.addVertex((float)($$9 - $$16), -$$4, (float)($$13 - $$12)).setUv($$14, $$5);
            $$20.addVertex((float)($$9 - $$16), -$$4, 0.0f).setUv($$15 + $$14, $$5);
            $$20.addVertex((float)($$9 - $$16), $$4, 0.0f).setUv($$15 + $$14, $$6);
            $$20.addVertex((float)($$9 - $$16), $$4, (float)($$13 - $$12)).setUv($$14, $$6);
            try (MeshData $$21 = $$20.buildOrThrow();){
                RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.worldBorderBuffer.slice(), $$21.vertexBuffer());
            }
            this.lastBorderMinX = $$8;
            this.lastBorderMaxX = $$9;
            this.lastBorderMinZ = $$10;
            this.lastBorderMaxZ = $$11;
            this.lastMinX = $$16;
            this.lastMinZ = $$12;
            this.needsRebuild = false;
        }
    }

    public void render(WorldBorder $$0, Vec3 $$1, double $$2, double $$3) {
        GpuTextureView $$27;
        GpuTextureView $$26;
        double $$4 = $$0.getMinX();
        double $$5 = $$0.getMaxX();
        double $$6 = $$0.getMinZ();
        double $$7 = $$0.getMaxZ();
        if ($$1.x < $$5 - $$2 && $$1.x > $$4 + $$2 && $$1.z < $$7 - $$2 && $$1.z > $$6 + $$2 || $$1.x < $$4 - $$2 || $$1.x > $$5 + $$2 || $$1.z < $$6 - $$2 || $$1.z > $$7 + $$2) {
            return;
        }
        double $$8 = 1.0 - $$0.getDistanceToBorder($$1.x, $$1.z) / $$2;
        $$8 = Math.pow($$8, 4.0);
        $$8 = Mth.clamp($$8, 0.0, 1.0);
        double $$9 = $$1.x;
        double $$10 = $$1.z;
        float $$11 = (float)$$3;
        int $$12 = $$0.getStatus().getColor();
        float $$13 = (float)ARGB.red($$12) / 255.0f;
        float $$14 = (float)ARGB.green($$12) / 255.0f;
        float $$15 = (float)ARGB.blue($$12) / 255.0f;
        float $$16 = (float)(Util.getMillis() % 3000L) / 3000.0f;
        float $$17 = (float)(-Mth.frac($$1.y * 0.5));
        float $$18 = $$17 + $$11;
        if (this.shouldRebuildWorldBorderBuffer($$0)) {
            this.rebuildWorldBorderBuffer($$0, $$2, $$10, $$9, $$11, $$18, $$17);
        }
        TextureManager $$19 = Minecraft.getInstance().getTextureManager();
        AbstractTexture $$20 = $$19.getTexture(FORCEFIELD_LOCATION);
        $$20.setUseMipmaps(false);
        RenderPipeline $$21 = RenderPipelines.WORLD_BORDER;
        RenderTarget $$22 = Minecraft.getInstance().getMainRenderTarget();
        RenderTarget $$23 = Minecraft.getInstance().levelRenderer.getWeatherTarget();
        if ($$23 != null) {
            GpuTextureView $$24 = $$23.getColorTextureView();
            GpuTextureView $$25 = $$23.getDepthTextureView();
        } else {
            $$26 = $$22.getColorTextureView();
            $$27 = $$22.getDepthTextureView();
        }
        GpuBuffer $$28 = this.indices.getBuffer(6);
        GpuBufferSlice $$29 = RenderSystem.getDynamicUniforms().writeTransform((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)new Vector4f($$13, $$14, $$15, (float)$$8), (Vector3fc)new Vector3f((float)(this.lastMinX - $$9), (float)(-$$1.y), (float)(this.lastMinZ - $$10)), (Matrix4fc)new Matrix4f().translation($$16, $$16, 0.0f), 0.0f);
        try (RenderPass $$30 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "World border", $$26, OptionalInt.empty(), $$27, OptionalDouble.empty());){
            $$30.setPipeline($$21);
            RenderSystem.bindDefaultUniforms($$30);
            $$30.setUniform("DynamicTransforms", $$29);
            $$30.setIndexBuffer($$28, this.indices.type());
            $$30.bindSampler("Sampler0", $$20.getTextureView());
            $$30.setVertexBuffer(0, this.worldBorderBuffer);
            ArrayList $$31 = new ArrayList();
            for (WorldBorder.DistancePerDirection $$32 : $$0.closestBorder($$9, $$10)) {
                if (!($$32.distance() < $$2)) continue;
                int $$33 = $$32.direction().get2DDataValue();
                $$31.add(new RenderPass.Draw(0, this.worldBorderBuffer, $$28, this.indices.type(), 6 * $$33, 6));
            }
            $$30.drawMultipleIndexed($$31, null, null, Collections.emptyList(), this);
        }
    }

    public void invalidate() {
        this.needsRebuild = true;
    }

    private boolean shouldRebuildWorldBorderBuffer(WorldBorder $$0) {
        return this.needsRebuild || $$0.getMinX() != this.lastBorderMinX || $$0.getMinZ() != this.lastBorderMinZ || $$0.getMaxX() != this.lastBorderMaxX || $$0.getMaxZ() != this.lastBorderMaxZ;
    }
}

