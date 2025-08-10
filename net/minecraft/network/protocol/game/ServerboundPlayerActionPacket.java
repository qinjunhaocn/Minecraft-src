/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundPlayerActionPacket
implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundPlayerActionPacket> STREAM_CODEC = Packet.codec(ServerboundPlayerActionPacket::write, ServerboundPlayerActionPacket::new);
    private final BlockPos pos;
    private final Direction direction;
    private final Action action;
    private final int sequence;

    public ServerboundPlayerActionPacket(Action $$0, BlockPos $$1, Direction $$2, int $$3) {
        this.action = $$0;
        this.pos = $$1.immutable();
        this.direction = $$2;
        this.sequence = $$3;
    }

    public ServerboundPlayerActionPacket(Action $$0, BlockPos $$1, Direction $$2) {
        this($$0, $$1, $$2, 0);
    }

    private ServerboundPlayerActionPacket(FriendlyByteBuf $$0) {
        this.action = $$0.readEnum(Action.class);
        this.pos = $$0.readBlockPos();
        this.direction = Direction.from3DDataValue($$0.readUnsignedByte());
        this.sequence = $$0.readVarInt();
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeEnum(this.action);
        $$0.writeBlockPos(this.pos);
        $$0.writeByte(this.direction.get3DDataValue());
        $$0.writeVarInt(this.sequence);
    }

    @Override
    public PacketType<ServerboundPlayerActionPacket> type() {
        return GamePacketTypes.SERVERBOUND_PLAYER_ACTION;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handlePlayerAction(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Action getAction() {
        return this.action;
    }

    public int getSequence() {
        return this.sequence;
    }

    public static final class Action
    extends Enum<Action> {
        public static final /* enum */ Action START_DESTROY_BLOCK = new Action();
        public static final /* enum */ Action ABORT_DESTROY_BLOCK = new Action();
        public static final /* enum */ Action STOP_DESTROY_BLOCK = new Action();
        public static final /* enum */ Action DROP_ALL_ITEMS = new Action();
        public static final /* enum */ Action DROP_ITEM = new Action();
        public static final /* enum */ Action RELEASE_USE_ITEM = new Action();
        public static final /* enum */ Action SWAP_ITEM_WITH_OFFHAND = new Action();
        private static final /* synthetic */ Action[] $VALUES;

        public static Action[] values() {
            return (Action[])$VALUES.clone();
        }

        public static Action valueOf(String $$0) {
            return Enum.valueOf(Action.class, $$0);
        }

        private static /* synthetic */ Action[] a() {
            return new Action[]{START_DESTROY_BLOCK, ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK, DROP_ALL_ITEMS, DROP_ITEM, RELEASE_USE_ITEM, SWAP_ITEM_WITH_OFFHAND};
        }

        static {
            $VALUES = Action.a();
        }
    }
}

