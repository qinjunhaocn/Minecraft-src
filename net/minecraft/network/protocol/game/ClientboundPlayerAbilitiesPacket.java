/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.world.entity.player.Abilities;

public class ClientboundPlayerAbilitiesPacket
implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundPlayerAbilitiesPacket> STREAM_CODEC = Packet.codec(ClientboundPlayerAbilitiesPacket::write, ClientboundPlayerAbilitiesPacket::new);
    private static final int FLAG_INVULNERABLE = 1;
    private static final int FLAG_FLYING = 2;
    private static final int FLAG_CAN_FLY = 4;
    private static final int FLAG_INSTABUILD = 8;
    private final boolean invulnerable;
    private final boolean isFlying;
    private final boolean canFly;
    private final boolean instabuild;
    private final float flyingSpeed;
    private final float walkingSpeed;

    public ClientboundPlayerAbilitiesPacket(Abilities $$0) {
        this.invulnerable = $$0.invulnerable;
        this.isFlying = $$0.flying;
        this.canFly = $$0.mayfly;
        this.instabuild = $$0.instabuild;
        this.flyingSpeed = $$0.getFlyingSpeed();
        this.walkingSpeed = $$0.getWalkingSpeed();
    }

    private ClientboundPlayerAbilitiesPacket(FriendlyByteBuf $$0) {
        byte $$1 = $$0.readByte();
        this.invulnerable = ($$1 & 1) != 0;
        this.isFlying = ($$1 & 2) != 0;
        this.canFly = ($$1 & 4) != 0;
        this.instabuild = ($$1 & 8) != 0;
        this.flyingSpeed = $$0.readFloat();
        this.walkingSpeed = $$0.readFloat();
    }

    private void write(FriendlyByteBuf $$0) {
        byte $$1 = 0;
        if (this.invulnerable) {
            $$1 = (byte)($$1 | 1);
        }
        if (this.isFlying) {
            $$1 = (byte)($$1 | 2);
        }
        if (this.canFly) {
            $$1 = (byte)($$1 | 4);
        }
        if (this.instabuild) {
            $$1 = (byte)($$1 | 8);
        }
        $$0.writeByte($$1);
        $$0.writeFloat(this.flyingSpeed);
        $$0.writeFloat(this.walkingSpeed);
    }

    @Override
    public PacketType<ClientboundPlayerAbilitiesPacket> type() {
        return GamePacketTypes.CLIENTBOUND_PLAYER_ABILITIES;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handlePlayerAbilities(this);
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public boolean isFlying() {
        return this.isFlying;
    }

    public boolean canFly() {
        return this.canFly;
    }

    public boolean canInstabuild() {
        return this.instabuild;
    }

    public float getFlyingSpeed() {
        return this.flyingSpeed;
    }

    public float getWalkingSpeed() {
        return this.walkingSpeed;
    }
}

