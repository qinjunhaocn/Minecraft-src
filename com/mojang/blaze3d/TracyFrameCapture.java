/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.jtracy.TracyClient
 */
package com.mojang.blaze3d;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;
import java.util.OptionalInt;
import net.minecraft.client.renderer.RenderPipelines;

public class TracyFrameCapture
implements AutoCloseable {
    private static final int MAX_WIDTH = 320;
    private static final int MAX_HEIGHT = 180;
    private static final int BYTES_PER_PIXEL = 4;
    private int targetWidth;
    private int targetHeight;
    private int width = 320;
    private int height = 180;
    private GpuTexture frameBuffer;
    private GpuTextureView frameBufferView;
    private GpuBuffer pixelbuffer;
    private int lastCaptureDelay;
    private boolean capturedThisFrame;
    private Status status = Status.WAITING_FOR_CAPTURE;

    public TracyFrameCapture() {
        GpuDevice $$0 = RenderSystem.getDevice();
        this.frameBuffer = $$0.createTexture("Tracy Frame Capture", 10, TextureFormat.RGBA8, this.width, this.height, 1, 1);
        this.frameBufferView = $$0.createTextureView(this.frameBuffer);
        this.pixelbuffer = $$0.createBuffer(() -> "Tracy Frame Capture buffer", 9, this.width * this.height * 4);
    }

    private void resize(int $$0, int $$1) {
        float $$2 = (float)$$0 / (float)$$1;
        if ($$0 > 320) {
            $$0 = 320;
            $$1 = (int)(320.0f / $$2);
        }
        if ($$1 > 180) {
            $$0 = (int)(180.0f * $$2);
            $$1 = 180;
        }
        $$0 = $$0 / 4 * 4;
        $$1 = $$1 / 4 * 4;
        if (this.width != $$0 || this.height != $$1) {
            this.width = $$0;
            this.height = $$1;
            GpuDevice $$3 = RenderSystem.getDevice();
            this.frameBuffer.close();
            this.frameBuffer = $$3.createTexture("Tracy Frame Capture", 10, TextureFormat.RGBA8, $$0, $$1, 1, 1);
            this.frameBufferView.close();
            this.frameBufferView = $$3.createTextureView(this.frameBuffer);
            this.pixelbuffer.close();
            this.pixelbuffer = $$3.createBuffer(() -> "Tracy Frame Capture buffer", 9, $$0 * $$1 * 4);
        }
    }

    public void capture(RenderTarget $$0) {
        if (this.status != Status.WAITING_FOR_CAPTURE || this.capturedThisFrame || $$0.getColorTexture() == null) {
            return;
        }
        this.capturedThisFrame = true;
        if ($$0.width != this.targetWidth || $$0.height != this.targetHeight) {
            this.targetWidth = $$0.width;
            this.targetHeight = $$0.height;
            this.resize(this.targetWidth, this.targetHeight);
        }
        this.status = Status.WAITING_FOR_COPY;
        CommandEncoder $$1 = RenderSystem.getDevice().createCommandEncoder();
        RenderSystem.AutoStorageIndexBuffer $$2 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        GpuBuffer $$3 = $$2.getBuffer(6);
        try (RenderPass $$4 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Tracy blit", this.frameBufferView, OptionalInt.empty());){
            $$4.setPipeline(RenderPipelines.TRACY_BLIT);
            $$4.setVertexBuffer(0, RenderSystem.getQuadVertexBuffer());
            $$4.setIndexBuffer($$3, $$2.type());
            $$4.bindSampler("InSampler", $$0.getColorTextureView());
            $$4.drawIndexed(0, 0, 6, 1);
        }
        $$1.copyTextureToBuffer(this.frameBuffer, this.pixelbuffer, 0, () -> {
            this.status = Status.WAITING_FOR_UPLOAD;
        }, 0);
        this.lastCaptureDelay = 0;
    }

    public void upload() {
        if (this.status != Status.WAITING_FOR_UPLOAD) {
            return;
        }
        this.status = Status.WAITING_FOR_CAPTURE;
        try (GpuBuffer.MappedView $$0 = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.pixelbuffer, true, false);){
            TracyClient.frameImage((ByteBuffer)$$0.data(), (int)this.width, (int)this.height, (int)this.lastCaptureDelay, (boolean)true);
        }
    }

    public void endFrame() {
        ++this.lastCaptureDelay;
        this.capturedThisFrame = false;
        TracyClient.markFrame();
    }

    @Override
    public void close() {
        this.frameBuffer.close();
        this.frameBufferView.close();
        this.pixelbuffer.close();
    }

    static final class Status
    extends Enum<Status> {
        public static final /* enum */ Status WAITING_FOR_CAPTURE = new Status();
        public static final /* enum */ Status WAITING_FOR_COPY = new Status();
        public static final /* enum */ Status WAITING_FOR_UPLOAD = new Status();
        private static final /* synthetic */ Status[] $VALUES;

        public static Status[] values() {
            return (Status[])$VALUES.clone();
        }

        public static Status valueOf(String $$0) {
            return Enum.valueOf(Status.class, $$0);
        }

        private static /* synthetic */ Status[] a() {
            return new Status[]{WAITING_FOR_CAPTURE, WAITING_FOR_COPY, WAITING_FOR_UPLOAD};
        }

        static {
            $VALUES = Status.a();
        }
    }
}

