/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.util;

import com.mojang.serialization.MapCodec;

public record KeyDispatchDataCodec<A>(MapCodec<A> codec) {
    public static <A> KeyDispatchDataCodec<A> of(MapCodec<A> $$0) {
        return new KeyDispatchDataCodec<A>($$0);
    }
}

