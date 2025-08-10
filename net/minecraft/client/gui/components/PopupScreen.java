/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PopupScreen
extends Screen {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("popup/background");
    private static final int SPACING = 12;
    private static final int BG_BORDER_WITH_SPACING = 18;
    private static final int BUTTON_SPACING = 6;
    private static final int IMAGE_SIZE_X = 130;
    private static final int IMAGE_SIZE_Y = 64;
    private static final int POPUP_DEFAULT_WIDTH = 250;
    private final Screen backgroundScreen;
    @Nullable
    private final ResourceLocation image;
    private final Component message;
    private final List<ButtonOption> buttons;
    @Nullable
    private final Runnable onClose;
    private final int contentWidth;
    private final LinearLayout layout = LinearLayout.vertical();

    PopupScreen(Screen $$0, int $$1, @Nullable ResourceLocation $$2, Component $$3, Component $$4, List<ButtonOption> $$5, @Nullable Runnable $$6) {
        super($$3);
        this.backgroundScreen = $$0;
        this.image = $$2;
        this.message = $$4;
        this.buttons = $$5;
        this.onClose = $$6;
        this.contentWidth = $$1 - 36;
    }

    @Override
    public void added() {
        super.added();
        this.backgroundScreen.clearFocus();
    }

    @Override
    protected void init() {
        this.backgroundScreen.init(this.minecraft, this.width, this.height);
        this.layout.spacing(12).defaultCellSetting().alignHorizontallyCenter();
        this.layout.addChild(new MultiLineTextWidget(this.title.copy().withStyle(ChatFormatting.BOLD), this.font).setMaxWidth(this.contentWidth).setCentered(true));
        if (this.image != null) {
            this.layout.addChild(ImageWidget.texture(130, 64, this.image, 130, 64));
        }
        this.layout.addChild(new MultiLineTextWidget(this.message, this.font).setMaxWidth(this.contentWidth).setCentered(true));
        this.layout.addChild(this.buildButtonRow());
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    private LinearLayout buildButtonRow() {
        int $$0 = 6 * (this.buttons.size() - 1);
        int $$12 = Math.min((this.contentWidth - $$0) / this.buttons.size(), 150);
        LinearLayout $$2 = LinearLayout.horizontal();
        $$2.spacing(6);
        for (ButtonOption $$3 : this.buttons) {
            $$2.addChild(Button.builder($$3.message(), $$1 -> $$3.action().accept(this)).width($$12).build());
        }
        return $$2;
    }

    @Override
    protected void repositionElements() {
        this.backgroundScreen.resize(this.minecraft, this.width, this.height);
        this.layout.arrangeElements();
        FrameLayout.centerInRectangle(this.layout, this.getRectangle());
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.backgroundScreen.renderBackground($$0, $$1, $$2, $$3);
        $$0.nextStratum();
        this.backgroundScreen.render($$0, -1, -1, $$3);
        $$0.nextStratum();
        this.renderTransparentBackground($$0);
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, this.layout.getX() - 18, this.layout.getY() - 18, this.layout.getWidth() + 36, this.layout.getHeight() + 36);
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.a(this.title, this.message);
    }

    @Override
    public void onClose() {
        if (this.onClose != null) {
            this.onClose.run();
        }
        this.minecraft.setScreen(this.backgroundScreen);
    }

    record ButtonOption(Component message, Consumer<PopupScreen> action) {
    }

    public static class Builder {
        private final Screen backgroundScreen;
        private final Component title;
        private Component message = CommonComponents.EMPTY;
        private int width = 250;
        @Nullable
        private ResourceLocation image;
        private final List<ButtonOption> buttons = new ArrayList<ButtonOption>();
        @Nullable
        private Runnable onClose = null;

        public Builder(Screen $$0, Component $$1) {
            this.backgroundScreen = $$0;
            this.title = $$1;
        }

        public Builder setWidth(int $$0) {
            this.width = $$0;
            return this;
        }

        public Builder setImage(ResourceLocation $$0) {
            this.image = $$0;
            return this;
        }

        public Builder setMessage(Component $$0) {
            this.message = $$0;
            return this;
        }

        public Builder addButton(Component $$0, Consumer<PopupScreen> $$1) {
            this.buttons.add(new ButtonOption($$0, $$1));
            return this;
        }

        public Builder onClose(Runnable $$0) {
            this.onClose = $$0;
            return this;
        }

        public PopupScreen build() {
            if (this.buttons.isEmpty()) {
                throw new IllegalStateException("Popup must have at least one button");
            }
            return new PopupScreen(this.backgroundScreen, this.width, this.image, this.title, this.message, List.copyOf(this.buttons), this.onClose);
        }
    }
}

