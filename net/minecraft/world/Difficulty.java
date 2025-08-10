/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public final class Difficulty
extends Enum<Difficulty>
implements StringRepresentable {
    public static final /* enum */ Difficulty PEACEFUL = new Difficulty(0, "peaceful");
    public static final /* enum */ Difficulty EASY = new Difficulty(1, "easy");
    public static final /* enum */ Difficulty NORMAL = new Difficulty(2, "normal");
    public static final /* enum */ Difficulty HARD = new Difficulty(3, "hard");
    public static final StringRepresentable.EnumCodec<Difficulty> CODEC;
    private static final IntFunction<Difficulty> BY_ID;
    public static final StreamCodec<ByteBuf, Difficulty> STREAM_CODEC;
    private final int id;
    private final String key;
    private static final /* synthetic */ Difficulty[] $VALUES;

    public static Difficulty[] values() {
        return (Difficulty[])$VALUES.clone();
    }

    public static Difficulty valueOf(String $$0) {
        return Enum.valueOf(Difficulty.class, $$0);
    }

    private Difficulty(int $$0, String $$1) {
        this.id = $$0;
        this.key = $$1;
    }

    public int getId() {
        return this.id;
    }

    public Component getDisplayName() {
        return Component.translatable("options.difficulty." + this.key);
    }

    public Component getInfo() {
        return Component.translatable("options.difficulty." + this.key + ".info");
    }

    @Deprecated
    public static Difficulty byId(int $$0) {
        return BY_ID.apply($$0);
    }

    @Nullable
    public static Difficulty byName(String $$0) {
        return CODEC.byName($$0);
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public String getSerializedName() {
        return this.key;
    }

    private static /* synthetic */ Difficulty[] f() {
        return new Difficulty[]{PEACEFUL, EASY, NORMAL, HARD};
    }

    static {
        $VALUES = Difficulty.f();
        CODEC = StringRepresentable.fromEnum(Difficulty::values);
        BY_ID = ByIdMap.a(Difficulty::getId, Difficulty.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Difficulty::getId);
    }
}

