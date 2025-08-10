/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsSlot;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.OpenServerTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class RealmsBrokenWorldScreen
extends RealmsScreen {
    private static final ResourceLocation SLOT_FRAME_SPRITE = ResourceLocation.withDefaultNamespace("widget/slot_frame");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int DEFAULT_BUTTON_WIDTH = 80;
    private final Screen lastScreen;
    @Nullable
    private RealmsServer serverData;
    private final long serverId;
    private final Component[] message = new Component[]{Component.translatable("mco.brokenworld.message.line1"), Component.translatable("mco.brokenworld.message.line2")};
    private int leftX;
    private final List<Integer> slotsThatHasBeenDownloaded = Lists.newArrayList();
    private int animTick;

    public RealmsBrokenWorldScreen(Screen $$0, long $$1, boolean $$2) {
        super($$2 ? Component.translatable("mco.brokenworld.minigame.title") : Component.translatable("mco.brokenworld.title"));
        this.lastScreen = $$0;
        this.serverId = $$1;
    }

    @Override
    public void init() {
        this.leftX = this.width / 2 - 150;
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).bounds((this.width - 150) / 2, RealmsBrokenWorldScreen.row(13) - 5, 150, 20).build());
        if (this.serverData == null) {
            this.fetchServerData(this.serverId);
        } else {
            this.addButtons();
        }
    }

    @Override
    public Component getNarrationMessage() {
        return ComponentUtils.formatList(Stream.concat(Stream.of(this.title), Stream.of(this.message)).collect(Collectors.toList()), CommonComponents.SPACE);
    }

    private void addButtons() {
        for (Map.Entry<Integer, RealmsSlot> $$0 : this.serverData.slots.entrySet()) {
            Button $$4;
            boolean $$2;
            int $$13 = $$0.getKey();
            boolean bl = $$2 = $$13 != this.serverData.activeSlot || this.serverData.isMinigameActive();
            if ($$2) {
                Button $$3 = Button.builder(Component.translatable("mco.brokenworld.play"), $$1 -> this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new SwitchSlotTask(this.serverData.id, $$13, this::doSwitchOrReset)))).bounds(this.getFramePositionX($$13), RealmsBrokenWorldScreen.row(8), 80, 20).build();
                $$3.active = !this.serverData.slots.get((Object)Integer.valueOf((int)$$13)).options.empty;
            } else {
                $$4 = Button.builder(Component.translatable("mco.brokenworld.download"), $$12 -> this.minecraft.setScreen(RealmsPopups.infoPopupScreen(this, Component.translatable("mco.configure.world.restore.download.question.line1"), $$1 -> this.downloadWorld($$13)))).bounds(this.getFramePositionX($$13), RealmsBrokenWorldScreen.row(8), 80, 20).build();
            }
            if (this.slotsThatHasBeenDownloaded.contains($$13)) {
                $$4.active = false;
                $$4.setMessage(Component.translatable("mco.brokenworld.downloaded"));
            }
            this.addRenderableWidget($$4);
        }
    }

    @Override
    public void tick() {
        ++this.animTick;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 17, -1);
        for (int $$4 = 0; $$4 < this.message.length; ++$$4) {
            $$0.drawCenteredString(this.font, this.message[$$4], this.width / 2, RealmsBrokenWorldScreen.row(-1) + 3 + $$4 * 12, -6250336);
        }
        if (this.serverData == null) {
            return;
        }
        for (Map.Entry<Integer, RealmsSlot> $$5 : this.serverData.slots.entrySet()) {
            if ($$5.getValue().options.templateImage != null && $$5.getValue().options.templateId != -1L) {
                this.drawSlotFrame($$0, this.getFramePositionX($$5.getKey()), RealmsBrokenWorldScreen.row(1) + 5, $$1, $$2, this.serverData.activeSlot == $$5.getKey() && !this.isMinigame(), $$5.getValue().options.getSlotName($$5.getKey()), $$5.getKey(), $$5.getValue().options.templateId, $$5.getValue().options.templateImage, $$5.getValue().options.empty);
                continue;
            }
            this.drawSlotFrame($$0, this.getFramePositionX($$5.getKey()), RealmsBrokenWorldScreen.row(1) + 5, $$1, $$2, this.serverData.activeSlot == $$5.getKey() && !this.isMinigame(), $$5.getValue().options.getSlotName($$5.getKey()), $$5.getKey(), -1L, null, $$5.getValue().options.empty);
        }
    }

    private int getFramePositionX(int $$0) {
        return this.leftX + ($$0 - 1) * 110;
    }

    public Screen createErrorScreen(RealmsServiceException $$0) {
        return new RealmsGenericErrorScreen($$0, this.lastScreen);
    }

    private void fetchServerData(long $$02) {
        RealmsUtil.supplyAsync($$1 -> $$1.getOwnRealm($$02), RealmsUtil.openScreenAndLogOnFailure(this::createErrorScreen, "Couldn't get own world")).thenAcceptAsync($$0 -> {
            this.serverData = $$0;
            this.addButtons();
        }, (Executor)this.minecraft);
    }

    public void doSwitchOrReset() {
        new Thread(() -> {
            RealmsClient $$0 = RealmsClient.getOrCreate();
            if (this.serverData.state == RealmsServer.State.CLOSED) {
                this.minecraft.execute(() -> this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this, new OpenServerTask(this.serverData, this, true, this.minecraft))));
            } else {
                try {
                    RealmsServer $$1 = $$0.getOwnRealm(this.serverId);
                    this.minecraft.execute(() -> RealmsMainScreen.play($$1, this));
                } catch (RealmsServiceException $$2) {
                    LOGGER.error("Couldn't get own world", $$2);
                    this.minecraft.execute(() -> this.minecraft.setScreen(this.createErrorScreen($$2)));
                }
            }
        }).start();
    }

    private void downloadWorld(int $$0) {
        RealmsClient $$12 = RealmsClient.getOrCreate();
        try {
            WorldDownload $$2 = $$12.requestDownloadInfo(this.serverData.id, $$0);
            RealmsDownloadLatestWorldScreen $$3 = new RealmsDownloadLatestWorldScreen(this, $$2, this.serverData.getWorldName($$0), $$1 -> {
                if ($$1) {
                    this.slotsThatHasBeenDownloaded.add($$0);
                    this.clearWidgets();
                    this.addButtons();
                } else {
                    this.minecraft.setScreen(this);
                }
            });
            this.minecraft.setScreen($$3);
        } catch (RealmsServiceException $$4) {
            LOGGER.error("Couldn't download world data", $$4);
            this.minecraft.setScreen(new RealmsGenericErrorScreen($$4, (Screen)this));
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private boolean isMinigame() {
        return this.serverData != null && this.serverData.isMinigameActive();
    }

    private void drawSlotFrame(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, boolean $$5, String $$6, int $$7, long $$8, @Nullable String $$9, boolean $$10) {
        ResourceLocation $$16;
        if ($$10) {
            ResourceLocation $$11 = RealmsWorldSlotButton.EMPTY_SLOT_LOCATION;
        } else if ($$9 != null && $$8 != -1L) {
            ResourceLocation $$12 = RealmsTextureManager.worldTemplate(String.valueOf($$8), $$9);
        } else if ($$7 == 1) {
            ResourceLocation $$13 = RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_1;
        } else if ($$7 == 2) {
            ResourceLocation $$14 = RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_2;
        } else if ($$7 == 3) {
            ResourceLocation $$15 = RealmsWorldSlotButton.DEFAULT_WORLD_SLOT_3;
        } else {
            $$16 = RealmsTextureManager.worldTemplate(String.valueOf(this.serverData.minigameId), this.serverData.minigameImage);
        }
        if ($$5) {
            float $$17 = 0.9f + 0.1f * Mth.cos((float)this.animTick * 0.2f);
            $$0.blit(RenderPipelines.GUI_TEXTURED, $$16, $$1 + 3, $$2 + 3, 0.0f, 0.0f, 74, 74, 74, 74, 74, 74, ARGB.colorFromFloat(1.0f, $$17, $$17, $$17));
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_SPRITE, $$1, $$2, 80, 80);
        } else {
            int $$18 = ARGB.colorFromFloat(1.0f, 0.56f, 0.56f, 0.56f);
            $$0.blit(RenderPipelines.GUI_TEXTURED, $$16, $$1 + 3, $$2 + 3, 0.0f, 0.0f, 74, 74, 74, 74, 74, 74, $$18);
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_SPRITE, $$1, $$2, 80, 80, $$18);
        }
        $$0.drawCenteredString(this.font, $$6, $$1 + 40, $$2 + 66, -1);
    }
}

