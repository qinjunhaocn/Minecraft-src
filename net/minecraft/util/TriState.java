/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util;

public final class TriState
extends Enum<TriState> {
    public static final /* enum */ TriState TRUE = new TriState();
    public static final /* enum */ TriState FALSE = new TriState();
    public static final /* enum */ TriState DEFAULT = new TriState();
    private static final /* synthetic */ TriState[] $VALUES;

    public static TriState[] values() {
        return (TriState[])$VALUES.clone();
    }

    public static TriState valueOf(String $$0) {
        return Enum.valueOf(TriState.class, $$0);
    }

    public boolean toBoolean(boolean $$0) {
        return switch (this.ordinal()) {
            case 0 -> true;
            case 1 -> false;
            default -> $$0;
        };
    }

    private static /* synthetic */ TriState[] a() {
        return new TriState[]{TRUE, FALSE, DEFAULT};
    }

    static {
        $VALUES = TriState.a();
    }
}

