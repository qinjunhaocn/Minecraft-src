/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import net.minecraft.Util;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonLinks;

public class DemoIntroScreen
extends Screen {
    private static final ResourceLocation DEMO_BACKGROUND_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/demo_background.png");
    private static final int BACKGROUND_TEXTURE_WIDTH = 256;
    private static final int BACKGROUND_TEXTURE_HEIGHT = 256;
    private MultiLineLabel movementMessage = MultiLineLabel.EMPTY;
    private MultiLineLabel durationMessage = MultiLineLabel.EMPTY;

    public DemoIntroScreen() {
        super(Component.translatable("demo.help.title"));
    }

    @Override
    protected void init() {
        int $$02 = -16;
        this.addRenderableWidget(Button.builder(Component.translatable("demo.help.buy"), $$0 -> {
            $$0.active = false;
            Util.getPlatform().openUri(CommonLinks.BUY_MINECRAFT_JAVA);
        }).bounds(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("demo.help.later"), $$0 -> {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
        }).bounds(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20).build());
        Options $$1 = this.minecraft.options;
        this.movementMessage = MultiLineLabel.a(this.font, Component.a("demo.help.movementShort", $$1.keyUp.getTranslatedKeyMessage(), $$1.keyLeft.getTranslatedKeyMessage(), $$1.keyDown.getTranslatedKeyMessage(), $$1.keyRight.getTranslatedKeyMessage()), Component.translatable("demo.help.movementMouse"), Component.a("demo.help.jump", $$1.keyJump.getTranslatedKeyMessage()), Component.a("demo.help.inventory", $$1.keyInventory.getTranslatedKeyMessage()));
        this.durationMessage = MultiLineLabel.create(this.font, Component.translatable("demo.help.fullWrapped"), 218);
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.renderBackground($$0, $$1, $$2, $$3);
        int $$4 = (this.width - 248) / 2;
        int $$5 = (this.height - 166) / 2;
        $$0.blit(RenderPipelines.GUI_TEXTURED, DEMO_BACKGROUND_LOCATION, $$4, $$5, 0.0f, 0.0f, 248, 166, 256, 256);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        int $$4 = (this.width - 248) / 2 + 10;
        int $$5 = (this.height - 166) / 2 + 8;
        $$0.drawString(this.font, this.title, $$4, $$5, -14737633, false);
        $$5 = this.movementMessage.renderLeftAlignedNoShadow($$0, $$4, $$5 + 12, 12, -11579569);
        this.durationMessage.renderLeftAlignedNoShadow($$0, $$4, $$5 + 20, this.font.lineHeight, -14737633);
    }
}

