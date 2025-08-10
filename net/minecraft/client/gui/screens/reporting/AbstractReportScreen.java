/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.datafixers.util.Unit
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.gui.screens.reporting;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.datafixers.util.Unit;
import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.GenericWaitingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.client.multiplayer.chat.report.Report;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ThrowingComponent;
import org.slf4j.Logger;

public abstract class AbstractReportScreen<B extends Report.Builder<?>>
extends Screen {
    private static final Component REPORT_SENT_MESSAGE = Component.translatable("gui.abuseReport.report_sent_msg");
    private static final Component REPORT_SENDING_TITLE = Component.translatable("gui.abuseReport.sending.title").withStyle(ChatFormatting.BOLD);
    private static final Component REPORT_SENT_TITLE = Component.translatable("gui.abuseReport.sent.title").withStyle(ChatFormatting.BOLD);
    private static final Component REPORT_ERROR_TITLE = Component.translatable("gui.abuseReport.error.title").withStyle(ChatFormatting.BOLD);
    private static final Component REPORT_SEND_GENERIC_ERROR = Component.translatable("gui.abuseReport.send.generic_error");
    protected static final Component SEND_REPORT = Component.translatable("gui.abuseReport.send");
    protected static final Component OBSERVED_WHAT_LABEL = Component.translatable("gui.abuseReport.observed_what");
    protected static final Component SELECT_REASON = Component.translatable("gui.abuseReport.select_reason");
    private static final Component DESCRIBE_PLACEHOLDER = Component.translatable("gui.abuseReport.describe");
    protected static final Component MORE_COMMENTS_LABEL = Component.translatable("gui.abuseReport.more_comments");
    private static final Component MORE_COMMENTS_NARRATION = Component.translatable("gui.abuseReport.comments");
    private static final Component ATTESTATION_CHECKBOX = Component.translatable("gui.abuseReport.attestation");
    protected static final int BUTTON_WIDTH = 120;
    protected static final int MARGIN = 20;
    protected static final int SCREEN_WIDTH = 280;
    protected static final int SPACING = 8;
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final Screen lastScreen;
    protected final ReportingContext reportingContext;
    protected final LinearLayout layout = LinearLayout.vertical().spacing(8);
    protected B reportBuilder;
    private Checkbox attestation;
    protected Button sendButton;

    protected AbstractReportScreen(Component $$0, Screen $$1, ReportingContext $$2, B $$3) {
        super($$0);
        this.lastScreen = $$1;
        this.reportingContext = $$2;
        this.reportBuilder = $$3;
    }

    protected MultiLineEditBox createCommentBox(int $$0, int $$1, Consumer<String> $$2) {
        AbuseReportLimits $$3 = this.reportingContext.sender().reportLimits();
        MultiLineEditBox $$4 = MultiLineEditBox.builder().setPlaceholder(DESCRIBE_PLACEHOLDER).build(this.font, $$0, $$1, MORE_COMMENTS_NARRATION);
        $$4.setValue(((Report.Builder)this.reportBuilder).comments());
        $$4.setCharacterLimit($$3.maxOpinionCommentsLength());
        $$4.setValueListener($$2);
        return $$4;
    }

    @Override
    protected void init() {
        this.layout.defaultCellSetting().alignHorizontallyCenter();
        this.createHeader();
        this.addContent();
        this.createFooter();
        this.onReportChanged();
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    protected void createHeader() {
        this.layout.addChild(new StringWidget(this.title, this.font));
    }

    protected abstract void addContent();

    protected void createFooter() {
        this.attestation = this.layout.addChild(Checkbox.builder(ATTESTATION_CHECKBOX, this.font).selected(((Report.Builder)this.reportBuilder).attested()).maxWidth(280).onValueChange(($$0, $$1) -> {
            ((Report.Builder)this.reportBuilder).setAttested($$1);
            this.onReportChanged();
        }).build());
        LinearLayout $$02 = this.layout.addChild(LinearLayout.horizontal().spacing(8));
        $$02.addChild(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).width(120).build());
        this.sendButton = $$02.addChild(Button.builder(SEND_REPORT, $$0 -> this.sendReport()).width(120).build());
    }

    protected void onReportChanged() {
        Report.CannotBuildReason $$0 = ((Report.Builder)this.reportBuilder).checkBuildable();
        this.sendButton.active = $$0 == null && this.attestation.selected();
        this.sendButton.setTooltip(Optionull.map($$0, Report.CannotBuildReason::tooltip));
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        FrameLayout.centerInRectangle(this.layout, this.getRectangle());
    }

    protected void sendReport() {
        ((Report.Builder)this.reportBuilder).build(this.reportingContext).ifLeft($$02 -> {
            CompletableFuture<Unit> $$12 = this.reportingContext.sender().send($$02.id(), $$02.reportType(), $$02.report());
            this.minecraft.setScreen(GenericWaitingScreen.createWaiting(REPORT_SENDING_TITLE, CommonComponents.GUI_CANCEL, () -> {
                this.minecraft.setScreen(this);
                $$12.cancel(true);
            }));
            $$12.handleAsync(($$0, $$1) -> {
                if ($$1 == null) {
                    this.onReportSendSuccess();
                } else {
                    if ($$1 instanceof CancellationException) {
                        return null;
                    }
                    this.onReportSendError((Throwable)$$1);
                }
                return null;
            }, (Executor)this.minecraft);
        }).ifRight($$0 -> this.displayReportSendError($$0.message()));
    }

    private void onReportSendSuccess() {
        this.clearDraft();
        this.minecraft.setScreen(GenericWaitingScreen.createCompleted(REPORT_SENT_TITLE, REPORT_SENT_MESSAGE, CommonComponents.GUI_DONE, () -> this.minecraft.setScreen(null)));
    }

    private void onReportSendError(Throwable $$0) {
        Component $$3;
        LOGGER.error("Encountered error while sending abuse report", $$0);
        Throwable throwable = $$0.getCause();
        if (throwable instanceof ThrowingComponent) {
            ThrowingComponent $$1 = (ThrowingComponent)throwable;
            Component $$2 = $$1.getComponent();
        } else {
            $$3 = REPORT_SEND_GENERIC_ERROR;
        }
        this.displayReportSendError($$3);
    }

    private void displayReportSendError(Component $$0) {
        MutableComponent $$1 = $$0.copy().withStyle(ChatFormatting.RED);
        this.minecraft.setScreen(GenericWaitingScreen.createCompleted(REPORT_ERROR_TITLE, $$1, CommonComponents.GUI_BACK, () -> this.minecraft.setScreen(this)));
    }

    void saveDraft() {
        if (((Report.Builder)this.reportBuilder).hasContent()) {
            this.reportingContext.setReportDraft(((Report)((Report.Builder)this.reportBuilder).report()).copy());
        }
    }

    void clearDraft() {
        this.reportingContext.setReportDraft(null);
    }

    @Override
    public void onClose() {
        if (((Report.Builder)this.reportBuilder).hasContent()) {
            this.minecraft.setScreen(new DiscardReportWarningScreen());
        } else {
            this.minecraft.setScreen(this.lastScreen);
        }
    }

    @Override
    public void removed() {
        this.saveDraft();
        super.removed();
    }

    class DiscardReportWarningScreen
    extends WarningScreen {
        private static final Component TITLE = Component.translatable("gui.abuseReport.discard.title").withStyle(ChatFormatting.BOLD);
        private static final Component MESSAGE = Component.translatable("gui.abuseReport.discard.content");
        private static final Component RETURN = Component.translatable("gui.abuseReport.discard.return");
        private static final Component DRAFT = Component.translatable("gui.abuseReport.discard.draft");
        private static final Component DISCARD = Component.translatable("gui.abuseReport.discard.discard");

        protected DiscardReportWarningScreen() {
            super(TITLE, MESSAGE, MESSAGE);
        }

        @Override
        protected Layout addFooterButtons() {
            LinearLayout $$02 = LinearLayout.vertical().spacing(8);
            $$02.defaultCellSetting().alignHorizontallyCenter();
            LinearLayout $$1 = $$02.addChild(LinearLayout.horizontal().spacing(8));
            $$1.addChild(Button.builder(RETURN, $$0 -> this.onClose()).build());
            $$1.addChild(Button.builder(DRAFT, $$0 -> {
                AbstractReportScreen.this.saveDraft();
                this.minecraft.setScreen(AbstractReportScreen.this.lastScreen);
            }).build());
            $$02.addChild(Button.builder(DISCARD, $$0 -> {
                AbstractReportScreen.this.clearDraft();
                this.minecraft.setScreen(AbstractReportScreen.this.lastScreen);
            }).build());
            return $$02;
        }

        @Override
        public void onClose() {
            this.minecraft.setScreen(AbstractReportScreen.this);
        }

        @Override
        public boolean shouldCloseOnEsc() {
            return false;
        }
    }
}

