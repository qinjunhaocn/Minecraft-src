/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class BellAttachType
extends Enum<BellAttachType>
implements StringRepresentable {
    public static final /* enum */ BellAttachType FLOOR = new BellAttachType("floor");
    public static final /* enum */ BellAttachType CEILING = new BellAttachType("ceiling");
    public static final /* enum */ BellAttachType SINGLE_WALL = new BellAttachType("single_wall");
    public static final /* enum */ BellAttachType DOUBLE_WALL = new BellAttachType("double_wall");
    private final String name;
    private static final /* synthetic */ BellAttachType[] $VALUES;

    public static BellAttachType[] values() {
        return (BellAttachType[])$VALUES.clone();
    }

    public static BellAttachType valueOf(String $$0) {
        return Enum.valueOf(BellAttachType.class, $$0);
    }

    private BellAttachType(String $$0) {
        this.name = $$0;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ BellAttachType[] a() {
        return new BellAttachType[]{FLOOR, CEILING, SINGLE_WALL, DOUBLE_WALL};
    }

    static {
        $VALUES = BellAttachType.a();
    }
}

