/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.gui.screens.configuration.RealmsBackupInfoScreen;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.DownloadTask;
import com.mojang.realmsclient.util.task.RestoreTask;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import org.slf4j.Logger;

public class RealmsBackupScreen
extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Component TITLE = Component.translatable("mco.configure.world.backup");
    static final Component RESTORE_TOOLTIP = Component.translatable("mco.backup.button.restore");
    static final Component HAS_CHANGES_TOOLTIP = Component.translatable("mco.backup.changes.tooltip");
    private static final Component NO_BACKUPS_LABEL = Component.translatable("mco.backup.nobackups");
    private static final Component DOWNLOAD_LATEST = Component.translatable("mco.backup.button.download");
    private static final String UPLOADED_KEY = "uploaded";
    private static final int PADDING = 8;
    final RealmsConfigureWorldScreen lastScreen;
    List<Backup> backups = Collections.emptyList();
    @Nullable
    BackupObjectSelectionList backupList;
    final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final int slotId;
    @Nullable
    Button downloadButton;
    final RealmsServer serverData;
    boolean noBackups = false;

    public RealmsBackupScreen(RealmsConfigureWorldScreen $$0, RealmsServer $$1, int $$2) {
        super(TITLE);
        this.lastScreen = $$0;
        this.serverData = $$1;
        this.slotId = $$2;
    }

    @Override
    public void init() {
        this.layout.addTitleHeader(TITLE, this.font);
        this.backupList = this.layout.addToContents(new BackupObjectSelectionList(this));
        LinearLayout $$02 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        this.downloadButton = $$02.addChild(Button.builder(DOWNLOAD_LATEST, $$0 -> this.downloadClicked()).build());
        this.downloadButton.active = false;
        $$02.addChild(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).build());
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
        this.fetchRealmsBackups();
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        if (this.noBackups && this.backupList != null) {
            $$0.drawString(this.font, NO_BACKUPS_LABEL, this.width / 2 - this.font.width(NO_BACKUPS_LABEL) / 2, this.backupList.getY() + this.backupList.getHeight() / 2 - this.font.lineHeight / 2, -1);
        }
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.backupList != null) {
            this.backupList.updateSize(this.width, this.layout);
        }
    }

    private void fetchRealmsBackups() {
        new Thread("Realms-fetch-backups"){

            @Override
            public void run() {
                RealmsClient $$0 = RealmsClient.getOrCreate();
                try {
                    List<Backup> $$1 = $$0.backupsFor((long)RealmsBackupScreen.this.serverData.id).backups;
                    RealmsBackupScreen.this.minecraft.execute(() -> {
                        RealmsBackupScreen.this.backups = $$1;
                        RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                        if (!RealmsBackupScreen.this.noBackups && RealmsBackupScreen.this.downloadButton != null) {
                            RealmsBackupScreen.this.downloadButton.active = true;
                        }
                        if (RealmsBackupScreen.this.backupList != null) {
                            RealmsBackupScreen.this.backupList.replaceEntries(RealmsBackupScreen.this.backups.stream().map($$0 -> new Entry((Backup)$$0)).toList());
                        }
                    });
                } catch (RealmsServiceException $$2) {
                    LOGGER.error("Couldn't request backups", $$2);
                }
            }
        }.start();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private void downloadClicked() {
        this.minecraft.setScreen(RealmsPopups.infoPopupScreen(this, Component.translatable("mco.configure.world.restore.download.question.line1"), $$0 -> this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new DownloadTask(this.serverData.id, this.slotId, (String)Objects.requireNonNullElse((Object)this.serverData.name, (Object)"") + " (" + this.serverData.slots.get((Object)Integer.valueOf((int)this.serverData.activeSlot)).options.getSlotName(this.serverData.activeSlot) + ")", this)))));
    }

    class BackupObjectSelectionList
    extends ContainerObjectSelectionList<Entry> {
        private static final int ITEM_HEIGHT = 36;

        public BackupObjectSelectionList(RealmsBackupScreen realmsBackupScreen) {
            super(Minecraft.getInstance(), realmsBackupScreen.width, realmsBackupScreen.layout.getContentHeight(), realmsBackupScreen.layout.getHeaderHeight(), 36);
        }

        @Override
        public int getRowWidth() {
            return 300;
        }
    }

    class Entry
    extends ContainerObjectSelectionList.Entry<Entry> {
        private static final int Y_PADDING = 2;
        private final Backup backup;
        @Nullable
        private Button restoreButton;
        @Nullable
        private Button changesButton;
        private final List<AbstractWidget> children = new ArrayList<AbstractWidget>();

        public Entry(Backup $$02) {
            this.backup = $$02;
            this.populateChangeList($$02);
            if (!$$02.changeList.isEmpty()) {
                this.changesButton = Button.builder(HAS_CHANGES_TOOLTIP, $$0 -> RealmsBackupScreen.this.minecraft.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, this.backup))).width(8 + RealmsBackupScreen.this.font.width(HAS_CHANGES_TOOLTIP)).createNarration($$0 -> CommonComponents.a(Component.a("mco.backup.narration", this.getShortBackupDate()), (Component)$$0.get())).build();
                this.children.add(this.changesButton);
            }
            if (!RealmsBackupScreen.this.serverData.expired) {
                this.restoreButton = Button.builder(RESTORE_TOOLTIP, $$0 -> this.restoreClicked()).width(8 + RealmsBackupScreen.this.font.width(HAS_CHANGES_TOOLTIP)).createNarration($$0 -> CommonComponents.a(Component.a("mco.backup.narration", this.getShortBackupDate()), (Component)$$0.get())).build();
                this.children.add(this.restoreButton);
            }
        }

        private void populateChangeList(Backup $$0) {
            int $$1 = RealmsBackupScreen.this.backups.indexOf($$0);
            if ($$1 == RealmsBackupScreen.this.backups.size() - 1) {
                return;
            }
            Backup $$2 = RealmsBackupScreen.this.backups.get($$1 + 1);
            for (String $$3 : $$0.metadata.keySet()) {
                if (!$$3.contains(RealmsBackupScreen.UPLOADED_KEY) && $$2.metadata.containsKey($$3)) {
                    if ($$0.metadata.get($$3).equals($$2.metadata.get($$3))) continue;
                    this.addToChangeList($$3);
                    continue;
                }
                this.addToChangeList($$3);
            }
        }

        private void addToChangeList(String $$0) {
            if ($$0.contains(RealmsBackupScreen.UPLOADED_KEY)) {
                String $$1 = DateFormat.getDateTimeInstance(3, 3).format(this.backup.lastModifiedDate);
                this.backup.changeList.put($$0, $$1);
                this.backup.setUploadedVersion(true);
            } else {
                this.backup.changeList.put($$0, this.backup.metadata.get($$0));
            }
        }

        private String getShortBackupDate() {
            return DateFormat.getDateTimeInstance(3, 3).format(this.backup.lastModifiedDate);
        }

        private void restoreClicked() {
            Component $$02 = RealmsUtil.convertToAgePresentationFromInstant(this.backup.lastModifiedDate);
            MutableComponent $$1 = Component.a("mco.configure.world.restore.question.line1", new Object[]{this.getShortBackupDate(), $$02});
            RealmsBackupScreen.this.minecraft.setScreen(RealmsPopups.warningPopupScreen(RealmsBackupScreen.this, $$1, $$0 -> {
                RealmsConfigureWorldScreen $$1 = RealmsBackupScreen.this.lastScreen.getNewScreen();
                RealmsBackupScreen.this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen($$1, new RestoreTask(this.backup, RealmsBackupScreen.this.serverData.id, $$1)));
            }));
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            int $$10 = $$2 + $$5 / 2;
            int $$11 = $$10 - ((RealmsBackupScreen)RealmsBackupScreen.this).font.lineHeight - 2;
            int $$12 = $$10 + 2;
            int $$13 = this.backup.isUploadedVersion() ? -8388737 : -1;
            $$0.drawString(RealmsBackupScreen.this.font, Component.a("mco.backup.entry", RealmsUtil.convertToAgePresentationFromInstant(this.backup.lastModifiedDate)), $$3, $$11, $$13);
            $$0.drawString(RealmsBackupScreen.this.font, this.getMediumDatePresentation(this.backup.lastModifiedDate), $$3, $$12, -11776948);
            int $$14 = 0;
            int $$15 = $$2 + $$5 / 2 - 10;
            if (this.restoreButton != null) {
                this.restoreButton.setX($$3 + $$4 - ($$14 += this.restoreButton.getWidth() + 8));
                this.restoreButton.setY($$15);
                this.restoreButton.render($$0, $$6, $$7, $$9);
            }
            if (this.changesButton != null) {
                this.changesButton.setX($$3 + $$4 - ($$14 += this.changesButton.getWidth() + 8));
                this.changesButton.setY($$15);
                this.changesButton.render($$0, $$6, $$7, $$9);
            }
        }

        private String getMediumDatePresentation(Date $$0) {
            return DateFormat.getDateTimeInstance(3, 3).format($$0);
        }
    }
}

