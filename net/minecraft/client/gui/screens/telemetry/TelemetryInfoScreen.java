/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.telemetry;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.telemetry.TelemetryEventWidget;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;

public class TelemetryInfoScreen
extends Screen {
    private static final Component TITLE = Component.translatable("telemetry_info.screen.title");
    private static final Component DESCRIPTION = Component.translatable("telemetry_info.screen.description").withColor(-4539718);
    private static final Component BUTTON_PRIVACY_STATEMENT = Component.translatable("telemetry_info.button.privacy_statement");
    private static final Component BUTTON_GIVE_FEEDBACK = Component.translatable("telemetry_info.button.give_feedback");
    private static final Component BUTTON_VIEW_DATA = Component.translatable("telemetry_info.button.show_data");
    private static final Component CHECKBOX_OPT_IN = Component.translatable("telemetry_info.opt_in.description");
    private static final int SPACING = 8;
    private static final boolean EXTRA_TELEMETRY_AVAILABLE = Minecraft.getInstance().extraTelemetryAvailable();
    private final Screen lastScreen;
    private final Options options;
    private final HeaderAndFooterLayout layout;
    @Nullable
    private TelemetryEventWidget telemetryEventWidget;
    @Nullable
    private MultiLineTextWidget description;
    private double savedScroll;

    public TelemetryInfoScreen(Screen $$0, Options $$1) {
        super(TITLE);
        this.layout = new HeaderAndFooterLayout(this, 16 + Minecraft.getInstance().font.lineHeight * 5 + 20, EXTRA_TELEMETRY_AVAILABLE ? 33 + Checkbox.getBoxSize(Minecraft.getInstance().font) : 33);
        this.lastScreen = $$0;
        this.options = $$1;
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.a(super.getNarrationMessage(), DESCRIPTION);
    }

    @Override
    protected void init() {
        LinearLayout $$02 = this.layout.addToHeader(LinearLayout.vertical().spacing(4));
        $$02.defaultCellSetting().alignHorizontallyCenter();
        $$02.addChild(new StringWidget(TITLE, this.font));
        this.description = $$02.addChild(new MultiLineTextWidget(DESCRIPTION, this.font).setCentered(true));
        LinearLayout $$12 = $$02.addChild(LinearLayout.horizontal().spacing(8));
        $$12.addChild(Button.builder(BUTTON_PRIVACY_STATEMENT, this::openPrivacyStatementLink).build());
        $$12.addChild(Button.builder(BUTTON_GIVE_FEEDBACK, this::openFeedbackLink).build());
        LinearLayout $$2 = this.layout.addToFooter(LinearLayout.vertical().spacing(4));
        if (EXTRA_TELEMETRY_AVAILABLE) {
            $$2.addChild(this.createTelemetryCheckbox());
        }
        LinearLayout $$3 = $$2.addChild(LinearLayout.horizontal().spacing(8));
        $$3.addChild(Button.builder(BUTTON_VIEW_DATA, this::openDataFolder).build());
        $$3.addChild(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onClose()).build());
        LinearLayout $$4 = this.layout.addToContents(LinearLayout.vertical().spacing(8));
        this.telemetryEventWidget = $$4.addChild(new TelemetryEventWidget(0, 0, this.width - 40, this.layout.getContentHeight(), this.font));
        this.telemetryEventWidget.setOnScrolledListener($$0 -> {
            this.savedScroll = $$0;
        });
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (this.telemetryEventWidget != null) {
            this.telemetryEventWidget.setScrollAmount(this.savedScroll);
            this.telemetryEventWidget.setWidth(this.width - 40);
            this.telemetryEventWidget.setHeight(this.layout.getContentHeight());
            this.telemetryEventWidget.updateLayout();
        }
        if (this.description != null) {
            this.description.setMaxWidth(this.width - 16);
        }
        this.layout.arrangeElements();
    }

    @Override
    protected void setInitialFocus() {
        if (this.telemetryEventWidget != null) {
            this.setInitialFocus(this.telemetryEventWidget);
        }
    }

    private AbstractWidget createTelemetryCheckbox() {
        OptionInstance<Boolean> $$0 = this.options.telemetryOptInExtra();
        return Checkbox.builder(CHECKBOX_OPT_IN, this.font).selected($$0).onValueChange(this::onOptInChanged).build();
    }

    private void onOptInChanged(AbstractWidget $$0, boolean $$1) {
        if (this.telemetryEventWidget != null) {
            this.telemetryEventWidget.onOptInChanged($$1);
        }
    }

    private void openPrivacyStatementLink(Button $$0) {
        ConfirmLinkScreen.confirmLinkNow((Screen)this, CommonLinks.PRIVACY_STATEMENT);
    }

    private void openFeedbackLink(Button $$0) {
        ConfirmLinkScreen.confirmLinkNow((Screen)this, CommonLinks.RELEASE_FEEDBACK);
    }

    private void openDataFolder(Button $$0) {
        Util.getPlatform().openPath(this.minecraft.getTelemetryManager().getLogDirectory());
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }
}

