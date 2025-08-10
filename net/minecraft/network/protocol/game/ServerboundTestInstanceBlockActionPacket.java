/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;

public record ServerboundTestInstanceBlockActionPacket(BlockPos pos, Action action, TestInstanceBlockEntity.Data data) implements Packet<ServerGamePacketListener>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundTestInstanceBlockActionPacket> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, ServerboundTestInstanceBlockActionPacket::pos, Action.STREAM_CODEC, ServerboundTestInstanceBlockActionPacket::action, TestInstanceBlockEntity.Data.STREAM_CODEC, ServerboundTestInstanceBlockActionPacket::data, ServerboundTestInstanceBlockActionPacket::new);

    public ServerboundTestInstanceBlockActionPacket(BlockPos $$0, Action $$1, Optional<ResourceKey<GameTestInstance>> $$2, Vec3i $$3, Rotation $$4, boolean $$5) {
        this($$0, $$1, new TestInstanceBlockEntity.Data($$2, $$3, $$4, $$5, TestInstanceBlockEntity.Status.CLEARED, Optional.empty()));
    }

    @Override
    public PacketType<ServerboundTestInstanceBlockActionPacket> type() {
        return GamePacketTypes.SERVERBOUND_TEST_INSTANCE_BLOCK_ACTION;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleTestInstanceBlockAction(this);
    }

    public static final class Action
    extends Enum<Action> {
        public static final /* enum */ Action INIT = new Action(0);
        public static final /* enum */ Action QUERY = new Action(1);
        public static final /* enum */ Action SET = new Action(2);
        public static final /* enum */ Action RESET = new Action(3);
        public static final /* enum */ Action SAVE = new Action(4);
        public static final /* enum */ Action EXPORT = new Action(5);
        public static final /* enum */ Action RUN = new Action(6);
        private static final IntFunction<Action> BY_ID;
        public static final StreamCodec<ByteBuf, Action> STREAM_CODEC;
        private final int id;
        private static final /* synthetic */ Action[] $VALUES;

        public static Action[] values() {
            return (Action[])$VALUES.clone();
        }

        public static Action valueOf(String $$0) {
            return Enum.valueOf(Action.class, $$0);
        }

        private Action(int $$0) {
            this.id = $$0;
        }

        private static /* synthetic */ Action[] a() {
            return new Action[]{INIT, QUERY, SET, RESET, SAVE, EXPORT, RUN};
        }

        static {
            $VALUES = Action.a();
            BY_ID = ByIdMap.a($$0 -> $$0.id, Action.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
            STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, $$0 -> $$0.id);
        }
    }
}

