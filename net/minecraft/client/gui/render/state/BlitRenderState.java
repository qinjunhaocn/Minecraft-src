/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix3x2f
 */
package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.joml.Matrix3x2f;

public record BlitRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements GuiElementRenderState
{
    public BlitRenderState(RenderPipeline $$0, TextureSetup $$1, Matrix3x2f $$2, int $$3, int $$4, int $$5, int $$6, float $$7, float $$8, float $$9, float $$10, int $$11, @Nullable ScreenRectangle $$12) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11, $$12, BlitRenderState.getBounds($$3, $$4, $$5, $$6, $$2, $$12));
    }

    @Override
    public void buildVertices(VertexConsumer $$0, float $$1) {
        $$0.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), $$1).setUv(this.u0(), this.v0()).setColor(this.color());
        $$0.addVertexWith2DPose(this.pose(), this.x0(), this.y1(), $$1).setUv(this.u0(), this.v1()).setColor(this.color());
        $$0.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), $$1).setUv(this.u1(), this.v1()).setColor(this.color());
        $$0.addVertexWith2DPose(this.pose(), this.x1(), this.y0(), $$1).setUv(this.u1(), this.v0()).setColor(this.color());
    }

    @Nullable
    private static ScreenRectangle getBounds(int $$0, int $$1, int $$2, int $$3, Matrix3x2f $$4, @Nullable ScreenRectangle $$5) {
        ScreenRectangle $$6 = new ScreenRectangle($$0, $$1, $$2 - $$0, $$3 - $$1).transformMaxBounds($$4);
        return $$5 != null ? $$5.intersection($$6) : $$6;
    }

    @Override
    @Nullable
    public ScreenRectangle scissorArea() {
        return this.scissorArea;
    }

    @Override
    @Nullable
    public ScreenRectangle bounds() {
        return this.bounds;
    }
}

