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

public record ColoredRectangleRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, int col1, int col2, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements GuiElementRenderState
{
    public ColoredRectangleRenderState(RenderPipeline $$0, TextureSetup $$1, Matrix3x2f $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, @Nullable ScreenRectangle $$9) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, ColoredRectangleRenderState.getBounds($$3, $$4, $$5, $$6, $$2, $$9));
    }

    @Override
    public void buildVertices(VertexConsumer $$0, float $$1) {
        $$0.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), $$1).setColor(this.col1());
        $$0.addVertexWith2DPose(this.pose(), this.x0(), this.y1(), $$1).setColor(this.col2());
        $$0.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), $$1).setColor(this.col2());
        $$0.addVertexWith2DPose(this.pose(), this.x1(), this.y0(), $$1).setColor(this.col1());
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

