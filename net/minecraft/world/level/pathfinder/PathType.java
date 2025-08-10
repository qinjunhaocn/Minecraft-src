/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.pathfinder;

public final class PathType
extends Enum<PathType> {
    public static final /* enum */ PathType BLOCKED = new PathType(-1.0f);
    public static final /* enum */ PathType OPEN = new PathType(0.0f);
    public static final /* enum */ PathType WALKABLE = new PathType(0.0f);
    public static final /* enum */ PathType WALKABLE_DOOR = new PathType(0.0f);
    public static final /* enum */ PathType TRAPDOOR = new PathType(0.0f);
    public static final /* enum */ PathType POWDER_SNOW = new PathType(-1.0f);
    public static final /* enum */ PathType DANGER_POWDER_SNOW = new PathType(0.0f);
    public static final /* enum */ PathType FENCE = new PathType(-1.0f);
    public static final /* enum */ PathType LAVA = new PathType(-1.0f);
    public static final /* enum */ PathType WATER = new PathType(8.0f);
    public static final /* enum */ PathType WATER_BORDER = new PathType(8.0f);
    public static final /* enum */ PathType RAIL = new PathType(0.0f);
    public static final /* enum */ PathType UNPASSABLE_RAIL = new PathType(-1.0f);
    public static final /* enum */ PathType DANGER_FIRE = new PathType(8.0f);
    public static final /* enum */ PathType DAMAGE_FIRE = new PathType(16.0f);
    public static final /* enum */ PathType DANGER_OTHER = new PathType(8.0f);
    public static final /* enum */ PathType DAMAGE_OTHER = new PathType(-1.0f);
    public static final /* enum */ PathType DOOR_OPEN = new PathType(0.0f);
    public static final /* enum */ PathType DOOR_WOOD_CLOSED = new PathType(-1.0f);
    public static final /* enum */ PathType DOOR_IRON_CLOSED = new PathType(-1.0f);
    public static final /* enum */ PathType BREACH = new PathType(4.0f);
    public static final /* enum */ PathType LEAVES = new PathType(-1.0f);
    public static final /* enum */ PathType STICKY_HONEY = new PathType(8.0f);
    public static final /* enum */ PathType COCOA = new PathType(0.0f);
    public static final /* enum */ PathType DAMAGE_CAUTIOUS = new PathType(0.0f);
    public static final /* enum */ PathType DANGER_TRAPDOOR = new PathType(0.0f);
    private final float malus;
    private static final /* synthetic */ PathType[] $VALUES;

    public static PathType[] values() {
        return (PathType[])$VALUES.clone();
    }

    public static PathType valueOf(String $$0) {
        return Enum.valueOf(PathType.class, $$0);
    }

    private PathType(float $$0) {
        this.malus = $$0;
    }

    public float getMalus() {
        return this.malus;
    }

    private static /* synthetic */ PathType[] b() {
        return new PathType[]{BLOCKED, OPEN, WALKABLE, WALKABLE_DOOR, TRAPDOOR, POWDER_SNOW, DANGER_POWDER_SNOW, FENCE, LAVA, WATER, WATER_BORDER, RAIL, UNPASSABLE_RAIL, DANGER_FIRE, DAMAGE_FIRE, DANGER_OTHER, DAMAGE_OTHER, DOOR_OPEN, DOOR_WOOD_CLOSED, DOOR_IRON_CLOSED, BREACH, LEAVES, STICKY_HONEY, COCOA, DAMAGE_CAUTIOUS, DANGER_TRAPDOOR};
    }

    static {
        $VALUES = PathType.b();
    }
}

