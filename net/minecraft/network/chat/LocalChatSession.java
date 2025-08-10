/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.chat;

import java.util.UUID;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.SignedMessageChain;
import net.minecraft.util.Signer;
import net.minecraft.world.entity.player.ProfileKeyPair;

public record LocalChatSession(UUID sessionId, ProfileKeyPair keyPair) {
    public static LocalChatSession create(ProfileKeyPair $$0) {
        return new LocalChatSession(UUID.randomUUID(), $$0);
    }

    public SignedMessageChain.Encoder createMessageEncoder(UUID $$0) {
        return new SignedMessageChain($$0, this.sessionId).encoder(Signer.from(this.keyPair.privateKey(), "SHA256withRSA"));
    }

    public RemoteChatSession asRemote() {
        return new RemoteChatSession(this.sessionId, this.keyPair.publicKey());
    }
}

