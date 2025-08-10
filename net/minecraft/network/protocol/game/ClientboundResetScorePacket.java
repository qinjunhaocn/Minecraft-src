/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;

public record ClientboundResetScorePacket(String owner, @Nullable String objectiveName) implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<FriendlyByteBuf, ClientboundResetScorePacket> STREAM_CODEC = Packet.codec(ClientboundResetScorePacket::write, ClientboundResetScorePacket::new);

    private ClientboundResetScorePacket(FriendlyByteBuf $$0) {
        this($$0.readUtf(), $$0.readNullable(FriendlyByteBuf::readUtf));
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeUtf(this.owner);
        $$0.writeNullable(this.objectiveName, FriendlyByteBuf::writeUtf);
    }

    @Override
    public PacketType<ClientboundResetScorePacket> type() {
        return GamePacketTypes.CLIENTBOUND_RESET_SCORE;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleResetScore(this);
    }

    @Nullable
    public String objectiveName() {
        return this.objectiveName;
    }
}

