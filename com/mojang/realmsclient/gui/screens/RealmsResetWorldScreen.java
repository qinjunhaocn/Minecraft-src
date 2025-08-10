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
import com.mojang.realmsclient.client.worldupload.RealmsCreateWorldFlow;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsSelectFileToUploadScreen;
import com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.RealmCreationTask;
import com.mojang.realmsclient.util.task.ResettingTemplateWorldTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.slf4j.Logger;

public class RealmsResetWorldScreen
extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Component CREATE_REALM_TITLE = Component.translatable("mco.selectServer.create");
    private static final Component CREATE_REALM_SUBTITLE = Component.translatable("mco.selectServer.create.subtitle");
    private static final Component CREATE_WORLD_TITLE = Component.translatable("mco.configure.world.switch.slot");
    private static final Component CREATE_WORLD_SUBTITLE = Component.translatable("mco.configure.world.switch.slot.subtitle");
    private static final Component GENERATE_NEW_WORLD = Component.translatable("mco.reset.world.generate");
    private static final Component RESET_WORLD_TITLE = Component.translatable("mco.reset.world.title");
    private static final Component RESET_WORLD_SUBTITLE = Component.translatable("mco.reset.world.warning");
    public static final Component CREATE_WORLD_RESET_TASK_TITLE = Component.translatable("mco.create.world.reset.title");
    private static final Component RESET_WORLD_RESET_TASK_TITLE = Component.translatable("mco.reset.world.resetting.screen.title");
    private static final Component WORLD_TEMPLATES_TITLE = Component.translatable("mco.reset.world.template");
    private static final Component ADVENTURES_TITLE = Component.translatable("mco.reset.world.adventure");
    private static final Component EXPERIENCES_TITLE = Component.translatable("mco.reset.world.experience");
    private static final Component INSPIRATION_TITLE = Component.translatable("mco.reset.world.inspiration");
    private final Screen lastScreen;
    private final RealmsServer serverData;
    private final Component subtitle;
    private final int subtitleColor;
    private final Component resetTaskTitle;
    private static final ResourceLocation UPLOAD_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/upload.png");
    private static final ResourceLocation ADVENTURE_MAP_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/adventure.png");
    private static final ResourceLocation SURVIVAL_SPAWN_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/survival_spawn.png");
    private static final ResourceLocation NEW_WORLD_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/new_world.png");
    private static final ResourceLocation EXPERIENCE_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/experience.png");
    private static final ResourceLocation INSPIRATION_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/inspiration.png");
    WorldTemplatePaginatedList templates;
    WorldTemplatePaginatedList adventuremaps;
    WorldTemplatePaginatedList experiences;
    WorldTemplatePaginatedList inspirations;
    public final int slot;
    @Nullable
    private final RealmCreationTask realmCreationTask;
    private final Runnable resetWorldRunnable;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    private RealmsResetWorldScreen(Screen $$0, RealmsServer $$1, int $$2, Component $$3, Component $$4, int $$5, Component $$6, Runnable $$7) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, null, $$7);
    }

    public RealmsResetWorldScreen(Screen $$0, RealmsServer $$1, int $$2, Component $$3, Component $$4, int $$5, Component $$6, @Nullable RealmCreationTask $$7, Runnable $$8) {
        super($$3);
        this.lastScreen = $$0;
        this.serverData = $$1;
        this.slot = $$2;
        this.subtitle = $$4;
        this.subtitleColor = $$5;
        this.resetTaskTitle = $$6;
        this.realmCreationTask = $$7;
        this.resetWorldRunnable = $$8;
    }

    public static RealmsResetWorldScreen forNewRealm(Screen $$0, RealmsServer $$1, RealmCreationTask $$2, Runnable $$3) {
        return new RealmsResetWorldScreen($$0, $$1, $$1.activeSlot, CREATE_REALM_TITLE, CREATE_REALM_SUBTITLE, -6250336, CREATE_WORLD_RESET_TASK_TITLE, $$2, $$3);
    }

    public static RealmsResetWorldScreen forEmptySlot(Screen $$0, int $$1, RealmsServer $$2, Runnable $$3) {
        return new RealmsResetWorldScreen($$0, $$2, $$1, CREATE_WORLD_TITLE, CREATE_WORLD_SUBTITLE, -6250336, CREATE_WORLD_RESET_TASK_TITLE, $$3);
    }

    public static RealmsResetWorldScreen forResetSlot(Screen $$0, RealmsServer $$1, Runnable $$2) {
        return new RealmsResetWorldScreen($$0, $$1, $$1.activeSlot, RESET_WORLD_TITLE, RESET_WORLD_SUBTITLE, -65536, RESET_WORLD_RESET_TASK_TITLE, $$2);
    }

    @Override
    public void init() {
        LinearLayout $$02 = this.layout.addToHeader(LinearLayout.vertical());
        $$02.defaultCellSetting().padding(this.font.lineHeight / 3);
        $$02.addChild(new StringWidget(this.title, this.font), LayoutSettings::alignHorizontallyCenter);
        $$02.addChild(new StringWidget(this.subtitle, this.font).setColor(this.subtitleColor), LayoutSettings::alignHorizontallyCenter);
        new Thread("Realms-reset-world-fetcher"){

            @Override
            public void run() {
                RealmsClient $$0 = RealmsClient.getOrCreate();
                try {
                    WorldTemplatePaginatedList $$1 = $$0.fetchWorldTemplates(1, 10, RealmsServer.WorldType.NORMAL);
                    WorldTemplatePaginatedList $$2 = $$0.fetchWorldTemplates(1, 10, RealmsServer.WorldType.ADVENTUREMAP);
                    WorldTemplatePaginatedList $$3 = $$0.fetchWorldTemplates(1, 10, RealmsServer.WorldType.EXPERIENCE);
                    WorldTemplatePaginatedList $$4 = $$0.fetchWorldTemplates(1, 10, RealmsServer.WorldType.INSPIRATION);
                    RealmsResetWorldScreen.this.minecraft.execute(() -> {
                        RealmsResetWorldScreen.this.templates = $$1;
                        RealmsResetWorldScreen.this.adventuremaps = $$2;
                        RealmsResetWorldScreen.this.experiences = $$3;
                        RealmsResetWorldScreen.this.inspirations = $$4;
                    });
                } catch (RealmsServiceException $$5) {
                    LOGGER.error("Couldn't fetch templates in reset world", $$5);
                }
            }
        }.start();
        GridLayout $$12 = this.layout.addToContents(new GridLayout());
        GridLayout.RowHelper $$2 = $$12.createRowHelper(3);
        $$2.defaultCellSetting().paddingHorizontal(16);
        $$2.addChild(new FrameButton(this.minecraft.font, GENERATE_NEW_WORLD, NEW_WORLD_LOCATION, $$0 -> RealmsCreateWorldFlow.createWorld(this.minecraft, this.lastScreen, this, this.slot, this.serverData, this.realmCreationTask)));
        $$2.addChild(new FrameButton(this.minecraft.font, RealmsSelectFileToUploadScreen.TITLE, UPLOAD_LOCATION, $$0 -> this.minecraft.setScreen(new RealmsSelectFileToUploadScreen(this.realmCreationTask, this.serverData.id, this.slot, this))));
        $$2.addChild(new FrameButton(this.minecraft.font, WORLD_TEMPLATES_TITLE, SURVIVAL_SPAWN_LOCATION, $$0 -> this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(WORLD_TEMPLATES_TITLE, this::templateSelectionCallback, RealmsServer.WorldType.NORMAL, this.templates))));
        $$2.addChild(SpacerElement.height(16), 3);
        $$2.addChild(new FrameButton(this.minecraft.font, ADVENTURES_TITLE, ADVENTURE_MAP_LOCATION, $$0 -> this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(ADVENTURES_TITLE, this::templateSelectionCallback, RealmsServer.WorldType.ADVENTUREMAP, this.adventuremaps))));
        $$2.addChild(new FrameButton(this.minecraft.font, EXPERIENCES_TITLE, EXPERIENCE_LOCATION, $$0 -> this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(EXPERIENCES_TITLE, this::templateSelectionCallback, RealmsServer.WorldType.EXPERIENCE, this.experiences))));
        $$2.addChild(new FrameButton(this.minecraft.font, INSPIRATION_TITLE, INSPIRATION_LOCATION, $$0 -> this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(INSPIRATION_TITLE, this::templateSelectionCallback, RealmsServer.WorldType.INSPIRATION, this.inspirations))));
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).build());
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.a(this.getTitle(), this.subtitle);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private void templateSelectionCallback(@Nullable WorldTemplate $$0) {
        this.minecraft.setScreen(this);
        if ($$0 != null) {
            this.runResetTasks(new ResettingTemplateWorldTask($$0, this.serverData.id, this.resetTaskTitle, this.resetWorldRunnable));
        }
        RealmsMainScreen.refreshServerList();
    }

    private void runResetTasks(LongRunningTask $$0) {
        ArrayList<LongRunningTask> $$1 = new ArrayList<LongRunningTask>();
        if (this.realmCreationTask != null) {
            $$1.add(this.realmCreationTask);
        }
        if (this.slot != this.serverData.activeSlot) {
            $$1.add(new SwitchSlotTask(this.serverData.id, this.slot, () -> {}));
        }
        $$1.add($$0);
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, $$1.toArray(new LongRunningTask[0])));
    }

    class FrameButton
    extends Button {
        private static final ResourceLocation SLOT_FRAME_SPRITE = ResourceLocation.withDefaultNamespace("widget/slot_frame");
        private static final int FRAME_SIZE = 60;
        private static final int FRAME_WIDTH = 2;
        private static final int IMAGE_SIZE = 56;
        private final ResourceLocation image;

        FrameButton(Font $$0, Component $$1, ResourceLocation $$2, Button.OnPress $$3) {
            super(0, 0, 60, 60 + $$0.lineHeight, $$1, $$3, DEFAULT_NARRATION);
            this.image = $$2;
        }

        @Override
        public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
            boolean $$4 = this.isHoveredOrFocused();
            int $$5 = -1;
            if ($$4) {
                $$5 = ARGB.colorFromFloat(1.0f, 0.56f, 0.56f, 0.56f);
            }
            int $$6 = this.getX();
            int $$7 = this.getY();
            $$0.blit(RenderPipelines.GUI_TEXTURED, this.image, $$6 + 2, $$7 + 2, 0.0f, 0.0f, 56, 56, 56, 56, 56, 56, $$5);
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_SPRITE, $$6, $$7, 60, 60, $$5);
            int $$8 = $$4 ? -6250336 : -1;
            $$0.drawCenteredString(RealmsResetWorldScreen.this.font, this.getMessage(), $$6 + 28, $$7 - 14, $$8);
        }
    }
}

