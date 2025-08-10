/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public record BrainDebugPayload(BrainDump brainDump) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, BrainDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(BrainDebugPayload::write, BrainDebugPayload::new);
    public static final CustomPacketPayload.Type<BrainDebugPayload> TYPE = CustomPacketPayload.createType("debug/brain");

    private BrainDebugPayload(FriendlyByteBuf $$0) {
        this(new BrainDump($$0));
    }

    private void write(FriendlyByteBuf $$0) {
        this.brainDump.write($$0);
    }

    public CustomPacketPayload.Type<BrainDebugPayload> type() {
        return TYPE;
    }

    public record BrainDump(UUID uuid, int id, String name, String profession, int xp, float health, float maxHealth, Vec3 pos, String inventory, @Nullable Path path, boolean wantsGolem, int angerLevel, List<String> activities, List<String> behaviors, List<String> memories, List<String> gossips, Set<BlockPos> pois, Set<BlockPos> potentialPois) {
        public BrainDump(FriendlyByteBuf $$0) {
            this($$0.readUUID(), $$0.readInt(), $$0.readUtf(), $$0.readUtf(), $$0.readInt(), $$0.readFloat(), $$0.readFloat(), $$0.readVec3(), $$0.readUtf(), $$0.readNullable(Path::createFromStream), $$0.readBoolean(), $$0.readInt(), $$0.readList(FriendlyByteBuf::readUtf), $$0.readList(FriendlyByteBuf::readUtf), $$0.readList(FriendlyByteBuf::readUtf), $$0.readList(FriendlyByteBuf::readUtf), $$0.readCollection(HashSet::new, BlockPos.STREAM_CODEC), $$0.readCollection(HashSet::new, BlockPos.STREAM_CODEC));
        }

        public void write(FriendlyByteBuf $$02) {
            $$02.writeUUID(this.uuid);
            $$02.writeInt(this.id);
            $$02.writeUtf(this.name);
            $$02.writeUtf(this.profession);
            $$02.writeInt(this.xp);
            $$02.writeFloat(this.health);
            $$02.writeFloat(this.maxHealth);
            $$02.writeVec3(this.pos);
            $$02.writeUtf(this.inventory);
            $$02.writeNullable(this.path, ($$0, $$1) -> $$1.writeToStream((FriendlyByteBuf)((Object)$$0)));
            $$02.writeBoolean(this.wantsGolem);
            $$02.writeInt(this.angerLevel);
            $$02.writeCollection(this.activities, FriendlyByteBuf::writeUtf);
            $$02.writeCollection(this.behaviors, FriendlyByteBuf::writeUtf);
            $$02.writeCollection(this.memories, FriendlyByteBuf::writeUtf);
            $$02.writeCollection(this.gossips, FriendlyByteBuf::writeUtf);
            $$02.writeCollection(this.pois, BlockPos.STREAM_CODEC);
            $$02.writeCollection(this.potentialPois, BlockPos.STREAM_CODEC);
        }

        public boolean hasPoi(BlockPos $$0) {
            return this.pois.contains($$0);
        }

        public boolean hasPotentialPoi(BlockPos $$0) {
            return this.potentialPois.contains($$0);
        }

        @Nullable
        public Path path() {
            return this.path;
        }
    }
}

