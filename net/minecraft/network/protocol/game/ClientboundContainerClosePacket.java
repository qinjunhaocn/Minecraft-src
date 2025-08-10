/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;

public class ClientboundContainerClosePacket
implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundContainerClosePacket> STREAM_CODEC = Packet.codec(ClientboundContainerClosePacket::write, ClientboundContainerClosePacket::new);
    private final int containerId;

    public ClientboundContainerClosePacket(int $$0) {
        this.containerId = $$0;
    }

    private ClientboundContainerClosePacket(FriendlyByteBuf $$0) {
        this.containerId = $$0.readContainerId();
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeContainerId(this.containerId);
    }

    @Override
    public PacketType<ClientboundContainerClosePacket> type() {
        return GamePacketTypes.CLIENTBOUND_CONTAINER_CLOSE;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleContainerClose(this);
    }

    public int getContainerId() {
        return this.containerId;
    }
}

