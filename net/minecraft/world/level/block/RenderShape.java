/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block;

public final class RenderShape
extends Enum<RenderShape> {
    public static final /* enum */ RenderShape INVISIBLE = new RenderShape();
    public static final /* enum */ RenderShape MODEL = new RenderShape();
    private static final /* synthetic */ RenderShape[] $VALUES;

    public static RenderShape[] values() {
        return (RenderShape[])$VALUES.clone();
    }

    public static RenderShape valueOf(String $$0) {
        return Enum.valueOf(RenderShape.class, $$0);
    }

    private static /* synthetic */ RenderShape[] a() {
        return new RenderShape[]{INVISIBLE, MODEL};
    }

    static {
        $VALUES = RenderShape.a();
    }
}

