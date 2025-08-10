/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.chat;

import net.minecraft.network.chat.MessageSignature;

public record LastSeenTrackedEntry(MessageSignature signature, boolean pending) {
    public LastSeenTrackedEntry acknowledge() {
        return this.pending ? new LastSeenTrackedEntry(this.signature, false) : this;
    }
}

