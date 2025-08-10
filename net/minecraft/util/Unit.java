/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class Unit
extends Enum<Unit> {
    public static final /* enum */ Unit INSTANCE = new Unit();
    public static final Codec<Unit> CODEC;
    public static final StreamCodec<ByteBuf, Unit> STREAM_CODEC;
    private static final /* synthetic */ Unit[] $VALUES;

    public static Unit[] values() {
        return (Unit[])$VALUES.clone();
    }

    public static Unit valueOf(String $$0) {
        return Enum.valueOf(Unit.class, $$0);
    }

    private static /* synthetic */ Unit[] a() {
        return new Unit[]{INSTANCE};
    }

    static {
        $VALUES = Unit.a();
        CODEC = Codec.unit((Object)((Object)INSTANCE));
        STREAM_CODEC = StreamCodec.unit(INSTANCE);
    }
}

