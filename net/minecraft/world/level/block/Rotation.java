/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  java.lang.MatchException
 */
package net.minecraft.world.level.block;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

public final class Rotation
extends Enum<Rotation>
implements StringRepresentable {
    public static final /* enum */ Rotation NONE = new Rotation(0, "none", OctahedralGroup.IDENTITY);
    public static final /* enum */ Rotation CLOCKWISE_90 = new Rotation(1, "clockwise_90", OctahedralGroup.ROT_90_Y_NEG);
    public static final /* enum */ Rotation CLOCKWISE_180 = new Rotation(2, "180", OctahedralGroup.ROT_180_FACE_XZ);
    public static final /* enum */ Rotation COUNTERCLOCKWISE_90 = new Rotation(3, "counterclockwise_90", OctahedralGroup.ROT_90_Y_POS);
    public static final IntFunction<Rotation> BY_ID;
    public static final Codec<Rotation> CODEC;
    public static final StreamCodec<ByteBuf, Rotation> STREAM_CODEC;
    @Deprecated
    public static final Codec<Rotation> LEGACY_CODEC;
    private final int index;
    private final String id;
    private final OctahedralGroup rotation;
    private static final /* synthetic */ Rotation[] $VALUES;

    public static Rotation[] values() {
        return (Rotation[])$VALUES.clone();
    }

    public static Rotation valueOf(String $$0) {
        return Enum.valueOf(Rotation.class, $$0);
    }

    private Rotation(int $$0, String $$1, OctahedralGroup $$2) {
        this.index = $$0;
        this.id = $$1;
        this.rotation = $$2;
    }

    public Rotation getRotated(Rotation $$0) {
        return switch ($$0.ordinal()) {
            case 2 -> {
                switch (this.ordinal()) {
                    default: {
                        throw new MatchException(null, null);
                    }
                    case 0: {
                        yield CLOCKWISE_180;
                    }
                    case 1: {
                        yield COUNTERCLOCKWISE_90;
                    }
                    case 2: {
                        yield NONE;
                    }
                    case 3: 
                }
                yield CLOCKWISE_90;
            }
            case 3 -> {
                switch (this.ordinal()) {
                    default: {
                        throw new MatchException(null, null);
                    }
                    case 0: {
                        yield COUNTERCLOCKWISE_90;
                    }
                    case 1: {
                        yield NONE;
                    }
                    case 2: {
                        yield CLOCKWISE_90;
                    }
                    case 3: 
                }
                yield CLOCKWISE_180;
            }
            case 1 -> {
                switch (this.ordinal()) {
                    default: {
                        throw new MatchException(null, null);
                    }
                    case 0: {
                        yield CLOCKWISE_90;
                    }
                    case 1: {
                        yield CLOCKWISE_180;
                    }
                    case 2: {
                        yield COUNTERCLOCKWISE_90;
                    }
                    case 3: 
                }
                yield NONE;
            }
            default -> this;
        };
    }

    public OctahedralGroup rotation() {
        return this.rotation;
    }

    public Direction rotate(Direction $$0) {
        if ($$0.getAxis() == Direction.Axis.Y) {
            return $$0;
        }
        return switch (this.ordinal()) {
            case 2 -> $$0.getOpposite();
            case 3 -> $$0.getCounterClockWise();
            case 1 -> $$0.getClockWise();
            default -> $$0;
        };
    }

    public int rotate(int $$0, int $$1) {
        return switch (this.ordinal()) {
            case 2 -> ($$0 + $$1 / 2) % $$1;
            case 3 -> ($$0 + $$1 * 3 / 4) % $$1;
            case 1 -> ($$0 + $$1 / 4) % $$1;
            default -> $$0;
        };
    }

    public static Rotation getRandom(RandomSource $$0) {
        return Util.a(Rotation.values(), $$0);
    }

    public static List<Rotation> getShuffled(RandomSource $$0) {
        return Util.b(Rotation.values(), $$0);
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    private int getIndex() {
        return this.index;
    }

    private static /* synthetic */ Rotation[] d() {
        return new Rotation[]{NONE, CLOCKWISE_90, CLOCKWISE_180, COUNTERCLOCKWISE_90};
    }

    static {
        $VALUES = Rotation.d();
        BY_ID = ByIdMap.a(Rotation::getIndex, Rotation.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        CODEC = StringRepresentable.fromEnum(Rotation::values);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Rotation::getIndex);
        LEGACY_CODEC = ExtraCodecs.legacyEnum(Rotation::valueOf);
    }
}

