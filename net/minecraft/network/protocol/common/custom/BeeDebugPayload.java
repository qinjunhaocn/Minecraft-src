/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public record BeeDebugPayload(BeeInfo beeInfo) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, BeeDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(BeeDebugPayload::write, BeeDebugPayload::new);
    public static final CustomPacketPayload.Type<BeeDebugPayload> TYPE = CustomPacketPayload.createType("debug/bee");

    private BeeDebugPayload(FriendlyByteBuf $$0) {
        this(new BeeInfo($$0));
    }

    private void write(FriendlyByteBuf $$0) {
        this.beeInfo.write($$0);
    }

    public CustomPacketPayload.Type<BeeDebugPayload> type() {
        return TYPE;
    }

    public record BeeInfo(UUID uuid, int id, Vec3 pos, @Nullable Path path, @Nullable BlockPos hivePos, @Nullable BlockPos flowerPos, int travelTicks, Set<String> goals, List<BlockPos> blacklistedHives) {
        public BeeInfo(FriendlyByteBuf $$0) {
            this($$0.readUUID(), $$0.readInt(), $$0.readVec3(), $$0.readNullable(Path::createFromStream), $$0.readNullable(BlockPos.STREAM_CODEC), $$0.readNullable(BlockPos.STREAM_CODEC), $$0.readInt(), $$0.readCollection(HashSet::new, FriendlyByteBuf::readUtf), $$0.readList(BlockPos.STREAM_CODEC));
        }

        public void write(FriendlyByteBuf $$02) {
            $$02.writeUUID(this.uuid);
            $$02.writeInt(this.id);
            $$02.writeVec3(this.pos);
            $$02.writeNullable(this.path, ($$0, $$1) -> $$1.writeToStream((FriendlyByteBuf)((Object)$$0)));
            $$02.writeNullable(this.hivePos, BlockPos.STREAM_CODEC);
            $$02.writeNullable(this.flowerPos, BlockPos.STREAM_CODEC);
            $$02.writeInt(this.travelTicks);
            $$02.writeCollection(this.goals, FriendlyByteBuf::writeUtf);
            $$02.writeCollection(this.blacklistedHives, BlockPos.STREAM_CODEC);
        }

        public boolean hasHive(BlockPos $$0) {
            return Objects.equals($$0, this.hivePos);
        }

        public String generateName() {
            return DebugEntityNameGenerator.getEntityName(this.uuid);
        }

        public String toString() {
            return this.generateName();
        }

        @Nullable
        public Path path() {
            return this.path;
        }

        @Nullable
        public BlockPos hivePos() {
            return this.hivePos;
        }

        @Nullable
        public BlockPos flowerPos() {
            return this.flowerPos;
        }
    }
}

