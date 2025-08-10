/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.gui.screens.worldselection;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelSummary;
import org.slf4j.Logger;

public class SelectWorldScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final WorldOptions TEST_OPTIONS = new WorldOptions("test1".hashCode(), true, false);
    protected final Screen lastScreen;
    private Button deleteButton;
    private Button selectButton;
    private Button renameButton;
    private Button copyButton;
    protected EditBox searchBox;
    private WorldSelectionList list;

    public SelectWorldScreen(Screen $$0) {
        super(Component.translatable("selectWorld.title"));
        this.lastScreen = $$0;
    }

    @Override
    protected void init() {
        this.searchBox = new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, Component.translatable("selectWorld.search"));
        this.searchBox.setResponder($$0 -> this.list.updateFilter((String)$$0));
        this.addWidget(this.searchBox);
        this.list = this.addRenderableWidget(new WorldSelectionList(this, this.minecraft, this.width, this.height - 112, 48, 36, this.searchBox.getValue(), this.list));
        this.selectButton = this.addRenderableWidget(Button.builder(LevelSummary.PLAY_WORLD, $$0 -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::joinWorld)).bounds(this.width / 2 - 154, this.height - 52, 150, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.create"), $$0 -> CreateWorldScreen.openFresh(this.minecraft, this)).bounds(this.width / 2 + 4, this.height - 52, 150, 20).build());
        this.renameButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit"), $$0 -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::editWorld)).bounds(this.width / 2 - 154, this.height - 28, 72, 20).build());
        this.deleteButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.delete"), $$0 -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::deleteWorld)).bounds(this.width / 2 - 76, this.height - 28, 72, 20).build());
        this.copyButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.recreate"), $$0 -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::recreateWorld)).bounds(this.width / 2 + 4, this.height - 28, 72, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 + 82, this.height - 28, 72, 20).build());
        this.updateButtonStatus(null);
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.searchBox);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.searchBox.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 8, -1);
    }

    public void updateButtonStatus(@Nullable LevelSummary $$0) {
        if ($$0 == null) {
            this.selectButton.setMessage(LevelSummary.PLAY_WORLD);
            this.selectButton.active = false;
            this.renameButton.active = false;
            this.copyButton.active = false;
            this.deleteButton.active = false;
        } else {
            this.selectButton.setMessage($$0.primaryActionMessage());
            this.selectButton.active = $$0.primaryActionActive();
            this.renameButton.active = $$0.canEdit();
            this.copyButton.active = $$0.canRecreate();
            this.deleteButton.active = $$0.canDelete();
        }
    }

    @Override
    public void removed() {
        if (this.list != null) {
            this.list.children().forEach(WorldSelectionList.Entry::close);
        }
    }

    private /* synthetic */ void lambda$init$7(Button $$0) {
        try {
            WorldSelectionList.WorldListEntry $$3;
            WorldSelectionList.Entry $$2;
            String $$1 = "DEBUG world";
            if (!this.list.children().isEmpty() && ($$2 = (WorldSelectionList.Entry)this.list.children().get(0)) instanceof WorldSelectionList.WorldListEntry && ($$3 = (WorldSelectionList.WorldListEntry)$$2).getLevelName().equals("DEBUG world")) {
                $$3.doDeleteWorld();
            }
            LevelSettings $$4 = new LevelSettings("DEBUG world", GameType.SPECTATOR, false, Difficulty.NORMAL, true, new GameRules(WorldDataConfiguration.DEFAULT.enabledFeatures()), WorldDataConfiguration.DEFAULT);
            String $$5 = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), "DEBUG world", "");
            this.minecraft.createWorldOpenFlows().createFreshLevel($$5, $$4, TEST_OPTIONS, WorldPresets::createNormalWorldDimensions, this);
        } catch (IOException $$6) {
            LOGGER.error("Failed to recreate the debug world", $$6);
        }
    }
}

