/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.animal.horse;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;

public final class Markings
extends Enum<Markings> {
    public static final /* enum */ Markings NONE = new Markings(0);
    public static final /* enum */ Markings WHITE = new Markings(1);
    public static final /* enum */ Markings WHITE_FIELD = new Markings(2);
    public static final /* enum */ Markings WHITE_DOTS = new Markings(3);
    public static final /* enum */ Markings BLACK_DOTS = new Markings(4);
    private static final IntFunction<Markings> BY_ID;
    private final int id;
    private static final /* synthetic */ Markings[] $VALUES;

    public static Markings[] values() {
        return (Markings[])$VALUES.clone();
    }

    public static Markings valueOf(String $$0) {
        return Enum.valueOf(Markings.class, $$0);
    }

    private Markings(int $$0) {
        this.id = $$0;
    }

    public int getId() {
        return this.id;
    }

    public static Markings byId(int $$0) {
        return BY_ID.apply($$0);
    }

    private static /* synthetic */ Markings[] b() {
        return new Markings[]{NONE, WHITE, WHITE_FIELD, WHITE_DOTS, BLACK_DOTS};
    }

    static {
        $VALUES = Markings.b();
        BY_ID = ByIdMap.a(Markings::getId, Markings.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    }
}

