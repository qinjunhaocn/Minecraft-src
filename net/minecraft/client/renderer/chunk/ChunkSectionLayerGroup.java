/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.pipeline.RenderTarget;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public final class ChunkSectionLayerGroup
extends Enum<ChunkSectionLayerGroup> {
    public static final /* enum */ ChunkSectionLayerGroup OPAQUE = new ChunkSectionLayerGroup(ChunkSectionLayer.SOLID, ChunkSectionLayer.CUTOUT_MIPPED, ChunkSectionLayer.CUTOUT);
    public static final /* enum */ ChunkSectionLayerGroup TRANSLUCENT = new ChunkSectionLayerGroup(ChunkSectionLayer.TRANSLUCENT);
    public static final /* enum */ ChunkSectionLayerGroup TRIPWIRE = new ChunkSectionLayerGroup(ChunkSectionLayer.TRIPWIRE);
    private final String label;
    private final ChunkSectionLayer[] layers;
    private static final /* synthetic */ ChunkSectionLayerGroup[] $VALUES;

    public static ChunkSectionLayerGroup[] values() {
        return (ChunkSectionLayerGroup[])$VALUES.clone();
    }

    public static ChunkSectionLayerGroup valueOf(String $$0) {
        return Enum.valueOf(ChunkSectionLayerGroup.class, $$0);
    }

    private ChunkSectionLayerGroup(ChunkSectionLayer ... $$0) {
        this.layers = $$0;
        this.label = this.toString().toLowerCase(Locale.ROOT);
    }

    public String label() {
        return this.label;
    }

    public ChunkSectionLayer[] b() {
        return this.layers;
    }

    public RenderTarget outputTarget() {
        Minecraft $$0 = Minecraft.getInstance();
        RenderTarget $$1 = switch (this.ordinal()) {
            case 2 -> $$0.levelRenderer.getWeatherTarget();
            case 1 -> $$0.levelRenderer.getTranslucentTarget();
            default -> $$0.getMainRenderTarget();
        };
        return $$1 != null ? $$1 : $$0.getMainRenderTarget();
    }

    private static /* synthetic */ ChunkSectionLayerGroup[] d() {
        return new ChunkSectionLayerGroup[]{OPAQUE, TRANSLUCENT, TRIPWIRE};
    }

    static {
        $VALUES = ChunkSectionLayerGroup.d();
    }
}

