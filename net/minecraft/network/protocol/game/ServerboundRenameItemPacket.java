/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundRenameItemPacket
implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundRenameItemPacket> STREAM_CODEC = Packet.codec(ServerboundRenameItemPacket::write, ServerboundRenameItemPacket::new);
    private final String name;

    public ServerboundRenameItemPacket(String $$0) {
        this.name = $$0;
    }

    private ServerboundRenameItemPacket(FriendlyByteBuf $$0) {
        this.name = $$0.readUtf();
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeUtf(this.name);
    }

    @Override
    public PacketType<ServerboundRenameItemPacket> type() {
        return GamePacketTypes.SERVERBOUND_RENAME_ITEM;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleRenameItem(this);
    }

    public String getName() {
        return this.name;
    }
}

