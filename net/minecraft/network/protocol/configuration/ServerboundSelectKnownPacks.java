/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.configuration;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.configuration.ConfigurationPacketTypes;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.packs.repository.KnownPack;

public record ServerboundSelectKnownPacks(List<KnownPack> knownPacks) implements Packet<ServerConfigurationPacketListener>
{
    public static final StreamCodec<ByteBuf, ServerboundSelectKnownPacks> STREAM_CODEC = StreamCodec.composite(KnownPack.STREAM_CODEC.apply(ByteBufCodecs.list(64)), ServerboundSelectKnownPacks::knownPacks, ServerboundSelectKnownPacks::new);

    @Override
    public PacketType<ServerboundSelectKnownPacks> type() {
        return ConfigurationPacketTypes.SERVERBOUND_SELECT_KNOWN_PACKS;
    }

    @Override
    public void handle(ServerConfigurationPacketListener $$0) {
        $$0.handleSelectKnownPacks(this);
    }
}

