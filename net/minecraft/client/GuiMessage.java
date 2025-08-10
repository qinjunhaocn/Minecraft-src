/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client;

import javax.annotation.Nullable;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;

public record GuiMessage(int addedTime, Component content, @Nullable MessageSignature signature, @Nullable GuiMessageTag tag) {
    @Nullable
    public GuiMessageTag.Icon icon() {
        return this.tag != null ? this.tag.icon() : null;
    }

    @Nullable
    public MessageSignature signature() {
        return this.signature;
    }

    @Nullable
    public GuiMessageTag tag() {
        return this.tag;
    }

    public record Line(int addedTime, FormattedCharSequence content, @Nullable GuiMessageTag tag, boolean endOfEntry) {
        @Nullable
        public GuiMessageTag tag() {
            return this.tag;
        }
    }
}

