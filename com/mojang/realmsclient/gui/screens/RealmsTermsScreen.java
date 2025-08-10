/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.CommonLinks;
import org.slf4j.Logger;

public class RealmsTermsScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component TITLE = Component.translatable("mco.terms.title");
    private static final Component TERMS_STATIC_TEXT = Component.translatable("mco.terms.sentence.1");
    private static final Component TERMS_LINK_TEXT = CommonComponents.space().append(Component.translatable("mco.terms.sentence.2").withStyle(Style.EMPTY.withUnderlined(true)));
    private final Screen lastScreen;
    private final RealmsServer realmsServer;
    private boolean onLink;

    public RealmsTermsScreen(Screen $$0, RealmsServer $$1) {
        super(TITLE);
        this.lastScreen = $$0;
        this.realmsServer = $$1;
    }

    @Override
    public void init() {
        int $$02 = this.width / 4 - 2;
        this.addRenderableWidget(Button.builder(Component.translatable("mco.terms.buttons.agree"), $$0 -> this.agreedToTos()).bounds(this.width / 4, RealmsTermsScreen.row(12), $$02, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("mco.terms.buttons.disagree"), $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 + 4, RealmsTermsScreen.row(12), $$02, 20).build());
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    private void agreedToTos() {
        RealmsClient $$0 = RealmsClient.getOrCreate();
        try {
            $$0.agreeToTos();
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new GetServerDetailsTask(this.lastScreen, this.realmsServer)));
        } catch (RealmsServiceException $$1) {
            LOGGER.error("Couldn't agree to TOS", $$1);
        }
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (this.onLink) {
            this.minecraft.keyboardHandler.setClipboard(CommonLinks.REALMS_TERMS.toString());
            Util.getPlatform().openUri(CommonLinks.REALMS_TERMS);
            return true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.a(super.getNarrationMessage(), TERMS_STATIC_TEXT).append(CommonComponents.SPACE).append(TERMS_LINK_TEXT);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 17, -1);
        $$0.drawString(this.font, TERMS_STATIC_TEXT, this.width / 2 - 120, RealmsTermsScreen.row(5), -1);
        int $$4 = this.font.width(TERMS_STATIC_TEXT);
        int $$5 = this.width / 2 - 121 + $$4;
        int $$6 = RealmsTermsScreen.row(5);
        int $$7 = $$5 + this.font.width(TERMS_LINK_TEXT) + 1;
        int $$8 = $$6 + 1 + this.font.lineHeight;
        this.onLink = $$5 <= $$1 && $$1 <= $$7 && $$6 <= $$2 && $$2 <= $$8;
        $$0.drawString(this.font, TERMS_LINK_TEXT, this.width / 2 - 120 + $$4, RealmsTermsScreen.row(5), this.onLink ? -9670204 : -13408581);
    }
}

