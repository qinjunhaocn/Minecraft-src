/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TabButton
extends AbstractWidget {
    private static final WidgetSprites SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/tab_selected"), ResourceLocation.withDefaultNamespace("widget/tab"), ResourceLocation.withDefaultNamespace("widget/tab_selected_highlighted"), ResourceLocation.withDefaultNamespace("widget/tab_highlighted"));
    private static final int SELECTED_OFFSET = 3;
    private static final int TEXT_MARGIN = 1;
    private static final int UNDERLINE_HEIGHT = 1;
    private static final int UNDERLINE_MARGIN_X = 4;
    private static final int UNDERLINE_MARGIN_BOTTOM = 2;
    private final TabManager tabManager;
    private final Tab tab;

    public TabButton(TabManager $$0, Tab $$1, int $$2, int $$3) {
        super(0, 0, $$2, $$3, $$1.getTabTitle());
        this.tabManager = $$0;
        this.tab = $$1;
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        int $$5;
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(this.isSelected(), this.isHoveredOrFocused()), this.getX(), this.getY(), this.width, this.height);
        Font $$4 = Minecraft.getInstance().font;
        int n = $$5 = this.active ? -1 : -6250336;
        if (this.isSelected()) {
            this.renderMenuBackground($$0, this.getX() + 2, this.getY() + 2, this.getRight() - 2, this.getBottom());
            this.renderFocusUnderline($$0, $$4, $$5);
        }
        this.renderString($$0, $$4, $$5);
    }

    protected void renderMenuBackground(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4) {
        Screen.renderMenuBackgroundTexture($$0, Screen.MENU_BACKGROUND, $$1, $$2, 0.0f, 0.0f, $$3 - $$1, $$4 - $$2);
    }

    public void renderString(GuiGraphics $$0, Font $$1, int $$2) {
        int $$3 = this.getX() + 1;
        int $$4 = this.getY() + (this.isSelected() ? 0 : 3);
        int $$5 = this.getX() + this.getWidth() - 1;
        int $$6 = this.getY() + this.getHeight();
        TabButton.renderScrollingString($$0, $$1, this.getMessage(), $$3, $$4, $$5, $$6, $$2);
    }

    private void renderFocusUnderline(GuiGraphics $$0, Font $$1, int $$2) {
        int $$3 = Math.min($$1.width(this.getMessage()), this.getWidth() - 4);
        int $$4 = this.getX() + (this.getWidth() - $$3) / 2;
        int $$5 = this.getY() + this.getHeight() - 2;
        $$0.fill($$4, $$5, $$4 + $$3, $$5 + 1, $$2);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, Component.a("gui.narrate.tab", this.tab.getTabTitle()));
        $$0.add(NarratedElementType.HINT, this.tab.getTabExtraNarration());
    }

    @Override
    public void playDownSound(SoundManager $$0) {
    }

    public Tab tab() {
        return this.tab;
    }

    public boolean isSelected() {
        return this.tabManager.getCurrentTab() == this.tab;
    }
}

