/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.network.protocol.game;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.util.debugchart.RemoteDebugSampleType;

public final class ClientboundDebugSamplePacket
extends Record
implements Packet<ClientGamePacketListener> {
    private final long[] sample;
    private final RemoteDebugSampleType debugSampleType;
    public static final StreamCodec<FriendlyByteBuf, ClientboundDebugSamplePacket> STREAM_CODEC = Packet.codec(ClientboundDebugSamplePacket::write, ClientboundDebugSamplePacket::new);

    private ClientboundDebugSamplePacket(FriendlyByteBuf $$0) {
        this($$0.d(), $$0.readEnum(RemoteDebugSampleType.class));
    }

    public ClientboundDebugSamplePacket(long[] $$0, RemoteDebugSampleType $$1) {
        this.sample = $$0;
        this.debugSampleType = $$1;
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.a(this.sample);
        $$0.writeEnum(this.debugSampleType);
    }

    @Override
    public PacketType<ClientboundDebugSamplePacket> type() {
        return GamePacketTypes.CLIENTBOUND_DEBUG_SAMPLE;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleDebugSample(this);
    }

    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ClientboundDebugSamplePacket.class, "sample;debugSampleType", "sample", "debugSampleType"}, this);
    }

    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ClientboundDebugSamplePacket.class, "sample;debugSampleType", "sample", "debugSampleType"}, this);
    }

    public final boolean equals(Object $$0) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ClientboundDebugSamplePacket.class, "sample;debugSampleType", "sample", "debugSampleType"}, this, $$0);
    }

    public long[] b() {
        return this.sample;
    }

    public RemoteDebugSampleType debugSampleType() {
        return this.debugSampleType;
    }
}

