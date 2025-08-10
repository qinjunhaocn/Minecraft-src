/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class SpriteIconButton
extends Button {
    protected final ResourceLocation sprite;
    protected final int spriteWidth;
    protected final int spriteHeight;

    SpriteIconButton(int $$0, int $$1, Component $$2, int $$3, int $$4, ResourceLocation $$5, Button.OnPress $$6, @Nullable Button.CreateNarration $$7) {
        super(0, 0, $$0, $$1, $$2, $$6, $$7 == null ? DEFAULT_NARRATION : $$7);
        this.spriteWidth = $$3;
        this.spriteHeight = $$4;
        this.sprite = $$5;
    }

    public static Builder builder(Component $$0, Button.OnPress $$1, boolean $$2) {
        return new Builder($$0, $$1, $$2);
    }

    public static class Builder {
        private final Component message;
        private final Button.OnPress onPress;
        private final boolean iconOnly;
        private int width = 150;
        private int height = 20;
        @Nullable
        private ResourceLocation sprite;
        private int spriteWidth;
        private int spriteHeight;
        @Nullable
        Button.CreateNarration narration;

        public Builder(Component $$0, Button.OnPress $$1, boolean $$2) {
            this.message = $$0;
            this.onPress = $$1;
            this.iconOnly = $$2;
        }

        public Builder width(int $$0) {
            this.width = $$0;
            return this;
        }

        public Builder size(int $$0, int $$1) {
            this.width = $$0;
            this.height = $$1;
            return this;
        }

        public Builder sprite(ResourceLocation $$0, int $$1, int $$2) {
            this.sprite = $$0;
            this.spriteWidth = $$1;
            this.spriteHeight = $$2;
            return this;
        }

        public Builder narration(Button.CreateNarration $$0) {
            this.narration = $$0;
            return this;
        }

        public SpriteIconButton build() {
            if (this.sprite == null) {
                throw new IllegalStateException("Sprite not set");
            }
            if (this.iconOnly) {
                return new CenteredIcon(this.width, this.height, this.message, this.spriteWidth, this.spriteHeight, this.sprite, this.onPress, this.narration);
            }
            return new TextAndIcon(this.width, this.height, this.message, this.spriteWidth, this.spriteHeight, this.sprite, this.onPress, this.narration);
        }
    }

    public static class TextAndIcon
    extends SpriteIconButton {
        protected TextAndIcon(int $$0, int $$1, Component $$2, int $$3, int $$4, ResourceLocation $$5, Button.OnPress $$6, @Nullable Button.CreateNarration $$7) {
            super($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        }

        @Override
        public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
            super.renderWidget($$0, $$1, $$2, $$3);
            int $$4 = this.getX() + this.getWidth() - this.spriteWidth - 2;
            int $$5 = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, $$4, $$5, this.spriteWidth, this.spriteHeight, this.alpha);
        }

        @Override
        public void renderString(GuiGraphics $$0, Font $$1, int $$2) {
            int $$3 = this.getX() + 2;
            int $$4 = this.getX() + this.getWidth() - this.spriteWidth - 4;
            int $$5 = this.getX() + this.getWidth() / 2;
            TextAndIcon.renderScrollingString($$0, $$1, this.getMessage(), $$5, $$3, this.getY(), $$4, this.getY() + this.getHeight(), $$2);
        }
    }

    public static class CenteredIcon
    extends SpriteIconButton {
        protected CenteredIcon(int $$0, int $$1, Component $$2, int $$3, int $$4, ResourceLocation $$5, Button.OnPress $$6, @Nullable Button.CreateNarration $$7) {
            super($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        }

        @Override
        public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
            super.renderWidget($$0, $$1, $$2, $$3);
            int $$4 = this.getX() + this.getWidth() / 2 - this.spriteWidth / 2;
            int $$5 = this.getY() + this.getHeight() / 2 - this.spriteHeight / 2;
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, $$4, $$5, this.spriteWidth, this.spriteHeight, this.alpha);
        }

        @Override
        public void renderString(GuiGraphics $$0, Font $$1, int $$2) {
        }
    }
}

