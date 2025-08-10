/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.client.multiplayer.chat;

import com.mojang.serialization.Codec;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringRepresentable;

public final class ChatTrustLevel
extends Enum<ChatTrustLevel>
implements StringRepresentable {
    public static final /* enum */ ChatTrustLevel SECURE = new ChatTrustLevel("secure");
    public static final /* enum */ ChatTrustLevel MODIFIED = new ChatTrustLevel("modified");
    public static final /* enum */ ChatTrustLevel NOT_SECURE = new ChatTrustLevel("not_secure");
    public static final Codec<ChatTrustLevel> CODEC;
    private final String serializedName;
    private static final /* synthetic */ ChatTrustLevel[] $VALUES;

    public static ChatTrustLevel[] values() {
        return (ChatTrustLevel[])$VALUES.clone();
    }

    public static ChatTrustLevel valueOf(String $$0) {
        return Enum.valueOf(ChatTrustLevel.class, $$0);
    }

    private ChatTrustLevel(String $$0) {
        this.serializedName = $$0;
    }

    public static ChatTrustLevel evaluate(PlayerChatMessage $$0, Component $$1, Instant $$2) {
        if (!$$0.hasSignature() || $$0.hasExpiredClient($$2)) {
            return NOT_SECURE;
        }
        if (ChatTrustLevel.isModified($$0, $$1)) {
            return MODIFIED;
        }
        return SECURE;
    }

    private static boolean isModified(PlayerChatMessage $$0, Component $$1) {
        if (!$$1.getString().contains($$0.signedContent())) {
            return true;
        }
        Component $$2 = $$0.unsignedContent();
        if ($$2 == null) {
            return false;
        }
        return ChatTrustLevel.containsModifiedStyle($$2);
    }

    private static boolean containsModifiedStyle(Component $$02) {
        return $$02.visit(($$0, $$1) -> {
            if (ChatTrustLevel.isModifiedStyle($$0)) {
                return Optional.of(true);
            }
            return Optional.empty();
        }, Style.EMPTY).orElse(false);
    }

    private static boolean isModifiedStyle(Style $$0) {
        return !$$0.getFont().equals(Style.DEFAULT_FONT);
    }

    public boolean isNotSecure() {
        return this == NOT_SECURE;
    }

    @Nullable
    public GuiMessageTag createTag(PlayerChatMessage $$0) {
        return switch (this.ordinal()) {
            case 1 -> GuiMessageTag.chatModified($$0.signedContent());
            case 2 -> GuiMessageTag.chatNotSecure();
            default -> null;
        };
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }

    private static /* synthetic */ ChatTrustLevel[] b() {
        return new ChatTrustLevel[]{SECURE, MODIFIED, NOT_SECURE};
    }

    static {
        $VALUES = ChatTrustLevel.b();
        CODEC = StringRepresentable.fromEnum(ChatTrustLevel::values);
    }
}

