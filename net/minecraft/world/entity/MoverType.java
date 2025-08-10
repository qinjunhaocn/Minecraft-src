/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity;

public final class MoverType
extends Enum<MoverType> {
    public static final /* enum */ MoverType SELF = new MoverType();
    public static final /* enum */ MoverType PLAYER = new MoverType();
    public static final /* enum */ MoverType PISTON = new MoverType();
    public static final /* enum */ MoverType SHULKER_BOX = new MoverType();
    public static final /* enum */ MoverType SHULKER = new MoverType();
    private static final /* synthetic */ MoverType[] $VALUES;

    public static MoverType[] values() {
        return (MoverType[])$VALUES.clone();
    }

    public static MoverType valueOf(String $$0) {
        return Enum.valueOf(MoverType.class, $$0);
    }

    private static /* synthetic */ MoverType[] a() {
        return new MoverType[]{SELF, PLAYER, PISTON, SHULKER_BOX, SHULKER};
    }

    static {
        $VALUES = MoverType.a();
    }
}

