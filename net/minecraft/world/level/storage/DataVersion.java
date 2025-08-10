/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.storage;

public record DataVersion(int version, String series) {
    public static final String MAIN_SERIES = "main";

    public boolean isSideSeries() {
        return !this.series.equals(MAIN_SERIES);
    }

    public boolean isCompatible(DataVersion $$0) {
        return this.series().equals($$0.series());
    }
}

