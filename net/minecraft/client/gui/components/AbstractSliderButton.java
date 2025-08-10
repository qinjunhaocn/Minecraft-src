/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public abstract class AbstractSliderButton
extends AbstractWidget {
    private static final ResourceLocation SLIDER_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider");
    private static final ResourceLocation HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_highlighted");
    private static final ResourceLocation SLIDER_HANDLE_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_handle");
    private static final ResourceLocation SLIDER_HANDLE_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_handle_highlighted");
    protected static final int TEXT_MARGIN = 2;
    public static final int DEFAULT_HEIGHT = 20;
    private static final int HANDLE_WIDTH = 8;
    private static final int HANDLE_HALF_WIDTH = 4;
    protected double value;
    private boolean canChangeValue;

    public AbstractSliderButton(int $$0, int $$1, int $$2, int $$3, Component $$4, double $$5) {
        super($$0, $$1, $$2, $$3, $$4);
        this.value = $$5;
    }

    private ResourceLocation getSprite() {
        if (this.isActive() && this.isFocused() && !this.canChangeValue) {
            return HIGHLIGHTED_SPRITE;
        }
        return SLIDER_SPRITE;
    }

    private ResourceLocation getHandleSprite() {
        if (this.isActive() && (this.isHovered || this.canChangeValue)) {
            return SLIDER_HANDLE_HIGHLIGHTED_SPRITE;
        }
        return SLIDER_HANDLE_SPRITE;
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return Component.a("gui.narrate.slider", this.getMessage());
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                $$0.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.focused"));
            } else {
                $$0.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.hovered"));
            }
        }
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        Minecraft $$4 = Minecraft.getInstance();
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.getSprite(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.getHandleSprite(), this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, this.getHeight(), ARGB.white(this.alpha));
        int $$5 = ARGB.color(this.alpha, this.active ? -1 : -6250336);
        this.renderScrollingString($$0, $$4.font, 2, $$5);
    }

    @Override
    public void onClick(double $$0, double $$1) {
        this.setValueFromMouse($$0);
    }

    @Override
    public void setFocused(boolean $$0) {
        super.setFocused($$0);
        if (!$$0) {
            this.canChangeValue = false;
            return;
        }
        InputType $$1 = Minecraft.getInstance().getLastInputType();
        if ($$1 == InputType.MOUSE || $$1 == InputType.KEYBOARD_TAB) {
            this.canChangeValue = true;
        }
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (CommonInputs.selected($$0)) {
            this.canChangeValue = !this.canChangeValue;
            return true;
        }
        if (this.canChangeValue) {
            boolean $$3;
            boolean bl = $$3 = $$0 == 263;
            if ($$3 || $$0 == 262) {
                float $$4 = $$3 ? -1.0f : 1.0f;
                this.setValue(this.value + (double)($$4 / (float)(this.width - 8)));
                return true;
            }
        }
        return false;
    }

    private void setValueFromMouse(double $$0) {
        this.setValue(($$0 - (double)(this.getX() + 4)) / (double)(this.width - 8));
    }

    private void setValue(double $$0) {
        double $$1 = this.value;
        this.value = Mth.clamp($$0, 0.0, 1.0);
        if ($$1 != this.value) {
            this.applyValue();
        }
        this.updateMessage();
    }

    @Override
    protected void onDrag(double $$0, double $$1, double $$2, double $$3) {
        this.setValueFromMouse($$0);
        super.onDrag($$0, $$1, $$2, $$3);
    }

    @Override
    public void playDownSound(SoundManager $$0) {
    }

    @Override
    public void onRelease(double $$0, double $$1) {
        super.playDownSound(Minecraft.getInstance().getSoundManager());
    }

    protected abstract void updateMessage();

    protected abstract void applyValue();
}

