/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  org.joml.Vector3f
 */
package net.minecraft.world.phys;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector3f;

public class Vec3
implements Position {
    public static final Codec<Vec3> CODEC = Codec.DOUBLE.listOf().comapFlatMap($$02 -> Util.fixedSize($$02, 3).map($$0 -> new Vec3((Double)$$0.get(0), (Double)$$0.get(1), (Double)$$0.get(2))), $$0 -> List.of((Object)$$0.x(), (Object)$$0.y(), (Object)$$0.z()));
    public static final StreamCodec<ByteBuf, Vec3> STREAM_CODEC = new StreamCodec<ByteBuf, Vec3>(){

        @Override
        public Vec3 decode(ByteBuf $$0) {
            return FriendlyByteBuf.readVec3($$0);
        }

        @Override
        public void encode(ByteBuf $$0, Vec3 $$1) {
            FriendlyByteBuf.writeVec3($$0, $$1);
        }

        @Override
        public /* synthetic */ void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Vec3)object2);
        }

        @Override
        public /* synthetic */ Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    public static final Vec3 ZERO = new Vec3(0.0, 0.0, 0.0);
    public final double x;
    public final double y;
    public final double z;

    public static Vec3 fromRGB24(int $$0) {
        double $$1 = (double)($$0 >> 16 & 0xFF) / 255.0;
        double $$2 = (double)($$0 >> 8 & 0xFF) / 255.0;
        double $$3 = (double)($$0 & 0xFF) / 255.0;
        return new Vec3($$1, $$2, $$3);
    }

    public static Vec3 atLowerCornerOf(Vec3i $$0) {
        return new Vec3($$0.getX(), $$0.getY(), $$0.getZ());
    }

    public static Vec3 atLowerCornerWithOffset(Vec3i $$0, double $$1, double $$2, double $$3) {
        return new Vec3((double)$$0.getX() + $$1, (double)$$0.getY() + $$2, (double)$$0.getZ() + $$3);
    }

    public static Vec3 atCenterOf(Vec3i $$0) {
        return Vec3.atLowerCornerWithOffset($$0, 0.5, 0.5, 0.5);
    }

    public static Vec3 atBottomCenterOf(Vec3i $$0) {
        return Vec3.atLowerCornerWithOffset($$0, 0.5, 0.0, 0.5);
    }

    public static Vec3 upFromBottomCenterOf(Vec3i $$0, double $$1) {
        return Vec3.atLowerCornerWithOffset($$0, 0.5, $$1, 0.5);
    }

    public Vec3(double $$0, double $$1, double $$2) {
        this.x = $$0;
        this.y = $$1;
        this.z = $$2;
    }

    public Vec3(Vector3f $$0) {
        this($$0.x(), $$0.y(), $$0.z());
    }

    public Vec3(Vec3i $$0) {
        this($$0.getX(), $$0.getY(), $$0.getZ());
    }

    public Vec3 vectorTo(Vec3 $$0) {
        return new Vec3($$0.x - this.x, $$0.y - this.y, $$0.z - this.z);
    }

    public Vec3 normalize() {
        double $$0 = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        if ($$0 < (double)1.0E-5f) {
            return ZERO;
        }
        return new Vec3(this.x / $$0, this.y / $$0, this.z / $$0);
    }

    public double dot(Vec3 $$0) {
        return this.x * $$0.x + this.y * $$0.y + this.z * $$0.z;
    }

    public Vec3 cross(Vec3 $$0) {
        return new Vec3(this.y * $$0.z - this.z * $$0.y, this.z * $$0.x - this.x * $$0.z, this.x * $$0.y - this.y * $$0.x);
    }

    public Vec3 subtract(Vec3 $$0) {
        return this.subtract($$0.x, $$0.y, $$0.z);
    }

    public Vec3 subtract(double $$0) {
        return this.subtract($$0, $$0, $$0);
    }

    public Vec3 subtract(double $$0, double $$1, double $$2) {
        return this.add(-$$0, -$$1, -$$2);
    }

    public Vec3 add(double $$0) {
        return this.add($$0, $$0, $$0);
    }

    public Vec3 add(Vec3 $$0) {
        return this.add($$0.x, $$0.y, $$0.z);
    }

    public Vec3 add(double $$0, double $$1, double $$2) {
        return new Vec3(this.x + $$0, this.y + $$1, this.z + $$2);
    }

    public boolean closerThan(Position $$0, double $$1) {
        return this.distanceToSqr($$0.x(), $$0.y(), $$0.z()) < $$1 * $$1;
    }

    public double distanceTo(Vec3 $$0) {
        double $$1 = $$0.x - this.x;
        double $$2 = $$0.y - this.y;
        double $$3 = $$0.z - this.z;
        return Math.sqrt($$1 * $$1 + $$2 * $$2 + $$3 * $$3);
    }

    public double distanceToSqr(Vec3 $$0) {
        double $$1 = $$0.x - this.x;
        double $$2 = $$0.y - this.y;
        double $$3 = $$0.z - this.z;
        return $$1 * $$1 + $$2 * $$2 + $$3 * $$3;
    }

    public double distanceToSqr(double $$0, double $$1, double $$2) {
        double $$3 = $$0 - this.x;
        double $$4 = $$1 - this.y;
        double $$5 = $$2 - this.z;
        return $$3 * $$3 + $$4 * $$4 + $$5 * $$5;
    }

    public boolean closerThan(Vec3 $$0, double $$1, double $$2) {
        double $$3 = $$0.x() - this.x;
        double $$4 = $$0.y() - this.y;
        double $$5 = $$0.z() - this.z;
        return Mth.lengthSquared($$3, $$5) < Mth.square($$1) && Math.abs($$4) < $$2;
    }

    public Vec3 scale(double $$0) {
        return this.multiply($$0, $$0, $$0);
    }

    public Vec3 reverse() {
        return this.scale(-1.0);
    }

    public Vec3 multiply(Vec3 $$0) {
        return this.multiply($$0.x, $$0.y, $$0.z);
    }

    public Vec3 multiply(double $$0, double $$1, double $$2) {
        return new Vec3(this.x * $$0, this.y * $$1, this.z * $$2);
    }

    public Vec3 horizontal() {
        return new Vec3(this.x, 0.0, this.z);
    }

    public Vec3 offsetRandom(RandomSource $$0, float $$1) {
        return this.add(($$0.nextFloat() - 0.5f) * $$1, ($$0.nextFloat() - 0.5f) * $$1, ($$0.nextFloat() - 0.5f) * $$1);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double lengthSqr() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double horizontalDistance() {
        return Math.sqrt(this.x * this.x + this.z * this.z);
    }

    public double horizontalDistanceSqr() {
        return this.x * this.x + this.z * this.z;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof Vec3)) {
            return false;
        }
        Vec3 $$1 = (Vec3)$$0;
        if (Double.compare($$1.x, this.x) != 0) {
            return false;
        }
        if (Double.compare($$1.y, this.y) != 0) {
            return false;
        }
        return Double.compare($$1.z, this.z) == 0;
    }

    public int hashCode() {
        long $$0 = Double.doubleToLongBits(this.x);
        int $$1 = (int)($$0 ^ $$0 >>> 32);
        $$0 = Double.doubleToLongBits(this.y);
        $$1 = 31 * $$1 + (int)($$0 ^ $$0 >>> 32);
        $$0 = Double.doubleToLongBits(this.z);
        $$1 = 31 * $$1 + (int)($$0 ^ $$0 >>> 32);
        return $$1;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public Vec3 lerp(Vec3 $$0, double $$1) {
        return new Vec3(Mth.lerp($$1, this.x, $$0.x), Mth.lerp($$1, this.y, $$0.y), Mth.lerp($$1, this.z, $$0.z));
    }

    public Vec3 xRot(float $$0) {
        float $$1 = Mth.cos($$0);
        float $$2 = Mth.sin($$0);
        double $$3 = this.x;
        double $$4 = this.y * (double)$$1 + this.z * (double)$$2;
        double $$5 = this.z * (double)$$1 - this.y * (double)$$2;
        return new Vec3($$3, $$4, $$5);
    }

    public Vec3 yRot(float $$0) {
        float $$1 = Mth.cos($$0);
        float $$2 = Mth.sin($$0);
        double $$3 = this.x * (double)$$1 + this.z * (double)$$2;
        double $$4 = this.y;
        double $$5 = this.z * (double)$$1 - this.x * (double)$$2;
        return new Vec3($$3, $$4, $$5);
    }

    public Vec3 zRot(float $$0) {
        float $$1 = Mth.cos($$0);
        float $$2 = Mth.sin($$0);
        double $$3 = this.x * (double)$$1 + this.y * (double)$$2;
        double $$4 = this.y * (double)$$1 - this.x * (double)$$2;
        double $$5 = this.z;
        return new Vec3($$3, $$4, $$5);
    }

    public Vec3 rotateClockwise90() {
        return new Vec3(-this.z, this.y, this.x);
    }

    public static Vec3 directionFromRotation(Vec2 $$0) {
        return Vec3.directionFromRotation($$0.x, $$0.y);
    }

    public static Vec3 directionFromRotation(float $$0, float $$1) {
        float $$2 = Mth.cos(-$$1 * ((float)Math.PI / 180) - (float)Math.PI);
        float $$3 = Mth.sin(-$$1 * ((float)Math.PI / 180) - (float)Math.PI);
        float $$4 = -Mth.cos(-$$0 * ((float)Math.PI / 180));
        float $$5 = Mth.sin(-$$0 * ((float)Math.PI / 180));
        return new Vec3($$3 * $$4, $$5, $$2 * $$4);
    }

    public Vec3 align(EnumSet<Direction.Axis> $$0) {
        double $$1 = $$0.contains(Direction.Axis.X) ? (double)Mth.floor(this.x) : this.x;
        double $$2 = $$0.contains(Direction.Axis.Y) ? (double)Mth.floor(this.y) : this.y;
        double $$3 = $$0.contains(Direction.Axis.Z) ? (double)Mth.floor(this.z) : this.z;
        return new Vec3($$1, $$2, $$3);
    }

    public double get(Direction.Axis $$0) {
        return $$0.choose(this.x, this.y, this.z);
    }

    public Vec3 with(Direction.Axis $$0, double $$1) {
        double $$2 = $$0 == Direction.Axis.X ? $$1 : this.x;
        double $$3 = $$0 == Direction.Axis.Y ? $$1 : this.y;
        double $$4 = $$0 == Direction.Axis.Z ? $$1 : this.z;
        return new Vec3($$2, $$3, $$4);
    }

    public Vec3 relative(Direction $$0, double $$1) {
        Vec3i $$2 = $$0.getUnitVec3i();
        return new Vec3(this.x + $$1 * (double)$$2.getX(), this.y + $$1 * (double)$$2.getY(), this.z + $$1 * (double)$$2.getZ());
    }

    @Override
    public final double x() {
        return this.x;
    }

    @Override
    public final double y() {
        return this.y;
    }

    @Override
    public final double z() {
        return this.z;
    }

    public Vector3f toVector3f() {
        return new Vector3f((float)this.x, (float)this.y, (float)this.z);
    }

    public Vec3 projectedOn(Vec3 $$0) {
        if ($$0.lengthSqr() == 0.0) {
            return $$0;
        }
        return $$0.scale(this.dot($$0)).scale(1.0 / $$0.lengthSqr());
    }
}

