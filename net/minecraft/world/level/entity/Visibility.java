/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.entity;

import net.minecraft.server.level.FullChunkStatus;

public final class Visibility
extends Enum<Visibility> {
    public static final /* enum */ Visibility HIDDEN = new Visibility(false, false);
    public static final /* enum */ Visibility TRACKED = new Visibility(true, false);
    public static final /* enum */ Visibility TICKING = new Visibility(true, true);
    private final boolean accessible;
    private final boolean ticking;
    private static final /* synthetic */ Visibility[] $VALUES;

    public static Visibility[] values() {
        return (Visibility[])$VALUES.clone();
    }

    public static Visibility valueOf(String $$0) {
        return Enum.valueOf(Visibility.class, $$0);
    }

    private Visibility(boolean $$0, boolean $$1) {
        this.accessible = $$0;
        this.ticking = $$1;
    }

    public boolean isTicking() {
        return this.ticking;
    }

    public boolean isAccessible() {
        return this.accessible;
    }

    public static Visibility fromFullChunkStatus(FullChunkStatus $$0) {
        if ($$0.isOrAfter(FullChunkStatus.ENTITY_TICKING)) {
            return TICKING;
        }
        if ($$0.isOrAfter(FullChunkStatus.FULL)) {
            return TRACKED;
        }
        return HIDDEN;
    }

    private static /* synthetic */ Visibility[] c() {
        return new Visibility[]{HIDDEN, TRACKED, TICKING};
    }

    static {
        $VALUES = Visibility.c();
    }
}

