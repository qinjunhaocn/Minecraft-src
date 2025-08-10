/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigurationTab;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;
import org.slf4j.Logger;

class RealmsSubscriptionTab
extends GridLayoutTab
implements RealmsConfigurationTab {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int DEFAULT_COMPONENT_WIDTH = 200;
    private static final int EXTRA_SPACING = 2;
    private static final int DEFAULT_SPACING = 6;
    static final Component TITLE = Component.translatable("mco.configure.world.subscription.tab");
    private static final Component SUBSCRIPTION_START_LABEL = Component.translatable("mco.configure.world.subscription.start");
    private static final Component TIME_LEFT_LABEL = Component.translatable("mco.configure.world.subscription.timeleft");
    private static final Component DAYS_LEFT_LABEL = Component.translatable("mco.configure.world.subscription.recurring.daysleft");
    private static final Component SUBSCRIPTION_EXPIRED_TEXT = Component.translatable("mco.configure.world.subscription.expired").withStyle(ChatFormatting.GRAY);
    private static final Component SUBSCRIPTION_LESS_THAN_A_DAY_TEXT = Component.translatable("mco.configure.world.subscription.less_than_a_day").withStyle(ChatFormatting.GRAY);
    private static final Component UNKNOWN = Component.translatable("mco.configure.world.subscription.unknown");
    private static final Component RECURRING_INFO = Component.translatable("mco.configure.world.subscription.recurring.info");
    private final RealmsConfigureWorldScreen configurationScreen;
    private final Minecraft minecraft;
    private final Button deleteButton;
    private final FocusableTextWidget subscriptionInfo;
    private final StringWidget startDateWidget;
    private final StringWidget daysLeftLabelWidget;
    private final StringWidget daysLeftWidget;
    private RealmsServer serverData;
    private Component daysLeft = UNKNOWN;
    private Component startDate = UNKNOWN;
    @Nullable
    private Subscription.SubscriptionType type;

    RealmsSubscriptionTab(RealmsConfigureWorldScreen $$0, Minecraft $$1, RealmsServer $$22) {
        super(TITLE);
        this.configurationScreen = $$0;
        this.minecraft = $$1;
        this.serverData = $$22;
        GridLayout.RowHelper $$32 = this.layout.rowSpacing(6).createRowHelper(1);
        Font $$4 = $$0.getFont();
        $$32.addChild(new StringWidget(200, $$4.lineHeight, SUBSCRIPTION_START_LABEL, $$4).alignLeft());
        this.startDateWidget = $$32.addChild(new StringWidget(200, $$4.lineHeight, this.startDate, $$4).alignLeft());
        $$32.addChild(SpacerElement.height(2));
        this.daysLeftLabelWidget = $$32.addChild(new StringWidget(200, $$4.lineHeight, TIME_LEFT_LABEL, $$4).alignLeft());
        this.daysLeftWidget = $$32.addChild(new StringWidget(200, $$4.lineHeight, this.daysLeft, $$4).alignLeft());
        $$32.addChild(SpacerElement.height(2));
        $$32.addChild(Button.builder(Component.translatable("mco.configure.world.subscription.extend"), $$3 -> ConfirmLinkScreen.confirmLinkNow((Screen)$$0, CommonLinks.extendRealms($$1.remoteSubscriptionId, $$1.getUser().getProfileId()))).bounds(0, 0, 200, 20).build());
        $$32.addChild(SpacerElement.height(2));
        this.deleteButton = $$32.addChild(Button.builder(Component.translatable("mco.configure.world.delete.button"), $$2 -> $$1.setScreen(RealmsPopups.warningPopupScreen($$0, Component.translatable("mco.configure.world.delete.question.line1"), $$0 -> this.deleteRealm()))).bounds(0, 0, 200, 20).build());
        $$32.addChild(SpacerElement.height(2));
        this.subscriptionInfo = $$32.addChild(new FocusableTextWidget(200, Component.empty(), $$4, true, true, 4), LayoutSettings.defaults().alignHorizontallyCenter());
        this.subscriptionInfo.setMaxWidth(200);
        this.subscriptionInfo.setCentered(false);
        this.updateData($$22);
    }

    private void deleteRealm() {
        RealmsUtil.runAsync($$0 -> $$0.deleteRealm(this.serverData.id), RealmsUtil.openScreenAndLogOnFailure(this.configurationScreen::createErrorScreen, "Couldn't delete world")).thenRunAsync(() -> this.minecraft.setScreen(this.configurationScreen.getLastScreen()), this.minecraft);
        this.minecraft.setScreen(this.configurationScreen);
    }

    private void getSubscription(long $$0) {
        RealmsClient $$1 = RealmsClient.getOrCreate();
        try {
            Subscription $$2 = $$1.subscriptionFor($$0);
            this.daysLeft = this.daysLeftPresentation($$2.daysLeft);
            this.startDate = RealmsSubscriptionTab.localPresentation($$2.startDate);
            this.type = $$2.type;
        } catch (RealmsServiceException $$3) {
            LOGGER.error("Couldn't get subscription", $$3);
            this.minecraft.setScreen(this.configurationScreen.createErrorScreen($$3));
        }
    }

    private static Component localPresentation(long $$0) {
        GregorianCalendar $$1 = new GregorianCalendar(TimeZone.getDefault());
        $$1.setTimeInMillis($$0);
        return Component.literal(DateFormat.getDateTimeInstance().format($$1.getTime())).withStyle(ChatFormatting.GRAY);
    }

    private Component daysLeftPresentation(int $$0) {
        boolean $$4;
        if ($$0 < 0 && this.serverData.expired) {
            return SUBSCRIPTION_EXPIRED_TEXT;
        }
        if ($$0 <= 1) {
            return SUBSCRIPTION_LESS_THAN_A_DAY_TEXT;
        }
        int $$1 = $$0 / 30;
        int $$2 = $$0 % 30;
        boolean $$3 = $$1 > 0;
        boolean bl = $$4 = $$2 > 0;
        if ($$3 && $$4) {
            return Component.a("mco.configure.world.subscription.remaining.months.days", $$1, $$2).withStyle(ChatFormatting.GRAY);
        }
        if ($$3) {
            return Component.a("mco.configure.world.subscription.remaining.months", $$1).withStyle(ChatFormatting.GRAY);
        }
        if ($$4) {
            return Component.a("mco.configure.world.subscription.remaining.days", $$2).withStyle(ChatFormatting.GRAY);
        }
        return Component.empty();
    }

    @Override
    public void updateData(RealmsServer $$0) {
        this.serverData = $$0;
        this.getSubscription($$0.id);
        this.startDateWidget.setMessage(this.startDate);
        if (this.type == Subscription.SubscriptionType.NORMAL) {
            this.daysLeftLabelWidget.setMessage(TIME_LEFT_LABEL);
        } else if (this.type == Subscription.SubscriptionType.RECURRING) {
            this.daysLeftLabelWidget.setMessage(DAYS_LEFT_LABEL);
        }
        this.daysLeftWidget.setMessage(this.daysLeft);
        boolean $$1 = RealmsMainScreen.isSnapshot() && $$0.parentWorldName != null;
        this.deleteButton.active = $$0.expired;
        if ($$1) {
            this.subscriptionInfo.setMessage(Component.a("mco.snapshot.subscription.info", $$0.parentWorldName));
        } else {
            this.subscriptionInfo.setMessage(RECURRING_INFO);
        }
        this.layout.arrangeElements();
    }

    @Override
    public Component getTabExtraNarration() {
        return CommonComponents.b(TITLE, SUBSCRIPTION_START_LABEL, this.startDate, TIME_LEFT_LABEL, this.daysLeft);
    }
}

