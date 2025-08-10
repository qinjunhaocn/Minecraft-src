/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.RowButton;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsPendingInvitesScreen
extends RealmsScreen {
    static final ResourceLocation ACCEPT_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("pending_invite/accept_highlighted");
    static final ResourceLocation ACCEPT_SPRITE = ResourceLocation.withDefaultNamespace("pending_invite/accept");
    static final ResourceLocation REJECT_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("pending_invite/reject_highlighted");
    static final ResourceLocation REJECT_SPRITE = ResourceLocation.withDefaultNamespace("pending_invite/reject");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component NO_PENDING_INVITES_TEXT = Component.translatable("mco.invites.nopending");
    static final Component ACCEPT_INVITE = Component.translatable("mco.invites.button.accept");
    static final Component REJECT_INVITE = Component.translatable("mco.invites.button.reject");
    private final Screen lastScreen;
    private final CompletableFuture<List<PendingInvite>> pendingInvites = CompletableFuture.supplyAsync(() -> {
        try {
            return RealmsClient.getOrCreate().pendingInvites().pendingInvites;
        } catch (RealmsServiceException $$0) {
            LOGGER.error("Couldn't list invites", $$0);
            return List.of();
        }
    }, Util.ioPool());
    @Nullable
    Component toolTip;
    PendingInvitationSelectionList pendingInvitationSelectionList;
    private Button acceptButton;
    private Button rejectButton;

    public RealmsPendingInvitesScreen(Screen $$0, Component $$1) {
        super($$1);
        this.lastScreen = $$0;
    }

    @Override
    public void init() {
        RealmsMainScreen.refreshPendingInvites();
        this.pendingInvitationSelectionList = new PendingInvitationSelectionList();
        this.pendingInvites.thenAcceptAsync($$02 -> {
            List $$1 = $$02.stream().map($$0 -> new Entry((PendingInvite)$$0)).toList();
            this.pendingInvitationSelectionList.replaceEntries($$1);
            if ($$1.isEmpty()) {
                this.minecraft.getNarrator().saySystemQueued(NO_PENDING_INVITES_TEXT);
            }
        }, this.screenExecutor);
        this.addRenderableWidget(this.pendingInvitationSelectionList);
        this.acceptButton = this.addRenderableWidget(Button.builder(ACCEPT_INVITE, $$0 -> this.handleInvitation(true)).bounds(this.width / 2 - 174, this.height - 32, 100, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onClose()).bounds(this.width / 2 - 50, this.height - 32, 100, 20).build());
        this.rejectButton = this.addRenderableWidget(Button.builder(REJECT_INVITE, $$0 -> this.handleInvitation(false)).bounds(this.width / 2 + 74, this.height - 32, 100, 20).build());
        this.updateButtonStates();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    void handleInvitation(boolean $$0) {
        Object e = this.pendingInvitationSelectionList.getSelected();
        if (e instanceof Entry) {
            Entry $$1 = (Entry)e;
            String $$22 = $$1.pendingInvite.invitationId;
            CompletableFuture.supplyAsync(() -> {
                try {
                    RealmsClient $$2 = RealmsClient.getOrCreate();
                    if ($$0) {
                        $$2.acceptInvitation($$22);
                    } else {
                        $$2.rejectInvitation($$22);
                    }
                    return true;
                } catch (RealmsServiceException $$3) {
                    LOGGER.error("Couldn't handle invite", $$3);
                    return false;
                }
            }, Util.ioPool()).thenAcceptAsync($$2 -> {
                if ($$2.booleanValue()) {
                    this.pendingInvitationSelectionList.removeInvitation($$1);
                    this.updateButtonStates();
                    RealmsDataFetcher $$3 = this.minecraft.realmsDataFetcher();
                    if ($$0) {
                        $$3.serverListUpdateTask.reset();
                    }
                    $$3.pendingInvitesTask.reset();
                }
            }, this.screenExecutor);
        }
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.toolTip = null;
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 12, -1);
        if (this.pendingInvites.isDone() && this.pendingInvitationSelectionList.hasPendingInvites()) {
            $$0.drawCenteredString(this.font, NO_PENDING_INVITES_TEXT, this.width / 2, this.height / 2 - 20, -1);
        }
        if (this.toolTip != null) {
            $$0.setTooltipForNextFrame(this.font, this.toolTip, $$1, $$2);
        }
    }

    void updateButtonStates() {
        Entry $$0 = (Entry)this.pendingInvitationSelectionList.getSelected();
        this.acceptButton.visible = $$0 != null;
        this.rejectButton.visible = $$0 != null;
    }

    class PendingInvitationSelectionList
    extends ObjectSelectionList<Entry> {
        public PendingInvitationSelectionList() {
            super(Minecraft.getInstance(), RealmsPendingInvitesScreen.this.width, RealmsPendingInvitesScreen.this.height - 72, 32, 36);
        }

        @Override
        public int getRowWidth() {
            return 260;
        }

        @Override
        public void setSelectedIndex(int $$0) {
            super.setSelectedIndex($$0);
            RealmsPendingInvitesScreen.this.updateButtonStates();
        }

        public boolean hasPendingInvites() {
            return this.getItemCount() == 0;
        }

        public void removeInvitation(Entry $$0) {
            this.removeEntry($$0);
        }
    }

    class Entry
    extends ObjectSelectionList.Entry<Entry> {
        private static final int TEXT_LEFT = 38;
        final PendingInvite pendingInvite;
        private final List<RowButton> rowButtons;

        Entry(PendingInvite $$0) {
            this.pendingInvite = $$0;
            this.rowButtons = Arrays.asList(new AcceptRowButton(), new RejectRowButton());
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.renderPendingInvitationItem($$0, this.pendingInvite, $$3, $$2, $$6, $$7);
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            RowButton.rowButtonMouseClicked(RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, this, this.rowButtons, $$2, $$0, $$1);
            return super.mouseClicked($$0, $$1, $$2);
        }

        private void renderPendingInvitationItem(GuiGraphics $$0, PendingInvite $$1, int $$2, int $$3, int $$4, int $$5) {
            $$0.drawString(RealmsPendingInvitesScreen.this.font, $$1.realmName, $$2 + 38, $$3 + 1, -1);
            $$0.drawString(RealmsPendingInvitesScreen.this.font, $$1.realmOwnerName, $$2 + 38, $$3 + 12, -9671572);
            $$0.drawString(RealmsPendingInvitesScreen.this.font, RealmsUtil.convertToAgePresentationFromInstant($$1.date), $$2 + 38, $$3 + 24, -9671572);
            RowButton.drawButtonsInRow($$0, this.rowButtons, RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, $$2, $$3, $$4, $$5);
            RealmsUtil.renderPlayerFace($$0, $$2, $$3, 32, $$1.realmOwnerUuid);
        }

        @Override
        public Component getNarration() {
            Component $$0 = CommonComponents.b(Component.literal(this.pendingInvite.realmName), Component.literal(this.pendingInvite.realmOwnerName), RealmsUtil.convertToAgePresentationFromInstant(this.pendingInvite.date));
            return Component.a("narrator.select", $$0);
        }

        class AcceptRowButton
        extends RowButton {
            AcceptRowButton() {
                super(15, 15, 215, 5);
            }

            @Override
            protected void draw(GuiGraphics $$0, int $$1, int $$2, boolean $$3) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$3 ? ACCEPT_HIGHLIGHTED_SPRITE : ACCEPT_SPRITE, $$1, $$2, 18, 18);
                if ($$3) {
                    RealmsPendingInvitesScreen.this.toolTip = ACCEPT_INVITE;
                }
            }

            @Override
            public void onClick(int $$0) {
                RealmsPendingInvitesScreen.this.handleInvitation(true);
            }
        }

        class RejectRowButton
        extends RowButton {
            RejectRowButton() {
                super(15, 15, 235, 5);
            }

            @Override
            protected void draw(GuiGraphics $$0, int $$1, int $$2, boolean $$3) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$3 ? REJECT_HIGHLIGHTED_SPRITE : REJECT_SPRITE, $$1, $$2, 18, 18);
                if ($$3) {
                    RealmsPendingInvitesScreen.this.toolTip = REJECT_INVITE;
                }
            }

            @Override
            public void onClick(int $$0) {
                RealmsPendingInvitesScreen.this.handleInvitation(false);
            }
        }
    }
}

