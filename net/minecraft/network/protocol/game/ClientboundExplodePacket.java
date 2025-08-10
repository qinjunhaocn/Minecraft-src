/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;

public record ClientboundExplodePacket(Vec3 center, Optional<Vec3> playerKnockback, ParticleOptions explosionParticle, Holder<SoundEvent> explosionSound) implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundExplodePacket> STREAM_CODEC = StreamCodec.composite(Vec3.STREAM_CODEC, ClientboundExplodePacket::center, Vec3.STREAM_CODEC.apply(ByteBufCodecs::optional), ClientboundExplodePacket::playerKnockback, ParticleTypes.STREAM_CODEC, ClientboundExplodePacket::explosionParticle, SoundEvent.STREAM_CODEC, ClientboundExplodePacket::explosionSound, ClientboundExplodePacket::new);

    @Override
    public PacketType<ClientboundExplodePacket> type() {
        return GamePacketTypes.CLIENTBOUND_EXPLODE;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleExplosion(this);
    }
}

