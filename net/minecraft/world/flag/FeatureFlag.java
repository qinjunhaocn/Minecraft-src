/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.flag;

import net.minecraft.world.flag.FeatureFlagUniverse;

public class FeatureFlag {
    final FeatureFlagUniverse universe;
    final long mask;

    FeatureFlag(FeatureFlagUniverse $$0, int $$1) {
        this.universe = $$0;
        this.mask = 1L << $$1;
    }
}

