/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;

public record BreezeDebugPayload(BreezeInfo breezeInfo) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, BreezeDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(BreezeDebugPayload::write, BreezeDebugPayload::new);
    public static final CustomPacketPayload.Type<BreezeDebugPayload> TYPE = CustomPacketPayload.createType("debug/breeze");

    private BreezeDebugPayload(FriendlyByteBuf $$0) {
        this(new BreezeInfo($$0));
    }

    private void write(FriendlyByteBuf $$0) {
        this.breezeInfo.write($$0);
    }

    public CustomPacketPayload.Type<BreezeDebugPayload> type() {
        return TYPE;
    }

    public record BreezeInfo(UUID uuid, int id, Integer attackTarget, BlockPos jumpTarget) {
        public BreezeInfo(FriendlyByteBuf $$0) {
            this($$0.readUUID(), $$0.readInt(), $$0.readNullable(FriendlyByteBuf::readInt), $$0.readNullable(BlockPos.STREAM_CODEC));
        }

        public void write(FriendlyByteBuf $$0) {
            $$0.writeUUID(this.uuid);
            $$0.writeInt(this.id);
            $$0.writeNullable(this.attackTarget, FriendlyByteBuf::writeInt);
            $$0.writeNullable(this.jumpTarget, BlockPos.STREAM_CODEC);
        }

        public String generateName() {
            return DebugEntityNameGenerator.getEntityName(this.uuid);
        }

        public String toString() {
            return this.generateName();
        }
    }
}

