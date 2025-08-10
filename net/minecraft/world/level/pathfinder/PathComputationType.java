/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.pathfinder;

public final class PathComputationType
extends Enum<PathComputationType> {
    public static final /* enum */ PathComputationType LAND = new PathComputationType();
    public static final /* enum */ PathComputationType WATER = new PathComputationType();
    public static final /* enum */ PathComputationType AIR = new PathComputationType();
    private static final /* synthetic */ PathComputationType[] $VALUES;

    public static PathComputationType[] values() {
        return (PathComputationType[])$VALUES.clone();
    }

    public static PathComputationType valueOf(String $$0) {
        return Enum.valueOf(PathComputationType.class, $$0);
    }

    private static /* synthetic */ PathComputationType[] a() {
        return new PathComputationType[]{LAND, WATER, AIR};
    }

    static {
        $VALUES = PathComputationType.a();
    }
}

