/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.material;

public final class FogType
extends Enum<FogType> {
    public static final /* enum */ FogType LAVA = new FogType();
    public static final /* enum */ FogType WATER = new FogType();
    public static final /* enum */ FogType POWDER_SNOW = new FogType();
    public static final /* enum */ FogType DIMENSION_OR_BOSS = new FogType();
    public static final /* enum */ FogType ATMOSPHERIC = new FogType();
    public static final /* enum */ FogType NONE = new FogType();
    private static final /* synthetic */ FogType[] $VALUES;

    public static FogType[] values() {
        return (FogType[])$VALUES.clone();
    }

    public static FogType valueOf(String $$0) {
        return Enum.valueOf(FogType.class, $$0);
    }

    private static /* synthetic */ FogType[] a() {
        return new FogType[]{LAVA, WATER, POWDER_SNOW, DIMENSION_OR_BOSS, ATMOSPHERIC, NONE};
    }

    static {
        $VALUES = FogType.a();
    }
}

