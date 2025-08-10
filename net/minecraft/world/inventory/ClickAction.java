/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.inventory;

public final class ClickAction
extends Enum<ClickAction> {
    public static final /* enum */ ClickAction PRIMARY = new ClickAction();
    public static final /* enum */ ClickAction SECONDARY = new ClickAction();
    private static final /* synthetic */ ClickAction[] $VALUES;

    public static ClickAction[] values() {
        return (ClickAction[])$VALUES.clone();
    }

    public static ClickAction valueOf(String $$0) {
        return Enum.valueOf(ClickAction.class, $$0);
    }

    private static /* synthetic */ ClickAction[] a() {
        return new ClickAction[]{PRIMARY, SECONDARY};
    }

    static {
        $VALUES = ClickAction.a();
    }
}

