/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.debugchart;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ResultField;

public class ProfilerPieChart {
    public static final int RADIUS = 105;
    public static final int PIE_CHART_THICKNESS = 10;
    private static final int MARGIN = 5;
    private final Font font;
    @Nullable
    private ProfileResults profilerPieChartResults;
    private String profilerTreePath = "root";
    private int bottomOffset = 0;

    public ProfilerPieChart(Font $$0) {
        this.font = $$0;
    }

    public void setPieChartResults(@Nullable ProfileResults $$0) {
        this.profilerPieChartResults = $$0;
    }

    public void setBottomOffset(int $$0) {
        this.bottomOffset = $$0;
    }

    public void render(GuiGraphics $$0) {
        if (this.profilerPieChartResults == null) {
            return;
        }
        List<ResultField> $$1 = this.profilerPieChartResults.getTimes(this.profilerTreePath);
        ResultField $$2 = (ResultField)$$1.removeFirst();
        int $$3 = $$0.guiWidth() - 105 - 10;
        int $$4 = $$3 - 105;
        int $$5 = $$3 + 105;
        int $$6 = $$1.size() * this.font.lineHeight;
        int $$7 = $$0.guiHeight() - this.bottomOffset - 5;
        int $$8 = $$7 - $$6;
        int $$9 = 62;
        int $$10 = $$8 - 62 - 5;
        $$0.fill($$4 - 5, $$10 - 62 - 5, $$5 + 5, $$7 + 5, -1873784752);
        $$0.submitProfilerChartRenderState($$1, $$4, $$10 - 62 + 10, $$5, $$10 + 62);
        DecimalFormat $$11 = new DecimalFormat("##0.00");
        $$11.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        String $$12 = ProfileResults.demanglePath($$2.name);
        Object $$13 = "";
        if (!"unspecified".equals($$12)) {
            $$13 = (String)$$13 + "[0] ";
        }
        $$13 = $$12.isEmpty() ? (String)$$13 + "ROOT " : (String)$$13 + $$12 + " ";
        int $$14 = -1;
        int $$15 = $$10 - 62;
        $$0.drawString(this.font, (String)$$13, $$4, $$15, -1);
        $$13 = $$11.format($$2.globalPercentage) + "%";
        $$0.drawString(this.font, (String)$$13, $$5 - this.font.width((String)$$13), $$15, -1);
        for (int $$16 = 0; $$16 < $$1.size(); ++$$16) {
            ResultField $$17 = $$1.get($$16);
            StringBuilder $$18 = new StringBuilder();
            if ("unspecified".equals($$17.name)) {
                $$18.append("[?] ");
            } else {
                $$18.append("[").append($$16 + 1).append("] ");
            }
            Object $$19 = $$18.append($$17.name).toString();
            int $$20 = $$8 + $$16 * this.font.lineHeight;
            $$0.drawString(this.font, (String)$$19, $$4, $$20, $$17.getColor());
            $$19 = $$11.format($$17.percentage) + "%";
            $$0.drawString(this.font, (String)$$19, $$5 - 50 - this.font.width((String)$$19), $$20, $$17.getColor());
            $$19 = $$11.format($$17.globalPercentage) + "%";
            $$0.drawString(this.font, (String)$$19, $$5 - this.font.width((String)$$19), $$20, $$17.getColor());
        }
    }

    public void profilerPieChartKeyPress(int $$0) {
        if (this.profilerPieChartResults == null) {
            return;
        }
        List<ResultField> $$1 = this.profilerPieChartResults.getTimes(this.profilerTreePath);
        if ($$1.isEmpty()) {
            return;
        }
        ResultField $$2 = $$1.remove(0);
        if ($$0 == 0) {
            int $$3;
            if (!$$2.name.isEmpty() && ($$3 = this.profilerTreePath.lastIndexOf(30)) >= 0) {
                this.profilerTreePath = this.profilerTreePath.substring(0, $$3);
            }
        } else if (--$$0 < $$1.size() && !"unspecified".equals($$1.get((int)$$0).name)) {
            if (!this.profilerTreePath.isEmpty()) {
                this.profilerTreePath = this.profilerTreePath + "\u001e";
            }
            this.profilerTreePath = this.profilerTreePath + $$1.get((int)$$0).name;
        }
    }
}

