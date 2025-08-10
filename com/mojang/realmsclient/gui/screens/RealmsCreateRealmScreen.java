/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.util.task.RealmCreationTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.StringUtil;

public class RealmsCreateRealmScreen
extends RealmsScreen {
    private static final Component CREATE_REALM_TEXT = Component.translatable("mco.selectServer.create");
    private static final Component NAME_LABEL = Component.translatable("mco.configure.world.name");
    private static final Component DESCRIPTION_LABEL = Component.translatable("mco.configure.world.description");
    private static final int BUTTON_SPACING = 10;
    private static final int CONTENT_WIDTH = 210;
    private final RealmsMainScreen lastScreen;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private EditBox nameBox;
    private EditBox descriptionBox;
    private final Runnable createWorldRunnable;

    public RealmsCreateRealmScreen(RealmsMainScreen $$0, RealmsServer $$1, boolean $$2) {
        super(CREATE_REALM_TEXT);
        this.lastScreen = $$0;
        this.createWorldRunnable = () -> this.createWorld($$1, $$2);
    }

    @Override
    public void init() {
        this.layout.addTitleHeader(this.title, this.font);
        LinearLayout $$02 = this.layout.addToContents(LinearLayout.vertical()).spacing(10);
        Button $$12 = Button.builder(CommonComponents.GUI_CONTINUE, $$0 -> this.createWorldRunnable.run()).build();
        $$12.active = false;
        this.nameBox = new EditBox(this.font, 210, 20, NAME_LABEL);
        this.nameBox.setResponder($$1 -> {
            $$0.active = !StringUtil.isBlank($$1);
        });
        this.descriptionBox = new EditBox(this.font, 210, 20, DESCRIPTION_LABEL);
        $$02.addChild(CommonLayouts.labeledElement(this.font, this.nameBox, NAME_LABEL));
        $$02.addChild(CommonLayouts.labeledElement(this.font, this.descriptionBox, DESCRIPTION_LABEL));
        LinearLayout $$2 = this.layout.addToFooter(LinearLayout.horizontal().spacing(10));
        $$2.addChild($$12);
        $$2.addChild(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).build());
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.nameBox);
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    private void createWorld(RealmsServer $$02, boolean $$12) {
        if (!$$02.isSnapshotRealm() && $$12) {
            AtomicBoolean $$2 = new AtomicBoolean();
            this.minecraft.setScreen(new AlertScreen(() -> {
                $$2.set(true);
                this.lastScreen.resetScreen();
                this.minecraft.setScreen(this.lastScreen);
            }, Component.translatable("mco.upload.preparing"), Component.empty()));
            CompletableFuture.supplyAsync(() -> RealmsCreateRealmScreen.createSnapshotRealm($$02), Util.backgroundExecutor()).thenAcceptAsync($$1 -> {
                if (!$$2.get()) {
                    this.showResetWorldScreen((RealmsServer)$$1);
                }
            }, (Executor)this.minecraft).exceptionallyAsync($$0 -> {
                MutableComponent $$4;
                this.lastScreen.resetScreen();
                Throwable $$1 = $$0.getCause();
                if ($$1 instanceof RealmsServiceException) {
                    RealmsServiceException $$2 = (RealmsServiceException)$$1;
                    Component $$3 = $$2.realmsError.errorMessage();
                } else {
                    $$4 = Component.translatable("mco.errorMessage.initialize.failed");
                }
                this.minecraft.setScreen(new RealmsGenericErrorScreen($$4, (Screen)this.lastScreen));
                return null;
            }, this.minecraft);
        } else {
            this.showResetWorldScreen($$02);
        }
    }

    private static RealmsServer createSnapshotRealm(RealmsServer $$0) {
        RealmsClient $$1 = RealmsClient.getOrCreate();
        try {
            return $$1.createSnapshotRealm($$0.id);
        } catch (RealmsServiceException $$2) {
            throw new RuntimeException($$2);
        }
    }

    private void showResetWorldScreen(RealmsServer $$0) {
        RealmCreationTask $$1 = new RealmCreationTask($$0.id, this.nameBox.getValue(), this.descriptionBox.getValue());
        RealmsResetWorldScreen $$2 = RealmsResetWorldScreen.forNewRealm(this, $$0, $$1, () -> this.minecraft.execute(() -> {
            RealmsMainScreen.refreshServerList();
            this.minecraft.setScreen(this.lastScreen);
        }));
        this.minecraft.setScreen($$2);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }
}

