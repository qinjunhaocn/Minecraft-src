/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  io.netty.buffer.ByteBuf
 *  org.apache.commons.lang3.function.TriFunction
 */
package net.minecraft.world.waypoints;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.Waypoint;
import org.apache.commons.lang3.function.TriFunction;
import org.slf4j.Logger;

public abstract class TrackedWaypoint
implements Waypoint {
    static final Logger LOGGER = LogUtils.getLogger();
    public static StreamCodec<ByteBuf, TrackedWaypoint> STREAM_CODEC = StreamCodec.ofMember(TrackedWaypoint::write, TrackedWaypoint::read);
    protected final Either<UUID, String> identifier;
    private final Waypoint.Icon icon;
    private final Type type;

    TrackedWaypoint(Either<UUID, String> $$0, Waypoint.Icon $$1, Type $$2) {
        this.identifier = $$0;
        this.icon = $$1;
        this.type = $$2;
    }

    public Either<UUID, String> id() {
        return this.identifier;
    }

    public abstract void update(TrackedWaypoint var1);

    public void write(ByteBuf $$0) {
        FriendlyByteBuf $$1 = new FriendlyByteBuf($$0);
        $$1.writeEither(this.identifier, UUIDUtil.STREAM_CODEC, FriendlyByteBuf::writeUtf);
        Waypoint.Icon.STREAM_CODEC.encode($$1, this.icon);
        $$1.writeEnum(this.type);
        this.writeContents($$0);
    }

    public abstract void writeContents(ByteBuf var1);

    private static TrackedWaypoint read(ByteBuf $$0) {
        FriendlyByteBuf $$1 = new FriendlyByteBuf($$0);
        Either<UUID, String> $$2 = $$1.readEither(UUIDUtil.STREAM_CODEC, FriendlyByteBuf::readUtf);
        Waypoint.Icon $$3 = (Waypoint.Icon)Waypoint.Icon.STREAM_CODEC.decode($$1);
        Type $$4 = $$1.readEnum(Type.class);
        return (TrackedWaypoint)$$4.constructor.apply($$2, (Object)$$3, (Object)$$1);
    }

    public static TrackedWaypoint setPosition(UUID $$0, Waypoint.Icon $$1, Vec3i $$2) {
        return new Vec3iWaypoint($$0, $$1, $$2);
    }

    public static TrackedWaypoint setChunk(UUID $$0, Waypoint.Icon $$1, ChunkPos $$2) {
        return new ChunkWaypoint($$0, $$1, $$2);
    }

    public static TrackedWaypoint setAzimuth(UUID $$0, Waypoint.Icon $$1, float $$2) {
        return new AzimuthWaypoint($$0, $$1, $$2);
    }

    public static TrackedWaypoint empty(UUID $$0) {
        return new EmptyWaypoint($$0);
    }

    public abstract double yawAngleToCamera(Level var1, Camera var2);

    public abstract PitchDirection pitchDirectionToCamera(Level var1, Projector var2);

    public abstract double distanceSquared(Entity var1);

    public Waypoint.Icon icon() {
        return this.icon;
    }

    static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type EMPTY = new Type((TriFunction<Either<UUID, String>, Waypoint.Icon, FriendlyByteBuf, TrackedWaypoint>)((TriFunction)EmptyWaypoint::new));
        public static final /* enum */ Type VEC3I = new Type((TriFunction<Either<UUID, String>, Waypoint.Icon, FriendlyByteBuf, TrackedWaypoint>)((TriFunction)Vec3iWaypoint::new));
        public static final /* enum */ Type CHUNK = new Type((TriFunction<Either<UUID, String>, Waypoint.Icon, FriendlyByteBuf, TrackedWaypoint>)((TriFunction)ChunkWaypoint::new));
        public static final /* enum */ Type AZIMUTH = new Type((TriFunction<Either<UUID, String>, Waypoint.Icon, FriendlyByteBuf, TrackedWaypoint>)((TriFunction)AzimuthWaypoint::new));
        final TriFunction<Either<UUID, String>, Waypoint.Icon, FriendlyByteBuf, TrackedWaypoint> constructor;
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private Type(TriFunction<Either<UUID, String>, Waypoint.Icon, FriendlyByteBuf, TrackedWaypoint> $$0) {
            this.constructor = $$0;
        }

        private static /* synthetic */ Type[] a() {
            return new Type[]{EMPTY, VEC3I, CHUNK, AZIMUTH};
        }

        static {
            $VALUES = Type.a();
        }
    }

    static class Vec3iWaypoint
    extends TrackedWaypoint {
        private Vec3i vector;

        public Vec3iWaypoint(UUID $$0, Waypoint.Icon $$1, Vec3i $$2) {
            super((Either<UUID, String>)Either.left((Object)$$0), $$1, Type.VEC3I);
            this.vector = $$2;
        }

        public Vec3iWaypoint(Either<UUID, String> $$0, Waypoint.Icon $$1, FriendlyByteBuf $$2) {
            super($$0, $$1, Type.VEC3I);
            this.vector = new Vec3i($$2.readVarInt(), $$2.readVarInt(), $$2.readVarInt());
        }

        @Override
        public void update(TrackedWaypoint $$0) {
            if ($$0 instanceof Vec3iWaypoint) {
                Vec3iWaypoint $$1 = (Vec3iWaypoint)$$0;
                this.vector = $$1.vector;
            } else {
                LOGGER.warn("Unsupported Waypoint update operation: {}", (Object)$$0.getClass());
            }
        }

        @Override
        public void writeContents(ByteBuf $$0) {
            VarInt.write($$0, this.vector.getX());
            VarInt.write($$0, this.vector.getY());
            VarInt.write($$0, this.vector.getZ());
        }

        private Vec3 position(Level $$02) {
            return this.identifier.left().map($$02::getEntity).map($$0 -> {
                if ($$0.blockPosition().distManhattan(this.vector) > 3) {
                    return null;
                }
                return $$0.getEyePosition();
            }).orElseGet(() -> Vec3.atCenterOf(this.vector));
        }

        @Override
        public double yawAngleToCamera(Level $$0, Camera $$1) {
            Vec3 $$2 = $$1.position().subtract(this.position($$0)).rotateClockwise90();
            float $$3 = (float)Mth.atan2($$2.z(), $$2.x()) * 57.295776f;
            return Mth.degreesDifference($$1.yaw(), $$3);
        }

        @Override
        public PitchDirection pitchDirectionToCamera(Level $$0, Projector $$1) {
            double $$4;
            Vec3 $$2 = $$1.projectPointToScreen(this.position($$0));
            boolean $$3 = $$2.z > 1.0;
            double d = $$4 = $$3 ? -$$2.y : $$2.y;
            if ($$4 < -1.0) {
                return PitchDirection.DOWN;
            }
            if ($$4 > 1.0) {
                return PitchDirection.UP;
            }
            if ($$3) {
                if ($$2.y > 0.0) {
                    return PitchDirection.UP;
                }
                if ($$2.y < 0.0) {
                    return PitchDirection.DOWN;
                }
            }
            return PitchDirection.NONE;
        }

        @Override
        public double distanceSquared(Entity $$0) {
            return $$0.distanceToSqr(Vec3.atCenterOf(this.vector));
        }
    }

    static class ChunkWaypoint
    extends TrackedWaypoint {
        private ChunkPos chunkPos;

        public ChunkWaypoint(UUID $$0, Waypoint.Icon $$1, ChunkPos $$2) {
            super((Either<UUID, String>)Either.left((Object)$$0), $$1, Type.CHUNK);
            this.chunkPos = $$2;
        }

        public ChunkWaypoint(Either<UUID, String> $$0, Waypoint.Icon $$1, FriendlyByteBuf $$2) {
            super($$0, $$1, Type.CHUNK);
            this.chunkPos = new ChunkPos($$2.readVarInt(), $$2.readVarInt());
        }

        @Override
        public void update(TrackedWaypoint $$0) {
            if ($$0 instanceof ChunkWaypoint) {
                ChunkWaypoint $$1 = (ChunkWaypoint)$$0;
                this.chunkPos = $$1.chunkPos;
            } else {
                LOGGER.warn("Unsupported Waypoint update operation: {}", (Object)$$0.getClass());
            }
        }

        @Override
        public void writeContents(ByteBuf $$0) {
            VarInt.write($$0, this.chunkPos.x);
            VarInt.write($$0, this.chunkPos.z);
        }

        private Vec3 position(double $$0) {
            return Vec3.atCenterOf(this.chunkPos.getMiddleBlockPosition((int)$$0));
        }

        @Override
        public double yawAngleToCamera(Level $$0, Camera $$1) {
            Vec3 $$2 = $$1.position();
            Vec3 $$3 = $$2.subtract(this.position($$2.y())).rotateClockwise90();
            float $$4 = (float)Mth.atan2($$3.z(), $$3.x()) * 57.295776f;
            return Mth.degreesDifference($$1.yaw(), $$4);
        }

        @Override
        public PitchDirection pitchDirectionToCamera(Level $$0, Projector $$1) {
            double $$2 = $$1.projectHorizonToScreen();
            if ($$2 < -1.0) {
                return PitchDirection.DOWN;
            }
            if ($$2 > 1.0) {
                return PitchDirection.UP;
            }
            return PitchDirection.NONE;
        }

        @Override
        public double distanceSquared(Entity $$0) {
            return $$0.distanceToSqr(Vec3.atCenterOf(this.chunkPos.getMiddleBlockPosition($$0.getBlockY())));
        }
    }

    static class AzimuthWaypoint
    extends TrackedWaypoint {
        private float angle;

        public AzimuthWaypoint(UUID $$0, Waypoint.Icon $$1, float $$2) {
            super((Either<UUID, String>)Either.left((Object)$$0), $$1, Type.AZIMUTH);
            this.angle = $$2;
        }

        public AzimuthWaypoint(Either<UUID, String> $$0, Waypoint.Icon $$1, FriendlyByteBuf $$2) {
            super($$0, $$1, Type.AZIMUTH);
            this.angle = $$2.readFloat();
        }

        @Override
        public void update(TrackedWaypoint $$0) {
            if ($$0 instanceof AzimuthWaypoint) {
                AzimuthWaypoint $$1 = (AzimuthWaypoint)$$0;
                this.angle = $$1.angle;
            } else {
                LOGGER.warn("Unsupported Waypoint update operation: {}", (Object)$$0.getClass());
            }
        }

        @Override
        public void writeContents(ByteBuf $$0) {
            $$0.writeFloat(this.angle);
        }

        @Override
        public double yawAngleToCamera(Level $$0, Camera $$1) {
            return Mth.degreesDifference($$1.yaw(), this.angle * 57.295776f);
        }

        @Override
        public PitchDirection pitchDirectionToCamera(Level $$0, Projector $$1) {
            double $$2 = $$1.projectHorizonToScreen();
            if ($$2 < -1.0) {
                return PitchDirection.DOWN;
            }
            if ($$2 > 1.0) {
                return PitchDirection.UP;
            }
            return PitchDirection.NONE;
        }

        @Override
        public double distanceSquared(Entity $$0) {
            return Double.POSITIVE_INFINITY;
        }
    }

    static class EmptyWaypoint
    extends TrackedWaypoint {
        private EmptyWaypoint(Either<UUID, String> $$0, Waypoint.Icon $$1, FriendlyByteBuf $$2) {
            super($$0, $$1, Type.EMPTY);
        }

        EmptyWaypoint(UUID $$0) {
            super((Either<UUID, String>)Either.left((Object)$$0), Waypoint.Icon.NULL, Type.EMPTY);
        }

        @Override
        public void update(TrackedWaypoint $$0) {
        }

        @Override
        public void writeContents(ByteBuf $$0) {
        }

        @Override
        public double yawAngleToCamera(Level $$0, Camera $$1) {
            return Double.NaN;
        }

        @Override
        public PitchDirection pitchDirectionToCamera(Level $$0, Projector $$1) {
            return PitchDirection.NONE;
        }

        @Override
        public double distanceSquared(Entity $$0) {
            return Double.POSITIVE_INFINITY;
        }
    }

    public static interface Camera {
        public float yaw();

        public Vec3 position();
    }

    public static interface Projector {
        public Vec3 projectPointToScreen(Vec3 var1);

        public double projectHorizonToScreen();
    }

    public static final class PitchDirection
    extends Enum<PitchDirection> {
        public static final /* enum */ PitchDirection NONE = new PitchDirection();
        public static final /* enum */ PitchDirection UP = new PitchDirection();
        public static final /* enum */ PitchDirection DOWN = new PitchDirection();
        private static final /* synthetic */ PitchDirection[] $VALUES;

        public static PitchDirection[] values() {
            return (PitchDirection[])$VALUES.clone();
        }

        public static PitchDirection valueOf(String $$0) {
            return Enum.valueOf(PitchDirection.class, $$0);
        }

        private static /* synthetic */ PitchDirection[] a() {
            return new PitchDirection[]{NONE, UP, DOWN};
        }

        static {
            $VALUES = PitchDirection.a();
        }
    }
}

