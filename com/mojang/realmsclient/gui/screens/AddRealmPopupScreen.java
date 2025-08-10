/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui.screens;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.CommonLinks;

public class AddRealmPopupScreen
extends RealmsScreen {
    private static final Component POPUP_TEXT = Component.translatable("mco.selectServer.popup");
    private static final Component CLOSE_TEXT = Component.translatable("mco.selectServer.close");
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("popup/background");
    private static final ResourceLocation TRIAL_AVAILABLE_SPRITE = ResourceLocation.withDefaultNamespace("icon/trial_available");
    private static final WidgetSprites CROSS_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/cross_button"), ResourceLocation.withDefaultNamespace("widget/cross_button_highlighted"));
    private static final int IMAGE_WIDTH = 195;
    private static final int IMAGE_HEIGHT = 152;
    private static final int BG_BORDER_SIZE = 6;
    private static final int BUTTON_SPACING = 4;
    private static final int PADDING = 10;
    private static final int WIDTH = 320;
    private static final int HEIGHT = 172;
    private static final int TEXT_WIDTH = 100;
    private static final int BUTTON_WIDTH = 99;
    private static final int CAROUSEL_SWITCH_INTERVAL = 100;
    private static List<ResourceLocation> carouselImages = List.of();
    private final Screen backgroundScreen;
    private final boolean trialAvailable;
    @Nullable
    private Button createTrialButton;
    private int carouselIndex;
    private int carouselTick;

    public AddRealmPopupScreen(Screen $$0, boolean $$1) {
        super(POPUP_TEXT);
        this.backgroundScreen = $$0;
        this.trialAvailable = $$1;
    }

    public static void updateCarouselImages(ResourceManager $$02) {
        Set<ResourceLocation> $$1 = $$02.listResources("textures/gui/images", $$0 -> $$0.getPath().endsWith(".png")).keySet();
        carouselImages = $$1.stream().filter($$0 -> $$0.getNamespace().equals("realms")).toList();
    }

    @Override
    protected void init() {
        this.backgroundScreen.resize(this.minecraft, this.width, this.height);
        if (this.trialAvailable) {
            this.createTrialButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.selectServer.trial"), ConfirmLinkScreen.confirmLink((Screen)this, CommonLinks.START_REALMS_TRIAL)).bounds(this.right() - 10 - 99, this.bottom() - 10 - 4 - 40, 99, 20).build());
        }
        this.addRenderableWidget(Button.builder(Component.translatable("mco.selectServer.buy"), ConfirmLinkScreen.confirmLink((Screen)this, CommonLinks.BUY_REALMS)).bounds(this.right() - 10 - 99, this.bottom() - 10 - 20, 99, 20).build());
        ImageButton $$02 = this.addRenderableWidget(new ImageButton(this.left() + 4, this.top() + 4, 14, 14, CROSS_BUTTON_SPRITES, $$0 -> this.onClose(), CLOSE_TEXT));
        $$02.setTooltip(Tooltip.create(CLOSE_TEXT));
        int $$1 = 142 - (this.trialAvailable ? 40 : 20);
        FittingMultiLineTextWidget $$2 = new FittingMultiLineTextWidget(this.right() - 10 - 100, this.top() + 10, 100, $$1, POPUP_TEXT, this.font);
        if ($$2.showingScrollBar()) {
            $$2.setWidth(94);
        }
        this.addRenderableWidget($$2);
    }

    @Override
    public void tick() {
        super.tick();
        if (++this.carouselTick > 100) {
            this.carouselTick = 0;
            this.carouselIndex = (this.carouselIndex + 1) % carouselImages.size();
        }
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        if (this.createTrialButton != null) {
            AddRealmPopupScreen.renderDiamond($$0, this.createTrialButton);
        }
    }

    public static void renderDiamond(GuiGraphics $$0, Button $$1) {
        int $$2 = 8;
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, TRIAL_AVAILABLE_SPRITE, $$1.getX() + $$1.getWidth() - 8 - 4, $$1.getY() + $$1.getHeight() / 2 - 4, 8, 8);
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.backgroundScreen.renderBackground($$0, -1, -1, $$3);
        $$0.nextStratum();
        this.backgroundScreen.render($$0, -1, -1, $$3);
        $$0.nextStratum();
        this.renderTransparentBackground($$0);
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, this.left(), this.top(), 320, 172);
        if (!carouselImages.isEmpty()) {
            $$0.blit(RenderPipelines.GUI_TEXTURED, carouselImages.get(this.carouselIndex), this.left() + 10, this.top() + 10, 0.0f, 0.0f, 195, 152, 195, 152);
        }
    }

    private int left() {
        return (this.width - 320) / 2;
    }

    private int top() {
        return (this.height - 172) / 2;
    }

    private int right() {
        return this.left() + 320;
    }

    private int bottom() {
        return this.top() + 172;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.backgroundScreen);
    }
}

