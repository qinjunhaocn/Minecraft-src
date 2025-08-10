/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.Unit;
import com.mojang.realmsclient.client.FileDownload;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import org.slf4j.Logger;

public class RealmsDownloadLatestWorldScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ReentrantLock DOWNLOAD_LOCK = new ReentrantLock();
    private static final int BAR_WIDTH = 200;
    private static final int BAR_TOP = 80;
    private static final int BAR_BOTTOM = 95;
    private static final int BAR_BORDER = 1;
    private final Screen lastScreen;
    private final WorldDownload worldDownload;
    private final Component downloadTitle;
    private final RateLimiter narrationRateLimiter;
    private Button cancelButton;
    private final String worldName;
    private final DownloadStatus downloadStatus;
    @Nullable
    private volatile Component errorMessage;
    private volatile Component status = Component.translatable("mco.download.preparing");
    @Nullable
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean showDots = true;
    private volatile boolean finished;
    private volatile boolean extracting;
    @Nullable
    private Long previousWrittenBytes;
    @Nullable
    private Long previousTimeSnapshot;
    private long bytesPersSecond;
    private int animTick;
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private int dotIndex;
    private boolean checked;
    private final BooleanConsumer callback;

    public RealmsDownloadLatestWorldScreen(Screen $$0, WorldDownload $$1, String $$2, BooleanConsumer $$3) {
        super(GameNarrator.NO_TITLE);
        this.callback = $$3;
        this.lastScreen = $$0;
        this.worldName = $$2;
        this.worldDownload = $$1;
        this.downloadStatus = new DownloadStatus();
        this.downloadTitle = Component.translatable("mco.download.title");
        this.narrationRateLimiter = RateLimiter.create(0.1f);
    }

    @Override
    public void init() {
        this.cancelButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onClose()).bounds((this.width - 200) / 2, this.height - 42, 200, 20).build());
        this.checkDownloadSize();
    }

    private void checkDownloadSize() {
        if (this.finished || this.checked) {
            return;
        }
        this.checked = true;
        if (this.getContentLength(this.worldDownload.downloadLink) >= 0x140000000L) {
            MutableComponent $$02 = Component.a("mco.download.confirmation.oversized", Unit.humanReadable(0x140000000L));
            this.minecraft.setScreen(RealmsPopups.warningAcknowledgePopupScreen(this, $$02, $$0 -> {
                this.minecraft.setScreen(this);
                this.downloadSave();
            }));
        } else {
            this.downloadSave();
        }
    }

    private long getContentLength(String $$0) {
        FileDownload $$1 = new FileDownload();
        return $$1.contentLength($$0);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
            Component $$0 = this.createProgressNarrationMessage();
            this.minecraft.getNarrator().saySystemNow($$0);
        }
    }

    private Component createProgressNarrationMessage() {
        ArrayList<Component> $$0 = Lists.newArrayList();
        $$0.add(this.downloadTitle);
        $$0.add(this.status);
        if (this.progress != null) {
            $$0.add(Component.a("mco.download.percent", this.progress));
            $$0.add(Component.a("mco.download.speed.narration", Unit.humanReadable(this.bytesPersSecond)));
        }
        if (this.errorMessage != null) {
            $$0.add(this.errorMessage);
        }
        return CommonComponents.joinLines($$0);
    }

    @Override
    public void onClose() {
        this.cancelled = true;
        if (this.finished && this.callback != null && this.errorMessage == null) {
            this.callback.accept(true);
        }
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.downloadTitle, this.width / 2, 20, -1);
        $$0.drawCenteredString(this.font, this.status, this.width / 2, 50, -1);
        if (this.showDots) {
            this.drawDots($$0);
        }
        if (this.downloadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar($$0);
            this.drawDownloadSpeed($$0);
        }
        if (this.errorMessage != null) {
            $$0.drawCenteredString(this.font, this.errorMessage, this.width / 2, 110, -65536);
        }
    }

    private void drawDots(GuiGraphics $$0) {
        int $$1 = this.font.width(this.status);
        if (this.animTick != 0 && this.animTick % 10 == 0) {
            ++this.dotIndex;
        }
        $$0.drawString(this.font, DOTS[this.dotIndex % DOTS.length], this.width / 2 + $$1 / 2 + 5, 50, -1);
    }

    private void drawProgressBar(GuiGraphics $$0) {
        double $$1 = Math.min((double)this.downloadStatus.bytesWritten / (double)this.downloadStatus.totalBytes, 1.0);
        this.progress = String.format(Locale.ROOT, "%.1f", $$1 * 100.0);
        int $$2 = (this.width - 200) / 2;
        int $$3 = $$2 + (int)Math.round(200.0 * $$1);
        $$0.fill($$2 - 1, 79, $$3 + 1, 96, -1);
        $$0.fill($$2, 80, $$3, 95, -8355712);
        $$0.drawCenteredString(this.font, Component.a("mco.download.percent", this.progress), this.width / 2, 84, -1);
    }

    private void drawDownloadSpeed(GuiGraphics $$0) {
        if (this.animTick % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long $$1 = Util.getMillis() - this.previousTimeSnapshot;
                if ($$1 == 0L) {
                    $$1 = 1L;
                }
                this.bytesPersSecond = 1000L * (this.downloadStatus.bytesWritten - this.previousWrittenBytes) / $$1;
                this.drawDownloadSpeed0($$0, this.bytesPersSecond);
            }
            this.previousWrittenBytes = this.downloadStatus.bytesWritten;
            this.previousTimeSnapshot = Util.getMillis();
        } else {
            this.drawDownloadSpeed0($$0, this.bytesPersSecond);
        }
    }

    private void drawDownloadSpeed0(GuiGraphics $$0, long $$1) {
        if ($$1 > 0L) {
            int $$2 = this.font.width(this.progress);
            $$0.drawString(this.font, Component.a("mco.download.speed", Unit.humanReadable($$1)), this.width / 2 + $$2 / 2 + 15, 84, -1);
        }
    }

    private void downloadSave() {
        new Thread(() -> {
            try {
                if (!DOWNLOAD_LOCK.tryLock(1L, TimeUnit.SECONDS)) {
                    this.status = Component.translatable("mco.download.failed");
                    return;
                }
                if (this.cancelled) {
                    this.downloadCancelled();
                    return;
                }
                this.status = Component.a("mco.download.downloading", this.worldName);
                FileDownload $$0 = new FileDownload();
                $$0.contentLength(this.worldDownload.downloadLink);
                $$0.download(this.worldDownload, this.worldName, this.downloadStatus, this.minecraft.getLevelSource());
                while (!$$0.isFinished()) {
                    if ($$0.isError()) {
                        $$0.cancel();
                        this.errorMessage = Component.translatable("mco.download.failed");
                        this.cancelButton.setMessage(CommonComponents.GUI_DONE);
                        return;
                    }
                    if ($$0.isExtracting()) {
                        if (!this.extracting) {
                            this.status = Component.translatable("mco.download.extracting");
                        }
                        this.extracting = true;
                    }
                    if (this.cancelled) {
                        $$0.cancel();
                        this.downloadCancelled();
                        return;
                    }
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException $$1) {
                        LOGGER.error("Failed to check Realms backup download status");
                    }
                }
                this.finished = true;
                this.status = Component.translatable("mco.download.done");
                this.cancelButton.setMessage(CommonComponents.GUI_DONE);
            } catch (InterruptedException $$2) {
                LOGGER.error("Could not acquire upload lock");
            } catch (Exception $$3) {
                this.errorMessage = Component.translatable("mco.download.failed");
                LOGGER.info("Exception while downloading world", $$3);
            } finally {
                if (!DOWNLOAD_LOCK.isHeldByCurrentThread()) {
                    return;
                }
                DOWNLOAD_LOCK.unlock();
                this.showDots = false;
                this.finished = true;
            }
        }).start();
    }

    private void downloadCancelled() {
        this.status = Component.translatable("mco.download.cancelled");
    }

    public static class DownloadStatus {
        public volatile long bytesWritten;
        public volatile long totalBytes;
    }
}

