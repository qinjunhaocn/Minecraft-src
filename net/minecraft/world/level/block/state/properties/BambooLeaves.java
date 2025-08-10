/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class BambooLeaves
extends Enum<BambooLeaves>
implements StringRepresentable {
    public static final /* enum */ BambooLeaves NONE = new BambooLeaves("none");
    public static final /* enum */ BambooLeaves SMALL = new BambooLeaves("small");
    public static final /* enum */ BambooLeaves LARGE = new BambooLeaves("large");
    private final String name;
    private static final /* synthetic */ BambooLeaves[] $VALUES;

    public static BambooLeaves[] values() {
        return (BambooLeaves[])$VALUES.clone();
    }

    public static BambooLeaves valueOf(String $$0) {
        return Enum.valueOf(BambooLeaves.class, $$0);
    }

    private BambooLeaves(String $$0) {
        this.name = $$0;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ BambooLeaves[] a() {
        return new BambooLeaves[]{NONE, SMALL, LARGE};
    }

    static {
        $VALUES = BambooLeaves.a();
    }
}

