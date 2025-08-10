/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.ScreenArea;

public interface GuiElementRenderState
extends ScreenArea {
    public void buildVertices(VertexConsumer var1, float var2);

    public RenderPipeline pipeline();

    public TextureSetup textureSetup();

    @Nullable
    public ScreenRectangle scissorArea();
}

