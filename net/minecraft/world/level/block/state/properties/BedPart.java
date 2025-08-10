/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class BedPart
extends Enum<BedPart>
implements StringRepresentable {
    public static final /* enum */ BedPart HEAD = new BedPart("head");
    public static final /* enum */ BedPart FOOT = new BedPart("foot");
    private final String name;
    private static final /* synthetic */ BedPart[] $VALUES;

    public static BedPart[] values() {
        return (BedPart[])$VALUES.clone();
    }

    public static BedPart valueOf(String $$0) {
        return Enum.valueOf(BedPart.class, $$0);
    }

    private BedPart(String $$0) {
        this.name = $$0;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ BedPart[] a() {
        return new BedPart[]{HEAD, FOOT};
    }

    static {
        $VALUES = BedPart.a();
    }
}

