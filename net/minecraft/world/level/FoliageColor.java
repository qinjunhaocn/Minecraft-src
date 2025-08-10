/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import net.minecraft.world.level.ColorMapColorUtil;

public class FoliageColor {
    public static final int FOLIAGE_EVERGREEN = -10380959;
    public static final int FOLIAGE_BIRCH = -8345771;
    public static final int FOLIAGE_DEFAULT = -12012264;
    public static final int FOLIAGE_MANGROVE = -7158200;
    private static int[] pixels = new int[65536];

    public static void a(int[] $$0) {
        pixels = $$0;
    }

    public static int get(double $$0, double $$1) {
        return ColorMapColorUtil.a($$0, $$1, pixels, -12012264);
    }
}

