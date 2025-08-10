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
import net.minecraft.world.phys.Vec3;

public abstract class ServerboundMovePlayerPacket
implements Packet<ServerGamePacketListener> {
    private static final int FLAG_ON_GROUND = 1;
    private static final int FLAG_HORIZONTAL_COLLISION = 2;
    protected final double x;
    protected final double y;
    protected final double z;
    protected final float yRot;
    protected final float xRot;
    protected final boolean onGround;
    protected final boolean horizontalCollision;
    protected final boolean hasPos;
    protected final boolean hasRot;

    static int packFlags(boolean $$0, boolean $$1) {
        int $$2 = 0;
        if ($$0) {
            $$2 |= 1;
        }
        if ($$1) {
            $$2 |= 2;
        }
        return $$2;
    }

    static boolean unpackOnGround(int $$0) {
        return ($$0 & 1) != 0;
    }

    static boolean unpackHorizontalCollision(int $$0) {
        return ($$0 & 2) != 0;
    }

    protected ServerboundMovePlayerPacket(double $$0, double $$1, double $$2, float $$3, float $$4, boolean $$5, boolean $$6, boolean $$7, boolean $$8) {
        this.x = $$0;
        this.y = $$1;
        this.z = $$2;
        this.yRot = $$3;
        this.xRot = $$4;
        this.onGround = $$5;
        this.horizontalCollision = $$6;
        this.hasPos = $$7;
        this.hasRot = $$8;
    }

    @Override
    public abstract PacketType<? extends ServerboundMovePlayerPacket> type();

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleMovePlayer(this);
    }

    public double getX(double $$0) {
        return this.hasPos ? this.x : $$0;
    }

    public double getY(double $$0) {
        return this.hasPos ? this.y : $$0;
    }

    public double getZ(double $$0) {
        return this.hasPos ? this.z : $$0;
    }

    public float getYRot(float $$0) {
        return this.hasRot ? this.yRot : $$0;
    }

    public float getXRot(float $$0) {
        return this.hasRot ? this.xRot : $$0;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public boolean horizontalCollision() {
        return this.horizontalCollision;
    }

    public boolean hasPosition() {
        return this.hasPos;
    }

    public boolean hasRotation() {
        return this.hasRot;
    }

    public static class StatusOnly
    extends ServerboundMovePlayerPacket {
        public static final StreamCodec<FriendlyByteBuf, StatusOnly> STREAM_CODEC = Packet.codec(StatusOnly::write, StatusOnly::read);

        public StatusOnly(boolean $$0, boolean $$1) {
            super(0.0, 0.0, 0.0, 0.0f, 0.0f, $$0, $$1, false, false);
        }

        private static StatusOnly read(FriendlyByteBuf $$0) {
            short $$1 = $$0.readUnsignedByte();
            boolean $$2 = ServerboundMovePlayerPacket.unpackOnGround($$1);
            boolean $$3 = ServerboundMovePlayerPacket.unpackHorizontalCollision($$1);
            return new StatusOnly($$2, $$3);
        }

        private void write(FriendlyByteBuf $$0) {
            $$0.writeByte(ServerboundMovePlayerPacket.packFlags(this.onGround, this.horizontalCollision));
        }

        @Override
        public PacketType<StatusOnly> type() {
            return GamePacketTypes.SERVERBOUND_MOVE_PLAYER_STATUS_ONLY;
        }
    }

    public static class Rot
    extends ServerboundMovePlayerPacket {
        public static final StreamCodec<FriendlyByteBuf, Rot> STREAM_CODEC = Packet.codec(Rot::write, Rot::read);

        public Rot(float $$0, float $$1, boolean $$2, boolean $$3) {
            super(0.0, 0.0, 0.0, $$0, $$1, $$2, $$3, false, true);
        }

        private static Rot read(FriendlyByteBuf $$0) {
            float $$1 = $$0.readFloat();
            float $$2 = $$0.readFloat();
            short $$3 = $$0.readUnsignedByte();
            boolean $$4 = ServerboundMovePlayerPacket.unpackOnGround($$3);
            boolean $$5 = ServerboundMovePlayerPacket.unpackHorizontalCollision($$3);
            return new Rot($$1, $$2, $$4, $$5);
        }

        private void write(FriendlyByteBuf $$0) {
            $$0.writeFloat(this.yRot);
            $$0.writeFloat(this.xRot);
            $$0.writeByte(ServerboundMovePlayerPacket.packFlags(this.onGround, this.horizontalCollision));
        }

        @Override
        public PacketType<Rot> type() {
            return GamePacketTypes.SERVERBOUND_MOVE_PLAYER_ROT;
        }
    }

    public static class Pos
    extends ServerboundMovePlayerPacket {
        public static final StreamCodec<FriendlyByteBuf, Pos> STREAM_CODEC = Packet.codec(Pos::write, Pos::read);

        public Pos(Vec3 $$0, boolean $$1, boolean $$2) {
            super($$0.x, $$0.y, $$0.z, 0.0f, 0.0f, $$1, $$2, true, false);
        }

        public Pos(double $$0, double $$1, double $$2, boolean $$3, boolean $$4) {
            super($$0, $$1, $$2, 0.0f, 0.0f, $$3, $$4, true, false);
        }

        private static Pos read(FriendlyByteBuf $$0) {
            double $$1 = $$0.readDouble();
            double $$2 = $$0.readDouble();
            double $$3 = $$0.readDouble();
            short $$4 = $$0.readUnsignedByte();
            boolean $$5 = ServerboundMovePlayerPacket.unpackOnGround($$4);
            boolean $$6 = ServerboundMovePlayerPacket.unpackHorizontalCollision($$4);
            return new Pos($$1, $$2, $$3, $$5, $$6);
        }

        private void write(FriendlyByteBuf $$0) {
            $$0.writeDouble(this.x);
            $$0.writeDouble(this.y);
            $$0.writeDouble(this.z);
            $$0.writeByte(ServerboundMovePlayerPacket.packFlags(this.onGround, this.horizontalCollision));
        }

        @Override
        public PacketType<Pos> type() {
            return GamePacketTypes.SERVERBOUND_MOVE_PLAYER_POS;
        }
    }

    public static class PosRot
    extends ServerboundMovePlayerPacket {
        public static final StreamCodec<FriendlyByteBuf, PosRot> STREAM_CODEC = Packet.codec(PosRot::write, PosRot::read);

        public PosRot(Vec3 $$0, float $$1, float $$2, boolean $$3, boolean $$4) {
            super($$0.x, $$0.y, $$0.z, $$1, $$2, $$3, $$4, true, true);
        }

        public PosRot(double $$0, double $$1, double $$2, float $$3, float $$4, boolean $$5, boolean $$6) {
            super($$0, $$1, $$2, $$3, $$4, $$5, $$6, true, true);
        }

        private static PosRot read(FriendlyByteBuf $$0) {
            double $$1 = $$0.readDouble();
            double $$2 = $$0.readDouble();
            double $$3 = $$0.readDouble();
            float $$4 = $$0.readFloat();
            float $$5 = $$0.readFloat();
            short $$6 = $$0.readUnsignedByte();
            boolean $$7 = ServerboundMovePlayerPacket.unpackOnGround($$6);
            boolean $$8 = ServerboundMovePlayerPacket.unpackHorizontalCollision($$6);
            return new PosRot($$1, $$2, $$3, $$4, $$5, $$7, $$8);
        }

        private void write(FriendlyByteBuf $$0) {
            $$0.writeDouble(this.x);
            $$0.writeDouble(this.y);
            $$0.writeDouble(this.z);
            $$0.writeFloat(this.yRot);
            $$0.writeFloat(this.xRot);
            $$0.writeByte(ServerboundMovePlayerPacket.packFlags(this.onGround, this.horizontalCollision));
        }

        @Override
        public PacketType<PosRot> type() {
            return GamePacketTypes.SERVERBOUND_MOVE_PLAYER_POS_ROT;
        }
    }
}

