/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public final class ChunkSectionLayer
extends Enum<ChunkSectionLayer> {
    public static final /* enum */ ChunkSectionLayer SOLID = new ChunkSectionLayer(RenderPipelines.SOLID, 0x400000, true, false);
    public static final /* enum */ ChunkSectionLayer CUTOUT_MIPPED = new ChunkSectionLayer(RenderPipelines.CUTOUT_MIPPED, 0x400000, true, false);
    public static final /* enum */ ChunkSectionLayer CUTOUT = new ChunkSectionLayer(RenderPipelines.CUTOUT, 786432, false, false);
    public static final /* enum */ ChunkSectionLayer TRANSLUCENT = new ChunkSectionLayer(RenderPipelines.TRANSLUCENT, 786432, true, true);
    public static final /* enum */ ChunkSectionLayer TRIPWIRE = new ChunkSectionLayer(RenderPipelines.TRIPWIRE, 1536, true, true);
    private final RenderPipeline pipeline;
    private final int bufferSize;
    private final boolean useMipmaps;
    private final boolean sortOnUpload;
    private final String label;
    private static final /* synthetic */ ChunkSectionLayer[] $VALUES;

    public static ChunkSectionLayer[] values() {
        return (ChunkSectionLayer[])$VALUES.clone();
    }

    public static ChunkSectionLayer valueOf(String $$0) {
        return Enum.valueOf(ChunkSectionLayer.class, $$0);
    }

    private ChunkSectionLayer(RenderPipeline $$0, int $$1, boolean $$2, boolean $$3) {
        this.pipeline = $$0;
        this.bufferSize = $$1;
        this.useMipmaps = $$2;
        this.sortOnUpload = $$3;
        this.label = this.toString().toLowerCase(Locale.ROOT);
    }

    public RenderPipeline pipeline() {
        return this.pipeline;
    }

    public int bufferSize() {
        return this.bufferSize;
    }

    public String label() {
        return this.label;
    }

    public boolean sortOnUpload() {
        return this.sortOnUpload;
    }

    public GpuTextureView textureView() {
        TextureManager $$0 = Minecraft.getInstance().getTextureManager();
        AbstractTexture $$1 = $$0.getTexture(TextureAtlas.LOCATION_BLOCKS);
        $$1.setUseMipmaps(this.useMipmaps);
        return $$1.getTextureView();
    }

    public RenderTarget outputTarget() {
        Minecraft $$0 = Minecraft.getInstance();
        switch (this.ordinal()) {
            case 4: {
                RenderTarget $$1 = $$0.levelRenderer.getWeatherTarget();
                return $$1 != null ? $$1 : $$0.getMainRenderTarget();
            }
            case 3: {
                RenderTarget $$2 = $$0.levelRenderer.getTranslucentTarget();
                return $$2 != null ? $$2 : $$0.getMainRenderTarget();
            }
        }
        return $$0.getMainRenderTarget();
    }

    private static /* synthetic */ ChunkSectionLayer[] g() {
        return new ChunkSectionLayer[]{SOLID, CUTOUT_MIPPED, CUTOUT, TRANSLUCENT, TRIPWIRE};
    }

    static {
        $VALUES = ChunkSectionLayer.g();
    }
}

