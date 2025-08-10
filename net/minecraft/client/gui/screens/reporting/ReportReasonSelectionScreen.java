/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.reporting;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.client.multiplayer.chat.report.ReportType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;

public class ReportReasonSelectionScreen
extends Screen {
    private static final Component REASON_TITLE = Component.translatable("gui.abuseReport.reason.title");
    private static final Component REASON_DESCRIPTION = Component.translatable("gui.abuseReport.reason.description");
    private static final Component READ_INFO_LABEL = Component.translatable("gui.abuseReport.read_info");
    private static final int DESCRIPTION_BOX_WIDTH = 320;
    private static final int DESCRIPTION_BOX_HEIGHT = 62;
    private static final int PADDING = 4;
    @Nullable
    private final Screen lastScreen;
    @Nullable
    private ReasonSelectionList reasonSelectionList;
    @Nullable
    ReportReason currentlySelectedReason;
    private final Consumer<ReportReason> onSelectedReason;
    final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    final ReportType reportType;

    public ReportReasonSelectionScreen(@Nullable Screen $$0, @Nullable ReportReason $$1, ReportType $$2, Consumer<ReportReason> $$3) {
        super(REASON_TITLE);
        this.lastScreen = $$0;
        this.currentlySelectedReason = $$1;
        this.onSelectedReason = $$3;
        this.reportType = $$2;
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(REASON_TITLE, this.font);
        LinearLayout $$02 = this.layout.addToContents(LinearLayout.vertical().spacing(4));
        this.reasonSelectionList = $$02.addChild(new ReasonSelectionList(this.minecraft));
        ReasonSelectionList.Entry $$12 = Optionull.map(this.currentlySelectedReason, this.reasonSelectionList::findEntry);
        this.reasonSelectionList.setSelected($$12);
        $$02.addChild(SpacerElement.height(this.descriptionHeight()));
        LinearLayout $$2 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        $$2.addChild(Button.builder(READ_INFO_LABEL, ConfirmLinkScreen.confirmLink((Screen)this, CommonLinks.REPORTING_HELP)).build());
        $$2.addChild(Button.builder(CommonComponents.GUI_DONE, $$0 -> {
            ReasonSelectionList.Entry $$1 = (ReasonSelectionList.Entry)this.reasonSelectionList.getSelected();
            if ($$1 != null) {
                this.onSelectedReason.accept($$1.getReason());
            }
            this.minecraft.setScreen(this.lastScreen);
        }).build());
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.reasonSelectionList != null) {
            this.reasonSelectionList.updateSizeAndPosition(this.width, this.listHeight(), this.layout.getHeaderHeight());
        }
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.fill(this.descriptionLeft(), this.descriptionTop(), this.descriptionRight(), this.descriptionBottom(), -16777216);
        $$0.renderOutline(this.descriptionLeft(), this.descriptionTop(), this.descriptionWidth(), this.descriptionHeight(), -1);
        $$0.drawString(this.font, REASON_DESCRIPTION, this.descriptionLeft() + 4, this.descriptionTop() + 4, -1);
        ReasonSelectionList.Entry $$4 = (ReasonSelectionList.Entry)this.reasonSelectionList.getSelected();
        if ($$4 != null) {
            int $$5 = this.descriptionLeft() + 4 + 16;
            int $$6 = this.descriptionRight() - 4;
            int $$7 = this.descriptionTop() + 4 + this.font.lineHeight + 2;
            int $$8 = this.descriptionBottom() - 4;
            int $$9 = $$6 - $$5;
            int $$10 = $$8 - $$7;
            int $$11 = this.font.wordWrapHeight($$4.reason.description(), $$9);
            $$0.drawWordWrap(this.font, $$4.reason.description(), $$5, $$7 + ($$10 - $$11) / 2, $$9, -1);
        }
    }

    private int descriptionLeft() {
        return (this.width - 320) / 2;
    }

    private int descriptionRight() {
        return (this.width + 320) / 2;
    }

    private int descriptionTop() {
        return this.descriptionBottom() - this.descriptionHeight();
    }

    private int descriptionBottom() {
        return this.height - this.layout.getFooterHeight() - 4;
    }

    private int descriptionWidth() {
        return 320;
    }

    private int descriptionHeight() {
        return 62;
    }

    int listHeight() {
        return this.layout.getContentHeight() - this.descriptionHeight() - 8;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    public class ReasonSelectionList
    extends ObjectSelectionList<Entry> {
        public ReasonSelectionList(Minecraft $$1) {
            super($$1, ReportReasonSelectionScreen.this.width, ReportReasonSelectionScreen.this.listHeight(), ReportReasonSelectionScreen.this.layout.getHeaderHeight(), 18);
            for (ReportReason $$2 : ReportReason.values()) {
                if (ReportReason.getIncompatibleCategories(ReportReasonSelectionScreen.this.reportType).contains((Object)$$2)) continue;
                this.addEntry(new Entry($$2));
            }
        }

        @Nullable
        public Entry findEntry(ReportReason $$0) {
            return this.children().stream().filter($$1 -> $$1.reason == $$0).findFirst().orElse(null);
        }

        @Override
        public int getRowWidth() {
            return 320;
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            ReportReasonSelectionScreen.this.currentlySelectedReason = $$0 != null ? $$0.getReason() : null;
        }

        public class Entry
        extends ObjectSelectionList.Entry<Entry> {
            final ReportReason reason;

            public Entry(ReportReason $$1) {
                this.reason = $$1;
            }

            @Override
            public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                int $$10 = $$3 + 1;
                int $$11 = $$2 + ($$5 - ((ReportReasonSelectionScreen)ReportReasonSelectionScreen.this).font.lineHeight) / 2 + 1;
                $$0.drawString(ReportReasonSelectionScreen.this.font, this.reason.title(), $$10, $$11, -1);
            }

            @Override
            public Component getNarration() {
                return Component.a("gui.abuseReport.reason.narration", this.reason.title(), this.reason.description());
            }

            @Override
            public boolean mouseClicked(double $$0, double $$1, int $$2) {
                ReasonSelectionList.this.setSelected(this);
                return super.mouseClicked($$0, $$1, $$2);
            }

            public ReportReason getReason() {
                return this.reason;
            }
        }
    }
}

