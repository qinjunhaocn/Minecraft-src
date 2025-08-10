/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.debugchart;

import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.debugchart.AbstractDebugChart;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.debugchart.SampleStorage;
import net.minecraft.util.debugchart.TpsDebugDimensions;

public class TpsDebugChart
extends AbstractDebugChart {
    private static final int TICK_METHOD_COLOR = -6745839;
    private static final int TASK_COLOR = -4548257;
    private static final int OTHER_COLOR = -10547572;
    private final Supplier<Float> msptSupplier;

    public TpsDebugChart(Font $$0, SampleStorage $$1, Supplier<Float> $$2) {
        super($$0, $$1);
        this.msptSupplier = $$2;
    }

    @Override
    protected void renderAdditionalLinesAndLabels(GuiGraphics $$0, int $$1, int $$2, int $$3) {
        float $$4 = (float)TimeUtil.MILLISECONDS_PER_SECOND / this.msptSupplier.get().floatValue();
        this.drawStringWithShade($$0, String.format(Locale.ROOT, "%.1f TPS", Float.valueOf($$4)), $$1 + 1, $$3 - 60 + 1);
    }

    @Override
    protected void drawAdditionalDimensions(GuiGraphics $$0, int $$1, int $$2, int $$3) {
        long $$4 = this.sampleStorage.get($$3, TpsDebugDimensions.TICK_SERVER_METHOD.ordinal());
        int $$5 = this.getSampleHeight($$4);
        $$0.fill($$2, $$1 - $$5, $$2 + 1, $$1, -6745839);
        long $$6 = this.sampleStorage.get($$3, TpsDebugDimensions.SCHEDULED_TASKS.ordinal());
        int $$7 = this.getSampleHeight($$6);
        $$0.fill($$2, $$1 - $$5 - $$7, $$2 + 1, $$1 - $$5, -4548257);
        long $$8 = this.sampleStorage.get($$3) - this.sampleStorage.get($$3, TpsDebugDimensions.IDLE.ordinal()) - $$4 - $$6;
        int $$9 = this.getSampleHeight($$8);
        $$0.fill($$2, $$1 - $$9 - $$7 - $$5, $$2 + 1, $$1 - $$7 - $$5, -10547572);
    }

    @Override
    protected long getValueForAggregation(int $$0) {
        return this.sampleStorage.get($$0) - this.sampleStorage.get($$0, TpsDebugDimensions.IDLE.ordinal());
    }

    @Override
    protected String toDisplayString(double $$0) {
        return String.format(Locale.ROOT, "%d ms", (int)Math.round(TpsDebugChart.toMilliseconds($$0)));
    }

    @Override
    protected int getSampleHeight(double $$0) {
        return (int)Math.round(TpsDebugChart.toMilliseconds($$0) * 60.0 / (double)this.msptSupplier.get().floatValue());
    }

    @Override
    protected int getSampleColor(long $$0) {
        float $$1 = this.msptSupplier.get().floatValue();
        return this.getSampleColor(TpsDebugChart.toMilliseconds($$0), $$1, -16711936, (double)$$1 * 1.125, -256, (double)$$1 * 1.25, -65536);
    }

    private static double toMilliseconds(double $$0) {
        return $$0 / 1000000.0;
    }
}

