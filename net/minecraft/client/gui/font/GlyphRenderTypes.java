/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.gui.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public record GlyphRenderTypes(RenderType normal, RenderType seeThrough, RenderType polygonOffset, RenderPipeline guiPipeline) {
    public static GlyphRenderTypes createForIntensityTexture(ResourceLocation $$0) {
        return new GlyphRenderTypes(RenderType.textIntensity($$0), RenderType.textIntensitySeeThrough($$0), RenderType.textIntensityPolygonOffset($$0), RenderPipelines.TEXT_INTENSITY);
    }

    public static GlyphRenderTypes createForColorTexture(ResourceLocation $$0) {
        return new GlyphRenderTypes(RenderType.text($$0), RenderType.textSeeThrough($$0), RenderType.textPolygonOffset($$0), RenderPipelines.TEXT);
    }

    public RenderType select(Font.DisplayMode $$0) {
        return switch ($$0) {
            default -> throw new MatchException(null, null);
            case Font.DisplayMode.NORMAL -> this.normal;
            case Font.DisplayMode.SEE_THROUGH -> this.seeThrough;
            case Font.DisplayMode.POLYGON_OFFSET -> this.polygonOffset;
        };
    }
}

