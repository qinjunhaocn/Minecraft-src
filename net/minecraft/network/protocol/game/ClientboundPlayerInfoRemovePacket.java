/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;

public record ClientboundPlayerInfoRemovePacket(List<UUID> profileIds) implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<FriendlyByteBuf, ClientboundPlayerInfoRemovePacket> STREAM_CODEC = Packet.codec(ClientboundPlayerInfoRemovePacket::write, ClientboundPlayerInfoRemovePacket::new);

    private ClientboundPlayerInfoRemovePacket(FriendlyByteBuf $$0) {
        this($$0.readList(UUIDUtil.STREAM_CODEC));
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeCollection(this.profileIds, UUIDUtil.STREAM_CODEC);
    }

    @Override
    public PacketType<ClientboundPlayerInfoRemovePacket> type() {
        return GamePacketTypes.CLIENTBOUND_PLAYER_INFO_REMOVE;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handlePlayerInfoRemove(this);
    }
}

