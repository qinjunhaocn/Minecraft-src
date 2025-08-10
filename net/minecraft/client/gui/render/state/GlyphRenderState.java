/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fc
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;

public record GlyphRenderState(Matrix3x2f pose, BakedGlyph.GlyphInstance instance, @Nullable ScreenRectangle scissorArea) implements GuiElementRenderState
{
    @Override
    public void buildVertices(VertexConsumer $$0, float $$1) {
        Matrix4f $$2 = new Matrix4f().mul((Matrix3x2fc)this.pose).translate(0.0f, 0.0f, $$1);
        this.instance.glyph().renderChar(this.instance, $$2, $$0, 0xF000F0, true);
    }

    @Override
    public RenderPipeline pipeline() {
        return this.instance.glyph().guiPipeline();
    }

    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.singleTextureWithLightmap(Objects.requireNonNull(this.instance.glyph().textureView()));
    }

    @Override
    @Nullable
    public ScreenRectangle bounds() {
        return null;
    }

    @Override
    @Nullable
    public ScreenRectangle scissorArea() {
        return this.scissorArea;
    }
}

