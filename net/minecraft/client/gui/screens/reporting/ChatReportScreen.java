/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntSet
 */
package net.minecraft.client.gui.screens.reporting;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.UUID;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.AbstractReportScreen;
import net.minecraft.client.gui.screens.reporting.ChatSelectionScreen;
import net.minecraft.client.gui.screens.reporting.ReportReasonSelectionScreen;
import net.minecraft.client.multiplayer.chat.report.ChatReport;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.client.multiplayer.chat.report.ReportType;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.Component;

public class ChatReportScreen
extends AbstractReportScreen<ChatReport.Builder> {
    private static final Component TITLE = Component.translatable("gui.chatReport.title");
    private static final Component SELECT_CHAT_MESSAGE = Component.translatable("gui.chatReport.select_chat");
    private MultiLineEditBox commentBox;
    private Button selectMessagesButton;
    private Button selectReasonButton;

    private ChatReportScreen(Screen $$0, ReportingContext $$1, ChatReport.Builder $$2) {
        super(TITLE, $$0, $$1, $$2);
    }

    public ChatReportScreen(Screen $$0, ReportingContext $$1, UUID $$2) {
        this($$0, $$1, new ChatReport.Builder($$2, $$1.sender().reportLimits()));
    }

    public ChatReportScreen(Screen $$0, ReportingContext $$1, ChatReport $$2) {
        this($$0, $$1, new ChatReport.Builder($$2, $$1.sender().reportLimits()));
    }

    @Override
    protected void addContent() {
        this.selectMessagesButton = this.layout.addChild(Button.builder(SELECT_CHAT_MESSAGE, $$02 -> this.minecraft.setScreen(new ChatSelectionScreen(this, this.reportingContext, (ChatReport.Builder)this.reportBuilder, $$0 -> {
            this.reportBuilder = $$0;
            this.onReportChanged();
        }))).width(280).build());
        this.selectReasonButton = Button.builder(SELECT_REASON, $$02 -> this.minecraft.setScreen(new ReportReasonSelectionScreen(this, ((ChatReport.Builder)this.reportBuilder).reason(), ReportType.CHAT, $$0 -> {
            ((ChatReport.Builder)this.reportBuilder).setReason((ReportReason)((Object)((Object)$$0)));
            this.onReportChanged();
        }))).width(280).build();
        this.layout.addChild(CommonLayouts.labeledElement(this.font, this.selectReasonButton, OBSERVED_WHAT_LABEL));
        this.commentBox = this.createCommentBox(280, this.font.lineHeight * 8, $$0 -> {
            ((ChatReport.Builder)this.reportBuilder).setComments((String)$$0);
            this.onReportChanged();
        });
        this.layout.addChild(CommonLayouts.labeledElement(this.font, this.commentBox, MORE_COMMENTS_LABEL, $$0 -> $$0.paddingBottom(12)));
    }

    @Override
    protected void onReportChanged() {
        IntSet $$0 = ((ChatReport.Builder)this.reportBuilder).reportedMessages();
        if ($$0.isEmpty()) {
            this.selectMessagesButton.setMessage(SELECT_CHAT_MESSAGE);
        } else {
            this.selectMessagesButton.setMessage(Component.a("gui.chatReport.selected_chat", $$0.size()));
        }
        ReportReason $$1 = ((ChatReport.Builder)this.reportBuilder).reason();
        if ($$1 != null) {
            this.selectReasonButton.setMessage($$1.title());
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

