/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonParseException
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  java.lang.MatchException
 */
package com.mojang.math;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.Mth;

public final class Quadrant
extends Enum<Quadrant> {
    public static final /* enum */ Quadrant R0 = new Quadrant(0);
    public static final /* enum */ Quadrant R90 = new Quadrant(1);
    public static final /* enum */ Quadrant R180 = new Quadrant(2);
    public static final /* enum */ Quadrant R270 = new Quadrant(3);
    public static final Codec<Quadrant> CODEC;
    public final int shift;
    private static final /* synthetic */ Quadrant[] $VALUES;

    public static Quadrant[] values() {
        return (Quadrant[])$VALUES.clone();
    }

    public static Quadrant valueOf(String $$0) {
        return Enum.valueOf(Quadrant.class, $$0);
    }

    private Quadrant(int $$0) {
        this.shift = $$0;
    }

    @Deprecated
    public static Quadrant parseJson(int $$0) {
        return switch (Mth.positiveModulo($$0, 360)) {
            case 0 -> R0;
            case 90 -> R90;
            case 180 -> R180;
            case 270 -> R270;
            default -> throw new JsonParseException("Invalid rotation " + $$0 + " found, only 0/90/180/270 allowed");
        };
    }

    public int rotateVertexIndex(int $$0) {
        return ($$0 + this.shift) % 4;
    }

    private static /* synthetic */ Quadrant[] a() {
        return new Quadrant[]{R0, R90, R180, R270};
    }

    static {
        $VALUES = Quadrant.a();
        CODEC = Codec.INT.comapFlatMap($$0 -> switch (Mth.positiveModulo($$0, 360)) {
            case 0 -> DataResult.success((Object)((Object)R0));
            case 90 -> DataResult.success((Object)((Object)R90));
            case 180 -> DataResult.success((Object)((Object)R180));
            case 270 -> DataResult.success((Object)((Object)R270));
            default -> DataResult.error(() -> "Invalid rotation " + $$0 + " found, only 0/90/180/270 allowed");
        }, $$0 -> switch ($$0.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> 0;
            case 1 -> 90;
            case 2 -> 180;
            case 3 -> 270;
        });
    }
}

