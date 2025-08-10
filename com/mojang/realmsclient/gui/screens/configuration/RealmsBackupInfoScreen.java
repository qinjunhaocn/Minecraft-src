/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.gui.screens.configuration.RealmsSlotOptionsScreen;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsBackupInfoScreen
extends RealmsScreen {
    private static final Component TITLE = Component.translatable("mco.backup.info.title");
    private static final Component UNKNOWN = Component.translatable("mco.backup.unknown");
    private final Screen lastScreen;
    final Backup backup;
    final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private BackupInfoList backupInfoList;

    public RealmsBackupInfoScreen(Screen $$0, Backup $$1) {
        super(TITLE);
        this.lastScreen = $$0;
        this.backup = $$1;
    }

    @Override
    public void init() {
        this.layout.addTitleHeader(TITLE, this.font);
        this.backupInfoList = this.layout.addToContents(new BackupInfoList(this.minecraft));
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).build());
        this.repositionElements();
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
    }

    @Override
    protected void repositionElements() {
        this.backupInfoList.setSize(this.width, this.layout.getContentHeight());
        this.layout.arrangeElements();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    Component checkForSpecificMetadata(String $$0, String $$1) {
        String $$2 = $$0.toLowerCase(Locale.ROOT);
        if ($$2.contains("game") && $$2.contains("mode")) {
            return this.gameModeMetadata($$1);
        }
        if ($$2.contains("game") && $$2.contains("difficulty")) {
            return this.gameDifficultyMetadata($$1);
        }
        return Component.literal($$1);
    }

    private Component gameDifficultyMetadata(String $$0) {
        try {
            return RealmsSlotOptionsScreen.DIFFICULTIES.get(Integer.parseInt($$0)).getDisplayName();
        } catch (Exception $$1) {
            return UNKNOWN;
        }
    }

    private Component gameModeMetadata(String $$0) {
        try {
            return RealmsSlotOptionsScreen.GAME_MODES.get(Integer.parseInt($$0)).getShortDisplayName();
        } catch (Exception $$1) {
            return UNKNOWN;
        }
    }

    class BackupInfoList
    extends ObjectSelectionList<BackupInfoListEntry> {
        public BackupInfoList(Minecraft $$02) {
            super($$02, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.layout.getContentHeight(), RealmsBackupInfoScreen.this.layout.getHeaderHeight(), 36);
            if (RealmsBackupInfoScreen.this.backup.changeList != null) {
                RealmsBackupInfoScreen.this.backup.changeList.forEach(($$0, $$1) -> this.addEntry(new BackupInfoListEntry((String)$$0, (String)$$1)));
            }
        }
    }

    class BackupInfoListEntry
    extends ObjectSelectionList.Entry<BackupInfoListEntry> {
        private static final Component TEMPLATE_NAME = Component.translatable("mco.backup.entry.templateName");
        private static final Component GAME_DIFFICULTY = Component.translatable("mco.backup.entry.gameDifficulty");
        private static final Component NAME = Component.translatable("mco.backup.entry.name");
        private static final Component GAME_SERVER_VERSION = Component.translatable("mco.backup.entry.gameServerVersion");
        private static final Component UPLOADED = Component.translatable("mco.backup.entry.uploaded");
        private static final Component ENABLED_PACK = Component.translatable("mco.backup.entry.enabledPack");
        private static final Component DESCRIPTION = Component.translatable("mco.backup.entry.description");
        private static final Component GAME_MODE = Component.translatable("mco.backup.entry.gameMode");
        private static final Component SEED = Component.translatable("mco.backup.entry.seed");
        private static final Component WORLD_TYPE = Component.translatable("mco.backup.entry.worldType");
        private static final Component UNDEFINED = Component.translatable("mco.backup.entry.undefined");
        private final String key;
        private final String value;

        public BackupInfoListEntry(String $$0, String $$1) {
            this.key = $$0;
            this.value = $$1;
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            $$0.drawString(RealmsBackupInfoScreen.this.font, this.translateKey(this.key), $$3, $$2, -6250336);
            $$0.drawString(RealmsBackupInfoScreen.this.font, RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.key, this.value), $$3, $$2 + 12, -1);
        }

        private Component translateKey(String $$0) {
            return switch ($$0) {
                case "template_name" -> TEMPLATE_NAME;
                case "game_difficulty" -> GAME_DIFFICULTY;
                case "name" -> NAME;
                case "game_server_version" -> GAME_SERVER_VERSION;
                case "uploaded" -> UPLOADED;
                case "enabled_packs" -> ENABLED_PACK;
                case "description" -> DESCRIPTION;
                case "game_mode" -> GAME_MODE;
                case "seed" -> SEED;
                case "world_type" -> WORLD_TYPE;
                default -> UNDEFINED;
            };
        }

        @Override
        public Component getNarration() {
            return Component.a("narrator.select", this.key + " " + this.value);
        }
    }
}

