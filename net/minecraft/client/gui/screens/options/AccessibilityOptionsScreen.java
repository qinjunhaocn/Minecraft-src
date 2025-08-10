/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.options;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;
import net.minecraft.world.flag.FeatureFlags;

public class AccessibilityOptionsScreen
extends OptionsSubScreen {
    public static final Component TITLE = Component.translatable("options.accessibility.title");

    private static OptionInstance<?>[] a(Options $$0) {
        return new OptionInstance[]{$$0.narrator(), $$0.showSubtitles(), $$0.highContrast(), $$0.autoJump(), $$0.menuBackgroundBlurriness(), $$0.textBackgroundOpacity(), $$0.backgroundForChatOnly(), $$0.chatOpacity(), $$0.chatLineSpacing(), $$0.chatDelay(), $$0.notificationDisplayTime(), $$0.bobView(), $$0.toggleCrouch(), $$0.toggleSprint(), $$0.screenEffectScale(), $$0.fovEffectScale(), $$0.darknessEffectScale(), $$0.damageTiltStrength(), $$0.glintSpeed(), $$0.glintStrength(), $$0.hideLightningFlash(), $$0.darkMojangStudiosBackground(), $$0.panoramaSpeed(), $$0.hideSplashTexts(), $$0.narratorHotkey(), $$0.rotateWithMinecart(), $$0.highContrastBlockOutline()};
    }

    public AccessibilityOptionsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, TITLE);
    }

    @Override
    protected void init() {
        AbstractWidget $$1;
        super.init();
        AbstractWidget $$0 = this.list.findOption(this.options.highContrast());
        if ($$0 != null && !this.minecraft.getResourcePackRepository().getAvailableIds().contains("high_contrast")) {
            $$0.active = false;
            $$0.setTooltip(Tooltip.create(Component.translatable("options.accessibility.high_contrast.error.tooltip")));
        }
        if (($$1 = this.list.findOption(this.options.rotateWithMinecart())) != null) {
            $$1.active = this.isMinecartOptionEnabled();
        }
    }

    @Override
    protected void addOptions() {
        this.list.a(AccessibilityOptionsScreen.a(this.options));
    }

    @Override
    protected void addFooter() {
        LinearLayout $$02 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        $$02.addChild(Button.builder(Component.translatable("options.accessibility.link"), ConfirmLinkScreen.confirmLink((Screen)this, CommonLinks.ACCESSIBILITY_HELP)).build());
        $$02.addChild(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.minecraft.setScreen(this.lastScreen)).build());
    }

    private boolean isMinecartOptionEnabled() {
        return this.minecraft.level != null && this.minecraft.level.enabledFeatures().contains(FeatureFlags.MINECART_IMPROVEMENTS);
    }
}

