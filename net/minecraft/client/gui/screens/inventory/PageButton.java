/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class PageButton
extends Button {
    private static final ResourceLocation PAGE_FORWARD_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/page_forward_highlighted");
    private static final ResourceLocation PAGE_FORWARD_SPRITE = ResourceLocation.withDefaultNamespace("widget/page_forward");
    private static final ResourceLocation PAGE_BACKWARD_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/page_backward_highlighted");
    private static final ResourceLocation PAGE_BACKWARD_SPRITE = ResourceLocation.withDefaultNamespace("widget/page_backward");
    private static final Component PAGE_BUTTON_NEXT = Component.translatable("book.page_button.next");
    private static final Component PAGE_BUTTON_PREVIOUS = Component.translatable("book.page_button.previous");
    private final boolean isForward;
    private final boolean playTurnSound;

    public PageButton(int $$0, int $$1, boolean $$2, Button.OnPress $$3, boolean $$4) {
        super($$0, $$1, 23, 13, $$2 ? PAGE_BUTTON_NEXT : PAGE_BUTTON_PREVIOUS, $$3, DEFAULT_NARRATION);
        this.isForward = $$2;
        this.playTurnSound = $$4;
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        ResourceLocation $$5;
        if (this.isForward) {
            ResourceLocation $$4 = this.isHoveredOrFocused() ? PAGE_FORWARD_HIGHLIGHTED_SPRITE : PAGE_FORWARD_SPRITE;
        } else {
            $$5 = this.isHoveredOrFocused() ? PAGE_BACKWARD_HIGHLIGHTED_SPRITE : PAGE_BACKWARD_SPRITE;
        }
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$5, this.getX(), this.getY(), 23, 13);
    }

    @Override
    public void playDownSound(SoundManager $$0) {
        if (this.playTurnSound) {
            $$0.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0f));
        }
    }
}

