/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public class Checkbox
extends AbstractButton {
    private static final ResourceLocation CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox_selected_highlighted");
    private static final ResourceLocation CHECKBOX_SELECTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox_selected");
    private static final ResourceLocation CHECKBOX_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox_highlighted");
    private static final ResourceLocation CHECKBOX_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox");
    private static final int TEXT_COLOR = -2039584;
    private static final int SPACING = 4;
    private static final int BOX_PADDING = 8;
    private boolean selected;
    private final OnValueChange onValueChange;
    private final MultiLineTextWidget textWidget;

    Checkbox(int $$0, int $$1, int $$2, Component $$3, Font $$4, boolean $$5, OnValueChange $$6) {
        super($$0, $$1, 0, 0, $$3);
        this.width = this.getAdjustedWidth($$2, $$3, $$4);
        this.textWidget = new MultiLineTextWidget($$3, $$4).setMaxWidth(this.width).setColor(-2039584);
        this.height = this.getAdjustedHeight($$4);
        this.selected = $$5;
        this.onValueChange = $$6;
    }

    private int getAdjustedWidth(int $$0, Component $$1, Font $$2) {
        return Math.min(Checkbox.getDefaultWidth($$1, $$2), $$0);
    }

    private int getAdjustedHeight(Font $$0) {
        return Math.max(Checkbox.getBoxSize($$0), this.textWidget.getHeight());
    }

    static int getDefaultWidth(Component $$0, Font $$1) {
        return Checkbox.getBoxSize($$1) + 4 + $$1.width($$0);
    }

    public static Builder builder(Component $$0, Font $$1) {
        return new Builder($$0, $$1);
    }

    public static int getBoxSize(Font $$0) {
        return $$0.lineHeight + 8;
    }

    @Override
    public void onPress() {
        this.selected = !this.selected;
        this.onValueChange.onValueChange(this, this.selected);
    }

    public boolean selected() {
        return this.selected;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                $$0.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused"));
            } else {
                $$0.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered"));
            }
        }
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        ResourceLocation $$7;
        Minecraft $$4 = Minecraft.getInstance();
        Font $$5 = $$4.font;
        if (this.selected) {
            ResourceLocation $$6 = this.isFocused() ? CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE : CHECKBOX_SELECTED_SPRITE;
        } else {
            $$7 = this.isFocused() ? CHECKBOX_HIGHLIGHTED_SPRITE : CHECKBOX_SPRITE;
        }
        int $$8 = Checkbox.getBoxSize($$5);
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$7, this.getX(), this.getY(), $$8, $$8, ARGB.white(this.alpha));
        int $$9 = this.getX() + $$8 + 4;
        int $$10 = this.getY() + $$8 / 2 - this.textWidget.getHeight() / 2;
        this.textWidget.setPosition($$9, $$10);
        this.textWidget.renderWidget($$0, $$1, $$2, $$3);
    }

    public static interface OnValueChange {
        public static final OnValueChange NOP = ($$0, $$1) -> {};

        public void onValueChange(Checkbox var1, boolean var2);
    }

    public static class Builder {
        private final Component message;
        private final Font font;
        private int maxWidth;
        private int x = 0;
        private int y = 0;
        private OnValueChange onValueChange = OnValueChange.NOP;
        private boolean selected = false;
        @Nullable
        private OptionInstance<Boolean> option = null;
        @Nullable
        private Tooltip tooltip = null;

        Builder(Component $$0, Font $$1) {
            this.message = $$0;
            this.font = $$1;
            this.maxWidth = Checkbox.getDefaultWidth($$0, $$1);
        }

        public Builder pos(int $$0, int $$1) {
            this.x = $$0;
            this.y = $$1;
            return this;
        }

        public Builder onValueChange(OnValueChange $$0) {
            this.onValueChange = $$0;
            return this;
        }

        public Builder selected(boolean $$0) {
            this.selected = $$0;
            this.option = null;
            return this;
        }

        public Builder selected(OptionInstance<Boolean> $$0) {
            this.option = $$0;
            this.selected = $$0.get();
            return this;
        }

        public Builder tooltip(Tooltip $$0) {
            this.tooltip = $$0;
            return this;
        }

        public Builder maxWidth(int $$0) {
            this.maxWidth = $$0;
            return this;
        }

        public Checkbox build() {
            OnValueChange $$02 = this.option == null ? this.onValueChange : ($$0, $$1) -> {
                this.option.set($$1);
                this.onValueChange.onValueChange($$0, $$1);
            };
            Checkbox $$12 = new Checkbox(this.x, this.y, this.maxWidth, this.message, this.font, this.selected, $$02);
            $$12.setTooltip(this.tooltip);
            return $$12;
        }
    }
}

