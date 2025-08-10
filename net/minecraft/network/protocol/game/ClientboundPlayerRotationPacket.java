/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;

public record ClientboundPlayerRotationPacket(float yRot, float xRot) implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<FriendlyByteBuf, ClientboundPlayerRotationPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, ClientboundPlayerRotationPacket::yRot, ByteBufCodecs.FLOAT, ClientboundPlayerRotationPacket::xRot, ClientboundPlayerRotationPacket::new);

    @Override
    public PacketType<ClientboundPlayerRotationPacket> type() {
        return GamePacketTypes.CLIENTBOUND_PLAYER_ROTATION;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleRotatePlayer(this);
    }
}

