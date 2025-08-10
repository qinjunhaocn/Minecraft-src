/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.commands.PublishCommand;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.GameType;

public class ShareToLanScreen
extends Screen {
    private static final int PORT_LOWER_BOUND = 1024;
    private static final int PORT_HIGHER_BOUND = 65535;
    private static final Component ALLOW_COMMANDS_LABEL = Component.translatable("selectWorld.allowCommands");
    private static final Component GAME_MODE_LABEL = Component.translatable("selectWorld.gameMode");
    private static final Component INFO_TEXT = Component.translatable("lanServer.otherPlayers");
    private static final Component PORT_INFO_TEXT = Component.translatable("lanServer.port");
    private static final Component PORT_UNAVAILABLE = Component.a("lanServer.port.unavailable", 1024, 65535);
    private static final Component INVALID_PORT = Component.a("lanServer.port.invalid", 1024, 65535);
    private static final int INVALID_PORT_COLOR = -43691;
    private final Screen lastScreen;
    private GameType gameMode = GameType.SURVIVAL;
    private boolean commands;
    private int port = HttpUtil.getAvailablePort();
    @Nullable
    private EditBox portEdit;

    public ShareToLanScreen(Screen $$0) {
        super(Component.translatable("lanServer.title"));
        this.lastScreen = $$0;
    }

    @Override
    protected void init() {
        IntegratedServer $$02 = this.minecraft.getSingleplayerServer();
        this.gameMode = $$02.getDefaultGameType();
        this.commands = $$02.getWorldData().isAllowCommands();
        this.addRenderableWidget(CycleButton.builder(GameType::getShortDisplayName).a((GameType[])new GameType[]{GameType.SURVIVAL, GameType.SPECTATOR, GameType.CREATIVE, GameType.ADVENTURE}).withInitialValue(this.gameMode).create(this.width / 2 - 155, 100, 150, 20, GAME_MODE_LABEL, ($$0, $$1) -> {
            this.gameMode = $$1;
        }));
        this.addRenderableWidget(CycleButton.onOffBuilder(this.commands).create(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_LABEL, ($$0, $$1) -> {
            this.commands = $$1;
        }));
        Button $$12 = Button.builder(Component.translatable("lanServer.start"), $$1 -> {
            MutableComponent $$3;
            this.minecraft.setScreen(null);
            if ($$02.publishServer(this.gameMode, this.commands, this.port)) {
                MutableComponent $$2 = PublishCommand.getSuccessMessage(this.port);
            } else {
                $$3 = Component.translatable("commands.publish.failed");
            }
            this.minecraft.gui.getChat().addMessage($$3);
            this.minecraft.getNarrator().saySystemQueued($$3);
            this.minecraft.updateTitle();
        }).bounds(this.width / 2 - 155, this.height - 28, 150, 20).build();
        this.portEdit = new EditBox(this.font, this.width / 2 - 75, 160, 150, 20, Component.translatable("lanServer.port"));
        this.portEdit.setResponder($$1 -> {
            Component $$2 = this.tryParsePort((String)$$1);
            this.portEdit.setHint(Component.literal("" + this.port).withStyle(ChatFormatting.DARK_GRAY));
            if ($$2 == null) {
                this.portEdit.setTextColor(-2039584);
                this.portEdit.setTooltip(null);
                $$0.active = true;
            } else {
                this.portEdit.setTextColor(-43691);
                this.portEdit.setTooltip(Tooltip.create($$2));
                $$0.active = false;
            }
        });
        this.portEdit.setHint(Component.literal("" + this.port).withStyle(ChatFormatting.DARK_GRAY));
        this.addRenderableWidget(this.portEdit);
        this.addRenderableWidget($$12);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onClose()).bounds(this.width / 2 + 5, this.height - 28, 150, 20).build());
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Nullable
    private Component tryParsePort(String $$0) {
        if ($$0.isBlank()) {
            this.port = HttpUtil.getAvailablePort();
            return null;
        }
        try {
            this.port = Integer.parseInt($$0);
            if (this.port < 1024 || this.port > 65535) {
                return INVALID_PORT;
            }
            if (!HttpUtil.isPortAvailable(this.port)) {
                return PORT_UNAVAILABLE;
            }
            return null;
        } catch (NumberFormatException $$1) {
            this.port = HttpUtil.getAvailablePort();
            return INVALID_PORT;
        }
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 50, -1);
        $$0.drawCenteredString(this.font, INFO_TEXT, this.width / 2, 82, -1);
        $$0.drawCenteredString(this.font, PORT_INFO_TEXT, this.width / 2, 142, -1);
    }
}

