/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.LoginPacketTypes;

public record ClientboundLoginFinishedPacket(GameProfile gameProfile) implements Packet<ClientLoginPacketListener>
{
    public static final StreamCodec<ByteBuf, ClientboundLoginFinishedPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.GAME_PROFILE, ClientboundLoginFinishedPacket::gameProfile, ClientboundLoginFinishedPacket::new);

    @Override
    public PacketType<ClientboundLoginFinishedPacket> type() {
        return LoginPacketTypes.CLIENTBOUND_LOGIN_FINISHED;
    }

    @Override
    public void handle(ClientLoginPacketListener $$0) {
        $$0.handleLoginFinished(this);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}

