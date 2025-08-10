/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui.screens.configuration;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen;
import com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigurationTab;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.configuration.RealmsSlotOptionsScreen;
import com.mojang.realmsclient.util.task.SwitchMinigameTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;

class RealmsWorldsTab
extends GridLayoutTab
implements RealmsConfigurationTab {
    static final Component TITLE = Component.translatable("mco.configure.worlds.title");
    private final RealmsConfigureWorldScreen configurationScreen;
    private final Minecraft minecraft;
    private RealmsServer serverData;
    private final Button optionsButton;
    private final Button backupButton;
    private final Button resetWorldButton;
    private final List<RealmsWorldSlotButton> slotButtonList = Lists.newArrayList();

    RealmsWorldsTab(RealmsConfigureWorldScreen $$02, Minecraft $$1, RealmsServer $$2) {
        super(TITLE);
        this.configurationScreen = $$02;
        this.minecraft = $$1;
        this.serverData = $$2;
        GridLayout.RowHelper $$32 = this.layout.spacing(20).createRowHelper(1);
        GridLayout.RowHelper $$4 = new GridLayout().spacing(16).createRowHelper(4);
        this.slotButtonList.clear();
        for (int $$5 = 1; $$5 < 5; ++$$5) {
            this.slotButtonList.add($$4.addChild(this.createSlotButton($$5), LayoutSettings.defaults().alignVerticallyBottom()));
        }
        $$32.addChild($$4.getGrid());
        GridLayout.RowHelper $$6 = new GridLayout().spacing(8).createRowHelper(1);
        this.optionsButton = $$6.addChild(Button.builder(Component.translatable("mco.configure.world.buttons.options"), $$3 -> $$1.setScreen(new RealmsSlotOptionsScreen($$02, $$2.slots.get($$2.activeSlot).clone(), $$2.worldType, $$2.activeSlot))).bounds(0, 0, 150, 20).build());
        this.backupButton = $$6.addChild(Button.builder(Component.translatable("mco.configure.world.backup"), $$3 -> $$1.setScreen(new RealmsBackupScreen($$02, $$2.clone(), $$2.activeSlot))).bounds(0, 0, 150, 20).build());
        this.resetWorldButton = $$6.addChild(Button.builder(Component.empty(), $$0 -> this.resetButtonPressed()).bounds(0, 0, 150, 20).build());
        $$32.addChild($$6.getGrid(), LayoutSettings.defaults().alignHorizontallyCenter());
        this.backupButton.active = true;
        this.updateData($$2);
    }

    private void resetButtonPressed() {
        if (this.isMinigame()) {
            this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(Component.translatable("mco.template.title.minigame"), this::templateSelectionCallback, RealmsServer.WorldType.MINIGAME));
        } else {
            this.minecraft.setScreen(RealmsResetWorldScreen.forResetSlot(this.configurationScreen, this.serverData.clone(), () -> this.minecraft.execute(() -> this.minecraft.setScreen(this.configurationScreen.getNewScreen()))));
        }
    }

    private void templateSelectionCallback(@Nullable WorldTemplate $$0) {
        if ($$0 != null && WorldTemplate.WorldTemplateType.MINIGAME == $$0.type) {
            this.configurationScreen.stateChanged();
            RealmsConfigureWorldScreen $$1 = this.configurationScreen.getNewScreen();
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen($$1, new SwitchMinigameTask(this.serverData.id, $$0, $$1)));
        } else {
            this.minecraft.setScreen(this.configurationScreen);
        }
    }

    private boolean isMinigame() {
        return this.serverData.isMinigameActive();
    }

    @Override
    public void onSelected(RealmsServer $$0) {
        this.updateData($$0);
    }

    @Override
    public void updateData(RealmsServer $$0) {
        this.serverData = $$0;
        this.optionsButton.active = !$$0.expired && !this.isMinigame();
        boolean bl = this.resetWorldButton.active = !$$0.expired;
        if (this.isMinigame()) {
            this.resetWorldButton.setMessage(Component.translatable("mco.configure.world.buttons.switchminigame"));
        } else {
            boolean $$1;
            boolean bl2 = $$1 = $$0.slots.containsKey($$0.activeSlot) && $$0.slots.get((Object)Integer.valueOf((int)$$0.activeSlot)).options.empty;
            if ($$1) {
                this.resetWorldButton.setMessage(Component.translatable("mco.configure.world.buttons.newworld"));
            } else {
                this.resetWorldButton.setMessage(Component.translatable("mco.configure.world.buttons.resetworld"));
            }
        }
        this.backupButton.active = !this.isMinigame();
        for (RealmsWorldSlotButton $$2 : this.slotButtonList) {
            RealmsWorldSlotButton.State $$3 = $$2.setServerData($$0);
            if ($$3.activeSlot) {
                $$2.setSize(80, 80);
                continue;
            }
            $$2.setSize(50, 50);
        }
    }

    private RealmsWorldSlotButton createSlotButton(int $$0) {
        return new RealmsWorldSlotButton(0, 0, 80, 80, $$0, this.serverData, $$1 -> {
            RealmsWorldSlotButton.State $$2 = ((RealmsWorldSlotButton)$$1).getState();
            switch ($$2.action) {
                case NOTHING: {
                    break;
                }
                case SWITCH_SLOT: {
                    if ($$2.minigame) {
                        this.switchToMinigame();
                        break;
                    }
                    if ($$2.empty) {
                        this.switchToEmptySlot($$0, this.serverData);
                        break;
                    }
                    this.switchToFullSlot($$0, this.serverData);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unknown action " + String.valueOf((Object)$$2.action));
                }
            }
        });
    }

    private void switchToMinigame() {
        RealmsSelectWorldTemplateScreen $$0 = new RealmsSelectWorldTemplateScreen(Component.translatable("mco.template.title.minigame"), this::templateSelectionCallback, RealmsServer.WorldType.MINIGAME);
        $$0.a(Component.translatable("mco.minigame.world.info.line1"), Component.translatable("mco.minigame.world.info.line2"));
        this.minecraft.setScreen($$0);
    }

    private void switchToFullSlot(int $$0, RealmsServer $$1) {
        this.minecraft.setScreen(RealmsPopups.infoPopupScreen(this.configurationScreen, Component.translatable("mco.configure.world.slot.switch.question.line1"), $$2 -> {
            RealmsConfigureWorldScreen $$3 = this.configurationScreen.getNewScreen();
            this.configurationScreen.stateChanged();
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen($$3, new SwitchSlotTask($$0.id, $$0, () -> this.minecraft.execute(() -> this.minecraft.setScreen($$3)))));
        }));
    }

    private void switchToEmptySlot(int $$0, RealmsServer $$1) {
        this.minecraft.setScreen(RealmsPopups.infoPopupScreen(this.configurationScreen, Component.translatable("mco.configure.world.slot.switch.question.line1"), $$2 -> {
            this.configurationScreen.stateChanged();
            RealmsResetWorldScreen $$3 = RealmsResetWorldScreen.forEmptySlot(this.configurationScreen, $$0, $$1, () -> this.minecraft.execute(() -> this.minecraft.setScreen(this.configurationScreen.getNewScreen())));
            this.minecraft.setScreen($$3);
        }));
    }
}

