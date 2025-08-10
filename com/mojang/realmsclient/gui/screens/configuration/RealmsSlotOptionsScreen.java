/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui.screens.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsSlot;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigureWorldScreen;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;

public class RealmsSlotOptionsScreen
extends RealmsScreen {
    private static final int DEFAULT_DIFFICULTY = 2;
    public static final List<Difficulty> DIFFICULTIES = ImmutableList.of(Difficulty.PEACEFUL, Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD);
    private static final int DEFAULT_GAME_MODE = 0;
    public static final List<GameType> GAME_MODES = ImmutableList.of(GameType.SURVIVAL, GameType.CREATIVE, GameType.ADVENTURE);
    private static final Component NAME_LABEL = Component.translatable("mco.configure.world.edit.slot.name");
    static final Component SPAWN_PROTECTION_TEXT = Component.translatable("mco.configure.world.spawnProtection");
    private EditBox nameEdit;
    protected final RealmsConfigureWorldScreen parentScreen;
    private int column1X;
    private int columnWidth;
    private final RealmsSlot slot;
    private final RealmsServer.WorldType worldType;
    private Difficulty difficulty;
    private GameType gameMode;
    private final String defaultSlotName;
    private String worldName;
    private boolean pvp;
    private boolean spawnMonsters;
    int spawnProtection;
    private boolean commandBlocks;
    private boolean forceGameMode;
    SettingsSlider spawnProtectionButton;

    public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen $$0, RealmsSlot $$1, RealmsServer.WorldType $$2, int $$3) {
        super(Component.translatable("mco.configure.world.buttons.options"));
        this.parentScreen = $$0;
        this.slot = $$1;
        this.worldType = $$2;
        this.difficulty = RealmsSlotOptionsScreen.findByIndex(DIFFICULTIES, $$1.options.difficulty, 2);
        this.gameMode = RealmsSlotOptionsScreen.findByIndex(GAME_MODES, $$1.options.gameMode, 0);
        this.defaultSlotName = $$1.options.getDefaultSlotName($$3);
        this.setWorldName($$1.options.getSlotName($$3));
        if ($$2 == RealmsServer.WorldType.NORMAL) {
            this.pvp = $$1.options.pvp;
            this.spawnProtection = $$1.options.spawnProtection;
            this.forceGameMode = $$1.options.forceGameMode;
            this.spawnMonsters = $$1.options.spawnMonsters;
            this.commandBlocks = $$1.options.commandBlocks;
        } else {
            this.pvp = true;
            this.spawnProtection = 0;
            this.forceGameMode = false;
            this.spawnMonsters = true;
            this.commandBlocks = true;
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parentScreen);
    }

    private static <T> T findByIndex(List<T> $$0, int $$1, int $$2) {
        try {
            return $$0.get($$1);
        } catch (IndexOutOfBoundsException $$3) {
            return $$0.get($$2);
        }
    }

    private static <T> int findIndex(List<T> $$0, T $$1, int $$2) {
        int $$3 = $$0.indexOf($$1);
        return $$3 == -1 ? $$2 : $$3;
    }

    @Override
    public void init() {
        this.columnWidth = 170;
        this.column1X = this.width / 2 - this.columnWidth;
        int $$02 = this.width / 2 + 10;
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            MutableComponent $$3;
            if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP) {
                MutableComponent $$12 = Component.translatable("mco.configure.world.edit.subscreen.adventuremap");
            } else if (this.worldType == RealmsServer.WorldType.INSPIRATION) {
                MutableComponent $$22 = Component.translatable("mco.configure.world.edit.subscreen.inspiration");
            } else {
                $$3 = Component.translatable("mco.configure.world.edit.subscreen.experience");
            }
            this.addLabel(new RealmsLabel($$3, this.width / 2, 26, -65536));
        }
        this.nameEdit = this.addWidget(new EditBox(this.minecraft.font, this.column1X, RealmsSlotOptionsScreen.row(1), this.columnWidth, 20, null, Component.translatable("mco.configure.world.edit.slot.name")));
        this.nameEdit.setValue(this.worldName);
        this.nameEdit.setResponder(this::setWorldName);
        CycleButton<Boolean> $$4 = this.addRenderableWidget(CycleButton.onOffBuilder(this.pvp).create($$02, RealmsSlotOptionsScreen.row(1), this.columnWidth, 20, Component.translatable("mco.configure.world.pvp"), ($$0, $$1) -> {
            this.pvp = $$1;
        }));
        CycleButton<GameType> $$5 = this.addRenderableWidget(CycleButton.builder(GameType::getShortDisplayName).withValues(GAME_MODES).withInitialValue(this.gameMode).create(this.column1X, RealmsSlotOptionsScreen.row(3), this.columnWidth, 20, Component.translatable("selectWorld.gameMode"), ($$0, $$1) -> {
            this.gameMode = $$1;
        }));
        this.spawnProtectionButton = this.addRenderableWidget(new SettingsSlider($$02, RealmsSlotOptionsScreen.row(3), this.columnWidth, this.spawnProtection, 0.0f, 16.0f));
        MutableComponent $$6 = Component.translatable("mco.configure.world.spawn_toggle.message");
        CycleButton<Boolean> $$7 = CycleButton.onOffBuilder(this.difficulty != Difficulty.PEACEFUL && this.spawnMonsters).create($$02, RealmsSlotOptionsScreen.row(5), this.columnWidth, 20, Component.translatable("mco.configure.world.spawnMonsters"), this.confirmDangerousOption($$6, $$0 -> {
            this.spawnMonsters = $$0;
        }));
        CycleButton<Difficulty> $$8 = this.addRenderableWidget(CycleButton.builder(Difficulty::getDisplayName).withValues(DIFFICULTIES).withInitialValue(this.difficulty).create(this.column1X, RealmsSlotOptionsScreen.row(5), this.columnWidth, 20, Component.translatable("options.difficulty"), ($$1, $$2) -> {
            this.difficulty = $$2;
            if (this.worldType == RealmsServer.WorldType.NORMAL) {
                boolean $$3;
                $$0.active = $$3 = this.difficulty != Difficulty.PEACEFUL;
                $$7.setValue($$3 && this.spawnMonsters);
            }
        }));
        this.addRenderableWidget($$7);
        CycleButton<Boolean> $$9 = this.addRenderableWidget(CycleButton.onOffBuilder(this.forceGameMode).create(this.column1X, RealmsSlotOptionsScreen.row(7), this.columnWidth, 20, Component.translatable("mco.configure.world.forceGameMode"), ($$0, $$1) -> {
            this.forceGameMode = $$1;
        }));
        CycleButton<Boolean> $$10 = this.addRenderableWidget(CycleButton.onOffBuilder(this.commandBlocks).create($$02, RealmsSlotOptionsScreen.row(7), this.columnWidth, 20, Component.translatable("mco.configure.world.commandBlocks"), ($$0, $$1) -> {
            this.commandBlocks = $$1;
        }));
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            $$4.active = false;
            $$7.active = false;
            this.spawnProtectionButton.active = false;
            $$9.active = false;
        }
        if (this.difficulty == Difficulty.PEACEFUL) {
            $$7.active = false;
        }
        if (this.slot.isHardcore()) {
            $$10.active = false;
            $$8.active = false;
            $$5.active = false;
            $$7.active = false;
            $$9.active = false;
        }
        this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.buttons.done"), $$0 -> this.saveSettings()).bounds(this.column1X, RealmsSlotOptionsScreen.row(13), this.columnWidth, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onClose()).bounds($$02, RealmsSlotOptionsScreen.row(13), this.columnWidth, 20).build());
    }

    private CycleButton.OnValueChange<Boolean> confirmDangerousOption(Component $$0, Consumer<Boolean> $$1) {
        return ($$2, $$3) -> {
            if ($$3.booleanValue()) {
                $$1.accept(true);
            } else {
                this.minecraft.setScreen(RealmsPopups.warningPopupScreen(this, $$0, $$1 -> {
                    $$1.accept(false);
                    $$1.onClose();
                }));
            }
        };
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.a(this.getTitle(), this.createLabelNarration());
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 17, -1);
        $$0.drawString(this.font, NAME_LABEL, this.column1X + this.columnWidth / 2 - this.font.width(NAME_LABEL) / 2, RealmsSlotOptionsScreen.row(0) - 5, -1);
        this.nameEdit.render($$0, $$1, $$2, $$3);
    }

    private void setWorldName(String $$0) {
        this.worldName = $$0.equals(this.defaultSlotName) ? "" : $$0;
    }

    private void saveSettings() {
        int $$0 = RealmsSlotOptionsScreen.findIndex(DIFFICULTIES, this.difficulty, 2);
        int $$1 = RealmsSlotOptionsScreen.findIndex(GAME_MODES, this.gameMode, 0);
        if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP || this.worldType == RealmsServer.WorldType.EXPERIENCE || this.worldType == RealmsServer.WorldType.INSPIRATION) {
            this.parentScreen.saveSlotSettings(new RealmsSlot(this.slot.slotId, new RealmsWorldOptions(this.slot.options.pvp, this.slot.options.spawnMonsters, this.slot.options.spawnProtection, this.slot.options.commandBlocks, $$0, $$1, this.slot.options.forceGameMode, this.worldName, this.slot.options.version, this.slot.options.compatibility), this.slot.settings));
        } else {
            boolean $$2 = this.worldType == RealmsServer.WorldType.NORMAL && this.difficulty != Difficulty.PEACEFUL && this.spawnMonsters;
            this.parentScreen.saveSlotSettings(new RealmsSlot(this.slot.slotId, new RealmsWorldOptions(this.pvp, $$2, this.spawnProtection, this.commandBlocks, $$0, $$1, this.forceGameMode, this.worldName, this.slot.options.version, this.slot.options.compatibility), this.slot.settings));
        }
    }

    class SettingsSlider
    extends AbstractSliderButton {
        private final double minValue;
        private final double maxValue;

        public SettingsSlider(int $$0, int $$1, int $$2, int $$3, float $$4, float $$5) {
            super($$0, $$1, $$2, 20, CommonComponents.EMPTY, 0.0);
            this.minValue = $$4;
            this.maxValue = $$5;
            this.value = (Mth.clamp((float)$$3, $$4, $$5) - $$4) / ($$5 - $$4);
            this.updateMessage();
        }

        @Override
        public void applyValue() {
            if (!RealmsSlotOptionsScreen.this.spawnProtectionButton.active) {
                return;
            }
            RealmsSlotOptionsScreen.this.spawnProtection = (int)Mth.lerp(Mth.clamp(this.value, 0.0, 1.0), this.minValue, this.maxValue);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(CommonComponents.optionNameValue(SPAWN_PROTECTION_TEXT, RealmsSlotOptionsScreen.this.spawnProtection == 0 ? CommonComponents.OPTION_OFF : Component.literal(String.valueOf(RealmsSlotOptionsScreen.this.spawnProtection))));
        }
    }
}

