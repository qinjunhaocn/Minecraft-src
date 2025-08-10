/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.reporting;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.AbstractReportScreen;
import net.minecraft.client.gui.screens.reporting.ReportReasonSelectionScreen;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.client.multiplayer.chat.report.ReportType;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.multiplayer.chat.report.SkinReport;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;

public class SkinReportScreen
extends AbstractReportScreen<SkinReport.Builder> {
    private static final int SKIN_WIDTH = 85;
    private static final int FORM_WIDTH = 178;
    private static final Component TITLE = Component.translatable("gui.abuseReport.skin.title");
    private MultiLineEditBox commentBox;
    private Button selectReasonButton;

    private SkinReportScreen(Screen $$0, ReportingContext $$1, SkinReport.Builder $$2) {
        super(TITLE, $$0, $$1, $$2);
    }

    public SkinReportScreen(Screen $$0, ReportingContext $$1, UUID $$2, Supplier<PlayerSkin> $$3) {
        this($$0, $$1, new SkinReport.Builder($$2, $$3, $$1.sender().reportLimits()));
    }

    public SkinReportScreen(Screen $$0, ReportingContext $$1, SkinReport $$2) {
        this($$0, $$1, new SkinReport.Builder($$2, $$1.sender().reportLimits()));
    }

    @Override
    protected void addContent() {
        LinearLayout $$03 = this.layout.addChild(LinearLayout.horizontal().spacing(8));
        $$03.defaultCellSetting().alignVerticallyMiddle();
        $$03.addChild(new PlayerSkinWidget(85, 120, this.minecraft.getEntityModels(), ((SkinReport)((SkinReport.Builder)this.reportBuilder).report()).getSkinGetter()));
        LinearLayout $$1 = $$03.addChild(LinearLayout.vertical().spacing(8));
        this.selectReasonButton = Button.builder(SELECT_REASON, $$02 -> this.minecraft.setScreen(new ReportReasonSelectionScreen(this, ((SkinReport.Builder)this.reportBuilder).reason(), ReportType.SKIN, $$0 -> {
            ((SkinReport.Builder)this.reportBuilder).setReason((ReportReason)((Object)((Object)$$0)));
            this.onReportChanged();
        }))).width(178).build();
        $$1.addChild(CommonLayouts.labeledElement(this.font, this.selectReasonButton, OBSERVED_WHAT_LABEL));
        this.commentBox = this.createCommentBox(178, this.font.lineHeight * 8, $$0 -> {
            ((SkinReport.Builder)this.reportBuilder).setComments((String)$$0);
            this.onReportChanged();
        });
        $$1.addChild(CommonLayouts.labeledElement(this.font, this.commentBox, MORE_COMMENTS_LABEL, $$0 -> $$0.paddingBottom(12)));
    }

    @Override
    protected void onReportChanged() {
        ReportReason $$0 = ((SkinReport.Builder)this.reportBuilder).reason();
        if ($$0 != null) {
            this.selectReasonButton.setMessage($$0.title());
        } else {
            this.selectReasonButton.setMessage(SELECT_REASON);
        }
        super.onReportChanged();
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        if (super.mouseReleased($$0, $$1, $$2)) {
            return true;
        }
        return this.commentBox.mouseReleased($$0, $$1, $$2);
    }
}

