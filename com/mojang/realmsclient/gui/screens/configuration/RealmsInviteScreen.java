/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigureWorldScreen;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.StringUtil;

public class RealmsInviteScreen
extends RealmsScreen {
    private static final Component TITLE = Component.translatable("mco.configure.world.buttons.invite");
    private static final Component NAME_LABEL = Component.translatable("mco.configure.world.invite.profile.name").withColor(-6250336);
    private static final Component INVITING_PLAYER_TEXT = Component.translatable("mco.configure.world.players.inviting").withColor(-6250336);
    private static final Component NO_SUCH_PLAYER_ERROR_TEXT = Component.translatable("mco.configure.world.players.error").withColor(-65536);
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    @Nullable
    private EditBox profileName;
    @Nullable
    private Button inviteButton;
    private final RealmsServer serverData;
    private final RealmsConfigureWorldScreen configureScreen;
    @Nullable
    private Component message;

    public RealmsInviteScreen(RealmsConfigureWorldScreen $$0, RealmsServer $$1) {
        super(TITLE);
        this.configureScreen = $$0;
        this.serverData = $$1;
    }

    @Override
    public void init() {
        this.layout.addTitleHeader(TITLE, this.font);
        LinearLayout $$02 = this.layout.addToContents(LinearLayout.vertical().spacing(8));
        this.profileName = new EditBox(this.minecraft.font, 200, 20, Component.translatable("mco.configure.world.invite.profile.name"));
        $$02.addChild(CommonLayouts.labeledElement(this.font, this.profileName, NAME_LABEL));
        this.inviteButton = $$02.addChild(Button.builder(TITLE, $$0 -> this.onInvite()).width(200).build());
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).width(200).build());
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    @Override
    protected void setInitialFocus() {
        if (this.profileName != null) {
            this.setInitialFocus(this.profileName);
        }
    }

    private void onInvite() {
        if (this.inviteButton == null || this.profileName == null) {
            return;
        }
        if (StringUtil.isBlank(this.profileName.getValue())) {
            this.showMessage(NO_SUCH_PLAYER_ERROR_TEXT);
            return;
        }
        long $$02 = this.serverData.id;
        String $$1 = this.profileName.getValue().trim();
        this.inviteButton.active = false;
        this.profileName.setEditable(false);
        this.showMessage(INVITING_PLAYER_TEXT);
        CompletableFuture.supplyAsync(() -> this.configureScreen.invitePlayer($$02, $$1), Util.ioPool()).thenAcceptAsync($$0 -> {
            if ($$0.booleanValue()) {
                this.minecraft.setScreen(this.configureScreen);
            } else {
                this.showMessage(NO_SUCH_PLAYER_ERROR_TEXT);
            }
            this.profileName.setEditable(true);
            this.inviteButton.active = true;
        }, this.screenExecutor);
    }

    private void showMessage(Component $$0) {
        this.message = $$0;
        this.minecraft.getNarrator().saySystemNow($$0);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.configureScreen);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        if (this.message != null && this.inviteButton != null) {
            $$0.drawCenteredString(this.font, this.message, this.width / 2, this.inviteButton.getY() + this.inviteButton.getHeight() + 8, -1);
        }
    }
}

