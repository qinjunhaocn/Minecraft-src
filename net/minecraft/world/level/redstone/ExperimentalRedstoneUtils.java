/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.redstone;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.Orientation;

public class ExperimentalRedstoneUtils {
    @Nullable
    public static Orientation initialOrientation(Level $$0, @Nullable Direction $$1, @Nullable Direction $$2) {
        if ($$0.enabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS)) {
            Orientation $$3 = Orientation.random($$0.random).withSideBias(Orientation.SideBias.LEFT);
            if ($$2 != null) {
                $$3 = $$3.withUp($$2);
            }
            if ($$1 != null) {
                $$3 = $$3.withFront($$1);
            }
            return $$3;
        }
        return null;
    }

    @Nullable
    public static Orientation withFront(@Nullable Orientation $$0, Direction $$1) {
        return $$0 == null ? null : $$0.withFront($$1);
    }
}

