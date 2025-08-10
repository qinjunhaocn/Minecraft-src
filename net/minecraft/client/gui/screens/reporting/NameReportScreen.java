/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.reporting;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.AbstractReportScreen;
import net.minecraft.client.multiplayer.chat.report.NameReport;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class NameReportScreen
extends AbstractReportScreen<NameReport.Builder> {
    private static final Component TITLE = Component.translatable("gui.abuseReport.name.title");
    private static final Component COMMENT_BOX_LABEL = Component.translatable("gui.abuseReport.name.comment_box_label");
    @Nullable
    private MultiLineEditBox commentBox;

    private NameReportScreen(Screen $$0, ReportingContext $$1, NameReport.Builder $$2) {
        super(TITLE, $$0, $$1, $$2);
    }

    public NameReportScreen(Screen $$0, ReportingContext $$1, UUID $$2, String $$3) {
        this($$0, $$1, new NameReport.Builder($$2, $$3, $$1.sender().reportLimits()));
    }

    public NameReportScreen(Screen $$0, ReportingContext $$1, NameReport $$2) {
        this($$0, $$1, new NameReport.Builder($$2, $$1.sender().reportLimits()));
    }

    @Override
    protected void addContent() {
        MutableComponent $$02 = Component.literal(((NameReport)((NameReport.Builder)this.reportBuilder).report()).getReportedName()).withStyle(ChatFormatting.YELLOW);
        this.layout.addChild(new StringWidget(Component.a("gui.abuseReport.name.reporting", $$02), this.font), $$0 -> $$0.alignHorizontallyCenter().padding(0, 8));
        this.commentBox = this.createCommentBox(280, this.font.lineHeight * 8, $$0 -> {
            ((NameReport.Builder)this.reportBuilder).setComments((String)$$0);
            this.onReportChanged();
        });
        this.layout.addChild(CommonLayouts.labeledElement(this.font, this.commentBox, COMMENT_BOX_LABEL, $$0 -> $$0.paddingBottom(12)));
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        if (super.mouseReleased($$0, $$1, $$2)) {
            return true;
        }
        if (this.commentBox != null) {
            return this.commentBox.mouseReleased($$0, $$1, $$2);
        }
        return false;
    }
}

