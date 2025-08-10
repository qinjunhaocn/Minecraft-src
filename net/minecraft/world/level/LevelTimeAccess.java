/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.dimension.DimensionType;

public interface LevelTimeAccess
extends LevelReader {
    public long dayTime();

    default public float getMoonBrightness() {
        return DimensionType.MOON_BRIGHTNESS_PER_PHASE[this.dimensionType().moonPhase(this.dayTime())];
    }

    default public float getTimeOfDay(float $$0) {
        return this.dimensionType().timeOfDay(this.dayTime());
    }

    default public int getMoonPhase() {
        return this.dimensionType().moonPhase(this.dayTime());
    }
}

