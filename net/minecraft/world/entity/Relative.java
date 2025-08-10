/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.entity;

import io.netty.buffer.ByteBuf;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class Relative
extends Enum<Relative> {
    public static final /* enum */ Relative X = new Relative(0);
    public static final /* enum */ Relative Y = new Relative(1);
    public static final /* enum */ Relative Z = new Relative(2);
    public static final /* enum */ Relative Y_ROT = new Relative(3);
    public static final /* enum */ Relative X_ROT = new Relative(4);
    public static final /* enum */ Relative DELTA_X = new Relative(5);
    public static final /* enum */ Relative DELTA_Y = new Relative(6);
    public static final /* enum */ Relative DELTA_Z = new Relative(7);
    public static final /* enum */ Relative ROTATE_DELTA = new Relative(8);
    public static final Set<Relative> ALL;
    public static final Set<Relative> ROTATION;
    public static final Set<Relative> DELTA;
    public static final StreamCodec<ByteBuf, Set<Relative>> SET_STREAM_CODEC;
    private final int bit;
    private static final /* synthetic */ Relative[] $VALUES;

    public static Relative[] values() {
        return (Relative[])$VALUES.clone();
    }

    public static Relative valueOf(String $$0) {
        return Enum.valueOf(Relative.class, $$0);
    }

    @SafeVarargs
    public static Set<Relative> a(Set<Relative> ... $$0) {
        HashSet<Relative> $$1 = new HashSet<Relative>();
        for (Set<Relative> $$2 : $$0) {
            $$1.addAll($$2);
        }
        return $$1;
    }

    private Relative(int $$0) {
        this.bit = $$0;
    }

    private int getMask() {
        return 1 << this.bit;
    }

    private boolean isSet(int $$0) {
        return ($$0 & this.getMask()) == this.getMask();
    }

    public static Set<Relative> unpack(int $$0) {
        EnumSet<Relative> $$1 = EnumSet.noneOf(Relative.class);
        for (Relative $$2 : Relative.values()) {
            if (!$$2.isSet($$0)) continue;
            $$1.add($$2);
        }
        return $$1;
    }

    public static int pack(Set<Relative> $$0) {
        int $$1 = 0;
        for (Relative $$2 : $$0) {
            $$1 |= $$2.getMask();
        }
        return $$1;
    }

    private static /* synthetic */ Relative[] b() {
        return new Relative[]{X, Y, Z, Y_ROT, X_ROT, DELTA_X, DELTA_Y, DELTA_Z, ROTATE_DELTA};
    }

    static {
        $VALUES = Relative.b();
        ALL = Set.of((Object[])Relative.values());
        ROTATION = Set.of((Object)((Object)X_ROT), (Object)((Object)Y_ROT));
        DELTA = Set.of((Object)((Object)DELTA_X), (Object)((Object)DELTA_Y), (Object)((Object)DELTA_Z), (Object)((Object)ROTATE_DELTA));
        SET_STREAM_CODEC = ByteBufCodecs.INT.map(Relative::unpack, Relative::pack);
    }
}

