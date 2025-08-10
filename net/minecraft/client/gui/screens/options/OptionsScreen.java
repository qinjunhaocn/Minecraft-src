/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.options;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.ChatOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OnlineOptionsScreen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.telemetry.TelemetryInfoScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.Difficulty;

public class OptionsScreen
extends Screen {
    private static final Component TITLE = Component.translatable("options.title");
    private static final Component SKIN_CUSTOMIZATION = Component.translatable("options.skinCustomisation");
    private static final Component SOUNDS = Component.translatable("options.sounds");
    private static final Component VIDEO = Component.translatable("options.video");
    private static final Component CONTROLS = Component.translatable("options.controls");
    private static final Component LANGUAGE = Component.translatable("options.language");
    private static final Component CHAT = Component.translatable("options.chat");
    private static final Component RESOURCEPACK = Component.translatable("options.resourcepack");
    private static final Component ACCESSIBILITY = Component.translatable("options.accessibility");
    private static final Component TELEMETRY = Component.translatable("options.telemetry");
    private static final Tooltip TELEMETRY_DISABLED_TOOLTIP = Tooltip.create(Component.translatable("options.telemetry.disabled"));
    private static final Component CREDITS_AND_ATTRIBUTION = Component.translatable("options.credits_and_attribution");
    private static final int COLUMNS = 2;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 61, 33);
    private final Screen lastScreen;
    private final Options options;
    @Nullable
    private CycleButton<Difficulty> difficultyButton;
    @Nullable
    private LockIconButton lockButton;

    public OptionsScreen(Screen $$0, Options $$1) {
        super(TITLE);
        this.lastScreen = $$0;
        this.options = $$1;
    }

    @Override
    protected void init() {
        LinearLayout $$02 = this.layout.addToHeader(LinearLayout.vertical().spacing(8));
        $$02.addChild(new StringWidget(TITLE, this.font), LayoutSettings::alignHorizontallyCenter);
        LinearLayout $$12 = $$02.addChild(LinearLayout.horizontal()).spacing(8);
        $$12.addChild(this.options.fov().createButton(this.minecraft.options));
        $$12.addChild(this.createOnlineButton());
        GridLayout $$2 = new GridLayout();
        $$2.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper $$3 = $$2.createRowHelper(2);
        $$3.addChild(this.openScreenButton(SKIN_CUSTOMIZATION, () -> new SkinCustomizationScreen(this, this.options)));
        $$3.addChild(this.openScreenButton(SOUNDS, () -> new SoundOptionsScreen(this, this.options)));
        $$3.addChild(this.openScreenButton(VIDEO, () -> new VideoSettingsScreen((Screen)this, this.minecraft, this.options)));
        $$3.addChild(this.openScreenButton(CONTROLS, () -> new ControlsScreen(this, this.options)));
        $$3.addChild(this.openScreenButton(LANGUAGE, () -> new LanguageSelectScreen((Screen)this, this.options, this.minecraft.getLanguageManager())));
        $$3.addChild(this.openScreenButton(CHAT, () -> new ChatOptionsScreen(this, this.options)));
        $$3.addChild(this.openScreenButton(RESOURCEPACK, () -> new PackSelectionScreen(this.minecraft.getResourcePackRepository(), this::applyPacks, this.minecraft.getResourcePackDirectory(), Component.translatable("resourcePack.title"))));
        $$3.addChild(this.openScreenButton(ACCESSIBILITY, () -> new AccessibilityOptionsScreen(this, this.options)));
        Button $$4 = $$3.addChild(this.openScreenButton(TELEMETRY, () -> new TelemetryInfoScreen(this, this.options)));
        if (!this.minecraft.allowsTelemetry()) {
            $$4.active = false;
            $$4.setTooltip(TELEMETRY_DISABLED_TOOLTIP);
        }
        $$3.addChild(this.openScreenButton(CREDITS_AND_ATTRIBUTION, () -> new CreditsAndAttributionScreen(this)));
        this.layout.addToContents($$2);
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onClose()).width(200).build());
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
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private void applyPacks(PackRepository $$0) {
        this.options.updateResourcePacks($$0);
        this.minecraft.setScreen(this);
    }

    private LayoutElement createOnlineButton() {
        if (this.minecraft.level != null && this.minecraft.hasSingleplayerServer()) {
            this.difficultyButton = OptionsScreen.createDifficultyButton(0, 0, "options.difficulty", this.minecraft);
            if (!this.minecraft.level.getLevelData().isHardcore()) {
                this.lockButton = new LockIconButton(0, 0, $$0 -> this.minecraft.setScreen(new ConfirmScreen(this::lockCallback, Component.translatable("difficulty.lock.title"), Component.a("difficulty.lock.question", this.minecraft.level.getLevelData().getDifficulty().getDisplayName()))));
                this.difficultyButton.setWidth(this.difficultyButton.getWidth() - this.lockButton.getWidth());
                this.lockButton.setLocked(this.minecraft.level.getLevelData().isDifficultyLocked());
                this.lockButton.active = !this.lockButton.isLocked();
                this.difficultyButton.active = !this.lockButton.isLocked();
                EqualSpacingLayout $$02 = new EqualSpacingLayout(150, 0, EqualSpacingLayout.Orientation.HORIZONTAL);
                $$02.addChild(this.difficultyButton);
                $$02.addChild(this.lockButton);
                return $$02;
            }
            this.difficultyButton.active = false;
            return this.difficultyButton;
        }
        return Button.builder(Component.translatable("options.online"), $$0 -> this.minecraft.setScreen(new OnlineOptionsScreen(this, this.options))).bounds(this.width / 2 + 5, this.height / 6 - 12 + 24, 150, 20).build();
    }

    public static CycleButton<Difficulty> createDifficultyButton(int $$0, int $$12, String $$22, Minecraft $$3) {
        return CycleButton.builder(Difficulty::getDisplayName).a((Difficulty[])Difficulty.values()).withInitialValue($$3.level.getDifficulty()).create($$0, $$12, 150, 20, Component.translatable($$22), ($$1, $$2) -> $$3.getConnection().send(new ServerboundChangeDifficultyPacket((Difficulty)$$2)));
    }

    private void lockCallback(boolean $$0) {
        this.minecraft.setScreen(this);
        if ($$0 && this.minecraft.level != null && this.lockButton != null && this.difficultyButton != null) {
            this.minecraft.getConnection().send(new ServerboundLockDifficultyPacket(true));
            this.lockButton.setLocked(true);
            this.lockButton.active = false;
            this.difficultyButton.active = false;
        }
    }

    @Override
    public void removed() {
        this.options.save();
    }

    private Button openScreenButton(Component $$0, Supplier<Screen> $$12) {
        return Button.builder($$0, $$1 -> this.minecraft.setScreen((Screen)$$12.get())).build();
    }
}

