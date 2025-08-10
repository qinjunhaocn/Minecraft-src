/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.text2speech.Narrator
 */
package net.minecraft.client.gui.screens;

import com.mojang.text2speech.Narrator;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommonButtons;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class AccessibilityOnboardingScreen
extends Screen {
    private static final Component TITLE = Component.translatable("accessibility.onboarding.screen.title");
    private static final Component ONBOARDING_NARRATOR_MESSAGE = Component.translatable("accessibility.onboarding.screen.narrator");
    private static final int PADDING = 4;
    private static final int TITLE_PADDING = 16;
    private static final float FADE_OUT_TIME = 1000.0f;
    private final LogoRenderer logoRenderer;
    private final Options options;
    private final boolean narratorAvailable;
    private boolean hasNarrated;
    private float timer;
    private final Runnable onClose;
    @Nullable
    private FocusableTextWidget textWidget;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, this.initTitleYPos(), 33);
    private float fadeInStart;
    private boolean fadingIn = true;
    private float fadeOutStart;

    public AccessibilityOnboardingScreen(Options $$0, Runnable $$1) {
        super(TITLE);
        this.options = $$0;
        this.onClose = $$1;
        this.logoRenderer = new LogoRenderer(true);
        this.narratorAvailable = Minecraft.getInstance().getNarrator().isActive();
    }

    @Override
    public void init() {
        LinearLayout $$02 = this.layout.addToContents(LinearLayout.vertical());
        $$02.defaultCellSetting().alignHorizontallyCenter().padding(4);
        this.textWidget = $$02.addChild(new FocusableTextWidget(this.width, this.title, this.font), $$0 -> $$0.padding(8));
        AbstractWidget abstractWidget = this.options.narrator().createButton(this.options);
        if (abstractWidget instanceof CycleButton) {
            CycleButton $$1;
            this.narratorButton = $$1 = (CycleButton)abstractWidget;
            this.narratorButton.active = this.narratorAvailable;
            $$02.addChild(this.narratorButton);
        }
        $$02.addChild(CommonButtons.accessibility(150, $$0 -> this.closeAndSetScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), false));
        $$02.addChild(CommonButtons.language(150, $$0 -> this.closeAndSetScreen(new LanguageSelectScreen((Screen)this, this.minecraft.options, this.minecraft.getLanguageManager())), false));
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_CONTINUE, $$0 -> this.onClose()).build());
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (this.textWidget != null) {
            this.textWidget.containWithin(this.width);
        }
        this.layout.arrangeElements();
    }

    @Override
    protected void setInitialFocus() {
        if (this.narratorAvailable && this.narratorButton != null) {
            this.setInitialFocus(this.narratorButton);
        } else {
            super.setInitialFocus();
        }
    }

    private int initTitleYPos() {
        return 90;
    }

    @Override
    public void onClose() {
        this.fadeOutStart = Util.getMillis();
    }

    private void closeAndSetScreen(Screen $$0) {
        this.close(false, () -> this.minecraft.setScreen($$0));
    }

    private void close(boolean $$0, Runnable $$1) {
        if ($$0) {
            this.options.onboardingAccessibilityFinished();
        }
        Narrator.getNarrator().clear();
        $$1.run();
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.handleInitialNarrationDelay();
        if (this.fadeInStart == 0.0f && this.fadingIn) {
            this.fadeInStart = Util.getMillis();
        }
        if (this.fadeInStart > 0.0f) {
            float $$4 = ((float)Util.getMillis() - this.fadeInStart) / 2000.0f;
            float $$5 = 1.0f;
            if ($$4 >= 1.0f) {
                this.fadingIn = false;
                this.fadeInStart = 0.0f;
            } else {
                $$4 = Mth.clamp($$4, 0.0f, 1.0f);
                $$5 = Mth.clampedMap($$4, 0.5f, 1.0f, 0.0f, 1.0f);
            }
            this.fadeWidgets($$5);
        }
        if (this.fadeOutStart > 0.0f) {
            float $$6 = 1.0f - ((float)Util.getMillis() - this.fadeOutStart) / 1000.0f;
            float $$7 = 0.0f;
            if ($$6 <= 0.0f) {
                this.fadeOutStart = 0.0f;
                this.close(true, this.onClose);
            } else {
                $$6 = Mth.clamp($$6, 0.0f, 1.0f);
                $$7 = Mth.clampedMap($$6, 0.5f, 1.0f, 0.0f, 1.0f);
            }
            this.fadeWidgets($$7);
        }
        this.logoRenderer.renderLogo($$0, this.width, 1.0f);
    }

    @Override
    protected void renderPanorama(GuiGraphics $$0, float $$1) {
        this.minecraft.gameRenderer.getPanorama().render($$0, this.width, this.height, false);
    }

    private void handleInitialNarrationDelay() {
        if (!this.hasNarrated && this.narratorAvailable) {
            if (this.timer < 40.0f) {
                this.timer += 1.0f;
            } else if (this.minecraft.isWindowActive()) {
                Narrator.getNarrator().say(ONBOARDING_NARRATOR_MESSAGE.getString(), true, 1.0f);
                this.hasNarrated = true;
            }
        }
    }
}

