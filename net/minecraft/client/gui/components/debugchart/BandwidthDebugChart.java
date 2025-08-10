/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.debugchart;

import java.util.Locale;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.debugchart.AbstractDebugChart;
import net.minecraft.util.Mth;
import net.minecraft.util.debugchart.SampleStorage;

public class BandwidthDebugChart
extends AbstractDebugChart {
    private static final int MIN_COLOR = -16711681;
    private static final int MID_COLOR = -6250241;
    private static final int MAX_COLOR = -65536;
    private static final int KILOBYTE = 1024;
    private static final int MEGABYTE = 0x100000;
    private static final int CHART_TOP_VALUE = 0x100000;

    public BandwidthDebugChart(Font $$0, SampleStorage $$1) {
        super($$0, $$1);
    }

    @Override
    protected void renderAdditionalLinesAndLabels(GuiGraphics $$0, int $$1, int $$2, int $$3) {
        this.drawLabeledLineAtValue($$0, $$1, $$2, $$3, 64);
        this.drawLabeledLineAtValue($$0, $$1, $$2, $$3, 1024);
        this.drawLabeledLineAtValue($$0, $$1, $$2, $$3, 16384);
        this.drawStringWithShade($$0, BandwidthDebugChart.toDisplayStringInternal(1048576.0), $$1 + 1, $$3 - BandwidthDebugChart.getSampleHeightInternal(1048576.0) + 1);
    }

    private void drawLabeledLineAtValue(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4) {
        this.drawLineWithLabel($$0, $$1, $$2, $$3 - BandwidthDebugChart.getSampleHeightInternal($$4), BandwidthDebugChart.toDisplayStringInternal($$4));
    }

    private void drawLineWithLabel(GuiGraphics $$0, int $$1, int $$2, int $$3, String $$4) {
        this.drawStringWithShade($$0, $$4, $$1 + 1, $$3 + 1);
        $$0.hLine($$1, $$1 + $$2 - 1, $$3, -1);
    }

    @Override
    protected String toDisplayString(double $$0) {
        return BandwidthDebugChart.toDisplayStringInternal(BandwidthDebugChart.toBytesPerSecond($$0));
    }

    private static String toDisplayStringInternal(double $$0) {
        if ($$0 >= 1048576.0) {
            return String.format(Locale.ROOT, "%.1f MiB/s", $$0 / 1048576.0);
        }
        if ($$0 >= 1024.0) {
            return String.format(Locale.ROOT, "%.1f KiB/s", $$0 / 1024.0);
        }
        return String.format(Locale.ROOT, "%d B/s", Mth.floor($$0));
    }

    @Override
    protected int getSampleHeight(double $$0) {
        return BandwidthDebugChart.getSampleHeightInternal(BandwidthDebugChart.toBytesPerSecond($$0));
    }

    private static int getSampleHeightInternal(double $$0) {
        return (int)Math.round(Math.log($$0 + 1.0) * 60.0 / Math.log(1048576.0));
    }

    @Override
    protected int getSampleColor(long $$0) {
        return this.getSampleColor(BandwidthDebugChart.toBytesPerSecond($$0), 0.0, -16711681, 8192.0, -6250241, 1.048576E7, -65536);
    }

    private static double toBytesPerSecond(double $$0) {
        return $$0 * 20.0;
    }
}

