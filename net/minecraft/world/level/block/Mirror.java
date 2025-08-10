/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.block;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;

public final class Mirror
extends Enum<Mirror>
implements StringRepresentable {
    public static final /* enum */ Mirror NONE = new Mirror("none", OctahedralGroup.IDENTITY);
    public static final /* enum */ Mirror LEFT_RIGHT = new Mirror("left_right", OctahedralGroup.INVERT_Z);
    public static final /* enum */ Mirror FRONT_BACK = new Mirror("front_back", OctahedralGroup.INVERT_X);
    public static final Codec<Mirror> CODEC;
    @Deprecated
    public static final Codec<Mirror> LEGACY_CODEC;
    private final String id;
    private final Component symbol;
    private final OctahedralGroup rotation;
    private static final /* synthetic */ Mirror[] $VALUES;

    public static Mirror[] values() {
        return (Mirror[])$VALUES.clone();
    }

    public static Mirror valueOf(String $$0) {
        return Enum.valueOf(Mirror.class, $$0);
    }

    private Mirror(String $$0, OctahedralGroup $$1) {
        this.id = $$0;
        this.symbol = Component.translatable("mirror." + $$0);
        this.rotation = $$1;
    }

    public int mirror(int $$0, int $$1) {
        int $$2 = $$1 / 2;
        int $$3 = $$0 > $$2 ? $$0 - $$1 : $$0;
        switch (this.ordinal()) {
            case 2: {
                return ($$1 - $$3) % $$1;
            }
            case 1: {
                return ($$2 - $$3 + $$1) % $$1;
            }
        }
        return $$0;
    }

    public Rotation getRotation(Direction $$0) {
        Direction.Axis $$1 = $$0.getAxis();
        return this == LEFT_RIGHT && $$1 == Direction.Axis.Z || this == FRONT_BACK && $$1 == Direction.Axis.X ? Rotation.CLOCKWISE_180 : Rotation.NONE;
    }

    public Direction mirror(Direction $$0) {
        if (this == FRONT_BACK && $$0.getAxis() == Direction.Axis.X) {
            return $$0.getOpposite();
        }
        if (this == LEFT_RIGHT && $$0.getAxis() == Direction.Axis.Z) {
            return $$0.getOpposite();
        }
        return $$0;
    }

    public OctahedralGroup rotation() {
        return this.rotation;
    }

    public Component symbol() {
        return this.symbol;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    private static /* synthetic */ Mirror[] d() {
        return new Mirror[]{NONE, LEFT_RIGHT, FRONT_BACK};
    }

    static {
        $VALUES = Mirror.d();
        CODEC = StringRepresentable.fromEnum(Mirror::values);
        LEGACY_CODEC = ExtraCodecs.legacyEnum(Mirror::valueOf);
    }
}

