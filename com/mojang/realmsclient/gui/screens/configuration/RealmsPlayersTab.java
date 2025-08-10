/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.gui.screens.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.screens.RealmsConfirmScreen;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigurationTab;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.configuration.RealmsInviteScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

class RealmsPlayersTab
extends GridLayoutTab
implements RealmsConfigurationTab {
    static final Logger LOGGER = LogUtils.getLogger();
    static final Component TITLE = Component.translatable("mco.configure.world.players.title");
    static final Component QUESTION_TITLE = Component.translatable("mco.question");
    private static final int PADDING = 8;
    final RealmsConfigureWorldScreen configurationScreen;
    final Minecraft minecraft;
    RealmsServer serverData;
    private final InvitedObjectSelectionList invitedList;

    RealmsPlayersTab(RealmsConfigureWorldScreen $$0, Minecraft $$1, RealmsServer $$2) {
        super(TITLE);
        this.configurationScreen = $$0;
        this.minecraft = $$1;
        this.serverData = $$2;
        GridLayout.RowHelper $$32 = this.layout.spacing(8).createRowHelper(1);
        this.invitedList = $$32.addChild(new InvitedObjectSelectionList($$0.width, this.calculateListHeight()), LayoutSettings.defaults().alignVerticallyTop().alignHorizontallyCenter());
        $$32.addChild(Button.builder(Component.translatable("mco.configure.world.buttons.invite"), $$3 -> $$1.setScreen(new RealmsInviteScreen($$0, $$2))).build(), LayoutSettings.defaults().alignVerticallyBottom().alignHorizontallyCenter());
        this.updateData($$2);
    }

    public int calculateListHeight() {
        return this.configurationScreen.getContentHeight() - 20 - 16;
    }

    @Override
    public void doLayout(ScreenRectangle $$0) {
        this.invitedList.setSize(this.configurationScreen.width, this.calculateListHeight());
        super.doLayout($$0);
    }

    @Override
    public void updateData(RealmsServer $$0) {
        this.serverData = $$0;
        this.invitedList.children().clear();
        for (PlayerInfo $$1 : $$0.players) {
            this.invitedList.children().add(new Entry($$1));
        }
    }

    class InvitedObjectSelectionList
    extends ContainerObjectSelectionList<Entry> {
        private static final int ITEM_HEIGHT = 36;

        public InvitedObjectSelectionList(int $$0, int $$1) {
            Minecraft minecraft = Minecraft.getInstance();
            int n = RealmsPlayersTab.this.configurationScreen.getHeaderHeight();
            Objects.requireNonNull(RealmsPlayersTab.this.configurationScreen.getFont());
            super(minecraft, $$0, $$1, n, 36, (int)(9.0f * 1.5f));
        }

        @Override
        protected void renderHeader(GuiGraphics $$0, int $$1, int $$2) {
            String $$3 = RealmsPlayersTab.this.serverData.players != null ? Integer.toString(RealmsPlayersTab.this.serverData.players.size()) : "0";
            MutableComponent $$4 = Component.a("mco.configure.world.invited.number", $$3).withStyle(ChatFormatting.UNDERLINE);
            $$0.drawString(RealmsPlayersTab.this.configurationScreen.getFont(), $$4, $$1 + this.getRowWidth() / 2 - RealmsPlayersTab.this.configurationScreen.getFont().width($$4) / 2, $$2, -1);
        }

        @Override
        protected void renderListBackground(GuiGraphics $$0) {
        }

        @Override
        protected void renderListSeparators(GuiGraphics $$0) {
        }

        @Override
        public int getRowWidth() {
            return 300;
        }
    }

    class Entry
    extends ContainerObjectSelectionList.Entry<Entry> {
        protected static final int SKIN_FACE_SIZE = 32;
        private static final Component NORMAL_USER_TEXT = Component.translatable("mco.configure.world.invites.normal.tooltip");
        private static final Component OP_TEXT = Component.translatable("mco.configure.world.invites.ops.tooltip");
        private static final Component REMOVE_TEXT = Component.translatable("mco.configure.world.invites.remove.tooltip");
        private static final ResourceLocation MAKE_OP_SPRITE = ResourceLocation.withDefaultNamespace("player_list/make_operator");
        private static final ResourceLocation REMOVE_OP_SPRITE = ResourceLocation.withDefaultNamespace("player_list/remove_operator");
        private static final ResourceLocation REMOVE_PLAYER_SPRITE = ResourceLocation.withDefaultNamespace("player_list/remove_player");
        private static final int ICON_WIDTH = 8;
        private static final int ICON_HEIGHT = 7;
        private final PlayerInfo playerInfo;
        private final Button removeButton;
        private final Button makeOpButton;
        private final Button removeOpButton;

        public Entry(PlayerInfo $$0) {
            this.playerInfo = $$0;
            int $$12 = RealmsPlayersTab.this.serverData.players.indexOf(this.playerInfo);
            this.makeOpButton = SpriteIconButton.builder(NORMAL_USER_TEXT, $$1 -> this.op($$12), false).sprite(MAKE_OP_SPRITE, 8, 7).width(16 + RealmsPlayersTab.this.configurationScreen.getFont().width(NORMAL_USER_TEXT)).narration($$1 -> CommonComponents.a(Component.a("mco.invited.player.narration", $$0.getName()), (Component)$$1.get(), Component.a("narration.cycle_button.usage.focused", OP_TEXT))).build();
            this.removeOpButton = SpriteIconButton.builder(OP_TEXT, $$1 -> this.deop($$12), false).sprite(REMOVE_OP_SPRITE, 8, 7).width(16 + RealmsPlayersTab.this.configurationScreen.getFont().width(OP_TEXT)).narration($$1 -> CommonComponents.a(Component.a("mco.invited.player.narration", $$0.getName()), (Component)$$1.get(), Component.a("narration.cycle_button.usage.focused", NORMAL_USER_TEXT))).build();
            this.removeButton = SpriteIconButton.builder(REMOVE_TEXT, $$1 -> this.uninvite($$12), false).sprite(REMOVE_PLAYER_SPRITE, 8, 7).width(16 + RealmsPlayersTab.this.configurationScreen.getFont().width(REMOVE_TEXT)).narration($$1 -> CommonComponents.a(Component.a("mco.invited.player.narration", $$0.getName()), (Component)$$1.get())).build();
            this.updateOpButtons();
        }

        private void op(int $$02) {
            UUID $$12 = RealmsPlayersTab.this.serverData.players.get($$02).getUuid();
            RealmsUtil.supplyAsync($$1 -> $$1.op(RealmsPlayersTab.this.serverData.id, $$12), $$0 -> LOGGER.error("Couldn't op the user", (Throwable)$$0)).thenAcceptAsync($$0 -> {
                this.updateOps((Ops)$$0);
                this.updateOpButtons();
                this.setFocused(this.removeOpButton);
            }, (Executor)RealmsPlayersTab.this.minecraft);
        }

        private void deop(int $$02) {
            UUID $$12 = RealmsPlayersTab.this.serverData.players.get($$02).getUuid();
            RealmsUtil.supplyAsync($$1 -> $$1.deop(RealmsPlayersTab.this.serverData.id, $$12), $$0 -> LOGGER.error("Couldn't deop the user", (Throwable)$$0)).thenAcceptAsync($$0 -> {
                this.updateOps((Ops)$$0);
                this.updateOpButtons();
                this.setFocused(this.makeOpButton);
            }, (Executor)RealmsPlayersTab.this.minecraft);
        }

        private void uninvite(int $$0) {
            if ($$0 >= 0 && $$0 < RealmsPlayersTab.this.serverData.players.size()) {
                PlayerInfo $$1 = RealmsPlayersTab.this.serverData.players.get($$0);
                RealmsConfirmScreen $$22 = new RealmsConfirmScreen($$2 -> {
                    if ($$2) {
                        RealmsUtil.runAsync($$1 -> $$1.uninvite(RealmsPlayersTab.this.serverData.id, $$1.getUuid()), $$0 -> LOGGER.error("Couldn't uninvite user", (Throwable)$$0));
                        RealmsPlayersTab.this.serverData.players.remove($$0);
                        RealmsPlayersTab.this.updateData(RealmsPlayersTab.this.serverData);
                    }
                    RealmsPlayersTab.this.minecraft.setScreen(RealmsPlayersTab.this.configurationScreen);
                }, QUESTION_TITLE, Component.a("mco.configure.world.uninvite.player", $$1.getName()));
                RealmsPlayersTab.this.minecraft.setScreen($$22);
            }
        }

        private void updateOps(Ops $$0) {
            for (PlayerInfo $$1 : RealmsPlayersTab.this.serverData.players) {
                $$1.setOperator($$0.ops.contains($$1.getName()));
            }
        }

        private void updateOpButtons() {
            this.makeOpButton.visible = !this.playerInfo.isOperator();
            this.removeOpButton.visible = !this.makeOpButton.visible;
        }

        private Button activeOpButton() {
            if (this.makeOpButton.visible) {
                return this.makeOpButton;
            }
            return this.removeOpButton;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.activeOpButton(), this.removeButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.activeOpButton(), this.removeButton);
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            int $$12;
            if (!this.playerInfo.getAccepted()) {
                int $$10 = -6250336;
            } else if (this.playerInfo.getOnline()) {
                int $$11 = -16711936;
            } else {
                $$12 = -1;
            }
            int $$13 = $$2 + $$5 / 2 - 16;
            RealmsUtil.renderPlayerFace($$0, $$3, $$13, 32, this.playerInfo.getUuid());
            int $$14 = $$2 + $$5 / 2 - RealmsPlayersTab.this.configurationScreen.getFont().lineHeight / 2;
            $$0.drawString(RealmsPlayersTab.this.configurationScreen.getFont(), this.playerInfo.getName(), $$3 + 8 + 32, $$14, $$12);
            int $$15 = $$2 + $$5 / 2 - 10;
            int $$16 = $$3 + $$4 - this.removeButton.getWidth();
            this.removeButton.setPosition($$16, $$15);
            this.removeButton.render($$0, $$6, $$7, $$9);
            int $$17 = $$16 - this.activeOpButton().getWidth() - 8;
            this.makeOpButton.setPosition($$17, $$15);
            this.makeOpButton.render($$0, $$6, $$7, $$9);
            this.removeOpButton.setPosition($$17, $$15);
            this.removeOpButton.render($$0, $$6, $$7, $$9);
        }
    }
}

