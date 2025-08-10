/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.debugchart;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.debugchart.AbstractDebugChart;
import net.minecraft.util.debugchart.SampleStorage;

public class FpsDebugChart
extends AbstractDebugChart {
    private static final int CHART_TOP_FPS = 30;
    private static final double CHART_TOP_VALUE = 33.333333333333336;

    public FpsDebugChart(Font $$0, SampleStorage $$1) {
        super($$0, $$1);
    }

    @Override
    protected void renderAdditionalLinesAndLabels(GuiGraphics $$0, int $$1, int $$2, int $$3) {
        this.drawStringWithShade($$0, "30 FPS", $$1 + 1, $$3 - 60 + 1);
        this.drawStringWithShade($$0, "60 FPS", $$1 + 1, $$3 - 30 + 1);
        $$0.hLine($$1, $$1 + $$2 - 1, $$3 - 30, -1);
        int $$4 = Minecraft.getInstance().options.framerateLimit().get();
        if ($$4 > 0 && $$4 <= 250) {
            $$0.hLine($$1, $$1 + $$2 - 1, $$3 - this.getSampleHeight(1.0E9 / (double)$$4) - 1, -16711681);
        }
    }

    @Override
    protected String toDisplayString(double $$0) {
        return String.format(Locale.ROOT, "%d ms", (int)Math.round(FpsDebugChart.toMilliseconds($$0)));
    }

    @Override
    protected int getSampleHeight(double $$0) {
        return (int)Math.round(FpsDebugChart.toMilliseconds($$0) * 60.0 / 33.333333333333336);
    }

    @Override
    protected int getSampleColor(long $$0) {
        return this.getSampleColor(FpsDebugChart.toMilliseconds($$0), 0.0, -16711936, 28.0, -256, 56.0, -65536);
    }

    private static double toMilliseconds(double $$0) {
        return $$0 / 1000000.0;
    }
}

