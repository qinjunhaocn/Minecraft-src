/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.network.protocol.common;

import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.CommonPacketTypes;
import net.minecraft.resources.ResourceLocation;

public final class ClientboundStoreCookiePacket
extends Record
implements Packet<ClientCommonPacketListener> {
    private final ResourceLocation key;
    private final byte[] payload;
    public static final StreamCodec<FriendlyByteBuf, ClientboundStoreCookiePacket> STREAM_CODEC = Packet.codec(ClientboundStoreCookiePacket::write, ClientboundStoreCookiePacket::new);
    private static final int MAX_PAYLOAD_SIZE = 5120;
    public static final StreamCodec<ByteBuf, byte[]> PAYLOAD_STREAM_CODEC = ByteBufCodecs.byteArray(5120);

    private ClientboundStoreCookiePacket(FriendlyByteBuf $$0) {
        this($$0.readResourceLocation(), (byte[])PAYLOAD_STREAM_CODEC.decode($$0));
    }

    public ClientboundStoreCookiePacket(ResourceLocation $$0, byte[] $$1) {
        this.key = $$0;
        this.payload = $$1;
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeResourceLocation(this.key);
        PAYLOAD_STREAM_CODEC.encode($$0, this.payload);
    }

    @Override
    public PacketType<ClientboundStoreCookiePacket> type() {
        return CommonPacketTypes.CLIENTBOUND_STORE_COOKIE;
    }

    @Override
    public void handle(ClientCommonPacketListener $$0) {
        $$0.handleStoreCookie(this);
    }

    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ClientboundStoreCookiePacket.class, "key;payload", "key", "payload"}, this);
    }

    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ClientboundStoreCookiePacket.class, "key;payload", "key", "payload"}, this);
    }

    public final boolean equals(Object $$0) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ClientboundStoreCookiePacket.class, "key;payload", "key", "payload"}, this, $$0);
    }

    public ResourceLocation key() {
        return this.key;
    }

    public byte[] e() {
        return this.payload;
    }
}

