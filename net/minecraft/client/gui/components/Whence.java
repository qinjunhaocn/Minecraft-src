/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

public final class Whence
extends Enum<Whence> {
    public static final /* enum */ Whence ABSOLUTE = new Whence();
    public static final /* enum */ Whence RELATIVE = new Whence();
    public static final /* enum */ Whence END = new Whence();
    private static final /* synthetic */ Whence[] $VALUES;

    public static Whence[] values() {
        return (Whence[])$VALUES.clone();
    }

    public static Whence valueOf(String $$0) {
        return Enum.valueOf(Whence.class, $$0);
    }

    private static /* synthetic */ Whence[] a() {
        return new Whence[]{ABSOLUTE, RELATIVE, END};
    }

    static {
        $VALUES = Whence.a();
    }
}

