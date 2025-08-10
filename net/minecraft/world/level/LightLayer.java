/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

public final class LightLayer
extends Enum<LightLayer> {
    public static final /* enum */ LightLayer SKY = new LightLayer();
    public static final /* enum */ LightLayer BLOCK = new LightLayer();
    private static final /* synthetic */ LightLayer[] $VALUES;

    public static LightLayer[] values() {
        return (LightLayer[])$VALUES.clone();
    }

    public static LightLayer valueOf(String $$0) {
        return Enum.valueOf(LightLayer.class, $$0);
    }

    private static /* synthetic */ LightLayer[] a() {
        return new LightLayer[]{SKY, BLOCK};
    }

    static {
        $VALUES = LightLayer.a();
    }
}

