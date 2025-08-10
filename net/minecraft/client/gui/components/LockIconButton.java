/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class LockIconButton
extends Button {
    private boolean locked;

    public LockIconButton(int $$0, int $$1, Button.OnPress $$2) {
        super($$0, $$1, 20, 20, Component.translatable("narrator.button.difficulty_lock"), $$2, DEFAULT_NARRATION);
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return CommonComponents.a(super.createNarrationMessage(), this.isLocked() ? Component.translatable("narrator.button.difficulty_lock.locked") : Component.translatable("narrator.button.difficulty_lock.unlocked"));
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean $$0) {
        this.locked = $$0;
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        Icon $$6;
        if (!this.active) {
            Icon $$4 = this.locked ? Icon.LOCKED_DISABLED : Icon.UNLOCKED_DISABLED;
        } else if (this.isHoveredOrFocused()) {
            Icon $$5 = this.locked ? Icon.LOCKED_HOVER : Icon.UNLOCKED_HOVER;
        } else {
            $$6 = this.locked ? Icon.LOCKED : Icon.UNLOCKED;
        }
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$6.sprite, this.getX(), this.getY(), this.width, this.height);
    }

    static final class Icon
    extends Enum<Icon> {
        public static final /* enum */ Icon LOCKED = new Icon(ResourceLocation.withDefaultNamespace("widget/locked_button"));
        public static final /* enum */ Icon LOCKED_HOVER = new Icon(ResourceLocation.withDefaultNamespace("widget/locked_button_highlighted"));
        public static final /* enum */ Icon LOCKED_DISABLED = new Icon(ResourceLocation.withDefaultNamespace("widget/locked_button_disabled"));
        public static final /* enum */ Icon UNLOCKED = new Icon(ResourceLocation.withDefaultNamespace("widget/unlocked_button"));
        public static final /* enum */ Icon UNLOCKED_HOVER = new Icon(ResourceLocation.withDefaultNamespace("widget/unlocked_button_highlighted"));
        public static final /* enum */ Icon UNLOCKED_DISABLED = new Icon(ResourceLocation.withDefaultNamespace("widget/unlocked_button_disabled"));
        final ResourceLocation sprite;
        private static final /* synthetic */ Icon[] $VALUES;

        public static Icon[] values() {
            return (Icon[])$VALUES.clone();
        }

        public static Icon valueOf(String $$0) {
            return Enum.valueOf(Icon.class, $$0);
        }

        private Icon(ResourceLocation $$0) {
            this.sprite = $$0;
        }

        private static /* synthetic */ Icon[] a() {
            return new Icon[]{LOCKED, LOCKED_HOVER, LOCKED_DISABLED, UNLOCKED, UNLOCKED_HOVER, UNLOCKED_DISABLED};
        }

        static {
            $VALUES = Icon.a();
        }
    }
}

