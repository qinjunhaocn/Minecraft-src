/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.entity.animal.horse;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public final class Variant
extends Enum<Variant>
implements StringRepresentable {
    public static final /* enum */ Variant WHITE = new Variant(0, "white");
    public static final /* enum */ Variant CREAMY = new Variant(1, "creamy");
    public static final /* enum */ Variant CHESTNUT = new Variant(2, "chestnut");
    public static final /* enum */ Variant BROWN = new Variant(3, "brown");
    public static final /* enum */ Variant BLACK = new Variant(4, "black");
    public static final /* enum */ Variant GRAY = new Variant(5, "gray");
    public static final /* enum */ Variant DARK_BROWN = new Variant(6, "dark_brown");
    public static final Codec<Variant> CODEC;
    private static final IntFunction<Variant> BY_ID;
    public static final StreamCodec<ByteBuf, Variant> STREAM_CODEC;
    private final int id;
    private final String name;
    private static final /* synthetic */ Variant[] $VALUES;

    public static Variant[] values() {
        return (Variant[])$VALUES.clone();
    }

    public static Variant valueOf(String $$0) {
        return Enum.valueOf(Variant.class, $$0);
    }

    private Variant(int $$0, String $$1) {
        this.id = $$0;
        this.name = $$1;
    }

    public int getId() {
        return this.id;
    }

    public static Variant byId(int $$0) {
        return BY_ID.apply($$0);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ Variant[] b() {
        return new Variant[]{WHITE, CREAMY, CHESTNUT, BROWN, BLACK, GRAY, DARK_BROWN};
    }

    static {
        $VALUES = Variant.b();
        CODEC = StringRepresentable.fromEnum(Variant::values);
        BY_ID = ByIdMap.a(Variant::getId, Variant.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Variant::getId);
    }
}

