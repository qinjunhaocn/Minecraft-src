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
import net.minecraft.network.protocol.configuration.ClientConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ConfigurationPacketTypes;
import net.minecraft.server.packs.repository.KnownPack;

public record ClientboundSelectKnownPacks(List<KnownPack> knownPacks) implements Packet<ClientConfigurationPacketListener>
{
    public static final StreamCodec<ByteBuf, ClientboundSelectKnownPacks> STREAM_CODEC = StreamCodec.composite(KnownPack.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundSelectKnownPacks::knownPacks, ClientboundSelectKnownPacks::new);

    @Override
    public PacketType<ClientboundSelectKnownPacks> type() {
        return ConfigurationPacketTypes.CLIENTBOUND_SELECT_KNOWN_PACKS;
    }

    @Override
    public void handle(ClientConfigurationPacketListener $$0) {
        $$0.handleSelectKnownPacks(this);
    }
}

