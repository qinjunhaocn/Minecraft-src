/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.client.renderer.texture.SpriteTicker;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.ARGB;
import org.slf4j.Logger;

public class SpriteContents
implements Stitcher.Entry,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation name;
    final int width;
    final int height;
    private final NativeImage originalImage;
    NativeImage[] byMipLevel;
    @Nullable
    private final AnimatedTexture animatedTexture;
    private final ResourceMetadata metadata;

    public SpriteContents(ResourceLocation $$0, FrameSize $$1, NativeImage $$22, ResourceMetadata $$3) {
        this.name = $$0;
        this.width = $$1.width();
        this.height = $$1.height();
        this.metadata = $$3;
        this.animatedTexture = $$3.getSection(AnimationMetadataSection.TYPE).map($$2 -> this.createAnimatedTexture($$1, $$22.getWidth(), $$22.getHeight(), (AnimationMetadataSection)((Object)$$2))).orElse(null);
        this.originalImage = $$22;
        this.byMipLevel = new NativeImage[]{this.originalImage};
    }

    public void increaseMipLevel(int $$0) {
        try {
            this.byMipLevel = MipmapGenerator.a(this.byMipLevel, $$0);
        } catch (Throwable $$1) {
            CrashReport $$2 = CrashReport.forThrowable($$1, "Generating mipmaps for frame");
            CrashReportCategory $$3 = $$2.addCategory("Sprite being mipmapped");
            $$3.setDetail("First frame", () -> {
                StringBuilder $$0 = new StringBuilder();
                if ($$0.length() > 0) {
                    $$0.append(", ");
                }
                $$0.append(this.originalImage.getWidth()).append("x").append(this.originalImage.getHeight());
                return $$0.toString();
            });
            CrashReportCategory $$4 = $$2.addCategory("Frame being iterated");
            $$4.setDetail("Sprite name", this.name);
            $$4.setDetail("Sprite size", () -> this.width + " x " + this.height);
            $$4.setDetail("Sprite frames", () -> this.getFrameCount() + " frames");
            $$4.setDetail("Mipmap levels", $$0);
            throw new ReportedException($$2);
        }
    }

    private int getFrameCount() {
        return this.animatedTexture != null ? this.animatedTexture.frames.size() : 1;
    }

    @Nullable
    private AnimatedTexture createAnimatedTexture(FrameSize $$0, int $$1, int $$2, AnimationMetadataSection $$3) {
        ArrayList<FrameInfo> $$11;
        int $$4 = $$1 / $$0.width();
        int $$5 = $$2 / $$0.height();
        int $$6 = $$4 * $$5;
        int $$7 = $$3.defaultFrameTime();
        if ($$3.frames().isEmpty()) {
            ArrayList<FrameInfo> $$8 = new ArrayList<FrameInfo>($$6);
            for (int $$9 = 0; $$9 < $$6; ++$$9) {
                $$8.add(new FrameInfo($$9, $$7));
            }
        } else {
            List<AnimationFrame> $$10 = $$3.frames().get();
            $$11 = new ArrayList<FrameInfo>($$10.size());
            for (AnimationFrame $$12 : $$10) {
                $$11.add(new FrameInfo($$12.index(), $$12.timeOr($$7)));
            }
            int $$13 = 0;
            IntOpenHashSet $$14 = new IntOpenHashSet();
            Iterator $$15 = $$11.iterator();
            while ($$15.hasNext()) {
                FrameInfo $$16 = (FrameInfo)((Object)$$15.next());
                boolean $$17 = true;
                if ($$16.time <= 0) {
                    LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", this.name, $$13, $$16.time);
                    $$17 = false;
                }
                if ($$16.index < 0 || $$16.index >= $$6) {
                    LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", this.name, $$13, $$16.index);
                    $$17 = false;
                }
                if ($$17) {
                    $$14.add($$16.index);
                } else {
                    $$15.remove();
                }
                ++$$13;
            }
            int[] $$18 = IntStream.range(0, $$6).filter(arg_0 -> SpriteContents.lambda$createAnimatedTexture$4((IntSet)$$14, arg_0)).toArray();
            if ($$18.length > 0) {
                LOGGER.warn("Unused frames in sprite {}: {}", (Object)this.name, (Object)Arrays.toString($$18));
            }
        }
        if ($$11.size() <= 1) {
            return null;
        }
        return new AnimatedTexture(List.copyOf($$11), $$4, $$3.interpolatedFrames());
    }

    void a(int $$0, int $$1, int $$2, int $$3, NativeImage[] $$4, GpuTexture $$5) {
        for (int $$6 = 0; $$6 < this.byMipLevel.length; ++$$6) {
            RenderSystem.getDevice().createCommandEncoder().writeToTexture($$5, $$4[$$6], $$6, 0, $$0 >> $$6, $$1 >> $$6, this.width >> $$6, this.height >> $$6, $$2 >> $$6, $$3 >> $$6);
        }
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    @Override
    public ResourceLocation name() {
        return this.name;
    }

    public IntStream getUniqueFrames() {
        return this.animatedTexture != null ? this.animatedTexture.getUniqueFrames() : IntStream.of(1);
    }

    @Nullable
    public SpriteTicker createTicker() {
        return this.animatedTexture != null ? this.animatedTexture.createTicker() : null;
    }

    public ResourceMetadata metadata() {
        return this.metadata;
    }

    @Override
    public void close() {
        for (NativeImage $$0 : this.byMipLevel) {
            $$0.close();
        }
    }

    public String toString() {
        return "SpriteContents{name=" + String.valueOf(this.name) + ", frameCount=" + this.getFrameCount() + ", height=" + this.height + ", width=" + this.width + "}";
    }

    public boolean isTransparent(int $$0, int $$1, int $$2) {
        int $$3 = $$1;
        int $$4 = $$2;
        if (this.animatedTexture != null) {
            $$3 += this.animatedTexture.getFrameX($$0) * this.width;
            $$4 += this.animatedTexture.getFrameY($$0) * this.height;
        }
        return ARGB.alpha(this.originalImage.getPixel($$3, $$4)) == 0;
    }

    public void uploadFirstFrame(int $$0, int $$1, GpuTexture $$2) {
        if (this.animatedTexture != null) {
            this.animatedTexture.uploadFirstFrame($$0, $$1, $$2);
        } else {
            this.a($$0, $$1, 0, 0, this.byMipLevel, $$2);
        }
    }

    private static /* synthetic */ boolean lambda$createAnimatedTexture$4(IntSet $$0, int $$1) {
        return !$$0.contains($$1);
    }

    class AnimatedTexture {
        final List<FrameInfo> frames;
        private final int frameRowSize;
        private final boolean interpolateFrames;

        AnimatedTexture(List<FrameInfo> $$0, int $$1, boolean $$2) {
            this.frames = $$0;
            this.frameRowSize = $$1;
            this.interpolateFrames = $$2;
        }

        int getFrameX(int $$0) {
            return $$0 % this.frameRowSize;
        }

        int getFrameY(int $$0) {
            return $$0 / this.frameRowSize;
        }

        void uploadFrame(int $$0, int $$1, int $$2, GpuTexture $$3) {
            int $$4 = this.getFrameX($$2) * SpriteContents.this.width;
            int $$5 = this.getFrameY($$2) * SpriteContents.this.height;
            SpriteContents.this.a($$0, $$1, $$4, $$5, SpriteContents.this.byMipLevel, $$3);
        }

        public SpriteTicker createTicker() {
            return new Ticker(SpriteContents.this, this, this.interpolateFrames ? new InterpolationData() : null);
        }

        public void uploadFirstFrame(int $$0, int $$1, GpuTexture $$2) {
            this.uploadFrame($$0, $$1, this.frames.get((int)0).index, $$2);
        }

        public IntStream getUniqueFrames() {
            return this.frames.stream().mapToInt($$0 -> $$0.index).distinct();
        }
    }

    static final class FrameInfo
    extends Record {
        final int index;
        final int time;

        FrameInfo(int $$0, int $$1) {
            this.index = $$0;
            this.time = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{FrameInfo.class, "index;time", "index", "time"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FrameInfo.class, "index;time", "index", "time"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FrameInfo.class, "index;time", "index", "time"}, this, $$0);
        }

        public int index() {
            return this.index;
        }

        public int time() {
            return this.time;
        }
    }

    class Ticker
    implements SpriteTicker {
        int frame;
        int subFrame;
        final AnimatedTexture animationInfo;
        @Nullable
        private final InterpolationData interpolationData;

        Ticker(SpriteContents spriteContents, @Nullable AnimatedTexture $$0, InterpolationData $$1) {
            this.animationInfo = $$0;
            this.interpolationData = $$1;
        }

        @Override
        public void tickAndUpload(int $$0, int $$1, GpuTexture $$2) {
            ++this.subFrame;
            FrameInfo $$3 = this.animationInfo.frames.get(this.frame);
            if (this.subFrame >= $$3.time) {
                int $$4 = $$3.index;
                this.frame = (this.frame + 1) % this.animationInfo.frames.size();
                this.subFrame = 0;
                int $$5 = this.animationInfo.frames.get((int)this.frame).index;
                if ($$4 != $$5) {
                    this.animationInfo.uploadFrame($$0, $$1, $$5, $$2);
                }
            } else if (this.interpolationData != null) {
                this.interpolationData.uploadInterpolatedFrame($$0, $$1, this, $$2);
            }
        }

        @Override
        public void close() {
            if (this.interpolationData != null) {
                this.interpolationData.close();
            }
        }
    }

    final class InterpolationData
    implements AutoCloseable {
        private final NativeImage[] activeFrame;

        InterpolationData() {
            this.activeFrame = new NativeImage[SpriteContents.this.byMipLevel.length];
            for (int $$0 = 0; $$0 < this.activeFrame.length; ++$$0) {
                int $$1 = SpriteContents.this.width >> $$0;
                int $$2 = SpriteContents.this.height >> $$0;
                this.activeFrame[$$0] = new NativeImage($$1, $$2, false);
            }
        }

        void uploadInterpolatedFrame(int $$0, int $$1, Ticker $$2, GpuTexture $$3) {
            AnimatedTexture $$4 = $$2.animationInfo;
            List<FrameInfo> $$5 = $$4.frames;
            FrameInfo $$6 = $$5.get($$2.frame);
            float $$7 = (float)$$2.subFrame / (float)$$6.time;
            int $$8 = $$6.index;
            int $$9 = $$5.get((int)(($$2.frame + 1) % $$5.size())).index;
            if ($$8 != $$9) {
                for (int $$10 = 0; $$10 < this.activeFrame.length; ++$$10) {
                    int $$11 = SpriteContents.this.width >> $$10;
                    int $$12 = SpriteContents.this.height >> $$10;
                    for (int $$13 = 0; $$13 < $$12; ++$$13) {
                        for (int $$14 = 0; $$14 < $$11; ++$$14) {
                            int $$15 = this.getPixel($$4, $$8, $$10, $$14, $$13);
                            int $$16 = this.getPixel($$4, $$9, $$10, $$14, $$13);
                            this.activeFrame[$$10].setPixel($$14, $$13, ARGB.lerp($$7, $$15, $$16));
                        }
                    }
                }
                SpriteContents.this.a($$0, $$1, 0, 0, this.activeFrame, $$3);
            }
        }

        private int getPixel(AnimatedTexture $$0, int $$1, int $$2, int $$3, int $$4) {
            return SpriteContents.this.byMipLevel[$$2].getPixel($$3 + ($$0.getFrameX($$1) * SpriteContents.this.width >> $$2), $$4 + ($$0.getFrameY($$1) * SpriteContents.this.height >> $$2));
        }

        @Override
        public void close() {
            for (NativeImage $$0 : this.activeFrame) {
                $$0.close();
            }
        }
    }
}

