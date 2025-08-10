/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;

public class ClientboundTagQueryPacket
implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundTagQueryPacket> STREAM_CODEC = Packet.codec(ClientboundTagQueryPacket::write, ClientboundTagQueryPacket::new);
    private final int transactionId;
    @Nullable
    private final CompoundTag tag;

    public ClientboundTagQueryPacket(int $$0, @Nullable CompoundTag $$1) {
        this.transactionId = $$0;
        this.tag = $$1;
    }

    private ClientboundTagQueryPacket(FriendlyByteBuf $$0) {
        this.transactionId = $$0.readVarInt();
        this.tag = $$0.readNbt();
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.transactionId);
        $$0.writeNbt(this.tag);
    }

    @Override
    public PacketType<ClientboundTagQueryPacket> type() {
        return GamePacketTypes.CLIENTBOUND_TAG_QUERY;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleTagQueryPacket(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}

