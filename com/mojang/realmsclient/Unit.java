/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient;

import java.util.Locale;

public final class Unit
extends Enum<Unit> {
    public static final /* enum */ Unit B = new Unit();
    public static final /* enum */ Unit KB = new Unit();
    public static final /* enum */ Unit MB = new Unit();
    public static final /* enum */ Unit GB = new Unit();
    private static final int BASE_UNIT = 1024;
    private static final /* synthetic */ Unit[] $VALUES;

    public static Unit[] values() {
        return (Unit[])$VALUES.clone();
    }

    public static Unit valueOf(String $$0) {
        return Enum.valueOf(Unit.class, $$0);
    }

    public static Unit getLargest(long $$0) {
        if ($$0 < 1024L) {
            return B;
        }
        try {
            int $$1 = (int)(Math.log($$0) / Math.log(1024.0));
            String $$2 = String.valueOf("KMGTPE".charAt($$1 - 1));
            return Unit.valueOf($$2 + "B");
        } catch (Exception $$3) {
            return GB;
        }
    }

    public static double convertTo(long $$0, Unit $$1) {
        if ($$1 == B) {
            return $$0;
        }
        return (double)$$0 / Math.pow(1024.0, $$1.ordinal());
    }

    public static String humanReadable(long $$0) {
        int $$1 = 1024;
        if ($$0 < 1024L) {
            return $$0 + " B";
        }
        int $$2 = (int)(Math.log($$0) / Math.log(1024.0));
        String $$3 = "" + "KMGTPE".charAt($$2 - 1);
        return String.format(Locale.ROOT, "%.1f %sB", (double)$$0 / Math.pow(1024.0, $$2), $$3);
    }

    public static String humanReadable(long $$0, Unit $$1) {
        return String.format(Locale.ROOT, "%." + ($$1 == GB ? "1" : "0") + "f %s", Unit.convertTo($$0, $$1), $$1.name());
    }

    private static /* synthetic */ Unit[] a() {
        return new Unit[]{B, KB, MB, GB};
    }

    static {
        $VALUES = Unit.a();
    }
}

