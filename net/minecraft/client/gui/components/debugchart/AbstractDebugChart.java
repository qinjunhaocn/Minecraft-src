/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.debugchart;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.debugchart.SampleStorage;

public abstract class AbstractDebugChart {
    protected static final int COLOR_GREY = -2039584;
    protected static final int CHART_HEIGHT = 60;
    protected static final int LINE_WIDTH = 1;
    protected final Font font;
    protected final SampleStorage sampleStorage;

    protected AbstractDebugChart(Font $$0, SampleStorage $$1) {
        this.font = $$0;
        this.sampleStorage = $$1;
    }

    public int getWidth(int $$0) {
        return Math.min(this.sampleStorage.capacity() + 2, $$0);
    }

    public int getFullHeight() {
        return 60 + this.font.lineHeight;
    }

    public void drawChart(GuiGraphics $$0, int $$1, int $$2) {
        int $$3 = $$0.guiHeight();
        $$0.fill($$1, $$3 - 60, $$1 + $$2, $$3, -1873784752);
        long $$4 = 0L;
        long $$5 = Integer.MAX_VALUE;
        long $$6 = Integer.MIN_VALUE;
        int $$7 = Math.max(0, this.sampleStorage.capacity() - ($$2 - 2));
        int $$8 = this.sampleStorage.size() - $$7;
        for (int $$9 = 0; $$9 < $$8; ++$$9) {
            int $$10 = $$1 + $$9 + 1;
            int $$11 = $$7 + $$9;
            long $$12 = this.getValueForAggregation($$11);
            $$5 = Math.min($$5, $$12);
            $$6 = Math.max($$6, $$12);
            $$4 += $$12;
            this.drawDimensions($$0, $$3, $$10, $$11);
        }
        $$0.hLine($$1, $$1 + $$2 - 1, $$3 - 60, -1);
        $$0.hLine($$1, $$1 + $$2 - 1, $$3 - 1, -1);
        $$0.vLine($$1, $$3 - 60, $$3, -1);
        $$0.vLine($$1 + $$2 - 1, $$3 - 60, $$3, -1);
        if ($$8 > 0) {
            String $$13 = this.toDisplayString($$5) + " min";
            String $$14 = this.toDisplayString((double)$$4 / (double)$$8) + " avg";
            String $$15 = this.toDisplayString($$6) + " max";
            $$0.drawString(this.font, $$13, $$1 + 2, $$3 - 60 - this.font.lineHeight, -2039584);
            $$0.drawCenteredString(this.font, $$14, $$1 + $$2 / 2, $$3 - 60 - this.font.lineHeight, -2039584);
            $$0.drawString(this.font, $$15, $$1 + $$2 - this.font.width($$15) - 2, $$3 - 60 - this.font.lineHeight, -2039584);
        }
        this.renderAdditionalLinesAndLabels($$0, $$1, $$2, $$3);
    }

    protected void drawDimensions(GuiGraphics $$0, int $$1, int $$2, int $$3) {
        this.drawMainDimension($$0, $$1, $$2, $$3);
        this.drawAdditionalDimensions($$0, $$1, $$2, $$3);
    }

    protected void drawMainDimension(GuiGraphics $$0, int $$1, int $$2, int $$3) {
        long $$4 = this.sampleStorage.get($$3);
        int $$5 = this.getSampleHeight($$4);
        int $$6 = this.getSampleColor($$4);
        $$0.fill($$2, $$1 - $$5, $$2 + 1, $$1, $$6);
    }

    protected void drawAdditionalDimensions(GuiGraphics $$0, int $$1, int $$2, int $$3) {
    }

    protected long getValueForAggregation(int $$0) {
        return this.sampleStorage.get($$0);
    }

    protected void renderAdditionalLinesAndLabels(GuiGraphics $$0, int $$1, int $$2, int $$3) {
    }

    protected void drawStringWithShade(GuiGraphics $$0, String $$1, int $$2, int $$3) {
        $$0.fill($$2, $$3, $$2 + this.font.width($$1) + 1, $$3 + this.font.lineHeight, -1873784752);
        $$0.drawString(this.font, $$1, $$2 + 1, $$3 + 1, -2039584, false);
    }

    protected abstract String toDisplayString(double var1);

    protected abstract int getSampleHeight(double var1);

    protected abstract int getSampleColor(long var1);

    protected int getSampleColor(double $$0, double $$1, int $$2, double $$3, int $$4, double $$5, int $$6) {
        if (($$0 = Mth.clamp($$0, $$1, $$5)) < $$3) {
            return ARGB.lerp((float)(($$0 - $$1) / ($$3 - $$1)), $$2, $$4);
        }
        return ARGB.lerp((float)(($$0 - $$3) / ($$5 - $$3)), $$4, $$6);
    }
}

