/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public final class CaveSurface
extends Enum<CaveSurface>
implements StringRepresentable {
    public static final /* enum */ CaveSurface CEILING = new CaveSurface(Direction.UP, 1, "ceiling");
    public static final /* enum */ CaveSurface FLOOR = new CaveSurface(Direction.DOWN, -1, "floor");
    public static final Codec<CaveSurface> CODEC;
    private final Direction direction;
    private final int y;
    private final String id;
    private static final /* synthetic */ CaveSurface[] $VALUES;

    public static CaveSurface[] values() {
        return (CaveSurface[])$VALUES.clone();
    }

    public static CaveSurface valueOf(String $$0) {
        return Enum.valueOf(CaveSurface.class, $$0);
    }

    private CaveSurface(Direction $$0, int $$1, String $$2) {
        this.direction = $$0;
        this.y = $$1;
        this.id = $$2;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    private static /* synthetic */ CaveSurface[] d() {
        return new CaveSurface[]{CEILING, FLOOR};
    }

    static {
        $VALUES = CaveSurface.d();
        CODEC = StringRepresentable.fromEnum(CaveSurface::values);
    }
}

