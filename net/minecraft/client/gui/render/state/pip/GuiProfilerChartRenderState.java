/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.render.state.pip;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.util.profiling.ResultField;

public record GuiProfilerChartRenderState(List<ResultField> chartData, int x0, int y0, int x1, int y1, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState
{
    public GuiProfilerChartRenderState(List<ResultField> $$0, int $$1, int $$2, int $$3, int $$4, @Nullable ScreenRectangle $$5) {
        this($$0, $$1, $$2, $$3, $$4, $$5, PictureInPictureRenderState.getBounds($$1, $$2, $$3, $$4, $$5));
    }

    @Override
    public float scale() {
        return 1.0f;
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

