/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public abstract class ImageWidget
extends AbstractWidget {
    ImageWidget(int $$0, int $$1, int $$2, int $$3) {
        super($$0, $$1, $$2, $$3, CommonComponents.EMPTY);
    }

    public static ImageWidget texture(int $$0, int $$1, ResourceLocation $$2, int $$3, int $$4) {
        return new Texture(0, 0, $$0, $$1, $$2, $$3, $$4);
    }

    public static ImageWidget sprite(int $$0, int $$1, ResourceLocation $$2) {
        return new Sprite(0, 0, $$0, $$1, $$2);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput $$0) {
    }

    @Override
    public void playDownSound(SoundManager $$0) {
    }

    @Override
    public boolean isActive() {
        return false;
    }

    public abstract void updateResource(ResourceLocation var1);

    @Override
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent $$0) {
        return null;
    }

    static class Texture
    extends ImageWidget {
        private ResourceLocation texture;
        private final int textureWidth;
        private final int textureHeight;

        public Texture(int $$0, int $$1, int $$2, int $$3, ResourceLocation $$4, int $$5, int $$6) {
            super($$0, $$1, $$2, $$3);
            this.texture = $$4;
            this.textureWidth = $$5;
            this.textureHeight = $$6;
        }

        @Override
        protected void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
            $$0.blit(RenderPipelines.GUI_TEXTURED, this.texture, this.getX(), this.getY(), 0.0f, 0.0f, this.getWidth(), this.getHeight(), this.textureWidth, this.textureHeight);
        }

        @Override
        public void updateResource(ResourceLocation $$0) {
            this.texture = $$0;
        }
    }

    static class Sprite
    extends ImageWidget {
        private ResourceLocation sprite;

        public Sprite(int $$0, int $$1, int $$2, int $$3, ResourceLocation $$4) {
            super($$0, $$1, $$2, $$3);
            this.sprite = $$4;
        }

        @Override
        public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

        @Override
        public void updateResource(ResourceLocation $$0) {
            this.sprite = $$0;
        }
    }
}

