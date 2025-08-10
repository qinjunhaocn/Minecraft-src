/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public record GuiMessageTag(int indicatorColor, @Nullable Icon icon, @Nullable Component text, @Nullable String logTag) {
    private static final Component SYSTEM_TEXT = Component.translatable("chat.tag.system");
    private static final Component SYSTEM_TEXT_SINGLE_PLAYER = Component.translatable("chat.tag.system_single_player");
    private static final Component CHAT_NOT_SECURE_TEXT = Component.translatable("chat.tag.not_secure");
    private static final Component CHAT_MODIFIED_TEXT = Component.translatable("chat.tag.modified");
    private static final Component CHAT_ERROR_TEXT = Component.translatable("chat.tag.error");
    private static final int CHAT_NOT_SECURE_INDICATOR_COLOR = 0xD0D0D0;
    private static final int CHAT_MODIFIED_INDICATOR_COLOR = 0x606060;
    private static final GuiMessageTag SYSTEM = new GuiMessageTag(0xD0D0D0, null, SYSTEM_TEXT, "System");
    private static final GuiMessageTag SYSTEM_SINGLE_PLAYER = new GuiMessageTag(0xD0D0D0, null, SYSTEM_TEXT_SINGLE_PLAYER, "System");
    private static final GuiMessageTag CHAT_NOT_SECURE = new GuiMessageTag(0xD0D0D0, null, CHAT_NOT_SECURE_TEXT, "Not Secure");
    private static final GuiMessageTag CHAT_ERROR = new GuiMessageTag(0xFF5555, null, CHAT_ERROR_TEXT, "Chat Error");

    public static GuiMessageTag system() {
        return SYSTEM;
    }

    public static GuiMessageTag systemSinglePlayer() {
        return SYSTEM_SINGLE_PLAYER;
    }

    public static GuiMessageTag chatNotSecure() {
        return CHAT_NOT_SECURE;
    }

    public static GuiMessageTag chatModified(String $$0) {
        MutableComponent $$1 = Component.literal($$0).withStyle(ChatFormatting.GRAY);
        MutableComponent $$2 = Component.empty().append(CHAT_MODIFIED_TEXT).append(CommonComponents.NEW_LINE).append($$1);
        return new GuiMessageTag(0x606060, Icon.CHAT_MODIFIED, $$2, "Modified");
    }

    public static GuiMessageTag chatError() {
        return CHAT_ERROR;
    }

    @Nullable
    public Icon icon() {
        return this.icon;
    }

    @Nullable
    public Component text() {
        return this.text;
    }

    @Nullable
    public String logTag() {
        return this.logTag;
    }

    public static final class Icon
    extends Enum<Icon> {
        public static final /* enum */ Icon CHAT_MODIFIED = new Icon(ResourceLocation.withDefaultNamespace("icon/chat_modified"), 9, 9);
        public final ResourceLocation sprite;
        public final int width;
        public final int height;
        private static final /* synthetic */ Icon[] $VALUES;

        public static Icon[] values() {
            return (Icon[])$VALUES.clone();
        }

        public static Icon valueOf(String $$0) {
            return Enum.valueOf(Icon.class, $$0);
        }

        private Icon(ResourceLocation $$0, int $$1, int $$2) {
            this.sprite = $$0;
            this.width = $$1;
            this.height = $$2;
        }

        public void draw(GuiGraphics $$0, int $$1, int $$2) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, $$1, $$2, this.width, this.height);
        }

        private static /* synthetic */ Icon[] a() {
            return new Icon[]{CHAT_MODIFIED};
        }

        static {
            $VALUES = Icon.a();
        }
    }
}

