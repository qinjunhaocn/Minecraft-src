/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.debugchart;

import java.util.Locale;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.debugchart.AbstractDebugChart;
import net.minecraft.util.debugchart.SampleStorage;

public class PingDebugChart
extends AbstractDebugChart {
    private static final int CHART_TOP_VALUE = 500;

    public PingDebugChart(Font $$0, SampleStorage $$1) {
        super($$0, $$1);
    }

    @Override
    protected void renderAdditionalLinesAndLabels(GuiGraphics $$0, int $$1, int $$2, int $$3) {
        this.drawStringWithShade($$0, "500 ms", $$1 + 1, $$3 - 60 + 1);
    }

    @Override
    protected String toDisplayString(double $$0) {
        return String.format(Locale.ROOT, "%d ms", (int)Math.round($$0));
    }

    @Override
    protected int getSampleHeight(double $$0) {
        return (int)Math.round($$0 * 60.0 / 500.0);
    }

    @Override
    protected int getSampleColor(long $$0) {
        return this.getSampleColor($$0, 0.0, -16711936, 250.0, -256, 500.0, -65536);
    }
}

