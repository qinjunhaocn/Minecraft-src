/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.level.redstone;

import com.google.common.annotations.VisibleForTesting;
import io.netty.buffer.ByteBuf;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

public class Orientation {
    public static final StreamCodec<ByteBuf, Orientation> STREAM_CODEC = ByteBufCodecs.idMapper(Orientation::fromIndex, Orientation::getIndex);
    private static final Orientation[] ORIENTATIONS = Util.make(() -> {
        Orientation[] $$0 = new Orientation[48];
        Orientation.a(new Orientation(Direction.UP, Direction.NORTH, SideBias.LEFT), $$0);
        return $$0;
    });
    private final Direction up;
    private final Direction front;
    private final Direction side;
    private final SideBias sideBias;
    private final int index;
    private final List<Direction> neighbors;
    private final List<Direction> horizontalNeighbors;
    private final List<Direction> verticalNeighbors;
    private final Map<Direction, Orientation> withFront = new EnumMap<Direction, Orientation>(Direction.class);
    private final Map<Direction, Orientation> withUp = new EnumMap<Direction, Orientation>(Direction.class);
    private final Map<SideBias, Orientation> withSideBias = new EnumMap<SideBias, Orientation>(SideBias.class);

    private Orientation(Direction $$02, Direction $$1, SideBias $$2) {
        this.up = $$02;
        this.front = $$1;
        this.sideBias = $$2;
        this.index = Orientation.generateIndex($$02, $$1, $$2);
        Vec3i $$3 = $$1.getUnitVec3i().cross($$02.getUnitVec3i());
        Direction $$4 = Direction.getNearest($$3, null);
        Objects.requireNonNull($$4);
        this.side = this.sideBias == SideBias.RIGHT ? $$4 : $$4.getOpposite();
        this.neighbors = List.of((Object)this.front.getOpposite(), (Object)this.front, (Object)this.side, (Object)this.side.getOpposite(), (Object)this.up.getOpposite(), (Object)this.up);
        this.horizontalNeighbors = this.neighbors.stream().filter($$0 -> $$0.getAxis() != this.up.getAxis()).toList();
        this.verticalNeighbors = this.neighbors.stream().filter($$0 -> $$0.getAxis() == this.up.getAxis()).toList();
    }

    public static Orientation of(Direction $$0, Direction $$1, SideBias $$2) {
        return ORIENTATIONS[Orientation.generateIndex($$0, $$1, $$2)];
    }

    public Orientation withUp(Direction $$0) {
        return this.withUp.get($$0);
    }

    public Orientation withFront(Direction $$0) {
        return this.withFront.get($$0);
    }

    public Orientation withFrontPreserveUp(Direction $$0) {
        if ($$0.getAxis() == this.up.getAxis()) {
            return this;
        }
        return this.withFront.get($$0);
    }

    public Orientation withFrontAdjustSideBias(Direction $$0) {
        Orientation $$1 = this.withFront($$0);
        if (this.front == $$1.side) {
            return $$1.withMirror();
        }
        return $$1;
    }

    public Orientation withSideBias(SideBias $$0) {
        return this.withSideBias.get((Object)$$0);
    }

    public Orientation withMirror() {
        return this.withSideBias(this.sideBias.getOpposite());
    }

    public Direction getFront() {
        return this.front;
    }

    public Direction getUp() {
        return this.up;
    }

    public Direction getSide() {
        return this.side;
    }

    public SideBias getSideBias() {
        return this.sideBias;
    }

    public List<Direction> getDirections() {
        return this.neighbors;
    }

    public List<Direction> getHorizontalDirections() {
        return this.horizontalNeighbors;
    }

    public List<Direction> getVerticalDirections() {
        return this.verticalNeighbors;
    }

    public String toString() {
        return "[up=" + String.valueOf(this.up) + ",front=" + String.valueOf(this.front) + ",sideBias=" + String.valueOf((Object)this.sideBias) + "]";
    }

    public int getIndex() {
        return this.index;
    }

    public static Orientation fromIndex(int $$0) {
        return ORIENTATIONS[$$0];
    }

    public static Orientation random(RandomSource $$0) {
        return Util.a(ORIENTATIONS, $$0);
    }

    private static Orientation a(Orientation $$0, Orientation[] $$1) {
        if ($$1[$$0.getIndex()] != null) {
            return $$1[$$0.getIndex()];
        }
        $$1[$$0.getIndex()] = $$0;
        for (SideBias sideBias : SideBias.values()) {
            $$0.withSideBias.put(sideBias, Orientation.a(new Orientation($$0.up, $$0.front, sideBias), $$1));
        }
        for (Enum enum_ : Direction.values()) {
            Direction $$4 = $$0.up;
            if (enum_ == $$0.up) {
                $$4 = $$0.front.getOpposite();
            }
            if (enum_ == $$0.up.getOpposite()) {
                $$4 = $$0.front;
            }
            $$0.withFront.put((Direction)enum_, Orientation.a(new Orientation($$4, (Direction)enum_, $$0.sideBias), $$1));
        }
        for (Enum enum_ : Direction.values()) {
            Direction $$6 = $$0.front;
            if (enum_ == $$0.front) {
                $$6 = $$0.up.getOpposite();
            }
            if (enum_ == $$0.front.getOpposite()) {
                $$6 = $$0.up;
            }
            $$0.withUp.put((Direction)enum_, Orientation.a(new Orientation((Direction)enum_, $$6, $$0.sideBias), $$1));
        }
        return $$0;
    }

    @VisibleForTesting
    protected static int generateIndex(Direction $$0, Direction $$1, SideBias $$2) {
        int $$4;
        if ($$0.getAxis() == $$1.getAxis()) {
            throw new IllegalStateException("Up-vector and front-vector can not be on the same axis");
        }
        if ($$0.getAxis() == Direction.Axis.Y) {
            boolean $$3 = $$1.getAxis() == Direction.Axis.X;
        } else {
            $$4 = $$1.getAxis() == Direction.Axis.Y ? 1 : 0;
        }
        int $$5 = $$4 << 1 | $$1.getAxisDirection().ordinal();
        return (($$0.ordinal() << 2) + $$5 << 1) + $$2.ordinal();
    }

    public static final class SideBias
    extends Enum<SideBias> {
        public static final /* enum */ SideBias LEFT = new SideBias("left");
        public static final /* enum */ SideBias RIGHT = new SideBias("right");
        private final String name;
        private static final /* synthetic */ SideBias[] $VALUES;

        public static SideBias[] values() {
            return (SideBias[])$VALUES.clone();
        }

        public static SideBias valueOf(String $$0) {
            return Enum.valueOf(SideBias.class, $$0);
        }

        private SideBias(String $$0) {
            this.name = $$0;
        }

        public SideBias getOpposite() {
            return this == LEFT ? RIGHT : LEFT;
        }

        public String toString() {
            return this.name;
        }

        private static /* synthetic */ SideBias[] b() {
            return new SideBias[]{LEFT, RIGHT};
        }

        static {
            $VALUES = SideBias.b();
        }
    }
}

