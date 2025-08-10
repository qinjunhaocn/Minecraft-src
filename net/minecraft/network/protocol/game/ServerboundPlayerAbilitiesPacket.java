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
import net.minecraft.world.entity.player.Abilities;

public class ServerboundPlayerAbilitiesPacket
implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundPlayerAbilitiesPacket> STREAM_CODEC = Packet.codec(ServerboundPlayerAbilitiesPacket::write, ServerboundPlayerAbilitiesPacket::new);
    private static final int FLAG_FLYING = 2;
    private final boolean isFlying;

    public ServerboundPlayerAbilitiesPacket(Abilities $$0) {
        this.isFlying = $$0.flying;
    }

    private ServerboundPlayerAbilitiesPacket(FriendlyByteBuf $$0) {
        byte $$1 = $$0.readByte();
        this.isFlying = ($$1 & 2) != 0;
    }

    private void write(FriendlyByteBuf $$0) {
        int $$1 = 0;
        if (this.isFlying) {
            $$1 = (byte)($$1 | 2);
        }
        $$0.writeByte($$1);
    }

    @Override
    public PacketType<ServerboundPlayerAbilitiesPacket> type() {
        return GamePacketTypes.SERVERBOUND_PLAYER_ABILITIES;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handlePlayerAbilities(this);
    }

    public boolean isFlying() {
        return this.isFlying;
    }
}

