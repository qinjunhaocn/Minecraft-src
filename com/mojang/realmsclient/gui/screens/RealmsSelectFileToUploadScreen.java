/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsUploadScreen;
import com.mojang.realmsclient.util.task.RealmCreationTask;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.slf4j.Logger;

public class RealmsSelectFileToUploadScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Component TITLE = Component.translatable("mco.upload.select.world.title");
    private static final Component UNABLE_TO_LOAD_WORLD = Component.translatable("selectWorld.unable_to_load");
    static final Component WORLD_TEXT = Component.translatable("selectWorld.world");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    @Nullable
    private final RealmCreationTask realmCreationTask;
    private final RealmsResetWorldScreen lastScreen;
    private final long realmId;
    private final int slotId;
    Button uploadButton;
    List<LevelSummary> levelList = Lists.newArrayList();
    int selectedWorld = -1;
    WorldSelectionList worldSelectionList;

    public RealmsSelectFileToUploadScreen(@Nullable RealmCreationTask $$0, long $$1, int $$2, RealmsResetWorldScreen $$3) {
        super(TITLE);
        this.realmCreationTask = $$0;
        this.lastScreen = $$3;
        this.realmId = $$1;
        this.slotId = $$2;
    }

    private void loadLevelList() {
        LevelStorageSource.LevelCandidates $$0 = this.minecraft.getLevelSource().findLevelCandidates();
        this.levelList = this.minecraft.getLevelSource().loadLevelSummaries($$0).join().stream().filter(LevelSummary::canUpload).collect(Collectors.toList());
        for (LevelSummary $$1 : this.levelList) {
            this.worldSelectionList.addEntry($$1);
        }
    }

    @Override
    public void init() {
        this.worldSelectionList = this.addRenderableWidget(new WorldSelectionList());
        try {
            this.loadLevelList();
        } catch (Exception $$02) {
            LOGGER.error("Couldn't load level list", $$02);
            this.minecraft.setScreen(new RealmsGenericErrorScreen(UNABLE_TO_LOAD_WORLD, Component.nullToEmpty($$02.getMessage()), this.lastScreen));
            return;
        }
        this.uploadButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.upload.button.name"), $$0 -> this.upload()).bounds(this.width / 2 - 154, this.height - 32, 153, 20).build());
        this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 + 6, this.height - 32, 153, 20).build());
        this.addLabel(new RealmsLabel(Component.translatable("mco.upload.select.world.subtitle"), this.width / 2, RealmsSelectFileToUploadScreen.row(-1), -6250336));
        if (this.levelList.isEmpty()) {
            this.addLabel(new RealmsLabel(Component.translatable("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, -1));
        }
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.a(this.getTitle(), this.createLabelNarration());
    }

    private void upload() {
        if (this.selectedWorld != -1) {
            LevelSummary $$0 = this.levelList.get(this.selectedWorld);
            this.minecraft.setScreen(new RealmsUploadScreen(this.realmCreationTask, this.realmId, this.slotId, this.lastScreen, $$0));
        }
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 13, -1);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    static Component gameModeName(LevelSummary $$0) {
        return $$0.getGameMode().getLongDisplayName();
    }

    static String formatLastPlayed(LevelSummary $$0) {
        return DATE_FORMAT.format(new Date($$0.getLastPlayed()));
    }

    class WorldSelectionList
    extends ObjectSelectionList<Entry> {
        public WorldSelectionList() {
            super(Minecraft.getInstance(), RealmsSelectFileToUploadScreen.this.width, RealmsSelectFileToUploadScreen.this.height - 40 - RealmsSelectFileToUploadScreen.row(0), RealmsSelectFileToUploadScreen.row(0), 36);
        }

        public void addEntry(LevelSummary $$0) {
            this.addEntry(new Entry($$0));
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf($$0);
            RealmsSelectFileToUploadScreen.this.uploadButton.active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0 && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount();
        }

        @Override
        public int getRowWidth() {
            return (int)((double)this.width * 0.6);
        }
    }

    class Entry
    extends ObjectSelectionList.Entry<Entry> {
        private final LevelSummary levelSummary;
        private final String name;
        private final Component id;
        private final Component info;

        public Entry(LevelSummary $$0) {
            this.levelSummary = $$0;
            this.name = $$0.getLevelName();
            this.id = Component.a("mco.upload.entry.id", new Object[]{$$0.getLevelId(), RealmsSelectFileToUploadScreen.formatLastPlayed($$0)});
            this.info = $$0.getInfo();
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.renderItem($$0, $$1, $$3, $$2);
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            RealmsSelectFileToUploadScreen.this.worldSelectionList.setSelectedIndex(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.levelSummary));
            return super.mouseClicked($$0, $$1, $$2);
        }

        protected void renderItem(GuiGraphics $$0, int $$1, int $$2, int $$3) {
            String $$5;
            if (this.name.isEmpty()) {
                String $$4 = String.valueOf(WORLD_TEXT) + " " + ($$1 + 1);
            } else {
                $$5 = this.name;
            }
            $$0.drawString(RealmsSelectFileToUploadScreen.this.font, $$5, $$2 + 2, $$3 + 1, -1);
            $$0.drawString(RealmsSelectFileToUploadScreen.this.font, this.id, $$2 + 2, $$3 + 12, -8355712);
            $$0.drawString(RealmsSelectFileToUploadScreen.this.font, this.info, $$2 + 2, $$3 + 12 + 10, -8355712);
        }

        @Override
        public Component getNarration() {
            Component $$0 = CommonComponents.b(Component.literal(this.levelSummary.getLevelName()), Component.literal(RealmsSelectFileToUploadScreen.formatLastPlayed(this.levelSummary)), RealmsSelectFileToUploadScreen.gameModeName(this.levelSummary));
            return Component.a("narrator.select", $$0);
        }
    }
}

