/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class ComparatorMode
extends Enum<ComparatorMode>
implements StringRepresentable {
    public static final /* enum */ ComparatorMode COMPARE = new ComparatorMode("compare");
    public static final /* enum */ ComparatorMode SUBTRACT = new ComparatorMode("subtract");
    private final String name;
    private static final /* synthetic */ ComparatorMode[] $VALUES;

    public static ComparatorMode[] values() {
        return (ComparatorMode[])$VALUES.clone();
    }

    public static ComparatorMode valueOf(String $$0) {
        return Enum.valueOf(ComparatorMode.class, $$0);
    }

    private ComparatorMode(String $$0) {
        this.name = $$0;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ ComparatorMode[] a() {
        return new ComparatorMode[]{COMPARE, SUBTRACT};
    }

    static {
        $VALUES = ComparatorMode.a();
    }
}

