/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class ChestType
extends Enum<ChestType>
implements StringRepresentable {
    public static final /* enum */ ChestType SINGLE = new ChestType("single");
    public static final /* enum */ ChestType LEFT = new ChestType("left");
    public static final /* enum */ ChestType RIGHT = new ChestType("right");
    private final String name;
    private static final /* synthetic */ ChestType[] $VALUES;

    public static ChestType[] values() {
        return (ChestType[])$VALUES.clone();
    }

    public static ChestType valueOf(String $$0) {
        return Enum.valueOf(ChestType.class, $$0);
    }

    private ChestType(String $$0) {
        this.name = $$0;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public ChestType getOpposite() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> SINGLE;
            case 1 -> RIGHT;
            case 2 -> LEFT;
        };
    }

    private static /* synthetic */ ChestType[] b() {
        return new ChestType[]{SINGLE, LEFT, RIGHT};
    }

    static {
        $VALUES = ChestType.b();
    }
}

