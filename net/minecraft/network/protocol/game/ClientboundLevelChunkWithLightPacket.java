/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLevelChunkWithLightPacket
implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLevelChunkWithLightPacket> STREAM_CODEC = Packet.codec(ClientboundLevelChunkWithLightPacket::write, ClientboundLevelChunkWithLightPacket::new);
    private final int x;
    private final int z;
    private final ClientboundLevelChunkPacketData chunkData;
    private final ClientboundLightUpdatePacketData lightData;

    public ClientboundLevelChunkWithLightPacket(LevelChunk $$0, LevelLightEngine $$1, @Nullable BitSet $$2, @Nullable BitSet $$3) {
        ChunkPos $$4 = $$0.getPos();
        this.x = $$4.x;
        this.z = $$4.z;
        this.chunkData = new ClientboundLevelChunkPacketData($$0);
        this.lightData = new ClientboundLightUpdatePacketData($$4, $$1, $$2, $$3);
    }

    private ClientboundLevelChunkWithLightPacket(RegistryFriendlyByteBuf $$0) {
        this.x = $$0.readInt();
        this.z = $$0.readInt();
        this.chunkData = new ClientboundLevelChunkPacketData($$0, this.x, this.z);
        this.lightData = new ClientboundLightUpdatePacketData($$0, this.x, this.z);
    }

    private void write(RegistryFriendlyByteBuf $$0) {
        $$0.writeInt(this.x);
        $$0.writeInt(this.z);
        this.chunkData.write($$0);
        this.lightData.write($$0);
    }

    @Override
    public PacketType<ClientboundLevelChunkWithLightPacket> type() {
        return GamePacketTypes.CLIENTBOUND_LEVEL_CHUNK_WITH_LIGHT;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleLevelChunkWithLight(this);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public ClientboundLevelChunkPacketData getChunkData() {
        return this.chunkData;
    }

    public ClientboundLightUpdatePacketData getLightData() {
        return this.lightData;
    }
}

