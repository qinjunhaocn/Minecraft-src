/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.ping;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.ping.ClientPongPacketListener;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.ping.ServerPingPacketListener;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.resources.ResourceLocation;

public class PingPacketTypes {
    public static final PacketType<ClientboundPongResponsePacket> CLIENTBOUND_PONG_RESPONSE = PingPacketTypes.createClientbound("pong_response");
    public static final PacketType<ServerboundPingRequestPacket> SERVERBOUND_PING_REQUEST = PingPacketTypes.createServerbound("ping_request");

    private static <T extends Packet<ClientPongPacketListener>> PacketType<T> createClientbound(String $$0) {
        return new PacketType(PacketFlow.CLIENTBOUND, ResourceLocation.withDefaultNamespace($$0));
    }

    private static <T extends Packet<ServerPingPacketListener>> PacketType<T> createServerbound(String $$0) {
        return new PacketType(PacketFlow.SERVERBOUND, ResourceLocation.withDefaultNamespace($$0));
    }
}

