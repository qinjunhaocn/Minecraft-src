/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.Unit;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.client.worldupload.RealmsUploadException;
import com.mojang.realmsclient.client.worldupload.RealmsWorldUpload;
import com.mojang.realmsclient.client.worldupload.RealmsWorldUploadStatusTracker;
import com.mojang.realmsclient.dto.RealmsSetting;
import com.mojang.realmsclient.dto.RealmsSlot;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.RealmCreationTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.level.storage.LevelSummary;

public class RealmsUploadScreen
extends RealmsScreen
implements RealmsWorldUploadStatusTracker {
    private static final int BAR_WIDTH = 200;
    private static final int BAR_TOP = 80;
    private static final int BAR_BOTTOM = 95;
    private static final int BAR_BORDER = 1;
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private static final Component VERIFYING_TEXT = Component.translatable("mco.upload.verifying");
    private final RealmsResetWorldScreen lastScreen;
    private final LevelSummary selectedLevel;
    @Nullable
    private final RealmCreationTask realmCreationTask;
    private final long realmId;
    private final int slotId;
    final AtomicReference<RealmsWorldUpload> currentUpload = new AtomicReference();
    private final UploadStatus uploadStatus;
    private final RateLimiter narrationRateLimiter;
    @Nullable
    private volatile Component[] errorMessage;
    private volatile Component status = Component.translatable("mco.upload.preparing");
    @Nullable
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean uploadFinished;
    private volatile boolean showDots = true;
    private volatile boolean uploadStarted;
    @Nullable
    private Button backButton;
    @Nullable
    private Button cancelButton;
    private int tickCount;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    public RealmsUploadScreen(@Nullable RealmCreationTask $$0, long $$1, int $$2, RealmsResetWorldScreen $$3, LevelSummary $$4) {
        super(GameNarrator.NO_TITLE);
        this.realmCreationTask = $$0;
        this.realmId = $$1;
        this.slotId = $$2;
        this.lastScreen = $$3;
        this.selectedLevel = $$4;
        this.uploadStatus = new UploadStatus();
        this.narrationRateLimiter = RateLimiter.create(0.1f);
    }

    @Override
    public void init() {
        this.backButton = this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onBack()).build());
        this.backButton.visible = false;
        this.cancelButton = this.layout.addToFooter(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onCancel()).build());
        if (!this.uploadStarted) {
            if (this.lastScreen.slot == -1) {
                this.uploadStarted = true;
                this.upload();
            } else {
                ArrayList<LongRunningTask> $$02 = new ArrayList<LongRunningTask>();
                if (this.realmCreationTask != null) {
                    $$02.add(this.realmCreationTask);
                }
                $$02.add(new SwitchSlotTask(this.realmId, this.lastScreen.slot, () -> {
                    if (!this.uploadStarted) {
                        this.uploadStarted = true;
                        this.minecraft.execute(() -> {
                            this.minecraft.setScreen(this);
                            this.upload();
                        });
                    }
                }));
                this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, $$02.toArray(new LongRunningTask[0])));
            }
        }
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    private void onBack() {
        this.minecraft.setScreen(new RealmsConfigureWorldScreen(new RealmsMainScreen(new TitleScreen()), this.realmId));
    }

    private void onCancel() {
        this.cancelled = true;
        RealmsWorldUpload $$0 = this.currentUpload.get();
        if ($$0 != null) {
            $$0.cancel();
        } else {
            this.minecraft.setScreen(this.lastScreen);
        }
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            if (this.showDots) {
                this.onCancel();
            } else {
                this.onBack();
            }
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        Component[] $$4;
        super.render($$0, $$1, $$2, $$3);
        if (!this.uploadFinished && this.uploadStatus.uploadStarted() && this.uploadStatus.uploadCompleted() && this.cancelButton != null) {
            this.status = VERIFYING_TEXT;
            this.cancelButton.active = false;
        }
        $$0.drawCenteredString(this.font, this.status, this.width / 2, 50, -1);
        if (this.showDots) {
            $$0.drawString(this.font, DOTS[this.tickCount / 10 % DOTS.length], this.width / 2 + this.font.width(this.status) / 2 + 5, 50, -1);
        }
        if (this.uploadStatus.uploadStarted() && !this.cancelled) {
            this.drawProgressBar($$0);
            this.drawUploadSpeed($$0);
        }
        if (($$4 = this.errorMessage) != null) {
            for (int $$5 = 0; $$5 < $$4.length; ++$$5) {
                $$0.drawCenteredString(this.font, $$4[$$5], this.width / 2, 110 + 12 * $$5, -65536);
            }
        }
    }

    private void drawProgressBar(GuiGraphics $$0) {
        double $$1 = this.uploadStatus.getPercentage();
        this.progress = String.format(Locale.ROOT, "%.1f", $$1 * 100.0);
        int $$2 = (this.width - 200) / 2;
        int $$3 = $$2 + (int)Math.round(200.0 * $$1);
        $$0.fill($$2 - 1, 79, $$3 + 1, 96, -1);
        $$0.fill($$2, 80, $$3, 95, -8355712);
        $$0.drawCenteredString(this.font, Component.a("mco.upload.percent", this.progress), this.width / 2, 84, -1);
    }

    private void drawUploadSpeed(GuiGraphics $$0) {
        this.drawUploadSpeed0($$0, this.uploadStatus.getBytesPerSecond());
    }

    private void drawUploadSpeed0(GuiGraphics $$0, long $$1) {
        String $$2 = this.progress;
        if ($$1 > 0L && $$2 != null) {
            int $$3 = this.font.width($$2);
            String $$4 = "(" + Unit.humanReadable($$1) + "/s)";
            $$0.drawString(this.font, $$4, this.width / 2 + $$3 / 2 + 15, 84, -1);
        }
    }

    @Override
    public void tick() {
        super.tick();
        ++this.tickCount;
        this.uploadStatus.refreshBytesPerSecond();
        if (this.narrationRateLimiter.tryAcquire(1)) {
            Component $$0 = this.createProgressNarrationMessage();
            this.minecraft.getNarrator().saySystemNow($$0);
        }
    }

    private Component createProgressNarrationMessage() {
        Component[] $$1;
        ArrayList<Component> $$0 = Lists.newArrayList();
        $$0.add(this.status);
        if (this.progress != null) {
            $$0.add(Component.a("mco.upload.percent", this.progress));
        }
        if (($$1 = this.errorMessage) != null) {
            $$0.addAll(Arrays.asList($$1));
        }
        return CommonComponents.joinLines($$0);
    }

    private void upload() {
        RealmsWorldOptions $$12;
        RealmsSlot $$2;
        Path $$02 = this.minecraft.gameDirectory.toPath().resolve("saves").resolve(this.selectedLevel.getLevelId());
        RealmsWorldUpload $$3 = new RealmsWorldUpload($$02, $$2 = new RealmsSlot(this.slotId, $$12 = RealmsWorldOptions.createFromSettings(this.selectedLevel.getSettings(), true, this.selectedLevel.levelVersion().minecraftVersionName()), List.of((Object)RealmsSetting.hardcoreSetting(this.selectedLevel.getSettings().hardcore()))), this.minecraft.getUser(), this.realmId, this);
        if (!this.currentUpload.compareAndSet(null, $$3)) {
            throw new IllegalStateException("Tried to start uploading but was already uploading");
        }
        $$3.packAndUpload().handleAsync(($$0, $$1) -> {
            if ($$1 != null) {
                if ($$1 instanceof CompletionException) {
                    CompletionException $$2 = (CompletionException)$$1;
                    $$1 = $$2.getCause();
                }
                if ($$1 instanceof RealmsUploadException) {
                    RealmsUploadException $$3 = (RealmsUploadException)$$1;
                    if ($$3.getStatusMessage() != null) {
                        this.status = $$3.getStatusMessage();
                    }
                    this.a($$3.b());
                } else {
                    this.status = Component.a("mco.upload.failed", $$1.getMessage());
                }
            } else {
                this.status = Component.translatable("mco.upload.done");
                if (this.backButton != null) {
                    this.backButton.setMessage(CommonComponents.GUI_DONE);
                }
            }
            this.uploadFinished = true;
            this.showDots = false;
            if (this.backButton != null) {
                this.backButton.visible = true;
            }
            if (this.cancelButton != null) {
                this.cancelButton.visible = false;
            }
            this.currentUpload.set(null);
            return null;
        }, (Executor)this.minecraft);
    }

    private void a(@Nullable Component ... $$0) {
        this.errorMessage = $$0;
    }

    @Override
    public UploadStatus getUploadStatus() {
        return this.uploadStatus;
    }

    @Override
    public void setUploading() {
        this.status = Component.a("mco.upload.uploading", this.selectedLevel.getLevelName());
    }
}

