/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.network.protocol.cookie;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.network.protocol.cookie.CookiePacketTypes;
import net.minecraft.network.protocol.cookie.ServerCookiePacketListener;
import net.minecraft.resources.ResourceLocation;

public final class ServerboundCookieResponsePacket
extends Record
implements Packet<ServerCookiePacketListener> {
    private final ResourceLocation key;
    @Nullable
    private final byte[] payload;
    public static final StreamCodec<FriendlyByteBuf, ServerboundCookieResponsePacket> STREAM_CODEC = Packet.codec(ServerboundCookieResponsePacket::write, ServerboundCookieResponsePacket::new);

    private ServerboundCookieResponsePacket(FriendlyByteBuf $$0) {
        this($$0.readResourceLocation(), $$0.readNullable(ClientboundStoreCookiePacket.PAYLOAD_STREAM_CODEC));
    }

    public ServerboundCookieResponsePacket(ResourceLocation $$0, @Nullable byte[] $$1) {
        this.key = $$0;
        this.payload = $$1;
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeResourceLocation(this.key);
        $$0.writeNullable(this.payload, ClientboundStoreCookiePacket.PAYLOAD_STREAM_CODEC);
    }

    @Override
    public PacketType<ServerboundCookieResponsePacket> type() {
        return CookiePacketTypes.SERVERBOUND_COOKIE_RESPONSE;
    }

    @Override
    public void handle(ServerCookiePacketListener $$0) {
        $$0.handleCookieResponse(this);
    }

    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ServerboundCookieResponsePacket.class, "key;payload", "key", "payload"}, this);
    }

    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ServerboundCookieResponsePacket.class, "key;payload", "key", "payload"}, this);
    }

    public final boolean equals(Object $$0) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ServerboundCookieResponsePacket.class, "key;payload", "key", "payload"}, this, $$0);
    }

    public ResourceLocation key() {
        return this.key;
    }

    @Nullable
    public byte[] e() {
        return this.payload;
    }
}

