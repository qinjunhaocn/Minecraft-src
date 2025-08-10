/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
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
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.slf4j.Logger;

public class CloudRenderer
extends SimplePreparableReloadListener<Optional<TextureData>>
implements AutoCloseable {
    private static final int FLAG_INSIDE_FACE = 16;
    private static final int FLAG_USE_TOP_COLOR = 32;
    private static final int MAX_RADIUS_CHUNKS = 128;
    private static final float CELL_SIZE_IN_BLOCKS = 12.0f;
    private static final int UBO_SIZE = new Std140SizeCalculator().putVec4().putVec3().putVec3().get();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/clouds.png");
    private static final float BLOCKS_PER_SECOND = 0.6f;
    private static final long EMPTY_CELL = 0L;
    private static final int COLOR_OFFSET = 4;
    private static final int NORTH_OFFSET = 3;
    private static final int EAST_OFFSET = 2;
    private static final int SOUTH_OFFSET = 1;
    private static final int WEST_OFFSET = 0;
    private boolean needsRebuild = true;
    private int prevCellX = Integer.MIN_VALUE;
    private int prevCellZ = Integer.MIN_VALUE;
    private RelativeCameraPos prevRelativeCameraPos = RelativeCameraPos.INSIDE_CLOUDS;
    @Nullable
    private CloudStatus prevType;
    @Nullable
    private TextureData texture;
    private int quadCount = 0;
    private final RenderSystem.AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
    private final MappableRingBuffer ubo = new MappableRingBuffer(() -> "Cloud UBO", 130, UBO_SIZE);
    @Nullable
    private MappableRingBuffer utb;

    /*
     * Enabled aggressive exception aggregation
     */
    @Override
    protected Optional<TextureData> prepare(ResourceManager $$0, ProfilerFiller $$1) {
        try (InputStream $$2 = $$0.open(TEXTURE_LOCATION);){
            NativeImage $$3 = NativeImage.read($$2);
            try {
                int $$4 = $$3.getWidth();
                int $$5 = $$3.getHeight();
                long[] $$6 = new long[$$4 * $$5];
                for (int $$7 = 0; $$7 < $$5; ++$$7) {
                    for (int $$8 = 0; $$8 < $$4; ++$$8) {
                        int $$9 = $$3.getPixel($$8, $$7);
                        if (CloudRenderer.isCellEmpty($$9)) {
                            $$6[$$8 + $$7 * $$4] = 0L;
                            continue;
                        }
                        boolean $$10 = CloudRenderer.isCellEmpty($$3.getPixel($$8, Math.floorMod($$7 - 1, $$5)));
                        boolean $$11 = CloudRenderer.isCellEmpty($$3.getPixel(Math.floorMod($$8 + 1, $$5), $$7));
                        boolean $$12 = CloudRenderer.isCellEmpty($$3.getPixel($$8, Math.floorMod($$7 + 1, $$5)));
                        boolean $$13 = CloudRenderer.isCellEmpty($$3.getPixel(Math.floorMod($$8 - 1, $$5), $$7));
                        $$6[$$8 + $$7 * $$4] = CloudRenderer.packCellData($$9, $$10, $$11, $$12, $$13);
                    }
                }
                Optional<TextureData> optional = Optional.of(new TextureData($$6, $$4, $$5));
                if ($$3 != null) {
                    $$3.close();
                }
                return optional;
            } catch (Throwable throwable) {
                if ($$3 != null) {
                    try {
                        $$3.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        } catch (IOException $$14) {
            LOGGER.error("Failed to load cloud texture", $$14);
            return Optional.empty();
        }
    }

    private static int getSizeForCloudDistance(int $$0) {
        int $$1 = 4;
        int $$2 = ($$0 + 1) * 2 * (($$0 + 1) * 2) / 2;
        int $$3 = $$2 * 4 + 54;
        return $$3 * 3;
    }

    @Override
    protected void apply(Optional<TextureData> $$0, ResourceManager $$1, ProfilerFiller $$2) {
        this.texture = $$0.orElse(null);
        this.needsRebuild = true;
    }

    private static boolean isCellEmpty(int $$0) {
        return ARGB.alpha($$0) < 10;
    }

    private static long packCellData(int $$0, boolean $$1, boolean $$2, boolean $$3, boolean $$4) {
        return (long)$$0 << 4 | (long)(($$1 ? 1 : 0) << 3) | (long)(($$2 ? 1 : 0) << 2) | (long)(($$3 ? 1 : 0) << 1) | (long)(($$4 ? 1 : 0) << 0);
    }

    private static boolean isNorthEmpty(long $$0) {
        return ($$0 >> 3 & 1L) != 0L;
    }

    private static boolean isEastEmpty(long $$0) {
        return ($$0 >> 2 & 1L) != 0L;
    }

    private static boolean isSouthEmpty(long $$0) {
        return ($$0 >> 1 & 1L) != 0L;
    }

    private static boolean isWestEmpty(long $$0) {
        return ($$0 >> 0 & 1L) != 0L;
    }

    public void render(int $$0, CloudStatus $$1, float $$2, Vec3 $$3, float $$4) {
        GpuTextureView $$33;
        GpuTextureView $$32;
        RenderPipeline $$22;
        RelativeCameraPos $$12;
        float $$8;
        float $$9;
        if (this.texture == null) {
            return;
        }
        int $$5 = Math.min(Minecraft.getInstance().options.cloudRange().get(), 128) * 16;
        int $$6 = Mth.ceil((float)$$5 / 12.0f);
        int $$7 = CloudRenderer.getSizeForCloudDistance($$6);
        if (this.utb == null || this.utb.currentBuffer().size() != $$7) {
            if (this.utb != null) {
                this.utb.close();
            }
            this.utb = new MappableRingBuffer(() -> "Cloud UTB", 258, $$7);
        }
        if (($$9 = ($$8 = (float)((double)$$2 - $$3.y)) + 4.0f) < 0.0f) {
            RelativeCameraPos $$10 = RelativeCameraPos.ABOVE_CLOUDS;
        } else if ($$8 > 0.0f) {
            RelativeCameraPos $$11 = RelativeCameraPos.BELOW_CLOUDS;
        } else {
            $$12 = RelativeCameraPos.INSIDE_CLOUDS;
        }
        double $$13 = $$3.x + (double)($$4 * 0.030000001f);
        double $$14 = $$3.z + (double)3.96f;
        double $$15 = (double)this.texture.width * 12.0;
        double $$16 = (double)this.texture.height * 12.0;
        $$13 -= (double)Mth.floor($$13 / $$15) * $$15;
        $$14 -= (double)Mth.floor($$14 / $$16) * $$16;
        int $$17 = Mth.floor($$13 / 12.0);
        int $$18 = Mth.floor($$14 / 12.0);
        float $$19 = (float)($$13 - (double)((float)$$17 * 12.0f));
        float $$20 = (float)($$14 - (double)((float)$$18 * 12.0f));
        boolean $$21 = $$1 == CloudStatus.FANCY;
        RenderPipeline renderPipeline = $$22 = $$21 ? RenderPipelines.CLOUDS : RenderPipelines.FLAT_CLOUDS;
        if (this.needsRebuild || $$17 != this.prevCellX || $$18 != this.prevCellZ || $$12 != this.prevRelativeCameraPos || $$1 != this.prevType) {
            this.needsRebuild = false;
            this.prevCellX = $$17;
            this.prevCellZ = $$18;
            this.prevRelativeCameraPos = $$12;
            this.prevType = $$1;
            this.utb.rotate();
            try (GpuBuffer.MappedView $$23 = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.utb.currentBuffer(), false, true);){
                this.buildMesh($$12, $$23.data(), $$17, $$18, $$21, $$6);
                this.quadCount = $$23.data().position() / 3;
            }
        }
        if (this.quadCount == 0) {
            return;
        }
        try (GpuBuffer.MappedView $$24 = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.ubo.currentBuffer(), false, true);){
            Std140Builder.intoBuffer($$24.data()).putVec4(ARGB.redFloat($$0), ARGB.greenFloat($$0), ARGB.blueFloat($$0), 1.0f).putVec3(-$$19, $$8, -$$20).putVec3(12.0f, 4.0f, 12.0f);
        }
        GpuBufferSlice $$25 = RenderSystem.getDynamicUniforms().writeTransform((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 0.0f);
        RenderTarget $$26 = Minecraft.getInstance().getMainRenderTarget();
        RenderTarget $$27 = Minecraft.getInstance().levelRenderer.getCloudsTarget();
        RenderSystem.AutoStorageIndexBuffer $$28 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        GpuBuffer $$29 = $$28.getBuffer(6 * this.quadCount);
        if ($$27 != null) {
            GpuTextureView $$30 = $$27.getColorTextureView();
            GpuTextureView $$31 = $$27.getDepthTextureView();
        } else {
            $$32 = $$26.getColorTextureView();
            $$33 = $$26.getDepthTextureView();
        }
        try (RenderPass $$34 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Clouds", $$32, OptionalInt.empty(), $$33, OptionalDouble.empty());){
            $$34.setPipeline($$22);
            RenderSystem.bindDefaultUniforms($$34);
            $$34.setUniform("DynamicTransforms", $$25);
            $$34.setIndexBuffer($$29, $$28.type());
            $$34.setVertexBuffer(0, RenderSystem.getQuadVertexBuffer());
            $$34.setUniform("CloudInfo", this.ubo.currentBuffer());
            $$34.setUniform("CloudFaces", this.utb.currentBuffer());
            $$34.setPipeline($$22);
            $$34.drawIndexed(0, 0, 6 * this.quadCount, 1);
        }
    }

    private void buildMesh(RelativeCameraPos $$0, ByteBuffer $$1, int $$2, int $$3, boolean $$4, int $$5) {
        if (this.texture == null) {
            return;
        }
        long[] $$6 = this.texture.cells;
        int $$7 = this.texture.width;
        int $$8 = this.texture.height;
        for (int $$9 = 0; $$9 <= 2 * $$5; ++$$9) {
            for (int $$10 = -$$9; $$10 <= $$9; ++$$10) {
                int $$11 = $$9 - Math.abs($$10);
                if ($$11 < 0 || $$11 > $$5 || $$10 * $$10 + $$11 * $$11 > $$5 * $$5) continue;
                if ($$11 != 0) {
                    this.a($$0, $$1, $$2, $$3, $$4, $$10, $$7, -$$11, $$8, $$6);
                }
                this.a($$0, $$1, $$2, $$3, $$4, $$10, $$7, $$11, $$8, $$6);
            }
        }
    }

    private void a(RelativeCameraPos $$0, ByteBuffer $$1, int $$2, int $$3, boolean $$4, int $$5, int $$6, int $$7, int $$8, long[] $$9) {
        int $$11;
        int $$10 = Math.floorMod($$2 + $$5, $$6);
        long $$12 = $$9[$$10 + ($$11 = Math.floorMod($$3 + $$7, $$8)) * $$6];
        if ($$12 == 0L) {
            return;
        }
        if ($$4) {
            this.buildExtrudedCell($$0, $$1, $$5, $$7, $$12);
        } else {
            this.buildFlatCell($$1, $$5, $$7);
        }
    }

    private void buildFlatCell(ByteBuffer $$0, int $$1, int $$2) {
        this.encodeFace($$0, $$1, $$2, Direction.DOWN, 32);
    }

    private void encodeFace(ByteBuffer $$0, int $$1, int $$2, Direction $$3, int $$4) {
        int $$5 = $$3.get3DDataValue() | $$4;
        $$5 |= ($$1 & 1) << 7;
        $$0.put((byte)($$1 >> 1)).put((byte)($$2 >> 1)).put((byte)($$5 |= ($$2 & 1) << 6));
    }

    private void buildExtrudedCell(RelativeCameraPos $$0, ByteBuffer $$1, int $$2, int $$3, long $$4) {
        boolean $$5;
        if ($$0 != RelativeCameraPos.BELOW_CLOUDS) {
            this.encodeFace($$1, $$2, $$3, Direction.UP, 0);
        }
        if ($$0 != RelativeCameraPos.ABOVE_CLOUDS) {
            this.encodeFace($$1, $$2, $$3, Direction.DOWN, 0);
        }
        if (CloudRenderer.isNorthEmpty($$4) && $$3 > 0) {
            this.encodeFace($$1, $$2, $$3, Direction.NORTH, 0);
        }
        if (CloudRenderer.isSouthEmpty($$4) && $$3 < 0) {
            this.encodeFace($$1, $$2, $$3, Direction.SOUTH, 0);
        }
        if (CloudRenderer.isWestEmpty($$4) && $$2 > 0) {
            this.encodeFace($$1, $$2, $$3, Direction.WEST, 0);
        }
        if (CloudRenderer.isEastEmpty($$4) && $$2 < 0) {
            this.encodeFace($$1, $$2, $$3, Direction.EAST, 0);
        }
        boolean bl = $$5 = Math.abs($$2) <= 1 && Math.abs($$3) <= 1;
        if ($$5) {
            for (Direction $$6 : Direction.values()) {
                this.encodeFace($$1, $$2, $$3, $$6, 16);
            }
        }
    }

    public void markForRebuild() {
        this.needsRebuild = true;
    }

    public void endFrame() {
        this.ubo.rotate();
    }

    @Override
    public void close() {
        this.ubo.close();
        if (this.utb != null) {
            this.utb.close();
        }
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return this.prepare(resourceManager, profilerFiller);
    }

    static final class RelativeCameraPos
    extends Enum<RelativeCameraPos> {
        public static final /* enum */ RelativeCameraPos ABOVE_CLOUDS = new RelativeCameraPos();
        public static final /* enum */ RelativeCameraPos INSIDE_CLOUDS = new RelativeCameraPos();
        public static final /* enum */ RelativeCameraPos BELOW_CLOUDS = new RelativeCameraPos();
        private static final /* synthetic */ RelativeCameraPos[] $VALUES;

        public static RelativeCameraPos[] values() {
            return (RelativeCameraPos[])$VALUES.clone();
        }

        public static RelativeCameraPos valueOf(String $$0) {
            return Enum.valueOf(RelativeCameraPos.class, $$0);
        }

        private static /* synthetic */ RelativeCameraPos[] a() {
            return new RelativeCameraPos[]{ABOVE_CLOUDS, INSIDE_CLOUDS, BELOW_CLOUDS};
        }

        static {
            $VALUES = RelativeCameraPos.a();
        }
    }

    public static final class TextureData
    extends Record {
        final long[] cells;
        final int width;
        final int height;

        public TextureData(long[] $$0, int $$1, int $$2) {
            this.cells = $$0;
            this.width = $$1;
            this.height = $$2;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{TextureData.class, "cells;width;height", "cells", "width", "height"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TextureData.class, "cells;width;height", "cells", "width", "height"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TextureData.class, "cells;width;height", "cells", "width", "height"}, this, $$0);
        }

        public long[] a() {
            return this.cells;
        }

        public int width() {
            return this.width;
        }

        public int height() {
            return this.height;
        }
    }
}

